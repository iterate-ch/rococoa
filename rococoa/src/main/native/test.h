//
//  test.h
//  rococoa
//
//  Created by Duncan McGregor on 03/12/2007.
//  Copyright 2007 __MyCompanyName__. All rights reserved.
//

#import <Cocoa/Cocoa.h>
#import <QTKit/QTKit.h>

typedef struct TestStruct {
	int anInt;
	double aDouble;
} TestStruct;

typedef struct TestStructOfStruct {
	double aDouble;
	TestStruct aStruct;
} TestStructOfStruct;

TestStruct returnStructByValue(int a, double b);

double addFieldsOfStructByValue(TestStruct s);

@interface TestShunt : NSObject

- (TestStruct) testReturnStructByValue: (int) a and: (double) b;

- (double) testAddFieldsOfStructByValue: (TestStruct) s;

- (TestStructOfStruct) testReturnStructOfStructByValue: (int) a and: (double) b;

- (double) testPassStructOfStructByValue: (TestStructOfStruct) s;

- (void) testNSNumberByReference: (NSNumber**) fillMeIn with: (int) aValue;

- (bool) isMainThread;

@end
