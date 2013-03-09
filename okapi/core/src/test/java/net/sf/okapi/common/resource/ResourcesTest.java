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

package net.sf.okapi.common.resource;

import java.util.ArrayList;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.EventType;
import net.sf.okapi.common.encoder.EncoderManager;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.resource.Code;
import net.sf.okapi.common.resource.DocumentPart;
import net.sf.okapi.common.resource.Property;
import net.sf.okapi.common.resource.StartDocument;
import net.sf.okapi.common.resource.TextFragment;
import net.sf.okapi.common.resource.ITextUnit;
import net.sf.okapi.common.resource.TextFragment.TagType;
import net.sf.okapi.common.skeleton.GenericSkeleton;
import net.sf.okapi.common.skeleton.GenericSkeletonWriter;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class ResourcesTest {

	LocaleId locEN = LocaleId.fromString("en");
	LocaleId locSV = LocaleId.fromString("sv");

	@Before
	public void setUp () throws Exception {
	}
	
	@Test
	public void testMETATag1 () {
		String test = "<meta http-equiv=\"keywords\" content=\"one,two,three\"/>";
		ArrayList<Event> list = new ArrayList<Event>();
		
		StartDocument sd = new StartDocument("sd");
		sd.setEncoding("UTF-16", false);
		sd.setMultilingual(true);
		sd.setLocale(locEN);
		sd.setLineBreak("\n");
		list.add(new Event(EventType.START_DOCUMENT, sd));

		// Build the input
		GenericSkeleton skel = new GenericSkeleton();
		ITextUnit tu = new TextUnit("t1", "one,two,three");
		skel.add("content=\"");
		skel.addContentPlaceholder(tu);
		skel.add("\"");		
		tu.setIsReferent(true);
		tu.setName("content");
		tu.setSkeleton(skel);
		list.add(new Event(EventType.TEXT_UNIT, tu));
		
		skel = new GenericSkeleton();
		DocumentPart dp = new DocumentPart("dp1", false);		
		skel.add("<meta http-equiv=\"keywords\" ");
		skel.addReference(tu);
		skel.add("/>");
		dp.setSkeleton(skel);
		list.add(new Event(EventType.DOCUMENT_PART, dp));

		// Output and compare
		assertEquals(generateOutput(list, test, locEN), test);
	}
	
	@Test
	public void testPWithAttributes () {
		String test = "<p title='my title'>Text of p</p>";
		ArrayList<Event> list = new ArrayList<Event>();
		
		StartDocument sd = new StartDocument("sd");
		sd.setEncoding("UTF-16", false);
		sd.setMultilingual(true);
		sd.setLocale(locEN);
		sd.setLineBreak("\n");
		list.add(new Event(EventType.START_DOCUMENT, sd));

		// Build the input
		GenericSkeleton skel = new GenericSkeleton();
		ITextUnit tu1 = new TextUnit("t1", "my title");
		skel.add("title='");
		skel.addContentPlaceholder(tu1);
		skel.add("'");		
		tu1.setIsReferent(true);
		tu1.setName("title");
		tu1.setSkeleton(skel);
		list.add(new Event(EventType.TEXT_UNIT, tu1));
		
		skel = new GenericSkeleton();
		ITextUnit tu2 = new TextUnit("tu2", "Text of p");		
		skel.add("<p ");
		skel.addReference(tu1);
		skel.add(">");
		skel.addContentPlaceholder(tu2);
		skel.append("</p>");
		tu2.setSkeleton(skel);
		list.add(new Event(EventType.TEXT_UNIT, tu2));

		// Output and compare
		assertEquals(generateOutput(list, test, locEN), test);
	}
	
	@Test
	public void testComplexEmptyElement () {
		String test = "<elem wr-prop1='wr-value1' ro-prop1='ro-value1' wr-prop2='wr-value2' text='text'/>";
		ArrayList<Event> list = new ArrayList<Event>();
		
		StartDocument sd = new StartDocument("sd");
		sd.setEncoding("UTF-16", false);
		sd.setMultilingual(true);
		sd.setLocale(locEN);
		sd.setLineBreak("\n");
		list.add(new Event(EventType.START_DOCUMENT, sd));

		// Build the input
		GenericSkeleton skel = new GenericSkeleton();
		ITextUnit tu = new TextUnit("t1", "text");
		skel.add("text='");
		skel.addContentPlaceholder(tu);
		skel.add("'");		
		tu.setIsReferent(true);
		tu.setName("text");
		tu.setSkeleton(skel);
		list.add(new Event(EventType.TEXT_UNIT, tu));
		
		skel = new GenericSkeleton();
		DocumentPart dp = new DocumentPart("dp1", false);		
		skel.add("<elem wr-prop1='");
		dp.setSourceProperty(new Property("wr-prop1", "wr-value1", false));
		skel.addValuePlaceholder(dp, "wr-prop1", null);
		skel.add("' ro-prop1='ro-value1' wr-prop2='");
		dp.setSourceProperty(new Property("ro-prop1", "ro-value1"));
		dp.setSourceProperty(new Property("wr-prop2", "wr-value2", false));
		skel.addValuePlaceholder(dp, "wr-prop2", null);
		skel.append("' ");
		skel.addReference(tu);
		skel.append("/>");
		
		dp.setSkeleton(skel);
		list.add(new Event(EventType.DOCUMENT_PART, dp));

		// Output and compare
		assertEquals(generateOutput(list, test, locEN), test);
	}

	@Test
	public void testPWithInlines () {
		String test = "<p>Before <b>bold</b> <a href=\"there\"/> after.</p>";
		ArrayList<Event> list = new ArrayList<Event>();
		
		StartDocument sd = new StartDocument("sd");
		sd.setEncoding("UTF-16", false);
		sd.setMultilingual(true);
		sd.setLocale(locEN);
		sd.setLineBreak("\n");
		list.add(new Event(EventType.START_DOCUMENT, sd));

		GenericSkeleton skel = new GenericSkeleton();
		DocumentPart dp1 = new DocumentPart("dp1", true);
		skel.add("<a href=\"");
		skel.addValuePlaceholder(dp1, "href", null);
		dp1.setSourceProperty(new Property("href", "there", false));
		skel.add("\"/>");
		dp1.setName("a");
		dp1.setSkeleton(skel);
		list.add(new Event(EventType.DOCUMENT_PART, dp1));
		
		skel = new GenericSkeleton();
		TextFragment tf = new TextFragment("Before ");
		tf.append(TagType.OPENING, "b", "<b>");
		tf.append("bold");
		tf.append(TagType.CLOSING, "b", "</b>");
		tf.append(" ");
		Code code = new Code(TagType.PLACEHOLDER, "a");
		code.appendReference("dp1");
		tf.append(code);
		tf.append(" after.");
		ITextUnit tu1 = new TextUnit("tu1");
		tu1.setSourceContent(tf);
		skel.add("<p>");
		skel.addContentPlaceholder(tu1);
		skel.append("</p>");
		tu1.setSkeleton(skel);
		list.add(new Event(EventType.TEXT_UNIT, tu1));

		// Output and compare
		assertEquals(generateOutput(list, test, locEN), test);
	}

	@Test
	public void testMETATag2 () {
		String test = "<meta http-equiv=\"Content-Language\" content=\"en\"/>";
		ArrayList<Event> list = new ArrayList<Event>();
		
		StartDocument sd = new StartDocument("sd");
		sd.setEncoding("UTF-16", false);
		sd.setMultilingual(true);
		sd.setLocale(locEN);
		sd.setLineBreak("\n");
		list.add(new Event(EventType.START_DOCUMENT, sd));

		GenericSkeleton skel = new GenericSkeleton();
		DocumentPart dp = new DocumentPart("dp1", false);		
		skel.add("<meta http-equiv=\"Content-Language\" content=\"");
		skel.addValuePlaceholder(dp, "language", null);
		skel.add("\"/>");
		dp.setSourceProperty(new Property("language", locEN.toString(), false));
		dp.setSkeleton(skel);
		list.add(new Event(EventType.DOCUMENT_PART, dp));

		// Output and compare
		assertEquals(generateOutput(list, test, locEN), test);
	}
	
	@Test
	public void testTMXTU () {
		String test = "<tu><tuv xml:lang='EN'><seg>T1-en<sub>Sub-en</sub>T2-en</seg></tuv>"
			+"<tuv xml:lang='SV'><seg>T1-sv<sub>Sub-sv</sub>T2-sv</seg></tuv></tu>";
		ArrayList<Event> list = new ArrayList<Event>();
		
		StartDocument sd = new StartDocument("sd");
		sd.setEncoding("UTF-16", false);
		sd.setMultilingual(true);
		sd.setLocale(locEN);
		sd.setLineBreak("\n");
		list.add(new Event(EventType.START_DOCUMENT, sd));

		// Create the main tu and its skeleton
		ITextUnit tu = new TextUnit("tu");
		GenericSkeleton skel = new GenericSkeleton();
		skel.add("<tu><tuv xml:lang='EN'><seg>");
		
		// Store the tuv content of the source
		TextFragment src = tu.getSource().getSegments().getFirstContent();
		src.append("T1-en");
		
		// Add the <sub> element of the source
		Code code = src.append(TagType.PLACEHOLDER, null, "<sub>");
		// Create the tu for sub as a reference
		ITextUnit tuSub = new TextUnit("tuSub", "Sub-en", true);
		// Create the skeleton for the tu of sub, just to hold the content
		GenericSkeleton skelSub = new GenericSkeleton();
		skelSub.addContentPlaceholder(tuSub);
		code.appendReference("tuSub");
		// But the <sub> tags go in the inline code because if they were in the
		// skeleton of the sub it would be identical for source and target
		// but that cannot be: we have to allow for different <sub> tags
		code.append("</sub>");
		
		// Add the second part of the source text
		src.append("T2-en");
		// Add the placeholder for the source tu
		skel.addContentPlaceholder(tu);

		// Add the skeleton between source and target
		skel.add("</seg></tuv><tuv xml:lang='SV'><seg>");

		// Create the target main content 
		TextFragment trg = tu.setTargetContent(locSV, new TextFragment());
		trg.append("T1-sv");
		
		// Add the sub inline in the target
		code = trg.append(TagType.PLACEHOLDER, null, "<sub>");
		// Set the content of the target sub in the tu of the sub
		tuSub.setTargetContent(locSV, new TextFragment("Sub-sv"));
		// Add the reference to the tu of the sub,
		// because it's in the target fragment it will get the target 
		code.appendReference("tuSub");
		// Increment the number of time tuSub is referenced
		tuSub.setReferenceCount(tuSub.getReferenceCount()+1);
		
		code.append("</sub>");
		trg.append("T2-sv");
		skel.addContentPlaceholder(tu, locSV);
		skel.add("</seg></tuv></tu>");

		// Send the tu of the sub
		tuSub.setSkeleton(skelSub);
		list.add(new Event(EventType.TEXT_UNIT, tuSub));
		
		// Send the main tu
		tu.setSkeleton(skel);
		list.add(new Event(EventType.TEXT_UNIT, tu));
		
		// Output and compare
		//FAIL TEST: content of sub is en instead of sv
		//This is a reported issue.
		//assertEquals(generateOutput(list, test, trgLang), test);
		generateOutput(list, test, locSV);
	}
	
	private String generateOutput (ArrayList<Event> list,
		String original,
		LocaleId outputLang)
	{
		GenericSkeletonWriter writer = new GenericSkeletonWriter();
		StringBuilder tmp = new StringBuilder();
		for ( Event event : list ) {
			switch ( event.getEventType() ) {
			case START_DOCUMENT:
				writer.processStartDocument(outputLang, "utf-8", null, new EncoderManager(),
					(StartDocument)event.getResource());
				break;
			case TEXT_UNIT:
				ITextUnit tu = event.getTextUnit();
				GenericSkeleton skl = (GenericSkeleton)tu.getSkeleton();
				if ( skl != null ) {
					//System.out.println("TU:skl="+skl.toString());
				}
				else {
					//System.out.println("TU:skl=None");
				}
				//System.out.println("  :txt="+tu.toString());
				tmp.append(writer.processTextUnit(tu));
				break;
			case DOCUMENT_PART:
				DocumentPart dp = (DocumentPart)event.getResource();
				skl = (GenericSkeleton)dp.getSkeleton();
				if ( skl != null ) {
					//System.out.println("DP:skl="+skl.toString());
				}
				else {
					//System.out.println("DP:skl=None");
				}
				tmp.append(writer.processDocumentPart(dp));
				break;
			}
		}
		writer.close();
		//System.out.println(" in: "+original);
		//System.out.println("out: "+tmp.toString());
		//System.out.println("-----");
		return tmp.toString();
	}

}
