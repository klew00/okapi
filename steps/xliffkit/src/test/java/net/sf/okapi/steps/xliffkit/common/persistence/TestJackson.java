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
===========================================================================*/

package net.sf.okapi.steps.xliffkit.common.persistence;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.zip.ZipEntry;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.EventType;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.resource.RawDocument;
import net.sf.okapi.common.resource.TextContainer;
import net.sf.okapi.common.resource.TextUnit;
import net.sf.okapi.common.resource.TextUnitUtil;
import net.sf.okapi.common.skeleton.ZipSkeleton;
import net.sf.okapi.steps.xliffkit.common.persistence.beans.EventBean;
import net.sf.okapi.steps.xliffkit.common.persistence.beans.TextUnitBean;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.junit.Before;
import org.junit.Test;

public class TestJackson {

//	private static final String fileName = "test3.txt";
	private ObjectMapper mapper;
	
	@Before
	public void setUp() {
		mapper = new ObjectMapper();
		
		mapper.configure(SerializationConfig.Feature.INDENT_OUTPUT, true); 
		mapper.configure(SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS, false);
		mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	}
		
	
	// DEBUG @Test
	public void testTextUnit() throws JsonGenerationException, JsonMappingException, IOException, URISyntaxException {
		Event event = new Event(EventType.TEXT_UNIT);
		//TextUnit tu = TextUnitUtil.buildTU("source", "skeleton");
		TextUnit tu = TextUnitUtil.buildTU("source-text" + (char) 2 + '"' + " : " + '"' + 
				'{' + '"' + "ssssss " + ':' + '"' + "ddddd" + "}:" + '<' + '>' + "sssddd: <>dsdd");
		tu.setSkeleton(new ZipSkeleton(new ZipEntry("")));
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
		tub.set(tu);
		
		EventBean evb = new EventBean();
		evb.set(event);
		//mapper.getDeserializationConfig().addHandler(new TestResolver());
		
		//String st = mapper.writeValueAsString(tub);
		String st = mapper.writeValueAsString(evb);
		System.out.println(st);
		
//		String st2 = mapper.writeValueAsString(evb);
//		System.out.println(st2);
		
		st = mapper.writeValueAsString(tub);
		tub = mapper.readValue(st, TextUnitBean.class);
		tu = tub.get(new TextUnit(""));
//		System.out.println(tu.getSource().getCodedText());
		System.out.println(((TextContainer)tub.getSource().get(new TextContainer())).getCodedText());
//		ISkeleton skel = tub.getSkeleton().read(ISkeleton.class);
//		if (skel != null)
//			System.out.println(skel.getClass());
		//System.out.println(tub.getSkeleton().getContent().getClass().getName());
		
//		ZipSkeletonBean zsb = new ZipSkeletonBean(); 
//		st = mapper.writeValueAsString(zsb);
//		zsb = mapper.readValue(st, ZipSkeletonBean.class);
		
		//ZipSkeletonBean zsb = mapper.readValue(st, ZipSkeletonBean.class);
	}
	
	// DEBUG 
	@Test
	public void testTextUnitWrite() {
	
		Event event = new Event(EventType.TEXT_UNIT);
		//TextUnit tu = TextUnitUtil.buildTU("source", "skeleton");
		TextUnit tu = TextUnitUtil.buildTU("source-text" + (char) 2 + '"' + " : " + '"' + 
				'{' + '"' + "ssssss " + ':' + '"' + "ddddd" + "}:" + '<' + '>' + "sssddd: <>dsdd");
		tu.setSkeleton(new ZipSkeleton(new ZipEntry("")));
		event.setResource(tu);
		tu.setTarget(LocaleId.FRENCH, new TextContainer("french-text"));
		tu.setTarget(LocaleId.TAIWAN_CHINESE, new TextContainer("chinese-text"));
		
		JSONPersistenceSession skelSession = new JSONPersistenceSession(Event.class);
		
		
		File tempSkeleton = null;
		try {
			tempSkeleton = File.createTempFile("aaa", "bbb");
		} catch (IOException e1) {
			// TODO Handle exception
		}
		tempSkeleton.deleteOnExit();
		
		try {
			skelSession.start(new FileOutputStream(tempSkeleton));
		} catch (FileNotFoundException e) {
			// TODO Handle exception
		}
		
		skelSession.serialize(event);
		skelSession.end();
		
		try {
			skelSession.start(new FileInputStream(tempSkeleton));
		} catch (FileNotFoundException e) {
			// TODO Handle exception
		}
		
		event = (Event) skelSession.deserialize();
		skelSession.end();
	}
	
	// DEBUG @Test
	public void testRawDocument() throws JsonGenerationException, JsonMappingException, IOException {
		Event event = new Event(EventType.RAW_DOCUMENT);
		event.setResource(new RawDocument("raw doc", LocaleId.ENGLISH));
		EventBean evb = new EventBean();
		evb.set(event);
		//mapper.getDeserializationConfig().addHandler(new TestResolver());
		
		//String st = mapper.writeValueAsString(tub);
		String st = mapper.writeValueAsString(evb);
		System.out.println(st);
	}
	
	// DEBUG @Test
	public void testRead() {
		
	}
	
}
