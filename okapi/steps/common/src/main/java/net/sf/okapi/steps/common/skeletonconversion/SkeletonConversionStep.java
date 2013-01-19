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

package net.sf.okapi.steps.common.skeletonconversion;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.EventType;
import net.sf.okapi.common.IResource;
import net.sf.okapi.common.encoder.EncoderManager;
import net.sf.okapi.common.resource.DocumentPart;
import net.sf.okapi.common.resource.IReferenceable;
import net.sf.okapi.common.resource.ITextUnit;
import net.sf.okapi.common.resource.MultiEvent;
import net.sf.okapi.common.resource.StartDocument;
import net.sf.okapi.common.resource.TextUnit;
import net.sf.okapi.common.skeleton.GenericSkeleton;
import net.sf.okapi.common.skeleton.GenericSkeletonWriter;
import net.sf.okapi.common.skeleton.ISkeletonWriter;
import net.sf.okapi.common.skeleton.ResourceSimplifier;
import net.sf.okapi.steps.common.tufiltering.ITextUnitFilter;
import net.sf.okapi.steps.common.tufiltering.TuFilteringStep;

public class SkeletonConversionStep extends TuFilteringStep {

	private ResourceSimplifier simplifier;
	private ISkeletonWriter writer;
	private EncoderManager em;
	
	public SkeletonConversionStep() {
		super();
	}
	
	public SkeletonConversionStep(ITextUnitFilter tuFilter) {
		super(tuFilter);
	}
	
	@Override
	protected Event handleStartDocument(Event event) {
		StartDocument sd = event.getStartDocument();
		
		writer = sd.getFilterWriter().getSkeletonWriter();
		if (writer instanceof GenericSkeletonWriter) {
			simplifier = new ResourceSimplifier((GenericSkeletonWriter) writer, sd.getEncoding(), sd.getLocale());
		}
		em = sd.getFilterWriter().getEncoderManager();
		writer.processStartDocument(sd.getLocale(), sd.getEncoding(), null, em, sd);
		return super.handleStartDocument(event);
	}
	
	private void convertTu(ITextUnit tu, MultiEvent me) {
		// After the resource simplifier tu skeleton can contain only a content placeholder and no refs in codes
		String str = writer.processTextUnit(tu);		
		ITextUnit newTu = new TextUnit(tu.getId());
		newTu.setIsTranslatable(false);
		
		me.addEvent(new Event(EventType.TEXT_UNIT, newTu));
		me.addEvent(new Event(EventType.DOCUMENT_PART, new DocumentPart(String.format("dp_%s_conv", tu.getId()), false, new GenericSkeleton(str))));
	}
	
	@Override
	public Event handleEvent(Event event) {
		IResource res = event.getResource();
		if (res instanceof IReferenceable && ((IReferenceable) res).isReferent()) {
			simplifier.convert(event); // To store a reference
		}
		return super.handleEvent(event);
	}
	
	@Override
	protected Event processFiltered(Event tuEvent) {
		ITextUnit tu = tuEvent.getTextUnit();
		if (tu.isReferent()) return tuEvent;
		
		Event e = simplifier.convert(tuEvent);
		MultiEvent newMe = new MultiEvent();
		if (e.isTextUnit()) {
			convertTu(tu, newMe);
		}
		else if (e.isMultiEvent()) {			
			MultiEvent me = e.getMultiEvent();			
			for (Event event : me) {
				if (event.isTextUnit()) {
					tu = event.getTextUnit();
					convertTu(tu, newMe);					
				}
				else {
					newMe.addEvent(event);
				}					
			}
		}
		return new Event(EventType.MULTI_EVENT, newMe);
	}
}
