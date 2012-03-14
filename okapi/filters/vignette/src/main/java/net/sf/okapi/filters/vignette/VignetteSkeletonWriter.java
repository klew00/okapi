/*===========================================================================
  Copyright (C) 2008-2010 by the Okapi Framework contributors
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

package net.sf.okapi.filters.vignette;

import net.sf.okapi.common.resource.EndSubfilter;
import net.sf.okapi.common.resource.Ending;
import net.sf.okapi.common.resource.StartGroup;
import net.sf.okapi.common.resource.StartSubfilter;
import net.sf.okapi.common.skeleton.GenericSkeletonWriter;

public class VignetteSkeletonWriter extends GenericSkeletonWriter {

	@Override
	public String processStartGroup (StartGroup resource) {
		if ( resource.isReferent() || ( storageStack.size() > 0 )) {
			return super.processStartGroup(resource);
		}
		
		String tmp = super.processStartGroup(resource);
		if ( resource instanceof StartSubfilter ) {
			tmp += "<![CDATA[";
		}
		return tmp;
	}
	
	@Override
	public String processEndGroup (Ending resource) {
		if ( storageStack.size() > 0 ) {
			return super.processEndGroup(resource);
		}

		String tmp = super.processEndGroup(resource);
		if ( resource instanceof EndSubfilter ) {
			tmp += "]]>";
		}
		return tmp;
	}

}
