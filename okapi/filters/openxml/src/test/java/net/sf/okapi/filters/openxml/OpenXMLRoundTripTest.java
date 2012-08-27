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

package net.sf.okapi.filters.openxml;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.TestUtil;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.resource.RawDocument;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.junit.Test;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This tests OpenXMLFilter (including OpenXMLContentFilter) and
 * OpenXMLZipFilterWriter (including OpenXMLContentSkeleton writer)
 * by filtering, automatically translating, and then writing the
 * zip file corresponding to a Word, Excel or Powerpoint 2009 file, 
 * then comparing it to a gold file to make sure nothing has changed.
 * It does this with a specific list of files.
 * 
 * <p>This is done with no translator first, to make sure the same
 * file is created that was filtered in the first place.  Then it
 * is translated into Pig Latin by PigLatinTranslator, translated so
 * codes are expanded by CodePeekTranslator, and then translated to
 * see a view like the translator will see by TagPeekTranslator.
 */

public class OpenXMLRoundTripTest {

	private ZipCompare zc=null;
	private LocaleId locLA = LocaleId.fromString("la");
	private LocaleId locENUS = LocaleId.fromString("en-us");

	private static Logger LOGGER = LoggerFactory.getLogger(OpenXMLRoundTripTest.class.getName());
	private boolean allGood=true;
	private ConditionalParameters cparams; // DWH 6-18-09
	private boolean bSquishy=true; // DWH 7-16-09

	@Test
	public void runTest () {
		cparams = getParametersFromUserInterface();

		ArrayList<String> themfiles = new ArrayList<String>();
		zc = new ZipCompare();

		themfiles.add("BoldWorld.docx");
		themfiles.add("Deli.docx");
		themfiles.add("DocProperties.docx");
		themfiles.add("Escapades.docx");
		themfiles.add("Addcomments.docx");
		themfiles.add("sample.docx");
		themfiles.add("styles.docx");
		themfiles.add("sample.pptx");
		themfiles.add("sample.xlsx");
		themfiles.add("sampleMore.xlsx");
		themfiles.add("sampleMore.pptx");
		themfiles.add("OpenXML_text_reference_v1_2.docx");
		themfiles.add("Mauris.docx");
		themfiles.add("Hidden.docx");
		themfiles.add("ExcelColors.xlsx");
		themfiles.add("UTF8.docx");
		themfiles.add("commentTable.xlsx");
		themfiles.add("InsertText.pptx");
		themfiles.add("Endpara.pptx");
		themfiles.add("TestDako2.docx");
		themfiles.add("TextBoxes.docx");
		themfiles.add("OutOfTheTextBox.docx");
		themfiles.add("MissingPara.docx");
		themfiles.add("EndGroup.docx");
		themfiles.add("Practice2.docx");
		themfiles.add("Hangs.docx");
		themfiles.add("TestLTinsideBoxFails.docx");

		//		themfiles.add("2008FinancialsDecember.xlsx");
//		themfiles.add("welfarelesson_new_skin_Mar3.pptx");	
//		themfiles.add("glorp.docx");

		for(String s : themfiles)
		{
//   	runOneTest(s,false,false); // English
  		runOneTest(s,true,false);  // PigLatin
// 		runOneTest(s,false,true);  // Tags
// 		runOneTest(s,true,true);   // Codes
		}
		assertTrue("Some Roundtrip files failed.",allGood);
	}

