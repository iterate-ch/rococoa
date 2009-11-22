/*
 * Copyright 2007, 2008 Duncan McGregor
 * 
 * This file is part of Rococoa, a library to allow Java to talk to Cocoa.
 * 
 * Rococoa is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Rococoa is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Rococoa.  If not, see <http://www.gnu.org/licenses/>.
 */
 
package org.rococoa.cocoa.qtkit;

import com.sun.jna.Platform;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Test;
import org.rococoa.cocoa.foundation.NSObject;
import org.rococoa.ObjCObjectByReference;
import org.rococoa.ObjCClass;
import org.rococoa.Rococoa;
import org.rococoa.cocoa.foundation.NSArray;
import org.rococoa.cocoa.foundation.NSError;
import org.rococoa.cocoa.foundation.NSMutableDictionary;
import org.rococoa.cocoa.foundation.NSNumber;
import org.rococoa.cocoa.foundation.NSString;
import org.rococoa.test.RococoaTestCase;


@SuppressWarnings("nls")
public class QTMovieTest extends RococoaTestCase {

	static String testMovieFile = "testdata/DrWho.mov";
    static int testMovieTimeScale = 1000;
	static {
        @SuppressWarnings("unused")
        QTKit instance = QTKit.instance;

		if (!new File(testMovieFile).exists()) {
			testMovieFile = "src/test/resources/test.mov";
			testMovieTimeScale = 600;
		}
    }
    
    @Test public void test() {
		File file = new File(testMovieFile);
        ObjCObjectByReference errorReference = new ObjCObjectByReference();
        QTMovie movie = QTMovie.movieWithFile_error(file, errorReference);

        assertNotNull(movie);
        assertNull(errorReference.getValueAs(NSError.class));//.id().isNull());
        QTTime time3 =  movie.currentTime();
        assertEquals(testMovieTimeScale, time3.timeScale.intValue());
        
        movie.setSelection(new QTTimeRange(new QTTime(50, testMovieTimeScale), new QTTime(100, testMovieTimeScale)));
		assertEquals(new QTTime(50, testMovieTimeScale), movie.selectionStart());
        assertEquals(new QTTime(100, testMovieTimeScale), movie.selectionDuration());
		assertEquals(new QTTime(150, testMovieTimeScale), movie.selectionEnd());
    }
	
    @Test public void testError() {
        File file = new File("NOSUCH");
        ObjCObjectByReference errorReference = new ObjCObjectByReference();
		QTMovie movie = QTMovie.movieWithFile_error(file, errorReference);
        assertNull(movie);
        NSError error = errorReference.getValueAs(NSError.class);
		assertEquals(-2000, error.code().intValue());
    }

	@Test public void testAttributeForKey() throws Exception {
        QTMovie movie = loadMovie(testMovieFile);
        NSObject attribute = movie.attributeForKey(QTMovie.QTMovieTimeScaleAttribute);
        
        assertTrue(attribute.isKindOfClass(ObjCClass.CLASS.classWithName("NSNumber")));
        assertFalse(attribute.isKindOfClass(ObjCClass.CLASS.classWithName("NSString")));
        
        //need to cast 'rococoa style'
        assertEquals(testMovieTimeScale, Rococoa.cast(attribute, NSNumber.class).intValue());
    }

    private QTMovie loadMovie(String filename) {
//        NSDictionary attributes = NSDictionary.CLASS.dictionaryWithObjectsAndKeys(
//                NSString.CLASS.stringWithString(filename),
//                NSString.CLASS.stringWithString(QTMovie.QTMovieFileNameAttribute),
//
//                NSNumber.CLASS.numberWithBool(false),
//                NSString.CLASS.stringWithString(QTMovie.QTMovieOpenAsyncOKAttribute),
//
//                null);
        
        NSMutableDictionary attributes = NSMutableDictionary.CLASS.dictionaryWithCapacity(2);
        attributes.setValue_forKey(NSString.CLASS.stringWithString(filename),
                QTMovie.QTMovieFileNameAttribute);
        attributes.setValue_forKey(NSNumber.CLASS.numberWithBool(false),
                QTMovie.QTMovieOpenAsyncOKAttribute);
        
        QTMovie movie = QTMovie.movieWithAttributes_error(attributes, null);
        
        assertNotNull(movie);
        assertNotNull(movie.id());
		return movie;
    }
    
    @Test public void testGetTracks() throws Exception {
        QTMovie movie = loadMovie(testMovieFile);
        NSArray tracks = movie.tracks();
        assertEquals(2, tracks.count());
        
        NSArray pictureTracks = movie.tracksOfMediaType(QTMedia.QTMediaTypeVideo);
        assertEquals(1, pictureTracks.count());
        
        NSArray soundTracks = movie.tracksOfMediaType(QTMedia.QTMediaTypeSound);
        assertEquals(1, soundTracks.count());
        
        NSArray mpegTracks = movie.tracksOfMediaType(QTMedia.QTMediaTypeMPEG);
        assertEquals(0, mpegTracks.count());
    }
    
    @Test public void testGetQTMedia() throws Exception {
        QTMovie movie = loadMovie(testMovieFile);
        QTTrack track = Rococoa.cast(movie.tracksOfMediaType(QTMedia.QTMediaTypeVideo).objectAtIndex(0), QTTrack.class);
        QTMedia media = track.media();
		if (!Platform.is64Bit()) // quickTimeMedia is in a "#if !__LP64__" block
			media.quickTimeMedia();
    }
}
