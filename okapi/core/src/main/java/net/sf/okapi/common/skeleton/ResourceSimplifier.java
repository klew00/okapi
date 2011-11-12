/*===========================================================================
  Copyright (C) 2008-2011 by the Okapi Framework contributors
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

package net.sf.okapi.common.skeleton;

import java.security.InvalidParameterException;
import java.util.List;
import java.util.logging.Logger;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.EventType;
import net.sf.okapi.common.IResource;
import net.sf.okapi.common.ISkeleton;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.resource.Code;
import net.sf.okapi.common.resource.DocumentPart;
import net.sf.okapi.common.resource.IReferenceable;
import net.sf.okapi.common.resource.ITextUnit;
import net.sf.okapi.common.resource.MultiEvent;
import net.sf.okapi.common.resource.StartDocument;
import net.sf.okapi.common.resource.TextContainer;
import net.sf.okapi.common.resource.TextFragment;
import net.sf.okapi.common.resource.TextPart;

/**
 * Simplifies events, i.e. splits the generic skeleton of a given event resource into parts to contain no references.
 * The skeleton parts are attached to newly created DOCUMENT_PART events.
 * Original references are converted either to skeleton parts, or TEXT_UNIT events.
 * The sequence of DOCUMENT_PART and TEXT_UNIT events is packed into a single MULTI_EVENT event.
 */
public class ResourceSimplifier {
	private final Logger logger = Logger.getLogger(getClass().getName());
	private boolean isMultilingual;
	private LocaleId trgLoc;
	private String outEncoding = "UTF-16BE";
	private GenericSkeletonWriter writer;
	private GenericSkeleton newSkel;
	private boolean useSDEncoding;
	private boolean useSDLocale;
	private boolean resolveCodeRefs;
	
	public ResourceSimplifier() {
		this((GenericSkeletonWriter) null);
	}
	
	public ResourceSimplifier(LocaleId trgLoc) {
		this(null, trgLoc);
	}
	
	public ResourceSimplifier(LocaleId trgLoc, String outEncoding) {
		this(null, trgLoc, outEncoding);
	}
	
	public ResourceSimplifier(GenericSkeletonWriter writer) {
		super();
		resolveCodeRefs = true;
		useSDEncoding = true;
		useSDLocale = true;
		
		this.writer = (writer == null) ? new GenericSkeletonWriter() : writer;
		newSkel = new GenericSkeleton();
		
		StartDocument sd = new StartDocument("");
		sd.setMultilingual(false); // Simple resources
		this.writer.processStartDocument(trgLoc, outEncoding, null, null, sd); // Sets writer fields + activates ref tracking mechanism of GSW
	}
	
	public ResourceSimplifier(GenericSkeletonWriter writer, LocaleId trgLoc) {
		this(writer);
		this.trgLoc = trgLoc;
		useSDLocale = false;
	}
	
	public ResourceSimplifier(GenericSkeletonWriter writer, LocaleId trgLoc, String outEncoding) {
		this(writer, trgLoc);		
		this.outEncoding = outEncoding;
		useSDEncoding = false;
	}

//	public void setMultilingual(boolean isMultilingual) {
//		this.isMultilingual = isMultilingual;
//	}
//	
//	public void setTargetLocale(LocaleId trgLoc) {
//		this.trgLoc = trgLoc;
//	}
//	
//	private void addEvent(MultiEvent me, Event event) {
//		// TODO merge adjacent DocumentParts 
//	}
//	
//	private void addDP(MultiEvent me, String id, GenericSkeleton skel) {
//		DocumentPart dp = new DocumentPart(id, false, skel); 
//		addEvent(me, new Event(EventType.DOCUMENT_PART, dp));
//	}
//	
	
	/**
	 * Merges adjacent document parts into one. Will work for simple resources only.
	 */	
	public static MultiEvent packMultiEvent(MultiEvent me) {
		Event prevEvent = null;
		MultiEvent newME = new MultiEvent();
		newME.setId(me.getId());
		
		for (Event event : me) {
			if (prevEvent != null && 
				event != null &&
				prevEvent.getEventType() == EventType.DOCUMENT_PART && 
				event.getEventType() == EventType.DOCUMENT_PART) {
				
				// Append to prev event's skeleton
				IResource res = event.getResource(); 
				ISkeleton skel = res.getSkeleton();
				if (skel instanceof GenericSkeleton) {
					IResource prevRes = prevEvent.getResource(); 
					ISkeleton prevSkel = prevRes.getSkeleton();
					if (prevSkel instanceof GenericSkeleton)
						((GenericSkeleton) prevSkel).add((GenericSkeleton) skel);
				}				
			}
			else {
				newME.addEvent(event);
				prevEvent = event;
			}			
		}
		return newME;
	}
	
