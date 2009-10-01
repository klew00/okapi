/*===========================================================================
  Copyright (C) 2009 by the Okapi Framework contributors
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
============================================================================*/

package net.sf.okapi.filters.ts;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import net.sf.okapi.common.Event;

import net.sf.okapi.common.resource.Property;
import net.sf.okapi.common.resource.RawDocument;
import net.sf.okapi.common.resource.TextUnit;
import net.sf.okapi.common.filters.FilterTestDriver;
import net.sf.okapi.common.filters.InputDocument;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class TsFilterTest {

	private TsFilter filter;
	private FilterTestDriver testDriver;
	
	String simpleSnippet = "<TS><context><name>AlarmAddLogDlg</name><message><source>Add Entry To System Log</source><translation type=\"unfinished\">Lagg till i system Loggen</translation></message></context></TS>";

	@Before
	public void setUp() throws ParserConfigurationException, SAXException, IOException {
		filter = new TsFilter();
		testDriver = new FilterTestDriver();
		testDriver.setDisplayLevel(0);
		testDriver.setShowSkeleton(true);
		
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	      DocumentBuilder builder = factory.newDocumentBuilder();
	      InputSource is = new InputSource(new StringReader("<hello><name>john</name></hello>"));
	      builder.parse( is );
		
	}
	
	//--methods--
	@Test
	public void testGetName() {
		assertEquals("okf_ts", filter.getName());
	}

	@Test
	public void testGetMimeType() {
		assertEquals("application/x-ts", filter.getMimeType());
	}	
	
	//--exceptions--
	@Test (expected=NullPointerException.class)
	public void testSourceLangNotSpecified() {
		FilterTestDriver.getStartDocument(getEvents(simpleSnippet, null));
	}

	@Test (expected=NullPointerException.class)
	public void testTargetLangNotSpecified() {
		FilterTestDriver.getStartDocument(getEvents(simpleSnippet, "en-us"));
	}

	@Test (expected=NullPointerException.class)
	public void testTargetLangNotSpecified2() {
		FilterTestDriver.getStartDocument(getEvents(simpleSnippet, "en-us", null));
	}
	
/*	@Test (expected=NullPointerException.class)
	public void testSourceLangEmpty() {
		FilterTestDriver.getStartDocument(getEvents(simpleSnippet, "","fr-fr"));
	}*/	
	
/*	@Test (expected=NullPointerException.class)
	public void testTargetLangEmpty() {
		FilterTestDriver.getStartDocument(getEvents(simpleSnippet, "en-us",""));
	}*/	
	
	@Test
	public void testInputStream() {
		InputStream tsStream = TsFilterTest.class.getResourceAsStream("/alarm_ro.ts");
		filter.open(new RawDocument(tsStream, "UTF-8", "en-us","fr-fr"));
		if ( !testDriver.process(filter) ) Assert.fail();
		filter.close();
	}	

	@Test
	public void testConsolidatedStream() {
		filter.open(new RawDocument(simpleSnippet, "en-us","fr-fr"));
		if ( !testDriver.process(filter) ) Assert.fail();
		filter.close();
		//System.out.println(FilterTestDriver.generateOutput(getEvents(simpleSnippet, "en-us","fr-fr"), simpleSnippet, "fr-fr"));
	}	

	@Test
	public void testTu() {
		TextUnit tu = FilterTestDriver.getTextUnit(getEvents(simpleSnippet, "en-us", "fr-fr"), 1);
		assertNotNull(tu);
		assertEquals("Add Entry To System Log", tu.getSourceContent().getCodedText());
		assertEquals("Lagg till i system Loggen", tu.getTargetContent("fr-fr").getCodedText());
		
		System.out.println(tu.getId());
		System.out.println(tu.getMimeType());
		System.out.println(tu.getName());
		System.out.println(tu.getType());
		System.out.println(tu.getPropertyNames());
		System.out.println(tu.getSkeleton());
		System.out.println(tu.getTargetLanguages());
		
		tu.setTargetProperty("fr-fr", new Property(Property.APPROVED, "no", false));
		System.out.println(tu.getTargetPropertyNames("fr-fr"));
		/*Property prop = dp.getProperty(Property.ENCODING);
		assertNotNull(prop);
		assertEquals("UTF-8", prop.getValue());
		assertFalse(prop.isReadOnly());*/
		
	}	

	

	
	/*
	@Test
	public void testOutputBasic_Comment () {
		assertEquals(simpleBilingualSnippet, FilterTestDriver.generateOutput(getEvents(simpleBilingualSnippet,"en-us","fr-fr"), simpleSnippet, "fr-fr"));
		System.out.println(FilterTestDriver.generateOutput(getEvents(simpleBilingualSnippet,"en-us","fr-fr"), simpleSnippet, "en"));
	}*/	
	
	/*@Test
	public void testStartDocument () {
		StartDocument sd = FilterTestDriver.getStartDocument(getEvents(simpleSnippet, "en-us","fr-fr"));
		assertNotNull(sd);
		assertNotNull(sd.getEncoding());
		assertNotNull(sd.getType());
		assertNotNull(sd.getMimeType());
		assertNotNull(sd.getLanguage());
		assertEquals("\r", sd.getLineBreak());
	}*/
	
	/*@Test
	public void testSimpleTransUnit () {
		TextUnit tu = FilterTestDriver.getTextUnit(getEvents(simpleSnippet, "en-us","fr-fr"), 1);
		assertNotNull(tu);
		assertEquals("Hello World!", tu.getSource().toString());
		assertEquals("tuid_1", tu.getName());
	}*/
	
	@Test
	public void testStartDocument () {
		URL url = TsFilterTest.class.getResource("/TSTest01.ts");
		assertTrue("Problem in StartDocument", FilterTestDriver.testStartDocument(filter,
			new InputDocument(url.getPath(), null),
			"UTF-8", "en", "en"));
	}
	
	@Test
	public void runTest () {
		FilterTestDriver testDriver = new FilterTestDriver();
		TsFilter filter = null;		
		try {
			filter = new TsFilter();
			URL url = TsFilterTest.class.getResource("/TSTest01.ts");
			filter.open(new RawDocument(new URI(url.toString()), "UTF-8", "EN-US", "fr-fr"));			
			if ( !testDriver.process(filter) ) Assert.fail();
			//process(filter);
			filter.close();
			
		}
		catch ( Throwable e ) {
			e.printStackTrace();
			Assert.fail("Exception occured");
		}
		finally {
			if ( filter != null ) filter.close();
		}
	}	
	
//	private void process (IFilter filter) {
//		
//		System.out.println("==================================================");
//		Event event;
//		while ( filter.hasNext() ) {
//			event = filter.next();
//			switch ( event.getEventType() ) {		
//			case START_DOCUMENT:
//				System.out.println("---Start Document");
//				printSkeleton(event.getResource());
//				break;
//			case END_DOCUMENT:
//				System.out.println("---End Document");
//				printSkeleton(event.getResource());
//				break;
//			case START_GROUP:
//				System.out.println("---Start Group");
//				printSkeleton(event.getResource());
//				break;
//			case END_GROUP:
//				System.out.println("---End Group");
//				printSkeleton(event.getResource());
//				break;
//			case TEXT_UNIT:
//				System.out.println("---Text Unit");
//				TextUnit tu = (TextUnit)event.getResource();
//				printResource(tu);
//				System.out.println("S=["+tu.toString()+"]");
//				int i = 1;
//				for ( String lang : tu.getTargetLanguages() ) {
//					System.out.println("T"+(i++)+" "+lang+"=["+tu.getTarget(lang).toString()+"]");
//				}
//				printSkeleton(tu);
//				break;
//			case DOCUMENT_PART:
//				System.out.println("---Document Part");
//				printResource((INameable)event.getResource());
//				printSkeleton(event.getResource());
//				break;				
//			}
//		}
//	}
	
//	private void printResource (INameable res) {
//		System.out.println("  id="+res.getId());
//		System.out.println("  name="+res.getName());
//		System.out.println("  type="+res.getType());
//		System.out.println("  mimeType="+res.getMimeType());
//	}

//	private void printSkeleton (IResource res) {
//		ISkeleton skel = res.getSkeleton();
//		if ( skel != null ) {
//			System.out.println("---");
//			System.out.println(skel.toString());
//			System.out.println("---");
//		}
//	}

	private ArrayList<Event> getEvents(String snippet, String srcLang, String trgLang){
		ArrayList<Event> list = new ArrayList<Event>();
		filter.open(new RawDocument(snippet, srcLang, trgLang));
		while ( filter.hasNext() ) {
			Event event = filter.next();
			list.add(event);
		}
		filter.close();
		return list;
	}
	
	//--without specifying target language--
	private ArrayList<Event> getEvents(String snippet, String srcLang){
		ArrayList<Event> list = new ArrayList<Event>();
		filter.open(new RawDocument(snippet, srcLang));
		while ( filter.hasNext() ) {
			Event event = filter.next();
			list.add(event);
		}
		filter.close();
		return list;
	}	

}
