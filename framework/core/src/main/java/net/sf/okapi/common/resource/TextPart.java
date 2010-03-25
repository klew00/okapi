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

package net.sf.okapi.common.resource;

public class TextPart {

	/**
	 * Text fragment of this part.
	 */
	public TextFragment text;
	
	public TextPart (TextFragment text) {
		if ( text == null ) {
			text = new TextFragment();
		}
		this.text = text;
	}
	
	public TextPart (String text) {
		this.text = new TextFragment(text);
	}

	@Override
	public TextPart clone () {
		return new TextPart(text.clone());
	}

	@Override
	public String toString () {
		if ( text == null ) return null;
		return text.toString();
	}

	public TextFragment getContent () {
		return text;
	}
	
	public boolean isSegment () {
		return false;
	}

}
