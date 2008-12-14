/*===========================================================================
  Copyright (C) 2008 by the Okapi Framework contributors
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
============================================================================*/

package net.sf.okapi.applications.rainbow.packages.ttx;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.TimeZone;

import net.sf.okapi.applications.rainbow.packages.BaseWriter;
import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.Util;
import net.sf.okapi.common.XMLWriter;
import net.sf.okapi.common.filters.FilterEvent;
import net.sf.okapi.common.resource.Code;
import net.sf.okapi.common.resource.TextFragment;
import net.sf.okapi.common.resource.TextUnit;

/**
 * Implements IWriter for TTX-based translation packages.
 */
public class Writer extends BaseWriter {
	
	private static final String   DTD_SETTINGS_FILE = "okapiTTX.ini";
	private static final String   EXTENSION = ".xml.ttx";
	
	private XMLWriter writer;
	private SimpleDateFormat dateFmt;

	public Writer() {
		super();
		// CreationDate format: "20030426T162120Z"
		dateFmt = new SimpleDateFormat("yyyyMMdd'T'HHmmZ");
		dateFmt.setTimeZone(TimeZone.getTimeZone("GMT"));
	}
	
	public String getPackageType () {
		return "ttx";
	}

	public String getReaderClass () {
		//TODO: Get the class path from object
		return "net.sf.okapi.applications.rainbow.packages.ttx.Reader";
	}
	
	@Override
	public void writeStartPackage ()
	{
		InputStream in = null;
		FileOutputStream out = null;
		try {
			manifest.setSourceLocation("work");
			manifest.setTargetLocation("work");
			manifest.setOriginalLocation("original");
			manifest.setDoneLocation("done");
			super.writeStartPackage();
			// Copy the tag settings file to the root of the package
			in = getClass().getResourceAsStream(DTD_SETTINGS_FILE);
			out = new FileOutputStream(manifest.getRoot() + File.separator + DTD_SETTINGS_FILE);
			byte[] buffer = new byte[1024];
			int bytesRead;
			while ( (bytesRead = in.read(buffer)) >= 0 ) {
				out.write(buffer, 0, bytesRead);
			}
		}
		catch ( IOException e ) {
			throw new RuntimeException(e);
		}
		finally {
			try {
				if ( in != null ) in.close();
				if ( out != null ) out.close();
			}
			catch ( IOException e ) {} // Swallow it
		}
	}

	public void createOutput (int docID,
		String relativeSourcePath,
		String relativeTargetPath,
		String sourceEncoding,
		String targetEncoding,
		String filterID,
		IParameters filterParams)
	{
		relativeWorkPath = relativeSourcePath + EXTENSION; 
		super.createOutput(docID, relativeSourcePath, relativeTargetPath,
			sourceEncoding, targetEncoding, filterID, filterParams);
		if ( writer == null ) writer = new XMLWriter();
		else writer.close(); // Else: make sure the previous output is closed
		
		writer.create(manifest.getRoot() + File.separator
			+ ((manifest.getSourceLocation().length() == 0 ) ? "" : (manifest.getSourceLocation() + File.separator)) 
			+ relativeWorkPath);
	}

	public void close () {
		if ( writer != null ) {
			writer.close();
			writer = null;
		}
	}

	public String getName() {
		return getClass().getName();
	}

	public IParameters getParameters () {
		return null;
	}

	public void setParameters (IParameters params) {
	}

	public FilterEvent handleEvent (FilterEvent event) {
		switch ( event.getEventType() ) {
		case START_DOCUMENT:
			processStartDocument();
			break;
		case TEXT_UNIT:
			processTextUnit((TextUnit)event.getResource());
			break;
		case END_DOCUMENT:
			processEndDocument();
			break;
		}
		return event;
	}

	public void processStartDocument () {
		writer.writeStartDocument();

		writer.writeStartElement("TRADOStag");
		writer.writeAttributeString("Version", "2.0");
	
		writer.writeStartElement("FrontMatter");

		writer.writeStartElement("ToolSettings");
		writer.writeAttributeString("CreationDate", dateFmt.format(new java.util.Date())); 
		writer.writeAttributeString("CreationTool", getClass().getName());
		writer.writeAttributeString("CreationToolVersion", "1"); // TODO: toolversion
		writer.writeEndElement(); // ToolSettings
		
		writer.writeStartElement("UserSettings");
		writer.writeAttributeString("DataType", "XML");
		writer.writeAttributeString("O-Encoding", "UTF-8");
		writer.writeAttributeString("SettingsName", "okapiTTX");
		writer.writeAttributeString("SettingsPath", DTD_SETTINGS_FILE);
		writer.writeAttributeString("SourceLanguage", manifest.getSourceLanguage());
		writer.writeAttributeString("TargetLanguage", manifest.getTargetLanguage());
		writer.writeAttributeString("TargetDefaultFont", "");
		writer.writeAttributeString("SourceDocumentPath", relativeSourcePath);
		writer.writeAttributeString("SettingsRelativePath", getSettingsRelativePath());
		writer.writeAttributeString("PlugInInfo", "");
		writer.writeEndElement(); // UserSettings
		
		writer.writeEndElement(); // FrontMatter
		
		writer.writeStartElement("Body");
		writer.writeStartElement("Raw");
		
		writer.writeRawXML("<ut Class=\"procinstr\" DisplayText=\"Instruction\">&lt;?xml version=&quot;1.0&quot;?&gt;</ut>");
		writer.writeLineBreak();
		writer.writeRawXML("<ut Type=\"start\" Style=\"external\" RightEdge=\"angle\" DisplayText=\"okapiTTX\">&lt;okapiTTX&gt;</ut>");
		writer.writeLineBreak();
	}

