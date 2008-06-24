//
//  test.h
//  rococoa
//
//  Created by Duncan McGregor on 03/12/2007.
//  Copyright 2007 __MyCompanyName__. All rights reserved.
//

#import <Cocoa/Cocoa.h>
#import <QTKit/QTKit.h>

typedef struct MyStruct {
	int anInt;
	double aDouble;
} MyStruct;

typedef struct MyStructOfStruct {
	double aDouble;
	MyStruct aStruct;
} MyStructOfStruct;

MyStruct returnStructByValue(int a, double b);

double addFieldsOfStructByValue(MyStruct s);

int passQTTimeRangeByValue(QTTimeRange r);

@interface TestShunt : NSObject

- (MyStruct) testReturnStructByValue: (int) a and: (double) b;

- (double) testAddFieldsOfStructByValue: (MyStruct) s;

- (MyStructOfStruct) testReturnStructOfStructByValue: (int) a and: (double) b;

- (double) testPassStructOfStructByValue: (MyStructOfStruct) s;

- (int) testPassQTTimeRangeByValue: (QTTimeRange) r;

- (void) testNSNumberByReference: (NSNumber**) fillMeIn with: (int) aValue;

- (bool) isMainThread;

@end
