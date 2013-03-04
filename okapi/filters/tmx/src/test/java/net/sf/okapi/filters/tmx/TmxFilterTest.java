package net.sf.okapi.filters.tmx;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.EventType;
import net.sf.okapi.common.MimeTypeMapper;
import net.sf.okapi.common.TestUtil;
import net.sf.okapi.common.exceptions.OkapiBadFilterInputException;
import net.sf.okapi.common.exceptions.OkapiIOException;
import net.sf.okapi.common.filters.FilterConfiguration;
import net.sf.okapi.common.resource.AlignmentStatus;
import net.sf.okapi.common.resource.DocumentPart;
import net.sf.okapi.common.resource.Property;
import net.sf.okapi.common.resource.RawDocument;
import net.sf.okapi.common.resource.StartDocument;
import net.sf.okapi.common.resource.ITextUnit;
import net.sf.okapi.common.filters.FilterTestDriver;
import net.sf.okapi.common.filters.RoundTripComparison;
import net.sf.okapi.common.filters.InputDocument;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.filters.tmx.Parameters;
import net.sf.okapi.filters.tmx.TmxFilter;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class TmxFilterTest {

	private TmxFilter filter;
	private FilterTestDriver testDriver;
	private String root;
	private LocaleId locEN = LocaleId.fromString("en");
	private LocaleId locFR = LocaleId.fromString("fr");
	private LocaleId locDE = LocaleId.fromString("de");
	private LocaleId locIT = LocaleId.fromString("it");
	private LocaleId locENUS = LocaleId.fromString("en-us");
	private LocaleId locFRFR = LocaleId.fromString("fr-fr");
	private LocaleId locFRCA = LocaleId.fromString("fr-ca");
	private LocaleId locENGB = LocaleId.fromString("en-gb");
	private LocaleId locJAJP = LocaleId.fromString("ja-jp");
	
	String simpleSnippetWithDTD = "<?xml version=\"1.0\"?>\r"
		+ "<!DOCTYPE tmx SYSTEM \"tmx14.dtd\"><tmx version=\"1.4\"><header creationtool=\"undefined_creationtool\" creationtoolversion=\"undefined_creationversion\" segtype=\"undefined_segtype\" o-tmf=\"undefined_unknown\" adminlang=\"undefined_adminlang\" srclang=\"en-us\" datatype=\"unknown\"></header><body><tu tuid=\"tuid_1\"><note>hello world note</note><tuv xml:lang=\"en-us\"><seg>Hello World!</seg></tuv></tu><tu tuid=\"tuid_2\"><tuv xml:lang=\"en-us\"><seg>Hello Universe!</seg></tuv></tu></body></tmx>\r";

	String simpleSnippet = "<?xml version=\"1.0\"?>\r"
		+ "<!-- document level comment --><tmx version=\"1.4\"><header creationtool=\"undefined_creationtool\" creationtoolversion=\"undefined_creationversion\" segtype=\"undefined_segtype\" o-tmf=\"undefined_unknown\" adminlang=\"undefined_adminlang\" srclang=\"en-us\" datatype=\"unknown\"></header><body><tu tuid=\"tuid_1\"><note>hello world note</note><tuv xml:lang=\"en-us\"><seg>Hello World!</seg></tuv></tu><tu tuid=\"tuid_1\"><tuv xml:lang=\"en-us\"><seg>Hello Universe!</seg></tuv></tu></body></tmx>\r";

	String simpleBilingualSnippet = "<?xml version=\"1.0\"?>\r"
		+ "<tmx version=\"1.4\"><header creationtool=\"undefined_creationtool\" creationtoolversion=\"undefined_creationversion\" segtype=\"undefined_segtype\" o-tmf=\"undefined_unknown\" adminlang=\"undefined_adminlang\" srclang=\"en-us\" datatype=\"unknown\"></header><body><tu tuid=\"tuid_1\"><tuv xml:lang=\"en-us\"><seg>Hello World!</seg></tuv><tuv xml:lang=\"fr-fr\"><seg>Bonjour le monde!</seg></tuv></tu></body></tmx>\r";
	
	String simpleBilingualSnippetWithSentence = "<?xml version=\"1.0\"?>\r"
			+ "<tmx version=\"1.4\"><header creationtool=\"undefined_creationtool\" creationtoolversion=\"undefined_creationversion\" segtype=\"undefined_segtype\" o-tmf=\"undefined_unknown\" adminlang=\"undefined_adminlang\" srclang=\"en-us\" datatype=\"unknown\"></header><body><tu tuid=\"tuid_1\" segtype=\"sentence\"><tuv xml:lang=\"en-us\"><seg>Hello World!</seg></tuv><tuv xml:lang=\"fr-fr\"><seg>Bonjour le monde!</seg></tuv></tu></body></tmx>\r";

	String simpleBilingualSnippetWithParagraph = "<?xml version=\"1.0\"?>\r"
			+ "<tmx version=\"1.4\"><header creationtool=\"undefined_creationtool\" creationtoolversion=\"undefined_creationversion\" segtype=\"undefined_segtype\" o-tmf=\"undefined_unknown\" adminlang=\"undefined_adminlang\" srclang=\"en-us\" datatype=\"unknown\"></header><body><tu tuid=\"tuid_1\" segtype=\"paragraph\"><tuv xml:lang=\"en-us\"><seg>Hello World!</seg></tuv><tuv xml:lang=\"fr-fr\"><seg>Bonjour le monde!</seg></tuv></tu></body></tmx>\r";

	String simpleBilingualSnippetWithUnknown = "<?xml version=\"1.0\"?>\r"
			+ "<tmx version=\"1.4\"><header creationtool=\"undefined_creationtool\" creationtoolversion=\"undefined_creationversion\" segtype=\"undefined_segtype\" o-tmf=\"undefined_unknown\" adminlang=\"undefined_adminlang\" srclang=\"en-us\" datatype=\"unknown\"></header><body><tu tuid=\"tuid_1\" segtype=\"undefined\"><tuv xml:lang=\"en-us\"><seg>Hello World!</seg></tuv><tuv xml:lang=\"fr-fr\"><seg>Bonjour le monde!</seg></tuv></tu></body></tmx>\r";

	String simpleBilingualSnippetHeaderSentence = "<?xml version=\"1.0\"?>\r"
			+ "<tmx version=\"1.4\"><header creationtool=\"undefined_creationtool\" creationtoolversion=\"undefined_creationversion\" segtype=\"sentence\" o-tmf=\"undefined_unknown\" adminlang=\"undefined_adminlang\" srclang=\"en-us\" datatype=\"unknown\"></header><body><tu tuid=\"tuid_1\"><tuv xml:lang=\"en-us\"><seg>Hello World!</seg></tuv><tuv xml:lang=\"fr-fr\"><seg>Bonjour le monde!</seg></tuv></tu></body></tmx>\r";

	String simpleBilingualSnippetHeaderParagraph = "<?xml version=\"1.0\"?>\r"
			+ "<tmx version=\"1.4\"><header creationtool=\"undefined_creationtool\" creationtoolversion=\"undefined_creationversion\" segtype=\"paragraph\" o-tmf=\"undefined_unknown\" adminlang=\"undefined_adminlang\" srclang=\"en-us\" datatype=\"unknown\"></header><body><tu tuid=\"tuid_1\"><tuv xml:lang=\"en-us\"><seg>Hello World!</seg></tuv><tuv xml:lang=\"fr-fr\"><seg>Bonjour le monde!</seg></tuv></tu></body></tmx>\r";	
	
	String biHeaderSentenceTuPara = "<?xml version=\"1.0\"?>\r"
			+ "<tmx version=\"1.4\"><header creationtool=\"undefined_creationtool\" creationtoolversion=\"undefined_creationversion\" segtype=\"sentence\" o-tmf=\"undefined_unknown\" adminlang=\"undefined_adminlang\" srclang=\"en-us\" datatype=\"unknown\"></header><body><tu tuid=\"tuid_1\" segtype=\"paragraph\"><tuv xml:lang=\"en-us\"><seg>Hello World!</seg></tuv><tuv xml:lang=\"fr-fr\"><seg>Bonjour le monde!</seg></tuv></tu></body></tmx>\r";

	String biHeaderParaTuSentence = "<?xml version=\"1.0\"?>\r"
			+ "<tmx version=\"1.4\"><header creationtool=\"undefined_creationtool\" creationtoolversion=\"undefined_creationversion\" segtype=\"paragraph\" o-tmf=\"undefined_unknown\" adminlang=\"undefined_adminlang\" srclang=\"en-us\" datatype=\"unknown\"></header><body><tu tuid=\"tuid_1\" segtype=\"sentence\"><tuv xml:lang=\"en-us\"><seg>Hello World!</seg></tuv><tuv xml:lang=\"fr-fr\"><seg>Bonjour le monde!</seg></tuv></tu></body></tmx>\r";	
	
	String tuMissingXmlLangSnippet = "<?xml version=\"1.0\"?>\r"
		+ "<tmx version=\"1.4\"><header creationtool=\"undefined_creationtool\" creationtoolversion=\"undefined_creationversion\" segtype=\"undefined_segtype\" o-tmf=\"undefined_unknown\" adminlang=\"undefined_adminlang\" srclang=\"en-us\" datatype=\"unknown\"></header><body><tu tuid=\"tuid_1\"><tuv><seg>Hello World!</seg></tuv></tu></body></tmx>\r";

	String invalidXmlSnippet = "<?xml version=\"1.0\"?>\r"
		+ "<tmx version=\"1.4\"><header creationtool=\"undefined_creationtool\" creationtoolversion=\"undefined_creationversion\" segtype=\"undefined_segtype\" o-tmf=\"undefined_unknown\" adminlang=\"undefined_adminlang\" srclang=\"en-us\" datatype=\"unknown\"></header><body><tu tuid=\"tuid_1\"><tuv xml:lang=\"en-us\"><seg>Hello World!</seg></tu></body></tmx>\r";

	String emptyTuSnippet = "<?xml version=\"1.0\"?>\r"
		+ "<tmx version=\"1.4\"><header creationtool=\"undefined_creationtool\" creationtoolversion=\"undefined_creationversion\" segtype=\"undefined_segtype\" o-tmf=\"undefined_unknown\" adminlang=\"undefined_adminlang\" srclang=\"en-us\" datatype=\"unknown\"></header><body><tu tuid=\"tuid_1\"></tu></body></tmx>\r";

	String invalidElementsInsideTuSnippet = "<?xml version=\"1.0\"?>\r"
		+ "<tmx version=\"1.4\"><header creationtool=\"undefined_creationtool\" creationtoolversion=\"undefined_creationversion\" segtype=\"undefined_segtype\" o-tmf=\"undefined_unknown\" adminlang=\"undefined_adminlang\" srclang=\"en-us\" datatype=\"unknown\"></header><body><tu tuid=\"tuid_1\"><tuv xml:lang=\"en-us\"><seg>Hello World!</seg></tuv></tu><tu tuid=\"tuid_1\"><tuv xml:lang=\"en-us\"><InvalidTag>Invalid Tag Content</InvalidTag><seg>Hello Universe!</seg></tuv></tu></body></tmx>\r";

	String invalidElementInsidePlaceholderSnippet = "<?xml version=\"1.0\"?>\r"
		+ "<!-- document level comment --><tmx version=\"1.4\"><header creationtool=\"undefined_creationtool\" creationtoolversion=\"undefined_creationversion\" segtype=\"undefined_segtype\" o-tmf=\"undefined_unknown\" adminlang=\"undefined_adminlang\" srclang=\"en-us\" datatype=\"unknown\"></header><body><tu tuid=\"tuid_1\"><note>hello world note</note><tuv xml:lang=\"en-us\"><seg>Hello World!</seg></tuv></tu><tu tuid=\"tuid_1\"><tuv xml:lang=\"en-us\"><seg>Hello <ph type=\"fnote\">Before Sub\"<sub>Hello Subflow. </sub>After <invalid> test invalid placeholder element </invalid> Sub</ph>Universe!</seg></tuv></tu></body></tmx>\r";
	
	String invalidElementInsideSubSnippet = "<?xml version=\"1.0\"?>\r"
		+ "<!-- document level comment --><tmx version=\"1.4\"><header creationtool=\"undefined_creationtool\" creationtoolversion=\"undefined_creationversion\" segtype=\"undefined_segtype\" o-tmf=\"undefined_unknown\" adminlang=\"undefined_adminlang\" srclang=\"en-us\" datatype=\"unknown\"></header><body><tu tuid=\"tuid_1\"><note>hello world note</note><tuv xml:lang=\"en-us\"><seg>Hello World!</seg></tuv></tu><tu tuid=\"tuid_1\"><tuv xml:lang=\"en-us\"><seg>Hello <ph type=\"fnote\">Before Sub\"<sub>Hello <invalid> test invalid sub element </invalid> Subflow. </sub>After Sub</ph>Universe!</seg></tuv></tu></body></tmx>\r";

	String multiTransSnippet = "<?xml version=\"1.0\"?>"
		+ "<tmx version=\"1.4\"><header creationtool=\"x\" creationtoolversion=\"1\" segtype=\"sentence\" o-tmf=\"x\" adminlang=\"en\" srclang=\"en-us\" datatype=\"plaintext\"></header><body><tu>"
		+ "<tuv xml:lang=\"en-us\"><seg>Hello</seg>s</tuv>"
		+ "<tuv xml:lang=\"fr\"><seg>Bonjour</seg></tuv>"
		+ "<tuv xml:lang=\"fr\"><seg>Salut</seg></tuv>"
		+ "<tuv xml:lang=\"de\"><seg>Hallo</seg></tuv>"
		+ "<tuv xml:lang=\"it\"><seg>Buongiorno</seg></tuv>"
		+ "</tu></body></tmx>\r";
	
	String utSnippetInSeg = "<?xml version=\"1.0\"?>\r"
		+ "<tmx version=\"1.4\"><header creationtool=\"undefined_creationtool\" creationtoolversion=\"undefined_creationversion\" segtype=\"undefined_segtype\" o-tmf=\"undefined_unknown\" adminlang=\"undefined_adminlang\" srclang=\"en-us\" datatype=\"unknown\"></header><body><tu tuid=\"tuid_1\"><tuv xml:lang=\"en-us\"><seg>Hello World!</seg></tuv></tu><tu tuid=\"tuid_1\"><tuv xml:lang=\"en-us\"><seg>Hello <ut>Ut Content</ut> Universe!</seg></tuv></tu></body></tmx>\r";

	String utSnippetInSub = "<?xml version=\"1.0\"?>\r"
		+ "<!-- document level comment --><tmx version=\"1.4\"><header creationtool=\"undefined_creationtool\" creationtoolversion=\"undefined_creationversion\" segtype=\"undefined_segtype\" o-tmf=\"undefined_unknown\" adminlang=\"undefined_adminlang\" srclang=\"en-us\" datatype=\"unknown\"></header><body><tu tuid=\"tuid_1\"><note>hello world note</note><tuv xml:lang=\"en-us\"><seg>Hello World!</seg></tuv></tu><tu tuid=\"tuid_1\"><tuv xml:lang=\"en-us\"><seg>Hello <ph type=\"fnote\">Before Sub\"<sub>Hello <ut> ut content </ut> Subflow. </sub>After Sub</ph>Universe!</seg></tuv></tu></body></tmx>\r";

	String utSnippetInHi = "<?xml version=\"1.0\"?>\r"
		+ "<!-- document level comment --><tmx version=\"1.4\"><header creationtool=\"undefined_creationtool\" creationtoolversion=\"undefined_creationversion\" segtype=\"undefined_segtype\" o-tmf=\"undefined_unknown\" adminlang=\"undefined_adminlang\" srclang=\"en-us\" datatype=\"unknown\"></header><body><tu tuid=\"tuid_1\"><note>hello world note</note><tuv xml:lang=\"en-us\"><seg>Hello World!</seg></tuv></tu><tu tuid=\"tuid_1\"><tuv xml:lang=\"en-us\"><seg>Hello <hi type=\"fnote\">Start hi <ut> ut content </ut> End hi.</hi>Universe!</seg></tuv></tu></body></tmx>\r";
	
	@Before
	public void setUp() {
		filter = new TmxFilter();
		testDriver = new FilterTestDriver();
		testDriver.setDisplayLevel(0);
		testDriver.setShowSkeleton(true);
		root = TestUtil.getParentDir(this.getClass(), "/Paragraph_TM.tmx");
	}

	@Test
	public void testTUProperties () {
		String snippet = "<?xml version=\"1.0\"?>\r"
			+ "<tmx version=\"1.4\"><header creationtool=\"z\" creationtoolversion=\"z\" segtype=\"block\" o-tmf=\"z\" adminlang=\"en\" srclang=\"en\" datatype=\"unknown\"></header>"
			+ "<body><tu tuid=\"tuid_1\">"
			+ "<prop type=\"p1\">val1</prop>"
			+ "<tuv xml:lang=\"en\"><seg>Hello World!</seg></tuv></tu></body></tmx>\r";
		ITextUnit tu = FilterTestDriver.getTextUnit(getEvents(snippet, locEN, locFR), 1);
		assertNotNull(tu);
		Property prop = tu.getProperty("p1");
		assertNotNull(prop);
		assertEquals("val1", prop.getValue());
	}
	
	@Test
	public void testDefaultInfo () {
		assertNotNull(filter.getParameters());
		assertNotNull(filter.getName());
		List<FilterConfiguration> list = filter.getConfigurations();
		assertNotNull(list);
		assertTrue(list.size()>0);
	}

	@Test
	public void testGetName() {
		assertEquals("okf_tmx", filter.getName());
	}

	@Test
	public void testGetMimeType() {
		assertEquals(MimeTypeMapper.TMX_MIME_TYPE, filter.getMimeType());
	}	

	@Test
	public void testLang11 () {
		String snippet = "<?xml version=\"1.0\"?>\r"
			+ "<tmx version=\"1.1\"><header creationtool=\"undefined_creationtool\" creationtoolversion=\"undefined_creationversion\" segtype=\"undefined_segtype\" o-tmf=\"undefined_unknown\" adminlang=\"undefined_adminlang\" srclang=\"en-us\" datatype=\"unknown\"></header><body>"
			+ "<tu tuid=\"tuid_1\"><tuv lang=\"en-us\"><seg>Hello World!</seg></tuv><tuv lang=\"fr-fr\"><seg>Bonjour le monde!</seg></tuv></tu></body></tmx>\r";
		ITextUnit tu = FilterTestDriver.getTextUnit(getEvents(snippet, locENUS, locFRFR), 1);
		assertNotNull(tu);
		assertTrue(tu.hasTarget(locFRFR));
	}

	@Test
	public void testSpecialChars () {
		String snippet = "<?xml version=\"1.0\"?>\r"
			+ "<tmx version=\"1.4\"><header creationtool=\"undefined_creationtool\" creationtoolversion=\"undefined_creationversion\" segtype=\"undefined_segtype\" o-tmf=\"undefined_unknown\" adminlang=\"undefined_adminlang\" srclang=\"en-us\" datatype=\"unknown\"></header>\r<body>\r"
			+ "<tu tuid=\"tuid_1\">\r"
			+ "<prop type=\"p2\">val2</prop>\r"
			+ "<prop type=\"p1\">val1</prop>\r"
			+ "<prop type=\"p4\">val4</prop>\r"
			+ "<prop type=\"p3\">val3</prop>\r"
			+ "<tuv xml:lang=\"en-us\">\r<seg>&amp;&lt;&quot;&apos;</seg>\r</tuv>\r<tuv xml:lang=\"fr-fr\">\r<seg>&amp;&lt;&quot;&apos;</seg>\r</tuv>\r</tu>\r"
			+ "<tu>\r<tuv xml:lang=\"en-us\">\r<seg>t2</seg>\r</tuv>\r<tuv xml:lang=\"fr-fr\">\r<seg>t2-fr</seg>\r</tuv>\r</tu>"
			+ "</body>\r</tmx>";
		String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
			+ "<tmx version=\"1.4\"><header creationtool=\"undefined_creationtool\" creationtoolversion=\"undefined_creationversion\" segtype=\"undefined_segtype\" o-tmf=\"undefined_unknown\" adminlang=\"undefined_adminlang\" srclang=\"en-us\" datatype=\"unknown\"></header>\r<body>\r"
			+ "<tu tuid=\"tuid_1\">\r"
			+ "<prop type=\"p2\">val2</prop>\r"
			+ "<prop type=\"p1\">val1</prop>\r"
			+ "<prop type=\"p4\">val4</prop>\r"
			+ "<prop type=\"p3\">val3</prop>\r"
			+ "<tuv xml:lang=\"en-us\">\r<seg>&amp;&lt;&quot;&apos;</seg>\r</tuv>\r<tuv xml:lang=\"fr-fr\">\r<seg>&amp;&lt;&quot;&apos;</seg>\r</tuv>\r</tu>\r"
			+ "<tu>\r<tuv xml:lang=\"en-us\">\r<seg>t2</seg>\r</tuv>\r<tuv xml:lang=\"fr-fr\">\r<seg>t2-fr</seg>\r</tuv>\r</tu>"
			+ "</body>\r</tmx>";
		
		assertEquals(expected, FilterTestDriver.generateOutput(getEvents(snippet, locENUS, locFRFR),
			filter.getEncoderManager(), locFRFR));
	}
	
	@Test
	public void testLineBreaks () {
		String snippet = "<?xml version=\"1.0\"?>\r"
			+ "<tmx version=\"1.4\"><header creationtool=\"undefined_creationtool\" creationtoolversion=\"undefined_creationversion\" segtype=\"undefined_segtype\" o-tmf=\"undefined_unknown\" adminlang=\"undefined_adminlang\" srclang=\"en-us\" datatype=\"unknown\"></header>\r<body>\r"
			+ "<tu tuid=\"tuid_1\">\r<tuv xml:lang=\"en-us\">\r<seg>Hello World!</seg>\r</tuv>\r<tuv xml:lang=\"fr-fr\">\r<seg>Bonjour le monde!</seg>\r</tuv>\r</tu>\r"
			+ "<tu>\r<tuv xml:lang=\"en-us\">\r<seg>t2</seg>\r</tuv>\r<tuv xml:lang=\"fr-fr\">\r<seg>t2-fr</seg>\r</tuv>\r</tu>\r"
			+ "</body>\r</tmx>";
		String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
			+ "<tmx version=\"1.4\"><header creationtool=\"undefined_creationtool\" creationtoolversion=\"undefined_creationversion\" segtype=\"undefined_segtype\" o-tmf=\"undefined_unknown\" adminlang=\"undefined_adminlang\" srclang=\"en-us\" datatype=\"unknown\"></header>\r<body>\r"
			+ "<tu tuid=\"tuid_1\">\r<tuv xml:lang=\"en-us\">\r<seg>Hello World!</seg>\r</tuv>\r<tuv xml:lang=\"fr-fr\">\r<seg>Bonjour le monde!</seg>\r</tuv>\r</tu>\r"
			+ "<tu>\r<tuv xml:lang=\"en-us\">\r<seg>t2</seg>\r</tuv>\r<tuv xml:lang=\"fr-fr\">\r<seg>t2-fr</seg>\r</tuv>\r</tu>\r"
			+ "</body>\r</tmx>";
		
		assertEquals(expected, FilterTestDriver.generateOutput(getEvents(snippet, locENUS, locFRFR),
			filter.getEncoderManager(), locFRFR));
	}
	
	@Test
	public void testXmlLangOverLang () {
		String snippet = "<?xml version=\"1.0\"?>\r"
			+ "<tmx version=\"1.1\"><header creationtool=\"undefined_creationtool\" creationtoolversion=\"undefined_creationversion\" segtype=\"undefined_segtype\" o-tmf=\"undefined_unknown\" adminlang=\"undefined_adminlang\" srclang=\"en-us\" datatype=\"unknown\"></header><body>"
			+ "<tu tuid=\"tuid_1\"><tuv xml:lang=\"en-us\" lang=\"de-de\"><seg>Hello World!</seg></tuv><tuv lang=\"it-it\" xml:lang=\"fr-fr\"><seg>Bonjour le monde!</seg></tuv></tu></body></tmx>\r";
		ITextUnit tu = FilterTestDriver.getTextUnit(getEvents(snippet, locENUS, locFRFR), 1);
		assertNotNull(tu);
		assertTrue(tu.hasTarget(locFRFR));
	}
	
	@Test
	public void testEscapes () {
		String snippet = "<?xml version=\"1.0\"?>\r"
			+ "<tmx version=\"1.1\"><header creationtool=\"undefined_creationtool\" creationtoolversion=\"undefined_creationversion\" segtype=\"undefined_segtype\" o-tmf=\"undefined_unknown\" adminlang=\"undefined_adminlang\" srclang=\"en-us\" datatype=\"unknown\"></header><body>"
			+ "<tu tuid=\"tuid_1\"><tuv xml:lang=\"en-us\"><seg>\"\'&amp;&lt;></seg></tuv><tuv xml:lang=\"fr-fr\"><seg>\"\'&amp;&lt;></seg></tuv></tu></body></tmx>\r";
		String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><tmx version=\"1.1\">"
			+ "<header creationtool=\"undefined_creationtool\" creationtoolversion=\"undefined_creationversion\" segtype=\"undefined_segtype\" o-tmf=\"undefined_unknown\" adminlang=\"undefined_adminlang\" srclang=\"en-us\" datatype=\"unknown\"></header><body>"
			+ "<tu tuid=\"tuid_1\"><tuv xml:lang=\"en-us\"><seg>&quot;&apos;&amp;&lt;></seg></tuv><tuv xml:lang=\"fr-fr\"><seg>&quot;&apos;&amp;&lt;></seg></tuv></tu></body></tmx>";
		assertEquals(expected, FilterTestDriver.generateOutput(getEvents(snippet, locENUS, locFRFR),
			filter.getEncoderManager(), locFRFR));
	}
	
	@Test
	public void testCancel() {
		Event event;
		filter.open(new RawDocument(simpleSnippet,locENUS,locFRFR));			
		while (filter.hasNext()) {
			event = filter.next();
			if (event.getEventType() == EventType.START_DOCUMENT) {
				assertTrue(event.getResource() instanceof StartDocument);
			} else if (event.getEventType() == EventType.TEXT_UNIT) {
				//--cancel after first text unit--
				filter.cancel();
				assertTrue(event.getResource() instanceof ITextUnit);
			} else if (event.getEventType() == EventType.DOCUMENT_PART) {
				assertTrue(event.getResource() instanceof DocumentPart);
			} 
		}
		
		event = filter.next();
		assertEquals(EventType.CANCELED, event.getEventType());
		filter.close();		
		
	}	
	
	//--exceptions--
	@Test (expected=NullPointerException.class)
	public void testSourceLangNotSpecified() {
		FilterTestDriver.getStartDocument(getEvents(simpleSnippet, null));
	}

	@Test (expected=NullPointerException.class)
	public void testTargetLangNotSpecified() {
		FilterTestDriver.getStartDocument(getEvents(simpleSnippet, locENUS));
	}

	@Test (expected=NullPointerException.class)
	public void testTargetLangNotSpecified2() {
		FilterTestDriver.getStartDocument(getEvents(simpleSnippet, locENUS, null));
	}
	
	@Test (expected=NullPointerException.class)
	public void testSourceLangEmpty() {
		FilterTestDriver.getStartDocument(getEvents(simpleSnippet, LocaleId.EMPTY,locFRFR));
	}	
	
	@Test (expected=NullPointerException.class)
	public void testTargetLangEmpty() {
		FilterTestDriver.getStartDocument(getEvents(simpleSnippet, locENUS,LocaleId.EMPTY));
	}	
	
	@Test (expected=OkapiBadFilterInputException.class)
	public void testTuXmlLangMissing() {
		FilterTestDriver.getStartDocument(getEvents(tuMissingXmlLangSnippet, locENUS,locFRFR));
	}
	
	@Test (expected=OkapiIOException.class)
	public void testInvalidXml() {
		FilterTestDriver.getStartDocument(getEvents(invalidXmlSnippet, locENUS,locFRFR));
	}

	@Test (expected=OkapiBadFilterInputException.class)
	public void testEmptyTu() {
		FilterTestDriver.getStartDocument(getEvents(emptyTuSnippet, locENUS,locFRFR));
	}

	@Test (expected=OkapiBadFilterInputException.class)
	public void testInvalidElementInTu() {
		
		Parameters p = (Parameters) filter.getParameters();
		p.setExitOnInvalid(true);
		FilterTestDriver.getStartDocument(getEvents(invalidElementsInsideTuSnippet, locENUS,locFRFR));
	}
	
	@Test (expected=OkapiBadFilterInputException.class)
	public void testInvalidElementInSub() {
		FilterTestDriver.getStartDocument(getEvents(invalidElementInsideSubSnippet, locENUS,locFRFR));
	}

	@Test (expected=OkapiBadFilterInputException.class)
	public void testInvalidElementInPlaceholder() {
		FilterTestDriver.getStartDocument(getEvents(invalidElementInsidePlaceholderSnippet, locENUS,locFRFR));
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testOpenInvalidInputStream() {
		InputStream nullStream=null;
		filter.open(new RawDocument(nullStream,"UTF-8",locENUS,locFRFR));			
		if ( !testDriver.process(filter) ) Assert.fail();
		filter.close();	
	}
	
	@Test (expected=OkapiIOException.class)
	public void testOpenInvalidUri() throws Exception{
		String basePath = TmxFilterTest.class.getResource("/Paragraph_TM.tmx").getPath();
		basePath = "file://"+basePath.replace("/bin/Paragraph_TM.tmx","");

		URI invalid_uri = new URI(basePath+"/invalid_filename.tmx");
		filter.open(new RawDocument(invalid_uri,"UTF-8",locENUS,locFRFR));			
		if ( !testDriver.process(filter) ) Assert.fail();
		filter.close();	
	}
	
	@Test
	public void testInputStream() {
		InputStream htmlStream = TmxFilterTest.class.getResourceAsStream("/Paragraph_TM.tmx");
		filter.open(new RawDocument(htmlStream, "UTF-8", locENUS,locFRFR));
		if ( !testDriver.process(filter) ) Assert.fail();
		filter.close();
	}	

	@Test
	public void testConsolidatedStream() {
		filter.open(new RawDocument(simpleSnippet, locENUS,locFRFR));
		if ( !testDriver.process(filter) ) Assert.fail();
		filter.close();
		//System.out.println(FilterTestDriver.generateOutput(getEvents(simpleSnippet, locENUS,locFRFR), simpleSnippet, locFRFR));
	}	

	@Test
	public void testOutputWithLT () {
		String snippet = "<?xml version=\"1.0\"?>\r"
			+ "<tmx version=\"1.1\"><header creationtool=\"undefined_creationtool\" creationtoolversion=\"undefined_creationversion\" segtype=\"undefined_segtype\" o-tmf=\"undefined_unknown\" adminlang=\"undefined_adminlang\" srclang=\"en-us\" datatype=\"unknown\"></header><body>"
			+ "<tu tuid=\"1\" creationtool=\"abc&lt;&apos;>\">"
			+ "<tuv xml:lang=\"en-US\"><seg>a<ph id='1' x=\"&lt;codeph class=&quot;+ topic/ph pr-d/codeph &quot;&gt;\">&lt;code></ph>b</seg></tuv></tu>"
			+ "</body></tmx>";
		String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
			+ "<tmx version=\"1.1\"><header creationtool=\"undefined_creationtool\" creationtoolversion=\"undefined_creationversion\" segtype=\"undefined_segtype\" o-tmf=\"undefined_unknown\" adminlang=\"undefined_adminlang\" srclang=\"en-us\" datatype=\"unknown\"></header><body>"
			+ "<tu tuid=\"1\" creationtool=\"abc&lt;'>\">"
			+ "<tuv xml:lang=\"en-US\"><seg>a<ph id=\"1\" x=\"&lt;codeph class=&quot;+ topic/ph pr-d/codeph &quot;>\">&lt;code></ph>b</seg></tuv>"
			+ "<tuv xml:lang=\"fr-fr\"><seg>a<ph id=\"1\" x=\"&lt;codeph class=&quot;+ topic/ph pr-d/codeph &quot;>\">&lt;code></ph>b</seg></tuv>\r"
			+ "</tu></body></tmx>";
		assertEquals(expected, FilterTestDriver.generateOutput(getEvents(snippet,locENUS, locFRFR),
			filter.getEncoderManager(), locFR));
	}		
	
	@Test
	public void testUnConsolidatedStream() {
		Parameters params = (Parameters)filter.getParameters();
		params.consolidateDpSkeleton=false;
		
		filter.open(new RawDocument(simpleSnippet, locENUS,locFRFR));
		if ( !testDriver.process(filter) ) Assert.fail();
		filter.close();
		//System.out.println(FilterTestDriver.generateOutput(getEvents(simpleSnippet, locENUS,locFRFR), simpleSnippet, locFRFR));
	}	
	
	/*
	@Test
	public void testOutputBasic_Comment () {
		assertEquals(simpleBilingualSnippet, FilterTestDriver.generateOutput(getEvents(simpleBilingualSnippet,locENUS,locFRFR), simpleSnippet, locFRFR));
		System.out.println(FilterTestDriver.generateOutput(getEvents(simpleBilingualSnippet,locENUS,locFRFR), simpleSnippet, "en"));
	}*/	
	
	@Test
	public void testStartDocument () {
		assertTrue("Problem in StartDocument", FilterTestDriver.testStartDocument(filter,
			new InputDocument(root+"Paragraph_TM.tmx", null),
			"UTF-8", locEN, locEN));
	}
	
	@Test
	public void testStartDocumentFromList () {
		StartDocument sd = FilterTestDriver.getStartDocument(getEvents(simpleSnippet, locENUS,locFRFR));
		assertNotNull(sd);
		assertNotNull(sd.getEncoding());
		assertNotNull(sd.getType());
		assertNotNull(sd.getMimeType());
		assertNotNull(sd.getLocale());
		assertEquals("\r", sd.getLineBreak());
	}
	
	@Test
	public void testDTDHandling () {
		ITextUnit tu = FilterTestDriver.getTextUnit(
			getEvents(simpleSnippetWithDTD, locENUS, locFRFR), 2);
		assertNotNull(tu);
		assertEquals("Hello Universe!", tu.getSource().getFirstContent().toText());
	}
	
	@Test
	public void testSegTypeSentence () {
		
		Parameters params = new Parameters();
		params.setSegType(TmxFilter.SEGTYPE_SENTENCE);
		filter.setParameters(params);
		
		ITextUnit tu = FilterTestDriver.getTextUnit(getEvents(simpleBilingualSnippetWithSentence, locENUS,locFRFR), 1);
		assertEquals(tu.getSource().getSegments().getAlignmentStatus(), AlignmentStatus.ALIGNED);
		assertTrue(tu.getSource().hasBeenSegmented());
		assertEquals(tu.getTarget(locFRFR).getSegments().getAlignmentStatus(), AlignmentStatus.ALIGNED);
		assertTrue(tu.getTarget(locFRFR).hasBeenSegmented());
	}
	
	@Test
	public void testSegTypePara () {
		
		Parameters params = new Parameters();
		params.setSegType(TmxFilter.SEGTYPE_PARA);
		filter.setParameters(params);
		
		ITextUnit tu = FilterTestDriver.getTextUnit(getEvents(simpleBilingualSnippetWithParagraph, locENUS,locFRFR), 1);
		assertEquals(tu.getSource().getSegments().getAlignmentStatus(), AlignmentStatus.NOT_ALIGNED);
		assertTrue(!tu.getSource().hasBeenSegmented());
		assertEquals(tu.getTarget(locFRFR).getSegments().getAlignmentStatus(), AlignmentStatus.NOT_ALIGNED);
		assertTrue(!tu.getTarget(locFRFR).hasBeenSegmented());

	}
	
	@Test
	public void testSegTypeOrSentence() {
		
		Parameters params = new Parameters();
		params.setSegType(TmxFilter.SEGTYPE_OR_SENTENCE);
		filter.setParameters(params);
		
		ITextUnit tu = FilterTestDriver.getTextUnit(getEvents(simpleBilingualSnippetWithSentence, locENUS,locFRFR), 1);
		assertEquals(tu.getSource().getSegments().getAlignmentStatus(), AlignmentStatus.ALIGNED);
		assertTrue(tu.getSource().hasBeenSegmented());
		assertEquals(tu.getTarget(locFRFR).getSegments().getAlignmentStatus(), AlignmentStatus.ALIGNED);
		assertTrue(tu.getTarget(locFRFR).hasBeenSegmented());
	}
	
	@Test
	public void testSegTypeOrParagraph() {
		
		Parameters params = new Parameters();
		params.setSegType(TmxFilter.SEGTYPE_OR_PARA);
		filter.setParameters(params);
		
		ITextUnit tu = FilterTestDriver.getTextUnit(getEvents(simpleBilingualSnippetWithParagraph, locENUS,locFRFR), 1);
		assertEquals(tu.getSource().getSegments().getAlignmentStatus(), AlignmentStatus.NOT_ALIGNED);
		assertTrue(!tu.getSource().hasBeenSegmented());
		assertEquals(tu.getTarget(locFRFR).getSegments().getAlignmentStatus(), AlignmentStatus.NOT_ALIGNED);
		assertTrue(!tu.getTarget(locFRFR).hasBeenSegmented());
	}
	
	@Test
	public void testSegTypeOrSentenceDefault() {
		
		Parameters params = new Parameters();
		params.setSegType(TmxFilter.SEGTYPE_OR_SENTENCE);
		filter.setParameters(params);
		
		ITextUnit tu = FilterTestDriver.getTextUnit(getEvents(simpleBilingualSnippet, locENUS,locFRFR), 1);
		assertEquals(tu.getSource().getSegments().getAlignmentStatus(), AlignmentStatus.NOT_ALIGNED);
		assertTrue(!tu.getSource().hasBeenSegmented());
		assertEquals(tu.getTarget(locFRFR).getSegments().getAlignmentStatus(), AlignmentStatus.NOT_ALIGNED);
		assertTrue(!tu.getTarget(locFRFR).hasBeenSegmented());
	}
	
	@Test
	public void testSegTypeOrParagraphDefault() {
		
		Parameters params = new Parameters();
		params.setSegType(TmxFilter.SEGTYPE_OR_PARA);
		filter.setParameters(params);
		
		ITextUnit tu = FilterTestDriver.getTextUnit(getEvents(simpleBilingualSnippet, locENUS,locFRFR), 1);
		assertEquals(tu.getSource().getSegments().getAlignmentStatus(), AlignmentStatus.NOT_ALIGNED);
		assertTrue(!tu.getSource().hasBeenSegmented());
		assertEquals(tu.getTarget(locFRFR).getSegments().getAlignmentStatus(), AlignmentStatus.NOT_ALIGNED);
		assertTrue(!tu.getTarget(locFRFR).hasBeenSegmented());
	}
	
	@Test
	public void testSegTypeOrSentenceUnknown() {
		
		Parameters params = new Parameters();
		params.setSegType(TmxFilter.SEGTYPE_OR_SENTENCE);
		filter.setParameters(params);
		
		ITextUnit tu = FilterTestDriver.getTextUnit(getEvents(simpleBilingualSnippetWithUnknown, locENUS,locFRFR), 1);
		assertEquals(tu.getSource().getSegments().getAlignmentStatus(), AlignmentStatus.NOT_ALIGNED);
		assertTrue(!tu.getSource().hasBeenSegmented());
		assertEquals(tu.getTarget(locFRFR).getSegments().getAlignmentStatus(), AlignmentStatus.NOT_ALIGNED);
		assertTrue(!tu.getTarget(locFRFR).hasBeenSegmented());
	}
	
	@Test
	public void testSegTypeOrParagraphUnknown() {
		
		Parameters params = new Parameters();
		params.setSegType(TmxFilter.SEGTYPE_OR_PARA);
		filter.setParameters(params);
		
		ITextUnit tu = FilterTestDriver.getTextUnit(getEvents(simpleBilingualSnippet, locENUS,locFRFR), 1);
		assertEquals(tu.getSource().getSegments().getAlignmentStatus(), AlignmentStatus.NOT_ALIGNED);
		assertTrue(!tu.getSource().hasBeenSegmented());
		assertEquals(tu.getTarget(locFRFR).getSegments().getAlignmentStatus(), AlignmentStatus.NOT_ALIGNED);
		assertTrue(!tu.getTarget(locFRFR).hasBeenSegmented());
	}
	
	@Test
	public void testSegTypeHeaderSentence() {
		
		Parameters params = new Parameters();
		params.setSegType(TmxFilter.SEGTYPE_OR_PARA);
		filter.setParameters(params);
		
		ITextUnit tu = FilterTestDriver.getTextUnit(getEvents(simpleBilingualSnippetHeaderSentence, locENUS,locFRFR), 1);
		assertEquals(tu.getSource().getSegments().getAlignmentStatus(), AlignmentStatus.ALIGNED);
		assertTrue(tu.getSource().hasBeenSegmented());
		assertEquals(tu.getTarget(locFRFR).getSegments().getAlignmentStatus(), AlignmentStatus.ALIGNED);
		assertTrue(tu.getTarget(locFRFR).hasBeenSegmented());
	}
	
	@Test
	public void testSegTypeHeaderParagraph() {
		
		Parameters params = new Parameters();
		params.setSegType(TmxFilter.SEGTYPE_OR_SENTENCE);
		filter.setParameters(params);
		
		ITextUnit tu = FilterTestDriver.getTextUnit(getEvents(simpleBilingualSnippetHeaderParagraph, locENUS,locFRFR), 1);
		assertEquals(tu.getSource().getSegments().getAlignmentStatus(), AlignmentStatus.NOT_ALIGNED);
		assertTrue(!tu.getSource().hasBeenSegmented());
		assertEquals(tu.getTarget(locFRFR).getSegments().getAlignmentStatus(), AlignmentStatus.NOT_ALIGNED);
		assertTrue(!tu.getTarget(locFRFR).hasBeenSegmented());
	}
	
	@Test
	public void testSegTypeHeaderSentenceOverwrite() {
		
		Parameters params = new Parameters();
		params.setSegType(TmxFilter.SEGTYPE_OR_SENTENCE);
		filter.setParameters(params);
		
		ITextUnit tu = FilterTestDriver.getTextUnit(getEvents(biHeaderSentenceTuPara, locENUS,locFRFR), 1);
		assertEquals(tu.getSource().getSegments().getAlignmentStatus(), AlignmentStatus.NOT_ALIGNED);
		assertTrue(!tu.getSource().hasBeenSegmented());
		assertEquals(tu.getTarget(locFRFR).getSegments().getAlignmentStatus(), AlignmentStatus.NOT_ALIGNED);
		assertTrue(!tu.getTarget(locFRFR).hasBeenSegmented());
	}
	
	@Test
	public void testSegTypeHeaderParagraphOverwrite() {
		
		Parameters params = new Parameters();
		params.setSegType(TmxFilter.SEGTYPE_OR_PARA);
		filter.setParameters(params);
		
		ITextUnit tu = FilterTestDriver.getTextUnit(getEvents(biHeaderParaTuSentence, locENUS,locFRFR), 1);
		assertEquals(tu.getSource().getSegments().getAlignmentStatus(), AlignmentStatus.ALIGNED);
		assertTrue(tu.getSource().hasBeenSegmented());
		assertEquals(tu.getTarget(locFRFR).getSegments().getAlignmentStatus(), AlignmentStatus.ALIGNED);
		assertTrue(tu.getTarget(locFRFR).hasBeenSegmented());
	}
	
	
	@Test
	public void testSimpleTransUnit () {
		ITextUnit tu = FilterTestDriver.getTextUnit(getEvents(simpleSnippet, locENUS,locFRFR), 1);
		assertNotNull(tu);
		assertEquals("Hello World!", tu.getSource().toString());
		assertEquals("tuid_1", tu.getName());
	}
	
	@Test
	public void testMulipleTargets () {
		ArrayList<Event> events = getEvents(multiTransSnippet, locENUS, locFR);

		ITextUnit tu = FilterTestDriver.getTextUnit(events, 1);
		assertNotNull(tu);
		assertEquals("Hello", tu.getSource().toString());
		assertEquals(3, tu.getTargetLocales().size());
		assertTrue(tu.hasTarget(locFR));
		assertEquals("Bonjour", tu.getTarget(locFR).toString());
		assertTrue(tu.hasTarget(locDE));
		assertEquals("Hallo", tu.getTarget(locDE).toString());
		assertTrue(tu.hasTarget(locIT));
		assertEquals("Buongiorno", tu.getTarget(locIT).toString());

		tu = FilterTestDriver.getTextUnit(events, 2);
		assertNotNull(tu);
		assertEquals("Hello", tu.getSource().toString());
		assertEquals(1, tu.getTargetLocales().size());
		assertTrue(tu.hasTarget(locFR));
		assertEquals("Salut", tu.getTarget(locFR).toString());
	}
	
	@Test
	public void testUtInSeg () {
		FilterTestDriver.getStartDocument(getEvents(utSnippetInSeg, locENUS,locFRFR));
	}

	@Test
	public void testUtInSub () {
		FilterTestDriver.getStartDocument(getEvents(utSnippetInSub, locENUS,locFRFR));
	}

	@Test
	public void testUtInHi () {
		FilterTestDriver.getStartDocument(getEvents(utSnippetInHi, locENUS,locFRFR));
	}

		

	
/*	@Test
	public void runTest () {
		FilterTestDriver testDriver = new FilterTestDriver();
		TmxFilter filter = null;		
		try {
			filter = new TmxFilter();
			URL url = TmxFilterTest.class.getResource("/ImportTest2A.tmx");
			filter.open(new RawDocument(new URI(url.toString()), "UTF-8", locENUS, "FR-CA"));			
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
	}	*/
	
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
	
	
	
	private ArrayList<Event> getEvents(String snippet, LocaleId srcLang, LocaleId trgLang){
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
	private ArrayList<Event> getEvents(String snippet, LocaleId srcLang){
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
		ArrayList<InputDocument> list = new ArrayList<InputDocument>();
		list.add(new InputDocument(root+"Paragraph_TM.tmx", null));

		RoundTripComparison rtc = new RoundTripComparison();
		assertTrue(rtc.executeCompare(filter, list, "UTF-8", locENUS, locFRFR));
	}	
	
	@Test
	public void testDoubleExtractionCompKit () throws URISyntaxException {
		ArrayList<InputDocument> list = new ArrayList<InputDocument>();
		RoundTripComparison rtc = new RoundTripComparison();

		list.add(new InputDocument(root+"compkit/ExportTest1A.tmx", null));
		list.add(new InputDocument(root+"compkit/ExportTest1B.tmx", null));
		list.add(new InputDocument(root+"compkit/ExportTest2A.tmx", null));
		list.add(new InputDocument(root+"compkit/ImportTest1A.tmx", null));
		list.add(new InputDocument(root+"compkit/ImportTest1B.tmx", null));
		list.add(new InputDocument(root+"compkit/ImportTest1C.tmx", null));
		list.add(new InputDocument(root+"compkit/ImportTest2A.tmx", null));
		list.add(new InputDocument(root+"compkit/ImportTest2B.tmx", null));
		assertTrue(rtc.executeCompare(filter, list, "UTF-8", locENUS, locFRCA));

		list.clear();
		list.add(new InputDocument(root+"compkit/ImportTest1D.tmx", null));
		list.add(new InputDocument(root+"compkit/ImportTest1H.tmx", null));
		list.add(new InputDocument(root+"compkit/ImportTest1L.tmx", null));
		assertTrue(rtc.executeCompare(filter, list, "UTF-8", locENUS, locENGB));

		list.clear();
		list.add(new InputDocument(root+"compkit/ImportTest1I.tmx", null));
		assertTrue(rtc.executeCompare(filter, list, "UTF-8", locENUS, locJAJP));
	}
	
	@Test
	public void testTUTUVAttrEscaping () {
		String snippet = "<?xml version=\"1.0\"?>\r"
			+ "<tmx version=\"1.4\"><header creationtool=\"z\" creationtoolversion=\"z\" segtype=\"block\" o-tmf=\"z\" adminlang=\"en\" srclang=\"en\" datatype=\"unknown\"></header>"
			+ "<body><tu tuid=\"tuid_1\" foo=\"&lt;VALUE1&amp;VALUE2\">"
			+ "<prop type=\"p1\">val1</prop>"
			+ "<tuv xml:lang=\"en\" foo=\"&lt;VALUE1&amp;VALUE2;\"><seg>Hello World!</seg></tuv></tu></body></tmx>\r";
		ITextUnit tu = FilterTestDriver.getTextUnit(getEvents(snippet, locEN, locFR), 1);
		assertNotNull(tu);
		// We should not find invalid skeleton content.  The skeleton content 
		// includes both the TU and TUV attribute values, so the easiest way to
		// check this is by making sure unescaped stuff didn't sneak in.
		assertTrue(tu.getSkeleton().toString().indexOf("<VALUE1") == -1);
		assertTrue(tu.getSkeleton().toString().indexOf("&VALUE2") == -1);
	}
	
}
