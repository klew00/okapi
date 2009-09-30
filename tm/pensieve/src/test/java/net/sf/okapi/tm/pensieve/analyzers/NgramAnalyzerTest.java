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

package net.sf.okapi.tm.pensieve.analyzers;


import java.io.Reader;
import java.io.StringReader;
import java.util.Locale;
import net.sf.okapi.tm.pensieve.Helper;
import org.apache.lucene.analysis.TokenStream;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author HaslamJD
 */
public class NgramAnalyzerTest {

    @Test
    public void testConstructor() throws Exception {
        NgramAnalyzer nga = new NgramAnalyzer(Locale.CANADA, 5);
        assertEquals("Locale", Locale.CANADA, (Locale) Helper.getPrivateMember(nga, "locale"));
        assertEquals("Ngram length", 5, (int) (Integer) Helper.getPrivateMember(nga, "ngramLength"));
    }

    @Test(expected=IllegalArgumentException.class)
    public void testConstructorInvalidNgramLength() throws Exception {
        NgramAnalyzer nga = new NgramAnalyzer(Locale.CANADA, 0);
    }

    @Test
    public void getTokenizer() throws Exception {
        NgramAnalyzer nga = new NgramAnalyzer(Locale.CANADA, 5);

        Reader r = new StringReader("Blah!");
        TokenStream ts = nga.tokenStream("fieldName", r);
        assertEquals("locale of tokenstream", Locale.CANADA, ((AlphabeticNgramTokenizer)ts).getLocale());
        assertEquals("ngram length", 5, ((AlphabeticNgramTokenizer)ts).getNgramLength());
        assertTrue("Valid reader and token", ts.incrementToken());
    }

}
