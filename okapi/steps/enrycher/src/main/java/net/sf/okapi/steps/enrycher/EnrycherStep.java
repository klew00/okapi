/*===========================================================================
  Copyright (C) 2012 by the Okapi Framework contributors
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

package net.sf.okapi.steps.enrycher;

import java.util.LinkedList;
import java.util.List;

import net.htmlparser.jericho.Attribute;
import net.htmlparser.jericho.Attributes;
import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.Source;
import net.sf.okapi.common.Event;
import net.sf.okapi.common.EventType;
import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.UsingParameters;
import net.sf.okapi.common.annotation.GenericAnnotation;
import net.sf.okapi.common.annotation.GenericAnnotationType;
import net.sf.okapi.common.annotation.GenericAnnotations;
import net.sf.okapi.common.pipeline.BasePipelineStep;
import net.sf.okapi.common.resource.Code;
import net.sf.okapi.common.resource.ITextUnit;
import net.sf.okapi.common.resource.MultiEvent;
import net.sf.okapi.common.resource.TextFragment;

@UsingParameters(Parameters.class)
public class EnrycherStep extends BasePipelineStep {

	private final static int MAXEVENTS = 20;  //TODO:--move to Parameters
	private final static String REF_PREFIX = "REF:"; // Should be the same as in the ITS engine
	
	private LinkedList<Event> events;
	private int maxEvents = MAXEVENTS;
	private boolean needReset;
	private EnrycherClient client;

	public EnrycherStep () {
		client = new EnrycherClient();
	}
	
	private void closeAndClean () {
		if ( events != null ) {
			events.clear();
			events = null;
		}
	}
	
	@Override
	public String getName () {
		return "Enrycher";
	}

	@Override
	public String getDescription () {
		return "Applies Enrycher ITS annotations to the source content. "
			+ "Expects: filter events. Sends back: filter events.";
	}

	@Override
	public IParameters getParameters () {
		return client.getParameters();
	}

	@Override
	public void setParameters (IParameters params) {
		client.setParameters((Parameters)params);
	}


	@Override
	protected Event handleStartBatch (Event event) {
		events = new LinkedList<Event>();
		maxEvents = MAXEVENTS;
		//maxEvents = params.getMaxEvents();
		if (( maxEvents < 1 ) || ( maxEvents > 1000 )) maxEvents = MAXEVENTS;
		return event;
	}
	
	@Override
	public Event handleEvent (Event event) {
		switch ( event.getEventType() ) {
		case START_BATCH:
			return handleStartBatch(event);
		// Events to store until the next trigger
		case TEXT_UNIT:
			// Store and possibly trigger
			return storeAndPossiblyProcess(event, false);			
		case DOCUMENT_PART:
		case START_GROUP:
		case END_GROUP:
		case START_SUBFILTER:
		case END_SUBFILTER:
			// Store and possibly trigger
			return storeAndPossiblyProcess(event, false);
		// Events that force the trigger if needed
		case CUSTOM:
		case MULTI_EVENT:
		case START_SUBDOCUMENT: // Could have text units between start document and sub-document
		case END_DOCUMENT:
		case END_SUBDOCUMENT:
			return storeAndPossiblyProcess(event, true);
		// Events that should clean up
		case CANCELED:
		case END_BATCH:
			closeAndClean();
			break;
			// Events before any storing or after triggers	
		case START_BATCH_ITEM:
		case END_BATCH_ITEM:
		case RAW_DOCUMENT:
		case START_DOCUMENT:
			break; // Do nothing special
		}
		return event;
	}
	
	private Event processEvents () {
		// Do the translations
		getTranslations();
		// Translations are done
		// Now we sent all the stored events down the pipeline
		needReset = true; // To reset the list next time around
		return new Event(EventType.MULTI_EVENT, new MultiEvent(events));
	}
	
	private Event storeAndPossiblyProcess (Event event,
			boolean mustProcess)
	{
			// Reset if needed
			if ( needReset ) {
				needReset = false;
				events.clear();
			}
			// Add the event
			events.add(event);
			// And trigger the process if needed
			if ( mustProcess || ( events.size() >= maxEvents )) {
				return processEvents();
			}
			// Else, if we just store this event, we pass a no-operation event down for now
			return Event.NOOP_EVENT;
	}
	
	private void getTranslations () {
		
		LinkedList<ITextUnit> tus = new LinkedList<ITextUnit>();
		
		if ( events.isEmpty() ) {
			return; // Nothing to do
		}
	
		StringBuffer sb = new StringBuffer();
		
		// Gather the text fragment to translate
		for ( Event event : events ) {
			
			//--process only textunits
			if ( event.isTextUnit() ) {
				ITextUnit tu = event.getTextUnit();
				
				//--skip non translatable--
				if ( !tu.isTranslatable() ) continue;
				
				if ( tu.getSource().hasBeenSegmented() ) {
					
					//TODO: Handle segmented TextUnits
					
				}else{
					TextFragment tf = tu.getSource().getFirstContent();
					
					//--skip empty textunits--
					if ( !tf.hasText() ) continue;

					//System.out.println("Coded text: " + tf.getCodedText());
					tus.add(tu);
					String stext = toCodedHTML(tf);
					//System.out.println("Append: <p id="+String.format("%s", tu.getId())+">"+stext+"</p>\n");
					sb.append("<p id="+String.format("%s", tu.getId())+">"+stext+"</p>\n");
				}
			}
		}
		
		//-- there are textunits to process --
		if ( !tus.isEmpty() ){
			
			//System.out.println("To Enrycher: " + sb);
			String fromEnrycher = client.processContent(sb.toString());
			//System.out.println("From Enrycher: " + fromEnrycher);

			//--Parse the returned string--
			Source source = new Source(fromEnrycher);

			//process each of the TUs
			for (ITextUnit tu : tus) {
				
				if ( tu.getSource().hasBeenSegmented() ) {
					
					//TODO: Handle segmented TextUnits
					
				}
				else {
					//System.out.println("TU ID: " + tu.getId());
					Element p = source.getElementById(tu.getId());

					//System.out.println("P: " + p.getContent());
					Source pSource = new Source(p.getContent().toString());

					//--Get the ITS spans from the P--
					List<Element> spans = getItsElements(pSource);

					//--Generate the insertions
					List<Insertion> insertions = getInsertions(tu.getSource().getFirstContent().toString(), pSource.toString(), spans);

					//--Annotate the TextFragment
					annotateFragment(tu.getSource().getFirstContent(), insertions);
				}
			}
		}
	}
	
	/**
	 * Converts from coded text to coded HTML. Copied from QueryUtil.java
	 * The resulting string is also valid XML.
	 * @param fragment the fragment to convert.
	 * @return The resulting HTML string.
	 */
	public String toCodedHTML (TextFragment fragment) {
		if ( fragment == null ) {
			return "";
		}
		Code code;
		StringBuilder sb = new StringBuilder();
		String text = fragment.getCodedText();
		for ( int i = 0; i < text.length(); i++ ) {
			switch (text.charAt(i)) {
			case TextFragment.MARKER_OPENING:
				code = fragment.getCode(text.charAt(++i));
				sb.append(String.format("<u id='%d'>", code.getId()));
				break;
			case TextFragment.MARKER_CLOSING:
				i++;
				sb.append("</u>");
				break;
			case TextFragment.MARKER_ISOLATED:
				code = fragment.getCode(text.charAt(++i));
				sb.append(String.format("<br id='%d'/>", code.getId()));
				break;
			case '&':
				sb.append("&amp;");
				break;
			case '<':
				sb.append("&lt;");
				break;
			default:
				sb.append(text.charAt(i));
			}
		}
		return sb.toString();
	}
	
	/**
	 * Get a list of insertions that includes start, end, and InlineAnnotation
	 * @param firstStr
	 * @param secondStr
	 * @param itsSpans
	 * @return
	 */
	List<Insertion> getInsertions (String firstStr,
		String secondStr,
		List<Element> itsSpans)
	{
		List<Insertion> insertions = new LinkedList<Insertion>();			
		int index1 = 0;
		int index2 = 0;
		
		for (Element itsSpan : itsSpans) {
			Insertion ins = getInsertion(index1, firstStr, index2, secondStr, itsSpan);
			index1 = ins.end;
			index2 = itsSpan.getEndTag().getEnd();
			insertions.add(ins);			
		}

		return insertions;
	}	
	
	/**
	 * Get one insertions that includes start, end, and InlineAnnotation
	 * @param firstPos
	 * @param firstStr
	 * @param secondPos
	 * @param secondStr
	 * @param span
	 * @return
	 */
	Insertion getInsertion (int firstPos,
		String firstStr,
		int secondPos,
		String secondStr,
		Element span)
	{
		
		Insertion insertion = new Insertion();
		
		insertion.begin = getInsertionPosition(firstPos, firstStr, secondPos, secondStr, span.getStartTag().getBegin());
		insertion.end = getInsertionPosition(insertion.begin, firstStr, span.getStartTag().getEnd(), secondStr, span.getEndTag().getBegin());
		insertion.genAnn = createAnnotation(span);

		return insertion;
	}
	
	/**
	 * Create GenericAnnotations from itsSpan
	 * @param itsSpan
	 * @return
	 */
	GenericAnnotations createAnnotation (Element itsSpan) {
		
		GenericAnnotations gas = new GenericAnnotations();
		
		GenericAnnotation ga = gas.add(GenericAnnotationType.TA);

		Attributes attributes = itsSpan.getAttributes();
		for ( Attribute attr : attributes ) {
			if ( attr.getKey().equals("its-ta-class-ref") ) {
				ga.setString(GenericAnnotationType.TA_CLASS, REF_PREFIX+attr.getValue());
			}
			else if ( attr.getKey().equals("its-ta-source") ) {
				ga.setString(GenericAnnotationType.TA_SOURCE, attr.getValue());
			}
			
			else if ( attr.getKey().equals("its-ta-ident") ) {
				ga.setString(GenericAnnotationType.TA_IDENT, attr.getValue());
			}
			else if ( attr.getKey().equals("its-ta-ident-ref") ) {
				ga.setString(GenericAnnotationType.TA_IDENT, REF_PREFIX+attr.getValue());
			}
			else if ( attr.getKey().equals("its-ta-confidence") ) {
				ga.setDouble(GenericAnnotationType.TA_CONFIDENCE, Double.parseDouble(attr.getValue()));
			}
		}
		return gas;
	}
	
	/**
	 * Locates Insertion position 
	 * @param firstPos
	 * @param firstStr
	 * @param secondPos
	 * @param secondStr
	 * @param secondMaxPos
	 * @return
	 */
	int getInsertionPosition (int firstPos,
		String firstStr,
		int secondPos,
		String secondStr,
		int secondMaxPos )
	{
		while( secondPos < secondMaxPos){
			//System.out.println(secondPos + ": " + firstStr.charAt(firstPos) + "=" + secondStr.charAt(secondPos));
			if( firstStr.charAt(firstPos) == secondStr.charAt(secondPos)){
				firstPos++;
				secondPos++;
			}else{
				//TODO: review
				break;
			}			
		}		
		
		if (secondPos == secondMaxPos){
			//System.out.println("Successful synch");
			return firstPos;
		}else{
			//TODO: improve
			return -1;
		}
	}
	
	/**
	 * Apply the list of Insertions to a TextFragment
	 * @param tf
	 * @param insertions
	 */
	void annotateFragment (TextFragment tf,
		List<Insertion> insertions)
	{
		int offset = 0;
		//System.out.println("Before Annotation: "+tf);
		for (Insertion insertion : insertions) {
			tf.annotate(insertion.begin + offset, insertion.end + offset, GenericAnnotationType.GENERIC, insertion.genAnn);
			offset += 4;
		}
		//System.out.println("After Annotation: "+tf);
	}
	
	/**
	 * Return the elements containing its-ta- attributes
	 * @param doc
	 * @return
	 */
	List<Element> getItsElements (Source doc) {
		List<Element> itsSpans = new LinkedList<Element>();
		for ( Element span : doc.getAllElements("span") ) {
			//--check if any of the attributes is its-ta-
			for ( Attribute a : span.getAttributes() ) {
				if ( a.getKey().startsWith("its-ta-" ) ) {
					itsSpans.add(span);
					break;
				}				
			}
		}
		return itsSpans;
	}
	
	/**
	 * Helper class representing an Inline Annotation to be added to the TextFragment
	 */
	class Insertion {
		public int begin;
		public int end;
		public GenericAnnotations genAnn;
	}

}
