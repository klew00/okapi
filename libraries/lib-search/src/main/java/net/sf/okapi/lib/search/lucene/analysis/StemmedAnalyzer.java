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

import java.io.Reader;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Locale;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.PorterStemFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;

/**
 *
 * @author  HargraveJE
 */
public class StemmedAnalyzer extends Analyzer
{
    private static HashMap LocaleToTokenizerMap;
    
    static
    {               
        LocaleToTokenizerMap = new HashMap(5);        
        
        // We have no stemmer for complex script languages - use ComplexTextNgramTokenizer
        LocaleToTokenizerMap.put(new Locale("th"), new ComplexTextNgramTokenizer(3, new Locale("th")));
        LocaleToTokenizerMap.put(new Locale("lo"), new ComplexTextNgramTokenizer(3, new Locale("lo")));         
        LocaleToTokenizerMap.put(new Locale("km"), new ComplexTextNgramTokenizer(3, new Locale("km")));
        
        // Traditional and Simplified Chinese        
        LocaleToTokenizerMap.put(Locale.CHINESE, new ChineseNgramTokenizer(1));
        LocaleToTokenizerMap.put(new Locale("zh", "", "withSpaces"), new ChineseSpaceTokenizer());         
        
        // Sinhalese
        LocaleToTokenizerMap.put(new Locale("si"), new AlphabeticNgramTokenizer(3, new Locale("si")));     
        
        // Russian
        LocaleToTokenizerMap.put(new Locale("ru"), new AlphabeticNgramTokenizer(3, new Locale("ru")));
        
        // Armenian
        LocaleToTokenizerMap.put(new Locale("hy"), new AlphabeticNgramTokenizer(3, new Locale("hy")));
    }
    
    private Locale locale;
    private Hashtable stopTable = new Hashtable();
    
    /** Creates a new instance of StemmedAnalyzer */
    public StemmedAnalyzer(Locale p_locale)
    {
        this(p_locale, true);        
    }
    
    /** Creates a new instance of StemmedAnalyzer */
    public StemmedAnalyzer(Locale p_locale, boolean filterStopWords)
    {
        locale = p_locale;
//      if (filterStopWords)
//    		stopTable = StopFilter.makeStopTable(StopAnalyzer.ENGLISH_STOP_WORDS);
    }
    
    public TokenStream tokenStream(String p_fieldName, Reader p_reader)
    {        
        return getTokenizer(p_reader);
    }  
    
    private TokenStream getTokenizer(Reader p_reader)
    {    
        Tokenizer tok = (Tokenizer)LocaleToTokenizerMap.get(locale);
        if (tok == null)
        {
            tok = new WordTokenizer();
        }
        ((LdsTokenizer)tok).setReader(p_reader);        
        
        return applyFiltersAndStemmers(tok);        
    }            
    
    private TokenStream applyFiltersAndStemmers(TokenStream p_tokenStream)
    {
        // English - lower case and stem with Porter Stemmer
        // don't filter stop words or punctuation
        if(locale.equals(Locale.ENGLISH))
        {
            TokenStream result = new LowerCaseFilter(p_tokenStream); 
            result = new PorterStemFilter(result);
            return result; // DWH 3-28-07
//          return new StopFilter(result, stopTable); DWH 3-28-07 deprecated
        }           
        
        // Indonesian
        if(locale.equals(new Locale("id")))
        {
            TokenStream result = new LowerCaseFilter(p_tokenStream); 
            return result;          
        }           
        
        return p_tokenStream;
    }
}
