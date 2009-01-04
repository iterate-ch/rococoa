#import <Cocoa/Cocoa.h>
#include <QTKit/QTTime.h>

void callOnMainThread(void (*fn)());

@interface RococoaHelper : NSObject
+ (void) callback: (NSValue*) fn;
@end


