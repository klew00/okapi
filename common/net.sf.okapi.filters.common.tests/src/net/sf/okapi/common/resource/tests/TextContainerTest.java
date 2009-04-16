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

package net.sf.okapi.common.resource.tests;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.junit.Test;
import static org.junit.Assert.*;

import net.sf.okapi.common.Range;
import net.sf.okapi.common.resource.Property;
import net.sf.okapi.common.resource.TextContainer;
import net.sf.okapi.common.resource.TextFragment;

public class TextContainerTest {

	@Test
	public void testGetSetContent () {
		TextContainer tc1 = new TextContainer();
		assertNotNull(tc1.getContent());
		assertTrue(tc1.isEmpty());
		tc1 = new TextContainer("text");
		assertEquals(tc1.getContent(), "text");
		// Test cloning
		TextContainer tc2 = tc1.clone();
		assertNotSame(tc1, tc2);
		assertEquals(tc1.toString(), tc2.toString());
	}

	@Test
	public void testGetSetProperties () {
		TextContainer tc1 = new TextContainer();
		Set<String> list = tc1.getPropertyNames();
		assertNotNull(list);
		assertTrue(list.size()==0);
		Property p1 = new Property("name", "value", false);
		tc1.setProperty(p1);
		assertTrue(tc1.hasProperty("name"));
		Property p2 = tc1.getProperty("name");
		assertSame(p1, p2);
		assertEquals(p2.getValue(), "value");
		assertFalse(p2.isReadOnly());
		list = tc1.getPropertyNames();
		assertEquals(list.size(), 1);
		for ( String name : list ) {
			p1 = tc1.getProperty(name);
			assertEquals(p1.toString(), "value");
			assertSame(p1, p2);
		}
	}

	@Test
	public void testAppendSimpleSegment () {
		TextContainer tc = new TextContainer();
		TextFragment tf = new TextFragment("text1");
		assertEquals(0, tc.getSegmentCount());
		tc.appendSegment(tf);
		assertEquals(1, tc.getSegmentCount());
		assertEquals("text1", tc.getSegments().get(0).toString());
	}

	private TextContainer createMultiSegmentContent () {
		TextContainer tc = new TextContainer();
		TextFragment tf = new TextFragment("text1");
		tc.appendSegment(tf);
		tc.append(' ');
		tf = new TextFragment("text2");
		tc.appendSegment(tf);
		return tc;
	}
	
	@Test
	public void testAppendSeveralSegments () {
		TextContainer tc = createMultiSegmentContent();
		assertEquals(2, tc.getSegmentCount());
		assertEquals("text1", tc.getSegments().get(0).toString());
		assertEquals("text2", tc.getSegments().get(1).toString());
		assertEquals("0 1", tc.toString());
	}
	
	@Test
	public void testMergingSegments () {
		TextContainer tc = createMultiSegmentContent();
		tc.mergeAllSegments();
		assertEquals(0, tc.getSegmentCount());
		assertNull(tc.getSegments());
		assertEquals("text1 text2", tc.toString());
	}
	
	@Test
	public void testSegments () {
		String originalText = "[seg1][seg2] [seg3]";
		TextContainer tc1 = new TextContainer(originalText);
		// "[seg1][seg2] [seg3]"
		//  0123456789012345678
		assertFalse(tc1.isSegmented());
		
		// Test one-by-one segmenting
		tc1.createSegment(0, 6);
		// "**[seg2] [seg3]"
		//  012345678901234
		assertTrue(tc1.isSegmented());
		tc1.createSegment(2, 8);
		// "**** [seg3]"
		//  01234567890
		tc1.createSegment(5, 11);
		assertEquals(tc1.toString().length(), 4);
		assertEquals(tc1.getCodedText().length(), 2+2+1+2);
		List<TextFragment> list = tc1.getSegments();
		assertNotNull(list);
		assertEquals(list.size(), 3);
		assertEquals(list.get(0).toString(), "[seg1]");
		assertEquals(list.get(1).toString(), "[seg2]");
		assertEquals(list.get(2).toString(), "[seg3]");
		// Test merge all
		tc1.mergeAllSegments();
		assertFalse(tc1.isSegmented());
		assertEquals(tc1.toString(), originalText);
	}

	@Test
	public void testSegmentsFromArray () {
		String originalText = "[seg1][seg2] [seg3]";
		TextContainer tc1 = new TextContainer(originalText);

		// Test segmenting from an array
		List<Range> ranges = new ArrayList<Range>();
		ranges.add(new Range(0, 6));
		ranges.add(new Range(6, 12));
		ranges.add(new Range(13, 19));
		
		tc1.createSegments(ranges);
		assertTrue(tc1.isSegmented());
		assertEquals(tc1.getCodedText().length(), 2+2+1+2);
		List<TextFragment> list = tc1.getSegments();
		assertNotNull(list);
		assertEquals(list.size(), 3);
		assertEquals(list.get(0).toString(), "[seg1]");
		assertEquals(list.get(1).toString(), "[seg2]");
		assertEquals(list.get(2).toString(), "[seg3]");
		// Test Merge one-by-one
		tc1.mergeSegment(0);
		assertEquals(list.size(), 2);
		tc1.mergeSegment(0);
		assertEquals(list.size(), 1);
		tc1.mergeSegment(0);
		assertFalse(tc1.isSegmented());
		assertEquals(tc1.toString(), originalText);
		
		// Re-segment again and re-merge out of sequence
		tc1.createSegments(ranges);
		list = tc1.getSegments();
		assertEquals(list.size(), 3);
		tc1.mergeSegment(1); // [seg2]
		tc1.mergeSegment(1); // [seg3]
		tc1.mergeSegment(0); // [seg1]
		assertEquals(tc1.toString(), originalText);
	}

}
