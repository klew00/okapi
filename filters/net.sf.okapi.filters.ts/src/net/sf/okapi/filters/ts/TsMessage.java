package net.sf.okapi.filters.ts;

import java.util.LinkedList;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import net.sf.okapi.common.Event;
import net.sf.okapi.common.EventType;
import net.sf.okapi.common.exceptions.OkapiIOException;
import net.sf.okapi.common.resource.DocumentPart;
import net.sf.okapi.common.resource.TextUnit;
import net.sf.okapi.common.skeleton.GenericSkeleton;
import net.sf.okapi.filters.ts.stax.Characters;
import net.sf.okapi.filters.ts.stax.EndElement;
import net.sf.okapi.filters.ts.stax.StartElement;
import net.sf.okapi.filters.ts.stax.StaxObject;

public class TsMessage {

	String trgLang;
	XMLStreamReader reader;	
	LinkedList<Event> queue;
	LinkedList<StaxObject> staxObjects;
	String linebreak;
	
	static enum Type {UNINITIALIZED, MISSING, UNFINISHED, OBSOLETE, INVALID};	
	
	public TsMessage(String trgLang, XMLStreamReader reader, LinkedList<Event> queue, String linebreak){
		this.staxObjects = new LinkedList<StaxObject>();
		this.trgLang = trgLang;
		this.reader = reader;
		this.queue = queue;
		this.linebreak = linebreak;
	}
	
	
	//-- Processing <message> --
	boolean processMessage(int tuId){
		
		staxObjects.add(new StartElementMessage(reader));

		try {
			while(reader.hasNext()){
				
				int eventType = reader.next();
				switch ( eventType ) {
				
				case XMLStreamConstants.COMMENT:
					//TODO
					break;
				
				case XMLStreamConstants.CHARACTERS: 
					
					if(staxObjects.getLast() instanceof StartElementSource){
						staxObjects.add(new CharactersSource(reader));
					}else if(staxObjects.getLast() instanceof StartElementTranslation){
						staxObjects.add(new CharactersTranslation(trgLang, reader));
					}else{
						staxObjects.add(new Characters(reader));	
					}
					break;


				case XMLStreamConstants.START_ELEMENT:
					
					if(reader.getLocalName().equals("source")){
						staxObjects.add(new StartElementSource(reader));
					}else if(reader.getLocalName().equals("translation")){
						staxObjects.add(new StartElementTranslation(reader));
					}else{
						staxObjects.add(new StartElement(reader));	
					}
					break;
					
				case XMLStreamConstants.END_ELEMENT:
					
					if(reader.getLocalName().equals("source")){
						staxObjects.add(new EndElementSource(reader));
					}else{
						staxObjects.add(new EndElement(reader));
					}
					
					//-- Message Processed --
					if (reader.getLocalName().equals("message")){
						processMessage();
						return true;		
					}
				}
			}
		} catch (XMLStreamException e) {
			throw new OkapiIOException(e);
		}
		return false;		
	}
	
