package net.sf.okapi.filters.ts;

import java.util.LinkedList;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import net.sf.okapi.common.Event;
import net.sf.okapi.common.EventType;
import net.sf.okapi.common.MimeTypeMapper;
import net.sf.okapi.common.exceptions.OkapiBadFilterInputException;
import net.sf.okapi.common.exceptions.OkapiIOException;
import net.sf.okapi.common.resource.DocumentPart;
import net.sf.okapi.common.resource.TextUnit;
import net.sf.okapi.common.skeleton.GenericSkeleton;
import net.sf.okapi.filters.ts.stax.Characters;
import net.sf.okapi.filters.ts.stax.EndElement;
import net.sf.okapi.filters.ts.stax.StartElement;
import net.sf.okapi.filters.ts.stax.StaxObject;

public class TsMessage {

	Parameters params;
	String trgLang;
	XMLStreamReader reader;	
	LinkedList<Event> queue;
	LinkedList<StaxObject> staxObjects;
	String linebreak;
	Elem currentElem=Elem.NEUTRAL;
	
	static enum Type {UNINITIALIZED, MISSING, UNFINISHED, OBSOLETE, INVALID};	
	static enum Elem {SOURCE, TARGET, NEUTRAL};
	
	public TsMessage(String trgLang, XMLStreamReader reader, LinkedList<Event> queue, String linebreak, Parameters params){
		this.staxObjects = new LinkedList<StaxObject>();
		this.trgLang = trgLang;
		this.reader = reader;
		this.queue = queue;
		this.linebreak = linebreak;
		this.params = params;
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
					if(currentElem == Elem.SOURCE){
						if(staxObjects.getLast() instanceof CharactersSource){
							CharactersSource cs = (CharactersSource) staxObjects.getLast();
							cs.append(reader);
						}else{
							staxObjects.add(new CharactersSource(reader));	
						}
					}else if(currentElem == Elem.TARGET){
						if(staxObjects.getLast() instanceof CharactersTranslation){
							CharactersTranslation ct = (CharactersTranslation) staxObjects.getLast();
							ct.append(reader);
						}else{
							staxObjects.add(new CharactersTranslation(trgLang, reader));
						}						
					}else{
						staxObjects.add(new Characters(reader));	
					}
					break;


				case XMLStreamConstants.START_ELEMENT:
					if(reader.getLocalName().equals("source")){
						currentElem = Elem.SOURCE;
						staxObjects.add(new StartElementSource(reader));
					}else if(reader.getLocalName().equals("translation")){
						currentElem = Elem.TARGET;
						staxObjects.add(new StartElementTranslation(trgLang,reader));
					}else if(reader.getLocalName().equals("byte")){						
						
						String value = getAttributeValue(reader, "value");
						if(value == null){
							//TODO: Invalid Byte Element
							break;
						}else if(value.trim() == null){
							//TODO: Invalid Value
							break;
						}
						
						if(params.decodeByteValues){
							System.out.println("Decode byte values");
							value = decodeByteValue(value);
						}else{
							value = "<byte value=\""+value+"\"/>";
							System.out.println("Do not decode byte values");
						}
						//encodeByteValue(value);
						
						if(currentElem == Elem.SOURCE){
							
							if(staxObjects.getLast() instanceof CharactersSource){
								CharactersSource cs = (CharactersSource) staxObjects.getLast();
								cs.append(value);
							}else{
								staxObjects.add(new CharactersSource(value));	
							}
							
						}else if(currentElem == Elem.TARGET){
							
							if(staxObjects.getLast() instanceof CharactersTranslation){
								CharactersTranslation ct = (CharactersTranslation) staxObjects.getLast();
								ct.append(value);
							}else{
								staxObjects.add(new CharactersTranslation(trgLang, value));
							}						
						}						
						
					}else{
						staxObjects.add(new StartElement(reader));	
					}
					break;
					
				case XMLStreamConstants.END_ELEMENT:
					
					if(reader.getLocalName().equals("source")){
						staxObjects.add(new EndElementSource(reader));
						currentElem = Elem.NEUTRAL;
					}else if(reader.getLocalName().equals("translation")){
						staxObjects.add(new EndElementTranslation(reader));
						currentElem = Elem.NEUTRAL;
					}else if (reader.getLocalName().equals("message")){
						staxObjects.add(new EndElement(reader));
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
				queue.add(new Event(EventType.DOCUMENT_PART, 
						new DocumentPart(String.valueOf(1), 
								false, 
								skel)));
			}else if(elemTrg.type == Type.UNFINISHED){
				
				//TODO: set the correct id sequence
				TextUnit tu = new TextUnit(String.valueOf(1));
				tu.setName(msgId);
				
				elemTrg.setTu(tu);
				
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
				
				if ( params.useCodeFinder )
					params.codeFinder.process(tu.getSourceContent());
				
				tu.setSkeleton(skel);
				tu.setMimeType(MimeTypeMapper.TS_MIME_TYPE);
				queue.add(new Event(EventType.TEXT_UNIT, tu));

				
			}else{
				
				//TODO: set the correct id sequence
				TextUnit tu = new TextUnit(String.valueOf(1));
				tu.setName(msgId);
				
				elemTrg.setTu(tu);
				
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
				tu.setMimeType(MimeTypeMapper.TS_MIME_TYPE);
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
	
	/**
	 * Retrieves the value of an attribute for the current open element.
	 * @param reader the XMLStreamReader to retrieve the attribute from.
	 * @param attr the name of the attribute to retrieve the value from.
	 * @return the value of the attribute.
	 */
	private String getAttributeValue(XMLStreamReader reader, String attr){
		//TO DO: Consider moving this into a general XMLStreamReader helper class
		int count = reader.getAttributeCount();
		for ( int i=0; i<count; i++ ) {
			if(reader.getAttributeLocalName(i).equals(attr)){
				return reader.getAttributeValue(i);
			}
		}
		return null;
	}
	
	/**
	 * Decodes the byte elements value, hexadecimal if it starts with x otherwise decimal.
	 * @param value the value of the byte element.
	 * @return the decoded value.
	 */
	private String decodeByteValue(String value){
		//TO DO: Consider moving this into a general XML helper class
		String newStr;
		try{
		if(value.startsWith("x")){
			newStr=value.substring(1, value.length());
			int i= Integer.parseInt(newStr,16);
			char c = (char)i;
			return ""+c;
		}else{
			int i= Integer.parseInt(value,16);
			char c = (char)i;
			return ""+c;
		}
		}catch(NumberFormatException ne){
			throw new OkapiBadFilterInputException("Invalid value ("+value+" ) in byte element. ");
		}
	}
}
