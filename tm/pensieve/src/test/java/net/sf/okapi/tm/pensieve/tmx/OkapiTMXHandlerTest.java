/*===========================================================================
Copyright (C) 2008-2009 by the Okapi Framework contributors
-----------------------------------------------------------------------------
This library is free software; you can redistribute it and/or modify it
under the terms of the GNU Lesser General Public License as published by
the Free Software Foundation; either version 2.1 of the License, or (at
your option) any later version.

This library is distributed in the hope that it will be useful, but
WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser
General Public License for more details.

You should have received a copy of the GNU Lesser General Public License
along with this library; if not, write to the Free Software Foundation,
Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA

See also the full LGPL text here: http://www.gnu.org/copyleft/lesser.html
===========================================================================*/

package net.sf.okapi.tm.pensieve.tmx;

import java.util.Map;
import net.sf.okapi.tm.pensieve.common.TMHit;
import net.sf.okapi.tm.pensieve.common.TranslationUnit;
import net.sf.okapi.tm.pensieve.writer.TMWriter;
import net.sf.okapi.common.filters.AbstractFilter;
import net.sf.okapi.common.resource.RawDocument;
import net.sf.okapi.common.resource.TextUnit;
import net.sf.okapi.common.resource.TextFragment;
import net.sf.okapi.common.Event;
import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.EventType;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
import net.sf.okapi.common.filterwriter.TMXWriter;
import net.sf.okapi.common.resource.Property;
import net.sf.okapi.tm.pensieve.Helper;
import net.sf.okapi.tm.pensieve.common.MetadataType;
import net.sf.okapi.tm.pensieve.seeker.TMSeeker;

/**
 *
 * @author Dax
 */
public class OkapiTMXHandlerTest {

    private URI sampleTMX;
    OkapiTMXHandler handler;
    StubTMWriter stubTmWriter;
    StubTMSeeker stubTmSeeker;
    StubTMXFilter stubTmxFilter;
    StubTmxWriter stubTmxWriter;

    @Before
    public void setUp() throws URISyntaxException {

        String[][] properties = {{"tuid", "helloid"},
            {"datatype", "plaintext"},
            {"Txt::FileName", "StringInfoForTest3.info"},
            {"Txt::GroupName", "APCCalibrateTimeoutAction0"}
        };

        stubTmxFilter = new StubTMXFilter();
        stubTmxFilter.addEvent("1", "hello", "ciao", "IT", properties);
        stubTmxFilter.addEvent("2", "world", "mondo", "IT", null);
        stubTmxFilter.events.add(new Event(EventType.DOCUMENT_PART, new TextUnit("holy cow")));

        sampleTMX = new URI("test.tmx");
        handler = new OkapiTMXHandler("EN", stubTmxFilter);
        stubTmWriter = new StubTMWriter();
        stubTmSeeker = new StubTMSeeker();
        stubTmSeeker.addTU("source", "EN", "target", "FR", "sourceid");
        stubTmSeeker.addTU("source2", "EN", "target2", "FR", "sourceid2");
        stubTmxWriter = new StubTmxWriter();
    }

    @Test
    public void exportTmxStepsCalled() throws IOException {
        handler.exportTmx(sampleTMX, stubTmSeeker, stubTmxWriter);
        assertEquals("tmx path", sampleTMX.getPath(), stubTmxWriter.path);
        assertTrue("doc started", stubTmxWriter.startWritten);
        //TODO: find out about lang - assertEquals("sourceLang", "EN", stubTmxWriter.sourceLanguage);
        //TODO: find out about lang - assertEquals("targetLang", "IT", stubTmxWriter.sourceLanguage);
        assertEquals("creationTool", "pensieve", stubTmxWriter.creationTool);
        assertEquals("creationToolVersion", "0.0.1", stubTmxWriter.creationToolVersion);
        assertEquals("segType", "sentence", stubTmxWriter.segType);
        assertEquals("originalTMFormat", "pensieve", stubTmxWriter.originalTMFormat);
        assertEquals("dataType", "unknown", stubTmxWriter.dataType);
        assertEquals("number of tus", 2, stubTmxWriter.textUnits.size());
        assertEquals("source of first tu written", "source", stubTmxWriter.textUnits.get(0).getSourceContent().toString());
        assertEquals("target of first tu written", "target", stubTmxWriter.textUnits.get(0).getTargetContent("FR").toString());

        assertEquals("attributes of first tu written", "sourceid", stubTmxWriter.attributes.get(0).get(MetadataType.ID.fieldName()));
        //TODO: Verify Content
        assertTrue("endDocument written", stubTmxWriter.endWritten);
        assertTrue("writer closed", stubTmxWriter.closed);
    }

