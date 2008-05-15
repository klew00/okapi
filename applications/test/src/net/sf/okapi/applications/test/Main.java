/*===========================================================================*/
/* Copyright (C) 2008 Yves Savourel (at ENLASO Corporation                   */
/*---------------------------------------------------------------------------*/
/* This library is free software; you can redistribute it and/or modify it   */
/* under the terms of the GNU Lesser General Public License as published by  */
/* the Free Software Foundation; either version 2.1 of the License, or (at   */
/* your option) any later version.                                           */
/*                                                                           */
/* This library is distributed in the hope that it will be useful, but       */
/* WITHOUT ANY WARRANTY; without even the implied warranty of                */
/* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser   */
/* General Public License for more details.                                  */
/*                                                                           */
/* You should have received a copy of the GNU Lesser General Public License  */
/* along with this library; if not, write to the Free Software Foundation,   */
/* Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA               */
/*                                                                           */
/* See also the full LGPL text here: http://www.gnu.org/copyleft/lesser.html */
/*===========================================================================*/

package net.sf.okapi.applications.test;

import java.io.File;

import net.sf.okapi.common.filters.CodeFragment;
import net.sf.okapi.common.filters.Container;
import net.sf.okapi.common.filters.ExtractionItem;
import net.sf.okapi.common.filters.IContainer;
import net.sf.okapi.common.filters.IExtractionItem;
import net.sf.okapi.common.filters.ISegment;
import net.sf.okapi.common.filters.Segment;

public class Main {

	static final File INDEX_DIR = new File("index");

	private static void testNewObjects () {
		try {
			System.out.println("---start testNewObjects---");
			IContainer cnt = new Container();
			cnt.append("t1");
			cnt.append(new CodeFragment(IContainer.CODE_ISOLATED, null, "<br/>"));
			cnt.append("t2");
			System.out.println("out 1: " + cnt.toString());
			String s1 = cnt.getCodedText();
			cnt.setContent(s1);
			System.out.println("out 2: " + cnt.toString());
			cnt.setContent("");
			System.out.println("out 3: " + cnt.toString());
		}		
		catch ( Exception E ) {
			System.out.println(E.getLocalizedMessage());
		}
		System.out.println("---end testNewObjects---");
	}
	
