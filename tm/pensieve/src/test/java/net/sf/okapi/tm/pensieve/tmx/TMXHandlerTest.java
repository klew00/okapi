/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.sf.okapi.tm.pensieve.tmx;

import net.sf.okapi.common.resource.TextFragment;
import net.sf.okapi.common.resource.TextUnit;
import net.sf.okapi.tm.pensieve.common.TranslationUnit;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

/**
 *
 * @author Dax
 */
public class TMXHandlerTest {

    List<TranslationUnit> italian_tus;
    List<TranslationUnit> nonExistantLang_tus;
    private final String sampleTMX = "/sample_tmx.xml";
    @Before
    public void setUp() {
        italian_tus = TMXHandler.getTranslationUnitsFromTMX(sampleTMX, "EN", "IT");
        nonExistantLang_tus = TMXHandler.getTranslationUnitsFromTMX(sampleTMX, "EN", "FR");
    }

    @Test(expected=NullPointerException.class)
    public void nonExistantFile() {
        TMXHandler.getTranslationUnitsFromTMX("/filethathasnochanceofexisting.xml", "EN", "FR");
    }

    @Test(expected=NullPointerException.class)
    public void EmptySourceLang() {
        TMXHandler.getTranslationUnitsFromTMX(sampleTMX, "", "FR");
    }

    @Test(expected=NullPointerException.class)
    public void NullSourceLang() {
        TMXHandler.getTranslationUnitsFromTMX(sampleTMX, null, "FR");
    }

    @Test(expected=NullPointerException.class)
    public void EmptyTargetLang() {
        TMXHandler.getTranslationUnitsFromTMX(sampleTMX, "EN", "");
    }

    @Test(expected=NullPointerException.class)
    public void NullTargetLang() {
        TMXHandler.getTranslationUnitsFromTMX(sampleTMX, "EN", null);
    }

    @Test
    public void TUCount_ExistingLang() {
        assertEquals("number of TUs", 2, italian_tus.size());
    }

    @Test
    public void TUCount_NonExistingLang() {
        assertEquals("number of TUs", 2, nonExistantLang_tus.size());
    }
    
    @Test
    public void SourceAndTargetForExistingLang() {
        assertEquals("first match source", "hello", italian_tus.get(0).getSource().getContent().toString());
        assertEquals("first match target", "ciao", italian_tus.get(0).getTarget().getContent().toString());
    }

    @Test
    public void SourceAndTargetForNonExistingLang() {
        assertEquals("first match source", "hello",
                nonExistantLang_tus.get(0).getSource().getContent().toString());
        assertNull("target for non-existant language should be null",
                nonExistantLang_tus.get(0).getTarget().getContent());
    }

    @Test
    public void convertTranslationUnit(){
        TextUnit textUnit = new TextUnit("someId", "some great text");
        textUnit.setTargetContent("kr", new TextFragment("some great text in Korean"));
        TranslationUnit tu = TMXHandler.convertTranslationUnit("en", "kr", textUnit);
        assertEquals("sourceLang", "en", tu.getSource().getLang());
        assertEquals("source content", "some great text", tu.getSource().getContent().toString());
        assertEquals("targetLang", "kr", tu.getTarget().getLang());
        assertEquals("target content", "some great text in Korean", tu.getTarget().getContent().toString());
    }

}
