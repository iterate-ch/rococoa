#import <Cocoa/Cocoa.h>

id createProxyForJavaObject(void* methodInvokedCallback, void* methodSignatureCallback);


@interface ProxyForJava : NSObject 

void (*methodInvokedCallback)(const char*, id);
char* (*methodSignatureCallback)(const char*);

+ (ProxyForJava*) createWithCallback: (void*) methodInvokedCallback methodSignatureCallback: (void*) methodSignatureCallback;
- (id) initWithCallback: (void*) theMethodInvokedCallback methodSignatureCallback: (void*) theMethodSignatureCallback;

- (void)forwardInvocation:(NSInvocation *) anInvocation;

- (NSMethodSignature *)methodSignatureForSelector:(SEL)aSelector;

@end
