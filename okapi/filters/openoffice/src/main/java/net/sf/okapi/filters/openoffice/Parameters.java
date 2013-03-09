/*===========================================================================
  Copyright (C) 2008-2009 by the Okapi Framework contributors
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

package net.sf.okapi.filters.openoffice;

import net.sf.okapi.common.BaseParameters;

public class Parameters extends BaseParameters {
	
	public boolean extractNotes;
	public boolean extractReferences;
	public boolean convertSpacesTabs;
	
	public Parameters () {
		reset();
		toString(); // fill the list
	}
	
	public void reset () {
		extractNotes = false;
		extractReferences = false;
		convertSpacesTabs = false;
	}

	public void fromString (String data) {
		reset();
		buffer.fromString(data);
		extractNotes = buffer.getBoolean("extractNotes", extractNotes);
		extractReferences = buffer.getBoolean("extractReferences", extractReferences);
		convertSpacesTabs = buffer.getBoolean("convertSpacesTabs", convertSpacesTabs);
	}

	@Override
	public String toString () {
		buffer.reset();
		buffer.setBoolean("extractNotes", extractNotes);
		buffer.setBoolean("extractReferences", extractReferences);
		buffer.setBoolean("convertSpacesTabs", convertSpacesTabs);
		return buffer.toString();
	}
}
