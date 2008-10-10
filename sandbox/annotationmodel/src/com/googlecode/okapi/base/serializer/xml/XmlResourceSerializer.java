package com.googlecode.okapi.base.serializer.xml;

import java.io.IOException;

import com.googlecode.okapi.resource.Document;
import com.googlecode.okapi.resource.PartId;
import com.googlecode.okapi.resource.TextFlow;
import com.googlecode.okapi.resource.textflow.ContentFragment;
import com.googlecode.okapi.resource.textflow.TextFragment;

import nu.xom.Attribute;
import nu.xom.Element;
import nux.xom.io.StreamingSerializer;

public class XmlResourceSerializer {

	StreamingSerializer serializer;
	
	public XmlResourceSerializer(StreamingSerializer serializer) {
		this.serializer = serializer;
	}
	
	public void serialize(Document document){
		
	}
	
	public void serialize(TextFlow tf) throws IOException {
		Element tfElem = new Element("text-flow");
		tfElem.addAttribute(
				new Attribute("id", tf.getId().get()));
		
		for(ContentFragment cf : tf.getFlow()){
			serialize(cf);
		}
		serializer.write(tfElem);
	}
	
	public void serialize(ContentFragment cf) throws IOException {

		if(cf instanceof TextFragment){
			serialize((TextFragment)cf);
		}
		// TODO other fragments
	}
	
	public void serialize(TextFragment tf) throws IOException {
		Element tfElem = new Element("tf");
		PartId part = tf.getPart();
		tfElem.addAttribute(
				new Attribute("id", tf.getId().get()));
		if(part != null){
			tfElem.addAttribute(
					new Attribute("part-ref", tf.getId().get()));
		}
		tfElem.appendChild(tf.getContent());
		serializer.write(tfElem);
	}
	
}
