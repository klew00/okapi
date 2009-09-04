/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.sf.okapi.tm.pensieve.tmx;

import net.sf.okapi.tm.pensieve.common.TranslationUnit;
import net.sf.okapi.tm.pensieve.writer.TMWriter;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Dax
 */
public class OkapiTMXHandlerTest {

    List<TranslationUnit> italian_tus;
    List<TranslationUnit> nonExistantLang_tus;
    private URI sampleTMX;
    TMXHandler handler;

    @Before
    public void setUp() throws URISyntaxException {
        sampleTMX = this.getClass().getResource("/sample_tmx.xml").toURI();
        handler = new OkapiTMXHandler(sampleTMX, "EN");
        italian_tus = handler.getTranslationUnitsFromTMX("IT");
        nonExistantLang_tus = handler.getTranslationUnitsFromTMX("FR");
    }

    @Test
    public void constructorNullFile() {
        String errMsg = null;
        try{
            new OkapiTMXHandler(null, "EN");
        }catch(IllegalArgumentException iae){
            errMsg = iae.getMessage();
        }
        assertEquals("Error message", "both uri and sourceLang must be set", errMsg);
    }

    @Test
    public void constructorEmptySourceLang() {
        String errMsg = null;
        try{
            new OkapiTMXHandler(sampleTMX, "");
        }catch(IllegalArgumentException iae){
            errMsg = iae.getMessage();
        }
        assertEquals("Error message", "both uri and sourceLang must be set", errMsg);
    }

    @Test(expected=IllegalArgumentException.class)
    public void getTranslationUnitsFromTMXEmptyTargetLang() {
        assertEquals("# found on empty target lang", 0, handler.getTranslationUnitsFromTMX("").size());
    }

    @Test(expected=IllegalArgumentException.class)
    public void getTranslationUnitsFromTMXNullTargetLang() {
        assertEquals("# found on empty target lang", 0, handler.getTranslationUnitsFromTMX(null).size());
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

    //An example of a Stub. I will likely change this to a Mock later
    @Test
    public void importTMXDocCount() throws IOException {
        StubTMWriter tmWriter = new StubTMWriter();

        handler.importTMX("EN", tmWriter);
        assertEquals("entries indexed", 2, tmWriter.tus.size());
    }

    public class StubTMWriter implements TMWriter{
        protected boolean endIndexCalled = false;
        protected List<TranslationUnit> tus = new ArrayList<TranslationUnit>();
        public void endIndex() throws IOException {

        }

        public void indexTranslationUnit(TranslationUnit tu) throws IOException {
            tus.add(tu);
        }
    }



}
