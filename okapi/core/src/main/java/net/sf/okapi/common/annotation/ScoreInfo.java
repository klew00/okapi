/*===========================================================================
  Copyright (C) 2009-2010 by the Okapi Framework contributors
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

package net.sf.okapi.common.annotation;

/**
 * Stores the information for a score used in a {@link ScoresAnnotation}.
 * @deprecated This class will be deleted soon. Use {@link AltTranslation} instead.
 */
public class ScoreInfo {

	/**
	 * Score of the match (0-100)
	 */
	public int score;
	
	/**
	 * Origin of the match (can be null). Should be "MT!" for machine translation. 
	 */
	public String origin;

	/**
	 * Creates a new ScoreInfo object with a given score and origin
	 * @param score the score (between 0 and 100).
	 * @param origin the origin (can be null).
	 */
	public ScoreInfo (int score,
		String origin)
	{
		this.score = score;
		this.origin = origin;
	}

}
