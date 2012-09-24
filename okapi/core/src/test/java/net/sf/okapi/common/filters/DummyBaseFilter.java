/*===========================================================================
  Copyright (C) 2009 by the Okapi Framework contributors
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

package net.sf.okapi.common.filters;

import java.util.ArrayList;
import java.util.List;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.filters.AbstractFilter;
import net.sf.okapi.common.filters.EventBuilder;
import net.sf.okapi.common.filters.FilterConfiguration;
import net.sf.okapi.common.filters.PropertyTextUnitPlaceholder;
import net.sf.okapi.common.filters.PropertyTextUnitPlaceholder.PlaceholderAccessType;
import net.sf.okapi.common.resource.Code;
import net.sf.okapi.common.resource.RawDocument;
import net.sf.okapi.common.resource.TextFragment.TagType;
import net.sf.okapi.common.skeleton.GenericSkeleton;

public class DummyBaseFilter extends AbstractFilter {
	private EventBuilder eventBuilder;
	
	public DummyBaseFilter() {
		eventBuilder = new EventBuilder("rootId", this);
	}
	
	public void close() {
	}

	@Override
	public String getName() {
		return "DummyBaseFilter";
	}
	
	public String getDisplayName () {
		return "Dummy Base Filter";
	}

	public IParameters getParameters() {
		return null;
	}

	public void open (RawDocument input) {
		open(input, true);
	}
	
	public void open(RawDocument input,
		boolean generateSkeleton)
	{
		if ( input.getInputCharSequence().equals("2") ) {
			createCase2();
		}
		else {
			createCase1();
		}
	}

	public void setParameters (IParameters params) {
	}

	public void setFilterConfigurationMapper (IFilterConfigurationMapper fcMapper) {
	}

	private void createCase1 () {
		setMimeType("text/xml");
		
		eventBuilder.reset("rootId", this);
		eventBuilder.addFilterEvent(createStartFilterEvent());
		
		eventBuilder.startTextUnit("Text.");
		eventBuilder.endTextUnit();
		eventBuilder.startDocumentPart("<docPart/>");
		eventBuilder.addToDocumentPart("<secondPart/>");
		eventBuilder.endDocumentPart();
		
		eventBuilder.flushRemainingTempEvents();
		eventBuilder.addFilterEvent(createEndFilterEvent());
	}

	private void createCase2 () {
		setMimeType("text/xml");
		setNewlineType("\n");

		eventBuilder.reset("rootId", this);
		eventBuilder.addFilterEvent(createStartFilterEvent());

		ArrayList<PropertyTextUnitPlaceholder> list = new ArrayList<PropertyTextUnitPlaceholder>();
		list.add(new PropertyTextUnitPlaceholder(PlaceholderAccessType.WRITABLE_PROPERTY, "attr", "val1", 10, 14));
		
		//TODO: Skeleton should be GenericSkeleton since BaseFilter uses only that one
		eventBuilder.startTextUnit("Before ", new GenericSkeleton("<tu attr='val1'>"), list);
		eventBuilder.addToTextUnit(new Code(TagType.OPENING, "bold", "<b>"));
		eventBuilder.addToTextUnit("Text");
		eventBuilder.addToTextUnit(new Code(TagType.CLOSING, "bold", "</b>"));
		
		eventBuilder.flushRemainingTempEvents();
		eventBuilder.addFilterEvent(createEndFilterEvent());
	}

	public List<FilterConfiguration> getConfigurations() {
		return null;
	}

	@Override
	protected boolean isUtf8Bom() {
		return false;
	}

	@Override
	protected boolean isUtf8Encoding() {
		return false;
	}

	public boolean hasNext() {
		return eventBuilder.hasNext();
	}

	public Event next() {
		return eventBuilder.next();
	}	
}
