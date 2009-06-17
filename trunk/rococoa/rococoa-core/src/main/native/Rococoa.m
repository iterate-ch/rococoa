#include "Rococoa.h"
#include <objc/objc-runtime.h>

void callOnMainThread(void (*fn)()) {
	// NSLog(@"callOnMainThread function at address %p", fn);
	// Pool is required as we're being called from Java, which probably doesn't have a pool to 
	// allocate the NSValue from.
	NSAutoreleasePool * pool = [[NSAutoreleasePool alloc] init];
	[RococoaHelper performSelectorOnMainThread: @selector(callback:) 
		withObject: [NSValue valueWithPointer: fn] waitUntilDone: YES];
	[pool release];
}

@implementation RococoaHelper : NSObject

+ (void) callback: (NSValue*) fnAsValue {
	void (*fn)() = [fnAsValue pointerValue]; 
	(*fn)();
} 

@end
