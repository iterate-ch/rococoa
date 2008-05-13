//
//  test.m
//  rococoa
//
//  Created by Duncan McGregor on 03/12/2007.
//  Copyright 2007 __MyCompanyName__. All rights reserved.
//

#import "test.h"

MyStruct returnStructByValue(int a, double b) {
	MyStruct result = {a, b};
	return result;
}

int passQTTimeRangeByValue(QTTimeRange r) {
	return r.time.timeValue + r.duration.timeValue;
}

@implementation TestShunt

- (MyStruct) testReturnStructByValue: (int) a and: (double) b {
	return returnStructByValue(a, b);
}

- (double) testPassStructByValue: (MyStruct) s {
	return s.b;
}

- (MyStructOfStruct) testReturnStructOfStructByValue: (int) a and: (double) b {
	MyStruct inside = returnStructByValue(a, b);
	MyStructOfStruct result = {b, inside};
	return result;
}

- (double) testPassStructOfStructByValue: (MyStructOfStruct) s {
	return s.b.b;
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
