/*
 * Copyright 2007, 2008, 2009 Duncan McGregor
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

package org.rococoa.contrib.appkit;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.junit.After;
import org.junit.Before;
import static org.junit.Assert.*;
import org.junit.Test;


import org.rococoa.cocoa.foundation.NSAutoreleasePool;
import org.rococoa.contrib.appkit.NSSpeechSynthesizer.NSSpeechStatus;
import org.rococoa.cocoa.foundation.NSRange;


/** Exercise the speech synthesizer.
 */
public class NSSpeechSynthesizerTest { 
    private static final int TIME_TO_WAIT = 5000;
    private NSAutoreleasePool pool;

    @Before
    public void preSetup() {
        pool = NSAutoreleasePool.new_();
    }

    @After
    public void postTeardown() {
        pool.drain();
    }

    @Test
    public void testDefaultVoice() {
        assertNotNull(NSSpeechSynthesizer.CLASS.defaultVoice()); //System preference, so no way of knowing actual value
        assertNotNull(NSSpeechSynthesizer.defaultVoice().getName());
        assertNotNull(NSSpeechSynthesizer.synthesizerWithVoice(null));
        assertEquals(NSSpeechSynthesizer.defaultVoice(), NSSpeechSynthesizer.synthesizerWithVoice(null).getVoice());
    }
    
    @Test
    public void testAvailableVoices() {
        assertEquals(NSSpeechSynthesizer.CLASS.availableVoices().count(), NSSpeechSynthesizer.availableVoices().size());
        assertTrue(NSSpeechSynthesizer.availableVoices().size() > 0);
        assertNotNull(NSSpeechSynthesizer.availableVoices().get(0).getName());
        assertTrue(NSSpeechSynthesizer.availableVoices().get(0).getName().length() > 0);
    }
 
    @Test
    public void testAddGetSpeechDictionary() {
        //first, let's teach the synth to talk like its from Newcastle (sort of)
        NSSpeechDictionary dict = new NSSpeechDictionary();
        dict.setLocaleIdentifier(Locale.US);
        Date now = new Date();
        dict.setModificationDate(now);
        assertEquals(Locale.US, dict.getLocaleIdentifier());
        assertEquals(now, dict.getModificationDate());
        dict.addPronounciation(new NSSpeechDictionary.Entry("about", "AXbUWt")); //en_GB_geordie!
        NSSpeechSynthesizer ss = NSSpeechSynthesizer.synthesizerWithVoice(null);
        SynthesizerDelegate sd = new SynthesizerDelegate(ss);
        ss.setDelegate(sd);
        ss.addSpeechDictionary(dict);
        ss.startSpeakingString("about");
        sd.waitForSpeechDone(TIME_TO_WAIT, true);
        String[] expected = new String[] {"%", "AX", "b", "UW", "t", "%"};
        assertEquals(Arrays.asList(expected), sd.getPhonemesSpoken());
        
        //Normally the synth falls into the SQL = 'S' 'Q' 'L' camp
        sd.reset();
        ss.startSpeakingString("SQL");
        sd.waitForSpeechDone(TIME_TO_WAIT, true);
        expected = new String[] {"%", "EH", "s", "k", "y", "UW", "EH", "l", "%"};
        assertEquals(Arrays.asList(expected), sd.getPhonemesSpoken());
        
        //but we can make it say  'sequel' instead...
        dict.setModificationDate(new Date());
        dict.addAbbreviation(new NSSpeechDictionary.Entry("SQL", "sIYkwAXl"));
        ss.addSpeechDictionary(dict);

        sd.reset();
        ss.startSpeakingString("SQL");
        sd.waitForSpeechDone(TIME_TO_WAIT, true);
        expected = new String[] {"%", "s", "IY", "k", "w", "AX", "l", "%"};
        assertEquals(Arrays.asList(expected), sd.getPhonemesSpoken());
    }
    
