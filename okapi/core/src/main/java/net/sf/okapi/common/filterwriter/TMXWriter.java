/*===========================================================================
Copyright (C) 2008-2010 by the Okapi Framework contributors
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

package net.sf.okapi.common.filterwriter;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import net.sf.okapi.common.Util;
import net.sf.okapi.common.XMLWriter;
import net.sf.okapi.common.annotation.ScoreInfo;
import net.sf.okapi.common.annotation.ScoresAnnotation;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.resource.Segment;
import net.sf.okapi.common.resource.TextContainer;
import net.sf.okapi.common.resource.TextFragment;
import net.sf.okapi.common.resource.TextUnit;

/**
 * Writer for TMX documents.
 */
public class TMXWriter {

    private static final String ATTR_NAMES = ";lang;tuid;o-encoding;datatype;usagecount;"
    	+ "lastusagedate;creationtool;creationtoolversion;creationdate;creationid;"
    	+ "changedate;segtype;changeid;o-tmf;srclang;";

    private static final String CREATIONID = "creationid";
    
    private XMLWriter writer;
    private TMXContent tmxCont = new TMXContent();
    private LocaleId srcLoc;
    private LocaleId trgLoc;
    private int itemCount;
    private Pattern exclusionPattern = null;
    private Hashtable<String, String> MTattribute;
    private boolean useMTPrefix = true;

    /**
     * Creates a new TMXWriter object.
     * Creates a new TMX document.
     * @param path The full path of the TMX document to create.
     * If another document exists already it will be overwritten.
     */
    public TMXWriter (String path) {
    	if ( path == null ) {
    		throw new IllegalArgumentException("path must be set");
    	}
    	writer = new XMLWriter(path);
    	MTattribute = new Hashtable<String, String>();
    	MTattribute.put(CREATIONID, Util.ORIGIN_MT);
    }

    /**
     * Creates a new TMXWriter object.
     * Creates a new TMX document.
     * @param writer an instance of an XMLWriter to use.
     * If another document exists already it will be overwritten.
     */
    public TMXWriter (XMLWriter writer) {
    	this.writer = writer;
    }

    /**
     * Closes the current output document if one is opened.
     */
    public void close () {
    	if ( writer != null ) {
    		writer.close();
    		writer = null;
    	}
    }

    /**
     * Gets the number of TU elements that have been written in the current output document.
     * @return The number of TU elements written in the current output document.
     */
    public int getItemCount () {
    	return itemCount;
    }

    /**
     * Sets the flag indicating whether the writer should output
     * workaround codes specific for Trados.
     * @param value true to output Trados-specific workarounds. False otherwise.
     */
    public void setTradosWorkarounds (boolean value) {
    	tmxCont.setTradosWorkarounds(value);
    }

    /**
     * Sets the flag indicating whether the writer should output
     * workaround codes specific for OmegaT.
     * @param value true to output OmegaT-specific workarounds. False otherwise.
     */
    public void setOmegaTWorkarounds (boolean value) {
    	tmxCont.setOmegaTWorkarounds(value);
    }
    
    /**
     * Sets a pattern oc content to not output. The given pattern is matched against
     * the source content of each item, if it matches, the item is not written.
     * @param pattern The regular expression pattern of the contents to not output.
     */
    public void setExclusionOption (String pattern) {
    	if ( Util.isEmpty(pattern) ) {
    		exclusionPattern = null;
    	} 
    	else {
    		exclusionPattern = Pattern.compile(pattern);
    	}
    }

    /**
     * Sets the default quote mode to use in escaping the TMX segment content (1 is the default).
     * @param quoteMode 0=no quote escaped, 1=apos and quot, 2=#39 and quot,
     * and 3=quot only.
     */
    public void setQuoteMode (int quoteMode) {
    	tmxCont.setQuoteMode(quoteMode);
    }
    
