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
//package org.ldschurch.trl.jwe;
//EnglishListStemFilter is used in TWEStemmedAnalyzer
/**
 * Copyright 2004 The Apache Software Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or lied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

import java.io.IOException;

import net.sf.okapi.lib.search.lucene.stemmers.EnglishListStemmer;
import net.sf.okapi.lib.search.lucene.stemmers.PorterRockwellStemmer;

import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
//import org.apache.lucene.analysis.*;
//import org.ldschurch.trl.lucene.analysis.*;
/** Transforms the token stream as per the Porter stemming algorithm.
    Note: the input to the stemming filter must already be in lower case,
    so you will need to use LowerCaseFilter or LowerCaseTokenizer farther
    down the Tokenizer chain in order for this to work properly!
    <P>
    To use this filter with other analyzers, you'll want to write an
    Analyzer class that sets up the TokenStream chain as you want it.
    To use this with LowerCaseTokenizer, for example, you'd write an
    analyzer like this:
    <P>
    <PRE>
    class MyAnalyzer extends Analyzer {
      public final TokenStream tokenStream(String fieldName, Reader reader) {
        return new PorterStemFilter(new LowerCaseTokenizer(reader));
      }
    }
    </PRE>
*/
public final class EnglishListStemFilter extends TokenFilter {
  private PorterRockwellStemmer stemmer;
  private EnglishListStemmer steamer;

  public EnglishListStemFilter(TokenStream in, EnglishListStemmer st) {
    super(in);
    stemmer = new PorterRockwellStemmer();
    steamer = st;
  }

  /** Returns the next input Token, after being stemmed */
  public final org.apache.lucene.analysis.Token next() throws IOException {
      org.apache.lucene.analysis.Token token = input.next();
    if (token == null)
      return null;
    else {
      String s = steamer.getBaseForm(token.termText()); // DWH 3-3-06
      if (s.equals("")) // DWH 3-14-06 didn't find it, so use Porter Stemmer
      {
        stemmer.stem(token.termText()); // DWH 3-3-06 default to Porter Stemmer
        if (s != token.termText()) // Yes, I mean object reference comparison here
          token.setTermText(s);       
      }
      else if (s != token.termText()) // Yes, I mean object reference comparison here
        token.setTermText(s);
      return token;
    }
  }
}


