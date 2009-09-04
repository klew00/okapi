package net.sf.okapi.tm.pensieve.common;

import net.sf.okapi.common.resource.TextFragment;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 * User: Christian Hargraves
 * Date: Aug 19, 2009
 * Time: 6:49:59 AM
 */
public class TranslationUnitTest {

    TranslationUnit tu;
    final static TextFragment SOURCE = new TextFragment("Joe McMac");
    final static TextFragment CONTENT = new TextFragment("Some content that isn't very long");

    @Test
    public void noArgConstructor(){
        tu = new TranslationUnit();
        assertNull("source", tu.getSource());
        assertNull("content", tu.getTarget());
        assertEquals("metadata entries", 0, tu.getMetadata().size());
    }

    @Test
    public void constructor_allParamsPassed(){
        tu = new TranslationUnit(SOURCE, CONTENT);
        assertEquals("source", SOURCE, tu.getSource());
        assertEquals("content", CONTENT, tu.getTarget());
        assertEquals("metadata entries", 0, tu.getMetadata().size());
    }

    @Test
    public void metaDataSetter(){
        tu = new TranslationUnit(SOURCE, CONTENT);
        MetaData md = new MetaData();
        tu.setMetadata(md);
        assertSame("metadata should be the same", md, tu.getMetadata());
    }

    @Test
    public void isSourceEmptyNull(){
        tu = new TranslationUnit();
        assertTrue("source should be empty", tu.isSourceEmpty());
    }

    @Test
    public void isSourceEmptyEmpty(){
        tu = new TranslationUnit();
        tu.setSource(new TextFragment(""));
        assertTrue("source should be empty", tu.isSourceEmpty());
    }

    @Test
    public void isSourceEmptyNotEmpty(){
        tu = new TranslationUnit();
        tu.setSource(new TextFragment("this is not empty"));
        assertFalse("source should not be empty", tu.isSourceEmpty());
    }

    @Test
    public void isTargetEmptyNull(){
        tu = new TranslationUnit();
        assertTrue("target should be empty", tu.isTargetEmpty());
    }

    @Test
    public void isTargetEmptyEmpty(){
        tu = new TranslationUnit();
        tu.setTarget(new TextFragment(""));
        assertTrue("target should be empty", tu.isTargetEmpty());
    }

    @Test
    public void isTargetEmptyNotEmpty(){
        tu = new TranslationUnit();
        tu.setTarget(new TextFragment("this is not empty"));
        assertFalse("target should not be empty", tu.isTargetEmpty());
    }


}
