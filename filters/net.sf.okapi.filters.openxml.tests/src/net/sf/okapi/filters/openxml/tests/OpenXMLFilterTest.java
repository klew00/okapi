/* Copyright (C) 2008 Jim Hargrave, Dan Higinbotham
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
/* Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307 USA              */
/*                                                                           */
/* See also the full LGPL text here: http://www.gnu.org/copyleft/lesser.html */
/*===========================================================================*/

package net.sf.okapi.filters.openxml.tests;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.EventType;
import net.sf.okapi.common.resource.TextUnit;
import net.sf.okapi.filters.openxml.OpenXMLFilter;

import net.sf.okapi.common.resource.DocumentPart;
import net.sf.okapi.common.resource.Ending;
import net.sf.okapi.common.resource.StartGroup;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * @author HargraveJE, HiginbothamDW
 * 
 */
public class OpenXMLFilterTest {
	private OpenXMLFilter openXMLFilter;
	private String sSkeletonSave="";
	private static final int MSWORD=1;
	private static final int MSEXCEL=2;
	private static final int MSPOWERPOINT=3;
//	private GenericFilterWriter gfw; // DWH 1-8-09
//	private GenericSkeletonWriter gsw; // DWH 1-8-09
	@Before
	public void setUp() {
	}

	@Test
	public void excludeInclude() {
//		gsw = new GenericSkeletonWriter(); // DWH 1-8-09
//		gfw = new GenericFilterWriter(gsw); // DWH 1-8-09
//		gfw.setOptions("en", "UTF-8"); // DWH 1-8-09
//		gfw.setOutput("d://nuttin"); // DWH 1-8-09

		openXMLFilter = new OpenXMLFilter();
		openXMLFilter.setOptions("en", "UTF-8", true);
//		if (openXMLFilter.doOneOpenXMLFile("m:/eclipse_okapi/workspace/net.sf.okapi.filters.openxml.tests/src/net/sf/okapi/filters/openxml/tests/sample.docx",MSWORD,3))
		if (openXMLFilter.doOneOpenXMLFile("src/net/sf/okapi/filters/openxml/tests/sample.docx",MSWORD,3))
			displayEvents();
		openXMLFilter.close();

		openXMLFilter = new OpenXMLFilter();
		openXMLFilter.setOptions("en", "UTF-8", true);
//		openXMLFilter.doOneOpenXMLFile("m:/eclipse_okapi/workspace/net.sf.okapi.filters.openxml.tests/src/net/sf/okapi/filters/openxml/tests/sampleDif.docx",MSWORD,3);
		if (openXMLFilter.doOneOpenXMLFile("src/net/sf/okapi/filters/openxml/tests/sampleDif.docx",MSWORD,3))
			displayEvents();
		openXMLFilter.close();

		openXMLFilter = new OpenXMLFilter();
		openXMLFilter.setOptions("en", "UTF-8", true);
//		openXMLFilter.doOneOpenXMLFile("m:/eclipse_okapi/workspace/net.sf.okapi.filters.openxml.tests/src/net/sf/okapi/filters/openxml/tests/sample.xlsx",MSEXCEL,3);
		if (openXMLFilter.doOneOpenXMLFile("src/net/sf/okapi/filters/openxml/tests/sample.xlsx",MSEXCEL,3))
			displayEvents();
		openXMLFilter.close();
		
		openXMLFilter = new OpenXMLFilter();
		openXMLFilter.setOptions("en", "UTF-8", true);
//		openXMLFilter.doOneOpenXMLFile("m:/eclipse_okapi/workspace/net.sf.okapi.filters.openxml.tests/src/net/sf/okapi/filters/openxml/tests/sampleMore.xlsx",MSEXCEL,3);
		if (openXMLFilter.doOneOpenXMLFile("src/net/sf/okapi/filters/openxml/tests/sampleMore.xlsx",MSEXCEL,3))
			displayEvents();
		openXMLFilter.close();

		openXMLFilter = new OpenXMLFilter();
		openXMLFilter.setOptions("en", "UTF-8", true);
//		openXMLFilter.doOneOpenXMLFile("m:/eclipse_okapi/workspace/net.sf.okapi.filters.openxml.tests/src/net/sf/okapi/filters/openxml/tests/sample.pptx",MSPOWERPOINT,3);
		if (openXMLFilter.doOneOpenXMLFile("src/net/sf/okapi/filters/openxml/tests/sample.pptx",MSPOWERPOINT,3))
			displayEvents();
		openXMLFilter.close();

		openXMLFilter = new OpenXMLFilter();
		openXMLFilter.setOptions("en", "UTF-8", true);
//		openXMLFilter.doOneOpenXMLFile("m:/eclipse_okapi/workspace/net.sf.okapi.filters.openxml.tests/src/net/sf/okapi/filters/openxml/tests/sampleMore.pptx",MSPOWERPOINT,3);
		if (openXMLFilter.doOneOpenXMLFile("src/net/sf/okapi/filters/openxml/tests/sampleMore.pptx",MSPOWERPOINT,3))
			displayEvents();
		openXMLFilter.close();
	}
	
