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
import java.util.Locale;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;

/**
 *
 * @author  HargraveJE
 */
public final class ExactAnalyzer extends Analyzer
{    
    
    private static HashMap LocaleToTokenizerMap;
    
    static
    {               
        LocaleToTokenizerMap = new HashMap(5);        
        
        // English
        LocaleToTokenizerMap.put(Locale.ENGLISH, new WordTokenizer());  
        
        // Traditional and Simplified Chinese
        LocaleToTokenizerMap.put(Locale.CHINESE, new ChineseNgramTokenizer(1));
        LocaleToTokenizerMap.put(new Locale("zh", "", "withSpaces"), new ChineseSpaceTokenizer());  
    }
    
    private Locale locale;
      
    /** Creates a new instance of ExactAnalysis */
    public ExactAnalyzer(Locale p_locale)
    {
        locale = p_locale;        
    }
    
    public TokenStream tokenStream(String p_fieldName, Reader p_reader)
    {        
        return getTokenizer(p_reader);
    }
    
    private TokenStream getTokenizer(Reader p_reader)
    {                       
        TokenStream tok = (Tokenizer)LocaleToTokenizerMap.get(locale);
        if (tok == null) //use default tokenizer
        {
            tok =  new WhitespaceTokenizer(null);                
        }       
        ((LdsTokenizer)tok).setReader(p_reader); 
        
        if(locale.equals(Locale.ENGLISH) || locale.equals(new Locale("id")))
        {
            tok = new LowerCaseFilter(tok);             
        }
                
        return tok;
    }            
}
