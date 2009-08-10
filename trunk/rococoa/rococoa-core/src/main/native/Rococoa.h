#import <Cocoa/Cocoa.h>
#include <QTKit/QTTime.h>

void callOnMainThread(void (*fn)(), BOOL waitUntilDone);

@interface RococoaHelper : NSObject
+ (void) callback: (NSValue*) fn;
@end