    /**
     * Sets the flag indicating if entries originating from MT should be written with a special
     * prefix in the source text (forcing a fuzzy match). An entry is deemed originating from
     * MT when its <code>creationid</code> attribute is set to <code>{@link Util#ORIGIN_MT}</code>. 
     * @param useMTPrefix true to use a prefix for entries originating from MT.
     */
    public void setUseMTPrefix (boolean useMTPrefix) {
   	 this.useMTPrefix = useMTPrefix;
    }

    /**
     * Writes the start of the TMC document.
     * @param sourceLocale The source locale (must be set).
     * @param targetLocale The target locale (must be set).
     * @param creationTool The identifier of the creation tool (can be null).
     * @param creationToolVersion The version of the creation tool (can be null).
     * @param segType The type of segments in the output.
     * @param originalTMFormat The identifier for the original TM engine (can be null).
     * @param dataType The type of data to output.
     */
    public void writeStartDocument(LocaleId sourceLocale,
   		LocaleId targetLocale,
   		String creationTool,
   		String creationToolVersion,
   		String segType,
   		String originalTMFormat,
   		String dataType)
    {
    	if ( sourceLocale == null ) {
    		throw new NullPointerException();
    	}
    	if ( targetLocale == null ) {
    		throw new NullPointerException();
    	}
    	this.srcLoc = sourceLocale;
    	this.trgLoc = targetLocale;

    	if ( tmxCont.getOmegaTWorkarounds() ) {
    		// If OmegaT mode is set, we need to overwrite the creationtool attribute
    		creationTool = "OmegaT";
    	}

    	writer.writeStartDocument();
    	writer.writeStartElement("tmx");
    	writer.writeAttributeString("version", "1.4");

    	writer.writeStartElement("header");
    	writer.writeAttributeString("creationtool",
    		(creationTool == null) ? "unknown" : creationTool);
    	writer.writeAttributeString("creationtoolversion",
    		(creationToolVersion == null) ? "unknown" : creationToolVersion);
    	writer.writeAttributeString("segtype",
    		(segType == null) ? "paragraph" : segType);
    	writer.writeAttributeString("o-tmf",
    		(originalTMFormat == null) ? "unknown" : originalTMFormat);
    	writer.writeAttributeString("adminlang", "en");
    	writer.writeAttributeString("srclang", srcLoc.toBCP47());
    	writer.writeAttributeString("datatype",
    		(dataType == null) ? "unknown" : dataType);
    	writer.writeEndElement(); // header

    	writer.writeStartElement("body");
    	writer.writeLineBreak();
    }

    /**
     * Writes the end of the TMX document.
     */
    public void writeEndDocument () {
    	writer.writeEndElementLineBreak(); // body
    	writer.writeEndElementLineBreak(); // tmx
    	writer.writeEndDocument();
    }

    /**
     * Writes a given text unit.
     * @param item The text unit to output.
     * @param attributes The optional set of attribute to put along with the entry.
     */
    public void writeItem (TextUnit item,
    	Map<String, String> attributes)
    {
    	writeItem(item, attributes, false);
    }

