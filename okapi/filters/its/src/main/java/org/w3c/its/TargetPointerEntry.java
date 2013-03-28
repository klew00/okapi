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

package org.w3c.its;

import net.sf.okapi.common.resource.ITextUnit;

import org.w3c.dom.Node;

//TODO: remove dependency to Okapi if possible
public class TargetPointerEntry {

	private static final String SRC_TRGPTRFLAGNAME = "\u10ff"; // Name of the user-data property that holds the target pointer flag in the source
	private static final String TRG_TRGPTRFLAGNAME = "\u20ff"; // Name of the user-data property that holds the target pointer flag in the target

	static final int BEFORE = 0;
	static final int AFTER = 1;
		
	private int type;
	private Node srcNode;
	private Node trgNode;
	private ITextUnit tu;
		
	TargetPointerEntry (Node srcNode,
		Node trgNode)
	{
		this.srcNode = srcNode;
		this.trgNode = trgNode;
	}
		
	public Node getSourceNode () {
		return srcNode;
	}
		
	public Node getTargetNode () {
		return trgNode;
	}
}
