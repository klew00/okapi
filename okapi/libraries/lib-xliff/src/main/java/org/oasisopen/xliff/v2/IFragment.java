/*===========================================================================
Copyright (C) 2011-2012 by the Okapi Framework contributors
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

package org.oasisopen.xliff.v2;

import java.io.Serializable;

public interface IFragment extends Serializable {
	
	public static final int STYLE_NODATA = 0;
	public static final int STYLE_DATAINSIDE = 1;
	public static final int STYLE_DATAOUTSIDE = 2;

	/**
	 * Gets the implementation-specific representation of the fragment content.
	 * Same as {@link #getCodedText()}.
	 * @return the implementation-specific representation of the fragment content.
	 */
	public String toString ();
	
	/**
	 * Gets the implementation-specific representation of the fragment content.
	 * Same as {@link #toString()}.
	 * @return the implementation-specific representation of the fragment content.
	 */
	public String getCodedText ();

	public void setCodedText (String codedText);
	
	/**
	 * Gets the content of this fragment formatted in XLIFF in the style {@link #STYLE_NODATA}.
	 * <p>Use {@link #toXLIFF(int)} to select the style of output.
	 * @return the content of this fragment formatted in XLIFF in the style {@link #STYLE_NODATA}.
	 */
	public String toXLIFF ();

	/**
	 * Gets the content of this fragment formatted in XLIFF in a given style.
	 * <p>The available styles are: {@link #STYLE_NODATA}, {@link #STYLE_DATAINSIDE}, and {@link #STYLE_DATAOUTSIDE}.
	 * @param style the style to use for the output.
	 * @return the content of this fragment formatted in XLIFF in a given style.
	 */
	public String toXLIFF (int style);

	/**
	 * Gets the data store associated with this fragment.
	 * @return the data store associated with this fragment.
	 */
	public IDataStore getDataStore ();
	
	/**
	 * Indicates if this fragment is empty or not.
	 * A fragment is empty if it has no text and no markers.
	 * @return true if the fragment is empty, false otherwise.
	 */
	public boolean isEmpty ();

	/**
	 * Gets the closing marker of a given opening one, if it
	 * corresponds to a well-formed span.
	 * @param openingMarker the opening marker to verify.
	 * @param from the first position after the opening marker.
	 * @return null if the content is not well-formed,
	 * the corresponding closing marker if the content is well-formed.
	 */
	public IMarker getWellFormedClosing (IMarker openingMarker,
		int from);
	
	/**
	 * Appends a plain text to this fragment. 
	 * @param plainText the text to append.
	 */
	public void append (String plainText);
	
	/**
	 * Appends a character to this fragment.
	 * @param ch the character to append.
	 */
	public void append (char ch);

	/**
	 * Appends an inline code to this fragment.
	 * @param code the code to append.
	 * @return the code that was appended.
	 */
	public ICode append (ICode code);
	
	/**
	 * Appends an inline annotation to this fragment.
	 * @param annotation the annotation to append.
	 * @return the annotation that was appended.
	 */
	public IAnnotation append (IAnnotation annotation);
	
	/**
	 * Appends an inline code to this fragment.
	 * @param type the type of code {@link org.oasisopen.xliff.v2.InlineType#InlineType InlineType}
	 * @param id the id of the code (corresponding opening and closing codes must have the same id).
	 * @param originalData the original data for this code.
	 * @return the code that was appended to this fragment.
	 */
	public ICode append (InlineType type,
		String id,
		String originalData);

}
