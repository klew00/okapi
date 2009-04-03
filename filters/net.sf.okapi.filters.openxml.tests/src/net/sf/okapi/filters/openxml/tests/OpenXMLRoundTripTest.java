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

package net.sf.okapi.filters.openxml.tests;

import java.io.File;
import java.net.URI;
import java.net.URL;

import net.sf.okapi.filters.openxml.OpenXMLFilter;
import net.sf.okapi.common.filterwriter.ZipFilterWriter;
import net.sf.okapi.common.Event;

import org.junit.Assert;
import org.junit.Test;

public class OpenXMLRoundTripTest {

	@Test
	public void runTest () {
		String glug = "docx";
//		String glug = "pptx";
//		String glug = "xlsx";
		String sPath,sInputPath,sOutputPath;
		Event event;
		URI uri;
		String sUserDir;
		OpenXMLFilter filter = null;		
		try {
			filter = new OpenXMLFilter(new PigLatinTranslator(),"pl"); // $$$
//			filter = new OpenXMLFilter();
			filter.setOptions("en-US", "UTF-8", true);
			sUserDir = System.getProperty("user.dir").replace('\\','/').toLowerCase();
			sInputPath = sUserDir + "/data/";
			sOutputPath = sUserDir + "/output/";
//			uri = new URI(sInputPath+"sample."+glug);
//			uri = new URI(sInputPath+"TranslationServicesOff.docx");
//			uri = new URI(sInputPath+"gtsftopic.docx");
			uri = new URI(sInputPath+"OpenXML_text_reference_document.docx");
//			uri = new URI(sInputPath+"OpenXML_text_reference_v1_1.docx");
//			uri = new URI(sInputPath+"OpenXML_text_reference_v1_2.docx");
			try
			{
//				filter.open(uri,true,3);
				filter.open(uri,false,3); // DWH 3-27-09
			}
			catch(Exception e)
			{
				throw new RuntimeException(e);				
			}
			
			ZipFilterWriter writer = new ZipFilterWriter();

			writer.setOptions("pl", "UTF-8"); // $$$
//			writer.setOptions("en-US", "UTF-8");

//			writer.setOutput(sOutputPath+"OutputSample."+glug);
//			writer.setOutput(sOutputPath+"OutputTranslationServicesOff.docx");
//			writer.setOutput(sOutputPath+"OutputGtsftopic.docx");
			writer.setOutput(sOutputPath+"OpenXML_text_reference_document.docx");
//			writer.setOutput(sOutputPath+"OpenXML_text_reference_v1_1.docx");
//			writer.setOutput(sOutputPath+"OpenXML_text_reference_v1_2.docx");
			
			while ( filter.hasNext() ) {
				event = filter.next();
				if (event!=null)
					writer.handleEvent(event);
				else
					event = null; // just for debugging
			}
			writer.close();
		}
		catch ( Throwable e ) {
			e.printStackTrace();
			Assert.fail();
		}
		finally {
			if ( filter != null ) filter.close();
		}
	}

}
