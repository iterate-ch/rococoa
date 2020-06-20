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

import org.rococoa.contrib.AbstractPropertyDictionary;
import org.rococoa.contrib.NativeEnum;

import org.rococoa.cocoa.foundation.NSArray;
import org.rococoa.cocoa.foundation.NSDictionary;
import org.rococoa.cocoa.foundation.NSMutableDictionary;
import org.rococoa.cocoa.foundation.NSString;
import static org.rococoa.contrib.appkit.NSVoice.VoiceProperty.*;

/** NSVoice encapsulates the properties of a speech synthesis voice, and can be
 *  used with NSSpeechSynthesizer to change the voice used to spreak.
 *  This class is a Rococoa enhancment designed to make it easier to work with
 *  voices, there is no actual class with this name in Cocoa.
 */
public class NSVoice extends AbstractPropertyDictionary<NSVoice.VoiceProperty> {
    //No way of knowing which voices a user has installed, so just expose the known ids
    public static final String AGNES = "com.apple.speech.synthesis.voice.Agnes";
    public static final String ALBERT = "com.apple.speech.synthesis.voice.Albert";
    public static final String BAD_NEWS = "com.apple.speech.synthesis.voice.BadNews";
    public static final String BAHH = "com.apple.speech.synthesis.voice.Bahh";
    public static final String BELLS = "com.apple.speech.synthesis.voice.Bells";
    public static final String BOING = "com.apple.speech.synthesis.voice.Boing";
    public static final String BRUCE = "com.apple.speech.synthesis.voice.Bruce";
    public static final String BUBBLES = "com.apple.speech.synthesis.voice.Bubbles";
    public static final String CELLOS = "com.apple.speech.synthesis.voice.Cellos";
    public static final String DERANGED = "com.apple.speech.synthesis.voice.Deranged";
    public static final String FRED = "com.apple.speech.synthesis.voice.Fred";
    public static final String GOOD_NEWS = "com.apple.speech.synthesis.voice.GoodNews";
    public static final String HYSTERICAL = "com.apple.speech.synthesis.voice.Hysterical";
    public static final String JUNIOR = "com.apple.speech.synthesis.voice.Junior";
    public static final String KATHY = "com.apple.speech.synthesis.voice.Kathy";
    public static final String ORGAN = "com.apple.speech.synthesis.voice.Organ";
    public static final String PRINCESS = "com.apple.speech.synthesis.voice.Princess";
    public static final String RALPH = "com.apple.speech.synthesis.voice.Ralph";
    public static final String TRINOIND = "com.apple.speech.synthesis.voice.Trinoids";
    public static final String VICKI = "com.apple.speech.synthesis.voice.Vicki";
    public static final String VICTORIA = "com.apple.speech.synthesis.voice.Victoria";
    public static final String WHISPHER = "com.apple.speech.synthesis.voice.Whisper";
    public static final String ZARVOX = "com.apple.speech.synthesis.voice.Zarvox";

    /** Defines the properties of a voice*/
    public enum VoiceProperty implements NativeEnum<NSString> {
        Name,
        Identifier,
        Age,
        Gender,
        DemoText,
        LocaleIdentifier,
        SupportedCharacters,
        IndividuallySpokenCharacters,
        Language;
        private final NSString value =  NSString.getGlobalString(NSVoice.class.getSimpleName() + name());
        public NSString getNativeValue() {
            return value;
        }
    }

    /** Construct a new voice using the given identifier
     *  @param voiceIdentifier the voice to lookup
     *  @throws IllegalArgumentException if the voice identified by <code>voiceIdentifier</code> is not installed
     */
    public NSVoice (String voiceIdentifier) throws IllegalArgumentException {
        super(NSMutableDictionary.dictionaryWithDictionary(
              checkData(voiceIdentifier, NSSpeechSynthesizer.CLASS.attributesForVoice(voiceIdentifier))));
    }

    NSVoice() {
        super(VoiceProperty.values().length);
    }
    
    NSVoice(final NSMutableDictionary data) {
        super(checkData(null, data));
    }
    
    NSVoice(NSDictionary data) {
        super(checkData(null, data));
    }    

    private static NSDictionary checkData(String identifier, NSDictionary data) {
        if ( data == null ) {
            throw new IllegalArgumentException("Invalid voice data" +
                    identifier == null ? "." : ", unknown identifier: " + identifier );
        } else {
            return data;
        }
    }

    public String getName() {
        return getString(Name);
    }

    public String getIdentifier() {
        return getString(Identifier);
    }

    public int getAge() {
        return getInt(Age);
    }

    public VoiceGender getGender() {
        return getEnum(VoiceGender.class, Gender);
    }

    public String getDemoText() {
        return getString(DemoText);
    }

    public String getLocaleIdentifier() {
        return getString(LocaleIdentifier);
    }

    public NSArray getSupportedCharacters() {
        return getValueAsType(SupportedCharacters, NSArray.class);
    }

    public NSArray getIndividuallySpokenCharacters() {
        return getValueAsType(IndividuallySpokenCharacters, NSArray.class);
    }

    @Deprecated
    public String getLanguage() {
        return getString(Language);
    }

    @Override
    public String toString() {
        return "[NSVoice: " + getData() + ']';
    }

    @Override
    public boolean equals(Object obj) {
        if ( obj instanceof NSVoice ) {
            return getIdentifier().equals(((NSVoice)obj).getIdentifier());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return getIdentifier().hashCode();
    }

    /** An enumeration representing possible genders for voices*/
    public enum VoiceGender implements NativeEnum<NSString> {
        Neuter,
        Male,
        Female;
        private final NSString value =  NSString.getGlobalString("NS" + getClass().getSimpleName() + name());
        public NSString getNativeValue() {
            return value;
        }
    }

}
