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

import net.sf.okapi.common.filterwriter.TMXWriter;
import net.sf.okapi.common.resource.TextUnit;
import net.sf.okapi.tm.pensieve.Helper;
import net.sf.okapi.tm.pensieve.common.TranslationUnit;
import net.sf.okapi.tm.pensieve.seeker.TmSeeker;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Dax
 */
public class OkapiTmxHandlerTest {

    URI sampleTMX;
    OkapiTmxHandler handler;
    TMXWriter mockTmxWriter;
    TmSeeker mockSeeker;
    List<TranslationUnit> tus;

    @Before
    public void setUp() throws URISyntaxException, IOException {
        mockTmxWriter = mock(TMXWriter.class);

        sampleTMX = new URI("test.tmx");
        handler = new OkapiTmxHandler();

        mockSeeker = mock(TmSeeker.class);
        tus = new LinkedList<TranslationUnit>();
        tus.add(Helper.createTU("EN", "FR", "source", "target", "sourceid"));
        tus.add(Helper.createTU("EN", "FR", "source2", "target2", "sourceid2"));
        when(mockSeeker.getAllTranslationUnits()).thenReturn(tus);
    }

    @Test
    public void exportTmxStepsCalled() throws IOException {
        //TODO: This should be easier to test. We should probably add some methods in XMLWriter and TMXWriter that
        //allow for interfaces like java's Writer to be sent it.
        handler.exportTmx(sampleTMX, "EN", "FR", mockSeeker, mockTmxWriter);

        ArgumentCaptor<TextUnit> tuCapture = ArgumentCaptor.forClass(TextUnit.class);

        verify(mockTmxWriter).writeStartDocument("EN", "FR", "pensieve", "0.0.1", "sentence", "pensieve", "unknown");
        verify(mockTmxWriter, times(2)).writeTUFull(tuCapture.capture());
        assertEquals("source of first tu written", "source", tuCapture.getAllValues().get(0).getSourceContent().toString());
        assertEquals("target of first tu written", "target", tuCapture.getAllValues().get(0).getTargetContent("FR").toString());
        assertEquals("target of first tu written", "sourceid", tuCapture.getAllValues().get(0).getName());
        assertEquals("source of second tu written", "source2", tuCapture.getAllValues().get(1).getSourceContent().toString());
        assertEquals("target of second tu written", "target2", tuCapture.getAllValues().get(1).getTargetContent("FR").toString());
        assertEquals("target of second tu written", "sourceid2", tuCapture.getAllValues().get(1).getName());
        verify(mockTmxWriter).writeEndDocument();
        verify(mockTmxWriter).close();
    }

    @Test
    public void exportTmxFileNull() throws IOException {
        String errMsg = null;
        try {
            handler.exportTmx(null, "", "", mockSeeker, mockTmxWriter);
        } catch (IllegalArgumentException iae) {
            errMsg = iae.getMessage();
        }
        assertEquals("Error message", "tmxUri was not set", errMsg);
    }

    @Test
    public void exportTmxSeekerNull() throws IOException {
        String errMsg = null;
        try {
            handler.exportTmx(sampleTMX, "", "", null, mockTmxWriter);
        } catch (IllegalArgumentException iae) {
            errMsg = iae.getMessage();
        }
        assertEquals("Error message", "tmSeeker was not set", errMsg);
    }

    @Test
    public void exportTmxWriterNull() throws IOException {
        String errMsg = null;
        try {
            handler.exportTmx(sampleTMX, "", "", mockSeeker, null);
        } catch (IllegalArgumentException iae) {
            errMsg = iae.getMessage();
        }
        assertEquals("Error message", "tmxWriter was not set", errMsg);
    }
}
