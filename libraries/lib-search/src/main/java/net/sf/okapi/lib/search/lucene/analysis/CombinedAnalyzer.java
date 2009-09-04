package net.sf.okapi.lib.search.lucene.analysis;

import java.util.Locale;

import net.sf.okapi.lib.search.lucene.stemmers.EnglishListStemmer;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.PerFieldAnalyzerWrapper;
import org.apache.lucene.analysis.WhitespaceAnalyzer;
/**
 *
 * @author  HargraveJE
 */
public class CombinedAnalyzer extends PerFieldAnalyzerWrapper 
{    
    private Analyzer sourceAnalyzer; // DWH 5-29-06
    private Analyzer targetAnalyzer; // DWH 5-29-06
    /** Creates a new instance of combinedAnalyzer */
    public CombinedAnalyzer(Locale p_source, Locale p_target)
    {
        this(p_source, p_target, true);       
    }
    
    /** Creates a new instance of combinedAnalyzer */
    public CombinedAnalyzer(Locale p_source, Locale p_target, boolean filterStopWords)
    {
        super(new WhitespaceAnalyzer());
        
        // create analyzer for each field
        sourceAnalyzer = new TWEStemmedAnalyzer(p_source, filterStopWords); // DWH 5-29-06
        addAnalyzer("s", sourceAnalyzer); // DWH 5-29-06
        targetAnalyzer = new TWEStemmedAnalyzer(p_target, filterStopWords); // DWH 5-29-06
        addAnalyzer("t", targetAnalyzer); // DWH 5-29-06
        addAnalyzer("es", new ExactAnalyzer(p_source));
        addAnalyzer("et", new ExactAnalyzer(p_target));
        addAnalyzer("fs", new FuzzyAnalyzer(p_source));
        addAnalyzer("ft", new FuzzyAnalyzer(p_target));
        addAnalyzer("c", new ExactAnalyzer(Locale.US)); // DWH 3-21-06 for comments
        addAnalyzer("id", new ExactAnalyzer(Locale.US)); // DWH 3-27-06 to search on citation
        addAnalyzer("n", new ExactAnalyzer(Locale.US)); // DWH 3-30-06 original document number
        addAnalyzer("zs", new ExactAnalyzer(p_source)); // DWH 10-28-08
        addAnalyzer("zt", new ExactAnalyzer(p_target)); // DWH 10-28-08
    }
    
    public CombinedAnalyzer(Locale p_source, Locale p_target, boolean filterStopWords, EnglishListStemmer els)
    {
        super(new WhitespaceAnalyzer());
        
        // create analyzer for each field
        sourceAnalyzer = new TWEStemmedAnalyzer(p_source, filterStopWords, els); // DWH 5-29-06
        addAnalyzer("s", sourceAnalyzer); // DWH 5-29-06
        targetAnalyzer = new TWEStemmedAnalyzer(p_target, filterStopWords, null); // DWH 3-23-07 no stemmer for target for now
        addAnalyzer("t", targetAnalyzer); // DWH 5-29-06
        addAnalyzer("es", new ExactAnalyzer(p_source));
        addAnalyzer("et", new ExactAnalyzer(p_target));
        addAnalyzer("fs", new FuzzyAnalyzer(p_source));
        addAnalyzer("ft", new FuzzyAnalyzer(p_target));
//      addAnalyzer("c", new ExactAnalyzer(Locale.US)); // DWH 3-21-06 for comments
        addAnalyzer("id", new ExactAnalyzer(Locale.US)); // DWH 3-27-06 to search on citation
        addAnalyzer("n", new ExactAnalyzer(Locale.US)); // DWH 3-30-06 original document number
//      addAnalyzer("zs", new ExactAnalyzer(p_source)); // DWH 10-28-08 for source with tag codes
//      addAnalyzer("zt", new ExactAnalyzer(p_target)); // DWH 10-28-08 for target with tag codes
    }
    /**
     */
    public String[] getFields(boolean p_isSource)
    {
        if (p_isSource)
        {
//          return new String[]{"s", "es", "fs", "c"}; // DWH 3-21-06 added c for comments
            return new String[]{"s", "es", "fs", "zs", "id", "n"};
              // DWH 9-13-06 was getting field -1 on id 10-28-08 added zs
        }
        
        return new String[]{"t", "et", "ft", "zt"}; // DWH 10-28-08 added zt
    }
    public Analyzer getSourceAnalyzer()
    {
      return sourceAnalyzer;
    }
    public Analyzer getTargetAnalyzer()
    {
      return targetAnalyzer;
    }
}
