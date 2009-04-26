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

package net.sf.okapi.filters.xliff.tests;

import java.net.URI;
import java.net.URL;
import java.util.ArrayList;

//import net.sf.okapi.common.Event;
//import net.sf.okapi.common.filters.IFilter;
//import net.sf.okapi.common.IResource;
//import net.sf.okapi.common.ISkeleton;
//import net.sf.okapi.common.resource.INameable;
//import net.sf.okapi.common.resource.TextUnit;
import net.sf.okapi.common.Event;
import net.sf.okapi.common.filterwriter.GenericContent;
import net.sf.okapi.common.resource.RawDocument;
import net.sf.okapi.common.resource.StartDocument;
import net.sf.okapi.common.resource.TextUnit;
import net.sf.okapi.filters.tests.FilterTestDriver;
//import net.sf.okapi.filters.xliff.AltTransAnnotation;
import net.sf.okapi.filters.xliff.XLIFFFilter;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class XLIFFFilterTest {

	private XLIFFFilter filter;
	private GenericContent fmt;

	@Before
	public void setUp() {
		filter = new XLIFFFilter();
		fmt = new GenericContent();
	}

	@Test
	public void testExternalFile () {
		FilterTestDriver testDriver = new FilterTestDriver();
		testDriver.setDisplayLevel(0);
		testDriver.setShowSkeleton(true);
		try {
			URL url = XLIFFFilterTest.class.getResource("/JMP-11-Test01.xlf");
			filter.open(new RawDocument(new URI(url.toString()), "UTF-8", "en", "fr"));
			if ( !testDriver.process(filter) ) Assert.fail();
			filter.close();
		}
		catch ( Throwable e ) {
			e.printStackTrace();
			Assert.fail();
		}
		finally {
			if ( filter != null ) filter.close();
		}
	}
	
	@Test
	public void testStartDocument () {
		StartDocument sd = FilterTestDriver.getStartDocument(createSimpleXLIFF());
		assertNotNull(sd);
		assertNotNull(sd.getEncoding());
		assertNotNull(sd.getType());
		assertNotNull(sd.getMimeType());
		assertNotNull(sd.getLanguage());
		assertEquals("\r", sd.getLineBreak());
	}
	
	@Test
	public void testSimpleTransUnit () {
		TextUnit tu = FilterTestDriver.getTextUnit(createSimpleXLIFF(), 1);
		assertNotNull(tu);
		assertEquals("Hello World!", tu.getSource().toString());
		assertEquals("13", tu.getName());
	}

	@Test
	public void testBilingualTransUnit () {
		TextUnit tu = FilterTestDriver.getTextUnit(createBilingualXLIFF(), 1);
		assertNotNull(tu);
		assertEquals("S1, S2", tu.getSource().toString());
		fmt.setContent(tu.getSourceContent());
		assertEquals("<1>S1</1>, <2>S2</2>", fmt.toString());
		assertTrue(tu.hasTarget("fr"));
		assertEquals("T2, T1", tu.getTarget("fr").toString());
		fmt.setContent(tu.getTargetContent("fr"));
		assertEquals("<2>T2</2>, <1>T1</1>", fmt.toString());
	}

	@Test
	public void testBPTTypeTransUnit () {
		TextUnit tu = FilterTestDriver.getTextUnit(createBPTTypeXLIFF(), 1);
		assertNotNull(tu);
		fmt.setContent(tu.getSourceContent());
		assertEquals("<1>S1</1>, <2>S2</2>", fmt.toString());
		assertTrue(tu.hasTarget("fr"));
		fmt.setContent(tu.getTargetContent("fr"));
		assertEquals("<2>T2</2>, <1>T1</1>", fmt.toString());
	}

	private ArrayList<Event> createSimpleXLIFF () {
		String snippet = "<?xml version=\"1.0\"?>\r"
			+ "<xliff version=\"1.2\">\r"
			+ "<file source-language=\"en\" datatype=\"plaintext\" original=\"file.ext\">"
			+ "<body><trans-unit id=\"13\"><source>Hello World!</source></trans-unit></body>"
			+ "</file></xliff>";
		return getEvents(snippet);
	}
	
	private ArrayList<Event> createBilingualXLIFF () {
		String snippet = "<?xml version=\"1.0\"?>\r"
			+ "<xliff version=\"1.2\">\r"
			+ "<file source-language=\"en\" datatype=\"plaintext\" original=\"file.ext\">"
			+ "<body><trans-unit id=\"13\"><source><g id='1'>S1</g>, <g id='2'>S2</g></source>"
			+ "<target><g id='2'>T2</g>, <g id='1'>T1</g></target></trans-unit></body>"
			+ "</file></xliff>";
		return getEvents(snippet);
	}
	
	private ArrayList<Event> createBPTTypeXLIFF () {
		String snippet = "<?xml version=\"1.0\"?>\r"
			+ "<xliff version=\"1.2\">\r"
			+ "<file source-language=\"en\" datatype=\"x-test\" original=\"file.ext\">"
			+ "<body><trans-unit id=\"13\"><source><bpt id='1'>a</bpt>S1<ept id='1'>/a</ept>, <bpt id='2'>b</bpt>S2<ept id='2'>/b</ept></source>"
			+ "<target><bpt id='2'>b</bpt>T2<ept id='2'>/b</ept>, <bpt id='1'>a</bpt>T1<ept id='1'>/a</ept></target></trans-unit></body>"
			+ "</file></xliff>";
		return getEvents(snippet);
	}
	
	private ArrayList<Event> getEvents(String snippet) {
		ArrayList<Event> list = new ArrayList<Event>();
		filter.open(new RawDocument(snippet, "en", "fr"));
		while ( filter.hasNext() ) {
			Event event = filter.next();
			list.add(event);
		}
		filter.close();
		return list;
	}
	
	
/*	private void process (IFilter filter) {
		System.out.println("================================================");
		Event event;
		while ( filter.hasNext() ) {
			event = filter.next();
			switch ( event.getEventType() ) {
			case START_DOCUMENT:
				System.out.println("---Start Document");
				printSkeleton(event.getResource());
				break;
			case END_DOCUMENT:
				System.out.println("---End Document");
				printSkeleton(event.getResource());
				break;
			case START_SUBDOCUMENT:
				System.out.println("---Start Sub Document");
				printSkeleton(event.getResource());
				break;
			case END_SUBDOCUMENT:
				System.out.println("---End Sub Document");
				printSkeleton(event.getResource());
				break;
			case START_GROUP:
				System.out.println("---Start Group");
				printSkeleton(event.getResource());
				break;
			case END_GROUP:
				System.out.println("---End Group");
				printSkeleton(event.getResource());
				break;
			case TEXT_UNIT:
				System.out.println("---Text Unit");
				TextUnit tu = (TextUnit)event.getResource();
				printResource(tu);
				System.out.println("S=["+tu.toString()+"]");
				for ( String lang : tu.getTargetLanguages() ) {
					System.out.println("T=["+tu.getTarget(lang).toString()+"]");
				}
				printAltTrans(tu);
				printSkeleton(tu);
				break;
			case DOCUMENT_PART:
				System.out.println("---Document Part");
				printResource((INameable)event.getResource());
				printSkeleton(event.getResource());
				break;
			}
		}
	}
	
	private void printResource (INameable res) {
		System.out.println("  id="+res.getId());
		System.out.println("  name="+res.getName());
		System.out.println("  type="+res.getType());
		System.out.println("  mimeType="+res.getMimeType());
	}

	private void printSkeleton (IResource res) {
		ISkeleton skel = res.getSkeleton();
		if ( skel != null ) {
			System.out.println("---");
			System.out.println(skel.toString());
			System.out.println("---");
		}
	}

	private void printAltTrans (TextUnit res) {
		System.out.println("---AltTransAnnotation---");
		AltTransAnnotation ata = res.getAnnotation(AltTransAnnotation.class);
		if ( ata == null ) {
			System.out.println("No annotation");
		}
		else {
			ata.startIteration();
			while ( ata.moveToNext() ) {
				TextUnit tu = ata.getEntry();
				if ( ata.hasSource() ) {
					System.out.println("S("+ata.getSourceLanguage()+")=["+tu.toString()+"]");
				}
				else {
					System.out.println("No source defined.");
					
				}
				System.out.println("T("+ata.getTargetLanguage()+")=["
					+tu.getTarget(ata.getTargetLanguage()).toString()+"]");
			}
		}
		System.out.println("---end of AltTransAnnotation---");
	}
*/
}
