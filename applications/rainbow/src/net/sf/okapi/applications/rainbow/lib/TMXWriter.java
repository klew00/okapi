package net.sf.okapi.applications.rainbow.lib;

import java.util.List;

import net.sf.okapi.common.XMLWriter;
import net.sf.okapi.common.resource.IExtractionItem;
import net.sf.okapi.common.resource.IPart;

public class TMXWriter {
	
	private XMLWriter   writer;
	private TMXContent  tmxCont;
	private String      sourceLang;
	private String      targetLang;
	private int         itemCount;


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
		writer.create(path);
	}
	
	public void writeStartDocument (String sourceLanguage,
		String targetLanguage)
	{
		if ( sourceLanguage == null ) throw new NullPointerException();
		if ( targetLanguage == null ) throw new NullPointerException();
		this.sourceLang = sourceLanguage;
		this.targetLang = targetLanguage;
		
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
	}
	
	public void writeEndDocument () {
		writer.writeEndElementLineBreak(); // body
		writer.writeEndElementLineBreak(); // tmx
		writer.writeEndDocument();
	}
	
	public void writeItem (IExtractionItem item)
	{
		if ( item == null ) throw new NullPointerException();
		itemCount++;
		
		String tuid = item.getName();
		if (( tuid == null ) || ( tuid.length() == 0 )) {
			tuid = String.format("autoID%d", itemCount);
		}
		writeTU(item.getSource(), item.getTarget(), tuid);
		
		if ( item.getSource().isSegmented() ) {
			List<IPart> srcList = item.getSourceSegments();
			List<IPart> trgList = item.getTargetSegments();
			for ( int i=0; i<srcList.size(); i++ ) {
				writeTU(srcList.get(i),
					(i>trgList.size()-1) ? null : trgList.get(i),
					String.format("%s_s%d", tuid, i+1));
			}
		}
	}
	
	private void writeTU (IPart source,
		IPart target,
		String tuid)
	{
		writer.writeStartElement("tu");
		if (( tuid != null ) && ( tuid.length() > 0 ))
			writer.writeAttributeString("tuid", tuid);

		writer.writeStartElement("tuv");
		writer.writeAttributeString("xml:lang", sourceLang);
		writer.writeStartElement("seg");
		writer.writeRawXML(tmxCont.setContent(source).toString());
		writer.writeEndElement(); // seg
		writer.writeEndElementLineBreak(); // tuv
		
		if ( target != null ) {
			writer.writeStartElement("tuv");
			writer.writeAttributeString("xml:lang", targetLang);
			writer.writeStartElement("seg");
			writer.writeRawXML(tmxCont.setContent(target).toString());
			writer.writeEndElement(); // seg
			writer.writeEndElementLineBreak(); // tuv
		}
		
		writer.writeEndElementLineBreak(); // tu
	}
}