	public void processMessage(){
		
		String msgId = getStartElementMessage().getId();
		
		StartElementTranslation elemTrg = getStartElementTranslation();
		if(elemTrg!=null){
			if(elemTrg.type == Type.OBSOLETE){
				GenericSkeleton skel = new GenericSkeleton();
				for(StaxObject so: staxObjects){
					//TODO: set the correct id sequence
					skel.add(so.getSkeleton());
				}
				System.out.println("Skel: "+skel);
				queue.add(new Event(EventType.DOCUMENT_PART, 
						new DocumentPart(String.valueOf(1), 
								false, 
								skel)));
			}else if(elemTrg.type == Type.UNFINISHED){
				
				//TODO: set the correct id sequence
				TextUnit tu = new TextUnit(String.valueOf(1));
				tu.setName(msgId);
				CharactersSource charsSrc = getCharactersSource();
				if(charsSrc!=null){
					charsSrc.setTu(tu);
				}
				CharactersTranslation charsTrg = getCharactersTranslation();
				if(charsTrg!=null){
					charsTrg.setTu(tu);
				}else{
					staxObjects.add(staxObjects.indexOf(elemTrg)+1, new CharactersTranslation(trgLang, tu));	
				}
				
				GenericSkeleton skel = new GenericSkeleton();
				for(StaxObject so: staxObjects){
					//TODO: set the correct id sequence
					skel.add(so.getSkeleton());
				}
				tu.setSkeleton(skel);
				tu.setMimeType("text/xml");
				queue.add(new Event(EventType.TEXT_UNIT, tu));

				
			}else{
				
				//TODO: set the correct id sequence
				TextUnit tu = new TextUnit(String.valueOf(1));
				tu.setName(msgId);
				CharactersSource charsSrc = getCharactersSource();
				if(charsSrc!=null){
					charsSrc.setTu(tu);
				}
				CharactersTranslation charsTrg = getCharactersTranslation();
				if(charsTrg!=null){
					charsTrg.setTu(tu);
				}else{
					staxObjects.add(staxObjects.indexOf(elemTrg)+1, new CharactersTranslation(trgLang, tu));	
				}
				
				GenericSkeleton skel = new GenericSkeleton();
				for(StaxObject so: staxObjects){
					//TODO: set the correct id sequence
					skel.add(so.getSkeleton());
				}
				tu.setSkeleton(skel);
				tu.setMimeType("text/xml");
				queue.add(new Event(EventType.TEXT_UNIT, tu));
			}
		}else{
			//TODO: Generate Missing Translation Element
			TextUnit tu = new TextUnit(String.valueOf(1));
			tu.setName(msgId);
			CharactersSource charsSrc = getCharactersSource();
			if(charsSrc!=null){
				charsSrc.setTu(tu);
			}
			
			EndElementSource endElemSrc = getEndElementSource();
			staxObjects.add(staxObjects.indexOf(endElemSrc)+1, new EndElementTranslation());
			staxObjects.add(staxObjects.indexOf(endElemSrc)+1, new CharactersTranslation(trgLang, tu));
			staxObjects.add(staxObjects.indexOf(endElemSrc)+1, new StartElementTranslation(Type.UNFINISHED));
			staxObjects.add(staxObjects.indexOf(endElemSrc)+1, new Characters(linebreak));
			

			GenericSkeleton skel = new GenericSkeleton();
			for(StaxObject so: staxObjects){
				//TODO: set the correct id sequence
				skel.add(so.getSkeleton());
			}
			tu.setSkeleton(skel);
			tu.setMimeType("text/xml");
			queue.add(new Event(EventType.TEXT_UNIT, tu));
		}
		
		/*for(StaxObject so: staxObjects){

			if (so.().equals("source")){

			}else if (so.().equals("translation")){

			}
			
			if (so.END().equals("translation")){

				if(procMsgTransValue!=null){
					TextContainer tc = new TextContainer(procMsgTransValue);
					tu.setTarget(trgLang, tc);
				}
				skel.addContentPlaceholder(tu, trgLang);
				procMsgTrans = false;
				
			}
			
			if (so.END.equals("source")){
				
			}
		}*/
	}
	
	
	
	EndElementSource getEndElementSource(){
		for(StaxObject so: staxObjects){
			if(so instanceof EndElementSource)
				return (EndElementSource)so;
		}
		return null;
	}
	
	StartElementTranslation getStartElementTranslation(){
		for(StaxObject so: staxObjects){
			if(so instanceof StartElementTranslation)
				return (StartElementTranslation)so;
			
		}
		return null;
	}

	StartElementMessage getStartElementMessage(){
		for(StaxObject so: staxObjects){
			if(so instanceof StartElementMessage)
				return (StartElementMessage)so;
			
		}
		return null;
	}

	CharactersSource getCharactersSource(){
		for(StaxObject so: staxObjects){
			if(so instanceof CharactersSource)
				return (CharactersSource)so;
		}
		return null;
	}
	
	CharactersTranslation getCharactersTranslation(){
		for(StaxObject so: staxObjects){
			if(so instanceof CharactersTranslation)
				return (CharactersTranslation)so;
		}
		return null;
	}
	
	public void printStaxObjects(String intro){
		System.out.println("StaxObjects intro: "+intro);
		System.out.println("StaxObjects size: "+staxObjects.size());
		for(StaxObject so: staxObjects){
			System.out.println("StaxObjects"+so.toString());
		}
	}
	
}
