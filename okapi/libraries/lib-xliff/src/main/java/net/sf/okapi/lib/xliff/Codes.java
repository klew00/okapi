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

package net.sf.okapi.lib.xliff;

import java.io.Serializable;
import java.util.ArrayList;

public class Codes implements Serializable {

	private static final long serialVersionUID = 0100L;

	private CodesStore store;
	private ArrayList<Code> codes;
	
	public Codes (CodesStore store) {
		this.store = store;
	}

	public boolean hasCode () {
		return (( codes != null ) && !codes.isEmpty() );
	}
	
	public int size () {
		if ( codes == null ) return 0;
		return codes.size();
	}

	public CodesStore getCodesStore () {
		return store;
	}

	public Code get (int index) {
		if ( codes == null ) return null;
		return codes.get(index);
	}
	
	public Code get (String id,
		CodeType type)
	{
		if ( codes == null ) return null;
		String tmp;
		switch ( type ) {
		case CLOSING:
			tmp = "c"+id;
			break;
		case OPENING:
			tmp = "o"+id;
			break;
		default:
			tmp = "p"+id;
			break;
		}
		for ( Code code : codes ) {
			if ( code.internalId.equals(tmp) ) return code;
		}
		return null; // Not found
	}

	public void add (Code code) {
		if ( codes == null ) codes = new ArrayList<Code>();
		codes.add(code);
	}

}
