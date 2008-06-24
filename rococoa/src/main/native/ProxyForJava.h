#import <Cocoa/Cocoa.h>

id proxyForJavaObject(void* methodInvokedCallback, void* methodSignatureCallback);


@interface ProxyForJava : NSObject {

void (*methodInvokedCallback)(const char*, id);
char* (*methodSignatureCallback)(const char*);

}

- (id) initWithMethodInvokedCallback: (void*) theMethodInvokedCallback methodSignatureCallback: (void*) theMethodSignatureCallback;

- (void)forwardInvocation:(NSInvocation *) anInvocation;

- (NSMethodSignature *)methodSignatureForSelector:(SEL)aSelector;

@end
