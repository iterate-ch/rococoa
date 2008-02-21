
#import "ProxyForJava.h"


id createProxyForJavaObject(void* methodInvokedCallback, void* methodSignatureCallback) {
	return [ProxyForJava createWithCallback: methodInvokedCallback methodSignatureCallback: methodSignatureCallback]; 
}

@implementation ProxyForJava

+ (ProxyForJava*) createWithCallback: (void*) methodInvokedCallback methodSignatureCallback: (void*) methodSignatureCallback {
	return [[[ProxyForJava alloc] initWithCallback: methodInvokedCallback methodSignatureCallback: methodSignatureCallback] autorelease];
} 

- (id) initWithCallback: (void*) theMethodInvokedCallback methodSignatureCallback: (void*) theMethodSignatureCallback {
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
	methodInvokedCallback(CFStringGetCStringPtr(selectorName, 0), anInvocation);
}

- (NSMethodSignature *)methodSignatureForSelector:(SEL)aSelector {
	NSString* selectorName = NSStringFromSelector(aSelector);
	// NSLog(@"methodSignatureForSelector %@", selectorName);
	if (aSelector == @selector(hash) || aSelector == @selector(isEqual:))
		return [super methodSignatureForSelector: aSelector];
	
	char* methodSignature = methodSignatureCallback(CFStringGetCStringPtr(selectorName, 0));
	if (methodSignature == 0) { 
		NSLog(@"No method signature for selector %@", selectorName);
		return nil;
	}
	NSMethodSignature* result = [NSMethodSignature signatureWithObjCTypes: methodSignature];
	return result;
}

@end
