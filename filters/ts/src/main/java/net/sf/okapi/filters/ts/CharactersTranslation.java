package net.sf.okapi.filters.ts;

import javax.xml.stream.XMLStreamReader;

import net.sf.okapi.common.resource.TextContainer;
import net.sf.okapi.common.resource.TextUnit;
import net.sf.okapi.common.skeleton.GenericSkeleton;
import net.sf.okapi.filters.ts.stax.Characters;

public class CharactersTranslation extends Characters{
	
	TextUnit tu;
	String lang;
	
	public CharactersTranslation(String lang, XMLStreamReader reader){
		super(reader);
		this.lang = lang;
	}
	
	public CharactersTranslation(String lang, TextUnit tu){
		super();
		this.lang = lang;
		this.tu = tu;
	}

	public CharactersTranslation(String lang, String str){
		super(str);
		this.lang = lang;
	}
	
	public void setTu(TextUnit tu){
		this.tu = tu;
	}

	public GenericSkeleton getSkeleton(){
		if(tu != null){
			
			if(rawText.length() > 0){
				TextContainer tc = new TextContainer(rawText);
				tu.setTarget(lang, tc);
			}
			GenericSkeleton skel = new GenericSkeleton();
			skel.addContentPlaceholder(tu, lang);
			return skel;	
		}else{
			return super.getSkeleton();
		}
	}
}