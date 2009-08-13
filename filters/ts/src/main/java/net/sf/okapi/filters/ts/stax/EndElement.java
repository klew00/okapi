package net.sf.okapi.filters.ts.stax;

import javax.xml.stream.XMLStreamReader;

import net.sf.okapi.common.skeleton.GenericSkeleton;
import net.sf.okapi.filters.ts.stax.StaxObject;

public class EndElement implements StaxObject{
	String namespace;
	String localname;
	
	public EndElement(XMLStreamReader reader){
		readObject(reader);
	}

	public EndElement(String localname) {
		this.namespace = "";
		this.localname = localname;
	}

	public void readObject(XMLStreamReader reader){
		this.namespace = reader.getPrefix();
		this.localname = reader.getLocalName();
	}
	
	public String toString(){
		if (( namespace == null ) || ( namespace.length()==0 ))
			return "</"+localname+">";
		else
			return "</"+namespace+":"+localname+">";
	}
	
	public GenericSkeleton getSkeleton(){
		if (( namespace == null ) || ( namespace.length()==0 ))
			return new GenericSkeleton("</"+localname+">");
		else
			return new GenericSkeleton("</"+namespace+":"+localname+">");
	}
}