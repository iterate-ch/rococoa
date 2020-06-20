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

import java.net.URI;
import java.util.ArrayList;
import java.util.List;


import org.rococoa.contrib.AbstractPropertyDictionary;
import org.rococoa.ID;
import org.rococoa.ObjCClass;
import org.rococoa.ObjCObject;
import org.rococoa.ObjCObjectByReference;
import org.rococoa.contrib.NativeEnum;
import org.rococoa.Rococoa;
import org.rococoa.cocoa.foundation.NSArray;
import org.rococoa.cocoa.foundation.NSDictionary;
import org.rococoa.cocoa.foundation.NSError;
import org.rococoa.cocoa.foundation.NSMutableDictionary;
import org.rococoa.cocoa.foundation.NSNumber;
import org.rococoa.cocoa.foundation.NSObject;
import org.rococoa.cocoa.foundation.NSRange;
import org.rococoa.cocoa.foundation.NSString;
import org.rococoa.cocoa.foundation.NSUInteger;
import org.rococoa.cocoa.foundation.NSURL;
//import org.rococoa.RunOnMainThread;

/** Provides access to Cocoa NSSpeechSynthesizer.
 *  Methods have been wrapped to take and return natural Java types.
 *  Convenience methods and wrapper classes have been implemented for property and NSDictionary based datastructures.
 */
//@RunOnMainThread
public abstract class NSSpeechSynthesizer extends NSObject {
    /** Defines the properties associated with a speech synthesizer. Getters and setters have been provided for most of these,
     *  so using the properties directly will not usually be necessary.
     *  @see NSSpeechSynthesizer#getProperty(org.rococoa.contrib.appkit.NSSpeechSynthesizer.SpeechProperty)
     *  @see NSSpeechSynthesizer#setProperty(org.rococoa.contrib.appkit.NSSpeechSynthesizer.SpeechProperty, org.rococoa.NSObject)
     */
    public enum SpeechProperty implements NativeEnum<NSString> {
        StatusProperty,
        ErrorsProperty,
        InputModeProperty,
        CharacterModeProperty,
        NumberModeProperty,
        RateProperty,
        PitchBaseProperty,
        PitchModProperty,
        VolumeProperty,
        SynthesizerInfoProperty,
        RecentSyncProperty,
        PhonemeSymbolsProperty,
        CurrentVoiceProperty,
        CommandDelimiterProperty,
        ResetProperty,
        OutputToFileURLProperty;
        private final NSString value =  NSString.getGlobalString("NSSpeech" + name());
        public NSString getNativeValue() {
            return value;
        }

        @Override
        public String toString() {
            return super.toString() + " (" + getNativeValue() + ')';
        }

    }
    /**Represents the Objective C class for NSSpeechSynthesizer*/
    public static final _Class CLASS = Rococoa.createClass("NSSpeechSynthesizer", _Class.class);
    /** Synthesizers have an associated delegate. This is the Objective C delegate object acting as the delegate*/
    private ObjCObject delegateProxy = null;
    /** Synthesizers have an associated delegate. This is the Java object provided by the user of this class
        that allows Java code to receive delegate related callbacks.*/
    private NSSpeechSynthesizerDelegate delegate = null;

    /** Represents the Objective C class of the speech synthesizer*/
    public static abstract class _Class implements ObjCClass {
        public _Class() {}
        public abstract NSSpeechSynthesizer alloc();
        public abstract NSDictionary attributesForVoice(String voiceIdentifier);

        public abstract NSArray availableVoices();
        public abstract String defaultVoice();
        public abstract boolean isAnyApplicationSpeaking();
    }

    /** Interface to be implemented by Java objects that want to be informed about events reported to the speech syntheszier's delegate*/
    public interface NSSpeechSynthesizerDelegate {
        public void speechSynthesizer_didEncounterErrorAtIndex_ofString_message(NSSpeechSynthesizer sender, Integer characterIndex, String text, String errorMessage);
        public void speechSynthesizer_didEncounterSyncMessage(NSSpeechSynthesizer sender, String syncFlag);
        public void speechSynthesizer_didFinishSpeaking(NSSpeechSynthesizer sender, boolean success);
        public void speechSynthesizer_willSpeakPhoneme(NSSpeechSynthesizer sender, short phonemeOpcode);
        public void speechSynthesizer_willSpeakWord_ofString(NSSpeechSynthesizer sender, NSRange wordToSpeak, String text);
    }

