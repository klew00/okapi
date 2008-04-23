/*===========================================================================*/
/* Copyright (C) 2007-2008 ENLASO Corporation, Okapi Development Team        */
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

package tests;

import java.io.File;

import net.sf.okapi.Filter.FilterItem;
import net.sf.okapi.Filter.FilterItemText;
import net.sf.okapi.Filter.IFilterItem;
import net.sf.okapi.Filter.InlineCode;
import net.sf.okapi.Library.Base.ILog;
import net.sf.okapi.Library.Base.Log;

public class Tests {

	static final File INDEX_DIR = new File("index");
	
	public static void main(String[] args) throws Exception
	{
		@SuppressWarnings("unused")
		int nTmp = Integer.parseInt("00c6", 16);
		
		ILog myLog = new Log();
		myLog.beginProcess("=== Start process");
		myLog.beginTask("--- Start task");
		
/*		LanguageManager LM = new LanguageManager(); 
		LM.loadList("C:\\Okapi\\Dev\\Java\\OkapiJava\\data\\common\\Languages.xml");
		for ( int i=0; i<LM.getCount(); i++ ) {
			LanguageItem LI = LM.getItem(i);
			myLog.message(String.format("C='%s', N='%s', E='%s', L='%d'",
				LI.getCode(), LI.getName(), LI.getEncoding(), LI.getLCID()));
		}
*/		
		IFilterItem FI = new FilterItem();

/*		Reader TMXR = new Reader(myLog);
		TMXR.open("C:/Tmp/TMXTest.tmx", null, null);
		IFilterItem FI;
		Item IT;
		while ( TMXR.readItem() > Reader.RESULT_ENDOFDOC ) {
			FI = TMXR.getSource();
			myLog.message(String.format("S='%s'", FI.getText(FilterItemText.GENERIC)));
			while ( (IT = TMXR.getNextTarget()) != null ) {
				myLog.message(String.format("T(%s)='%s'",
					IT.getLang(), IT.getFI().getText(FilterItemText.GENERIC)));
			}
		}
		myLog.message(String.format("version=%s", TMXR.getVersion()));
		TMXR.close();
*/	

		FI = new FilterItem();
		FI.reset();

		String sTmp = "abc квс \t\r\n";
		FI.setText(sTmp);
		if ( !FI.getText(0).equals(sTmp) )
			myLog.error("GetText error.");

		myLog.newLine();
		myLog.message("----- Test Coord:");
		if ( FI.hasCoord() ) myLog.error("HasCoord error.");
		sTmp = "1;2;3;4";
		FI.setCoord(sTmp);
		myLog.message("["+FI.getCoord()+"]");
		if ( !FI.hasCoord() ) myLog.error("!HasCoord error.");
		if ( !FI.getCoord().equals(sTmp) ) myLog.error("Coord error.");
		FI.setCoord("#;#;33;44");
		myLog.message("["+FI.getCoord()+"]");
		FI.setX(1.1f);
		FI.setY(2.0f);
		myLog.message("["+FI.getCoord()+"]");
		
		myLog.newLine();
		myLog.message("----- Test Font:");
		sTmp = "fname;fsize;fweight;fstyle;fcharset";
		FI.setFont(sTmp);
		myLog.message("["+FI.getFont()+"]");
		if ( !FI.getFont().equals(sTmp) ) myLog.error("Font error.");

		myLog.newLine();
		myLog.message("----- Test ResName:");
		sTmp = "myResname";
		FI.setResName(sTmp);
		myLog.message("["+FI.getResName()+"]");
		if ( !FI.getResName().equals(sTmp) ) myLog.error("ResName error.");

		myLog.newLine();
		myLog.message("----- Test Properties:");
		FI.reset();
		myLog.message("Initial list: [" +
			(FI.listProperties().length()==0 ? "OK" : "Not OK!") + "]");
		FI.setProperty("prop1", "value1");
		FI.setProperty("prop2", "value2");
		FI.setProperty("prop3", "value3");
		myLog.message("List 1: ["+FI.listProperties()+"]");
		String[] aKeys = FI.listProperties().split(";");
		for ( String sKey : aKeys )
		{
			myLog.message("Property : ["+sKey+"] = [" + FI.getProperty(sKey) + "]");
		}
		FI.setProperty("prop2", null);
		myLog.message("List after prop2=null: ["+FI.listProperties()+"]");
		aKeys = FI.listProperties().split(";");
		for ( String sKey : aKeys )
		{
			myLog.message("Property : ["+sKey+"] = [" + FI.getProperty(sKey) + "]");
		}
		// Test the copy
		IFilterItem TmpFI = new FilterItem();
		TmpFI.copyFrom(FI);
		FI.reset();
		myLog.message("Copied properties: ["+TmpFI.listProperties()+"]");
		aKeys = TmpFI.listProperties().split(";");
		for ( String sKey : aKeys )
		{
			myLog.message("Property : ["+sKey+"] = [" + TmpFI.getProperty(sKey) + "]");
		}

		myLog.newLine();
		myLog.message("----- Test RemoveEnd:");
		FI.reset();
		FI.appendText("123");
		FI.appendCode(InlineCode.ISOLATED, null, "[code]");
		FI.appendText("456");
		myLog.message("Start: ["+FI.getText(FilterItemText.GENERIC)+"]");
		FI.removeEnd(2);
		myLog.message("After RemoveEnd(2): ["+FI.getText(FilterItemText.GENERIC)+"]");
		if ( FI.getTextLength(FilterItemText.CODED) != 6 )
			myLog.error("RemoveEnd error.");
		FI.removeEnd(10);
		myLog.message("After RemoveEnd(10): ["+FI.getText(FilterItemText.GENERIC)+"]");
		if ( FI.getTextLength(FilterItemText.CODED) != 5 )
			myLog.error("RemoveEnd error.");

		myLog.newLine();
		myLog.message("Codes:");
		for ( int i=0; i<FI.getCodeCount(); i++ )
		{
			myLog.message(String.format("Code %1$d = '%2$s'",
				i, FI.getCode(i, true)));
		}

		myLog.newLine();
		myLog.message("----- Test Text:");
		FI.reset();
		FI.appendText("Text before");
		FI.appendChar(' ');
		FI.appendCode(InlineCode.ISOLATED, "name1", "{/code\\}");
		FI.appendChar(' ');
		FI.appendCode(InlineCode.ISOLATED, "protected", "{/protected\\}");
		FI.appendChar(' ');
		FI.appendCode(InlineCode.OPENING, "name2", "<code&>");
		FI.appendText("Text after.");
		FI.appendCode(InlineCode.CLOSING, "name2", "</code&>");

		myLog.newLine();
		myLog.message("Codes:");
		for ( int i=0; i<FI.getCodeCount(); i++ )
		{
			myLog.message(String.format("Code %1$d: data='%2$s' id='%3$d' label='%4$s'",
				i, FI.getCode(i, true), FI.getCodeID(i), FI.getCodeLabel(i)));
		}

		myLog.newLine();
		String sBuffer = FI.getText(FilterItemText.GENERIC);
		myLog.message("Generic before = ["+sBuffer+"]");
		sTmp = FI.getText(0);
		String sCodes = FI.getCodeMapping();
		myLog.message("     Coded text: ["+sTmp+"]");
		myLog.message("  Codes mapping: ["+sCodes+"]");
		FI.reset();
		FI.modifyText(sTmp);
		FI.setCodeMapping(sCodes);
		sTmp = FI.getText(FilterItemText.GENERIC);
		myLog.message(" Generic after = ["+sTmp+"]");
		if ( !sTmp.equals(sBuffer) ) myLog.error("Problem using CodeMapping and/or GetText.");

		myLog.newLine();
		myLog.message("      Coded = [" +
			FI.getText(FilterItemText.CODED) + "] Len=" +
			FI.getTextLength(FilterItemText.CODED));

		myLog.message(" Codes only = [" +
			FI.getText(FilterItemText.CODESONLY) + "] Len=" +
			FI.getTextLength(FilterItemText.CODESONLY));

		myLog.message("      Plain = [" +
			FI.getText(FilterItemText.PLAIN) + "] Len=" +
			FI.getTextLength(FilterItemText.PLAIN));

		myLog.message("        RTF = [" +
			FI.getText(FilterItemText.RTF) + "] Len=" +
			FI.getTextLength(FilterItemText.RTF));

		myLog.message("      XLIFF = [" +
			FI.getText(FilterItemText.XLIFF) + "] Len=" +
			FI.getTextLength(FilterItemText.XLIFF));

		myLog.message("    XLIFFGX = [" +
			FI.getText(FilterItemText.XLIFFGX) + "] Len=" +
			FI.getTextLength(FilterItemText.XLIFFGX));

		myLog.message("  XLIFF+RTF = [" +
			FI.getText(FilterItemText.XLIFFRTF) + "] Len=" +
			FI.getTextLength(FilterItemText.XLIFFRTF));

		myLog.message("XLIFFGX+RTF = [" +
			FI.getText(FilterItemText.XLIFFGXRTF) + "] Len=" +
			FI.getTextLength(FilterItemText.XLIFFGXRTF));

		myLog.message("        TMX = [" +
			FI.getText(FilterItemText.TMX) + "] len=" +
			FI.getTextLength(FilterItemText.TMX));

		myLog.message("   Original = [" +
				FI.getText(FilterItemText.ORIGINAL) + "] len=" +
				FI.getTextLength(FilterItemText.ORIGINAL));

		String sG = FI.getText(FilterItemText.GENERIC);
		myLog.message("    Generic = [" +
				sG + "] len=" + FI.getTextLength(FilterItemText.GENERIC));
		
		sG = FilterItem.genericToCoded(sG, FI);
		FI.modifyText(sG);
		myLog.message("Re-Original = [" +
				FI.getText(FilterItemText.ORIGINAL) + "] len=" +
				FI.getTextLength(FilterItemText.ORIGINAL));

		

//		myLog.newLine();
//		myLog.message("Word count = " + WordCount(FI));

		
		myLog.newLine();
/*
		
		FilterAccess FA = new FilterAccess(myLog);
		FA.loadList("C:\\Okapi\\Dev\\Java\\OkapiJava\\data\\common\\Filters.xml");
		FA.loadFilter("okf_properties");
		IFilter Flt = FA.getFilter();
		Flt.initialize(myLog);
		
		myLog.message("Filter ID = " + Flt.getIdentifier());
		myLog.message("Filter datatype = " + Flt.getDefaultDatatype());
		myLog.message("Filter description =" + Flt.getDescription());
		
		//UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		//UIManager.put("swing.boldMetal", false);
		
		Flt.editSettings("okf_properties@myParamters", true);
		
		String sPath = "C:\\Okapi\\Data\\Test\\Test01.properties";
		Flt.openInputFile(sPath, "en", "windows-1252");
		Flt.setOutputOptions("fr", "windows-1252");
		Flt.openOutputFile(sPath+".out");
		int nRes;
		FI.reset();
		do {
			nRes = Flt.readItem();
			if ( nRes == FilterItemType.TEXT ) {
				FI = Flt.getItem();
				myLog.message(FI.getText(FilterItemText.GENERIC));
				//String sText = FI.GetText(FilterItemText.CODED);
			}
			Flt.writeItem();
		} while ( nRes > FilterItemType.ENDINPUT );
		Flt.closeOutput();
		Flt.closeInput();
*/		
		myLog.endTask("--- End task");
		myLog.endProcess("=== End process");
		
	}

}
