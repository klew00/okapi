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

package net.sf.okapi.common.annotation;

import java.util.UUID;

import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.resource.TextContainer;
import net.sf.okapi.common.resource.TextUnit;

/**
 * Stores the data representing an alternate translation.
 */
public class AltTranslation implements Comparable<AltTranslation> {
	
	LocaleId srcLocId;
	LocaleId trgLocId;
	TextUnit tu;
	AltTranslationType type;
	int score;
	String origin;

	/**
	 * Creates a new AltTranslation object.
	 * @param sourceLocId
	 * 		the locale of the source.
	 * @param targetLocId
	 * 		the locale of the target.
	 * @param originalSource
	 * 		the original source content.
	 * @param alternateSource
	 * 		the source content corresponding to the alternate translation.
	 * @param alternateTarget
	 * 		the content of alternate translation. 
	 * @param type
	 * 		the type of alternate translation.
	 * @param score
	 * 		the score for this alternate translation (must be between 0 and 100).
	 * @param origin
	 * 		an optional identifier for the origin of this alternate translation.
	 */
	public AltTranslation (LocaleId sourceLocId,
		LocaleId targetLocId,
		TextContainer originalSource,
		TextContainer alternateSource,
		TextContainer alternateTarget,
		AltTranslationType type,
		int score,
		String origin)
	{
		this.srcLocId = sourceLocId;
		tu = new TextUnit(UUID.randomUUID().toString());
		if ( alternateSource != null ) {
			tu.setSource(alternateSource);
		}

//TODO: copy code-content from original source to alternate target if necessary
//TODO: the magic should go here
		
		tu.setTarget(trgLocId, alternateTarget);
		this.type = type;
		this.score = score;
		this.origin = origin;
	}

	/**
	 * Gets the target content of this entry.
	 * @return the target content of this entry.
	 */
	public TextContainer getTarget () {
		return tu.getTarget(trgLocId);
	}

	/**
	 * Gets the source locale for this entry.
	 * @return the source locale for this entry.
	 */
	public LocaleId getSourceLocale () {
		return srcLocId;
	}

	/**
	 * Gets the target locale for this entry.
	 * @return the target locale for this entry.
	 */
	public LocaleId getTargetLocale () {
		return trgLocId;
	}
	
	/**
	 * Gets the source content of this entry (can be empty)
	 * @return the source content of this entry (can be empty)
	 */
	public TextContainer getSource () {
		return tu.getSource();
	}
	
	/**
	 * Gets the score for this entry.
	 * @return the score for this entry.
	 */
	public int getScore () {
		return score;
	}
	
	public String getOrigin () {
		return origin;
	}

	public TextUnit getEntry () {
		return tu;
	}

	public AltTranslationType getType () {
		return type;
	}

	@Override
	public int compareTo (AltTranslation arg) {
		// TODO Auto-generated method stub
		return 0;
	}

}