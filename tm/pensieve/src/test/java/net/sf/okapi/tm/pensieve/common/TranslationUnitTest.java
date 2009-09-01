package net.sf.okapi.tm.pensieve.common;

import net.sf.okapi.common.resource.TextFragment;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 * User: Christian Hargraves
 * Date: Aug 19, 2009
 * Time: 6:49:59 AM
 */
public class TranslationUnitTest {

    TranslationUnit tu;
    final static TextFragment SOURCE = new TextFragment("Joe McMac");
    final static String CONTENT = "Some content that isn't very long";

    @Test
    public void constructor_allParamsPassed(){
        tu = new TranslationUnit(SOURCE, CONTENT);
        assertEquals("source", SOURCE, tu.getSource().getCodedText());
        assertEquals("content", CONTENT, tu.getTarget());
    }

}