    /** Construct a new synthesizer that speaks with a specified voice.
     *  @param voiceIdentifer the identifier of the voice to use
     *  @return the newly created synthesizer
     *  @throws IllegalArgumentException if the identifier is invalid or the voice indicated is not installed
     */
    public static NSSpeechSynthesizer synthesizerWithVoiceIdentifier(String voiceIdentifer) throws IllegalArgumentException {
        return CLASS.alloc().initWithVoice(voiceIdentifer);
    }

    /** Construct a new synthesizer that speaks with a specified voice.
     *  @param voice the voice to use, or null to use the defalt voice
     *  @return the newly created synthesizer
     *  @throws IllegalArgumentException if the voice is invalid or not installed
     */
    public static NSSpeechSynthesizer synthesizerWithVoice(NSVoice voice) {
        return synthesizerWithVoiceIdentifier(voice == null ? null : voice.getIdentifier());
    }

    /** Determine the default voice for this computer, set in system preferences
     *  @return the system default voice
     */
    public static NSVoice defaultVoice() {
        return new NSVoice(CLASS.attributesForVoice(CLASS.defaultVoice()));
    }

    /** Get a list of all available voices
     *  @return a list of available voices
     */
    public static List<NSVoice> availableVoices() {
        NSArray availableVoices = CLASS.availableVoices();
        List<NSVoice> result = new ArrayList<NSVoice>(availableVoices.count());
        for (int i=0; i < availableVoices.count(); i++) {
            result.add(new NSVoice(CLASS.attributesForVoice(availableVoices.objectAtIndex(i).toString())));
        }
        return result;
    }

    /** Determine whether any application is currently generating speech through
     *  the default output channel at present.
     *  @return true if the default speech output channel is in use
     */
    public static boolean isAnyApplicationSpeaking() {
        return CLASS.isAnyApplicationSpeaking();
    }

    /** Set the delegate that will receive events when speech is generated.
     *  @param delegate the delegate to set, replacing any existing one
     */
    public synchronized void setDelegate(final NSSpeechSynthesizerDelegate delegate) {
        if ( delegate != null ) {
            this.delegate = delegate;
            delegateProxy = Rococoa.proxy(delegate);
            setDelegate(this.delegateProxy.id());
        }
    }

    /** Get the current delegate
     *  @return the delegate
     */
    public synchronized NSSpeechSynthesizerDelegate getDelegate() {
        return delegate;
    }

    /** Convert a phoneme opcode to human readable form
     *  @param opcode the phoneme code to convert
     *  @return the corresponding readable string
     */
    public String opcodeToPhoneme(short opcode) {
        for(NSSpeechPhonemeInfo info : getPhonemeInfo()) {
            if (info.getOpcode() == opcode) {
                return info.getSymbol();
            }
        }
        return "?";
    }

    /** Create a sync point that can be embeded in speech, to trigger a
     *  synchronization callback.
     *  @param marker the marker number for the sync point
     *  @return a sync point marker that can be embeded in a string to be spoken
     */
    public static String createSyncPoint(int marker) {
        return String.format("[[sync 0x%h]]", marker);
    }

    /** Get the value of a synthesizer property.
     *  @param property the property whose value should be retrieved
     *  @return the value asociated with the property
     *  @throws IllegalArgumentException if an error occurs while reading the property
     */
    public NSObject getProperty(SpeechProperty property) throws IllegalArgumentException {
         ObjCObjectByReference errorPtr = new ObjCObjectByReference();
         NSObject result = objectForProperty_error(property.getNativeValue(), errorPtr);
         NSError error = errorPtr.getValueAs(NSError.class);
         //objectForProperty:error isn't well documented, so be very conservative
         if ( result != null && !result.id().isNull() && (error == null || error.id().isNull() || error.code().intValue() == 0) ) {
             return result;
         } else {
             throw new IllegalArgumentException("Could not get property: " + property + ", error: " + error.localizedDescription());
         }
    }

    /** Set the value of a synthesizer property.
     *  @param property the property whose value will be set
     *  @param value the value to set
     *  @throws IllegalArgumentException if an error occurs while setting the property
     */
    public void setProperty(SpeechProperty property, NSObject value) throws IllegalArgumentException {
        ObjCObjectByReference errorPtr = new ObjCObjectByReference();
        if ( !setObject_forProperty_error(value, property.getNativeValue(), errorPtr) ) {
            NSError error = errorPtr.getValueAs(NSError.class);
            throw new IllegalArgumentException("Could not set property: " + property + " to value " + value + ", error: " + error.localizedDescription());
        }
    }

