/*===========================================================================
  Copyright (C) 2009-2011 by the Okapi Framework contributors
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

import net.sf.okapi.common.Event;
import net.sf.okapi.common.IResource;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.pipeline.BasePipelineStep;
import net.sf.okapi.common.pipeline.annotations.StepParameterMapping;
import net.sf.okapi.common.pipeline.annotations.StepParameterType;
import net.sf.okapi.common.resource.Segment;
import net.sf.okapi.common.resource.TextContainer;
import net.sf.okapi.common.resource.TextFragment;
import net.sf.okapi.common.resource.ITextUnit;

public class UppercaseStep extends BasePipelineStep {

	private LocaleId trgLoc;
	
	@StepParameterMapping(parameterType = StepParameterType.TARGET_LOCALE)
	public void setTargetLocale (LocaleId targetLocale) {
		trgLoc = targetLocale;
	}
	
	public String getName () {
		return "Uppercase";
	}
	
	public String getDescription () {
		return "Converts text units content to upper cases.";
	}

	@Override
	protected Event handleTextUnit (Event event) {
		ITextUnit tu = event.getTextUnit();
		if ( tu.isTranslatable() ) {
			TextContainer tc = tu.createTarget(trgLoc, false, IResource.COPY_SEGMENTED_CONTENT);
			for ( Segment seg : tc.getSegments() ) {
				TextFragment tf = seg.getContent();
				tf.setCodedText(tf.getCodedText().toUpperCase());
			}
		}
		return event;
	}

}
