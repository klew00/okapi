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
public class TranslationUnitVariantTest {

    private final TextFragment content = new TextFragment();
    private final String lang = "fb";

    @Test
    public void constructorNoArg() {
        TranslationUnitVariant tuv = new TranslationUnitVariant();
        assertNull("text fragment should be null", tuv.getContent());
        assertNull("lang should be null", tuv.getLang());
    }

    @Test
    public void constructorTwoArgs() {

        TranslationUnitVariant tuv = new TranslationUnitVariant(lang, content);
        assertSame("content", content, tuv.getContent());
        assertSame("lang", lang, tuv.getLang());
    }

    @Test
    public void setContent() {
        TranslationUnitVariant tuv = new TranslationUnitVariant();
        tuv.setContent(content);
        assertSame("content", content, tuv.getContent());
    }

    @Test
    public void setLang() {
        TranslationUnitVariant tuv = new TranslationUnitVariant();
        tuv.setLang(lang);
        assertSame("content", lang, tuv.getLang());
    }
}