	private void processEndDocument () {
		try {
			writer.writeRawXML("<ut Type=\"end\" Style=\"external\" LeftEdge=\"angle\" DisplayText=\"okapiTTX\">&lt;/okapiTTX&gt;</ut>");
			writer.writeEndElement(); // Raw
			writer.writeEndElement(); // Body
			writer.writeEndElement(); // TRADOStag
			writer.writeEndDocument();
			manifest.addDocument(docID, relativeWorkPath, relativeSourcePath,
					relativeTargetPath, sourceEncoding, targetEncoding, filterID);
		}
		finally {
			close();
		}
	}

	private void processTextUnit (TextUnit item) {
		String name = item.getName();
		writer.writeRawXML(String.format(
			"<ut Type=\"start\" Style=\"external\" RightEdge=\"angle\" DisplayText=\"u\">&lt;u id='%s'%s&gt;</ut>",
			item.getId(), (name.length()>0 ? " rn='"+name+"'" : "") ));
		//TODO: MUST implement the <df> tag to set the font, otherwise non-ANSI display as ????
		if ( item.hasTarget(trgLang) ) {
			//TODO: Info about the match
			writer.writeStartElement("Tu");
			//TODO: writer.writeAttributeString("Origin", "manual");
			//TODO: writer.writeAttributeString("MatchPercent", "100");
			writer.writeStartElement("Tuv");
			writer.writeAttributeString("Lang", manifest.getSourceLanguage());
			writeContent(item.getSourceContent());
			writer.writeEndElement(); //Tuv
			writer.writeStartElement("Tuv");
			writer.writeAttributeString("Lang", manifest.getTargetLanguage());
			writeContent(item.getTargetContent(trgLang));
			writer.writeEndElement(); //Tuv
			writer.writeEndElement(); //Tu

			// Write the item in the TM if needed
			tmxWriter.writeItem(item, null);
		}
		else {
			writeContent(item.getSourceContent());
		}
		
		writer.writeRawXML("<ut Type=\"end\" Style=\"external\" LeftEdge=\"angle\" DisplayText=\"u\">&lt;/u&gt;</ut>");
		writer.writeLineBreak();
	}

	private void writeContent (TextFragment srcContent) {
		String text = srcContent.getCodedText();
		List<Code> codes = srcContent.getCodes();
		Code code;
		for ( int i=0; i<text.length(); i++ ) {
			switch ( text.charAt(i) ) {
			case TextFragment.MARKER_OPENING:
				code = codes.get(TextFragment.toIndex(text.charAt(++i)));
				writer.writeStartElement("ut");
				writer.writeAttributeString("Type", "start");
				writer.writeAttributeString("RightEdge", "angle");
				writer.writeAttributeString("DisplayText", code.getData());
				writer.writeString(code.getData());
				writer.writeEndElement(); // ut
				break;
			case TextFragment.MARKER_CLOSING:
				code = codes.get(TextFragment.toIndex(text.charAt(++i)));
				writer.writeStartElement("ut");
				writer.writeAttributeString("Type", "end");
				writer.writeAttributeString("LeftEdge", "angle");
				writer.writeAttributeString("DisplayText", code.getData());
				writer.writeString(code.getData());
				writer.writeEndElement(); // ut
				break;
			case TextFragment.MARKER_ISOLATED:
			case TextFragment.MARKER_SEGMENT:
				code = codes.get(TextFragment.toIndex(text.charAt(++i)));
				writer.writeStartElement("ut");
				writer.writeAttributeString("DisplayText", code.getData());
				writer.writeString(code.getData());
				writer.writeEndElement(); // ut
				break;
			default:
				//TODO: Use a content object like XLIFF and TMX, too slow here
				writer.writeString(String.valueOf(text.charAt(i)));
			}
		}
	}
	
	/*
	private void writeSegment (boolean isSegment,
		TextFragment srcFragment,
		TextFragment trgFragment)
	{
		if ( isSegment ) { // Start the segment and the source
			writer.writeStartElement("Tu");
			//TODO: writer.writeAttributeString("Origin", "manual");
			//TODO: writer.writeAttributeString("MatchPercent", "100");
			writer.writeStartElement("Tuv");
			writer.writeAttributeString("Lang", manifest.getSourceLanguage());
		}
		// Write the source content
		writeContent(srcFragment);
		if ( isSegment ) { // End the source segment if needed
			writer.writeEndElement(); //Tuv
		}
		
		// Target: if it is a segment and we have a target
		if ( isSegment && ( trgFragment != null )) { // Has a target
			writer.writeStartElement("Tuv");
			writer.writeAttributeString("Lang", manifest.getTargetLanguage());
			writeContent(trgFragment);
			writer.writeEndElement(); //Tuv
		}
		// Write end of segment if needed
		if ( isSegment ) {
			writer.writeEndElement(); //Tu
		}
	}*/
	
	private String getSettingsRelativePath () {
		String dir = Util.getDirectoryName(relativeSourcePath);
		int n = dir.length();
		if ( n == 0 ) return ".\\" + DTD_SETTINGS_FILE; // TTX are under Windows, use DOS separator
		dir = dir.replaceAll("\\"+File.separator, ""); // Using backslash for regex escape
		n -= (dir.length()-1); // Sub-folder count +1 
		StringBuilder relation = new StringBuilder();
		for ( int i=0; i<n; i++ ) {
			relation.append("..\\");
		}
		return relation.toString() + DTD_SETTINGS_FILE;
	}

}
