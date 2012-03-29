/*===========================================================================
  Copyright (C) 2011 by the Okapi Framework contributors
-----------------------------------------------------------------------------
  This library is free software; you can redistribute it and/or modify it
  under the terms of the GNU Lesser General Public License as published by
  the Free Software Foundation; either version 2.1 of the License, or (at
  your option) any later version.

  This library is distributed in the hope that it will be useful, but
  WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser
  General Public License for more details.

  You should have received a copy of the GNU Lesser General Public License
  along with this library; if not, write to the Free Software Foundation,
  Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA

  See also the full LGPL text here: http://www.gnu.org/copyleft/lesser.html
===========================================================================*/

package net.sf.okapi.filters.xini.rainbowkit;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.EventType;
import net.sf.okapi.common.MimeTypeMapper;
import net.sf.okapi.common.exceptions.OkapiIOException;
import net.sf.okapi.common.resource.Ending;
import net.sf.okapi.common.resource.ITextUnit;
import net.sf.okapi.common.resource.RawDocument;
import net.sf.okapi.common.resource.Segment;
import net.sf.okapi.common.resource.StartDocument;
import net.sf.okapi.common.resource.TextContainer;
import net.sf.okapi.common.resource.TextFragment;
import net.sf.okapi.common.resource.TextUnit;
import net.sf.okapi.filters.xini.InlineCodeTransformer;
import net.sf.okapi.filters.xini.jaxb.Element;
import net.sf.okapi.filters.xini.jaxb.Field;
import net.sf.okapi.filters.xini.jaxb.Page;
import net.sf.okapi.filters.xini.jaxb.Seg;
import net.sf.okapi.filters.xini.jaxb.Xini;
import net.sf.okapi.filters.xini.jaxb.Element.ElementContent;

public class XINIRainbowkitReader {
	private Xini xini;
	private InputStream xiniStream;
	private InlineCodeTransformer transformer;

	public XINIRainbowkitReader() {
		transformer = new InlineCodeTransformer();
	}

	@SuppressWarnings("unchecked")
	public void open(RawDocument input) {

		xiniStream = input.getStream();

		// unmarshalling
		try {
			JAXBContext jc = JAXBContext.newInstance(Xini.class.getPackage().getName());
			Unmarshaller u = jc.createUnmarshaller();
			JAXBElement<Xini> jaxbXini = (JAXBElement<Xini>) u.unmarshal(xiniStream);
			xini = jaxbXini.getValue();
		}
		catch (JAXBException e) {
			throw new RuntimeException(e);
		}
	}

	public void close() {
		try {
			if ( xiniStream != null ) {
				xiniStream.close();
				xiniStream = null;
			}
		}
		catch (IOException e) {
			throw new OkapiIOException(e);
		}
	}


	/**
	 * Creates {@link Event}s representing a {@link Xini} from only the XINI page with the given name
	 */
	public LinkedList<Event> getFilterEvents(String relDocName) {
		LinkedList<Event> events = new LinkedList<Event>();

		for (Page page : xini.getMain().getPage()) {
			if(page.getPageName().equals(relDocName)) {
				events.addAll(processPage(page));
				break;
			}
		}

		return events;
	}

	/**
	 * Creates {@link Event}s representing a XINI {@link Page}
	 */
	private LinkedList<Event> processPage(Page page) {
		LinkedList<Event> events = new LinkedList<Event>();

		String startDocId = page.getPageID()+"";
		StartDocument startDoc = new StartDocument(startDocId);

		// set Properties
		startDoc.setName(page.getPageName());
//		startDoc.setFilterParameters(getParameters());
		startDoc.setFilterWriter(new XINIRainbowkitWriter());
		startDoc.setType(MimeTypeMapper.XINI_MIME_TYPE);
		startDoc.setMimeType(MimeTypeMapper.XINI_MIME_TYPE);
		startDoc.setMultilingual(false);

		events.add(new Event(EventType.START_DOCUMENT, startDoc));

		for (Element element : page.getElements().getElement()) {
			events.addAll(processElement(element));
		}

		Ending ending = new Ending(page.getPageID()+"end");
		events.add(new Event(EventType.END_DOCUMENT, ending));

		return events;
	}

	/**
	 * Creates {@link Event}s representing a XINI {@link Element}
	 */
	private LinkedList<Event> processElement(Element element) {
		LinkedList<Event> events = new LinkedList<Event>();
		ElementContent elContent = element.getElementContent();

		if (elContent.getFields() != null) {
			for (Field field : elContent.getFields().getField()) {
				events.addAll(processField(field));
			}
		}
		else if (elContent.getTable() != null) {
			// that's not generated by the writer
		}
		else if (elContent.getINITable() != null) {
			// that's not generated by the writer
		}
		return events;
	}

	/**
	 * Creates {@link Event}s representing a XINI {@link Field}
	 */
	private LinkedList<Event> processField(Field field) {
		LinkedList<Event> events = new LinkedList<Event>();

		ITextUnit tu = new TextUnit(field.getExternalID());
		TextContainer tc = new TextContainer();

		String emptySegsFlags = field.getEmptySegmentsFlags();
		if (emptySegsFlags == null) {

			// The emptySegmentsFlag is only used in ONTRAM TKit
			int segIndex = 0;
			for (Seg xiniSeg : field.getSeg()) {

				TextFragment tf = new TextFragment(processSegment(xiniSeg));
				boolean collapseIfPreviousEmpty = segIndex == 0;
				tc.getSegments().append(tf, collapseIfPreviousEmpty);

				segIndex++;
			}
		}
		else {

			// This is definitely from the ONTRAM TKit
			int segIndex;
			int nonEmptySegIndex = 0;
			for (segIndex = 0; segIndex < emptySegsFlags.length(); segIndex++) {

				char empty = emptySegsFlags.charAt(segIndex);
				if (empty == '0') {

					Seg xiniSeg = field.getSeg().get(nonEmptySegIndex);
					nonEmptySegIndex++;

					TextFragment tf = new TextFragment(processSegment(xiniSeg));
					// To merge first XINI Segment into previously created Segment in TC
					boolean collapseIfPreviousEmpty = segIndex == 0;
					tc.getSegments().append(tf, collapseIfPreviousEmpty);
				}
				else {
					tc.getSegments().append(new Segment(""));
				}
			}
		}

		tu.setSource(tc);
		tu.setName(field.getLabel());

		events.add(new Event(EventType.TEXT_UNIT, tu));

		return events;
	}

	private TextFragment processSegment(Seg xiniSeg) {
		//Text fragments are never empty
		TextFragment fragment = new TextFragment() {

			@Override
			public boolean isEmpty() {
				return false;
			}
			
		};

		// new
		String leadingSpacer = xiniSeg.getLeadingSpacer();
		if(leadingSpacer != null){
			fragment.append(leadingSpacer);
		}

		fragment.append(transformer.serializeTextPartsForTKit(xiniSeg.getContent()));

		if(xiniSeg.getTrailingSpacer() != null){
			fragment.append(xiniSeg.getTrailingSpacer());
		}
		return fragment;
	}
}