    @Test
    public void exportTmxFileNull() throws IOException {
        String errMsg = null;
        try {
            handler.exportTmx(null, stubTmSeeker, stubTmxWriter);
        } catch (IllegalArgumentException iae) {
            errMsg = iae.getMessage();
        }
        assertEquals("Error message", "tmxUri was not set", errMsg);
    }

    @Test
    public void exportTmxSeekerNull() throws IOException {
        String errMsg = null;
        try {
            handler.exportTmx(sampleTMX, null, stubTmxWriter);
        } catch (IllegalArgumentException iae) {
            errMsg = iae.getMessage();
        }
        assertEquals("Error message", "tmSeeker was not set", errMsg);
    }

    @Test
    public void exportTmxWriterNull() throws IOException {
        String errMsg = null;
        try {
            handler.exportTmx(sampleTMX, stubTmSeeker, null);
        } catch (IllegalArgumentException iae) {
            errMsg = iae.getMessage();
        }
        assertEquals("Error message", "tmxWriter was not set", errMsg);
    }

    @Test
    public void importTMXMetadataWithData() throws IOException {
        handler.importTmx(sampleTMX, "IT", stubTmWriter);
        assertEquals("ID", "helloid", stubTmWriter.tus.get(0).getMetadata().get(MetadataType.ID));
        assertEquals("TYPE", "plaintext", stubTmWriter.tus.get(0).getMetadata().get(MetadataType.TYPE));
        assertEquals("FILE_NAME", "StringInfoForTest3.info", stubTmWriter.tus.get(0).getMetadata().get(MetadataType.FILE_NAME));
        assertEquals("GROUP_NAME", "APCCalibrateTimeoutAction0", stubTmWriter.tus.get(0).getMetadata().get(MetadataType.GROUP_NAME));
        assertEquals("# of metadata", 0, stubTmWriter.tus.get(1).getMetadata().size());
    }

    @Test
    public void importTMXMetadataWithoutData() throws IOException {
        handler.importTmx(sampleTMX, "IT", stubTmWriter);
        assertEquals("# of metadata", 0, stubTmWriter.tus.get(1).getMetadata().size());
    }

    @Test
    public void importTmxNullFile() throws IOException {
        String errMsg = null;
        try {
            handler.importTmx(null, "FR", stubTmWriter);
        } catch (IllegalArgumentException iae) {
            errMsg = iae.getMessage();
        }
        assertEquals("Error message", "tmxUri was not set", errMsg);
    }

    @Test
    public void constructorEmptySourceLang() {
        String errMsg = null;
        try {
            new OkapiTMXHandler("", stubTmxFilter);
        } catch (IllegalArgumentException iae) {
            errMsg = iae.getMessage();
        }
        assertEquals("Error message", "sourceLang must be set", errMsg);
    }

    @Test
    public void constructorEmptyFilter() {
        String errMsg = null;
        try {
            new OkapiTMXHandler("EN", null);
        } catch (IllegalArgumentException iae) {
            errMsg = iae.getMessage();
        }
        assertEquals("Error message", "filter must be set", errMsg);
    }

    @Test(expected = IllegalArgumentException.class)
    public void importTMXEmptyTargetLang() throws IOException {
        handler.importTmx(sampleTMX, "", new StubTMWriter());
    }

    @Test(expected = IllegalArgumentException.class)
    public void importTMXNullTargetLang() throws IOException {
        handler.importTmx(sampleTMX, null, new StubTMWriter());
    }

    @Test(expected = IllegalArgumentException.class)
    public void importTMXNullTMWriter() throws IOException {
        handler.importTmx(sampleTMX, "FR", null);
    }

    @Test
    public void tUCount_ExistingLang() throws IOException {
        handler.importTmx(sampleTMX, "IT", stubTmWriter);
        assertEquals("number of TUs", 2, stubTmWriter.tus.size());
    }

    @Test
    public void tUCount_NonExistingLang() throws IOException {
        handler.importTmx(sampleTMX, "FR", stubTmWriter);
        assertEquals("number of TUs", 2, stubTmWriter.tus.size());
        assertNull("targets content should be null", stubTmWriter.tus.get(0).getTarget().getContent());
        assertEquals("target lang", "FR", stubTmWriter.tus.get(0).getTarget().getLang());
    }

    @Test
    public void sourceAndTargetForExistingLang() throws IOException {
        handler.importTmx(sampleTMX, "IT", stubTmWriter);
        assertEquals("first match source", "hello", stubTmWriter.tus.get(0).getSource().getContent().toString());
        assertEquals("first match target", "ciao", stubTmWriter.tus.get(0).getTarget().getContent().toString());
    }