    @Test
    public void testStartSpeakingString() throws InterruptedException {
        NSSpeechSynthesizer ss = NSSpeechSynthesizer.synthesizerWithVoice(null);
        SynthesizerDelegate sd = new SynthesizerDelegate(ss);
        ss.startSpeakingString("Hello world");
        sd.waitForSpeechDone(TIME_TO_WAIT, true);
    }

    @Test
    public void testIsSpeaking() throws InterruptedException {
        NSSpeechSynthesizer ss = NSSpeechSynthesizer.synthesizerWithVoice(null);
        SynthesizerDelegate sd = new SynthesizerDelegate(ss);
        assertTrue(!ss.isSpeaking());
        ss.startSpeakingString("Hello world");
        assertTrue(ss.isSpeaking());
        sd.waitForSpeechDone(TIME_TO_WAIT, true);
    }

    @Test
    public void testIsAnyApplicationSpeaking() throws InterruptedException {
        NSSpeechSynthesizer ss = NSSpeechSynthesizer.synthesizerWithVoice(null);
        SynthesizerDelegate sd = new SynthesizerDelegate(ss);
        ss.setDelegate(sd);
        assertTrue(!NSSpeechSynthesizer.isAnyApplicationSpeaking());
        ss.startSpeakingString("Hello world");
        assertTrue(NSSpeechSynthesizer.isAnyApplicationSpeaking());
        sd.waitForSpeechDone(TIME_TO_WAIT, true);
    }

    @Test
    public void testDidFinishSpeaking() {
        NSSpeechSynthesizer ss = NSSpeechSynthesizer.synthesizerWithVoice(null);
        SynthesizerDelegate sd = new SynthesizerDelegate(ss);
        ss.setDelegate(sd);
        ss.startSpeakingString("hello doctor");
        sd.waitForSpeechDone(TIME_TO_WAIT, true);
    }

    @Test
    public void testWillSpeakWord() {
        NSSpeechSynthesizer ss = NSSpeechSynthesizer.synthesizerWithVoice(null);
        SynthesizerDelegate sd = new SynthesizerDelegate(ss);
        ss.setDelegate(sd);
        String toSpeak = "hello doctor name";
        ss.startSpeakingString(toSpeak);
        sd.waitForSpeechDone(5000, true);
        assertEquals(Arrays.asList(toSpeak.split(" ")), sd.getWordsSpoken());
    }

    @Test
    public void testWillSpeakPhoneme() {
        NSSpeechSynthesizer ss = NSSpeechSynthesizer.synthesizerWithVoice(null);
        SynthesizerDelegate sd = new SynthesizerDelegate(ss);
        ss.setDelegate(sd);
        String toSpeak = "blue daisy";
        ss.startSpeakingString(toSpeak);
        sd.waitForSpeechDone(TIME_TO_WAIT, true);
        //every so often some of the phonemes get flipped around, which isn't important to this test
        List<String> expected = new ArrayList<String>(Arrays.asList(new String[] {"%", "b", "l", "UW", "d", "EY", "z", "IY", "%"}));
        Collections.sort(expected);
        List<String> actual = new ArrayList<String>(sd.getPhonemesSpoken());
        Collections.sort(actual);
        assertEquals(expected,actual);
    }

