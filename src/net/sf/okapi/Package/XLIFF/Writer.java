/*===========================================================================*/
/* Copyright (C) 2008 ENLASO Corporation, Okapi Development Team             */
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

package net.sf.okapi.Package.XLIFF;

import java.io.File;

import net.sf.okapi.Filter.FilterItemText;
import net.sf.okapi.Filter.IFilterItem;
import net.sf.okapi.Library.Base.ILog;
import net.sf.okapi.Package.BaseWriter;
import net.sf.okapi.Format.XML.XMLWriter;

/**
 * Implements IWriter for generic XLIFF translation packages.
 */
public class Writer extends BaseWriter {
	
	private XMLWriter   m_XW = null;
	private String      m_sRelativePath;
	private int         m_nDKey;
	private boolean     m_bUseGX = false;

	public Writer(ILog log) {
		super(log);
	}
	
	public String getPackageType () {
		return "genericxliff";
	}

	public void createDocument (int p_nDKey,
		String p_sRelativePath)
	{
		try {
			if ( m_XW == null ) m_XW = new XMLWriter();
			else m_XW.close(); // Else: make sure the previous output is closed
		
			m_nDKey = p_nDKey;
			m_sRelativePath = p_sRelativePath + ".xlf";

			m_XW.create(m_Mnf.getRoot() + File.separator
				+ ((m_Mnf.getSourceLocation().length() == 0 ) ? "" : (m_Mnf.getSourceLocation() + File.separator)) 
				+ m_sRelativePath);
		}
		catch ( Exception E ) {
			m_Log.error(E.getLocalizedMessage());
		}
	}

	public void writeEndDocument () {
		m_XW.writeEndElement(); // body
		m_XW.writeEndElement(); // file
		m_XW.writeEndElement(); // xliff
		m_XW.writeEndDocument();
		m_XW.close();
		m_Mnf.addDocument(m_nDKey, m_sRelativePath);
	}

	public void writeItem (IFilterItem p_Source,
		IFilterItem p_Target,
		int p_nStatus)
	{
		m_XW.writeStartElement("trans-unit");
		m_XW.writeAttributeString("id", String.valueOf(p_Source.getItemID()));
		if ( p_Source.getResName().length() != 0 )
			m_XW.writeAttributeString("resname", p_Source.getResName());
		if ( p_Source.getResType().length() != 0 )
			m_XW.writeAttributeString("restype", p_Source.getResType());
		if ( !p_Source.isTranslatable() )
			m_XW.writeAttributeString("translate", "no");
		if ( p_Source.isPreFormatted() )
			m_XW.writeAttributeString("xml:space", "preserve");
		if (( p_Target != null ) && ( p_nStatus == TSTATUS_OK ))
			m_XW.writeAttributeString("approved", "yes");
		if ( p_Source.hasCoord() )
			m_XW.writeAttributeString("coord", p_Source.getCoord());
		if ( p_Source.hasFont() )
			m_XW.writeAttributeString("font", p_Source.getFont());

		m_XW.writeStartElement("source");
		m_XW.writeAttributeString("xml:lang", m_Mnf.getSourceLanguage());
		m_XW.writeRawXML(p_Source.getText(m_bUseGX ?
			FilterItemText.XLIFFGX : FilterItemText.XLIFF));
		m_XW.writeEndElement(); // source

		// Target (if needed)
		if ( p_Target != null ) {
			m_XW.writeStartElement("target");
			m_XW.writeAttributeString("xml:lang", m_Mnf.getTargetLanguage());
			
			switch ( p_nStatus ) {
				case TSTATUS_OK:
					m_XW.writeAttributeString("state", "final");
					break;
				case TSTATUS_TOEDIT:
					m_XW.writeAttributeString("state", "translated");
					break;
				case TSTATUS_TOREVIEW:
					m_XW.writeAttributeString("state", "needs-review-translation");
					break;
				case TSTATUS_TOTRANS:
					m_XW.writeAttributeString("state", "needs-translation");
					break;
			}

			if ( p_Source.isTranslated() ) {
				m_XW.writeRawXML(p_Target.getText(m_bUseGX ?
					FilterItemText.XLIFFGX : FilterItemText.XLIFF));
			}
			else {
				m_XW.writeRawXML(p_Source.getText(m_bUseGX ?
					FilterItemText.XLIFFGX : FilterItemText.XLIFF));
			}
			
			m_XW.writeEndElement(); // target
		}

		// Note
		if ( p_Source.hasNote() ) {
			m_XW.writeStartElement("note");
			String sFrom = p_Source.getNoteAttributeFrom();
			if (( sFrom != null ) && ( sFrom.length() != 0 ))
				m_XW.writeAttributeString("from", sFrom);
			m_XW.writeString(p_Source.getNote());
			m_XW.writeEndElement(); // note
		}
		
		m_XW.writeEndElement(); // trans-unit
	}

	public void writeStartDocument (String p_sOriginal) {
		m_XW.writeStartDocument();

		m_XW.writeStartElement("xliff");
		m_XW.writeAttributeString("version", "1.2");
		m_XW.writeAttributeString("xmlns", "urn:oasis:names:tc:xliff:document:1.2");
		
		m_XW.writeStartElement("file");
		m_XW.writeAttributeString("source-language", m_Mnf.getSourceLanguage());
		m_XW.writeAttributeString("target-language", m_Mnf.getTargetLanguage());
		m_XW.writeAttributeString("original", p_sOriginal);
		m_XW.writeAttributeString("datatype", "TODO");
		
		m_XW.writeStartElement("header");
		m_XW.writeEndElement(); // header
		
		m_XW.writeStartElement("body");
	}

}
