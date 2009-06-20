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

package net.sf.okapi.filters.table;

import net.sf.okapi.filters.plaintext.common.CompoundParameters;

/**
 * Table Filter parameters
 * 
 * @version 0.1, 09.06.2009
 * @author Sergei Vasilyev  
 */

public class Parameters extends CompoundParameters {

	public Parameters() {
		super();
		
		addParameters(net.sf.okapi.filters.table.csv.Parameters.class);
		addParameters(net.sf.okapi.filters.table.fwc.Parameters.class);
		addParameters(net.sf.okapi.filters.table.tsv.Parameters.class);
	}
}

