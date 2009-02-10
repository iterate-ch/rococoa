//
//  test.m
//  rococoa
//
//  Created by Duncan McGregor on 03/12/2007.
//  Copyright 2007 __MyCompanyName__. All rights reserved.
//

#import "test.h"
#import <stdarg.h>

TestIntDoubleStruct createIntDoubleStruct(int a, double b) {
	TestIntDoubleStruct result = {a, b};
	return result;
}

double addFieldsOfStructByValue(TestIntDoubleStruct s) {
	return s.anInt + s.aDouble;
}

// JNA got broken for this case
double addFieldsOfStructByValueVARARGS(size_t count, ...) {
	va_list vl;
	va_start(vl, count);
	TestIntDoubleStruct s = va_arg(vl, TestIntDoubleStruct);
	double result = addFieldsOfStructByValue(s);
	va_end(vl);
	return result;
}

TestFloatFloatStruct createFloatFloatStruct(float a, float b) {
	TestFloatFloatStruct result = {a, b};
	return result;
}

TestIntFloatStruct createIntFloatStruct(int a, float b) {
	TestIntFloatStruct result = {a, b};
	return result;
}

TestIntLongStruct createIntLongStruct(int a, int64_t b) {
	TestIntLongStruct result = {a, b};
	return result;
}

TestIntIntStruct createIntIntStruct(int a, int b) {
	TestIntIntStruct result = {a, b};
	return result;
}

@implementation TestShunt

- (TestIntDoubleStruct) testReturnStructByValue: (int) a and: (double) b {
	return createIntDoubleStruct(a, b);
}

- (double) testAddFieldsOfStructByValue: (TestIntDoubleStruct) s {
	return addFieldsOfStructByValue(s);
}

- (TestStructOfStruct) testReturnStructOfStructByValue: (int) a and: (double) b {
	TestIntDoubleStruct inside = createIntDoubleStruct(a, b);
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
