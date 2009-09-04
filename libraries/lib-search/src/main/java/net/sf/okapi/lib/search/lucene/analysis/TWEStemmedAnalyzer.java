package net.sf.okapi.lib.search.lucene.analysis;
// this is where to change the code to allow using the SpanishBaseList ...
import java.io.Reader;
import java.util.HashMap;
import java.util.Locale;

import net.sf.okapi.lib.search.lucene.stemmers.EnglishListStemmer;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.PorterStemFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
/*
import org.apache.lucene.analysis.de.GermanStemFilter; // DWH 12-7-06
import org.apache.lucene.analysis.fr.FrenchStemFilter; // DWH 12-7-06
import org.apache.lucene.analysis.nl.DutchStemFilter; // DWH 12-7-06
import org.apache.lucene.analysis.ru.RussianStemFilter; // DWH 12-7-06
import org.apache.lucene.analysis.ru.RussianLowerCaseFilter; // DWH 12-7-06
*/
/**
 *
 * @author  HargraveJE
 */
public class TWEStemmedAnalyzer extends Analyzer
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
//      LocaleToTokenizerMap.put(new Locale("ru"), new AlphabeticNgramTokenizer(3, new Locale("ru")));
        // DWH 7-9-08 use regular word tokenizer for Russion so wildcards will work in searches
        
        // Armenian
        LocaleToTokenizerMap.put(new Locale("hy"), new AlphabeticNgramTokenizer(3, new Locale("hy")));
    }
    
    private Locale locale;
//  private Hashtable stopTable = new Hashtable();
    private EnglishListStemmer els; // DWH 3-3-06
    
    /** Creates a new instance of StemmedAnalyzer */
    public TWEStemmedAnalyzer(Locale p_locale)
    {
        this(p_locale, true);
        els = null; // DWH 12-4-06 just to be sure
    }
    
    /** Creates a new instance of StemmedAnalyzer */
    public TWEStemmedAnalyzer(Locale p_locale, boolean filterStopWords)
    {
        locale = p_locale;
        els = null; // DWH 12-4-06 just to be sure
//      if (filterStopWords && locale.equals(Locale.ENGLISH)) // DWH 3-23-07 added locale English
//          stopTable = StopFilter.makeStopTable(StopAnalyzer.ENGLISH_STOP_WORDS);
    }
    
    public TWEStemmedAnalyzer(Locale p_locale, boolean filterStopWords, EnglishListStemmer st)
    {
        locale = p_locale;
//      if (filterStopWords && locale.equals(Locale.ENGLISH)) // DWH 3-23-07 added locale English
//          stopTable = StopFilter.makeStopTable(StopAnalyzer.ENGLISH_STOP_WORDS);
        els = st; // DWH 3-3-06 only want one
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
        String lang;  
        TokenStream result = new LowerCaseFilter(p_tokenStream);
        if(els != null) // DWH 12-4-06
        {
            if (locale.equals(Locale.ENGLISH))
              result = new ListStemFilter(result,els,true); // DWH 3-3-06 true for English
            else
              result = new ListStemFilter(result,els,false); // DWH 12-7-06 false for non-English
//          return new StopFilter(result, stopTable);
        }           
        else if (locale.equals(Locale.ENGLISH)) // DWH 3-30-06 PorterStemFilter was blotted out
            result = new PorterStemFilter(result);
        else
        {
          lang = locale.getLanguage();
/*
          if (lang.equals("de"))
          {
            result = new GermanStemFilter(p_tokenStream);
            return result;
          }
          else if (lang.equals("fr"))
          {
            result = new FrenchStemFilter(p_tokenStream);
            return result;
          }
          else if (lang.equals("nl"))
          {
            result = new DutchStemFilter(p_tokenStream);
            return result;
          }
          else if (lang.equals("ru"))
          {
            result = new RussianStemFilter(new RussianLowerCaseFilter(p_tokenStream));
            return result;
          }
*/
          if (lang.equals("id"))
          {
            return result;
          }
          else if (lang.equals("sp"))
          {
            return result;
          }
        }       
//      return p_tokenStream;
        return result; // DWH 3-23-07 using the LowerCase Filter for non-Unicode is probably wrong ...
    }
/* DWH 12-4-06 commented out; see above version for multiple language stem filters
    private TokenStream applyFiltersAndStemmers(TokenStream p_tokenStream)
    {
      // English - lower case and stem with Porter Stemmer
        // don't filter stop words or punctuation
        if(locale.equals(Locale.ENGLISH))
        {
            TokenStream result = new LowerCaseFilter(p_tokenStream); 
            if (els==null) // DWH 3-30-06 PorterStemFilter was blotted out
              result = new PorterStemFilter(result);   
            else
              result = new EnglishListStemFilter(result,els); // DWH 3-3-06   
            return new StopFilter(result, stopTable);
        }           
        
        // Indonesian
        if(locale.equals(new Locale("id")))
        {
            TokenStream result = new LowerCaseFilter(p_tokenStream); 
            return result;          
        }           
        
        return p_tokenStream;
    }
*/
}
