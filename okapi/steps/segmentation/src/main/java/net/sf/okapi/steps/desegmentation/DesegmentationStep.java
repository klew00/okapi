/*===========================================================================
  Copyright (C) 2010-2011 by the Okapi Framework contributors
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

package net.sf.okapi.steps.desegmentation;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.UsingParameters;
import net.sf.okapi.common.pipeline.BasePipelineStep;
import net.sf.okapi.common.pipeline.annotations.StepParameterMapping;
import net.sf.okapi.common.pipeline.annotations.StepParameterType;
import net.sf.okapi.common.resource.ITextUnit;
import net.sf.okapi.common.resource.TextContainer;

@UsingParameters(Parameters.class)
public class DesegmentationStep extends BasePipelineStep {

	private Parameters params;
	private LocaleId targetLocale;

	public DesegmentationStep () {
		params = new Parameters();
	}
	
	@StepParameterMapping(parameterType = StepParameterType.TARGET_LOCALE)
	public void setTargetLocale (LocaleId targetLocale) {
		this.targetLocale = targetLocale;
	}
	
	@Override
	public String getName () {
		return "Desegmentation";
	}

	@Override
	public String getDescription () {
		return "Joins all segments into a single content. "
			+ "Expects: filter events. Sends back: filter events.";
	}

	@Override
	public IParameters getParameters () {
		return params;
	}

	@Override
	public void setParameters (IParameters params) {
		this.params = (Parameters)params;
	}

	@Override
	protected Event handleTextUnit (Event event) {
		ITextUnit tu = event.getTextUnit();

		// Skip non-translatable
		if ( !tu.isTranslatable() ) return event;
		
		// Desegment source if needed
		if ( params.getDesegmentSource() && tu.getSource().hasBeenSegmented() ) {
			tu.getSource().getSegments().joinAll();
		}
		
		// Desegment target if needed
		if ( params.getDesegmentTarget() ) {
			TextContainer cont = tu.getTarget(targetLocale);
			if ( cont != null ) {
				if ( cont.hasBeenSegmented() ) {
					cont.getSegments().joinAll();
				}
			}
		}
		
		return event;
	}

}
