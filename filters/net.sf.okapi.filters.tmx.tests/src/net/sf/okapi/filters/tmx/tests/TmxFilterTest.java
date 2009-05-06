package net.sf.okapi.filters.tmx.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.Util;
import net.sf.okapi.common.exceptions.OkapiBadFilterInputException;
import net.sf.okapi.common.exceptions.OkapiIOException;
import net.sf.okapi.common.resource.RawDocument;
import net.sf.okapi.common.resource.StartDocument;
import net.sf.okapi.common.resource.TextUnit;
import net.sf.okapi.filters.tests.FilterTestDriver;
import net.sf.okapi.filters.tests.InputDocument;
import net.sf.okapi.filters.tests.RoundTripComparison;
import net.sf.okapi.filters.tmx.TmxFilter;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class TmxFilterTest {

	private TmxFilter filter;
	
	String simpleSnippet = "<?xml version=\"1.0\"?>\r"
		+ "<tmx version=\"1.4\"><header creationtool=\"undefined_creationtool\" creationtoolversion=\"undefined_creationversion\" segtype=\"undefined_segtype\" o-tmf=\"undefined_unknown\" adminlang=\"undefined_adminlang\" srclang=\"en-us\" datatype=\"unknown\"></header><body><tu tuid=\"tuid_1\"><tuv xml:lang=\"en-us\"><seg>Hello World!</seg></tuv></tu></body></tmx>\r";

	String tuMissingXmlLangSnippet = "<?xml version=\"1.0\"?>\r"
		+ "<tmx version=\"1.4\"><header creationtool=\"undefined_creationtool\" creationtoolversion=\"undefined_creationversion\" segtype=\"undefined_segtype\" o-tmf=\"undefined_unknown\" adminlang=\"undefined_adminlang\" srclang=\"en-us\" datatype=\"unknown\"></header><body><tu tuid=\"tuid_1\"><tuv><seg>Hello World!</seg></tuv></tu></body></tmx>\r";

	String invalidXmlSnippet = "<?xml version=\"1.0\"?>\r"
		+ "<tmx version=\"1.4\"><header creationtool=\"undefined_creationtool\" creationtoolversion=\"undefined_creationversion\" segtype=\"undefined_segtype\" o-tmf=\"undefined_unknown\" adminlang=\"undefined_adminlang\" srclang=\"en-us\" datatype=\"unknown\"></header><body><tu tuid=\"tuid_1\"><tuv xml:lang=\"en-us\"><seg>Hello World!</seg></tu></body></tmx>\r";

	
	@Before
	public void setUp() {
		filter = new TmxFilter();
	}


	@Test (expected=NullPointerException.class)
	public void testSourceLangNotSpecified() {
		FilterTestDriver.getStartDocument(getEvents(simpleSnippet, null));
	}

	@Test (expected=NullPointerException.class)
	public void testTargetLangEmpty() {
		FilterTestDriver.getStartDocument(getEvents(simpleSnippet, "en-us",""));
	}	
	
	@Test (expected=NullPointerException.class)
	public void testTargetLangEmpty2() {
		FilterTestDriver.getStartDocument(getEvents(simpleSnippet, "en-us",null));
	}	
	
	@Test (expected=OkapiBadFilterInputException.class)
	public void testTuXmlLangMissing() {
		FilterTestDriver.getStartDocument(getEvents(tuMissingXmlLangSnippet, "en-us","fr-fr"));
	}
	
	@Test (expected=OkapiIOException.class)
	public void testInvalidXml() {
		FilterTestDriver.getStartDocument(getEvents(invalidXmlSnippet, "en-us","fr-fr"));
	}
	
	
	@Test
	public void testStartDocument () {
		StartDocument sd = FilterTestDriver.getStartDocument(getEvents(simpleSnippet, "en-us","fr-fr"));
		assertNotNull(sd);
		assertNotNull(sd.getEncoding());
		assertNotNull(sd.getType());
		assertNotNull(sd.getMimeType());
		assertNotNull(sd.getLanguage());
		assertEquals("\r", sd.getLineBreak());
	}
	
	@Test
	public void testSimpleTransUnit () {
		TextUnit tu = FilterTestDriver.getTextUnit(getEvents(simpleSnippet, "en-us","fr-fr"), 1);
		assertNotNull(tu);
		assertEquals("Hello World!", tu.getSource().toString());
		assertEquals("tuid_1", tu.getName());
	}
	
	@Test
	public void runTest () {
		FilterTestDriver testDriver = new FilterTestDriver();
		TmxFilter filter = null;		
		try {
			filter = new TmxFilter();
			URL url = TmxFilterTest.class.getResource("/ImportTest2A.tmx");
			filter.open(new RawDocument(new URI(url.toString()), "UTF-8", "EN-US", "FR-CA"));			
			if ( !testDriver.process(filter) ) Assert.fail();
			filter.close();
			//process(filter);
			//filter.close();
			
		}
		catch ( Throwable e ) {
			e.printStackTrace();
			Assert.fail("Exception occured");
		}
		finally {
			if ( filter != null ) filter.close();
		}
	}	
	
/*	private void process (IFilter filter) {
		
		System.out.println("==================================================");
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
				int i = 1;
				for ( String lang : tu.getTargetLanguages() ) {
					System.out.println("T"+(i++)+" "+lang+"=["+tu.getTarget(lang).toString()+"]");
				}
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
	}*/
	
	
	
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
	
	
	@Test
	public void testDoubleExtraction () throws URISyntaxException {
		// Read all files in the data directory
		URL url = TmxFilterTest.class.getResource("/Paragraph_TM.tmx");
		String root = Util.getDirectoryName(url.getPath());
		root = Util.getDirectoryName(root) + "/data/";
		
		ArrayList<InputDocument> list = new ArrayList<InputDocument>();
		list.add(new InputDocument(root+"Paragraph_TM.TMX", null));

		RoundTripComparison rtc = new RoundTripComparison();
		assertTrue(rtc.executeCompare(filter, list, "UTF-8", "en-us", "fr-fr"));
	}	
	
}
