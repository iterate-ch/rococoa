# Returning values from objc\_msgSend etc #

## Here is my understanding so far ##

From the [Objective-C 2.0 Runtime Reference](http://developer.apple.com/documentation/Cocoa/Reference/ObjCRuntimeRef/Reference/reference.html#//apple_ref/c/func/objc_msgSend):
```
id objc_msgSend(struct objc_super *super, SEL op,  ...)
void objc_msgSend_stret(void * stretAddr, id theReceiver, SEL theSelector,  ...)
```
When it encounters a method call, the compiler generates a call to one of the functions objc\_msgSend, objc\_msgSend\_stret, objc\_msgSendSuper, or objc\_msgSendSuper\_stret.
Messages sent to an object's superclass (using the super keyword) are sent using objc\_msgSendSuper; other messages are sent using objc\_msgSend.
Methods that have data structures as return values are sent using objc\_msgSendSuper\_stret and objc\_msgSend\_stret.

So for methods returning id or int I call objc\_msgSendSuper, casting the result to the known return type of the method.
So far so good.

Now to return structs I have to call objc\_msgSend\_stret.
This is complicated (not that the previous reference mentions this), because, from objc-runtime.h:
```
/* Struct-returning Messaging Primitives (prototypes)
*
* For historical reasons, the prototypes for the struct-returning
* messengers are unusual. The portable, correct way to call these functions
* is to cast them to your desired return type first.
*
* For example, `NSRect result = [myNSView frame]` could be written as:
*   NSRect (*msgSend_stret_fn)(id, SEL, ...) = (NSRect(*)(id, SEL, ...))objc_msgSend_stret;
*   NSRect result = (*msgSend_stret_fn)(myNSView, @selector(frame));
* or, without the function pointer:
*   NSRect result = (*(NSRect(*)(id, SEL, ...))objc_msgSend_stret)(myNSView, @selector(frame));
*
* BE WARNED that these prototypes have changed in the past and will change
* in the future. Code that uses a cast like the example above will be
* unaffected.
*/
```
What I think this means is that despite the prototype saying that the function returns void and takes void**, in reality it doesn't take void** at all, does return a struct by value on the stack, and your calling code had better clean it up.
In C the way to make this happen is to cast the function pointer to a pointer to a function that does return the correct struct by value, thus tricking the compiler into generating the correct cleanup code.

If you don't have a C compiler handy to do this for you, this is going to be tricky.
Luckily Rococoa uses [JNA](https://github.com/java-native-access/jna), which in turns uses [libffi](http://sourceware.org/libffi/), which is very close to having a C compiler to hand at runtime.
Armed with a description of the structure being returned, libffi sorts out the call stack after the call.

It turns out that libffi doesn't only help with objc\_msgSend\_stret.
It surprised me early on with Rococoa that I could call objc\_msgSend as if it returned Java long, and everything worked OK.
This didn't make sense, as a long is larger that an id by some 32 bits.
Now, not documented in objc-runtime.h, but in the [Universal Binary Programming Guidelines, Second Edition](http://developer.apple.com/documentation/MacOSX/Conceptual/universal_binary/universal_binary_tips/chapter_5_section_23.html) it says that you should pull the cast function pointer stunt with objc\_msgSend as well.

If your application directly calls the Objective-C runtime function objc\_msgSend, you should always cast to the appropriate return value.
For instance, for a method that returns a BOOL data type, the following code executes properly on a PPC Macintosh but might not on an Intel-based Macintosh computer:
```
BOOL isEqual = objc_msgSend(string, @selector("isEqual:"), otherString);
```
To ensure that the code does executes properly on an Intel-based Macintosh computer, you would change the code to the following:
```
BOOL isEqual = ((BOOL (*)(id, SEL, id))objc_msgSend)(object, @selector("isEqual:"), otherString);
```
So our long test is passing because the we are calling objc\_msgSend through libffi, telling it that the function returns 64 bits.
Libffi obligingly fixes up the stack as if 64 bits were returned, which it turns out they were, as objc\_msgSend has played the same game of hack the stack as objc\_msgSend\_stret.
Or at least that is my interpretation.
I can't find any reference that says that this is actually what happens when returning a long.
(Also, returning a long does not work this way on PPC.)

Just when I thought that I understood the rules, I tried calling a method that returns NSSize.
This is a struct, so objc\_msgSend\_stret applies. Except that it doesn't.
A bit of Googling yielded [this](http://www.cocoabuilder.com/archive/message/cocoa/2006/6/25/166236), which says:
```
This fails because the struct type is NSPoint. In the i386 function call ABI, sufficiently small structs like NSPoint are returned in registers instead of in memory. 

In this case, you need to use objc_msgSend() (cast to return NSPoint). 
objc_msgSend_stret() only works for structs returned in memory. 

The PPC ABI returns eight-byte structs in memory, so objc_msgSend_stret() is the correct call there. 
```

Ooh, another special case, not even hinted at in the docs seen so far.
The post includes a link to the [Mac OS X ABI Function Call Guide](http://developer.apple.com/documentation/DeveloperTools/Conceptual/LowLevelABI/Introduction.html) which is dense, and doesn't describe when to use objc\_msgSend or objc\_msgSend\_stret.
It does say that 8 byte structs are returned in registers in IA-32 and memory for PPC-32, which I guess hints at the differentiation.

Finally, we have `double objc_msgSend_fpret(id self, SEL op, ...)`.
The [Objective-C 2.0 Runtime Reference](http://developer.apple.com/documentation/Cocoa/Reference/ObjCRuntimeRef/Reference/reference.html#//apple_ref/c/func/objc_msgSend_fpret) says:
```
On the i386 platform, the ABI for functions returning a floating-point value is incompatible with that for functions returning an integral type. 
On the i386 platform, therefore, you must use objc_msgSend_fpret for functions that for functions returning non-integral type. 
For float or long double return types, cast the function to an appropriate function pointer type first.
This function is not used on the PPC or PPC64 platforms.
```

But Rococoa has been happily passing test which return float and double from objC\_msgSend on Intel, as I didn't know about this function.
In fact, the only use I've yet found for objc\_msgSend\_fpret is allowing me to interpret the stret in objc\_msgSend\_stret as STructRET!

## Outstanding Questions ##

  * Am I right so far, or am have I got the wrong end of the stick?
  * If both objc\_msgSend\_stret and objc\_msgSend hack the stack to return different sized return values, why are there 2 functions?
  * Should I call objc\_msgSend\_stret when returning longs on PPC?
  * Do I really need to call objc\_msgSend\_fpret for floating point on Intel?
  * Is there a comprehensive reference to this subject that I'm missing?

## And Some Answers ##

Pending a re-write of this page, some very helpful answers can be found on the [Cocoa-dev Mailing List](http://lists.apple.com/archives/cocoa-dev/2008/Feb/msg02338.html).