	/**
	 * Converts a given event into a multi-event if it contains references in its source's codes or in skeleton, or passes it on if 
	 * either the skeleton is no instance of GenericSkeleton, contains no references, or the resource is referent.
	 * @param event the given event
	 * @return the given event or a newly created multi-event
	 */
	public Event convert(Event event) {
		if (event == null)
			throw new InvalidParameterException("Event cannot be null");
		
		IResource res = event.getResource();
		//if (res instanceof StartGroup)
//		if (res.getId().equals("tu4"))
//			System.out.println(res.getClass().getName());
		
		if (res instanceof IReferenceable) {			
			if  (((IReferenceable) res).isReferent()) {
				writer.addToReferents(event);
				// The referent is not processed at this point (only later from a resource referencing it)
				return event;
				//return Event.NOOP_EVENT;
			}
		}
		
		if (event.getEventType() == EventType.START_DOCUMENT) {
			StartDocument sd = (StartDocument) res;
			isMultilingual = sd.isMultilingual();
			if (useSDEncoding) this.outEncoding = sd.getEncoding(); // Default setting: output encoding = input encoding
			if (useSDLocale) this.trgLoc = sd.getLocale();
		}
				
		if (!isComplex(res)) {
			if (event.getEventType() == EventType.END_DOCUMENT)
				writer.close(); // Clears the referents cache
			return event;
		}
		
//		switch (event.getEventType()) {
//		case START_DOCUMENT:
//		case END_DOCUMENT:
//		case START_SUBDOCUMENT:
//		case END_SUBDOCUMENT:
//		case START_GROUP:
//		case END_GROUP:
//		case TEXT_UNIT:
//		case DOCUMENT_PART:
//			break;
//		default:
//			return event; // All other events with a skeleton are not processed
//		}
		
		// Process the resource's skeleton
		MultiEvent me = new MultiEvent();		
		processResource(res, me);
		
		// Different event types are processed differently
		switch (event.getEventType()) {
		case END_DOCUMENT:
			writer.close(); // Clears the referents cache
			// No break here
		case START_DOCUMENT:
		case START_SUBDOCUMENT:
		case END_SUBDOCUMENT:
		case START_GROUP:
		case END_GROUP:
			// Referents are sent to the GSW cache and are accessed there as refs are processed.
			// Here we deal with non-referents only.
			// The original event (the skeleton should be deleted) precedes in the resulting multi-event DPs/TUs 
			// created from its original skeleton parts
			res.setSkeleton(null);
			me.addEvent(event, 0);  
			break;
		case TEXT_UNIT:
		case DOCUMENT_PART:
			break;
		default:
			return event;
		}

		if (me.size() == 0) 
			return event;
		else if (me.size() == 1)
			return me.iterator().next();
		else
			return new Event(EventType.MULTI_EVENT, assignIDs(packMultiEvent(me), res));
	}
		
	private MultiEvent assignIDs(MultiEvent me, IResource resource) {
		int counter = 0;
		for (Event event : me) {
			IResource res = event.getResource();
			String resId = resource.getId();
			
			if (res instanceof DocumentPart && !(resource instanceof DocumentPart)) {
				String id = "";
				if (counter++ == 0) id = resId;
				else
					id = String.format("%s_%d", resId, counter++);
				
				res.setId("" + String.format("dp_%s", id));
			}
			else
				res.setId(resId);
			
//			if (res instanceof DocumentPart && resource instanceof DocumentPart)
//				res.setId(id);
//			else
//				res.setId("" + String.format("dp_%s", resId));
		}
		return me;
	}

