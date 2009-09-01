package net.sf.okapi.tm.pensieve.seeker;

import net.sf.okapi.tm.pensieve.writer.ExactMatchWriter;
import net.sf.okapi.tm.pensieve.common.TranslationUnit;
import net.sf.okapi.tm.pensieve.common.TranslationUnitFields;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Before;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.util.List;
import net.sf.okapi.common.resource.TextFragment;

/**
 * User: Christian Hargraves
 * Date: Aug 17, 2009
 * Time: 1:04:24 PM
 */
public class TMSeekerTest {

    static final Directory DIR = new RAMDirectory();
    static final TextFragment TARGET = new TextFragment("target text");

    TMSeeker seeker;

    @Before
    public void setUp() throws FileNotFoundException {
        seeker = new TMSeeker(DIR);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorNullIndexDir() {
        new TMSeeker(null);
    }

    @Test
    public void searchForWordsNothingFound() throws Exception {
        ExactMatchWriter writer = getWriter();
        writer.endIndex();
        List<TranslationUnit> docs = seeker.searchForWords(TranslationUnitFields.SOURCE, "anonexistentwordthatshouldnowayeverexist", 10);
        assertNotNull("docs returned should not be null", docs);
        assertEquals("number of docs found", 0, docs.size());
    }

    @Test
    public void searchForWordsOverMaxDocs() throws Exception {
        ExactMatchWriter writer = getWriter();

        populateIndex(writer, 12, "patents are evil", "unittest");
        final int desiredReturns = 2;
        writer.endIndex();
        List<TranslationUnit> docs = seeker.searchForWords(TranslationUnitFields.SOURCE, "patents", desiredReturns);
        assertEquals("number of docs found", desiredReturns, docs.size());
    }

    @Test
    public void searchForWordsUnderMaxDocs() throws Exception {
        ExactMatchWriter writer = getWriter();

        final int desiredReturns = 8;

        populateIndex(writer, desiredReturns, "patents are evil", "unittest");
        writer.endIndex();

        List<TranslationUnit> docs = seeker.searchForWords(TranslationUnitFields.SOURCE, "patents", 10);
        assertEquals("number of docs found", desiredReturns, docs.size());
    }

    @Test(expected = RuntimeException.class)
    public void searchWordsInvalidQuery() throws Exception {
        seeker.searchForWords(TranslationUnitFields.SOURCE, "patents evil are]", 10);
    }

    @Test
    public void searchWordsMultipleSubPhrases() throws Exception {
        ExactMatchWriter writer = getWriter();


        writer.indexTranslationUnit(new TranslationUnit(new TextFragment("patents are evil"),TARGET));
        writer.indexTranslationUnit(new TranslationUnit(new TextFragment("patents evil are"),TARGET));
        writer.indexTranslationUnit(new TranslationUnit(new TextFragment("are patents evil"),TARGET));
        writer.indexTranslationUnit(new TranslationUnit(new TextFragment("completely unrelated phrase"),TARGET));
        writer.endIndex();

        List<TranslationUnit> docs = seeker.searchForWords(TranslationUnitFields.SOURCE, "\"patents evil\"", 10);
        assertEquals("number of docs found", 2, docs.size());
    }

    @Test
    public void searchExactSingleMatch() throws Exception {
        ExactMatchWriter writer = getWriter();
        String str = "watch out for the killer rabbit";

        final int numOfIndices = 18;

        populateIndex(writer, numOfIndices, str, "two");

        writer.endIndex();
        List<TranslationUnit> docs = seeker.searchExact(TranslationUnitFields.SOURCE_EXACT, str+1, 10);
        assertEquals("number of docs found", 1, docs.size());
    }

    @Test
    public void searchExactMultipleMatches() throws Exception {
        ExactMatchWriter writer = getWriter();
        String str = "watch out for the killer rabbit";
        for(int i = 0; i < 5; i++){
            writer.indexTranslationUnit(new TranslationUnit(new TextFragment(str), TARGET));
        }

        writer.endIndex();
        List<TranslationUnit> docs = seeker.searchExact(TranslationUnitFields.SOURCE_EXACT, str, 10);
        assertEquals("number of docs found", 5, docs.size());
    }

    @Test
    public void searchExactDifferentStopWords() throws Exception {
        ExactMatchWriter writer = getWriter();
        String str = "watch out for the killer rabbit";
        writer.indexTranslationUnit(new TranslationUnit(new TextFragment(str), TARGET));
        writer.indexTranslationUnit(new TranslationUnit(new TextFragment("watch out for the the killer rabbit"), TARGET));

        writer.endIndex();
        List<TranslationUnit> docs = seeker.searchExact(TranslationUnitFields.SOURCE_EXACT, str, 10);
        assertEquals("number of docs found", 1, docs.size());
    }

    @Test
    public void searchExactDifferentOrder() throws Exception {
        ExactMatchWriter writer = getWriter();
        String str = "watch out for the killer rabbit";
        writer.indexTranslationUnit(new TranslationUnit(new TextFragment(str), TARGET));
        writer.indexTranslationUnit(new TranslationUnit(new TextFragment("watch out for the the killer rabbit"), TARGET));

        writer.endIndex();
        List<TranslationUnit> docs = seeker.searchExact(TranslationUnitFields.SOURCE_EXACT, "killer rabbit the for out watch", 10);
        assertEquals("number of docs found", 0, docs.size());
    }

    @Test
    public void getTextUnit() throws Exception {
        String source = "watch out for the killer rabbit";
        String target = "j";
        Document doc = new Document();
        doc.add(new Field(TranslationUnitFields.SOURCE_EXACT.name(), source,
                Field.Store.NO, Field.Index.ANALYZED));
        doc.add(new Field(TranslationUnitFields.TARGET.name(), target,
                Field.Store.NO, Field.Index.NOT_ANALYZED));
        TranslationUnit tu = seeker.getTranslationUnit(doc);
        assertEquals("source field", source, tu.getSource().getCodedText());
        assertEquals("target field", target, tu.getTarget().getCodedText());
    }

    ExactMatchWriter getWriter() throws Exception {
        return new ExactMatchWriter(DIR);
    }

    void populateIndex(ExactMatchWriter writer, int numOfEntries, String source, String target) throws Exception {

        for (int i=0; i<numOfEntries; i++) {
            writer.indexTranslationUnit(new TranslationUnit(new TextFragment(source + i), new TextFragment(target)));
        }
        writer.indexTranslationUnit(new TranslationUnit(new TextFragment("something that in no way should ever match"), new TextFragment("unittesttarget")));
    }
}
