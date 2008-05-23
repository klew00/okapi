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

package net.sf.okapi.applications.rainbow.lib;

import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.util.Stack;

import net.sf.okapi.common.Util;

public class XMLWriter {
	
	private CharsetEncoder        m_Enc;
	private PrintWriter           m_PW = null;
	private boolean               m_bInStartTag;
	private Stack<String>         m_stkElems;

	public void create (String p_sPath)
		throws FileNotFoundException
	{
		Util.createDirectories(p_sPath);
		OutputStream OS = new BufferedOutputStream(new FileOutputStream(p_sPath));
		Charset Chs = Charset.forName("UTF-8");
		m_Enc = Chs.newEncoder();
		m_PW = new PrintWriter(new OutputStreamWriter(OS, m_Enc));
		m_bInStartTag = false;
		m_stkElems = new Stack<String>(); 
	}
	
	public void close () {
		if ( m_PW != null ) {
			m_PW.close();
			m_PW = null;
		}
		if ( m_stkElems != null ) {
			m_stkElems.clear();
			m_stkElems = null;
		}
	}
	
	public void writeStartDocument () {
		m_PW.println("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
	}
	
	public void writeEndDocument () {
		closeStartTag();
		m_PW.flush();
	}
	
	public void writeStartElement (String p_sName) {
		closeStartTag();
		m_stkElems.push(p_sName);
		m_PW.write("<" + p_sName);
		m_bInStartTag = true;
	}
	
	public void writeEndElement () {
		closeStartTag();
		m_PW.write("</" + m_stkElems.pop() + ">");
	}
	
	public void writeEndElementLineBreak () {
		closeStartTag();
		m_PW.println("</" + m_stkElems.pop() + ">");
	}
	
	public void writeElementString (String name,
		String content)
	{
		closeStartTag();
		m_PW.write("<" + name + ">");
		m_PW.write(Util.escapeToXML(content, 0, false));
		m_PW.print("</" + name + ">");
	}
	
	public void writeAttributeString (String p_sName,
		String p_sValue)
	{
		m_PW.write(" " + p_sName + "=\"" + Util.escapeToXML(p_sValue, 3, false) + "\"");
	}
	
	public void writeString (String p_sText) {
		closeStartTag();
		m_PW.write(Util.escapeToXML(p_sText, 0, false));
	}
	
	public void writeRawXML (String p_sXML) {
		closeStartTag();
		m_PW.write(p_sXML);
	}
	
	public void writeComment (String p_sText) {
		closeStartTag();
		m_PW.write("<!--");
		m_PW.write(p_sText);
		m_PW.write("-->");
	}
	
	public void writeLineBreak () {
		m_PW.println("");
	}
	
	private void closeStartTag () {
		if ( m_bInStartTag ) {
			m_PW.write(">");
			m_bInStartTag = false;
		}
	}
}