    @Test
    public void testStopSpeakingAtBoundary() throws InterruptedException {
        NSSpeechSynthesizer ss = NSSpeechSynthesizer.synthesizerWithVoice(null);
        SynthesizerDelegate sd = new SynthesizerDelegate(ss);
        ss.setDelegate(sd);
        String toSpeak = "Hello are you receiving me now? I really hope someone is!";
        ss.startSpeakingString(toSpeak);
        Thread.sleep(50);
        ss.stopSpeakingAtBoundary(NSSpeechSynthesizer.NSSpeechBoundary.WordBoundary);
        sd.waitForSpeechDone(TIME_TO_WAIT, false);
        //don't want test case to be too timing dependent
        assertTrue("Expected less than 3 words but got: " + sd.getWordsSpoken(), sd.getWordsSpoken().size() < 3);
        assertTrue("Expected at least one word but got: " + sd.getWordsSpoken(), sd.getWordsSpoken().size() >= 1);

        //near as I can tell, SentenceBoundary just doesn't work!
        sd.reset();
        ss.startSpeakingString(toSpeak);
        sd.waitForNextWord(TIME_TO_WAIT);
        ss.stopSpeakingAtBoundary(NSSpeechSynthesizer.NSSpeechBoundary.SentenceBoundary);
        sd.waitForWord(TIME_TO_WAIT, "now");
        sd.waitForSpeechDone(TIME_TO_WAIT, false);
        assertTrue("Expected 6 word sentence but got: " + sd.getWordsSpoken(), sd.getWordsSpoken().size() == 6);        

        sd.reset();
        ss.startSpeakingString(toSpeak);
        sd.waitForWord(TIME_TO_WAIT, "are");
        ss.stopSpeakingAtBoundary(NSSpeechSynthesizer.NSSpeechBoundary.ImmediateBoundary);
        sd.waitForSpeechDone(TIME_TO_WAIT, false);
        assertTrue("Expected less than 3 words but got: " + sd.getWordsSpoken(), sd.getWordsSpoken().size() < 3);
        assertTrue("Expected at least one word but got: " + sd.getWordsSpoken(), sd.getWordsSpoken().size() >= 0);
    }

    @Test
    public void testGetStatus() {
        NSSpeechSynthesizer ss = NSSpeechSynthesizer.synthesizerWithVoice(null);
        SynthesizerDelegate sd = new SynthesizerDelegate(ss);        
        NSSpeechStatus status = ss.getStatus();
        assertEquals(status.isOutputBusy(), ss.isSpeaking());
        assertFalse(status.isOutputPaused());
        assertEquals("Should have no characters left", 0, status.getNumberOfCharactersLeft());
        assertEquals(0, status.getPhonemeCode());
        
        ss.startSpeakingString("Status check");
        status = ss.getStatus();
        assertEquals(status.isOutputBusy(), ss.isSpeaking());
        assertFalse(status.isOutputPaused());
        assertTrue("Should have characters left", status.getNumberOfCharactersLeft() > 0);
        //assertTrue("Opcode should not be zero", status.getPhonemeCode() != 0); always zero... seems to have word granularity
        sd.waitForSpeechDone(TIME_TO_WAIT, true);
    }
    
    @Test
    public void testPauseSpeakingAtBoundary() throws InterruptedException {
        NSSpeechSynthesizer ss = NSSpeechSynthesizer.synthesizerWithVoice(null);
        SynthesizerDelegate sd = new SynthesizerDelegate(ss);
        ss.startSpeakingString("Status check number two");
        sd.waitForNextWord(1000);
        ss.pauseSpeakingAtBoundary(NSSpeechSynthesizer.NSSpeechBoundary.WordBoundary);
        Thread.sleep(1000); //this API is very asynchronous ... need to sleep before polling status
        NSSpeechStatus status = ss.getStatus();   
        assertFalse("Output should not be busy", status.isOutputBusy());
        assertTrue("Output should be paused", status.isOutputPaused());
        assertEquals("Check number of characters left failed", 16, status.getNumberOfCharactersLeft());        
        ss.continueSpeaking();
        sd.waitForNextWord(2500);
        ss.pauseSpeakingAtBoundary(NSSpeechSynthesizer.NSSpeechBoundary.ImmediateBoundary);
        Thread.sleep(TIME_TO_WAIT);
        status = ss.getStatus();   
        assertFalse("Output should not be busy", status.isOutputBusy());
        assertTrue("Output should be paused", status.isOutputPaused());
        assertEquals("Check number of characters left failed", 10, status.getNumberOfCharactersLeft());
        ss.continueSpeaking();
        sd.waitForSpeechDone(TIME_TO_WAIT, true);
    }

