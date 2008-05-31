package net.sf.okapi.applications.rainbow.utilities.alignment;

import net.sf.okapi.common.XMLWriter;
import net.sf.okapi.common.resource.IExtractionItem;

public class TMXWriter {
	
	private XMLWriter   writer;
	private TMXContent  tmxCont;
	private String      sourceLang;
	private String      targetLang;

	public void close () {
		if ( writer != null ) {
			writer.close();
			writer = null;
		}
		
	}
	
	public void create (String path) {
		if ( path == null ) throw new NullPointerException();
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
		writer.writeStartElement("body");
	}
	
	public void writeEndDocument () {
		writer.writeEndElementLineBreak(); // body
		writer.writeEndElementLineBreak(); // tmx
		writer.writeEndDocument();
	}
	
	public void writeItem (IExtractionItem sourceItem,
		IExtractionItem targetItem)
	{
		if ( sourceItem == null ) throw new NullPointerException();
		// targetItem can be null, not check for it
		
		writer.writeStartElement("tu");

		writer.writeStartElement("tuv");
		writer.writeAttributeString("xml:lang", sourceLang);
		writer.writeStartElement("seg");
		writer.writeRawXML(tmxCont.setContent(sourceItem.getContent()).toString());
		writer.writeEndElement(); // seg
		writer.writeEndElementLineBreak(); // tuv
		
		if ( targetItem != null ) {
			writer.writeStartElement("tuv");
			writer.writeAttributeString("xml:lang", targetLang);
			writer.writeStartElement("seg");
			writer.writeRawXML(tmxCont.setContent(targetItem.getContent()).toString());
			writer.writeEndElement(); // seg
			writer.writeEndElementLineBreak(); // tuv
		}
		
		writer.writeEndElementLineBreak(); // tu
	}
}
