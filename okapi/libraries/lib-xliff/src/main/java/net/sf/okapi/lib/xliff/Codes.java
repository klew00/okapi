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

import java.util.ArrayList;

import org.oasisopen.xliff.v2.ICode;
import org.oasisopen.xliff.v2.ICodes;
import org.oasisopen.xliff.v2.IDataStore;
import org.oasisopen.xliff.v2.InlineType;

public class Codes implements ICodes {

	private static final long serialVersionUID = 0100L;

	private IDataStore store;
	private ArrayList<ICode> codes;
	
	public Codes (IDataStore store) {
		this.store = store;
	}

	@Override
	public boolean hasCode () {
		return (( codes != null ) && !codes.isEmpty() );
	}
	
	@Override
	public boolean hasCodeWithOriginalData () {
		if ( codes != null ) {
			for ( ICode code : codes ) {
				if ( code.hasOriginalData() ) {
					return true;
				}
			}
		}
		return false;
	}
	
	public ICode getClosingPart (ICode openingCode) {
		for ( ICode code : codes ) {
			if ( code.getId().equals(openingCode.getId()) ) {
				return code;
			}
		}
		return null;
	}
	
	public ICode getOpeningPart (ICode closingCode) {
		for ( ICode code : codes ) {
			if ( code.getId().equals(closingCode.getId()) ) {
				return code;
			}
		}
		return null;
	}
	
	@Override
	public int size () {
		if ( codes == null ) return 0;
		return codes.size();
	}

	@Override
	public IDataStore getDataStore () {
		return store;
	}

	@Override
	public ICode get (int index) {
		if ( codes == null ) return null;
		return codes.get(index);
	}
	
	@Override
	public ICode get (String id,
		InlineType type)
	{
		if ( codes == null ) return null;
		String tmp = Util.toInternalId(id, type);
		for ( ICode code : codes ) {
			if ( code.getInternalId().equals(tmp) ) return code;
		}
		return null; // Not found
	}

	@Override
	public void add (ICode code) {
		if ( codes == null ) codes = new ArrayList<ICode>();
		codes.add(code);
	}

	boolean validateClosingTypesMatchOpeningTypes () {
		if ( codes == null ) return true;
		for ( ICode code : codes ) {
			if ( code.getInlineType().equals(InlineType.CLOSING) ) {
				ICode opening = getOpeningPart(code);
				if ( opening == null ) {
					// Not found yet
				}
				else {
					String oType = opening.getType();
					String cType = code.getType();
					if ( oType == null ) {
						if ( cType != null ) {
							// Error: type defined in closing but not in opening
						}
					}
//					else 
				}
			}
		}
		return true;
	}
}
