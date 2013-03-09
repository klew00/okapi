/*===========================================================================
  Copyright (C) 2010 by the Okapi Framework contributors
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

package net.sf.okapi.steps.xliffsplitter;

import net.sf.okapi.common.BaseParameters;
import net.sf.okapi.common.EditorFor;
import net.sf.okapi.common.ParametersDescription;
import net.sf.okapi.common.uidescription.EditorDescription;
import net.sf.okapi.common.uidescription.IEditorDescriptionProvider;
import net.sf.okapi.common.uidescription.TextInputPart;

@EditorFor(XliffJoinerParameters.class)
public class XliffJoinerParameters extends BaseParameters implements IEditorDescriptionProvider {

	private static final String INPUTFILEMARKER = "inputFileMarker";
	private static final String OUTPUTFILEMARKER = "outputFileMarker";

	private String inputFileMarker;
	private String outputFileMarker;

	public XliffJoinerParameters() {
		reset();
	}

	public void reset() {
		inputFileMarker = "_PART";
		outputFileMarker = "_CONCAT";
	}

	public void fromString(String data) {
		reset();
		buffer.fromString(data);
		
		inputFileMarker = buffer.getString(INPUTFILEMARKER, inputFileMarker);
		outputFileMarker = buffer.getString(OUTPUTFILEMARKER, outputFileMarker);
	}

	public String toString() {
		buffer.reset();
		buffer.setString(INPUTFILEMARKER, inputFileMarker);
		buffer.setString(OUTPUTFILEMARKER, outputFileMarker);
		return buffer.toString();
	}

	public String getInputFileMarker() {
		return inputFileMarker;
	}
	
	public void setInputFileMarker (String inputFileMarker) {
		this.inputFileMarker = inputFileMarker;
	}
	
	public String getOutputFileMarker() {
		return outputFileMarker;
	}
	
	public void setOutputFileMarker (String outputFileMarker) {
		this.outputFileMarker = outputFileMarker;
	}
	

	@Override
	public ParametersDescription getParametersDescription() {
		ParametersDescription desc = new ParametersDescription(this);
		desc.add(INPUTFILEMARKER, "Input file marker", null);
		desc.add(OUTPUTFILEMARKER, "Output file marker", null);
		return desc;
	}
	
	@Override
	public EditorDescription createEditorDescription (ParametersDescription paramsDesc) {
		EditorDescription desc = new EditorDescription("XLIFF Joiner", true, false);
		
		TextInputPart tip = desc.addTextInputPart(paramsDesc.get(INPUTFILEMARKER));
		tip.setVertical(false);
		tip = desc.addTextInputPart(paramsDesc.get(OUTPUTFILEMARKER));
		tip.setVertical(false);
		tip.setAllowEmpty(true);

		return desc;
	}

}