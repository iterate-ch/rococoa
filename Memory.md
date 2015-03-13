# Rococoa Memory Management #

## The autorelease pool ##

While Java has garbage collection, Objective-C memory management (prior to Objective-C 2.0) is based around manual reference counting.

[Essentially](http://developer.apple.com/documentation/Cocoa/Conceptual/MemoryMgmt/MemoryMgmt.html), objects have a reference count, 1 when first created, and incremented by calling `- (id)retain`. On calling `- (void)release` the count is decremented, and if it has fallen to 0, the object's memory is reclaimed. (Actuallly Rococoa uses `CFRetain` and `CFRelease`).

Conventionally, newly created objects are put into an autorelease pool, which retains them, and when the pool itself is reclaimed, all of its objects are `release`d. If an object should live longer than the current pool, then client code should `retain` the object itself, and release when done with.

Rococoa automatically retains Objective-C objects wrapped by the Java `NSObject`, and releases them when the Java `NSObject` is garbage collected. So why am I bothering you with all this detail? Because you need an autorelease pool in the first place.

At the moment the way Rococoa creates a pool is through `ID Foundation.createPool()`, releasing it with `void Foundation.releasePool(ID pool)`. It's your job to make sure that there is a current pool before you invoke any Objective-C methods which need to allocate from it. For example, RococoaTestCase says
```
   public void runBare() throws Throwable {
       pool = NSAutoreleasePool.new_();
       try {
           super.runBare();
       } finally {
           pool.drain();
       }
   }
```
thus giving each test run its own pool.

Helpfully, if you forget to create a pool, the Objective-C runtime will gently chastise you on stderr with messages like :
`*** _NSAutoreleaseNoPool(): Object 0x1222a0 of class NSCFString autoreleased with no pool in place - just leaking`

Note that while a pool is required during allocation, objects can outlive their pool, and will if you keep a reference to a Java NSObject.

~~I plan to replace those nasty calls to Foundation with a Java NSAutoreleasePool which you can just hang onto by reference. But the current system works, and it's best to be conservative about memory, as accessing an already disposed object will crash your app. Actually I'm pretty sure that there's at least one bug hiding in there already - a small prize to anyone who finds it.~~

Finally, note that if your code has been called by the Cocoa event thread (as a result of a native button press for example) then Cocoa will already have arranged a pool for you, and will dispose of it once the event is done.

~~Really finally, I've no idea how this all relates to Objective-C garbage collection, introduced in v2, and am hoping that someone will be able to tell me.~~. See also [issue 7](https://code.google.com/p/rococoa/issues/detail?id=7).

## Java weak references and Rococoa proxies ##

Always make sure to keep a strong reference in Java for an object that is exposed to the Obj-C runtime using the proxying mechanism in Rococoa. Otherwise when the Java reference is garbage collected, during finalization the proxy is released using `CFRelease` in `NSObjectInvocationHandler` This will cause the object to be potentially deallocated too early while it is still referenced from another object living in Obj-C runtime.

Some examples where this applies are:
  * Delegates and datasource protocols implemented in Java.
  * Return values from datasource methods.
  * Observers and its notification senders you register as a target in `NSNotficationCenter`.