	public void runOneTest (String filename, boolean bTranslating, boolean bPeeking) {
		String sInputPath=null,sOutputPath=null,sGoldPath=null;
		Event event;
		//File filly;
		URI uri;
		OpenXMLFilter filter = null;
		//BufferedInputStream bis;
		boolean rtrued2;
		try {
			if (bPeeking)
			{
				if (bTranslating)
					filter = new OpenXMLFilter(new CodePeekTranslator(), locENUS);
				else
					filter = new OpenXMLFilter(new TagPeekTranslator(), locENUS);
			}
			else if (bTranslating)
				filter = new OpenXMLFilter(new PigLatinTranslator(), locLA);
			else
				filter = new OpenXMLFilter();
			
			filter.setParameters(cparams);

			filter.setOptions(locENUS, "UTF-8", true);
//			filter.setLogLevel(Level.FINEST);
//			filter.setLogLevel(Level.FINE);
			
			sInputPath = TestUtil.getParentDir(this.getClass(), "/BoldWorld.docx");
			sOutputPath = sInputPath + "output/";
			sGoldPath = sInputPath + "gold/";

//			URL url = OpenXMLRoundTripTest.class.getResource("/BoldWorld.docx");
//			String sUserDir = Util.getDirectoryName(url.getPath());
//     		sInputPath = sUserDir + "/";
//			sOutputPath = sUserDir + "/output/";
//			sGoldPath = sUserDir + "/gold/";
			
//			sUserDir = OpenXMLRoundTripTest.class.getProtectionDomain().getCodeSource().getLocation().toExternalForm();;
//			sUserDirURI = sUserDir.substring(0,sUserDir.length()-5); // DWH 6-9-09
//			sUserDir = sUserDir.substring(6,sUserDir.length()-5);
////			sUserDir = System.getProperty("user.dir").replace('\\','/').toLowerCase();
//			sInputPath = sUserDirURI + "/data/"; // DWH 6-9-09
////			sInputPath = sUserDir + "/data/"; // DWH 6-9-09
//			sOutputPath = sUserDir + "/ootput/";
//			sGoldPath = sUserDir + "/gold/";
			uri = new File(sInputPath+filename).toURI();
			
			try
			{
//				filly = new File(sInputPath+filename);
//				bis = new BufferedInputStream(new FileInputStream(filly));
//				filter.open(new RawDocument(bis,"UTF-8","en-US"),true,false,Level.FINEST); // DWH 6-09-09			

				filter.open(new RawDocument(uri,"UTF-8", locENUS),true,bSquishy); // DWH 7-16-09 squishiness
			}
			catch(Exception e)
			{
				throw new RuntimeException(e);				
			}
			
			OpenXMLZipFilterWriter writer = new OpenXMLZipFilterWriter(); // DWH 4-8-09 was just ZipFilterWriter

			if (bPeeking)
				writer.setOptions(locENUS, "UTF-8");
			else if (bTranslating)
				writer.setOptions(locLA, "UTF-8");
			else
				writer.setOptions(locENUS, "UTF-8");

			writer.setOutput(sOutputPath+ (bPeeking ? (bTranslating ? "Peek" : "Tag") : (bTranslating ? "Tran" : "Out"))+filename);
			
			while ( filter.hasNext() ) {
				event = filter.next();
				if (event!=null)
				{
					writer.handleEvent(event);
				}
				else
					event = null; // just for debugging
			}
			writer.close();
			rtrued2 = zc.zipsExactlyTheSame(sOutputPath+(bPeeking ? (bTranslating ? "Peek" : "Tag") : (bTranslating ? "Tran" : "Out"))+filename,
					   					      sGoldPath+(bPeeking ? (bTranslating ? "Peek" : "Tag") : (bTranslating ? "Tran" : "Out"))+filename);
			if (!rtrued2) // DWH 12-18-09 log only if it failed
				LOGGER.info((bPeeking ? (bTranslating ? "Peek" : "Tag") : (bTranslating ? "Tran" : "Out"))+filename+
						   (rtrued2 ? " SUCCEEDED" : " FAILED"));
			if (!rtrued2)
				allGood = false;
		}
		catch ( Throwable e ) {
			LOGGER.warn(e.getMessage());
			fail("An unexpected exception was thrown on file '"+filename+"': " + e);
		}
		finally {
			if ( filter != null ) filter.close();
		}
	}
	private ConditionalParameters getParametersFromUserInterface()
	{
		ConditionalParameters parms;
//    Choose the first to get the UI $$$
//		parms = (new Editor()).getParametersFromUI(new ConditionalParameters());
		parms = new ConditionalParameters();
		return parms;
	}
}
