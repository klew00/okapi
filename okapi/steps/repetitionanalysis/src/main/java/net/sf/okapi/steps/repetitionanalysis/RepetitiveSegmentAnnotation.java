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

import net.sf.okapi.common.annotation.IAnnotation;

public class RepetitiveSegmentAnnotation implements IAnnotation {
	
	private String tuid;
	private String headTuid;
	private float score;
	
	public RepetitiveSegmentAnnotation(String tuid, String headTuid, float score) {
		super();
		this.tuid = tuid;
		this.headTuid = headTuid;
		this.score = score;
	}

	public String getTuid() {
		return tuid;
	}

	/**
	 * Gets the first repetitive segment matching the one the annotation is attached to (the head of the list of repetitive segments).
	 * @return tuid of the head segment
	 */
	public String getHeadTuid() {
		return headTuid;
	}

	public float getScore() {
		return score;
	}
}
