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

package net.sf.okapi.steps.msbatchtranslation;

import net.sf.okapi.common.BaseParameters;
import net.sf.okapi.common.EditorFor;
import net.sf.okapi.common.ParametersDescription;
import net.sf.okapi.common.uidescription.EditorDescription;
import net.sf.okapi.common.uidescription.IEditorDescriptionProvider;
import net.sf.okapi.common.uidescription.SeparatorPart;
import net.sf.okapi.common.uidescription.SpinInputPart;
import net.sf.okapi.common.uidescription.TextInputPart;
import net.sf.okapi.common.uidescription.TextLabelPart;

@EditorFor(SubmissionParameters.class)
public class SubmissionParameters extends BaseParameters implements IEditorDescriptionProvider {

	private static final String APPID = "appId";
	private static final String BATCHSIZE = "batchSize";
	private static final String RATING = "rating";
	
	private String appId;
	private int batchSize;
	private int rating;
	
	public SubmissionParameters () {
		reset();
		toString();
	}
	
	public SubmissionParameters (String initialData) {
		fromString(initialData);
	}

	@Override
	public void fromString (String data) {
		reset();
		buffer.fromString(data);
		appId = buffer.getEncodedString(APPID, appId);
		batchSize = buffer.getInteger(BATCHSIZE, batchSize);
		rating = buffer.getInteger(RATING, rating);
	}

	@Override
	public void reset () {
		// Default
		appId = "";
		batchSize = 80;
		rating = 4;
	}

	@Override
	public String toString () {
		buffer.reset();
		buffer.setEncodedString(APPID, appId);
		buffer.setInteger(BATCHSIZE, batchSize);
		buffer.setInteger(RATING, rating);
		return buffer.toString();
	}

	public int getBatchSize () {
		return batchSize;
	}
	
	public void setBatchSize (int batchSize) {
		this.batchSize = batchSize;
	}
	
	public int getRating () {
		return rating;
	}
	
	public void setRating (int rating) {
		this.rating = rating;
	}
	public String getAppId () {
		return appId;
	}

	public void setAppId (String appId) {
		this.appId = appId;
	}

	@Override
	public ParametersDescription getParametersDescription () {
		ParametersDescription desc = new ParametersDescription(this);
		desc.add(APPID, "Application ID", "Application ID for the account to use");
		desc.add(BATCHSIZE, "Batch size", "Number of segments to send in each batch");
		desc.add(RATING, "Default rating", "Default rating to use for if one is not provided with the translated segment");
		return desc;
	}

	@Override
	public EditorDescription createEditorDescription (ParametersDescription paramsDesc) {
		EditorDescription desc = new EditorDescription("Microsoft Batch Submission Settings");

		TextLabelPart tlp = desc.addTextLabelPart("Powered by Microsoft\u00AE Translator"); // Required by TOS
		tlp.setVertical(true);
		SeparatorPart sp = desc.addSeparatorPart();
		sp.setVertical(true);

		TextInputPart tip = desc.addTextInputPart(paramsDesc.get(APPID));
		tip.setPassword(true);

		sp = desc.addSeparatorPart();
		sp.setVertical(true);
		
		SpinInputPart sip = desc.addSpinInputPart(paramsDesc.get(RATING));
		sip.setRange(-10, 10);
		
		sip = desc.addSpinInputPart(paramsDesc.get(BATCHSIZE));
		sip.setRange(1, 100);
		
		return desc;
	}

}
