//
//  test.m
//  rococoa
//
//  Created by Duncan McGregor on 03/12/2007.
//  Copyright 2007 __MyCompanyName__. All rights reserved.
//

#import "test.h"
#import <stdarg.h>

TestStruct returnStructByValue(int a, double b) {
	TestStruct result = {a, b};
	return result;
}

double addFieldsOfStructByValue(TestStruct s) {
	return s.anInt + s.aDouble;
}

// JNA got broken for this case
double addFieldsOfStructByValueVARARGS(size_t count, ...) {
	va_list vl;
	va_start(vl, count);
	TestStruct s = va_arg(vl, TestStruct);
	double result = addFieldsOfStructByValue(s);
	va_end(vl);
	return result;
}

@implementation TestShunt

- (TestStruct) testReturnStructByValue: (int) a and: (double) b {
	return returnStructByValue(a, b);
}

- (double) testAddFieldsOfStructByValue: (TestStruct) s {
	return addFieldsOfStructByValue(s);
}

- (TestStructOfStruct) testReturnStructOfStructByValue: (int) a and: (double) b {
	TestStruct inside = returnStructByValue(a, b);
	TestStructOfStruct result = {b, inside};
	return result;
}

- (double) testPassStructOfStructByValue: (TestStructOfStruct) s {
	return s.aStruct.aDouble;
}

- (void) testNSNumberByReference: (NSNumber**) fillMeIn with: (int) aValue {
	NSNumber* number = [NSNumber numberWithInt: aValue];
	*fillMeIn = number;
}

- (bool) isMainThread {
	return pthread_main_np();
}


@end