    /** Add a speech dictionary to those in use with this synthesizer
     *  @param dictionary the dictionary to add
     */
    public void addSpeechDictionary(NSSpeechDictionary dictionary) {
        addSpeechDictionary(dictionary.getData());
    }


    /** Describes boundaries between speech units*/
    public static final class NSSpeechBoundary extends NSUInteger {
        private static final long serialVersionUID = 0;
        public static final NSSpeechBoundary ImmediateBoundary = new NSSpeechBoundary(0);
        public static final NSSpeechBoundary WordBoundary = new NSSpeechBoundary(1);
        public static final NSSpeechBoundary SentenceBoundary = new NSSpeechBoundary(2);
        public NSSpeechBoundary() {} //required by the plumbing
        private NSSpeechBoundary(int ordinal) {
            super(ordinal);
        }
    }

    /** Get the status of the synthesizer
     *  @return the current status of the synthesizer
     */
    public NSSpeechStatus getStatus() {
        return new NSSpeechStatus(Rococoa.cast(getProperty(SpeechProperty.StatusProperty), NSMutableDictionary.class));
    }

    //read-only
    /** Describes the current status of the synthesizer*/
    public static class NSSpeechStatus extends AbstractPropertyDictionary<NSSpeechStatus.StatusProperty> {
        public enum StatusProperty implements NativeEnum<NSString> {
            OutputBusy,
            OutputPaused,
            NumberOfCharactersLeft,
            PhonemeCode;
            private final NSString value =  NSString.getGlobalString(NSSpeechStatus.class.getSimpleName() + name());
            public NSString getNativeValue() {
                return value;
            }
        }

        public NSSpeechStatus() {
            super(StatusProperty.values().length);
        }

        public NSSpeechStatus(final NSMutableDictionary data) {
            super(data);
        }

        public boolean isOutputBusy() {
            return getBoolean(StatusProperty.OutputBusy);
        }

        public boolean isOutputPaused() {
            return getBoolean(StatusProperty.OutputPaused);
        }

        public int getNumberOfCharactersLeft() {
            return getInt(StatusProperty.NumberOfCharactersLeft);
        }

        public short getPhonemeCode() {
            return getShort(StatusProperty.PhonemeCode);
        }
    }

    /** Get the latest error that occurred in the synthesizer
     *  @return the latest error to occur
     */
    public NSSpeechError getError() {
        return new NSSpeechError(Rococoa.cast(getProperty(SpeechProperty.ErrorsProperty), NSMutableDictionary.class));
    }

    //read-only
    /** Describes an error that occurred*/
    public static class NSSpeechError extends AbstractPropertyDictionary<NSSpeechError.ErrorProperty> {
        public enum ErrorProperty implements NativeEnum<NSString> {
            Count,
            OldestCode,
            OldestCharacterOffset,
            NewestCode,
            NewestCharacterOffset;
            private final NSString value =  NSString.getGlobalString(NSSpeechError.class.getSimpleName() + name());
            public NSString getNativeValue() {
                return value;
            }
        }

        public NSSpeechError() {
            super(ErrorProperty.values().length);
        }

        public NSSpeechError(final NSMutableDictionary data) {
            super(data);
        }

        public int getErrorCount() {
            return getInt(ErrorProperty.Count);
        }

        public int getOldestCode() {
            return getInt(ErrorProperty.OldestCode);
        }
        public int getOldestCharacterOffset() {
            return getInt(ErrorProperty.OldestCharacterOffset);
        }
        public int getNewestCode() {
            return getInt(ErrorProperty.NewestCode);
        }
        public int getNewestCharacterOffset() {
            return getInt(ErrorProperty.NewestCharacterOffset);
        }

    }

    /** Describes the modes a synthesizer can operate in.*/
    public enum NSSpeechMode implements NativeEnum<NSString> {
        Text,
        Phoneme,
        Normal,
        Literal;
        private final NSString value =  NSString.getGlobalString(this.getClass().getSimpleName() + name());
        public NSString getNativeValue() {
            return value;
        }
    }

    /** Get the current input mode, whether the synthesizer is currently expecting regular text input or raw phonemes.
     *  @return either <code>NSSpeechMode.Text</code> or <code>NSSpeechMode.Phoneme</code> depending on the current mode
     */
    public NSSpeechMode getInputMode() {
         return NativeEnum.Resolver.fromNative(NSSpeechMode.class, getProperty(SpeechProperty.InputModeProperty));
    }

