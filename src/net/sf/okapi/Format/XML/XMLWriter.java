package net.sf.okapi.Format.XML;

import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.util.Stack;

import net.sf.okapi.Library.Base.Utils;

public class XMLWriter {
	
	private CharsetEncoder        m_Enc;
	private PrintWriter           m_PW = null;
	private boolean               m_bInStartTag;
	private Stack<String>         m_stkElems;

	public void create (String p_sPath)
		throws FileNotFoundException
	{
		Utils.createDirectories(p_sPath);
		OutputStream OS = new BufferedOutputStream(new FileOutputStream(p_sPath));
		Charset Chs = Charset.forName("UTF-8");
		m_Enc = Chs.newEncoder();
		m_PW = new PrintWriter(new OutputStreamWriter(OS, m_Enc));
		m_bInStartTag = false;
		m_stkElems = new Stack<String>(); 
	}
	
	public void close () {
		if ( m_PW != null ) {
			m_PW.flush();
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
		m_PW.println("</" + m_stkElems.pop() + ">");
	}
	
	public void writeAttributeString (String p_sName,
		String p_sValue)
	{
		m_PW.write(" " + p_sName + "=\"" + Utils.escapeToXML(p_sValue, 3, false) + "\"");
	}
	
	public void writeString (String p_sText) {
		closeStartTag();
		m_PW.write(Utils.escapeToXML(p_sText, 0, false));
	}
	
	public void writeRawXML (String p_sXML) {
		closeStartTag();
		m_PW.write(p_sXML);
	}
	
	public void writeComment (String p_sText) {
		closeStartTag();
		m_PW.write("<!--");
		m_PW.write(p_sText);
		m_PW.println("-->");
	}
	
	private void closeStartTag () {
		if ( m_bInStartTag ) {
			m_PW.write(">");
			m_bInStartTag = false;
		}
	}
}