	private boolean isComplex(IResource res) {
		if (res == null)
			return false;
		
		if (res instanceof ITextUnit) {
			ITextUnit tu = (ITextUnit)res;
			TextFragment tf = tu.getSource().getUnSegmentedContentCopy();
			for (Code code : tf.getCodes()) {
				if (code.hasReference()) return true;
			}
		}
		
		ISkeleton skel = res.getSkeleton();
		if (skel == null) {
			return false;
		}		
		if (!(skel instanceof GenericSkeleton)) {
			return false;
		}
		
		List<GenericSkeletonPart> parts = ((GenericSkeleton) skel).getParts();
		for (GenericSkeletonPart part : parts)
			if (!SkeletonUtil.isText(part)) 
				return true;
		
		return false;
	}

//	public Event toMultiEvent(Event event, LocaleId targetLocale) {
//		if (event == null)
//			throw new InvalidParameterException("Event cannot be null");
//		
//		IResource res = event.getResource();
//		if (res == null)
//			return wrapEvent(event);
//		
//		ISkeleton skel = res.getSkeleton();
//		if (!(skel instanceof GenericSkeleton)) {
//			// TODO log
//			return wrapEvent(event);
//		}
//		
//		MultiEvent me = new MultiEvent(); 
//		List<GenericSkeletonPart> parts = ((GenericSkeleton) skel).getParts();
//		
//		switch (event.getEventType()) {
//		case TEXT_UNIT:
//		}
//	
//		
//		
//		return wrapEvent(event); // TODO replace with real stuff		
//	}
//
//	public static Event fromMultiEvent(Event event) {
//		if (event == null)
//			throw new InvalidParameterException("Event cannot be null");
//		if (event.getEventType() != EventType.MULTI_EVENT)
//			throw new InvalidParameterException("MULTI_EVENT type is expected");
//		MultiEvent me = (MultiEvent) event.getResource();
//		//if (me.iterator().)
//		
//		return null;		
//	}
//	
//	private Event wrapEvent(Event event) {
//		MultiEvent me = new MultiEvent();
//		me.addEvent(event);
//		return new Event(EventType.MULTI_EVENT, me);
//	}
	
	private void flushSkeleton(String resId, int dpIndex, MultiEvent me) {
		if (newSkel.isEmpty()) return;
			
		//me.addEvent(new Event(EventType.DOCUMENT_PART, new DocumentPart(String.format("%s_%d", resId, dpIndex), false, newSkel)));
		me.addEvent(new Event(EventType.DOCUMENT_PART, new DocumentPart("", false, newSkel))); // IDs are set in packMultiEvent()
		newSkel = new GenericSkeleton(); // newSkel.clear() would damage an already sent skeleton
	}
	
	private void addTU(MultiEvent me, String resId, int tuIndex, ITextUnit tu) {
		String id = null;		
		if (tuIndex == 1)
			id = resId;
		else {
			logger.warning("Duplicate TU: " + resId);
			id = String.format("%s_%d", resId, tuIndex);
		}
		
		ITextUnit newTU = tu.clone();
		newTU.setId(id);
		newTU.setSkeleton(null);
		newTU.setIsReferent(false); //!!! to have GSW write it out
		
		me.addEvent(new Event(EventType.TEXT_UNIT, newTU));
	}
	
