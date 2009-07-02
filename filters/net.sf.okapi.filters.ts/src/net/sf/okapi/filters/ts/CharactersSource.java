package net.sf.okapi.filters.ts;

import javax.xml.stream.XMLStreamReader;

import net.sf.okapi.common.resource.TextContainer;
import net.sf.okapi.common.resource.TextUnit;
import net.sf.okapi.common.skeleton.GenericSkeleton;
import net.sf.okapi.filters.ts.stax.Characters;

public class CharactersSource extends Characters{
	
	TextUnit tu;
	
	public CharactersSource(XMLStreamReader reader){
		super(reader);
	}
	
	public void setTu(TextUnit tu){
		this.tu = tu;
	}

	public GenericSkeleton getSkeleton(){
		if(tu != null){
			TextContainer tc = new TextContainer(rawText);
			tu.setSource(tc);
			GenericSkeleton skel = new GenericSkeleton();
			skel.addContentPlaceholder(tu);
			return skel;				
		}else{
			return super.getSkeleton();
		}
	}
}