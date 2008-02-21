package org.rococoa.quicktime;

import java.io.File;

import org.rococoa.NSClass;
import org.rococoa.NSObject;
import org.rococoa.NSObjectByReference;
import org.rococoa.NSTestCase;
import org.rococoa.Rococoa;
import org.rococoa.cocoa.NSArray;
import org.rococoa.cocoa.NSError;
import org.rococoa.cocoa.NSMutableDictionary;
import org.rococoa.cocoa.NSNumber;
import org.rococoa.cocoa.NSString;


@SuppressWarnings("nls")
public class QTMovieTest extends NSTestCase {

    private QTMovie movie;

    static {
        @SuppressWarnings("unused")
        QTKit instance = QTKit.instance;
    }
    
    public void test() {
        File file = new File("testdata/DrWho.mov");
        NSObjectByReference errorReference = new NSObjectByReference();
        QTMovie movie = QTMovie.movieWithFile_error(file, errorReference);

        assertFalse(movie.id().isNull());
        assertTrue(errorReference.getValueAs(NSError.class).id().isNull());
        
        QTTime time3 =  movie.currentTime();
        assertEquals(1000, time3.timeScale);     
        
        movie.setSelection(new QTTimeRange(new QTTime(50, 1000), new QTTime(750, 1000)));
        assertEquals(new QTTime(50, 1000), movie.selectionStart());
        assertEquals(new QTTime(750, 1000), movie.selectionDuration());
        assertEquals(new QTTime(800, 1000), movie.selectionEnd());
    }
    
    
    public void testError() {
        File file = new File("NOSUCH");
        NSObjectByReference errorReference = new NSObjectByReference();
        QTMovie movie = QTMovie.movieWithFile_error(file, errorReference);
        assertTrue(movie.id().isNull());
        NSError error = errorReference.getValueAs(NSError.class);
        assertEquals(-2000, error.code());
    }

    public void testAttributeForKey() throws Exception {
        loadMovie("testdata/DrWho.mov");
        NSObject attribute = movie.attributeForKey(QTMovie.QTMovieTimeScaleAttribute);
        
        assertTrue(attribute.isKindOfClass(NSClass.CLASS.classWithName("NSNumber")));
        assertFalse(attribute.isKindOfClass(NSClass.CLASS.classWithName("NSString")));
        
        //need to cast 'rococoa style'
        assertEquals(1000, Rococoa.cast(attribute, NSNumber.class).intValue());
    }

    private void loadMovie(String filename) {
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
        
        movie = QTMovie.movieWithAttributes_error(attributes, null);
        
        assertNotNull(movie);
        assertNotNull(movie.id());
    }
    
    public void testGetTracks() throws Exception {
        loadMovie("testdata/DrWho.mov");
        NSArray tracks = movie.tracks();
        assertEquals(2, tracks.count());
        
        NSArray pictureTracks = movie.tracksOfMediaType(QTMedia.QTMediaTypeVideo);
        assertEquals(1, pictureTracks.count());
        
        NSArray soundTracks = movie.tracksOfMediaType(QTMedia.QTMediaTypeSound);
        assertEquals(1, soundTracks.count());
        
        NSArray mpegTracks = movie.tracksOfMediaType(QTMedia.QTMediaTypeMPEG);
        assertEquals(0, mpegTracks.count());
    }
    
    public void testGetQTMedia() throws Exception {
        loadMovie("testdata/DrWho.mov");
        QTTrack track = Rococoa.cast(movie.tracksOfMediaType(QTMedia.QTMediaTypeVideo).objectAtIndex(0), QTTrack.class);
        QTMedia media = track.media();
        media.quickTimeMedia();
    }
}
