/*===========================================================================
Copyright (C) 2008-2009 by the Okapi Framework contributors
-----------------------------------------------------------------------------
This library is free software; you can redistribute it and/or modify it
under the terms of the GNU Lesser General Public License as published by
the Free Software Foundation; either version 2.1 of the License, or (at
your option) any later version.

This library is distributed in the hope that it will be useful, but
WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser
General Public License for more details.

You should have received a copy of the GNU Lesser General Public License
along with this library; if not, write to the Free Software Foundation,
Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA

See also the full LGPL text here: http://www.gnu.org/copyleft/lesser.html
===========================================================================*/

package net.sf.okapi.lib.search.lucene.analysis;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.Reader;
import java.io.StringReader;
import java.util.Locale;

import net.sf.okapi.lib.search.Helper;

import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.TypeAttribute;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author HaslamJD
 */
public class AlphabeticNgramTokenizerTest {

    AlphabeticNgramTokenizer ngramTk;

    @Before
    public void setUp() {
        Reader r = new StringReader("123456");
        ngramTk = new AlphabeticNgramTokenizer(r, 5, null);
    }

    @Test
    public void Constructor() throws Exception {
        Reader r = new StringReader("123456");
        ngramTk = new AlphabeticNgramTokenizer(r, 5, Locale.CANADA);
        assertEquals("ngram length", 5, ngramTk.getNgramLength());
        assertEquals("locale", Locale.CANADA, ngramTk.getLocale());
        assertNotNull("Term Attribute should initialized", Helper.getPrivateMember(ngramTk, "termAttribute"));
        assertNotNull("Offset Attribute should initialized", Helper.getPrivateMember(ngramTk, "offsetAttribute"));
        assertNotNull("Type Attribute should initialized", Helper.getPrivateMember(ngramTk, "typeAttribute"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void InvalidNGramLengthConstructor() {
        ngramTk = new AlphabeticNgramTokenizer(null, 0, Locale.CANADA);
    }

    @Test(expected = IllegalArgumentException.class)
    public void NullReaderConstructor() {
        ngramTk = new AlphabeticNgramTokenizer(null, 10, Locale.CANADA);
    }

    @Test
    public void IncrementToken() throws Exception {
        assertTrue("should have first token", ngramTk.incrementToken());
        assertTrue("should have second token", ngramTk.incrementToken());
        assertTrue("should have third token", ngramTk.incrementToken());
        assertFalse("should not have fourth token", ngramTk.incrementToken());
    }

    @Test
    public void IncrementTokenTermValue() throws Exception {
        ngramTk.incrementToken();
        assertEquals("First Token Value", "12345", getTermString());
        ngramTk.incrementToken();
        assertEquals("Second Token Value", "23456", getTermString());
        ngramTk.incrementToken();
        assertEquals("ThirdToken Value", "3456", getTermString());
        ngramTk.incrementToken();
        assertEquals("non-existent Token Value", "", getTermString());
    }

    @Test
    public void IncrementTokenTypeValue() throws Exception {
        ngramTk.incrementToken();
        assertEquals("First Token Type", "ngram(5)", getTypeString());
        
        ngramTk.incrementToken();
        assertEquals("Second Token Type", "ngram(5)", getTypeString());
        
        ngramTk.incrementToken();
        assertEquals("Third Token Type", "ngram(5)", getTypeString());
        
        ngramTk.incrementToken();
        assertEquals("non-existent Token Type (default)", "word", getTypeString());
    }

    @Test
    public void IncrementTokenOffsetValue() throws Exception {
        ngramTk.incrementToken();
        assertEquals("First Token Offset Start", 0, getOffsetStart());
        assertEquals("First Token Offset End", 5, getOffsetEnd());
        ngramTk.incrementToken();
        assertEquals("First Token Offset Start", 1, getOffsetStart());
        assertEquals("First Token Offset End", 6, getOffsetEnd());
        ngramTk.incrementToken();       
        assertEquals("First Token Offset Start", 2, getOffsetStart());
        assertEquals("First Token Offset End", 6, getOffsetEnd());
        ngramTk.incrementToken();
        assertEquals("non-existent Token Offset Start", 0, getOffsetStart());
        assertEquals("non-existent Token Offset End", 0, getOffsetEnd());
    }

    @Test
    public void IncrementTokenTypeValueNoLocaleNoLowerCase() throws Exception {
        Reader r = new StringReader("THIS SHOULD NOT BE LOWERCASE");
        ngramTk = new AlphabeticNgramTokenizer(r, 27, null);
        ngramTk.incrementToken();
        assertEquals("First Token Value", "THIS SHOULD NOT BE LOWERCAS", getTermString());
    }

    @Test
    public void IncrementTokenTypeValueNonArmenianLocaleLowerCase() throws Exception {
        Reader r = new StringReader("THIS SHOULD ALL BE LOWERCASE");
        ngramTk = new AlphabeticNgramTokenizer(r, 27, Locale.CANADA);
        ngramTk.incrementToken();
        assertEquals("First Token Value", "this should all be lowercas", getTermString());
    }

    // we now attempt to lowercase every language and let icu4j decide what to do @Test
    public void IncrementTokenTypeValueArmenianLocaleNoLowerCase() throws Exception {
        Reader r = new StringReader("THIS SHOULD NOT BE LOWERCASE");
        ngramTk = new AlphabeticNgramTokenizer(r, 27, new Locale("hy"));
        ngramTk.incrementToken();
        assertEquals("First Token Value", "THIS SHOULD NOT BE LOWERCAS", getTermString());
        r = new StringReader("THIS SHOULD NOT BE LOWERCASE");
        ngramTk = new AlphabeticNgramTokenizer(r, 27, new Locale("si"));
        ngramTk.incrementToken();
        assertEquals("First Token Value", "THIS SHOULD NOT BE LOWERCAS", getTermString());
    }

    @Test
    public void ResetNewReader() throws Exception {
        ngramTk.incrementToken();
        ngramTk.incrementToken();
        Reader r = new StringReader("Holy Reset Batman");
        ngramTk.reset(r);
        ngramTk.incrementToken();
        assertEquals("First Token Value", "Holy ", getTermString());
        assertEquals("First Token Offset Start", 0, getOffsetStart());
        assertEquals("First Token Offset End", 5, getOffsetEnd());
    }

    private String getTermString() throws Exception {
        return ((CharTermAttribute) Helper.getPrivateMember(ngramTk, "termAttribute")).toString();
    }

    private String getTypeString() throws Exception {
        return ((TypeAttribute) Helper.getPrivateMember(ngramTk, "typeAttribute")).type();
    }

    private int getOffsetStart() throws Exception {
        return ((OffsetAttribute) Helper.getPrivateMember(ngramTk, "offsetAttribute")).startOffset();
    }

    private int getOffsetEnd() throws Exception {
        return ((OffsetAttribute) Helper.getPrivateMember(ngramTk, "offsetAttribute")).endOffset();
    }
}