	/**
	 * Creates events from references of a given resource, adds created events to a given multi-event resource.
	 */
	private void processResource(IResource resource, MultiEvent me) {
		if (resource == null)
			throw new InvalidParameterException("Resource parameter cannot be null");
		if (me == null)
			throw new InvalidParameterException("MultiEvent parameter cannot be null");
		
		int dpCounter = 0;
		int tuCounter = 0;
		String resId = resource.getId();
		ISkeleton skel = resource.getSkeleton();		
		boolean hasGenericSkeleton = skel instanceof GenericSkeleton; 
		
		if (resource instanceof ITextUnit) {
			ITextUnit tu = (ITextUnit)resource;
			if (tu.isReferent()) {
				// Referenced TU, we got here from recursion (see *** below)
				if (!hasGenericSkeleton) {
					newSkel.add(writer.getString(tu, trgLoc, 1));
					return;
				}
				// Otherwise the skeleton is analyzed below
			}
			else {
				// Regular TU
				TextContainer tc = tu.getSource();
				
				for (TextPart part : tc) {
					TextFragment tf = part.getContent();
					for (Code code : tf.getCodes()) {
						if (code.hasReference()) {
							// Resolve reference(s) with GSW, replace the original data
							if (resolveCodeRefs)
								code.setData(writer.expandCodeContent(code, trgLoc, 0));
						}
					}
				}
//				for (Iterator<TextPart> iter = tc.iterator(); iter.hasNext();) {
//					TextPart part = iter.next();
//					TextFragment tf = part.getContent();
//					for (Code code : tf.getCodes()) {
//						if (code.hasReference()) {
//							// Resolve reference(s) with GSW, replace the original data
//							if (resolveCodeRefs)
//								code.setData(writer.expandCodeContent(code, trgLoc, 0));
//						}
//					}
//				}			
				if (!hasGenericSkeleton)
					addTU(me, resId, ++tuCounter, tu);
			}
		}
		
		if (!hasGenericSkeleton) return;
		
		List<GenericSkeletonPart> parts = ((GenericSkeleton) skel).getParts();				
//		if (resource instanceof INameable)
//			mimeType = ((INameable) resource).getMimeType();
		
		for (GenericSkeletonPart part : parts) {
			if (SkeletonUtil.isText(part)) {
				//newSkel.add(part.toString());
				newSkel.add(writer.getString(part, 1));
			}				
			else if (SkeletonUtil.isReference(part)) {
				flushSkeleton(resId, ++dpCounter, me);				
				
				IReferenceable referent = writer.getReference(SkeletonUtil.getRefId(part));
				if (referent instanceof IResource)
					processResource((IResource) referent, me); // ***
			}
			else if (SkeletonUtil.isSourcePlaceholder(part, resource)) {
				processSourcePlaceholder(part, resource, me, resId, tuCounter, dpCounter);
			}
			else if (SkeletonUtil.isTargetPlaceholder(part, resource)) {
				processTargetPlaceholder(part, resource, me, resId, tuCounter, dpCounter);
			}
			else if (SkeletonUtil.isValuePlaceholder(part, resource)) {
				// For both isMultilingual true/false
				newSkel.add(writer.getString(part, 1));
			}
			else if (SkeletonUtil.isExtSourcePlaceholder(part, resource)) {
				checkExtParent(part.getParent(), resId);
				processSourcePlaceholder(part, resource, me, resId, tuCounter, dpCounter);
			}
			else if (SkeletonUtil.isExtTargetPlaceholder(part, resource)) {
				checkExtParent(part.getParent(), resId);
				processTargetPlaceholder(part, resource, me, resId, tuCounter, dpCounter);
			}
			else if (SkeletonUtil.isExtValuePlaceholder(part, resource)) {
				// For both isMultilingual true/false
				checkExtParent(part.getParent(), resId);
				newSkel.add(writer.getString(part, 1));
			}
		}
		flushSkeleton(resId, ++dpCounter, me); // Flush remaining skeleton tail
	}

	private void processSourcePlaceholder(GenericSkeletonPart part, IResource resource, 
			MultiEvent me, String resId, int tuCounter, int dpCounter) {
		if (isMultilingual) {
			if (part.parent instanceof ITextUnit)
				newSkel.add(writer.getContent((ITextUnit)part.parent, null, 0)); // Source goes to skeleton
			else {
				logger.warning("The self-reference must be a text-unit: " + resId);
				newSkel.add(part.parent.toString());
			}
		}
		else {
			flushSkeleton(resId, ++dpCounter, me);
			addTU(me, resId, ++tuCounter, (ITextUnit)resource);
		}
	}	
	
	private void processTargetPlaceholder(GenericSkeletonPart part, IResource resource, 
			MultiEvent me, String resId, int tuCounter, int dpCounter) {
		// For both isMultilingual true/false
		if (part.getLocale() == trgLoc) {
			flushSkeleton(resId, ++dpCounter, me);
			addTU(me, resId, ++tuCounter, (ITextUnit)resource);
		}
		else {
			//newSkel.add(writer.getContent((TextUnit) resource, trgLoc, 1));
			newSkel.add(writer.getContent((ITextUnit)resource, part.getLocale(), 1));			
		}
	}
	
	private boolean checkExtParent(IResource parent, String resId) {
		if (parent instanceof IReferenceable) {
			IReferenceable r = (IReferenceable) parent;
			if (!r.isReferent()) {
				logger.warning("Referent flag is not set in parent: " + resId);
				return false;
			}
			return true;
		}
		else {
			logger.warning("Invalid parent type: " + resId);
			return false;
		}
	}

	public void setResolveCodeRefs(boolean resolve) {
		this.resolveCodeRefs = resolve;
	}

	public boolean getResolveCodeRefs() {
		return resolveCodeRefs;
	}
}