	private static void testSegments (ISegment initialSeg) {
		System.out.println("---start testSegments---");
		ISegment seg = initialSeg;
		seg.append("text1");
		seg.append(' ');
		seg.append("text2");
		seg.append(ISegment.CODE_ISOLATED, "br", "<br/>");
		seg.append("text3 ");
		seg.append(ISegment.CODE_OPENING, "b", "<b>");
		seg.append("bolded text");
		seg.append(ISegment.CODE_CLOSING, "b", "</b>");
		
		System.out.println("Original   : '"+seg.toString()+"'");
		System.out.println("Coded      : '"+seg.getCodedText()+"'");
		System.out.println("Generic    : '"+seg.toString(ISegment.TEXTTYPE_GENERIC)+"'");
		System.out.println("Plain text : '"+seg.toString(ISegment.TEXTTYPE_PLAINTEXT)+"'");
		System.out.println("XLIFF-1.2  : '"+seg.toString(ISegment.TEXTTYPE_XLIFF12)+"'");
		System.out.println("XLIFF-1.2XG: '"+seg.toString(ISegment.TEXTTYPE_XLIFF12XG)+"'");
		System.out.println("TMX-1.4    : '"+seg.toString(ISegment.TEXTTYPE_TMX14)+"'");
		
		System.out.println("---codes:");
		for ( int i=0; i<seg.getCodeCount(); i++ ) {
			System.out.println(String.format("Code %d: id=%d, data='%s', label='%s'",
				i, seg.getCodeID(i), seg.getCodeData(i), seg.getCodeLabel(i)));
		}
		System.out.println("Codes: '"+seg.getCodes()+"'");
		
		System.out.println("---internals:");
		System.out.println("Original : '"+seg.toString()+"'");
		
		String tmp1 = seg.getCodes();
		String tmp2 = seg.getCodedText();
		seg = new Segment();
		seg.setCodes(tmp1);
		seg.setTextFromCoded(tmp2);
		System.out.println("Rebuilt-1: '"+seg.toString()+"'");
		
		tmp1 = seg.getCodes();
		tmp2 = seg.toString(ISegment.TEXTTYPE_GENERIC);
		seg = new Segment();
		seg.setCodes(tmp1);
		seg.setTextFromGeneric(tmp2);
		System.out.println("Rebuilt-2: '"+seg.toString()+"'");
		
		System.out.println("---append seg:");
		seg.reset();
		seg.append("a");
		seg.append(ISegment.CODE_ISOLATED, null, "<br/>");
		seg.append("b");
		seg.append(ISegment.CODE_OPENING, "b", "<b>");
		seg.append("c");
		seg.append(ISegment.CODE_CLOSING, "b", "</b>");
		seg.append("d");
		System.out.println("seg1  : '"+seg.toString()+"'");
		System.out.println("seg1  : '"+seg.toString(ISegment.TEXTTYPE_GENERIC)+"'");
		
		ISegment seg2 = new Segment();
		seg2.append("|e");
		seg2.append(ISegment.CODE_ISOLATED, null, "<br/>");
		seg2.append("f");
		seg2.append(ISegment.CODE_OPENING, "i", "<i>");
		seg2.append("g");
		seg2.append(ISegment.CODE_CLOSING, "i", "</i>");
		seg2.append("h");
		System.out.println("seg2  : '"+seg2.toString()+"'");
		System.out.println("seg2  : '"+seg2.toString(ISegment.TEXTTYPE_GENERIC)+"'");
		
		seg.append(seg2);
		System.out.println("seg1+2: '"+seg.toString()+"'");
		System.out.println("seg1+2: '"+seg.toString(ISegment.TEXTTYPE_GENERIC)+"'");
		System.out.println("seg1+2: '"+seg.toString(ISegment.TEXTTYPE_CODED)+"'");
		
		seg2 = seg.subSegment(0, 6, true);
		System.out.println("sub-1w/missing: '"+seg2.toString(ISegment.TEXTTYPE_GENERIC)+"'");
		
		seg2 = seg.subSegment(0, 6, false);
		System.out.println("sub-1no-missing: '"+seg2.toString(ISegment.TEXTTYPE_GENERIC)+"'");
		
		System.out.println("---end testSegments---\n");
	}
	
	private static void testExtractionItems () {
		System.out.println("---start testExtractionItems---");
		IExtractionItem item = new ExtractionItem();
		item.append("text1");
		item.append(ISegment.CODE_ISOLATED, null, "<br/>");
		item.append("text2");
		item.append(ISegment.CODE_OPENING, "b", "<b>");
		item.append("text3");
		item.append(ISegment.CODE_CLOSING, "b", "</b>");
		item.append("text4");
		System.out.println("item: '"+item.toString()+"'");
		System.out.println(String.format("segment count = %d", item.getSegmentCount()));
		
		ISegment seg = new Segment();
		seg.append("a");
		seg.append(ISegment.CODE_ISOLATED, null, "<img/>");
		seg.append("b");
		seg.append(ISegment.CODE_OPENING, null, "<i>");
		seg.append("c");
		seg.append(ISegment.CODE_CLOSING, null, "</i>");
		seg.append("d");
		System.out.println("seg to add: '"+seg.toString()+"'");
		
		item.addSegment(seg);
		System.out.println(String.format("segment count after addition = %d", item.getSegmentCount()));
		System.out.println("item: '"+item.toString()+"'");
		System.out.println("item: '"+item.toString(ISegment.TEXTTYPE_GENERIC)+"'");
		
		System.out.println("---end testExtractionItems---\n");
	}
	
	public static void main(String[] args) throws Exception
	{
		testNewObjects();
		//testSegments(new Segment());
		//testSegments(new ExtractionItem());
		//testExtractionItems();
	}		
		
}
