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

package net.sf.okapi.Format.TMX;

import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

import net.sf.okapi.Filter.FilterItemText;
import net.sf.okapi.Filter.IFilterItem;
import net.sf.okapi.Library.Base.Utils;

public class Writer {

	java.io.Writer      m_W;
	
	public Writer (java.io.Writer p_Writer) {
		m_W = p_Writer;
	}
	
	public Writer (String p_sPath)
		throws UnsupportedEncodingException, FileNotFoundException
	{
		OutputStream OS = new BufferedOutputStream(new FileOutputStream(p_sPath));
		m_W = new OutputStreamWriter(OS, "UTF-8");
	}
	
	public void close ()
		throws IOException
	{
		if ( m_W != null ) {
			m_W.close();
			m_W = null;
		}
	}
	
	public void writeStartDocument ()
		throws IOException
	{
		m_W.write("<?xml version=\"1.0\"?>");
		m_W.write("<tmx version=\"1.4\">");
	}
	
	public void writeEndDocument ()
		throws IOException
	{
		m_W.write("</tmx>");
		m_W.flush();
	}
	
	public void writeStartTU (String p_sID)
		throws IOException
	{
		m_W.write("<tu");
		if ( p_sID != null ) m_W.write(" tuid=\"" + p_sID + "\"");
		m_W.write(">");
	}
	
	public void writeEndTU ()
		throws IOException
	{
		m_W.write("</tu>");
	}
	
	public void writeTUV (String p_sLang,
		IFilterItem p_FI)
		throws IOException
	{
		m_W.write(String.format("<tuv xml:lang=\"%s\">", p_sLang));
		m_W.write("<seg>" + p_FI.getText(FilterItemText.TMX) + "</seg>");
		m_W.write("</tuv>");
	}
	
	public void writeProperties (String p_sType,
		String p_sValue)
		throws IOException
	{
		m_W.write("<prop type=\"" + p_sType + "\">");
		m_W.write(Utils.escapeToXML(p_sValue, 1, false));
		m_W.write("</prop>");
	}
}