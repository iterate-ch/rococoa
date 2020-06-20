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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.rococoa.contrib.NativeEnum;
import org.rococoa.Rococoa;

import org.rococoa.cocoa.foundation.NSMutableArray;
import org.rococoa.cocoa.foundation.NSMutableDictionary;
import org.rococoa.cocoa.foundation.NSString;
import static org.rococoa.contrib.appkit.NSSpeechDictionary.SpeechDictionaryProperty.*;

/** NSVoice encapsulates the properties of a speech synthesis dictionary, and can be
 *  used with NSSpeechSynthesizer to change the way words are pronounced.
 *  Dictionaries contain lists of entries that define how a given word or abbreviation
 *  should be pronounced.
 *  This class is a Rococoa enhancment designed to make it easier to work with
 *  speech dictionaries, there is no actual class with this name in Cocoa.
 */
public class NSSpeechDictionary extends AbstractPropertyDictionary<NSSpeechDictionary.SpeechDictionaryProperty> {
    /** Used to parse Locale strings, to convert Locales between Cocoa and Java*/
    private static final Pattern LOCALE_SPLITTER = Pattern.compile("([a-z]{2})_([A-Z]{2})_?([a-zA-Z1-9]*)");
    /** Defines the properties of a speech dictionary*/
    public enum SpeechDictionaryProperty implements NativeEnum<NSString> {
        LocaleIdentifier, 
        ModificationDate, 
        Pronunciations, 
        Abbreviations;
        private final NSString value =  NSString.getGlobalString(NSSpeechDictionary.class.getSimpleName() + name());
        public NSString getNativeValue() {
            return value;
        }
    }

    /** Construct a new empty speech dictionary*/
    public NSSpeechDictionary() {
        super(SpeechDictionaryProperty.values().length);
    }

    /** Construct a new SpecchDictionary from existing data
     *  @param data used to initialize the speech dictionary, must contain valid keys and values for a speech dictionary
     */
    public NSSpeechDictionary(final NSMutableDictionary data) {
        super(data);
    }

    /** Get the Locale associated with this dictionary
     *  @return the dictionary's Locale
     */
    public Locale getLocaleIdentifier() {
        String locale = getString(LocaleIdentifier);
        Matcher m = LOCALE_SPLITTER.matcher(locale);
        if ( m.matches() ) {
            if ( m.group(2) == null ) {
                throw new IllegalStateException("Could not parse locale: " + locale + " not enough fields.");
            }
            return m.group(3) != null ? new Locale(m.group(1), m.group(2), m.group(3)) : new Locale(m.group(1), m.group(2));
        } else {
            throw new IllegalStateException("Could not parse locale, does not follow expected format: " + locale);
        }
    }

    public void setLocaleIdentifier(Locale localeIdentifier) {
        setString(LocaleIdentifier, localeIdentifier.toString());
    }

    public Date getModificationDate() {
        return getDate(ModificationDate);
    }

    public void setModificationDate(Date modificationDate) {
        setDate(ModificationDate, modificationDate);
    }

    public List<Entry> getPronunciations() {
        NSMutableArray pronounciations = getValueAsType(Pronunciations, NSMutableArray.class);
        List<Entry> result = new ArrayList<Entry>(pronounciations.count());
        for (int i=0; i < pronounciations.count(); i++) {
            result.add(new Entry(Rococoa.cast(pronounciations.objectAtIndex(i), NSMutableDictionary.class)));
        }
        return Collections.unmodifiableList(result);
    }

    public void setPronunciations(List<Entry> pronounciations) {
        NSMutableArray pronounciationDicts = NSMutableArray.CLASS.arrayWithCapacity(pronounciations.size());
        for (Entry e : pronounciations) {
            pronounciationDicts.addObject(e.getData());
        }
        setValue(Pronunciations, pronounciationDicts);
    }
    
    public void addPronounciation(Entry pronounciation) {
        NSMutableArray pronounciations = getValueAsType(Pronunciations, NSMutableArray.class);
        if ( pronounciations == null ) {
            pronounciations = NSMutableArray.CLASS.arrayWithCapacity(1);
        }
        pronounciations.addObject(pronounciation.getData());
        setValue(Pronunciations, pronounciations);
    }

    public List<Entry> getAbbreviations() {
        NSMutableArray abbreviations = getValueAsType(Abbreviations, NSMutableArray.class);
        List<Entry> result = new ArrayList<Entry>(abbreviations.count());
        for (int i=0; i < abbreviations.count(); i++) {
            result.add(new Entry(Rococoa.cast(abbreviations.objectAtIndex(i), NSMutableDictionary.class)));
        }
        return Collections.unmodifiableList(result);
    }

    public void setAbbreviations(List<Entry> abbreviations) {
        NSMutableArray abbreviationDicts = NSMutableArray.CLASS.arrayWithCapacity(abbreviations.size());
        for (Entry e : abbreviations) {
            abbreviationDicts.addObject(e.getData());
        }
        setValue(Abbreviations, abbreviationDicts);
    }

    public void addAbbreviation(Entry abbreviation) {
        NSMutableArray abbreviations = getValueAsType(Abbreviations, NSMutableArray.class);
        if ( abbreviations == null ) {
            abbreviations = NSMutableArray.CLASS.arrayWithCapacity(1);
        }
        abbreviations.addObject(abbreviation.getData());
        setValue(Abbreviations, abbreviations);
    }
    
    @Override
    public String toString() {
        return "[NSSpeechDictionary: " + getData() + ']';
    }

    /** Represents a dictionary entry.
     *  Maps a spelling of a word to the associated phonemes that should be used to pronounce it.
     */
    public static class Entry extends AbstractPropertyDictionary<Entry.DictionaryEntryProperty> {
        /**The properties associated with a dictionary entry*/
        public enum DictionaryEntryProperty implements NativeEnum<NSString>{
            Spelling, 
            Phonemes;
            private final NSString value =  NSString.getGlobalString(NSSpeechDictionary.class.getSimpleName() + "Entry" + name());
            public NSString getNativeValue() {
                return value;
            }
        }
        public Entry() {
            super(DictionaryEntryProperty.values().length);
        }
        public Entry(final NSMutableDictionary data) {
            super(data);
        }
        public Entry(final String spelling, final String phonemes) {
            this(NSMutableDictionary.dictionaryWithObjectsAndKeys(
                    NSString.stringWithString(phonemes), DictionaryEntryProperty.Phonemes.getNativeValue(),
                    NSString.stringWithString(spelling), DictionaryEntryProperty.Spelling.getNativeValue(),
                    null)
                );
        }
        public String getSpelling() {
            return getString(DictionaryEntryProperty.Spelling);
        }

        public void setSpelling(String entrySpelling) {
            setString(DictionaryEntryProperty.Spelling, entrySpelling);
        }

        public String getPhonemes() {
            return getString(DictionaryEntryProperty.Phonemes);
        }

        public void setPhonemes(String entryPhonemes) {
            setString(DictionaryEntryProperty.Phonemes, entryPhonemes);
        }
    }
}
