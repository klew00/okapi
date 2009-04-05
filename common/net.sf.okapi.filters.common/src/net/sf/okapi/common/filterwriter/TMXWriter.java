/*===========================================================================
  Copyright (C) 2008-2009 by the Okapi Framework contributors
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

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import net.sf.okapi.common.XMLWriter;
import net.sf.okapi.common.resource.TextContainer;
import net.sf.okapi.common.resource.TextFragment;
import net.sf.okapi.common.resource.TextUnit;

public class TMXWriter {
	
	private XMLWriter writer;
	private TMXContent tmxCont;
	private String srcLang;
	private String trgLang;
	private int itemCount;
	private boolean withTradosWorkarounds;
	private Pattern exclusionPattern = null;

	public void close () {
		if ( writer != null ) {
			writer.close();
			writer = null;
		}
	}
	
	public int getItemCount () {
		return itemCount;
	}

	public void create (String path) {
		if ( path == null ) throw new NullPointerException();
		itemCount = 0;
		writer = new XMLWriter();
		tmxCont = new TMXContent();
		tmxCont.setTradosWorkarounds(withTradosWorkarounds);
		writer.create(path);
	}
	
	public void setTradosWorkarounds (boolean value) {
		withTradosWorkarounds = value;
		if ( tmxCont != null ) {
			tmxCont.setTradosWorkarounds(withTradosWorkarounds);
		}
	}
	
	public void setExclusionOption (String pattern) {
		if (( pattern == null ) || ( pattern.length() == 0 )) {
			exclusionPattern = null;
		}
		else {
			exclusionPattern = Pattern.compile(pattern);
		}
	}
	
	public void writeStartDocument (String sourceLanguage,
		String targetLanguage,
		String creationTool,
		String creationToolVersion,
		String segType,
		String originalTMFormat,
		String dataType)
	{
		if ( sourceLanguage == null ) throw new NullPointerException();
		if ( targetLanguage == null ) throw new NullPointerException();
		this.srcLang = sourceLanguage;
		this.trgLang = targetLanguage;
		
		writer.writeStartDocument();
		writer.writeStartElement("tmx");
		writer.writeAttributeString("version", "1.4");
		
		writer.writeStartElement("header");
		writer.writeAttributeString("creationtool",
			(creationTool==null) ? "unknown" : creationTool);
		writer.writeAttributeString("creationtoolversion",
			(creationToolVersion==null) ? "unknown" : creationToolVersion);
		writer.writeAttributeString("segtype",
			(segType==null) ? "paragraph" : segType);
		writer.writeAttributeString("o-tmf",
			(originalTMFormat==null) ? "unknown" : originalTMFormat);
		writer.writeAttributeString("adminlang", "en");
		writer.writeAttributeString("srclang", srcLang);
		writer.writeAttributeString("datatype",
			(dataType==null) ? "unknown" : dataType);
		writer.writeEndElement(); // header
		
		writer.writeStartElement("body");
		writer.writeLineBreak();
	}
	
	public void writeEndDocument () {
		writer.writeEndElementLineBreak(); // body
		writer.writeEndElementLineBreak(); // tmx
		writer.writeEndDocument();
	}
	
	public void writeItem (TextUnit item,
		Map<String, String> attributes)
	{
		if ( item == null ) throw new NullPointerException();
		itemCount++;
		
		String tuid = item.getName();
		if (( tuid == null ) || ( tuid.length() == 0 )) {
			tuid = String.format("autoID%d", itemCount);
		}
		
		TextContainer srcTC = item.getSource();
		TextContainer trgTC = item.getTarget(trgLang);
//TODO: Output only the items with real match or translations		
		if ( !srcTC.isSegmented() ) { // Source is not segmented
			writeTU(srcTC, trgTC, tuid, attributes);
		}
		else if ( trgTC.isSegmented() ) { // Source AND target are segmented
			// Write the segments
			List<TextFragment> srcList = srcTC.getSegments();
			List<TextFragment> trgList = trgTC.getSegments();
			for ( int i=0; i<srcList.size(); i++ ) {
				writeTU(srcList.get(i),
					(i>trgList.size()-1) ? null : trgList.get(i),
					String.format("%s_s%02d", tuid, i+1),
					attributes);
			}
		}
		// Else no TMX output needed for source segmented but not target
	}

	private void writeTU (TextFragment source,
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
		
		writer.writeStartElement("tu");
		if (( tuid != null ) && ( tuid.length() > 0 ))
			writer.writeAttributeString("tuid", tuid);
		writer.writeLineBreak();

		if (( attributes != null ) && ( attributes.size() > 0 )) {
			for ( String name : attributes.keySet() ) {
				writer.writeStartElement("prop");
				writer.writeAttributeString("type", name);
				writer.writeString(attributes.get(name));
				writer.writeEndElementLineBreak(); // prop
			}
		}

		writer.writeStartElement("tuv");
		writer.writeAttributeString("xml:lang", srcLang);
		writer.writeStartElement("seg");
		writer.writeRawXML(tmxCont.setContent(source).toString());
		writer.writeEndElement(); // seg
		writer.writeEndElementLineBreak(); // tuv
		
		if ( target != null ) {
			writer.writeStartElement("tuv");
			writer.writeAttributeString("xml:lang", trgLang);
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
	public void writeFullItem (TextUnit item) {
		if ( item == null ) throw new NullPointerException();
		itemCount++;
		
		String tuid = item.getName();
		if (( tuid == null ) || ( tuid.length() == 0 )) {
			tuid = String.format("autoID%d", itemCount);
		}

		TextContainer srcCont = item.getSource();
		Set<String> langs = item.getTargetLanguages();
		
//TODO: Support segmented output
		if ( !srcCont.isSegmented() ) { // Source is not segmented
			// Write start TU
			writer.writeStartElement("tu");
			if (( tuid != null ) && ( tuid.length() > 0 ))
				writer.writeAttributeString("tuid", tuid);
			writer.writeLineBreak();
			
			// Write any resource-level properties
			Set<String> names = item.getPropertyNames();
			for ( String name : names ) {
				writer.writeStartElement("prop");
				writer.writeAttributeString("type", name);
				writer.writeString(item.getProperty(name).getValue());
				writer.writeEndElementLineBreak(); // prop
			}

			// Write source TUV
			writeTUV(srcCont, srcLang, srcCont);
			
			// Write each target TUV
			for ( String lang : langs ) {
				writeTUV(item.getTarget(lang), lang, item.getTarget(lang));
			}
			
			// Write end TU
			writer.writeEndElementLineBreak(); // tu
		}
	}

	/**
	 * Writes an TUV element.
	 * @param frag The TextFragment for the content of this TUV. This can be
	 * a segment of a TextContainer.
	 * @param language The language for this TUV.
	 * @param contForProp The TextContainer that has the properties to write for
	 * this TUV, or null for no properties.
	 */
	private void writeTUV (TextFragment frag,
		String language,
		TextContainer contForProp)
	{
		writer.writeStartElement("tuv");
		writer.writeAttributeString("xml:lang", language);
		writer.writeStartElement("seg");
		writer.writeRawXML(tmxCont.setContent(frag).toString());
		writer.writeEndElement(); // seg

		if ( contForProp != null ) {
			Set<String> names = contForProp.getPropertyNames();
			if ( names.size() > 0 ) writer.writeLineBreak();
			for ( String name : names ) {
				writer.writeStartElement("prop");
				writer.writeAttributeString("type", name);
				writer.writeString(contForProp.getProperty(name).getValue());
				writer.writeEndElementLineBreak(); // prop
			}
		}
		writer.writeEndElementLineBreak(); // tuv
	}

}
