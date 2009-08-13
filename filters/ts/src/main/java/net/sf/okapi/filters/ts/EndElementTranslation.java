package net.sf.okapi.filters.ts;

import javax.xml.stream.XMLStreamReader;
import net.sf.okapi.filters.ts.stax.EndElement;

public class EndElementTranslation extends EndElement{

	public EndElementTranslation(XMLStreamReader reader){
		super(reader);
	}
	
	public EndElementTranslation(){
		super("translation");
	}
	
	public String toString(){
		return "[End Source]" + super.toString();
	}
}