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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.NoSuchElementException;

import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.query.MatchType;
import net.sf.okapi.common.resource.TextFragment;

/**
 * Annotation for storing one or more alternate translations for a target content.
 * <p>When used, this annotation is designed to be attached to the segments or the container of the targets.
 */
public class AltTranslationsAnnotation implements IAnnotation, Iterable<AltTranslation> {
	
	private ArrayList<AltTranslation> list;

	/**
	 * Creates a new empty AltTranslationsAnnotation object.
	 */
	public AltTranslationsAnnotation () {
		list = new ArrayList<AltTranslation>(2);
	}
	
	/**
	 * Adds an existing {@link AltTranslation} object to this annotation.
	 * @param alt the {@link AltTranslation} object to add.
	 */
	public void add (AltTranslation alt) {
		list.add(alt);
	}
	
	/**
	 * Adds a new entry to the list of alternate translations.
	 * @param sourceLocId the locale of the source.
	 * @param targetLocId the locale of the target.
	 * @param originalSource the original source content.
	 * @param alternateSource the source content corresponding to the alternate translation.
	 * @param alternateTarget the content of alternate translation. 
	 * @param type the type of alternate translation.
	 * @param score the score for this alternate translation (must be between 0 and 100).
	 * @param origin an option identifier for the origin of this alternate translation.
	 * @return the {@link AltTranslation} object created and added to this annotation.
	 */
	public AltTranslation add (LocaleId sourceLocId,
		LocaleId targetLocId,
		TextFragment originalSource,
		TextFragment alternateSource,
		TextFragment alternateTarget,
		MatchType type,
		int score,
		String origin)
	{
		list.add(new AltTranslation(sourceLocId, targetLocId, originalSource,
			alternateSource, alternateTarget, type, score, origin));
		return list.get(list.size()-1);
	}

	/**
	 * Creates a new iterator for the entries in this annotations.
	 * @return a new iterator for the entries in this annotations.
	 */
	@Override
	public Iterator<AltTranslation> iterator () {
		return new Iterator<AltTranslation>() {
			int current = 0;
			
			@Override
			public void remove () {
				throw new UnsupportedOperationException("The method remove() not supported.");
			}
			
			@Override
			public AltTranslation next () {
				if ( current >= list.size() ) {
					throw new NoSuchElementException("No more content parts.");
				}
				return list.get(current++);
			}
			
			@Override
			public boolean hasNext () {
				return (current<list.size());
			}
		};
	}

	/**
	 * Gets the first entry in the list of alternate translations.
	 * @return the first alternate translation entry or null if the list is empty.
	 */
	public AltTranslation getFirst () {
		if ( list.isEmpty() ) {
			return null;
		}
		return list.get(0);
	}
	
	/**
	 * Gets the last entry in the list of alternate translations.
	 * @return the last alternate translation entry or null if the list is empty.
	 */
	public AltTranslation getLast () {
		if ( list.isEmpty() ) {
			return null;
		}
		return list.get(list.size()-1);
	}

	/**
	 * Indicates if the list of alternate translation is empty. 
	 * @return true if the list is empty.
	 */
	public boolean isEmpty () {
		return list.isEmpty();
	}
	
	/**
	 * Gets the number of entries in this annotation.
	 * @return the number of alternate translations available.
	 */
	public int size () {
		return list.size();
	}

}
