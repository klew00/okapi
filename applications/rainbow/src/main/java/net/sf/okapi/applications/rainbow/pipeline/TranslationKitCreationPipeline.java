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

package net.sf.okapi.applications.rainbow.pipeline;

import net.sf.okapi.steps.common.RawDocumentToFilterEventsStep;
import net.sf.okapi.steps.leveraging.LeveragingStep;
import net.sf.okapi.steps.rainbowkit.creation.ExtractionStep;
import net.sf.okapi.steps.segmentation.SegmentationStep;

public class TranslationKitCreationPipeline extends PredefinedPipeline {

	public TranslationKitCreationPipeline () {
		super("TranslationKitCreationPipeline",
			"Translation Kit Creation");
		addStep(new RawDocumentToFilterEventsStep());
		
		SegmentationStep stepSeg = new SegmentationStep();
		((net.sf.okapi.steps.segmentation.Parameters)stepSeg.getParameters()).segmentSource = false;
		((net.sf.okapi.steps.segmentation.Parameters)stepSeg.getParameters()).segmentTarget = false;
		((net.sf.okapi.steps.segmentation.Parameters)stepSeg.getParameters()).copySource = false;
		addStep(stepSeg);

		LeveragingStep stepLev1 = new LeveragingStep();
		((net.sf.okapi.steps.leveraging.Parameters)stepLev1.getParameters()).setLeverage(false);
		addStep(stepLev1);
		
		LeveragingStep stepLev2 = new LeveragingStep();
		((net.sf.okapi.steps.leveraging.Parameters)stepLev2.getParameters()).setLeverage(false);
		((net.sf.okapi.steps.leveraging.Parameters)stepLev2.getParameters()).setFillIfTargetIsEmpty(true);
		addStep(stepLev2);
		
		addStep(new ExtractionStep());
		setInitialStepIndex(4);
	}
	
}
