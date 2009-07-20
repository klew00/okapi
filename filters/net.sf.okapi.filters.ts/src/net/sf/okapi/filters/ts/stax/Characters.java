package net.sf.okapi.filters.ts.stax;

import javax.xml.stream.XMLStreamReader;

import net.sf.okapi.common.Util;
import net.sf.okapi.common.skeleton.GenericSkeleton;
import net.sf.okapi.filters.ts.stax.StaxObject;

public class Characters implements StaxObject{
	protected String rawText;
	protected String escapedText;
	
	public Characters(XMLStreamReader reader){
		readObject(reader);
	}

	public Characters(String str){
		this.rawText = str;
		this.escapedText = str;
	}
	
	public Characters(){
		this.rawText = "";
		this.escapedText = "";
	}
	
	public void readObject(XMLStreamReader reader){
		this.rawText = reader.getText();
		this.escapedText = Util.escapeToXML(reader.getText(), 0, true, null); 
	}
	
	public void append(XMLStreamReader reader){
		this.rawText = this.rawText.concat(reader.getText()); 
		this.escapedText = this.rawText.concat(Util.escapeToXML(reader.getText(), 0, true, null));
	}
	
	public void append(String s){
		this.rawText = this.rawText.concat(s); 
		this.escapedText = this.rawText.concat(Util.escapeToXML(s, 0, true, null));
	}	
	
	public String toString(){
		return escapedText;
	}
	
	public GenericSkeleton getSkeleton(){
		return new GenericSkeleton(escapedText);
	}
}