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

import net.sf.okapi.common.Event;
import net.sf.okapi.common.IResource;
import net.sf.okapi.common.pipeline.BasePipelineStep;
import net.sf.okapi.common.resource.TextFragment;
import net.sf.okapi.common.resource.TextUnit;

public class UppercaseStep extends BasePipelineStep {

	private String trgLang;

	public UppercaseStep(String targetLang) {
		trgLang = targetLang;
	}

	public String getName() {
		return "UppercaseStep";
	}

	public void handleTextUnit(Event event) {
		TextUnit tu = (TextUnit) event.getResource();
		if (tu.isTranslatable()) {
			TextFragment tf = tu.createTarget(trgLang, false,
					IResource.COPY_CONTENT);
			tf.setCodedText(tf.getCodedText().toUpperCase());
		}
	}
}
