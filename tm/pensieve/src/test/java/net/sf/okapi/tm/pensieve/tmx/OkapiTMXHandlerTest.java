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
public class OkapiTMXHandlerTest {

    List<TranslationUnit> italian_tus;
    List<TranslationUnit> nonExistantLang_tus;
    private final String sampleTMX = "/sample_tmx.xml";
    TMXHandler handler;

    @Before
    public void setUp() {
        handler = new OkapiTMXHandler(sampleTMX, "EN");
        italian_tus = handler.getTranslationUnitsFromTMX("IT");
        nonExistantLang_tus = handler.getTranslationUnitsFromTMX("FR");
    }

    public void constructorNullFile() {
        String errMsg = null;
        try{
            new OkapiTMXHandler("", "EN");
        }catch(IllegalArgumentException iae){
            errMsg = iae.getMessage();
        }
        assertEquals("Error message", "both filename and sourceLang must be set", errMsg);
    }

    public void constructorEmptySourceLang() {
        String errMsg = null;
        try{
            new OkapiTMXHandler(sampleTMX, "");
        }catch(IllegalArgumentException iae){
            errMsg = iae.getMessage();
        }
        assertEquals("Error message", "both filename and sourceLang must be set", errMsg);
    }

    public void constructornonExistantFile() {
        String errMsg = null;
        try{
            new OkapiTMXHandler("/filethathasnochanceofexisting.xml", "EN");
        }catch(IllegalArgumentException iae){
            errMsg = iae.getMessage();
        }
        assertEquals("Error message", "/filethathasnochanceofexisting.xml was not found!", errMsg);
    }

    public void getTranslationUnitsFromTMXEmptyTargetLang() {
        assertEquals("# found on empty target lang", 0, handler.getTranslationUnitsFromTMX("").size());
    }

    public void getTranslationUnitsFromTMXNullTargetLang() {
        assertEquals("# found on empty target lang", 0, handler.getTranslationUnitsFromTMX("").size());
    }

    @Test
    public void tUCount_ExistingLang() {
        assertEquals("number of TUs", 2, italian_tus.size());
    }

    @Test
    public void tUCount_NonExistingLang() {
        assertEquals("number of TUs", 2, nonExistantLang_tus.size());
    }
    
    @Test
    public void sourceAndTargetForExistingLang() {
        assertEquals("first match source", "hello", italian_tus.get(0).getSource().getContent().toString());
        assertEquals("first match target", "ciao", italian_tus.get(0).getTarget().getContent().toString());
    }

    @Test
    public void sourceAndTargetForNonExistingLang() {
        assertEquals("first match source", "hello",
                nonExistantLang_tus.get(0).getSource().getContent().toString());
        assertNull("target for non-existant language should be null",
                nonExistantLang_tus.get(0).getTarget().getContent());
    }

    @Test
    public void convertTranslationUnit(){
        TextUnit textUnit = new TextUnit("someId", "some great text");
        textUnit.setTargetContent("kr", new TextFragment("some great text in Korean"));
        TranslationUnit tu = OkapiTMXHandler.convertTranslationUnit("en", "kr", textUnit);
        assertEquals("sourceLang", "en", tu.getSource().getLang());
        assertEquals("source content", "some great text", tu.getSource().getContent().toString());
        assertEquals("targetLang", "kr", tu.getTarget().getLang());
        assertEquals("target content", "some great text in Korean", tu.getTarget().getContent().toString());
    }

}
