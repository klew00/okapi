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

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sf.okapi.tm.pensieve.analyzers;

import java.io.Reader;
import java.util.Locale;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;

/**
 *
 * @author HaslamJD
 */
public class NgramAnalyzer extends Analyzer {

    private Locale locale;
    private int ngramLength;

    public NgramAnalyzer(Locale locale, int ngramLength) {
        if (ngramLength <= 0) {
            throw new IllegalArgumentException("'ngramLength' cannot be less than 1");
        }
        this.locale = locale;
        this.ngramLength = ngramLength;
    }

    @Override
    public TokenStream tokenStream(String fieldName, Reader reader) {
        return new AlphabeticNgramTokenizer(reader, ngramLength, locale);
    }
}