    /** Set the current input mode, whether the synthesizer is currently expecting regular text input or raw phonemes.
     *  @param mode either <code>NSSpeechMode.Text</code> or <code>NSSpeechMode.Phoneme</code> depending on the desired mode
     */
    public void setInputMode(NSSpeechMode mode) {
        setProperty(SpeechProperty.InputModeProperty, mode.getNativeValue());
    }

    /** Get the current character mode, whether the synthesizer is currently speaking strings normally or speaking each character literally.
     *  @return either <code>NSSpeechMode.Normal</code> or <code>NSSpeechMode.Literal</code> depending on the current mode
     */
    public NSSpeechMode getCharacterMode() {
        return NativeEnum.Resolver.fromNative(NSSpeechMode.class, getProperty(SpeechProperty.CharacterModeProperty));
    }

    /** Set the current character mode, whether the synthesizer is currently speaking strings normally or speaking each character literally.
     *  @param mode either <code>NSSpeechMode.Normal</code> or <code>NSSpeechMode.Literal</code> depending on the desired mode
     */
    public void setCharacterMode(NSSpeechMode mode) {
        setProperty(SpeechProperty.CharacterModeProperty, mode.getNativeValue());
    }

    /** Get the current number mode, whether the synthesizer is currently speaking numbers normally or speaking each digit literally.
     *  @return either <code>NSSpeechMode.Normal</code> or <code>NSSpeechMode.Literal</code> depending on the current mode
     */
    public NSSpeechMode getNumberMode() {
        return NativeEnum.Resolver.fromNative(NSSpeechMode.class, getProperty(SpeechProperty.NumberModeProperty));
    }

    /** Set the current number mode, whether the synthesizer is currently speaking numbers normally or speaking each digit literally.
     *  @param mode either <code>NSSpeechMode.Normal</code> or <code>NSSpeechMode.Literal</code> depending on the desired mode
     */
    public void setNumberMode(NSSpeechMode mode) {
        setProperty(SpeechProperty.NumberModeProperty, mode.getNativeValue());
    }

    /** Get identifying information about this synthesizer
     *  @return information that identifies this synthesizer
     */
    public NSSpeechSynthesizerInfo getSynthesizerInfo() {
        return new NSSpeechSynthesizerInfo(Rococoa.cast(getProperty(SpeechProperty.SynthesizerInfoProperty), NSMutableDictionary.class));
    }

    //read-only
    /** Describes identifying information about the synthesizer*/
    public static class NSSpeechSynthesizerInfo extends AbstractPropertyDictionary<NSSpeechSynthesizerInfo.SpeechSynthesizerInfoProperty> {
        public enum SpeechSynthesizerInfoProperty implements NativeEnum<NSString> {
            Identifier,
            Version;
            private final NSString value =  NSString.getGlobalString(NSSpeechSynthesizerInfo.class.getSimpleName() + name());
            public NSString getNativeValue() {
                return value;
            }
        }
        public NSSpeechSynthesizerInfo() {
            super(SpeechSynthesizerInfoProperty.values().length);
        }
        public NSSpeechSynthesizerInfo(final NSMutableDictionary data) {
            super(data);
        }
        public String getSynthesizerIdentifier() {
            return getString(SpeechSynthesizerInfoProperty.Identifier);
        }
        public String getSynthesizerVersion() {
            return getString(SpeechSynthesizerInfoProperty.Version);
        }
    }

    /** Get the baseline pitch for the synthesizer
     *  @return the baseline pitch
     */
    public float getPitchBase() {
        return Rococoa.cast(getProperty(SpeechProperty.PitchBaseProperty), NSNumber.class).floatValue();
    }

    /** Set the baseline pitch for the synthesizer
     *  @param baselinePitch the baseline pitch to use
     */
    public void setPitchBase(float baselinePitch) {
        setProperty(SpeechProperty.PitchBaseProperty, NSNumber.CLASS.numberWithFloat(baselinePitch));
    }

    /** Get the pitch modulation for the synthesizer
     *  @return the pitch modulation
     */
    public float getPitchMod() {
        return Rococoa.cast(getProperty(SpeechProperty.PitchModProperty), NSNumber.class).floatValue();
    }

