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
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;

/**
 *
 * @author Dax
 */
public class OkapiTMXHandlerTest {

    private URI sampleTMX;
    OkapiTMXHandler handler;
    StubTMWriter stubTmWriter;
    StubTMXFilter stubTmxFilter;

    @Before
    public void setUp() throws URISyntaxException {

        stubTmxFilter = new StubTMXFilter();
        stubTmxFilter.addEvent("1", "hello", "ciao", "IT");
        stubTmxFilter.addEvent("2", "world", "mondo", "IT");
        stubTmxFilter.events.add(new Event(EventType.DOCUMENT_PART, new TextUnit("holy cow")));

        sampleTMX = new URI("");
        handler = new OkapiTMXHandler("EN", stubTmxFilter);
        stubTmWriter = new StubTMWriter();
    }

    @Test
    public void importTmxNullFile() throws IOException {
        String errMsg = null;
        try{
            handler.importTmx(null, "FR", stubTmWriter);
        }catch(IllegalArgumentException iae){
            errMsg = iae.getMessage();
        }
        assertEquals("Error message", "tmxUri was not set", errMsg);
    }

    @Test
    public void constructorEmptySourceLang() {
        String errMsg = null;
        try{
            new OkapiTMXHandler("", stubTmxFilter );
        }catch(IllegalArgumentException iae){
            errMsg = iae.getMessage();
        }
        assertEquals("Error message", "sourceLang must be set", errMsg);
    }

    @Test(expected=IllegalArgumentException.class)
    public void importTMXEmptyTargetLang() throws IOException{
        handler.importTmx(sampleTMX, "", new StubTMWriter());
    }

    @Test(expected=IllegalArgumentException.class)
    public void importTMXNullTargetLang() throws IOException{
        handler.importTmx(sampleTMX,null, new StubTMWriter());
    }

    @Test
    public void tUCount_ExistingLang() throws IOException {
        handler.importTmx(sampleTMX, "IT", stubTmWriter);
        assertEquals("number of TUs", 2, stubTmWriter.tus.size());
    }

    @Test
    public void tUCount_NonExistingLang() throws IOException {
        handler.importTmx(sampleTMX, "FR", stubTmWriter);
        //TODO: Is this the behavior we want?  Returning null targets for nonexistant languages
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

    public class StubTMWriter implements TMWriter{
        protected boolean endIndexCalled = false;
        protected List<TranslationUnit> tus = new ArrayList<TranslationUnit>();
        public void endIndex() throws IOException {

        }

        public void indexTranslationUnit(TranslationUnit tu) throws IOException {
            tus.add(tu);
        }

        public void delete(String id) throws IOException {
            
        }
    }

    public class StubTMXFilter extends AbstractFilter{
        private List<Event> events;
        private Iterator<Event> eventIterator;

        public StubTMXFilter() {
            this.events = new ArrayList<Event>();

        }

        public void addEvent(String id, String source, String target, String targetLang) {
            TextUnit tu = new TextUnit(id, source);
            tu.setTargetContent(targetLang, new TextFragment(target));
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

        public void setParameters(IParameters params) {}

        public void open(RawDocument input) {}

        public void open(RawDocument input, boolean generateSkeleton) {}

        public void close() {}
    }


}
