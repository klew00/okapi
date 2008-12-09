/*===========================================================================*/
/* Copyright (C) 2008 By the Okapi Framework contributors                    */
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

package net.sf.okapi.tm.simpletm;

import java.util.List;
import java.util.Map;

import net.sf.okapi.common.XMLWriter;
import net.sf.okapi.common.resource.TextContainer;
import net.sf.okapi.common.resource.TextFragment;
import net.sf.okapi.common.resource.TextUnit;

public class TMXWriter {
	
	private XMLWriter writer;
	private TMXContent tmxCont;
	private String sourceLang;
	private String trgLang;
	private int itemCount;
	private boolean withTradosWorkarounds;

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
	
	public void writeStartDocument (String sourceLanguage,
		String targetLanguage)
	{
		if ( sourceLanguage == null ) throw new NullPointerException();
		if ( targetLanguage == null ) throw new NullPointerException();
		this.sourceLang = sourceLanguage;
		this.trgLang = targetLanguage;
		
		writer.writeStartDocument();
		writer.writeStartElement("tmx");
		writer.writeAttributeString("version", "1.4");
		
		writer.writeStartElement("header");
		writer.writeAttributeString("creationtool", "TODO");
		writer.writeAttributeString("creationtoolversion", "TODO");
		writer.writeAttributeString("segtype", "paragraph");
		writer.writeAttributeString("o-tmf", "TODO");
		writer.writeAttributeString("adminlang", "en");
		writer.writeAttributeString("srclang", sourceLang);
		writer.writeAttributeString("datatype", "TODO");
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
		//TextContainer trgTC = item.getTargetContent();
		if ( srcTC.isSegmented() ) {
			//TODO: Optionally, write the paragraph-level entry
			//writeTU(srcTC, item.getTargetContent(), tuid);
			// Write the segments
			List<TextFragment> srcList = item.getSource().getSegments();
			List<TextFragment> trgList = item.getTarget(trgLang).getSegments();
			for ( int i=0; i<srcList.size(); i++ ) {
				writeTU(srcList.get(i),
					(i>trgList.size()-1) ? null : trgList.get(i),
					String.format("%s_s%d", tuid, i+1),
					attributes);
			}
		}
		else { // Un-segmented entry
			writeTU(srcTC, item.getTarget(trgLang), tuid, attributes);
		}
	}
	
	private void writeTU (TextFragment source,
		TextFragment target,
		String tuid,
		Map<String, String> attributes)
	{
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
		writer.writeAttributeString("xml:lang", sourceLang);
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
}