    /** Set the pitch modulation for the synthesizer
     *  @param modulation the pitch modulation to use
     */
    public void setPitchMod(float modulation) {
        if ( modulation < 0.0f || modulation > 127.0f) {
            throw new IllegalArgumentException("Pitch modulation must be in the range 0.0 - 127.0");
        }
        setProperty(SpeechProperty.PitchModProperty, NSNumber.CLASS.numberWithFloat(modulation));
    }

    /** Get a list of phonemes the synthesizer uses
     *  @return information about the phonemes the synthesizer uses
     */
    public List<NSSpeechPhonemeInfo> getPhonemeInfo() {
        NSArray infos = Rococoa.cast(getProperty(SpeechProperty.PhonemeSymbolsProperty), NSArray.class);
        List<NSSpeechPhonemeInfo> result = new ArrayList<NSSpeechPhonemeInfo>(infos.count());
        for(int i=0; i < infos.count(); i++) {
            NSDictionary phonemeInfo = Rococoa.cast(infos.objectAtIndex(i), NSDictionary.class);
            result.add(new NSSpeechPhonemeInfo(NSMutableDictionary.dictionaryWithDictionary(phonemeInfo)));
        }
        return result;
    }

    //read-only
    /** Describes information about phonemes the synthesizer uses*/
    public static class NSSpeechPhonemeInfo extends AbstractPropertyDictionary<NSSpeechPhonemeInfo.PhonemeInfoProperty> {
        public enum PhonemeInfoProperty implements NativeEnum<NSString> {
            Opcode,
            Symbol,
            Example,
            HiliteStart,
            HiliteEnd;
            private final NSString value =  NSString.getGlobalString(NSSpeechPhonemeInfo.class.getSimpleName() + name());
            public NSString getNativeValue() {
                return value;
            }
        }

        public NSSpeechPhonemeInfo() {
            super(PhonemeInfoProperty.values().length);
        }

        public NSSpeechPhonemeInfo(final NSMutableDictionary data) {
            super(data);
        }

        public short getOpcode() {
            return getShort(PhonemeInfoProperty.Opcode);
        }

        public String getSymbol() {
            return getString(PhonemeInfoProperty.Symbol);
        }

        public String getExample() {
            return getString(PhonemeInfoProperty.Example);
        }

        public int getHiliteStart() {
            return getInt(PhonemeInfoProperty.HiliteStart);
        }

        public int getHiliteEnd() {
            return getInt(PhonemeInfoProperty.HiliteEnd);
        }

        @Override
        public String toString() {
            return getData().toString();
        }
    }

    /** Get the most recent sync point encountered when speaking
     *  @return the identifying number of the most recent sync point
     */
    public int getRecentSync() {
        return Rococoa.cast(getProperty(SpeechProperty.RecentSyncProperty), NSNumber.class).intValue();
    }

    /** Set the voice to use
     *  @param voice the new voice to use
     *  @return true if the voice change was successful
     */
    public boolean setVoice(NSVoice voice) {
        //CurrentVocieProperty basically takes a map with 2 keys, creator and id like an old school VoiceSpec spec (see SpeechSynthesis.h)
        //setProperty(SpeechProperty.CurrentVoiceProperty, dictionary of creator and id would go here );
        //this is much less painful - we'd have to call the C API, and why do it the hard way? If someone needs to, they can always
        //call setProperty() for themselves.
        return setVoice(voice.getIdentifier());
    }

    /** Get the current voice in use
     *  @return the voice in use
     */
    public NSVoice getVoice() {
        return new NSVoice(CLASS.attributesForVoice(voice()));
    }

    /** Set the command delimiter to use
     *  @param delimiters the delimiters to use when embedding commands in speech
     */
    public void setCommandDelimiter(NSSpeechCommand delimiters) {
        setProperty(SpeechProperty.CommandDelimiterProperty, delimiters.getData());
    }

    //write-only
    /** Desribes how to set the delimiters used to embed commands in text to be spoken*/
    public static class NSSpeechCommand extends AbstractPropertyDictionary<NSSpeechCommand.CommandDelimiterProperty> {
        public enum CommandDelimiterProperty implements NativeEnum<NSString>{
            Prefix,
            Suffix;
            private final NSString value =  NSString.getGlobalString(NSSpeechCommand.class.getSimpleName() + name());
            public NSString getNativeValue() {
                return value;
            }
        }

        public NSSpeechCommand() {
            super(CommandDelimiterProperty.values().length);
        }

