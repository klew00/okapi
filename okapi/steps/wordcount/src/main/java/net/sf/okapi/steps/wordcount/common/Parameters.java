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

package net.sf.okapi.steps.wordcount.common;

import net.sf.okapi.common.EditorFor;
import net.sf.okapi.common.ParametersDescription;
import net.sf.okapi.common.ParametersString;
import net.sf.okapi.common.uidescription.EditorDescription;
import net.sf.okapi.common.uidescription.IEditorDescriptionProvider;
import net.sf.okapi.lib.extra.AbstractParameters;

@EditorFor(Parameters.class)
public class Parameters extends AbstractParameters implements IEditorDescriptionProvider {

	private static final String COUNTINTEXTUNITS = "countInTextUnits";
	private static final String COUNTINBATCH = "countInBatch";
	private static final String COUNTINBATCHITEMS = "countInBatchItems";
	private static final String COUNTINDOCUMENTS = "countInDocuments";
	private static final String COUNTINSUBDOCUMENTS = "countInSubDocuments";
	private static final String COUNTINGROUPS = "countInGroups";
	private static final String BUFFERSIZE = "bufferSize";
	
	private boolean countInBatch;
	private boolean countInBatchItems;
	private boolean countInDocuments;
	private boolean countInSubDocuments;
	private boolean countInGroups;
	private int bufferSize;
	
	public boolean getCountInBatch () {
		return countInBatch;
	}
	
	public void setCountInBatch (boolean countInBatch) {
		this.countInBatch = countInBatch;
	}
	
	public boolean getCountInBatchItems () {
		return countInBatchItems;
	}
	
	public void setCountInBatchItems (boolean countInBatchItems) {
		this.countInBatchItems = countInBatchItems;
	}
	
	public boolean getCountInDocuments () {
		return countInDocuments;
	}
	
	public void setCountInDocuments (boolean countInDocuments) {
		this.countInDocuments = countInDocuments;
	}
	
	public boolean getCountInSubDocuments () {
		return countInSubDocuments;
	}
	
	public void setCountInSubDocuments (boolean countInSubDocuments) {
		this.countInSubDocuments = countInSubDocuments;
	}
	
	public boolean getCountInGroups () {
		return countInGroups;
	}
	
	public void setCountInGroups (boolean countInGroups) {
		this.countInGroups = countInGroups;
	}
	
	public boolean getCountInTextUnits () {
		return true; // Always
	}

	public void setBufferSize(int bufferSize) {
		this.bufferSize = bufferSize;
	}

	public int getBufferSize() {
		return bufferSize;
	}
	
	@Override
	protected void parameters_init() {
	}

	@Override
	protected void parameters_load (ParametersString buffer) {
		countInBatch = buffer.getBoolean(COUNTINBATCH, countInBatch);
		countInBatchItems = buffer.getBoolean(COUNTINBATCHITEMS, countInBatchItems);
		countInDocuments = buffer.getBoolean(COUNTINDOCUMENTS, countInDocuments);
		countInSubDocuments = buffer.getBoolean(COUNTINSUBDOCUMENTS, countInSubDocuments);
		countInGroups = buffer.getBoolean(COUNTINGROUPS, countInGroups);
		bufferSize = buffer.getInteger(BUFFERSIZE, bufferSize);
	}

	@Override
	protected void parameters_reset () {
		countInBatch = true; // Defaults for the scoping report step
		countInBatchItems = true; // Defaults for the scoping report step
		countInDocuments = false;
		countInSubDocuments = false;
		countInGroups = false;
		bufferSize = 0;
	}

	@Override
	protected void parameters_save (ParametersString buffer) {
		buffer.setBoolean(COUNTINBATCH, countInBatch);
		buffer.setBoolean(COUNTINBATCHITEMS, countInBatchItems);
		buffer.setBoolean(COUNTINDOCUMENTS, countInDocuments);
		buffer.setBoolean(COUNTINSUBDOCUMENTS, countInSubDocuments);
		buffer.setBoolean(COUNTINGROUPS, countInGroups);
		buffer.setInteger(BUFFERSIZE, bufferSize);
	}
	
	@Override
	public ParametersDescription getParametersDescription () {
		ParametersDescription desc = new ParametersDescription(this);
		desc.add(COUNTINTEXTUNITS, "Text units", null);
		desc.add(COUNTINBATCH, "Batches", null);
		desc.add(COUNTINBATCHITEMS, "Batch items", null);
		desc.add(COUNTINDOCUMENTS, "Documents", null);
		desc.add(COUNTINSUBDOCUMENTS, "Sub-documents", null);
		desc.add(COUNTINGROUPS, "Groups", null);
		desc.add(BUFFERSIZE, "Size of text buffer:", null);
		return desc;
	}	

	@Override
	public EditorDescription createEditorDescription (ParametersDescription paramsDesc) {
		EditorDescription desc = new EditorDescription("Word Count", true, false);
		desc.addTextLabelPart("Create a word count annotation for each of the following resources:");
		desc.addCheckboxPart(paramsDesc.get(COUNTINTEXTUNITS));
		desc.addCheckboxPart(paramsDesc.get(COUNTINBATCH));
		desc.addCheckboxPart(paramsDesc.get(COUNTINBATCHITEMS));
		desc.addCheckboxPart(paramsDesc.get(COUNTINDOCUMENTS));
		desc.addCheckboxPart(paramsDesc.get(COUNTINSUBDOCUMENTS));
		desc.addCheckboxPart(paramsDesc.get(COUNTINGROUPS));
		desc.addSpinInputPart(paramsDesc.get(BUFFERSIZE));
		return desc;
	}

}
