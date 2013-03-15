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

package net.sf.okapi.common.uidescription;

import java.util.UUID;

import net.sf.okapi.common.ParameterDescriptor;

/**
 * UI part descriptor for a text item. This UI part supports no type.
 */
public class TextLabelPart extends AbstractPart {

	/**
	 * Creates a new TextPart object.
	 */
	public TextLabelPart (String text) {
		// Create descriptor with unique name to allow several in the same dialog
		super(new ParameterDescriptor(UUID.randomUUID().toString(), null, text, null));
	}

	@Override
	protected void checkType () {
		// Nothing to check
	}

}
