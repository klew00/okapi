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

package net.sf.okapi.steps.repetitionanalysis;

import net.sf.okapi.common.BaseParameters;
import net.sf.okapi.common.EditorFor;
import net.sf.okapi.common.ParametersDescription;
import net.sf.okapi.common.uidescription.EditorDescription;
import net.sf.okapi.common.uidescription.IEditorDescriptionProvider;
import net.sf.okapi.common.uidescription.SpinInputPart;

@EditorFor(Parameters.class)
public class Parameters extends BaseParameters implements IEditorDescriptionProvider {

	private static final String FUZZYTHRESHOLD = "fuzzyThreshold";
	private static final String MAXHITS = "maxHits";
	
	private int fuzzyThreshold;
	private int maxHits;
	
	public Parameters () {
		reset();
	}
	
	public void reset() {
		fuzzyThreshold = 100;
		maxHits = 20;
	}
	
	@Override
	public void fromString(String data) {
		reset();
		buffer.fromString(data);
		fuzzyThreshold = buffer.getInteger(FUZZYTHRESHOLD, fuzzyThreshold);
		maxHits = buffer.getInteger(MAXHITS, maxHits);
	}
	
	@Override
	public String toString() {
		buffer.reset();
		buffer.setInteger(FUZZYTHRESHOLD, fuzzyThreshold);
		buffer.setInteger(MAXHITS, maxHits);
		return buffer.toString();
	}
	
	public void setFuzzyThreshold(int fuzzyThreshold) {
		this.fuzzyThreshold = fuzzyThreshold;
	}

	public int getFuzzyThreshold() {
		return fuzzyThreshold;
	}

	@Override
	public ParametersDescription getParametersDescription () {
		ParametersDescription desc = new ParametersDescription(this);
		desc.add(FUZZYTHRESHOLD, "Fuzzy threshold (1-100)", "Fuzzy threshold for fuzzy repetitions. Leave 100 for exact repetitions only.");
		desc.add(MAXHITS, "Max hits", "Maximum number of exact and fuzzy repetitions to keep track of for every segment.");
		return desc;
	}
	
	@Override
	public EditorDescription createEditorDescription(ParametersDescription paramDesc) {
		EditorDescription desc = new EditorDescription("Repetition Analysis");		
		SpinInputPart sip = desc.addSpinInputPart(paramDesc.get(FUZZYTHRESHOLD));
		SpinInputPart sip2 = desc.addSpinInputPart(paramDesc.get(MAXHITS));
		sip.setRange(1, 100);		
		sip2.setRange(1, 100);
		return desc;
	}

	public int getMaxHits() {
		return maxHits;
	}

	public void setMaxHits(int maxHits) {
		this.maxHits = maxHits;
	}

}