    @Test
    public void testPauseSpeakingAtSentenceBoundary() throws InterruptedException {
        NSSpeechSynthesizer ss = NSSpeechSynthesizer.synthesizerWithVoice(null);
        SynthesizerDelegate sd = new SynthesizerDelegate(ss);
        ss.startSpeakingString("This is the way the world ends. Not with a bang.");
        sd.waitForNextWord(1000);
        ss.pauseSpeakingAtBoundary(NSSpeechSynthesizer.NSSpeechBoundary.SentenceBoundary);
        sd.waitForWord(10000, "ends"); //this tells you the word is _about_ to be spoken
        Thread.sleep(750); //so we need to wait a bit more
        NSSpeechStatus status = ss.getStatus();   
        assertFalse("Output should not be busy", status.isOutputBusy());
        assertTrue("Output should be paused", status.isOutputPaused());
        //often returns 22, which is just before 'ends'. There's a heck of a lag, basically, in the getStatus interface
        assertTrue("Check number of characters left failed", status.getNumberOfCharactersLeft() >= 16);
        ss.continueSpeaking();
        sd.waitForSpeechDone(5000, true);
    }
    
    @Test
    public void testGetError() throws InterruptedException {
        NSSpeechSynthesizer ss = NSSpeechSynthesizer.synthesizerWithVoice(null);
        SynthesizerDelegate sd = new SynthesizerDelegate(ss);
        ss.startSpeakingString("Try this one [[pbas foobar]] two　three");
        sd.waitForWord(1000, "three");
        assertTrue("Should have error position", sd.position > 0);
        assertTrue("Should have error message", sd.errorMessage != null);

        NSSpeechSynthesizer.NSSpeechError error = ss.getError();
        assertTrue("Should find error", error.getErrorCount() > 0);
        assertTrue("Should have error position", error.getNewestCharacterOffset() > 0);
        assertTrue("Should have error code", error.getNewestCode() != 0);
        sd.waitForSpeechDone(5000, true);
    }

    @Test
    public void testInputMode() {
        NSSpeechSynthesizer ss = NSSpeechSynthesizer.synthesizerWithVoice(null);
        assertEquals(NSSpeechSynthesizer.NSSpeechMode.Text, ss.getInputMode());
        ss.setInputMode(NSSpeechSynthesizer.NSSpeechMode.Text);
        assertEquals("Should be text in, phonemes out", "_d1AOg.", ss.phonemesFromText("dog"));
        ss.setInputMode(NSSpeechSynthesizer.NSSpeechMode.Phoneme);
        assertEquals(NSSpeechSynthesizer.NSSpeechMode.Phoneme, ss.getInputMode());
        assertEquals("Should be phonemes in, phonemes out", "_d1AOg.", ss.phonemesFromText("_d1AOg."));
    }
    
    @Test
    public void testCharacterMode() {
        NSSpeechSynthesizer ss = NSSpeechSynthesizer.synthesizerWithVoice(null);
        assertEquals(NSSpeechSynthesizer.NSSpeechMode.Normal, ss.getCharacterMode());
        ss.setCharacterMode(NSSpeechSynthesizer.NSSpeechMode.Normal);
        assertEquals("Should say dog", "_d1AOg.", ss.phonemesFromText("dog"));
        ss.setCharacterMode(NSSpeechSynthesizer.NSSpeechMode.Literal);
        assertEquals(NSSpeechSynthesizer.NSSpeechMode.Literal, ss.getCharacterMode());
        assertEquals("Should say d o g", "_d1IY ~2OW _J1IY.", ss.phonemesFromText("dog"));
    }
    
