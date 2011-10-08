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
import java.util.Map;

import org.oasisopen.xliff.v2.ICandidate;
import org.oasisopen.xliff.v2.IDataStore;
import org.oasisopen.xliff.v2.IExtendedAttribute;
import org.oasisopen.xliff.v2.IExtendedAttributes;
import org.oasisopen.xliff.v2.IFragment;
import org.oasisopen.xliff.v2.INote;
import org.oasisopen.xliff.v2.IWithCandidates;
import org.oasisopen.xliff.v2.IWithNotes;

public class XLIFFWriter {

	private PrintWriter writer = null;
    private String lb = System.getProperty("line.separator");
    private boolean isIndented = false;
    private String indent;
    private boolean inDocument;
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
			throw new XLIFFWriterException(Res.t("cantCreateDocument"), e);
		}
		catch ( UnsupportedEncodingException e ) {
			throw new XLIFFWriterException("Unsupported encoding.", e);
		}
		catch ( IOException e ) {
			throw new XLIFFWriterException(Res.t("cantCreateDocument"), e);
		}
    }

    public void create (Writer output,
    	String sourceLang)
    {
    	setLanguages(sourceLang, null);
    	writer = new PrintWriter(output);
		indent = "";
		inFile = false;
		inDocument = false;
	}
    
    /**
     * Sets the current source and target language of the XLIFF document.
     * @param sourceLang XML language code for the source (required)
     * @param targetLang XML language code for the target (use null when undefined)
     */
    public void setLanguages (String sourceLang,
    	String targetLang)
    {
    	this.sourceLang = sourceLang;
    	this.targetLang = targetLang;
    }
    
    public void setInlineStyle (int style) {
    	switch ( style ) {
    	case Fragment.STYLE_DATAINSIDE:
    	case Fragment.STYLE_DATAOUTSIDE:
    	case Fragment.STYLE_NODATA:
    		this.style = style;
    		return;
    	}
    	throw new XLIFFWriterException(String.format(Res.t("badInlineStyle"), style));
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
	
	public void writeEvent (XLIFFEvent event) {
		switch ( event.getType() ) {
		case START_DOCUMENT:
			writeStartDocument(event.getDocumentData(), null);
			break;
			
		case START_SECTION:
			SectionData sd = event.getSectionData();
			setLanguages(sd.getSourceLanguage(), sd.getTargetLanguage());
			writeStartFile(sd);
			break;
			
		case START_GROUP:
			writeStartGroup(event.getGroupData());
			break;
			
		case TEXT_UNIT:
			writeUnit(event.getUnit());
			break;
			
		case END_GROUP:
			writeEndGroup();
			break;
			
		case END_SECTION:
			writeEndFile();
			break;
			
		case END_DOCUMENT:
			writeEndDocument();
			break;
		}
	}
	
	public void writeUnit (Unit unit) {
		// Check if there is something to write
		// A unit must have at least one part 
		if ( unit.getPartCount() == 0 ) {
			return;
		}
		if ( !inFile ) writeStartFile(null);
		writer.print(indent+String.format("<unit id=\"%s\"", Util.toXML(unit.getId(), true)));
		writeExtendedAttributes(unit.getExtendedAttributes());
		writer.print(">"+lb);
		if ( isIndented ) indent += " ";
		
		if ( style == Fragment.STYLE_DATAOUTSIDE ) {
			writeOriginalData(unit.getDataStore());
		}
		
		for ( Part part : unit ) {
			Segment seg = null;
			if ( part instanceof Segment ) {
				seg = (Segment)part;
				writer.print(indent+"<"+Util.ELEM_SEGMENT);
				if ( seg.getId() != null ) {
					writer.print(" id=\"" + seg.getId() + "\"");
				}
				writeExtendedAttributes(part.getExtendedAttributes());
				writer.print(">"+lb);
			}
			else {
				writer.print(indent+"<"+Util.ELEM_IGNORABLE+">"+lb);
			}
			if ( isIndented ) indent += " ";
			
			// Source
			writeFragment(Util.ELEM_SOURCE, part.getSource(), 0);
			// Target
			if ( part.hasTarget() ) {
				writeFragment(Util.ELEM_TARGET, part.getTarget(true), part.getTargetOrder());
			}
			
			if ( seg != null ) {
				// Write notes if needed
				writeNotes(seg);
				// Write candidates if needed
				writeCandidates(seg);
			}
			
			if ( isIndented ) indent = indent.substring(1);
			if ( seg != null ) writer.print(indent+"</segment>"+lb);
			else writer.print(indent+"</ignorable>"+lb);
		}

		// Unit-level notes if needed
		writeNotes(unit);
		// Unit-level candidates
		writeCandidates(unit);
		
		if ( isIndented ) indent = indent.substring(1);
		writer.print(indent+"</unit>"+lb);
	}

	private void writeExtendedAttributes (IExtendedAttributes attributes) {
		if ( attributes == null ) return;
		for ( String namespaceURI : attributes.getNamespaces() ) {
			writer.print(" xmlns:" + attributes.getNamespacePrefix(namespaceURI)
				+ "=\"" + namespaceURI + "\"");
		}
		for ( IExtendedAttribute att : attributes ) {
			writer.print(" " + att.getPrefix() + ":" + att.getLocalPart()
				+ "=\"" + Util.toXML(att.getValue(), true) + "\"");
		}
	}
	
	private void writeCandidates (IWithCandidates parent) {
		if ( parent.getCandidates().size() == 0 ) {
			return;
		}
		writer.print(indent+"<matches>"+lb);
		if ( isIndented ) indent += " ";
		for ( ICandidate alt : parent.getCandidates() ) {
			writer.print(indent+"<match>"+lb);
			if ( isIndented ) indent += " ";
			if ( style == Fragment.STYLE_DATAOUTSIDE ) {
				writeOriginalData(alt.getDataStore());
			}
			writeFragment(Util.ELEM_SOURCE, alt.getSource(), -1);
			writeFragment(Util.ELEM_TARGET, alt.getTarget(), -1);
			if ( isIndented ) indent = indent.substring(1);
			writer.print(indent+"</match>"+lb);
		}
		if ( isIndented ) indent = indent.substring(1);
		writer.print(indent+"</matches>"+lb);
	}

	private void writeNotes (IWithNotes parent) {
		if ( parent.getNoteCount() == 0 ) {
			return;
		}
		writer.print(indent+"<notes>"+lb);
		if ( isIndented ) indent += " ";
		for ( INote note : parent.getNotes() ) {
			writer.print(indent+"<simpleNote");
			switch ( note.getAppliesTo() ) {
			case SOURCE:
				writer.print(" appliesTo=\"source\"");
				break;
			case TARGET:
				writer.print(" appliesTo=\"target\"");
				break;
			case DEFAULT:
				// This is the default,no need to output it
				break;
			}
			writer.print(">"+Util.toXML(note.getText(), false));
			writer.print("</simpleNote>"+lb);
		}
		if ( isIndented ) indent = indent.substring(1);
		writer.print(indent+"</notes>"+lb);
	}
	
	public void writeStartDocument (DocumentData docData,
		String comment)
	{
		if ( docData == null ) {
			docData = new DocumentData("2.0");
		}
		writer.print("<?xml version=\"1.0\"?>"+lb);
		writer.print("<xliff xmlns=\""+Util.NS_XLIFF20+"\" version=\""+docData.getVersion()+"\"");
		writeExtendedAttributes(docData.getExtendedAttributes());
		writer.print(">"+lb);
		if ( isIndented ) indent += " ";
		inDocument = true;

		// Extra comment at the top is needed
		if ( !Util.isNullOrEmpty(comment) ) {
			writer.print(indent+"<!-- " + Util.toXML(comment, false) + "-->"+lb);
		}
	}
	
	public void writeEndDocument () {
		if ( inFile ) {
			writeEndFile();
		}
		if ( inDocument ) {
			if ( isIndented ) indent = indent.substring(1);
			writer.print("</xliff>"+lb);
		}
	}
	
	public void writeStartFile (SectionData secData) {
		if ( !inDocument ) writeStartDocument(null, null);
		writer.print(indent+String.format("<%s %s=\"%s\"", Util.ELEM_SECTION, Util.ATTR_SOURCELANG, sourceLang));
		if ( !Util.isNullOrEmpty(targetLang) ) {
			writer.print(String.format(" %s=\"%s\"", Util.ATTR_TARGETLANG, targetLang));
		}
		if ( secData != null ) {
			writeExtendedAttributes(secData.getExtendedAttributes());
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
	
	public void writeStartGroup (GroupData groupData) {
		if ( !inFile ) writeStartFile(null);
		writer.print(indent+"<"+Util.ELEM_GROUP);
		if ( groupData != null ) {
			writer.print(String.format(" %s=\"%s\"", Util.ATTR_ID, groupData.getId()));
		}
		writer.print(">"+lb);
		if ( isIndented ) indent += " ";
	}
	
	public void writeEndGroup () {
		if ( isIndented ) indent = indent.substring(1);
		writer.print(indent+"</"+Util.ELEM_GROUP+">"+lb);
	}
	
	private void writeFragment (String name,
		IFragment fragment,
		int order) // order must be 0 for source fragments
	{
		if ( order > 0 ) {
			writer.print(indent+String.format("<%s order=\"%d\">", name, order));
		}
		else {
			writer.print(indent+"<"+name+">");
		}
		writer.print(fragment.toXLIFF(style));
		writer.print("</"+name+">"+lb);
	}
	
	private void writeOriginalData (IDataStore store) {
		if ( !store.hasCodeWithOriginalData() ) {
			return; // Nothing to write out
		}
		
		// Else: compute the map and write it
		store.calculateOriginalDataToIdsMap();
		Map<String, String> map = store.getOutsideRepresentationMap();

		writer.print(indent+"<"+Util.ELEM_ORIGINALDATA+">"+lb);
		if ( isIndented ) indent += " ";

		for ( String originalData : map.keySet() ) {
			String id = map.get(originalData); // The original data is the key during output
			writer.print(indent+String.format("<%s %s=\"%s\">", Util.ELEM_DATA, Util.ATTR_ID, id));
			writer.print(Util.toXML(originalData, false));
			writer.print("</"+Util.ELEM_DATA+">"+lb);
		}

		if ( isIndented ) indent = indent.substring(1);
		writer.print(indent+"</"+Util.ELEM_ORIGINALDATA+">"+lb);
		store.setOutsideRepresentationMap(map);
	}

}
