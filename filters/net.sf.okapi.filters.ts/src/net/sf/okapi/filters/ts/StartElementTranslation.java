package net.sf.okapi.filters.ts;

import javax.xml.stream.XMLStreamReader;

import net.sf.okapi.filters.ts.TsMessage.Type;
import net.sf.okapi.filters.ts.stax.Attribute;
import net.sf.okapi.filters.ts.stax.StartElement;

public class StartElementTranslation extends StartElement{

	Type type = Type.UNINITIALIZED;
	
	public StartElementTranslation(XMLStreamReader reader){
		super(reader);
		setType();
	}
	
	public StartElementTranslation(Type type){
		super("translation");
		
		if (type == Type.UNFINISHED){
			attributes.add(new Attribute("","type","unfinished"));
			this.type = type;
		}else{
			this.type = Type.MISSING;
		} 
	}
	
	void setType(){
		for(Attribute attr: attributes){
			if(attr.getLocalname().equals("type")){
				if(attr.getValue().equals("unfinished"))
					this.type = Type.UNFINISHED;
				else if (attr.getValue().equals("obsolete"))
					this.type = Type.OBSOLETE;
				else
					this.type = Type.INVALID;
				return;
			}
		}
		this.type = Type.MISSING;
	}
	
	public String toString(){
		return "[Translation Type is:"+type+"]" + super.toString();
	}
}