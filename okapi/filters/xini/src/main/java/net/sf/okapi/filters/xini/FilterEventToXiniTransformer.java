package net.sf.okapi.filters.xini;

import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;

import net.sf.okapi.common.resource.Code;
import net.sf.okapi.common.resource.Segment;
import net.sf.okapi.common.resource.TextContainer;
import net.sf.okapi.common.resource.TextFragment;
import net.sf.okapi.common.resource.TextUnit;
import net.sf.okapi.filters.xini.jaxb.Element;
import net.sf.okapi.filters.xini.jaxb.Empty;
import net.sf.okapi.filters.xini.jaxb.Field;
import net.sf.okapi.filters.xini.jaxb.Fields;
import net.sf.okapi.filters.xini.jaxb.Main;
import net.sf.okapi.filters.xini.jaxb.ObjectFactory;
import net.sf.okapi.filters.xini.jaxb.Page;
import net.sf.okapi.filters.xini.jaxb.PlaceHolder;
import net.sf.okapi.filters.xini.jaxb.PlaceHolderType;
import net.sf.okapi.filters.xini.jaxb.Seg;
import net.sf.okapi.filters.xini.jaxb.Xini;
import net.sf.okapi.filters.xini.jaxb.Page.Elements;

public class FilterEventToXiniTransformer {
	private static final Logger LOGGER = Logger
			.getLogger(FilterEventToXiniTransformer.class.getName());

	private ObjectFactory objectFactory = new ObjectFactory();
	private Marshaller m;
	private JAXBContext jc;

	private Xini xini;
	private Main main;
	private Page currentPage;

	private int currentPageId;
	private int currentElementId;
	private int currentFieldId;
	private int phCounter;

	public void init() {
		try {

			jc = JAXBContext.newInstance(ObjectFactory.class);
			m = jc.createMarshaller();
			m.setProperty("jaxb.noNamespaceSchemaLocation", "http://www.ontram.com/xsd/xini.xsd");

		} catch (JAXBException e) {
			throw new RuntimeException(e);
		}
		
		currentPageId = 0;
		currentElementId = 10;
		currentFieldId = 0;
		
		xini = objectFactory.createXini();
		xini.setSchemaVersion("1.0");
		main = objectFactory.createMain();
		xini.setMain(main);
	}

	protected void startPage(String name) {
		currentPage = new Page();
		currentPageId += 1;
		currentElementId = 10;
		currentFieldId = 0;

		currentPage.setPageID(currentPageId);
		currentPage.setPageName(name);

		currentPage.setElements(new Elements());
		xini.getMain().getPage().add(currentPage);
	}
	
	public void transformTextUnit(TextUnit tu) {

		// Get the source container
		TextContainer textContainer = tu.getSource();

		// Skip empty TextUnits
		boolean srcHasText = textContainer.hasText(false);
		if (!srcHasText) {
			return;
		}

		// Create XML elements
		Element element = objectFactory.createElement();
		Element.ElementContent elementContent = objectFactory.createElementElementContent();
		Fields fields = objectFactory.createFields();
		Field field = objectFactory.createField();
		
		// Connect XML elements
		currentPage.getElements().getElement().add(element);
		element.setElementContent(elementContent);
		elementContent.setFields(fields);
		fields.getField().add(field);
		
		// Set IDs and add meta-data
		element.setElementID(currentElementId);
		field.setFieldID(currentFieldId);
		//TODO That's not the right attribute! Add specific one in the schema
		field.setCustomerTextID(tu.getId());
		
		int currentSegmentId = 0;
		
		for (Segment okapiSegment : textContainer.getSegments()) {
			
			phCounter = 1;
			
			// Skip empty segments
			if(okapiSegment.getContent().getText().isEmpty()) {
				continue;
			}

			Seg xiniSegment = objectFactory.createSeg();
			xiniSegment.setSegID(currentSegmentId);
			field.getSegAndTrans().add(xiniSegment);
			
			TextFragment textFragment = okapiSegment.getContent();

			List<Code> codes = textFragment.getCodes();
			
			if (codes.size() > 0) {
				xiniSegment.getContent().addAll(
						transformInlineTags(textFragment.getCodedText(), codes));
			}
			else {
				xiniSegment.getContent().add(textFragment.getText());
			}
			
			currentSegmentId++;
		}
	}

