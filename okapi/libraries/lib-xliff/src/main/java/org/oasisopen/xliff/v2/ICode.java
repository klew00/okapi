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

package org.oasisopen.xliff.v2;

import java.io.Serializable;

public interface ICode extends Serializable {

	public String getOriginalData ();
	
	public void setOriginalData (String originalData);
	
	public boolean hasOriginalData ();
	
	public String getId ();
	
	public String getInternalId ();
	
	public InlineType getInlineType ();

	public void setInlineType (InlineType inlineType);

	public String getType ();
	
	public void setType (String type);
	
	public String getDisp ();
	
	public void setDisp (String disp);
	
	public String getEquiv ();
	
	public void setEquiv (String equiv);
	
	public String getSubFlows ();
	
	public void setSubFlows (String subFlows);
	
	public int getHints ();
	
	public void setHints (int hints);
	
	public boolean canDelete ();

	public void setCanDelete (boolean canDelete);

	public boolean canReplicate ();

	public void setCanReplicate (boolean canReplicate);

	public boolean canReorder ();

	public void setCanReorder (boolean canReorder);

	public boolean canChangeParent ();

	public void setCanChangeParent (boolean canChangeParent);
	
	public boolean equals (ICode code);

}
