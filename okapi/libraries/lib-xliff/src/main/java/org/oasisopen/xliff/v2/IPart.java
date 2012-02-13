/*===========================================================================
  Copyright (C) 2012 by the Okapi Framework contributors
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

public interface IPart extends Serializable, IWithExtendedAttributes {

	public IFragment getSource ();
	
	public void setSource (IFragment fragment);
	
	public void setSource (String plainText);
	
	public boolean hasTarget ();

	public IFragment getTarget (boolean createIfNeeded);
	
	public void setTarget (IFragment fragment);
	
	public void setTarget (String plainText);
	
	public void setTargetOrder (int targetOrder);
	
	public int getTargetOrder ();
	
	public IDataStore getDataStore ();

}
