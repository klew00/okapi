/*===========================================================================
  Copyright (C) 2008-2012 by the Okapi Framework contributors
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

package net.sf.okapi.common.resource;

import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.encoder.IEncoder;
import net.sf.okapi.common.filters.SubFilterSkeletonWriter;
import net.sf.okapi.common.filterwriter.IFilterWriter;
import net.sf.okapi.common.skeleton.ISkeletonWriter;


public class StartSubfilter extends StartGroup {
	
	private StartDocument startDoc;
	//private SubFilter subFilter;
	private SubFilterSkeletonWriter skelWriter;
	private IEncoder parentEncoder;

//	/**
//	 * Creates a new {@link StartSubfilter} object.
//	 * @param parentId The identifier of the parent resource for this sub filter.
//	 * @param startDoc The StartDocument resource of the subfilter.
//	 */
//	public StartSubfilter(SubFilter subFilter, StartDocument startDoc) {
//		super(startDoc.getName(), null, false); // Not referenced by default
//		this.startDoc = startDoc;
//		this.subFilter = subFilter;
//	}

	/**
	 * Creates a new {@link StartSubfilter} object with the identifier of the group's parent
	 * and the group's identifier.
	 * @param id the identifier of this sub filter.
	 * @param startDoc The StartDocument resource of the subfilter.
	 * @param parentEncoder2 
	 */
	public StartSubfilter(String id, StartDocument startDoc, IEncoder parentEncoder) {
		super(startDoc.getName(), null, false); // Not referenced by default
		this.startDoc = startDoc;
		this.parentEncoder = parentEncoder;
		setId(id);		
	}
	
	public LocaleId getLocale() {
		return startDoc.getLocale();
	}
	
	public String getEncoding() {
		return startDoc.getEncoding();
	}
	
	public boolean isMultilingual() {
		return startDoc.isMultilingual();
	}
	
	public IParameters getFilterParameters() {
		return startDoc.getFilterParameters();
	}
	
	public IFilterWriter getFilterWriter() {
		return startDoc.getFilterWriter();
	}
	
	public boolean hasUTF8BOM() {
		return startDoc.hasUTF8BOM();
	}
	
	public String getLineBreak() {
		return startDoc.getLineBreak();
	}

	public StartDocument getStartDoc() {
		return startDoc;
	}

	public SubFilterSkeletonWriter getSkeletonWriter() {
		return skelWriter;
	}

	public ISkeletonWriter createSkeletonWriter(StartSubfilter resource,
			LocaleId outputLocale, String outputEncoding) {
		this.skelWriter = new SubFilterSkeletonWriter(this);
		return this.skelWriter.setOptions(outputLocale, outputEncoding, this);
	}

	public IEncoder getParentEncoder() {
		return parentEncoder;
	}
	
}
