
#import "ProxyForJava.h"


id proxyForJavaObject(void* methodInvokedCallback, void* methodSignatureCallback) {
	return [[ProxyForJava alloc] initWithMethodInvokedCallback: methodInvokedCallback methodSignatureCallback: methodSignatureCallback];
}

@implementation ProxyForJava

- (id) initWithMethodInvokedCallback: (void*) theMethodInvokedCallback methodSignatureCallback: (void*) theMethodSignatureCallback {
	self = [super init];
	if (self != nil) {
		methodInvokedCallback = theMethodInvokedCallback;		
		methodSignatureCallback = theMethodSignatureCallback;
	}
	return self;
}

- (void)forwardInvocation:(NSInvocation *) anInvocation {
	// calls back to Java on methodInvokedCallback, 
	SEL selector = [anInvocation selector];
	NSString* selectorName = NSStringFromSelector(selector);
	// NSLog(@"forwardInvocation for %@", selectorName);
	methodInvokedCallback(CFStringGetCStringPtr((CFStringRef) selectorName, 0), anInvocation);
}

- (BOOL)respondsToSelector:(SEL)aSelector {
	// NSLog(@"respondsToSelector called");
	NSMethodSignature* signature = [self methodSignatureForSelector:aSelector];
	return signature != nil;
}

- (NSMethodSignature *)methodSignatureForSelector:(SEL)aSelector {
	NSString* selectorName = NSStringFromSelector(aSelector);
	// NSLog(@"methodSignatureForSelector %@", selectorName);
	if (aSelector == @selector(hash) || aSelector == @selector(isEqual:))
		return [super methodSignatureForSelector: aSelector];
	
	char* methodSignature = methodSignatureCallback(CFStringGetCStringPtr((CFStringRef) selectorName, 0));
	if (methodSignature == 0) { 
		return nil;
	}
	NSMethodSignature* result = [NSMethodSignature signatureWithObjCTypes: methodSignature];
	return result;
}

@end