	public void displayEvents()
	{
		Event event;
		while (openXMLFilter.hasNext()) {
			event = openXMLFilter.next();
			if (event.getEventType() == EventType.TEXT_UNIT) {
				assertTrue(event.getResource() instanceof TextUnit);
			} else if (event.getEventType() == EventType.DOCUMENT_PART) {
				assertTrue(event.getResource() instanceof DocumentPart);
			} else if (event.getEventType() == EventType.START_GROUP
					|| event.getEventType() == EventType.END_GROUP) {
				assertTrue(event.getResource() instanceof StartGroup || event.getResource() instanceof Ending);
			}
			System.out.println(event.getEventType().toString() + ": ");
			if (event.getResource() != null) {
				System.out.println("(" + event.getResource().getId()+")");
				if (event.getResource() instanceof DocumentPart) {
					System.out.println(((DocumentPart) event.getResource()).getSourcePropertyNames());
				} else {
					System.out.println(event.getResource().toString());
				}
				if (event.getResource().getSkeleton() != null) {
					System.out.println("*Skeleton: \n" + event.getResource().getSkeleton().toString());
				}
			}
		}
		System.out.println("");
		System.out.println("");
	}
/*
	public void displayEvents()
	{
		FilterEvent event;
		while ((event = openXMLFilter.next()).getEventType() != FilterEventType.FINISHED)
		{
			if (event.getEventType() == FilterEventType.TEXT_UNIT)
			{
				assertTrue(event.getResource() instanceof TextUnit); // DWH 1-5-09
				if (event.getResource().toString()!=null) // DWH 1-5-09 whole if
				{			
					System.out.println("Text:");
					System.out.println(event.getResource().toString());
				}
			}
			else if (event.getEventType() == FilterEventType.DOCUMENT_PART)
			{
				assertTrue(event.getResource() instanceof DocumentPart);
				if (event.getResource().getSkeleton()!=null) // DWH 1-5-09 whole if
				{
					System.out.println("DocumentPart:");
					System.out.println(event.getResource().getSkeleton().toString());
				}
			} 
		    else if (event.getEventType() == FilterEventType.START_GROUP ||
		    		 event.getEventType() == FilterEventType.END_GROUP)
			{
				assertTrue(event.getResource() instanceof StartGroup ||
						   event.getResource() instanceof Ending);
				if (event.getResource().toString()!=null)
				{
					System.out.println("Group:");
					System.out.println(event.getResource().toString());
				}
			}
		}
	}

	public void spitOutResources()
	{
		FilterEvent event;
		String sText,sIText,scText,ssText; // DWH 1-5-09 ssText
		TextFragment tf; // DWH 1-7-09
		TextUnit tu; // DWH 1-7-09
//		IContainable item=null; 1-5-09
		while ((event = openXMLFilter.next()).getEventType() != FilterEventType.FINISHED)
		{
			if (event.getEventType() == FilterEventType.TEXT_UNIT)
			{
				assertTrue(event.getResource() instanceof TextUnit); // DWH 1-5-09 
//				sIText = item.toString(); 1-5-09
				sIText = event.getResource().toString(); // DWH 1-5-09
				tu = (TextUnit)event.getResource(); // DWH 1-7-09
				tf = (TextFragment)tu.getSourceContent();
				scText = tf.toString(); // DWH 1-7-09 text with internal codes (tags)
//				scText = gsw.processTextUnit(tu); // DWH 1-8-09
				sText = gotMilk(scText); // text without codes (tags)
				if (event.getResource().getSkeleton()!=null) // DWH 1-5-09 whole if
				{
					ssText = event.getResource().getSkeleton().toString(); // DWH 1-5-09 ssText
					sSkeletonSave = sSkeletonSave + ssText;					
				}
				if (!sText.equals("")) // DWH 10-13-08
				{
					outSkeleton();
					System.out.println("Text:");
//					assertNotNull(item); 1-5-09
					System.out.println(sText);
				}
			}
//			else if (event.getEventType() == FilterEventType.SKELETON_UNIT) 1-5-09
			else if (event.getEventType() == FilterEventType.DOCUMENT_PART)
			{
//				assertTrue(item instanceof SkeletonUnit); 1-5-09
				assertTrue(event.getResource() instanceof DocumentPart);
//				assertNotNull(item); 1-5-09
//				sIText = item.toString(); 1-5-09
//				sIText = event.getResource().toString(); // DWH 1-5-09 good then commented
//				sSkeletonSave = sSkeletonSave + sIText;
				if (event.getResource().getSkeleton()!=null) // DWH 1-5-09 whole if
				{
					ssText = event.getResource().getSkeleton().toString(); // DWH 1-5-09 ssText
					sSkeletonSave = sSkeletonSave + ssText;					
				}
			} 
		    else if (event.getEventType() == FilterEventType.START_GROUP || event.getEventType() == FilterEventType.END_GROUP)
			{
//				assertTrue(item instanceof Group); 1-5-09
				assertTrue(event.getResource() instanceof StartGroup || event.getResource() instanceof Ending);
				outSkeleton();
				System.out.println("Group:");
//				assertNotNull(item);
//				sIText = item.toString(); 1-5-09
				sIText = event.getResource().toString(); // DWH 1-5-09
				System.out.println(sIText);
				if (event.getResource().getSkeleton()!=null) // DWH 1-5-09 whole if
				{
					ssText = event.getResource().getSkeleton().toString(); // DWH 1-5-09 ssText
					sSkeletonSave = sSkeletonSave + ssText;					
				}
			}
		}
		outSkeleton();
		openXMLFilter.resetParse(); // get it ready to do another document
	}
	private String gotMilk(String sCheese) // true if there is text and not just tags
	{
		boolean bInTag=false;
		int iLen,iNdx;
		char c;
		boolean bInSkeleton=true;
		String sBean,sRslt="",sSkeleton="";
		if (sCheese!=null)
		{
			iLen = sCheese.length();
			for(iNdx=0;iNdx<iLen;iNdx++)
			{
				c = sCheese.charAt(iNdx);
				if (c=='<')
				{
					if (bInSkeleton)
					{
						if (iNdx+1<iLen && sCheese.charAt(iNdx+1)=='/')
						{
							sSkeleton += "</";
							iNdx++;
						}
						else
						{
							bInSkeleton = false;
							sRslt += "<";
						}
					}
					else
						sRslt += "<";
					bInTag = true;
				}
				else if (c=='>')
				{
					bInTag = false;
					if (bInSkeleton)
						sSkeleton += ">";
					else
						sRslt += ">";
				}
				else if (c=='\r' || c=='\n')
				{
					if (bInSkeleton)
						sSkeleton += c;
					else
						sRslt += c;
					continue;
				}
				else if (!bInTag) // if there is any text, return from here
				{
					sRslt += sCheese.substring(iNdx);
					return(sRslt);
				}
				else if (bInSkeleton)
					sSkeleton += c;
				else
					sRslt += c;
			}
		}
		sSkeleton += sRslt; // no text was found, so put everything in skeleton
		sRslt = "";
		return (sRslt);
	}
	private void outSkeleton()
	{
		if (sSkeletonSave.length()>0)
		{
			System.out.println("Skeleton:");
			System.out.println(sSkeletonSave);
		}
		sSkeletonSave = "";
	}
*/
}
