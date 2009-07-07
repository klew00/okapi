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

package net.sf.okapi.steps.common.stepannotations.tests;

import net.sf.okapi.steps.common.stepannotations.ExternalParameter;
import net.sf.okapi.common.Event;
import net.sf.okapi.common.pipeline.BasePipelineStep;

public class XSLTransformStep extends BasePipelineStep {
	@SuppressWarnings("unused")
	@ExternalParameter(description = "Path to the xslt file used by this step", 
			longDescription = "The xslt path is provided by the external client")
	private String xsltPath;

	public XSLTransformStep() {
		xsltPath = "";
	}

	@Override
	public void destroy() {
	}

	public String getDescription() {
		return "Apply an XSLT template to an XML document.";
	}

	public String getName() {
		return "XSLT Transformation";
	}

	@Override
	public boolean isDone() {
		return false;
	}

	@Override
	public boolean needsOutput(int inputIndex) {
		return false;
	}

	@Override
	protected void handleStartBatch(Event event) {
	}

	@Override
	protected void handleStartBatchItem(Event event) {
	}

	@Override
	protected void handleRawDocument(Event event) {
	}
}