    @Test
    public void testNumberMode() {
        NSSpeechSynthesizer ss = NSSpeechSynthesizer.synthesizerWithVoice(null);
        assertEquals(NSSpeechSynthesizer.NSSpeechMode.Normal, ss.getNumberMode());
        ss.setNumberMode(NSSpeechSynthesizer.NSSpeechMode.Normal);
        assertEquals("Should say twelve", "_tw1EHlv.", ss.phonemesFromText("12"));
        ss.setNumberMode(NSSpeechSynthesizer.NSSpeechMode.Literal);
        assertEquals(NSSpeechSynthesizer.NSSpeechMode.Literal, ss.getNumberMode());
        assertEquals("Should say one two", "_w1UXn _t1UW.", ss.phonemesFromText("12"));
    }

    @Test
    public void testSynthesizerInfo() {
        NSSpeechSynthesizer ss = NSSpeechSynthesizer.synthesizerWithVoice(null);
        NSSpeechSynthesizer.NSSpeechSynthesizerInfo ssi = ss.getSynthesizerInfo();
        assertTrue(ssi.getSynthesizerIdentifier() != null);
        assertTrue(ssi.getSynthesizerVersion() != null);
    }

    @Test
    public void testPitchBase() {
        NSSpeechSynthesizer ss = NSSpeechSynthesizer.synthesizerWithVoice(null);
        float pitchBase = ss.getPitchBase();
        assertTrue(pitchBase > 0.0f);
        ss.setPitchBase(pitchBase * 1.5f);
        assertEquals(pitchBase * 1.5f, ss.getPitchBase(), 0.001);
    }

