#import <objc/objc-class.h>
#import <objc/objc-runtime.h>
#import <QTKit/QTKit.h>
#import <Cocoa/Cocoa.h>

int main (int argc, const char * argv[]) {
		
	NSAutoreleasePool * pool = [[NSAutoreleasePool alloc] init];
	
	QTMovie* movie = [QTMovie movieWithFile: @"DrWho.mov" error: nil];

	NSLog(@"Hello %@", QTMovieTimeDidChangeNotification);

	[pool release];
	return 0;
}
