/*===========================================================================
  Copyright (C) 2011 by the Okapi Framework contributors
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

package net.sf.okapi.lib.xliff;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

public class XLIFFWriter {

	/**
	 * URI for the XLIFF 2.0 namespace.
	 */
	public static final String NS_XLIFF20 = "urn:oasis:names:tc:xliff:document:2.0";

	private PrintWriter writer = null;
    private String lb = System.getProperty("line.separator");
    private boolean isIndented = false;
    private String indent;
    private boolean inFile;
    private int style = Fragment.STYLE_NODATA;
    private String sourceLang;
    private String targetLang;

    public void create (File file,
    	String sourceLang)
    {
		try {
			// Create the directories if needed
			String path = file.getCanonicalPath();
			int n = path.lastIndexOf('\\');
			if ( n == -1 ) path.lastIndexOf('/');
			if ( n > -1 ) {
				File dir = new File(path.substring(0, n));
				dir.mkdirs();
			}
			// Create the file
			create(new OutputStreamWriter(
				new BufferedOutputStream(new FileOutputStream(file)), "UTF-8"),
				sourceLang);
		}
		catch ( FileNotFoundException e ) {
			throw new XLIFFWriterException("Cannote create document.", e);
		}
		catch ( UnsupportedEncodingException e ) {
			throw new XLIFFWriterException("Unsupported encoding.", e);
		}
		catch ( IOException e ) {
			throw new XLIFFWriterException("Cannote create document.", e);
		}
    }

    public void create (Writer output,
    	String sourceLang)
    {
    	setLanguages(sourceLang, null);
    	writer = new PrintWriter(output);
		indent = "";
		inFile = false;
	}
    
    /**
     * Sets the current source and target language of the XLIFF document.
     * @param sourceLang XML language code for the source (required)
     * @param targetLang XML language code for the target (use null when undefined)
     */
    public void setLanguages (String sourceLang,
    	String targetLang)
    {
    	if ( Util.isNullOrEmpty(sourceLang) ) {
    		throw new XLIFFWriterException("Source language must be defined.");
    	}
    	this.sourceLang = sourceLang;
    	this.targetLang = targetLang;
    }
    
    public void setInlineStyle (int style) {
    	switch ( style ) {
    	case Fragment.STYLE_DATAINSIDE:
    	case Fragment.STYLE_DATAOUTSIDE:
    	case Fragment.STYLE_NODATA:
    	case Fragment.STYLE_XSDTEMP:
    		this.style = style;
    		return;
    	}
    	throw new XLIFFWriterException(String.format("Style %d is not valid.", style));
    }
    
    public int getInlineStyle () {
    	return style;
    }
    
    public void setLineBreak (String lineBreak) {
    	lb = lineBreak;
    }
    
    public String getLineBreak () {
    	return lb;
    }
    
    public void setIsIndented (boolean isIndented) {
    	this.isIndented = isIndented;
    }
    
    public boolean getIsIndented () {
    	return isIndented;
    }
	
	public void close () {
		if ( writer != null ) {
			writer.close();
			writer = null;
		}
	}
	
	public void writeUnit (Unit unit) {
		// Check if there is something to write
		// A unit must have at least one part 
		if ( unit.getPartCount() == 0 ) {
			return;
		}
		if ( !inFile ) writeStartFile();
		writer.print(indent+String.format("<unit id=\"%s\"", Fragment.toXML(unit.getId(), true)));
		writer.print(">"+lb);
		if ( isIndented ) indent += " ";
		
		if ( style == Fragment.STYLE_DATAOUTSIDE ) {
			writeNativeData(unit.getCodesStore());
		}
		
		int sequence = 0;
		for ( Part part : unit ) {
			Segment seg = null;
			if ( part instanceof Segment ) {
				seg = (Segment)part;
			}
			sequence++;
			
			if ( seg != null ) writer.print(indent+"<segment>"+lb);
			else writer.print(indent+"<ignorable>"+lb);
			
			if ( isIndented ) indent += " ";
			
			// Source
			writeFragment("source", part.getSource(), -1);
			// Target
			if ( part.hasTarget() ) {
				writeFragment("target", part.getTarget(true),
					(part.targetOrder!=sequence ? part.targetOrder : 0));
			}
			
			if ( seg != null ) {
				if ( seg.getCandidates().size() > 0 ) {
					writer.print(indent+"<matches>"+lb);
					if ( isIndented ) indent += " ";
				
					for ( Alternate alt : seg.getCandidates() ) {
						writer.print(indent+"<match>"+lb);
						if ( isIndented ) indent += " ";
						if ( style == Fragment.STYLE_DATAOUTSIDE ) {
							writeNativeData(alt.getCodesStore());
						}
						writeFragment("source", alt.getSource(), -1);
						writeFragment("target", alt.getTarget(), -1);
						if ( isIndented ) indent = indent.substring(1);
						writer.print(indent+"</match>"+lb);
					}
					if ( isIndented ) indent = indent.substring(1);
					writer.print(indent+"</matches>"+lb);
				}
			}
			
			if ( isIndented ) indent = indent.substring(1);
			
			if ( seg != null ) writer.print(indent+"</segment>"+lb);
			else writer.print(indent+"</ignorable>"+lb);
		}

		if ( isIndented ) indent = indent.substring(1);
		writer.print(indent+"</unit>"+lb);
	}

	public void writeStartDocument () {
		writer.print("<?xml version=\"1.0\" encoding=\"UTF-8\"?>"+lb);
		writer.print("<xliff version=\"2.0\""+lb);
		writer.print(" xmlns=\""+NS_XLIFF20+"\""+lb);
		writer.print(" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\""+lb);
		writer.print(" xsi:schemaLocation=\"urn:oasis:names:tc:xliff:document:2.0 xliff_core_2.0.xsd\""+lb);		
		writer.print(">"+lb);
		
		writer.print("<!-- This output is EXPERIMENTAL only. -->"+lb);
		writer.print("<!-- XLIFF 2.0 is not defined yet. -->"+lb);
		writer.print("<!-- For feedback or more info, please see the XLIFF TC (http://www.oasis-open.org/committees/xliff) -->"+lb);
		if ( isIndented ) indent += " ";
	}
	
	public void writeEndDocument () {
		if ( inFile ) {
			writeEndFile();
		}
		if ( isIndented ) indent = indent.substring(1);
		writer.print("</xliff>"+lb);
	}
	
	public void writeStartFile () {
		writer.print(indent+String.format("<file source=\"%s\"", sourceLang));
		if ( !Util.isNullOrEmpty(targetLang) ) {
			writer.print(String.format(" target=\"%s\"", targetLang));
		}
		writer.print(">"+lb);
		if ( isIndented ) indent += " ";
		inFile = true;
	}
	
	public void writeEndFile () {
		if ( inFile ) {
			if ( isIndented ) indent = indent.substring(1);
			writer.print(indent+"</file>"+lb);
			inFile = false;
		}
	}
	
	public void writeStartGroup () {
		if ( !inFile ) writeStartFile();
		writer.print(indent+"<group>"+lb);
		if ( isIndented ) indent += " ";
	}
	
	public void writeEndGroup () {
		if ( isIndented ) indent = indent.substring(1);
		writer.print(indent+"</group>"+lb);
	}
	
	private void writeFragment (String name,
		Fragment fragment,
		int order)
	{
		if ( order > 0 ) {
			writer.print(indent+String.format("<%s order=\"%d\">", name, order));
		}
		else {
			writer.print(indent+"<"+name+">");
		}
		writer.print(fragment.getString(style));
		writer.print("</"+name+">"+lb);
	}
	
	private void writeNativeData (CodesStore store) {
		if ( !store.hasNonEmptyCode() ) {
			return; // Nothing to write out
		}
		// Else: we have at least one code
		// Do the output
		
		writer.print(indent+"<nativeData>"+lb);
		if ( isIndented ) indent += " ";
		
		Codes codes = store.getSourceCodes();
		for ( int i=0; i<codes.size(); i++ ) {
			Code code = codes.get(i);
			writer.print(indent+String.format("<data id=\"s%s\">", code.getInternalId()));
			writer.print(Fragment.toXML(code.getNativeData(), false));
			writer.print("</data>"+lb);
		}
		codes = store.getTargetCodes();
		for ( int i=0; i<codes.size(); i++ ) {
			Code code = codes.get(i);
			writer.print(indent+String.format("<data id=\"t%s\">", code.getInternalId()));
			writer.print(Fragment.toXML(code.getNativeData(), false));
			writer.print("</data>"+lb);
		}
		
		if ( isIndented ) indent = indent.substring(1);
		writer.print(indent+"</nativeData>"+lb);
	}

}
