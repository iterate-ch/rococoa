#import <Cocoa/Cocoa.h>

void callOnMainThread(void (*fn)(), BOOL waitUntilDone);

@interface RococoaHelper : NSObject
+ (void) callback: (NSValue*) fn;
@end


