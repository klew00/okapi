/*===========================================================================*/
/* Copyright (C) 2008 Yves Savourel (at ENLASO Corporation)                  */
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

package net.sf.okapi.applications.rainbow.packages.ttx;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.TimeZone;

import net.sf.okapi.applications.rainbow.packages.BaseWriter;
import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.Util;
import net.sf.okapi.common.XMLWriter;
import net.sf.okapi.common.resource.CodeFragment;
import net.sf.okapi.common.resource.IContainer;
import net.sf.okapi.common.resource.IExtractionItem;
import net.sf.okapi.common.resource.IFragment;

/**
 * Implements IWriter for TTX-based translation packages.
 */
public class Writer extends BaseWriter {
	
	private static final String   DTD_SETTINGS_FILE = "okapiTTX.ini";
	
	private XMLWriter        writer;
	private String           relativePath;
	private int              docKey;
	private SimpleDateFormat dateFmt;


	public Writer() {
		super();
		//CreationDate="20071026T162120Z"
		dateFmt = new SimpleDateFormat("yyyyMMdd'T'HHmmZ");
		dateFmt.setTimeZone(TimeZone.getTimeZone("GMT"));
	}
	
	public String getPackageType () {
		return "ttx";
	}

	@Override
	public void writeStartPackage ()
	{
		InputStream in = null;
		FileOutputStream out = null;
		try {
			manifest.setSourceLocation("work");
			manifest.setTargetLocation("work");
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
		catch ( Exception e ) {
			throw new RuntimeException(e);
		}
		finally {
			try {
				if ( in != null ) in.close();
				if ( out != null ) out.close();
			}
			catch ( Exception e ) {} // Swallow it
		}
	}

	public void createDocument (int docID,
		String newRelativePath,
		String inputEncoding,
		String outputEncoding,
		String filterSettings,
		IParameters filterParams)
	{
		super.createDocument(docID, relativePath, inputEncoding,
			outputEncoding, filterSettings, filterParams);
		if ( writer == null ) writer = new XMLWriter();
		else writer.close(); // Else: make sure the previous output is closed
		
		this.docKey = docID;
		this.relativePath = newRelativePath + ".xml.ttx";

		writer.create(manifest.getRoot() + File.separator
			+ ((manifest.getSourceLocation().length() == 0 ) ? "" : (manifest.getSourceLocation() + File.separator)) 
			+ relativePath);
	}

	public void writeEndDocument () {
		writer.writeRawXML("<ut Type=\"end\" Style=\"external\" LeftEdge=\"angle\" DisplayText=\"okapiTTX\">&lt;/okapiTTX&gt;</ut>");
		writer.writeEndElement(); // Raw
		writer.writeEndElement(); // Body
		writer.writeEndElement(); // TRADOStag
		writer.writeEndDocument();
		writer.close();
		manifest.addDocument(docKey, relativePath);
	}

	private void writeContent (IContainer content) {
		List<IFragment> fragList = content.getFragments();
		for ( IFragment frag : fragList ) {
			if ( frag.isText() ) {
				writer.writeString(frag.toString());
			}
			else {
				switch ( ((CodeFragment)frag).type ) {
				case IContainer.CODE_OPENING:
					writer.writeStartElement("ut");
					writer.writeAttributeString("Type", "start");
					writer.writeAttributeString("RightEdge", "angle");
					writer.writeAttributeString("DisplayText", frag.toString());
					writer.writeString(frag.toString());
					writer.writeEndElement(); // ut
					break;
				case IContainer.CODE_CLOSING:
					writer.writeStartElement("ut");
					writer.writeAttributeString("Type", "end");
					writer.writeAttributeString("LeftEdge", "angle");
					writer.writeAttributeString("DisplayText", frag.toString());
					writer.writeString(frag.toString());
					writer.writeEndElement(); // ut
					break;
				case IContainer.CODE_ISOLATED:
					writer.writeStartElement("ut");
					writer.writeAttributeString("DisplayText", frag.toString());
					writer.writeString(frag.toString());
					writer.writeEndElement(); // ut
					break;
				}
			}
		}
	}
	
	public void writeItem (IExtractionItem sourceItem,
		IExtractionItem targetItem,
		int status)
	{
		writer.writeRawXML(String.format(
			"<ut Type=\"start\" Style=\"external\" RightEdge=\"angle\" DisplayText=\"u\">&lt;u id='%s'&gt;</ut>",
			sourceItem.getID()));
		//TODO: MUST implement the <df> tag to set the font, otherwise non-ANSI display as ????
		if (( sourceItem.hasTarget() ) && ( targetItem != null )) {
			//TODO: Info about the match
			writer.writeStartElement("Tu");
			writer.writeStartElement("Tuv");
			writer.writeAttributeString("Lang", manifest.getSourceLanguage());
			writeContent(sourceItem.getContent());
			writer.writeEndElement(); //Tuv
			writer.writeStartElement("Tuv");
			writer.writeAttributeString("Lang", manifest.getTargetLanguage());
			writeContent(targetItem.getContent());
			writer.writeEndElement(); //Tuv
			writer.writeEndElement(); //Tu
		}
		else {
			writeContent(sourceItem.getContent());
		}
		
		writer.writeRawXML("<ut Type=\"end\" Style=\"external\" LeftEdge=\"angle\" DisplayText=\"u\">&lt;/u&gt;</ut>");
		writer.writeLineBreak();
	}

	private String getSettingsRelativePath () {
		String dir = Util.getDirectoryName(relativePath);
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
	
	public void writeStartDocument () {
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
		writer.writeAttributeString("SourceDocumentPath", relativePath);
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

}
