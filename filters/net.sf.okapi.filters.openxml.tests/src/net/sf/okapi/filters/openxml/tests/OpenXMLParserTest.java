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

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.TreeMap;
import java.util.Iterator;

import net.sf.okapi.common.filters.IParser;
import net.sf.okapi.common.resource.Group;
import net.sf.okapi.common.resource.IContainable;
import net.sf.okapi.common.resource.SkeletonUnit;
import net.sf.okapi.common.resource.TextUnit;
import net.sf.okapi.filters.openxml.OpenXMLParser;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * @author HargraveJE
 * 
 */
public class OpenXMLParserTest {
	private OpenXMLParser openXMLParser;
	private String sSkeletonSave="";
	private static final int MSWORD=1;
	private static final int MSEXCEL=2;
	private static final int MSPOWERPOINT=3;

	@Before
	public void setUp() {		
	}

	@Test
	public void excludeInclude() {
		openXMLParser = new OpenXMLParser();
		doOneOpenXMLFile("m:/eclipse_okapi/workspace/net.sf.okapi.filters.openxml.tests/src/net/sf/okapi/filters/openxml/tests/sample.docx",MSWORD);
		doOneOpenXMLFile("m:/eclipse_okapi/workspace/net.sf.okapi.filters.openxml.tests/src/net/sf/okapi/filters/openxml/tests/sampleDif.docx",MSWORD);
		doOneOpenXMLFile("m:/eclipse_okapi/workspace/net.sf.okapi.filters.openxml.tests/src/net/sf/okapi/filters/openxml/tests/sample.xlsx",MSEXCEL);
		doOneOpenXMLFile("m:/eclipse_okapi/workspace/net.sf.okapi.filters.openxml.tests/src/net/sf/okapi/filters/openxml/tests/sampleMore.xlsx",MSEXCEL);
		doOneOpenXMLFile("m:/eclipse_okapi/workspace/net.sf.okapi.filters.openxml.tests/src/net/sf/okapi/filters/openxml/tests/sample.pptx",MSPOWERPOINT);
		doOneOpenXMLFile("m:/eclipse_okapi/workspace/net.sf.okapi.filters.openxml.tests/src/net/sf/okapi/filters/openxml/tests/sampleMore.pptx",MSPOWERPOINT);
		openXMLParser.close();
	}
	
	public void doOneOpenXMLFile(String sOneFileName, int filetype)
	{
		TreeMap<String,InputStream> tmSubdocs;
		Iterator<String> it;
		InputStream isInputStream;
		String sDocName;
		tmSubdocs = openXMLParser.pryopen(sOneFileName,filetype);
		if (filetype==MSWORD)
		{
			isInputStream = tmSubdocs.get("Document");
			if (isInputStream!=null)
			{
				openXMLParser.open(isInputStream);
				// put out beginning group
				System.out.println("\n\n<<<<<<< "+sOneFileName+" : Main Document >>>>>>>");
				spitOutResources();
				// put out end group?
			}
		}
		for(it = tmSubdocs.keySet().iterator(); it.hasNext();)
		{
			sDocName = (String)it.next();
			if (sDocName.equals("Document")) // main document in MSEXCEL and MSPOWERPOINT are irrelevant
				continue;
			isInputStream = tmSubdocs.get(sDocName);
			openXMLParser.open(isInputStream);
			// put out beginning group with name sDocName
			System.out.println("\n\n<<<<<<< "+sOneFileName+" : "+sDocName+" >>>>>>>");
			spitOutResources();
			// put out end group?
		}			
		openXMLParser.pryclosed();		
	}
	
	public void spitOutResources()
	{
		IParser.ParserTokenType tokenType;
		String sText,sIText;
		IContainable item=null;
		while ((tokenType = openXMLParser.parseNext()) != IParser.ParserTokenType.ENDINPUT)
		{
			item = openXMLParser.getResource();
			if (tokenType == IParser.ParserTokenType.TRANSUNIT)
			{
				assertTrue(item instanceof TextUnit);
				//assertEquals(item.toString(), "Text should be included. <b>");
				sIText = item.toString();
				sText = gotMilk(sIText); // has some text in it and not just tags
				if (!sText.equals("")) // DWH 10-13-08
				{
					outSkeleton();
					System.out.println("Text:");
					assertNotNull(item);
					System.out.println(sText);
				}
			} else if (tokenType == IParser.ParserTokenType.SKELETON)
			{
				assertTrue(item instanceof SkeletonUnit);
//				System.out.println("Skeleton:");
				assertNotNull(item);
				sIText = item.toString();
				sSkeletonSave = sSkeletonSave + sIText;
			} 
			else if (tokenType == IParser.ParserTokenType.STARTGROUP || tokenType == IParser.ParserTokenType.ENDGROUP)
			{
				assertTrue(item instanceof Group);
				outSkeleton();
				System.out.println("Group:");
				assertNotNull(item);
				sIText = item.toString();
				System.out.println(sIText);
			}
		}
		outSkeleton();
		openXMLParser.resetParse(); // get it ready to do another document
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
}
