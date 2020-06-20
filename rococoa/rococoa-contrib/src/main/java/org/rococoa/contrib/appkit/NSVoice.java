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

import org.rococoa.cocoa.foundation.NSArray;
import org.rococoa.cocoa.foundation.NSDictionary;
import org.rococoa.cocoa.foundation.NSMutableDictionary;
import org.rococoa.cocoa.foundation.NSString;
import org.rococoa.contrib.AbstractPropertyDictionary;
import org.rococoa.contrib.NativeEnum;

import static org.rococoa.contrib.appkit.NSVoice.VoiceProperty.Age;
import static org.rococoa.contrib.appkit.NSVoice.VoiceProperty.DemoText;
import static org.rococoa.contrib.appkit.NSVoice.VoiceProperty.Gender;
import static org.rococoa.contrib.appkit.NSVoice.VoiceProperty.Identifier;
import static org.rococoa.contrib.appkit.NSVoice.VoiceProperty.IndividuallySpokenCharacters;
import static org.rococoa.contrib.appkit.NSVoice.VoiceProperty.Language;
import static org.rococoa.contrib.appkit.NSVoice.VoiceProperty.LocaleIdentifier;
import static org.rococoa.contrib.appkit.NSVoice.VoiceProperty.Name;
import static org.rococoa.contrib.appkit.NSVoice.VoiceProperty.SupportedCharacters;

/** NSVoice encapsulates the properties of a speech synthesis voice, and can be
 *  used with NSSpeechSynthesizer to change the voice used to spreak.
 *  This class is a Rococoa enhancment designed to make it easier to work with
 *  voices, there is no actual class with this name in Cocoa.
 */
public class NSVoice extends AbstractPropertyDictionary<NSVoice.VoiceProperty> {
    //No way of knowing which voices a user has installed, so just expose the known ids
    public static final String ALEX = "com.apple.speech.synthesis.voice.Alex";
    public static final String ALICE = "com.apple.speech.synthesis.voice.alice";
    public static final String ALVA = "com.apple.speech.synthesis.voice.alva";
    public static final String AMELIE = "com.apple.speech.synthesis.voice.amelie";
    public static final String ANNA = "com.apple.speech.synthesis.voice.anna";
    public static final String CARMIT = "com.apple.speech.synthesis.voice.carmit";
    public static final String DAMAYANTI = "com.apple.speech.synthesis.voice.damayanti";
    public static final String DANIEL = "com.apple.speech.synthesis.voice.daniel";
    public static final String DIEGO = "com.apple.speech.synthesis.voice.diego";
    public static final String ELLEN = "com.apple.speech.synthesis.voice.ellen";
    public static final String FIONA = "com.apple.speech.synthesis.voice.fiona";
    public static final String FRED = "com.apple.speech.synthesis.voice.Fred";
    public static final String IOANA = "com.apple.speech.synthesis.voice.ioana";
    public static final String JOANA = "com.apple.speech.synthesis.voice.joana";
    public static final String JORGE = "com.apple.speech.synthesis.voice.jorge";
    public static final String JUAN = "com.apple.speech.synthesis.voice.juan";
    public static final String KANYA = "com.apple.speech.synthesis.voice.kanya";
    public static final String KAREN = "com.apple.speech.synthesis.voice.karen";
    public static final String KYOKO = "com.apple.speech.synthesis.voice.kyoko.premium";
    public static final String LAURA = "com.apple.speech.synthesis.voice.laura";
    public static final String LEKHA = "com.apple.speech.synthesis.voice.lekha";
    public static final String LUCA = "com.apple.speech.synthesis.voice.luca";
    public static final String LUCIANA = "com.apple.speech.synthesis.voice.luciana";
    public static final String MAGED = "com.apple.speech.synthesis.voice.maged";
    public static final String MARISKA = "com.apple.speech.synthesis.voice.mariska";
    public static final String MEI_JIA = "com.apple.speech.synthesis.voice.mei-jia";
    public static final String MELINA = "com.apple.speech.synthesis.voice.melina";
    public static final String MILENA = "com.apple.speech.synthesis.voice.milena";
    public static final String MOIRA = "com.apple.speech.synthesis.voice.moira";
    public static final String MONICA = "com.apple.speech.synthesis.voice.monica";
    public static final String NORA = "com.apple.speech.synthesis.voice.nora";
    public static final String PAULINA = "com.apple.speech.synthesis.voice.paulina";
    public static final String RISHI = "com.apple.speech.synthesis.voice.rishi";
    public static final String SAMANTHA = "com.apple.speech.synthesis.voice.samantha";
    public static final String SARA = "com.apple.speech.synthesis.voice.sara";
    public static final String SATU = "com.apple.speech.synthesis.voice.satu";
    public static final String SIN_JI = "com.apple.speech.synthesis.voice.sin-ji";
    public static final String TESSA = "com.apple.speech.synthesis.voice.tessa";
    public static final String THOMAS = "com.apple.speech.synthesis.voice.thomas";
    public static final String TING_TING = "com.apple.speech.synthesis.voice.ting-ting";
    public static final String VEENA = "com.apple.speech.synthesis.voice.veena";
    public static final String VICTORIA = "com.apple.speech.synthesis.voice.Victoria";
    public static final String XANDER = "com.apple.speech.synthesis.voice.xander";
    public static final String YELDA = "com.apple.speech.synthesis.voice.yelda";
    public static final String YUNA = "com.apple.speech.synthesis.voice.yuna";
    public static final String YURI = "com.apple.speech.synthesis.voice.yuri";
    public static final String ZOSIA = "com.apple.speech.synthesis.voice.zosia";
    public static final String ZUZANA = "com.apple.speech.synthesis.voice.zuzana";

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
        if (data == null || data.count() == 0) {
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
        if (obj instanceof NSVoice) {
            return getIdentifier().equals(((NSVoice) obj).getIdentifier());
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
