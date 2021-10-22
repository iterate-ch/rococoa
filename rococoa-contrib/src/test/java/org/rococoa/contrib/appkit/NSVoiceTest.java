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

import java.util.Locale;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.rococoa.cocoa.foundation.NSArray;
import org.rococoa.contrib.appkit.NSVoice.VoiceGender;
import org.rococoa.test.RococoaTestCase;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Simple tests for functionality of the voice class
 */
public class NSVoiceTest extends RococoaTestCase {

    @Test
    @Disabled
    @SuppressWarnings("deprecation")
    public void testAttributesForVoice() {
        NSVoice voice = new NSVoice(NSVoice.VICTORIA);
        assertEquals(35, voice.getAge());
        assertEquals("Isn't it nice to have a computer that will talk to you?", voice.getDemoText());
        assertEquals(VoiceGender.Female, voice.getGender());
        assertEquals(NSVoice.VICTORIA, voice.getIdentifier());
        assertEquals(Locale.US.toString(), voice.getLocaleIdentifier());
        assertEquals("en-US", voice.getLanguage()); //deprecated method, but we test it anyway
        assertEquals("Victoria", voice.getName());
        NSArray supportedChars = voice.getSupportedCharacters();
        assertNotNull(supportedChars);
        assertTrue(supportedChars.count() > 0);
        NSArray individuallySpokenChars = voice.getIndividuallySpokenCharacters();
        assertNotNull(individuallySpokenChars);
        assertTrue(individuallySpokenChars.count() > 0);
    }

    @Test
    public void testBadIdentifier() {
        String badId = "This voice does not exist";
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> {
            new NSVoice(badId);
        });
        assertTrue(e.getMessage().indexOf(badId) > 0);
    }
}
