package net.sf.okapi.filters.ts;

import javax.xml.stream.XMLStreamReader;
import net.sf.okapi.filters.ts.stax.Attribute;
import net.sf.okapi.filters.ts.stax.StartElement;

public class StartElementMessage extends StartElement{

	String id;
	
	public StartElementMessage(XMLStreamReader reader){
		super(reader);
		setId();
	}
	
	public String getId(){
		return this.id;
	}
	
	public void setId(String id){
		this.id = id;
	}
	
	public void setId(){
		for(Attribute attr: attributes){
			if(attr.getLocalname().equals("id")){
				this.id = attr.getValue();				
				return;
			}
		}
	}
	
	public String toString(){
		return "[Source]" + super.toString();
	}
}