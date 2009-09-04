/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sf.okapi.tm.pensieve.common;

import net.sf.okapi.common.resource.TextFragment;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author HaslamJD
 */
public class TranslationUnitValueTest {

    private final TextFragment content = new TextFragment();
    private final String lang = "fb";

    @Test
    public void constructorNoArg() {
        TranslationUnitValue tuv = new TranslationUnitValue();
        assertNull("text fragment should be null", tuv.getContent());
        assertNull("lang should be null", tuv.getLang());
    }

    @Test
    public void constructorTwoArgs() {

        TranslationUnitValue tuv = new TranslationUnitValue(lang, content);
        assertSame("content", content, tuv.getContent());
        assertSame("lang", lang, tuv.getLang());
    }

    @Test
    public void setContent() {
        TranslationUnitValue tuv = new TranslationUnitValue();
        tuv.setContent(content);
        assertSame("content", content, tuv.getContent());
    }

    @Test
    public void setLang() {
        TranslationUnitValue tuv = new TranslationUnitValue();
        tuv.setLang(lang);
        assertSame("content", lang, tuv.getLang());
    }
}
