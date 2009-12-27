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

package net.sf.okapi.steps.codesremoval;

import java.util.List;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.pipeline.BasePipelineStep;
import net.sf.okapi.common.pipeline.annotations.StepParameterMapping;
import net.sf.okapi.common.pipeline.annotations.StepParameterType;
import net.sf.okapi.common.resource.Segment;
import net.sf.okapi.common.resource.TextContainer;
import net.sf.okapi.common.resource.TextFragment;
import net.sf.okapi.common.resource.TextUnit;

public class CodesRemovalStep extends BasePipelineStep {

	private Parameters params;
	private LocaleId targetLocale;

	public CodesRemovalStep () {
		params = new Parameters();
	}
	
	@StepParameterMapping(parameterType = StepParameterType.TARGET_LOCALE)
	public void setTargetLocale (LocaleId targetLocale) {
		this.targetLocale = targetLocale;
	}
	
	public String getName () {
		return "Inline Codes Removal";
	}

	public String getDescription () {
		return "Removes inline codes from text units content of a document.";
	}

	@Override
	public IParameters getParameters () {
		return params;
	}

	@Override
	protected void handleTextUnit (Event event) {
		TextUnit tu = (TextUnit)event.getResource();
		// Skip non-translatable
		//TODO: make it an option
		if ( !tu.isTranslatable() ) return;

		// Process source if needed
		if ( params.getStripSource() ) {
			processContainer(tu.getSource());
		}
		
		// Process target if needed
		if ( params.getStripTarget() ) {
			if ( tu.hasTarget(targetLocale) ) {
				processContainer(tu.getTarget(targetLocale));
			}
		}
	}

	private void processContainer (TextContainer tc) {
		if ( tc.isSegmented() ) {
			List<Segment> segments = tc.getSegments();
			for ( Segment seg :segments ) {
				processFragment(seg.text);
			}
		}
		else {
			processFragment(tc.getContent());
		}
	}
	
	private void processFragment (TextFragment tf) {
		//TODO: direct cahnge of codes instead of calling tf.remove() each time
		String text = tf.getCodedText();
		int comp = 0;
		for ( int i=0; i<text.length(); i++) {
			switch ( text.charAt(i) ) {
			case TextFragment.MARKER_OPENING:
			case TextFragment.MARKER_CLOSING:
			case TextFragment.MARKER_ISOLATED:
			// Do not remove segment markers!
				tf.remove(i-comp, (i-comp)+2);
				i++;
				comp += 2; // Compensate for removed marker
				break;
			}
		}
	}

}
