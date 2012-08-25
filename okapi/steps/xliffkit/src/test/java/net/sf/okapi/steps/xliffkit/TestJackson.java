/*===========================================================================
  Copyright (C) 2009-2011 by the Okapi Framework contributors
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

package net.sf.okapi.steps.xliffkit;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import net.sf.okapi.common.Event;
import net.sf.okapi.common.EventType;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.resource.ITextUnit;
import net.sf.okapi.common.resource.RawDocument;
import net.sf.okapi.common.resource.Segment;
import net.sf.okapi.common.resource.TextContainer;
import net.sf.okapi.common.resource.TextFragment;
import net.sf.okapi.common.resource.TextUnitUtil;
import net.sf.okapi.common.skeleton.ZipSkeleton;
import net.sf.okapi.lib.beans.v1.EventBean;
import net.sf.okapi.lib.beans.v1.InputStreamBean;
import net.sf.okapi.lib.beans.v1.TextUnitBean;
import net.sf.okapi.lib.beans.v1.ZipSkeletonBean;
import net.sf.okapi.lib.beans.sessions.OkapiJsonSession;
import org.apache.commons.io.input.CountingInputStream;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonParser.Feature;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.junit.Before;
import org.junit.Test;

public class TestJackson {

//	private static final String fileName = "test3.txt";
	private ObjectMapper mapper;
	private OkapiJsonSession session;
	
	@Before
	public void setUp() {
		mapper = new ObjectMapper();
		
		mapper.configure(SerializationConfig.Feature.INDENT_OUTPUT, true); 
		mapper.configure(SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS, false);
		mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		//mapper.configure(DeserializationConfig.Feature.USE_ANNOTATIONS, false);
		mapper.configure(Feature.AUTO_CLOSE_SOURCE, false);
		mapper.configure(JsonGenerator.Feature.AUTO_CLOSE_TARGET, false);
		session = new OkapiJsonSession();
	}

	@Test // Make sure we have at least one test to avoid build errors
	public void testDescription () {
		session.setDescription("abc");
		assertEquals("abc", session.getDescription());
	}
	
	private void log(String str) {
		Logger logger = LoggerFactory.getLogger(getClass().getName()); // loggers are cached
		logger.debug(str);
	}
	
	// DEBUG @Test
	public void testTextUnit() throws JsonGenerationException, JsonMappingException, IOException, URISyntaxException {
		Event event = new Event(EventType.TEXT_UNIT);
		//TextUnit tu = TextUnitUtil.buildTU("source", "skeleton");
		ITextUnit tu = TextUnitUtil.buildTU("source-text" + (char) 2 + '"' + " : " + '"' + 
				'{' + '"' + "ssssss " + ':' + '"' + "ddddd" + "}:" + '<' + '>' + "sssddd: <>dsdd");
		tu.setSkeleton(new ZipSkeleton(null, new ZipEntry("")));
		event.setResource(tu);
		tu.setTarget(LocaleId.FRENCH, new TextContainer("french-text"));
		tu.setTarget(LocaleId.TAIWAN_CHINESE, new TextContainer("chinese-text"));

		//FileOutputStream output = new FileOutputStream(new File(this.getClass().getResource(fileName).toURI()));
//		mapper.writeValue(output, event);
//		output.close();
		
//		// Use JAXB annotations
//		AnnotationIntrospector introspector = new JaxbAnnotationIntrospector();
//		mapper.getSerializationConfig().setAnnotationIntrospector(introspector);
//		
//		GenericSkeleton skel = (GenericSkeleton) tu.getSkeleton();
//		System.out.println(mapper.writeValueAsString(skel));
		TextUnitBean tub = new TextUnitBean();
		tub.set(tu, session);
		
		EventBean evb = new EventBean();
		evb.set(event, session);
		//mapper.getDeserializationConfig().addHandler(new TestResolver());
		
		//String st = mapper.writeValueAsString(tub);
		String st = mapper.writeValueAsString(evb);
		log(st);
		
//		String st2 = mapper.writeValueAsString(evb);
//		System.out.println(st2);
		
		st = mapper.writeValueAsString(tub);
		tub = mapper.readValue(st, TextUnitBean.class);
		tu = tub.get(ITextUnit.class, session);
//		System.out.println(tu.getSource().getCodedText());
		log(((TextContainer)tub.getSource().get(TextContainer.class, session)).getCodedText());
//		ISkeleton skel = tub.getSkeleton().read(ISkeleton.class);
//		if (skel != null)
//			System.out.println(skel.getClass());
		//System.out.println(tub.getSkeleton().getContent().getClass().getName());
		
//		ZipSkeletonBean zsb = new ZipSkeletonBean(); 
//		st = mapper.writeValueAsString(zsb);
//		zsb = mapper.readValue(st, ZipSkeletonBean.class);
		
		//ZipSkeletonBean zsb = mapper.readValue(st, ZipSkeletonBean.class);
	}
	
	
	
	// DEBUG @Test
	public void testRawDocument() throws JsonGenerationException, JsonMappingException, IOException {
		Event event = new Event(EventType.RAW_DOCUMENT);
		event.setResource(new RawDocument("raw doc", LocaleId.ENGLISH));
		EventBean evb = new EventBean();
		evb.set(event, session);
		//mapper.getDeserializationConfig().addHandler(new TestResolver());
		
		//String st = mapper.writeValueAsString(tub);
		String st = mapper.writeValueAsString(evb);
		log(st);
	}
	
	// DEBUG @Test
	public void testMultipleRead1() throws IOException {
		OkapiJsonSession skelSession = new OkapiJsonSession();
		
		File tempSkeleton = null;
		tempSkeleton = File.createTempFile("~aaa", ".txt");
		tempSkeleton.deleteOnExit();
		
		skelSession.start(new FileOutputStream(tempSkeleton));
		String st1 = "string1";
		String st2 = "string2";
		skelSession.serialize(st1);
		skelSession.serialize(st2);
		skelSession.end();
		
		FileInputStream fis = new FileInputStream(tempSkeleton);
		skelSession.start(fis);
		
//		System.out.println(fis.available());
//		String st3 = skelSession.deserialize(String.class);
//		System.out.println(fis.available());
//		String st4 = skelSession.deserialize(String.class);
		skelSession.end();
	}
	
	// DEBUG @Test
	public void testMultipleRead2() throws IOException {
		OkapiJsonSession skelSession = new OkapiJsonSession();
		
		File tempSkeleton = null;
		tempSkeleton = File.createTempFile("~aaa", ".txt");
		tempSkeleton.deleteOnExit();
		
		skelSession.start(new FileOutputStream(tempSkeleton));
		Object st1 = new Object();
		Object st2 = new Object();
		
		List<Object> list = new ArrayList<Object> ();
		list.add(st1);
		list.add(st2);
		
//		skelSession.serialize(st1);
//		skelSession.serialize(st2);
		skelSession.serialize(list);
		skelSession.end();
		
		CountingInputStream fis = new CountingInputStream(new FileInputStream(tempSkeleton));
		skelSession.start(fis);
		
//		System.out.println(fis.available());
//		Object st3 = (Object) skelSession.deserialize(Object.class);
//		System.out.println(fis.available());
//		Object st4 = (Object) skelSession.deserialize(Object.class);
		skelSession.end();
	}
	
	// DEBUG @Test
	public void testInputStream() {
		InputStream is = this.getClass().getResourceAsStream("test3.txt");
		log(is.markSupported()? "true" : "false");
//		is.mark(Integer.MAX_VALUE);
		
		//System.out.println();
		//InputStream is2 = is.clone();
		
		String st = "";
		try {
			st = mapper.writeValueAsString(is);
		} catch (JsonGenerationException e) {
			// TODO Handle exception
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Handle exception
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Handle exception
			e.printStackTrace();
		}
		log(st);
	}
	
	
	// DEBUG @Test
	public void testZipSkeleton() throws URISyntaxException, IOException {
		ZipFile zf = null;
			//String name = this.getClass().getResource("sample1.en.fr.zip").toString();
//			URI uri = this.getClass().getResource("test3.txt").toURI();
//			String name = this.getClass().getResource("test3.txt").toString();
			
			//name = Util.getFilename(name, true);
				zf = new ZipFile(new File(this.getClass().getResource("sample1.en.fr.zip").toURI()));
		ZipSkeleton zs = new ZipSkeleton(zf, null);
		ZipSkeletonBean zsb = new ZipSkeletonBean();
		zsb.set(zs, session);
		String st = mapper.writeValueAsString(zsb);
		log(st);
		zf.close();
	}

	// DEBUG @Test
	public void testInputStreamBean() throws URISyntaxException, JsonGenerationException, JsonMappingException, IOException {
		FileInputStream fis = new FileInputStream(new File(this.getClass().getResource("test3.txt").toURI()));
		InputStreamBean isb = new InputStreamBean();
		isb.set(fis, session);
		String st = mapper.writeValueAsString(isb);
		log(st);
	}
	
	// DEBUG @Test
	public void testPersistenceRoundtrip() throws IOException {
	
		Event event1 = new Event(EventType.TEXT_UNIT);
		ITextUnit tu1 = TextUnitUtil.buildTU("source-text1" + (char) 2 + '"' + " : " + '"' + 
				'{' + '"' + "ssssss " + ':' + '"' + "ddddd" + "}:" + '<' + '>' + "sssddd: <>dsdd");
		String zipName = this.getClass().getResource("sample1.en.fr.zip").getFile();
		tu1.setSkeleton(new ZipSkeleton(new ZipFile(new File(zipName)), null));
		event1.setResource(tu1);
		tu1.setTarget(LocaleId.FRENCH, new TextContainer("french-text1"));
		tu1.setTarget(LocaleId.TAIWAN_CHINESE, new TextContainer("chinese-text1"));
				
		Event event2 = new Event(EventType.TEXT_UNIT);
		ITextUnit tu2 = TextUnitUtil.buildTU("source-text2" + (char) 2 + '"' + " : " + '"' + 
				'{' + '"' + "ssssss " + ':' + '"' + "ddddd" + "}:" + '<' + '>' + "sssddd: <>dsdd");
		tu2.setSkeleton(new ZipSkeleton(null, new ZipEntry("aa1/content/content.gmx")));
		event2.setResource(tu2);
		tu2.setTarget(LocaleId.FRENCH, new TextContainer("french-text2"));
		tu2.setTarget(LocaleId.TAIWAN_CHINESE, new TextContainer("chinese-text2"));
		
		tu1.getSource().append("part1");
		tu1.getSource().getSegments().append(new Segment("segId1", new TextFragment("seg1")));
		tu1.getSource().append("part2");
		tu1.getSource().getSegments().append(new Segment("segId2", new TextFragment("seg2")));
				
		OkapiJsonSession skelSession = new OkapiJsonSession();
		
		File tempSkeleton = null;
		tempSkeleton = File.createTempFile("~aaa", ".txt");
		tempSkeleton.deleteOnExit();
		
		skelSession.start(new FileOutputStream(tempSkeleton));
		
		ArrayList<Event> events = new ArrayList<Event>();
		events.add(event1);
		events.add(event2);
		
		skelSession.serialize(events);
		skelSession.end();
		
//		FileInputStream fis = new FileInputStream(tempSkeleton);
//		skelSession.start(fis);		
//		ArrayList<Event> events2 = skelSession.deserialize(Events.class);
//		skelSession.end();
//		
//		FilterTestDriver.compareEvents(events, events2);
//		FilterTestDriver.laxCompareEvents(events, events2);
	}
	
	// DEBUG @Test
	public void testMultipleObject() throws IOException {
	
		Event event1 = new Event(EventType.TEXT_UNIT);
		ITextUnit tu1 = TextUnitUtil.buildTU("source-text1" + (char) 2 + '"' + " : " + '"' + 
				'{' + '"' + "ssssss " + ':' + '"' + "ddddd" + "}:" + '<' + '>' + "sssddd: <>dsdd");
		String zipName = this.getClass().getResource("sample1.en.fr.zip").getFile();
		tu1.setSkeleton(new ZipSkeleton(new ZipFile(new File(zipName)), null));
		event1.setResource(tu1);
		tu1.setTarget(LocaleId.FRENCH, new TextContainer("french-text1"));
		tu1.setTarget(LocaleId.TAIWAN_CHINESE, new TextContainer("chinese-text1"));
				
		Event event2 = new Event(EventType.TEXT_UNIT);
		ITextUnit tu2 = TextUnitUtil.buildTU("source-text2" + (char) 2 + '"' + " : " + '"' + 
				'{' + '"' + "ssssss " + ':' + '"' + "ddddd" + "}:" + '<' + '>' + "sssddd: <>dsdd");
		tu2.setSkeleton(new ZipSkeleton(null, new ZipEntry("aa1/content/content.gmx")));
		event2.setResource(tu2);
		tu2.setTarget(LocaleId.FRENCH, new TextContainer("french-text2"));
		tu2.setTarget(LocaleId.TAIWAN_CHINESE, new TextContainer("chinese-text2"));
		
		tu1.getSource().append("part1");
		tu1.getSource().getSegments().append(new Segment("segId1", new TextFragment("seg1")));
		tu1.getSource().append("part2");
		tu1.getSource().getSegments().append(new Segment("segId2", new TextFragment("seg2")));
				
		OkapiJsonSession skelSession = new OkapiJsonSession();
		
		File tempSkeleton = null;
		tempSkeleton = File.createTempFile("~aaa", ".txt");
		tempSkeleton.deleteOnExit();
		
		skelSession.start(new FileOutputStream(tempSkeleton));
		
		ArrayList<Event> events = new ArrayList<Event>();
		events.add(event1);
		events.add(event2);
		
		skelSession.serialize(event1);
		skelSession.serialize(event2);
		skelSession.end();
		
		FileInputStream fis = new FileInputStream(tempSkeleton);
		skelSession.start(fis);		
		
		Event event11 = skelSession.deserialize(Event.class);
		Event event22 = skelSession.deserialize(Event.class);
//		Event event33 = skelSession.deserialize(Event.class);
		
		skelSession.end();
				
		ArrayList<Event> events2 = new ArrayList<Event>();
		events2.add(event11);
		events2.add(event22);
		
//		FilterTestDriver.compareEvents(events, events2);
//		FilterTestDriver.laxCompareEvents(events, events2);
	}
	
	// DEBUG @Test
	public void testDeserialization() {
		
		// test1.txt -- created by old beans from new core, reading to new core
		OkapiJsonSession skelSession = new OkapiJsonSession();
		skelSession.start(this.getClass().getResourceAsStream("test1.txt"));		
		
//		Event event11 = skelSession.deserialize(Event.class);
//		Event event12 = skelSession.deserialize(Event.class);
		
		skelSession.end();
		
		// test2.txt -- created by new beans from new core, reading to new core
		skelSession.start(this.getClass().getResourceAsStream("test2.txt"));		
		
//		Event event21 = skelSession.deserialize(Event.class);
//		Event event22 = skelSession.deserialize(Event.class);
		
		skelSession.end();
		
		// test4.txt -- created by old beans from old core, reading to new core  
		skelSession.start(this.getClass().getResourceAsStream("test4.txt"));		
		
//		Event event41 = skelSession.deserialize(Event.class);
//		Event event42 = skelSession.deserialize(Event.class);
		
		skelSession.end();
	}
}