    /**
     * Writes a given text unit.
     * @param item the text unit to output.
     * @param attributes the optional set of attribute to put along with the entry.
     * @param alternate indicates if this item is an 'alternate'. If it is an alternate, if the
     * target locale does not have any entry in this item, the first found entry is used
     * instead. This is to allow getting for example FR-CA translations for an FR project.
     */
    public void writeItem (TextUnit item,
   		Map<String, String> attributes,
   		boolean alternate)
    {
    	String tuid = item.getName();
    	if ( Util.isEmpty(tuid) ) {
    		// itemCount will be incremented in writeTU, so do a +1 here to take that in account
    		tuid = String.format("autoID%d", itemCount + 1);
    	}

    	TextContainer srcTC = item.getSource();
    	TextContainer trgTC = item.getTarget(trgLoc);

    	if (( trgTC == null ) && alternate ) {
    		// If we don't have a target but are in alternate mode: get the first
    		// available locale in the list
    		Iterator<LocaleId> iter = item.getTargetLocales().iterator();
    		if ( iter.hasNext() ) {
    			trgTC = item.getTarget(iter.next());
    		}
    	}

    	//TODO: Output only the items with real match or translations (not copy of source)		
    	ScoresAnnotation scores = null;
    	if ( trgTC != null ) {
    		scores = trgTC.getAnnotation(ScoresAnnotation.class);
    	}

    	int i = -1;
		for ( Segment srcSeg : srcTC.getSegments() ) {
    		i++;
    		TextFragment tf = srcSeg.text;
			Segment trgSeg = trgTC.getSegment(srcSeg.id);
			if ( trgSeg == null ) {
				// No corresponding target: skip it
				continue;
			}
    		if ( scores != null ) { // If the TU is scored
    			ScoreInfo si = scores.get(i); // Try to get the score for this segment
				if ( si.score == 0 ) {
					// Not score: in a scored-TU: skip this segment
					continue;
				}
				// Scored properly. Now see if we need to alter the source text
				if (( si.origin != null ) && si.origin.equals(Util.ORIGIN_MT) ) {
					// Set the MT flag attribute
		        	if ( attributes != null ) {
		        		if ( !attributes.containsKey(CREATIONID) ) {
		        			attributes.put(CREATIONID, Util.ORIGIN_MT);
		        		}
		        	}
		        	else {
		        		attributes = MTattribute;
		        	}
		        	
		        	// Add the flag prefix if requested (that's why we clone)
		        	if ( useMTPrefix ) {
		        		tf = srcSeg.text.clone();
		        		tf.setCodedText(Util.ORIGIN_MT+" "+tf.getCodedText());
		        	}
				}
    		}
    		// Write out the segment
       		writeTU(tf, trgSeg.text,
   				String.format("%s_s%s", tuid, srcSeg.id), attributes);
    	}
    }

    /**
     * Writes a TMX TU element.
     * @param source the fragment for the source text.
     * @param target the fragment for the target text.
     * @param tuid the TUID attribute (can be null).
     * @param attributes the optional set of attribute to put along with the entry.
     */
    public void writeTU (TextFragment source,
   		TextFragment target,
   		String tuid,
   		Map<String, String> attributes)
    {
    	// Check if this source entry should be excluded from the output
    	if ( exclusionPattern != null ) {
    		if ( exclusionPattern.matcher(source.getCodedText()).matches() ) {
    			// The source coded text matches: do not include this entry in the output
    			return;
    		}
    	}

    	itemCount++;
    	writer.writeStartElement("tu");
    	if ( !Util.isEmpty(tuid) ) {
    		writer.writeAttributeString("tuid", tuid);
    	}
    	writer.writeLineBreak();

    	// Write properties
    	if (( attributes != null ) && ( attributes.size() > 0 )) {
    		for ( String name : attributes.keySet() ) {
    			// Filter out standard attributes
    			if ( ATTR_NAMES.contains(";"+name+";") ) {
    				continue;
    			}
    			// Write out the property
    			writer.writeStartElement("prop");
    			writer.writeAttributeString("type", name);
    			writer.writeString(attributes.get(name));
    			writer.writeEndElementLineBreak(); // prop
    		}
    	}

    	writer.writeStartElement("tuv");
    	writer.writeAttributeString("xml:lang", srcLoc.toBCP47());
    	writer.writeStartElement("seg");
    	writer.writeRawXML(tmxCont.setContent(source).toString());
    	writer.writeEndElement(); // seg
    	writer.writeEndElementLineBreak(); // tuv

    	if ( target != null ) {
    		writer.writeStartElement("tuv");
    		writer.writeAttributeString("xml:lang", trgLoc.toBCP47());
        	// Write creationid if available
        	if ( attributes != null ) {
        		if ( attributes.containsKey(CREATIONID) ) {
        			writer.writeAttributeString(CREATIONID, attributes.get(CREATIONID));
        		}
        	}
    		writer.writeStartElement("seg");
    		writer.writeRawXML(tmxCont.setContent(target).toString());
    		writer.writeEndElement(); // seg
    		writer.writeEndElementLineBreak(); // tuv
    	}

    	writer.writeEndElementLineBreak(); // tu
    }

