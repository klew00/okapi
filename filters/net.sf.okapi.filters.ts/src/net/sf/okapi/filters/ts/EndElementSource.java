package net.sf.okapi.filters.ts;

import javax.xml.stream.XMLStreamReader;

import net.sf.okapi.filters.ts.stax.EndElement;

public class EndElementSource extends EndElement{

	public EndElementSource(XMLStreamReader reader){
		super(reader);
	}
	
	public String toString(){
		return "[End Source]" + super.toString();
	}
}