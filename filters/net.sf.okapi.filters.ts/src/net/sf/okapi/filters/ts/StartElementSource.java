package net.sf.okapi.filters.ts;

import javax.xml.stream.XMLStreamReader;

import net.sf.okapi.filters.ts.stax.StartElement;

public class StartElementSource extends StartElement{

	public StartElementSource(XMLStreamReader reader){
		super(reader);
	}
	
	public String toString(){
		return "[Source]" + super.toString();
	}
}