package net.sf.okapi.filters.ts;

import javax.xml.stream.XMLStreamReader;
import net.sf.okapi.common.resource.Property;
import net.sf.okapi.common.resource.TextContainer;
import net.sf.okapi.common.resource.TextUnit;
import net.sf.okapi.common.skeleton.GenericSkeleton;
import net.sf.okapi.filters.ts.TsMessage.Type;
import net.sf.okapi.filters.ts.stax.Attribute;
import net.sf.okapi.filters.ts.stax.StartElement;

public class StartElementTranslation extends StartElement{

	Type type = Type.UNINITIALIZED;
	TextUnit tu;
	String lang;
	
	public StartElementTranslation(String lang, XMLStreamReader reader){
		super(reader);
		this.lang = lang;
		setType();
	}
	
	public void setTu(TextUnit tu){
		this.tu = tu;
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
				if(attr.getValue().equals("unfinished")){
					this.type = Type.UNFINISHED;
				}else if (attr.getValue().equals("obsolete")){
					this.type = Type.OBSOLETE;
				}else{
					this.type = Type.INVALID;
				}
				return;
			}
		}
		this.type = Type.MISSING;
	}
	
	public String toString(){
		return "[Translation Type is:"+type+"]" + super.toString();
	}
	
	/**
	 * Generates the skeleton for the start element.
	 * @return the generated GenericSkeleton. 
	 */	
	public GenericSkeleton getSkeleton(){

		GenericSkeleton skel = new GenericSkeleton();
		skel.append("<"+localname);

		if(tu != null){
			boolean foundType = false;
			for(Attribute attr: attributes){
				if(attr.getLocalname().equals("type")){
					foundType = true;
					if(attr.getValue().equals("unfinished")){
						skel.append(" ");
						skel.addValuePlaceholder(tu, Property.APPROVED, lang);
						tu.setTargetProperty(lang, new Property(Property.APPROVED, "no", false));
					}
				}else{
					skel.append(String.format(" %s=\"%s\"", attr.getLocalname(), attr.getValue()));
				}
			}
			if(!foundType){
				skel.append(" ");
				skel.addValuePlaceholder(tu, Property.APPROVED, lang);
				tu.setTargetProperty(lang, new Property(Property.APPROVED, "yes", false));
			}
			
		}else{
			for(Attribute attr: attributes){
				skel.append(String.format(" %s=\"%s\"", attr.getLocalname(), attr.getValue()));
			}
		}
		skel.append(">");
		return skel;
	}
	
}