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

package net.sf.okapi.Package.ttx;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

import net.sf.okapi.Filter.FilterItemText;
import net.sf.okapi.Filter.IFilterItem;
import net.sf.okapi.Library.Base.ILog;
import net.sf.okapi.Library.Base.Utils;
import net.sf.okapi.Package.BaseWriter;
import net.sf.okapi.Format.XML.XMLWriter;

/**
 * Implements IWriter for TTX-based translation packages.
 */
public class Writer extends BaseWriter {
	
	private static final String   DTD_SETTINGS_FILE = "okapiTTX.ini";
	
	private XMLWriter        m_XW = null;
	private String           m_sRelativePath;
	private int              m_nDKey;
	private SimpleDateFormat dateFmt;

	public Writer(ILog log) {
		super(log);
		//CreationDate="20071026T162120Z"
		dateFmt = new SimpleDateFormat("yyyyMMddTHHmmZ");
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
			super.writeStartPackage();
			// Copy the tag settings file to the root of the package
			in = getClass().getResourceAsStream(DTD_SETTINGS_FILE);
			Utils.createDirectories(m_Mnf.getRoot() + File.separator);
			out = new FileOutputStream(m_Mnf.getRoot() + File.separator + DTD_SETTINGS_FILE);
			byte[] buffer = new byte[1024];
			int bytesRead;
			while ( (bytesRead = in.read(buffer)) >= 0 ) {
				out.write(buffer, 0, bytesRead);
			}
		}
		catch ( Exception E ) {
			m_Log.error(E.getLocalizedMessage());
		}
		finally {
			try {
				if ( in != null ) in.close();
				if ( out != null ) out.close();
			}
			catch ( Exception E ) {
				m_Log.error(E.getLocalizedMessage());
			}
		}
	}

	public void createDocument (int p_nDKey,
		String p_sRelativePath)
	{
		try {
			if ( m_XW == null ) m_XW = new XMLWriter();
			else m_XW.close(); // Else: make sure the previous output is closed
		
			m_nDKey = p_nDKey;
			m_sRelativePath = p_sRelativePath + ".xml.ttx";

			m_XW.create(m_Mnf.getRoot() + File.separator
				+ ((m_Mnf.getSourceLocation().length() == 0 ) ? "" : (m_Mnf.getSourceLocation() + File.separator)) 
				+ m_sRelativePath);
		}
		catch ( Exception E ) {
			m_Log.error(E.getLocalizedMessage());
		}
	}

	public void writeEndDocument () {
		m_XW.writeRawXML("<ut Type=\"end\" Style=\"external\" LeftEdge=\"angle\" DisplayText=\"okapiTTX\">&lt;/okapiTTX&gt;</ut>");
		m_XW.writeEndElement(); // Raw
		m_XW.writeEndElement(); // Body
		m_XW.writeEndElement(); // TRADOStag
		m_XW.writeEndDocument();
		m_XW.close();
		m_Mnf.addDocument(m_nDKey, m_sRelativePath);
	}

	public void writeItem (IFilterItem p_Source,
		IFilterItem p_Target,
		int p_nStatus)
	{
		m_XW.writeRawXML(String.format(
			"<ut Type=\"start\" Style=\"external\" RightEdge=\"angle\" DisplayText=\"u\">&lt;u id='%d'&gt;</ut>",
			p_Source.getItemID()));
		//TODO: inline tags
		m_XW.writeString(p_Source.getText(FilterItemText.ORIGINAL));
		m_XW.writeRawXML("<ut Type=\"end\" Style=\"external\" LeftEdge=\"angle\" DisplayText=\"u\">&lt;/u&gt;</ut>");
		m_XW.writeLineBreak();
	}

	private String getSettingsRelativePath () {
		String dir = Utils.getDirectoryName(m_sRelativePath);
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
	
	public void writeStartDocument (String p_sOriginal) {
		m_XW.writeStartDocument();

		m_XW.writeStartElement("TRADOStag");
		m_XW.writeAttributeString("Version", "2.0");
	
		m_XW.writeStartElement("FrontMatter");

		m_XW.writeStartElement("ToolSettings");
		m_XW.writeAttributeString("CreationDate", dateFmt.format(new java.util.Date())); 
		m_XW.writeAttributeString("CreationTool", getClass().getName());
		m_XW.writeAttributeString("CreationToolVersion", "1"); // TODO: toolversion
		m_XW.writeEndElement(); // ToolSettings
		
		m_XW.writeStartElement("UserSettings");
		m_XW.writeAttributeString("DataType", "XML");
		m_XW.writeAttributeString("O-Encoding", "UTF-8");
		m_XW.writeAttributeString("SettingsName", "okapiTTX");
		m_XW.writeAttributeString("SettingsPath", DTD_SETTINGS_FILE);
		m_XW.writeAttributeString("SourceLanguage", m_Mnf.getSourceLanguage());
		m_XW.writeAttributeString("TargetLanguage", m_Mnf.getTargetLanguage());
		m_XW.writeAttributeString("TargetDefaultFont", "");
		m_XW.writeAttributeString("SourceDocumentPath", p_sOriginal);
		m_XW.writeAttributeString("SettingsRelativePath", getSettingsRelativePath());
		m_XW.writeAttributeString("PlugInInfo", "");
		m_XW.writeEndElement(); // UserSettings
		
		m_XW.writeEndElement(); // FrontMatter
		
		m_XW.writeStartElement("Body");
		m_XW.writeStartElement("Raw");
		
		m_XW.writeRawXML("<ut Class=\"procinstr\" DisplayText=\"Instruction\">&lt;?xml version=&quot;1.0&quot;?&gt;</ut>");
		m_XW.writeLineBreak();
		m_XW.writeRawXML("<ut Type=\"start\" Style=\"external\" RightEdge=\"angle\" DisplayText=\"okapiTTX\">&lt;okapiTTX&gt;</ut>");
		m_XW.writeLineBreak();
	}

}
