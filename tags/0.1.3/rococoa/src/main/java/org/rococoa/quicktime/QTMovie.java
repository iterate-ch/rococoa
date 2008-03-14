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
 
package org.rococoa.quicktime;

import java.io.File;
import java.util.concurrent.Callable;

import org.rococoa.Foundation;
import org.rococoa.ID;
import org.rococoa.NSClass;
import org.rococoa.NSObject;
import org.rococoa.NSObjectByReference;
import org.rococoa.Rococoa;
import org.rococoa.RunOnMainThread;
import org.rococoa.cocoa.NSArray;
import org.rococoa.cocoa.NSDictionary;
import org.rococoa.cocoa.NSImage;
import org.rococoa.cocoa.NSString;

import com.sun.jna.Pointer;

/**
 * Wrapper for QTKit QTMovie.
 * 
 * For a discussion on threading and QT see 
 * <a href="http://developer.apple.com/technotes/tn/tn2125.html">TN2125</a>
 * 
 * @author duncan
 *
 */
public @RunOnMainThread abstract class QTMovie implements NSObject {
    
    // Loading the QTMovie class has to happen on the main thread
    private static final _Class CLASS = 
        Foundation.callOnMainThread(new Callable<_Class>() {
            public _Class call() throws Exception {
                return Rococoa.wrap(Foundation.nsClass("QTMovie"), _Class.class); //$NON-NLS-1$
            }});
    
    // Creating instances has to happen on the main thread
    private @RunOnMainThread interface _Class extends NSClass {
        QTMovie movie();
        QTMovie movieWithFile_error(String fileName, NSObjectByReference errorReference);        
        QTMovie movieWithAttributes_error(NSDictionary attributes, NSObjectByReference errorReference);
        QTMovie movieWithQuickTimeMovie_disposeWhenDone_error(Pointer movie, boolean b, NSObjectByReference errorReference);
    }

    public static final String QTMovieTimeScaleAttribute = "QTMovieTimeScaleAttribute"; //$NON-NLS-1$
    public static final String QTMovieFileNameAttribute = "QTMovieFileNameAttribute";  //$NON-NLS-1$
    public static final String QTMovieOpenAsyncOKAttribute = "QTMovieOpenAsyncOKAttribute";  //$NON-NLS-1$    
    public static final String QTMoviePlaysSelectionOnlyAttribute = "QTMoviePlaysSelectionOnlyAttribute";  //$NON-NLS-1$
    public static final String QTMovieLoadStateAttribute = "QTMovieLoadStateAttribute";  //$NON-NLS-1$
    public static final long QTMovieLoadStateError = -1L;
    public static final long QTMovieLoadStateLoading = 1000L;
    public static final long QTMovieLoadStateComplete = 100000L;
    
    public static final String QTMovieFlatten = "QTMovieFlatten";  //$NON-NLS-1$
    public static final String QTMovieExport = "QTMovieExport";  //$NON-NLS-1$
    public static final String QTMovieExportType = "QTMovieExportType";  //$NON-NLS-1$
    public static final String QTMovieEditableAttribute = "QTMovieEditableAttribute"; //$NON-NLS-1$
    
    public static QTMovie movie() {
        return CLASS.movie();
    }

    public static QTMovie movieWithFile_error(File file, NSObjectByReference errorReference) {
        return movieWithFile_error(file.getAbsolutePath(), errorReference);
    }
    public static QTMovie movieWithFile_error(String fileName, NSObjectByReference errorReference) {
        return CLASS.movieWithFile_error(fileName, errorReference);
    }
    public static QTMovie movieWithAttributes_error(NSDictionary attributes, NSObjectByReference errorReference) {
        return CLASS.movieWithAttributes_error(attributes, errorReference);
    }
    public static QTMovie movieWithQuickTimeMovie_disposeWhenDone_error(Pointer movie, boolean b, NSObjectByReference errorReference) {
        return CLASS.movieWithQuickTimeMovie_disposeWhenDone_error(movie, b, errorReference);
    }

    public abstract QTTime duration();
    
    public abstract void gotoBeginning();
    
    public abstract void gotoEnd();
    
    public abstract void play();
    
    public abstract void stop();
    
    public abstract void stepBackward();
    
    public abstract void stepForward();

    public abstract void setCurrentTime(QTTime time);
    
    public abstract QTTime currentTime();

    public abstract void setRate(float speed);
    
    public abstract float rate();
    
    public abstract NSImage frameImageAtTime(QTTime time);

    public abstract NSObject attributeForKey(NSString key);
    public abstract NSObject attributeForKey(String key);
    public abstract NSObject attributeForKey(ID keyId);

    public abstract void setAttribute_forKey(NSObject attribute, NSString key);
    public abstract void setAttribute_forKey(NSObject attribute, String key);
    public abstract void setAttribute_forKey(NSObject attribute, ID key);
    
    public abstract void insertSegmentOfMovie_timeRange_atTime(QTMovie movie, QTTimeRange range, QTTime time);
    public abstract void insertSegmentOfMovie_fromRange_scaledToRange(QTMovie movie, QTTimeRange srcRange, QTTimeRange dstRange);
    public abstract void insertEmptySegmentAt(QTTimeRange range);

    public abstract NSArray tracksOfMediaType(String mediaTypeVideo);
    public abstract NSArray tracksOfMediaType(ID mediaTypeVideo);
    public abstract NSArray tracks();

    public abstract void setSelection(QTTimeRange timeRange);
    
    public abstract QTTime selectionStart();
    
    public abstract QTTime selectionDuration();
    
    public abstract QTTime selectionEnd();

    public abstract void writeToFile_withAttributes(String filename, NSDictionary attributes);

    public abstract Pointer quickTimeMovie();

}
