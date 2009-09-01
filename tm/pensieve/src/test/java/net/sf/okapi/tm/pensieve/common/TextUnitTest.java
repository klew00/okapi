package net.sf.okapi.tm.pensieve.common;

import net.sf.okapi.tm.pensieve.common.TranslationUnit;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 * User: Christian Hargraves
 * Date: Aug 19, 2009
 * Time: 6:49:59 AM
 */
public class TextUnitTest {

    TranslationUnit tu;
    final static String AUTHOR = "Joe McMac";
    final static String CONTENT = "Some content that isn't very long";

    @Test
    public void constructor_allParamsPassed(){
        tu = new TranslationUnit(AUTHOR, CONTENT);
        assertEquals("author", AUTHOR, tu.getAuthor());
        assertEquals("content", CONTENT, tu.getContent());
    }

}