	private ArrayList<Serializable> transformInlineTags(String codedText, List<Code> codes) {
		ArrayList<Serializable> parts = new ArrayList<Serializable>();
		StringBuilder tempString = new StringBuilder();

		for (int charIndex = 0; charIndex < codedText.length(); charIndex++) {
			
			int codePoint = codedText.codePointAt(charIndex);

			switch (codePoint) {

				case TextFragment.MARKER_OPENING:
					
					int codeOpCharIndex = TextFragment.toIndex(codedText.charAt(++charIndex));
					Code code = codes.get(codeOpCharIndex);
	
					// Save last part of the text that had no codes
					if (tempString.length() > 0)
						parts.add(tempString.toString());
					tempString = new StringBuilder();
	
					int endMarkerIndex = findEndMark(codes, code, codedText, charIndex);
					String innerCodedText="";
					
					if(endMarkerIndex < codedText.length() && endMarkerIndex > 0) {
						
						innerCodedText = codedText.substring(charIndex + 1, endMarkerIndex - 1);
						charIndex = endMarkerIndex;
					}
					
					if(!innerCodedText.equals(""))
						parts.add(getRepresentingObject(code, codes, innerCodedText));
					
					endMarkerIndex = 0;
					break;
					
				case TextFragment.MARKER_ISOLATED:
	
					if (codedText.length() > charIndex + 1) {
						int codeIsoCharIndex = TextFragment.toIndex(codedText.charAt(++charIndex));
						code = codes.get(codeIsoCharIndex);
						
						if (tempString.length() > 0)
							parts.add(tempString.toString());
						tempString = new StringBuilder();
						
						parts.add(getRepresentingObject(code, codes, null));
					}
					break;
					
				default:
					
					if (codedText.length() > charIndex)
						tempString.append(codedText.charAt(charIndex));
					break;
			}
		}

		if (tempString.length() > 0)
			parts.add(tempString.toString());
		return parts;
	}

	private int findEndMark(List<Code> codes, Code code, String codedText, int startCharIndex) {

		for (int charIndex = startCharIndex; charIndex < codedText.length(); charIndex++) {
			
			int codePoint = codedText.codePointAt(charIndex);
			
			if (codePoint == TextFragment.MARKER_CLOSING && codedText.length() > charIndex + 1) {

				int endCodeIndex = TextFragment.toIndex(codedText.charAt(++charIndex));
				Code endCode = codes.get(endCodeIndex);

				if (endCode.getType() == code.getType())
					return charIndex;
			}
		}

		return 0;
	}

	private Serializable getRepresentingObject(Code code, List<Code> codes, String innerCodedText) {

		if (code.getType().equals("br")) {

			Empty emptyContent = new Empty();
			return objectFactory.createTextContentBr(emptyContent);
		}

		PlaceHolder ph = new PlaceHolder();
		ph.setID(phCounter);
		ph.setType(PlaceHolderType.PH);
		
		if (innerCodedText != null && !innerCodedText.isEmpty()) {
			ph.getContent().addAll(transformInlineTags(innerCodedText, codes));
		}
		
		Serializable phelement = objectFactory.createTextContentPh(ph);
		
		phCounter++;

		return phelement;

	}

	public void marshall(OutputStream os) {

		try {
			//TODO revert after testing!
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		} catch (PropertyException e) {
			LOGGER.warning("JAXB PropertyException: " + e.getLocalizedMessage());
		}
		try {
			m.marshal(xini, os);
		} catch (JAXBException e) {
			throw new RuntimeException(e);
		}

	}

}