    /**
     * Writes a TextUnit (all targets) with all the properties associated to it.
     * @param item The text unit to write.
     */
    public void writeTUFull (TextUnit item) {
    	if ( item == null ) {
    		throw new NullPointerException();
    	}
    	itemCount++;

    	// In a TU, each target may have a different source-corresponding segmentation
    	// The way TMX is written here, we assume that all targets match the current source segmentation

    	String tuid = item.getName();
    	if ( Util.isEmpty(tuid) ) {
    		tuid = String.format("autoID%d", itemCount);
    	}

    	TextContainer srcCont = item.getSource();
    	Set<LocaleId> locales = item.getTargetLocales();
		Set<String> names = item.getPropertyNames();

		// For each segment: write a separate TU
		for ( Segment srcSeg : srcCont.getSegments() ) {
    		
    		// Write start TU
    		writer.writeStartElement("tu");
    		if ( srcCont.contentIsOneSegment() ) {
    			writer.writeAttributeString("tuid", tuid);
    		}
    		else {
    			writer.writeAttributeString("tuid", String.format("%s_%s", tuid, srcSeg.id));
    		}
    		writer.writeLineBreak();

    		// Write any resource-level properties
    		for ( String name : names ) {
    			// Filter out attributes (temporary solution)
    			if ( ATTR_NAMES.contains(";"+name+";") ) continue;
    			// Write out the property
    			writer.writeStartElement("prop");
    			writer.writeAttributeString("type", name);
    			writer.writeString(item.getProperty(name).getValue());
    			writer.writeEndElementLineBreak(); // prop
    		}

    		// Write the source TUV
    		writeTUV(srcSeg.text, srcLoc, srcCont);
		
    		// Write each target TUV
    		for ( LocaleId loc : locales ) {
        		TextContainer trgCont = item.getTarget(loc);
        		Segment trgSeg = trgCont.getSegment(srcSeg.id);
        		// Write target only if we have one corresponding to the source segment
        		if ( trgSeg != null ) {
        			writeTUV(trgSeg.text, loc, trgCont);
        		}
    		}

    		// Write end TU
    		writer.writeEndElementLineBreak(); // tu

    	} // End of the segments loop
    	
    }

    /**
     * Writes a TUV element.
     * @param frag the TextFragment for the content of this TUV. This can be
     * a segment of a TextContainer.
     * @param locale the locale for this TUV.
     * @param contForProp the TextContainer that has the properties to write for
     * this TUV, or null for no properties.
     */
    private void writeTUV (TextFragment frag,
   		LocaleId locale,
   		TextContainer contForProp)
    {
    	writer.writeStartElement("tuv");
    	writer.writeAttributeString("xml:lang", locale.toBCP47());
    	writer.writeStartElement("seg");
    	writer.writeRawXML(tmxCont.setContent(frag).toString());
    	writer.writeEndElement(); // seg

    	if ( contForProp != null ) {
    		boolean propWritten = false;
    		Set<String> names = contForProp.getPropertyNames();
    		for ( String name : names ) {
    			// Filter out attributes (temporary solution)
    			if ( ATTR_NAMES.contains(";"+name+";") ) continue;
    			// Write out the property
    			writer.writeLineBreak();
    			writer.writeStartElement("prop");
    			writer.writeAttributeString("type", name);
    			writer.writeString(contForProp.getProperty(name).getValue());
    			writer.writeEndElement(); // prop
    			propWritten = true;
    		}
    		if ( propWritten ) {
    			writer.writeLineBreak();
    		}
    	}
    	writer.writeEndElementLineBreak(); // tuv
    }

}