    @Test
    public void testPitchMod() {
        NSSpeechSynthesizer ss = NSSpeechSynthesizer.synthesizerWithVoice(null);
        float pitchMod = ss.getPitchMod();
        assertTrue(pitchMod > 0.0f);
        ss.setPitchMod(pitchMod * 0.9f);
        assertEquals(pitchMod * 0.9f, ss.getPitchMod(), 0.001);
        try {
            ss.setPitchMod(-1.0f);
            fail("Should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException iae) {
        }
        try {
            ss.setPitchMod(127.1f);
            fail("Should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException iae) {
        }
    }

    @Test
    public void testPhonemeInfo() {
        NSSpeechSynthesizer ss = NSSpeechSynthesizer.synthesizerWithVoice(null);
        List<NSSpeechSynthesizer.NSSpeechPhonemeInfo> spis = ss.getPhonemeInfo();
        assertTrue(spis.size() > 5);
        NSSpeechSynthesizer.NSSpeechPhonemeInfo spi = spis.get(4);
        assertTrue(spi.getExample() != null);
        assertTrue(spi.getSymbol() != null);
        assertTrue(spi.getHiliteEnd() >= 0);
        assertTrue(spi.getHiliteStart() >= 0);
        assertTrue(spi.getOpcode() != 0);

    }

    @Test
    public void testRecentSyncAndCallback() throws InterruptedException {
        NSSpeechSynthesizer ss = NSSpeechSynthesizer.synthesizerWithVoice(null);
        SynthesizerDelegate sd = new SynthesizerDelegate(ss);
        ss.startSpeakingString("I see no " + NSSpeechSynthesizer.createSyncPoint('A') + " ships sailing");
        sd.waitForWord(2500, "sailing");
        assertEquals("Should have synch with A", "A", sd.synchMark);
        sd.waitForSpeechDone(3000, true);   
        assertEquals("Should be able to get recent sync", 'A', ss.getRecentSync());
    }

    @Test
    public void testVoice() {
        NSSpeechSynthesizer ss = NSSpeechSynthesizer.synthesizerWithVoice(null);
        NSVoice defaultVoice = NSSpeechSynthesizer.defaultVoice();
        assertEquals(defaultVoice, ss.getVoice());
        assertEquals(defaultVoice.getIdentifier(), ss.voice());
        ss.setVoice(NSVoice.BAD_NEWS);
        assertEquals(NSVoice.BAD_NEWS, ss.voice());
        assertEquals(new NSVoice(NSVoice.BAD_NEWS), ss.getVoice());
        ss.setVoice(NSVoice.BRUCE);
        assertEquals(new NSVoice(NSVoice.BRUCE), ss.getVoice());
        ss = NSSpeechSynthesizer.synthesizerWithVoice(new NSVoice(NSVoice.FRED));
        assertEquals(new NSVoice(NSVoice.FRED), ss.getVoice());
    }

    
    @Test
    public void testCommandDelimiter() {
        NSSpeechSynthesizer ss = NSSpeechSynthesizer.synthesizerWithVoice(null);
        SynthesizerDelegate sd = new SynthesizerDelegate(ss);

        // this raises a question - NSSpeechCommand - should it encapsulate the available commands and
        //offer factory methods? e.g. NSSpeechCommand.createSyncPoint above has a bug, in the sense that it doesn't know what the
        //current delimiters actually are... actually, since there's no API to GET the current delimiters, in the general case it can't
        //work - the caller would always have to pass them in - still could maybe work as a factory still, just more complex
        //something like NSSpeechCommand.createSync(String prefix, String suffix, String syncPoint) ? 

        ss.setCommandDelimiter(new NSSpeechSynthesizer.NSSpeechCommand("{", "}"));
        ss.startSpeakingString("I see no {sync 0x42} ships sailing");
        sd.waitForWord(2500, "sailing");
        assertEquals("Should have synch with B", "B", sd.synchMark);
        sd.waitForSpeechDone(3000, true);
        
    }

    @Test
    public void testReset() {
        NSSpeechSynthesizer ss = NSSpeechSynthesizer.synthesizerWithVoice(null);
        float pitchBase = ss.getPitchBase();
        assertTrue(pitchBase > 0.0f);
        ss.setPitchBase(pitchBase + 1.0f);
        assertEquals(pitchBase + 1.0f, ss.getPitchBase(), 0.001);
        ss.reset();
        assertEquals(pitchBase, ss.getPitchBase(), 0.001);
    }

    //pass null for computer speakers
    @Test
    public void testSetOutputToFileURL() throws IOException {
        File helloWorld = null;
        FileInputStream fis = null;
        try {
            helloWorld = File.createTempFile("helloworld", ".aiff");
            helloWorld.deleteOnExit();
            NSSpeechSynthesizer ss = NSSpeechSynthesizer.synthesizerWithVoice(null);
            SynthesizerDelegate sd = new SynthesizerDelegate(ss);
            ss.setOutputToFileURL(helloWorld.toURI());
            ss.startSpeakingString("Hello World");
            sd.waitForSpeechDone(5000, true);
            assertTrue(helloWorld.exists());
            fis = new FileInputStream(helloWorld);
            assertTrue("Should have some bytes", fis.available() > 0);
        } finally {
            if ( fis != null ) {
                fis.close();
            }
            if ( helloWorld != null ) {
                helloWorld.delete();
            }
        }
    }

    @Test
    public void testStartSpeakingStringToURL() throws IOException {
        File helloWorld = null;
        FileInputStream fis = null;
        try {
            helloWorld = File.createTempFile("helloworld", ".aiff");
            helloWorld.deleteOnExit();
            NSSpeechSynthesizer ss = NSSpeechSynthesizer.synthesizerWithVoice(null);
            SynthesizerDelegate sd = new SynthesizerDelegate(ss);
            ss.startSpeakingStringToURL("Hello World", helloWorld.toURI());
            sd.waitForSpeechDone(5000, true);
            assertTrue(helloWorld.exists());
            fis = new FileInputStream(helloWorld);
            assertTrue("Should have some bytes", fis.available() > 0);
        } finally {
            if ( fis != null ) {
                fis.close();
            }
            if ( helloWorld != null ) {
                helloWorld.delete();
            }
        }
    }

    private static class SynthesizerDelegate implements NSSpeechSynthesizer.NSSpeechSynthesizerDelegate {

        private volatile boolean success = false;
        private List<String> wordsSpoken = new ArrayList<String>();
        private List<String> phonemesSpoken = new ArrayList<String>();
        private String wordWaitingFor;
        private int position = -1;
        private String synchMark;
        private String errorMessage;
        private static final Object speechDoneMonitor = new Object();
        private static final Object waitForSpeechWordMonitor = new Object();
        
        SynthesizerDelegate(NSSpeechSynthesizer ss) {
            ss.setDelegate(this);
        }

        public void reset() {
            success = false;
            wordsSpoken.clear();
            phonemesSpoken.clear();
            wordWaitingFor = null;
            position = -1;
            errorMessage = null;
            synchMark = null;
        }

        public boolean isSuccess() {
            return success;
        }

        public List<String> getWordsSpoken() {
            return wordsSpoken;
        }

        public List<String> getPhonemesSpoken() {
            return phonemesSpoken;
        }

        public void speechSynthesizer_didFinishSpeaking(NSSpeechSynthesizer sender, final boolean success) {
            this.success = success;
            synchronized (speechDoneMonitor) {
                speechDoneMonitor.notify();
            }
        }

        public void waitForSpeechDone(long interval, boolean stoppedNormally) {
            synchronized (speechDoneMonitor) {
                try {
                    speechDoneMonitor.wait(interval);
                    assertEquals("Success flag check failed", stoppedNormally, isSuccess());
                } catch (InterruptedException ex) {
                    fail("Should have been notified in " + getCallerName() + " but interrupted out: " + ex);
                }
            }
        }
       
        public void waitForNextWord(long interval) {
            synchronized (waitForSpeechWordMonitor) {
                try {
                    waitForSpeechWordMonitor.wait(interval);
                } catch (InterruptedException ex) {
                    fail("Should have been notified in " + getCallerName() + " but interrupted out: " + ex);
                }
            }
        }
                
        public void waitForWord(long interval, final String word) {
            synchronized (waitForSpeechWordMonitor) {
                wordWaitingFor = word;
                try {
                    waitForSpeechWordMonitor.wait(interval);
                } catch (InterruptedException ex) {
                    fail("Should have been notified in " + getCallerName() + " but interrupted out: " + ex);
                }
            }
        }

        private String getCallerName() {
            for (StackTraceElement ste : Thread.currentThread().getStackTrace()) {
                if ( ste.getMethodName().startsWith("test") ) {
                    return ste.getMethodName();
                }
            }
            return "Unknown method";
        }

        public void speechSynthesizer_didEncounterErrorAtIndex_ofString_message(NSSpeechSynthesizer sender, Integer characterIndex, String text, String errorMessage) {
            position = characterIndex;
            this.errorMessage = errorMessage;
            //System.out.println(errorMessage);
            //System.out.println("In callback: " + sender.getError());
        }

        public void speechSynthesizer_didEncounterSyncMessage(NSSpeechSynthesizer sender, String synchMark) {
            this.synchMark = synchMark;
         //   System.out.println("In callback, sync: " + sender.getRecentSync());
        }

        public synchronized void speechSynthesizer_willSpeakPhoneme(NSSpeechSynthesizer sender, short phonemeOpcode) {
            phonemesSpoken.add(sender.opcodeToPhoneme(phonemeOpcode));
        }

        public void speechSynthesizer_willSpeakWord_ofString(NSSpeechSynthesizer sender, NSRange wordToSpeak, String text) {
            wordsSpoken.add(text.substring((int) wordToSpeak.getLocation(), (int) wordToSpeak.getEndLocation()));
            if ( wordWaitingFor == null || wordsSpoken.get(wordsSpoken.size()-1).equals(wordWaitingFor)) {
                synchronized(waitForSpeechWordMonitor) {
                    waitForSpeechWordMonitor.notify();
                }
            }
        }
    }
}