    @Test
    public void sourceAndTargetForNonExistingLang() throws IOException {
        handler.importTmx(sampleTMX, "FR", stubTmWriter);
        assertEquals("first match source", "hello",
                stubTmWriter.tus.get(0).getSource().getContent().toString());
        assertNull("target for non-existant language should be null",
                stubTmWriter.tus.get(0).getTarget().getContent());
    }

    //An example of a Stub. I will likely change this to a Mock later
    @Test
    public void importTMXDocCount() throws IOException {
        StubTMWriter tmWriter = new StubTMWriter();
        handler.importTmx(sampleTMX, "EN", tmWriter);
        assertEquals("entries indexed", 2, tmWriter.tus.size());
    }

    public class StubTMWriter implements TMWriter {

        protected boolean endIndexCalled = false;
        protected List<TranslationUnit> tus = new ArrayList<TranslationUnit>();

        public void endIndex() throws IOException {
        }

        public void indexTranslationUnit(TranslationUnit tu) throws IOException {
            tus.add(tu);
        }

        public void delete(String id) throws IOException {
        }

        public void update(TranslationUnit tu) throws IOException {
        }
    }

    public class StubTMSeeker implements TMSeeker {

        List<TranslationUnit> tus = new ArrayList<TranslationUnit>();

        private void addTU(String source, String sourceLang, String target, String targetLang, String id) {
            tus.add(Helper.createTU(sourceLang, targetLang, source, target, id));
        }

        public List<TMHit> searchExact(String query, int max) throws IOException {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public List<TMHit> searchForWords(String query, int max) throws IOException {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public List<TMHit> searchFuzzyWuzzy(String query, int max) throws IOException {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public List<TranslationUnit> getAllTranslationUnits() {
            return tus;
        }
    }

    public class StubTmxWriter extends TMXWriter {

        private String path;
        private String sourceLanguage;
        private String targetLanguage;
        private String creationTool;
        private String creationToolVersion;
        private String segType;
        private String originalTMFormat;
        private String dataType;
        private Boolean closed;
        private Boolean startWritten;
        private Boolean endWritten;
        private List<TextUnit> textUnits;
        private List<Map<String, String>> attributes;

        public StubTmxWriter() {
            path = "";
            sourceLanguage = "";
            targetLanguage = "";
            creationTool = "";
            creationToolVersion = "";
            segType = "";
            originalTMFormat = "";
            dataType = "";
            closed = false;
            startWritten = false;
            endWritten = false;
            textUnits = new ArrayList<TextUnit>();
            attributes = new ArrayList<Map<String, String>>();
        }

        @Override
        public void close() {
            closed = true;
        }

        @Override
        public void create(String path) {
            this.path = path;
        }

        @Override
        public void writeEndDocument() {
            endWritten = true;
        }

        @Override
        public void writeItem(TextUnit item, Map<String, String> attributes) {
            textUnits.add(item);
            this.attributes.add(attributes);
        }

        @Override
        public void writeStartDocument(String sourceLanguage, String targetLanguage, String creationTool, String creationToolVersion, String segType, String originalTMFormat, String dataType) {
            startWritten = true;
            this.sourceLanguage = sourceLanguage;
            this.targetLanguage = targetLanguage;
            this.creationTool = creationTool;
            this.creationToolVersion = creationToolVersion;
            this.segType = segType;
            this.originalTMFormat = originalTMFormat;
            this.dataType = dataType;
        }
    }

    public class StubTMXFilter extends AbstractFilter {

        private List<Event> events;
        private Iterator<Event> eventIterator;

        public StubTMXFilter() {
            this.events = new ArrayList<Event>();

        }

        public void addEvent(String id, String source, String target, String targetLang, String[][] properties) {
            TextUnit tu = new TextUnit(id, source);
            tu.setTargetContent(targetLang, new TextFragment(target));
            //populate properties
            if (properties != null) {
                for (String[] prop : properties) {
                    tu.setProperty(new Property(prop[0], prop[1]));
                }
            }
            events.add(new Event(EventType.TEXT_UNIT, tu));
        }

        public boolean hasNext() {
            if (eventIterator == null) {
                eventIterator = this.events.iterator();
            }
            return eventIterator.hasNext();
        }

        public Event next() {
            return eventIterator.next();
        }

        protected boolean isUtf8Encoding() {
            return false;
        }

        protected boolean isUtf8Bom() {
            return false;
        }

        public IParameters getParameters() {
            return null;
        }

        public void setParameters(IParameters params) {
        }

        public void open(RawDocument input) {
        }

        public void open(RawDocument input, boolean generateSkeleton) {
        }

        public void close() {
        }
    }
}
