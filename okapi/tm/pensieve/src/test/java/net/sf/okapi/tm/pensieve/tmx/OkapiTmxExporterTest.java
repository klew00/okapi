/*===========================================================================
Copyright (C) 2008-2011 by the Okapi Framework contributors
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
import net.sf.okapi.common.resource.ITextUnit;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.tm.pensieve.Helper;
import net.sf.okapi.tm.pensieve.common.MetadataType;
import net.sf.okapi.tm.pensieve.common.TranslationUnit;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Iterator;
import net.sf.okapi.tm.pensieve.seeker.PensieveSeeker;

/**
 * @author Dax
 */
public class OkapiTmxExporterTest {

    URI sampleTMX;
    OkapiTmxExporter handler;
    TMXWriter mockTmxWriter;
    Iterator<TranslationUnit> mockIterator;
    PensieveSeeker mockSeeker;
    LocaleId locEN = LocaleId.fromString("en");
    LocaleId locFR = LocaleId.fromString("fr");
    LocaleId locKR = LocaleId.fromString("kr");
    LocaleId locProps = LocaleId.fromString("Props"); // Not sure what is this locale?
    
    ArgumentCaptor<ITextUnit> tuCapture;

    
    @SuppressWarnings("unchecked")
	@Before
    public void setUp() throws URISyntaxException, IOException {
        tuCapture = ArgumentCaptor.forClass(ITextUnit.class);
        
        mockIterator = mock(Iterator.class);
        mockTmxWriter = mock(TMXWriter.class);

        sampleTMX = new URI("test.tmx");
        handler = new OkapiTmxExporter();

        mockSeeker = mock(PensieveSeeker.class);

        when(mockIterator.hasNext())
                .thenReturn(true)
                .thenReturn(true)
                .thenReturn(true)
                .thenReturn(true)
                .thenReturn(false);
        TranslationUnit tuWithMetadata = Helper.createTU(locEN, locProps, "props_source", "props_target", "props_sourceid");
        tuWithMetadata.setMetadataValue(MetadataType.GROUP_NAME, "PropsGroupName");
        tuWithMetadata.setMetadataValue(MetadataType.FILE_NAME, "PropsFileName");

        when(mockIterator.next())
                .thenReturn(Helper.createTU(locEN, locFR, "source", "target", "sourceid"))
                .thenReturn(Helper.createTU(locEN, locFR, "source2", "target2", "sourceid2"))
                .thenReturn(Helper.createTU(locEN, locKR, "kr_source", "kr_target", "kr_sourceid"))
                .thenReturn(tuWithMetadata)
                .thenReturn(null);

        when(mockSeeker.iterator()).thenReturn(mockIterator);
    }

    @Test
    public void exportTmxBehavior() throws IOException {
        handler.exportTmx(locEN, locFR, mockSeeker, mockTmxWriter);

        verify(mockTmxWriter).writeStartDocument(locEN, locFR, "pensieve", "0.0.1", "sentence", "pensieve", "unknown");
        verify(mockTmxWriter, times(2)).writeTUFull((ITextUnit) anyObject());
        verify(mockTmxWriter).writeEndDocument();
        verify(mockTmxWriter).close();
    }

    @Test
    public void exportTmxTextUnitContentNoProps() throws IOException {
        handler.exportTmx(locEN, locFR, mockSeeker, mockTmxWriter);

        verify(mockTmxWriter, times(2)).writeTUFull(tuCapture.capture());
        assertEquals("source of first tu written", "source", tuCapture.getAllValues().get(0).getSource().getFirstContent().toText());
        assertEquals("target of first tu written", "target", tuCapture.getAllValues().get(0).getTarget(locFR).getFirstContent().toText());
        assertEquals("target of first tu written", "sourceid", tuCapture.getAllValues().get(0).getName());
        assertEquals("source of second tu written", "source2", tuCapture.getAllValues().get(1).getSource().getFirstContent().toText());
        assertEquals("target of second tu written", "target2", tuCapture.getAllValues().get(1).getTarget(locFR).getFirstContent().toText());
        assertEquals("target of second tu written", "sourceid2", tuCapture.getAllValues().get(1).getName());
    }

    @Test
    public void exportTmxTextUnitContentWithProps() throws IOException {
        handler.exportTmx(locEN, locProps, mockSeeker, mockTmxWriter);

        verify(mockTmxWriter, times(1)).writeTUFull(tuCapture.capture());
        ITextUnit capturedTU = tuCapture.getValue();
        assertEquals("source of first tu written", "props_source", capturedTU.getSource().getFirstContent().toText());
        assertEquals("target of first tu written", "props_target", capturedTU.getTarget(locProps).getFirstContent().toText());
        assertEquals("target of first tu written", "props_sourceid", capturedTU.getName());
        assertEquals("groupname metadata", "PropsGroupName", capturedTU.getProperty("Txt::GroupName").getValue());
        assertEquals("filename metadata", "PropsFileName", capturedTU.getProperty("Txt::FileName").getValue());
        assertEquals("metadata size", 2, capturedTU.getPropertyNames().size());
    }

    @Test
    public void exportTmxAllTargetLang() throws IOException {
        handler.exportTmx(locEN, mockSeeker, mockTmxWriter);
        verify(mockTmxWriter, times(4)).writeTUFull((ITextUnit)anyObject());
    }

    @Test
    public void exportTmxNoMatchingSourceLang() throws IOException {
        handler.exportTmx(locKR, locFR, mockSeeker, mockTmxWriter);
        verify(mockTmxWriter, never()).writeTUFull((ITextUnit)anyObject());
    }

    @Test
    public void exportTmxSpecificTargetLang() throws IOException {
        handler.exportTmx(locEN, locKR, mockSeeker, mockTmxWriter);
        verify(mockTmxWriter, times(1)).writeTUFull(tuCapture.capture());
        assertEquals("target of first tu written", "kr_sourceid", tuCapture.getValue().getName());
    }

    @Test
    public void exportTmxSeekerNull() throws IOException {
        String errMsg = null;
        try {
            handler.exportTmx(LocaleId.EMPTY, LocaleId.EMPTY, null, mockTmxWriter);
        } catch (IllegalArgumentException iae) {
            errMsg = iae.getMessage();
        }
        assertEquals("Error message", "'tmSeeker' was not set", errMsg);
    }

    @Test
    public void exportTmxWriterNull() throws IOException {
        String errMsg = null;
        try {
            handler.exportTmx(LocaleId.EMPTY, LocaleId.EMPTY, mockSeeker, null);
        } catch (IllegalArgumentException iae) {
            errMsg = iae.getMessage();
        }
        assertEquals("Error message", "'tmxWriter' was not set", errMsg);
    }

    @Test
    public void exportTmxSourceLangNull() throws IOException {
        String errMsg = null;
        try {
            handler.exportTmx(null, LocaleId.EMPTY, mockSeeker, mockTmxWriter);
        } catch (IllegalArgumentException iae) {
            errMsg = iae.getMessage();
        }
        assertEquals("Error message", "'sourceLang' was not set", errMsg);
    }
}
