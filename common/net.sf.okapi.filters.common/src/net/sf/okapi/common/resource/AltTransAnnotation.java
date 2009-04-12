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

package net.sf.okapi.common.resource;

import java.util.LinkedList;

import net.sf.okapi.common.annotation.IAnnotation;
import net.sf.okapi.common.resource.TextContainer;
import net.sf.okapi.common.resource.TextUnit;

/**
 * Implements an annotation for alternate translations. For example, to represent
 * the alt-trans elements of XLIFF. This annotation is meant to
 * contains all the alternate translation entries of a give text unit.
 */
public class AltTransAnnotation implements IAnnotation {
	
	private class AltTrans {

		String srcLang;
		String trgLang;
		TextUnit tu;
		
		public AltTrans (String sourceLanguage,
			TextUnit textUnit)
		{
			srcLang = sourceLanguage;
			tu = textUnit;
		}
	}

	private LinkedList<AltTrans> list;
	private int id;
	private int current;
	
	/**
	 * Creates a new AltTransAnnotation object. 
	 */
	public AltTransAnnotation () {
		list = new LinkedList<AltTrans>();
		current = -1;
	}
	
	/**
	 * Adds a new entry to this annotation. This method also set the current entry
	 * of the iteration to the entry that was just added.
	 * @param sourceLanguage code of the source language, or null.
	 * @param sourceText text of the source element, or null.
	 */
	public void addNew (String sourceLanguage,
		TextContainer sourceText)
	{
		TextUnit tu = new TextUnit(String.valueOf(++id));
		if ( sourceText != null ) {
			tu.setSource(sourceText);
		}
		list.add(new AltTrans(sourceLanguage, tu));
		current = list.size()-1;
	}

	/**
	 * Sets the target text for the last entry added to this
	 * annotation. If no entry exists yet, or if one exists but has its target
	 * already set, a new entry with empty source is created automatically.
	 * @param targetLanguage code of the target language.
	 * @param targetText text of the target.
	 */
	public void setTarget (String targetLanguage,
		TextContainer targetText)
	{
		if ( list.size() == 0 ) {
			// We have no entry yet: create one
			addNew(null, null);
		}
		if ( list.getLast().trgLang != null ) {
			// We have an entry, but its target is already set: create a new one
			addNew(null, null);
		}
		list.getLast().tu.setTarget(targetLanguage, targetText);
		list.getLast().trgLang = targetLanguage;
	}

	/**
	 * Resets the iteration mode for this annotation. 
	 * @return true if there is at least one entry, false if this annotation has 
	 * no entry.
	 */
	public boolean startIteration () {
		current = -1;
		return (list.size() > 0);
	}
	
	/**
	 * Moves to the next alt-trans entry in this annotation.
	 * @return true if there was one available, false if there is no more entry.
	 */
	public boolean moveToNext () {
		if ( ++current < list.size() ) return true;
		return false;
	}

	/**
	 * Indicates if the current entry of this annotation has a source.
	 * @return true if the current entry has a source, false if it does not.
	 */
	public boolean hasSource () {
		if (( current == -1 ) && ( current < list.size() )) return false;
		return (list.get(current).srcLang != null);
	}
	
	/**
	 * Gets the current entry for this annotation. For ease of use the entry is returned
	 * as a TextUnit. Be aware that there are slight difference between a normal
	 * text unit and one that represents an alternate translation. 
	 * @return the TextUnit object for the current entry, or null if the current 
	 * entry is not set.
	 */
	public TextUnit getEntry () {
		if (( current == -1 ) && ( current < list.size() )) return null;
		return list.get(current).tu;
	}

	/**
	 * Indicates if there is at least one entry in this annotation.
	 * @return true if there is at least one entry in this annotation,
	 * false if there is none.
	 */
	public boolean isEmpty () {
		return (list.size() == 0);
	}
	
	/**
	 * Gets the source language for the current entry of this annotation.
	 * @return the code for the source language for the current entry,
	 * or null if the current entry is not set, or if there is no source
	 * for this entry.
	 */
	public String getSourceLanguage () {
		if (( current == -1 ) && ( current < list.size() )) return null;
		return list.get(current).srcLang;
	}

	/**
	 * Gets the target language for the current entry of this annotation.
	 * @return the code for the target language for the current entry,
	 * or null if the current entry is not set, or if there is no target
	 * for this entry.
	 */
	public String getTargetLanguage () {
		if (( current == -1 ) && ( current < list.size() )) return null;
		return list.get(current).trgLang;
	}

}
