//
//  test.m
//  rococoa
//
//  Created by Duncan McGregor on 03/12/2007.
//  Copyright 2007 __MyCompanyName__. All rights reserved.
//

#import "test.h"
#import <stdarg.h>

MyStruct returnStructByValue(int a, double b) {
	MyStruct result = {a, b};
	return result;
}

double addFieldsOfStructByValue(MyStruct s) {
	return s.anInt + s.aDouble;
}

// JNA got broken for this case
double addFieldsOfStructByValueVARARGS(size_t count, ...) {
	va_list vl;
	va_start(vl, count);
	MyStruct s = va_arg(vl, MyStruct);
	double result = addFieldsOfStructByValue(s);
	va_end(vl);
	return result;
}

int passQTTimeRangeByValue(QTTimeRange r) {
	return r.time.timeValue + r.duration.timeValue;
}

@implementation TestShunt

- (MyStruct) testReturnStructByValue: (int) a and: (double) b {
	return returnStructByValue(a, b);
}

- (double) testAddFieldsOfStructByValue: (MyStruct) s {
	return addFieldsOfStructByValue(s);
}

- (MyStructOfStruct) testReturnStructOfStructByValue: (int) a and: (double) b {
	MyStruct inside = returnStructByValue(a, b);
	MyStructOfStruct result = {b, inside};
	return result;
}

- (double) testPassStructOfStructByValue: (MyStructOfStruct) s {
	return s.aStruct.aDouble;
}

- (int) testPassQTTimeRangeByValue: (QTTimeRange) r {
	return passQTTimeRangeByValue(r);
}

- (void) testNSNumberByReference: (NSNumber**) fillMeIn with: (int) aValue {
	NSNumber* number = [NSNumber numberWithInt: aValue];
	*fillMeIn = number;
}

- (bool) isMainThread {
	return pthread_main_np();
}


@end
