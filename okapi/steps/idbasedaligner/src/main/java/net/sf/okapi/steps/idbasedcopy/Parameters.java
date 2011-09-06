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

package net.sf.okapi.steps.idbasedcopy;

import net.sf.okapi.common.BaseParameters;
import net.sf.okapi.common.EditorFor;
import net.sf.okapi.common.ParametersDescription;
import net.sf.okapi.common.uidescription.EditorDescription;
import net.sf.okapi.common.uidescription.IEditorDescriptionProvider;

@EditorFor(Parameters.class)
public class Parameters extends BaseParameters implements IEditorDescriptionProvider {

	private static final String MARKASTRANSLATENO = "markAsTranslateNo";
	private static final String MARKASAPPROVED = "markAsApproved";
	
	private boolean markAsTranslateNo;
	private boolean markAsApproved;
	
	public Parameters () {
		reset();
		toString();
	}
	
	public Parameters (String initialData) {
		fromString(initialData);
	}

	@Override
	public void fromString (String data) {
		reset();
		buffer.fromString(data);
		markAsTranslateNo = buffer.getBoolean(MARKASTRANSLATENO, markAsTranslateNo);
		markAsApproved = buffer.getBoolean(MARKASAPPROVED, markAsApproved);
	}

	@Override
	public void reset () {
		// Default
		markAsTranslateNo = false;
		markAsApproved = true;
	}

	@Override
	public String toString () {
		buffer.reset();
		buffer.setBoolean(MARKASTRANSLATENO, markAsTranslateNo);
		buffer.setBoolean(MARKASAPPROVED, markAsApproved);
		return buffer.toString();
	}

	public boolean getMarkAsTranslateNo () {
		return markAsTranslateNo;
	}

	public void setMarkAsTranslateNo (boolean markAsTranslateNo) {
		this.markAsTranslateNo = markAsTranslateNo;
	}

	public boolean getMarkAsApproved () {
		return markAsApproved;
	}

	public void setMarkAsApproved (boolean markAsApproved) {
		this.markAsApproved = markAsApproved;
	}

	@Override
	public ParametersDescription getParametersDescription () {
		ParametersDescription desc = new ParametersDescription(this);
		desc.add(MARKASTRANSLATENO, "Set the text unit as non-translatable", null);
		desc.add(MARKASAPPROVED, "Set the target property 'approved' to 'yes'", null);
		return desc;
	}

	@Override
	public EditorDescription createEditorDescription (ParametersDescription paramsDesc) {
		EditorDescription desc = new EditorDescription("Id-Based Copy Settings");
		desc.addTextLabelPart("If the text unit has a match:");
		desc.addCheckboxPart(paramsDesc.get(MARKASTRANSLATENO));
		desc.addCheckboxPart(paramsDesc.get(MARKASAPPROVED));
		return desc;
	}

}
