/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.sf.okapi.tm.pensieve.tmx;

import java.util.List;
import net.sf.okapi.tm.pensieve.common.MetaDataTypes;
import net.sf.okapi.tm.pensieve.common.TranslationUnit;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

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
        assertEquals("first match source", "hello", italian_tus.get(0).getSource().toString());
        assertEquals("first match target", "ciao", italian_tus.get(0).getTarget().toString());
    }

    @Test
    public void MetaDataForExistingLang() {
        assertEquals("first match source_lang", "EN",
                italian_tus.get(0).getMetadata().get(MetaDataTypes.SOURCE_LANG));
        assertEquals("first match target", "IT",
                italian_tus.get(0).getMetadata().get(MetaDataTypes.TARGET_LANG));
    }

    @Test
    public void SourceAndTargetForNonExistingLang() {
        assertEquals("first match source", "hello",
                nonExistantLang_tus.get(0).getSource().toString());
        assertNull("target for non-existant language should be null",
                nonExistantLang_tus.get(0).getTarget());
    }
}