        public NSSpeechCommand(final NSMutableDictionary data) {
            super(data);
        }

        public NSSpeechCommand(String prefix, String suffix) {
            this();
            setCommandPrefix(prefix);
            setCommandSuffix(suffix);
        }

        public String getCommandPrefix() {
            return getString(CommandDelimiterProperty.Prefix);
        }
        public void setCommandPrefix(String prefix) {
            setString(CommandDelimiterProperty.Prefix, prefix);
        }

        public String getCommandSuffix() {
            return getString(CommandDelimiterProperty.Suffix);
        }
        public void setCommandSuffix(String suffix) {
            setString(CommandDelimiterProperty.Suffix, suffix);
        }
    }

    /** Reset the synthesizer to the default settings*/
    public void reset() {
        setProperty(SpeechProperty.ResetProperty, null);
    }
    //pass null for computer speakers
    /** Set the synthesizer to send output to a file instead of the default output channel
     *  @param uri a file URI to send output to, pass null to switch back to the computer speakers
     */
    public void setOutputToFileURL(URI uri) {
        setProperty(SpeechProperty.OutputToFileURLProperty, uri != null ? NSURL.CLASS.URLWithString(uri.toString()) : null);
    }

    /** Speak the given string to a file
     *  @param text the text to speak
     *  @param uri a file URI indicating where to send output to
     *  @return true if the synthesis began successfully
     */
    public boolean startSpeakingStringToURL(String text, URI uri) {
        return startSpeakingString_toURL(NSString.stringWithString(text), NSURL.CLASS.URLWithString(uri.toString()));
    }

    //Those methods that will simply be called directly are public, the rest are 'package' - wrapper methods can be found above.
    abstract void addSpeechDictionary(NSDictionary speechDictionary);
    /** Resume speaking if output was paused*/
    public abstract void continueSpeaking();
    abstract ID delegate();
    abstract NSSpeechSynthesizer initWithVoice(String voiceIdentifier);
    /** Determine whether the current application is speaking using the default output channel
     *  @return true if the current application is speaking
     */
    public abstract boolean isSpeaking();
    abstract NSObject objectForProperty_error(NSString speechProperty, ObjCObjectByReference out_error);
    abstract boolean setObject_forProperty_error(NSObject object, NSString speechProperty, ObjCObjectByReference out_error);
    /** Pause speech at the indicated boundary
     *  @param boundary the place to stop speech
     */
    public abstract void pauseSpeakingAtBoundary(NSSpeechBoundary boundary);
    /** Convert text to phonemes.
     *  @param text the text to convert
     *  @return the corresponding phonemes
     */
    public abstract String phonemesFromText(String text);
    /** Get the current rate of speech
     *  @return the current rate of speech
     */
    public abstract float rate();
    abstract void setDelegate(ID delegate);
    /** Set the current rate of speech
     *  @param rate the rate to use
     */
    public abstract void setRate(float rate);
    /** Set whether the feedback window should be used
     *  @param useFeedbackWindow pass true to enable the feedback window
     */
    public abstract void setUsesFeedbackWindow(boolean useFeedbackWindow);
    /** Set the voice to use, should be called when the synthesizer is not currently speaking
     *  @param voiceIdentifier the identifier of the voice to use
     *  @return true if the voice change was successful
     */
    public abstract boolean setVoice(String voiceIdentifier);
    /** Set the volume
     *  @param volume the volume to use
     */
    public abstract void setVolume(float volume);
    /** Start speaking the given string through the default system speech channel
     *  @param text the text to speak
     *  @return true if synthesis starts successfully
     */
    public abstract boolean startSpeakingString(String text);
    abstract boolean startSpeakingString_toURL(NSString text, NSURL url);
    /** Stop this synthesizer from speaking*/
    public abstract void stopSpeaking();
    /** Stop this synthesizer from speaking when it reaches the indicated place.
     *  @param boundary the place to stop speaking
     */
    public abstract void stopSpeakingAtBoundary(NSSpeechBoundary boundary);
    /** Get whether the feedback window is enabled
     *  @return true if the feedback window is enabled
     */
    public abstract boolean usesFeedbackWindow();
    abstract String voice();
    /** Get the identifier of the current voice
     *  @return the identifier of the current voice
     */
    public String getVoiceIdentifier() {
        return voice();
    }
    abstract float volume();
    /** Get the current volume
     *  @return the current volume
     */
    public float getVolume() {
        return volume();
    }
}
