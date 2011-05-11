/*===========================================================================
  Copyright (C) 2009 by the Okapi Framework contributors
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

package net.sf.okapi.connectors.mymemory;

import net.sf.okapi.common.BaseParameters;
import net.sf.okapi.common.ParametersDescription;
import net.sf.okapi.common.uidescription.EditorDescription;
import net.sf.okapi.common.uidescription.IEditorDescriptionProvider;
import net.sf.okapi.common.uidescription.TextInputPart;

public class Parameters extends BaseParameters implements IEditorDescriptionProvider {

	public static final String KEY = "key";
	public static final String USEMT = "useMT";
	
	private String key;
	private int useMT;
	
	public Parameters () {
		reset();
		toString();
	}
	
	public Parameters (String initialData) {
		fromString(initialData);
	}
	
	public String getKey () {
		return key;
	}

	public void setKey (String key) {
		this.key = key;
	}

	public int getUseMT() {
		return useMT;
	}

	public void setUseMT (int useMT) {
		this.useMT = useMT;
	}

	@Override
	public void fromString (String data) {
		reset();
		buffer.fromString(data);
		key = buffer.getEncodedString(Parameters.KEY, key);
		useMT = buffer.getInteger(Parameters.USEMT, useMT);
	}

	@Override
	public void reset () {
		key = "mmDemo123";
		useMT = 1;
	}

	@Override
	public String toString () {
		buffer.reset();
		buffer.setEncodedString(Parameters.KEY, key);
		buffer.setInteger(Parameters.USEMT, useMT);
		return buffer.toString();
	}

	@Override
	public ParametersDescription getParametersDescription () {
		ParametersDescription desc = new ParametersDescription(this);
		desc.add(Parameters.KEY, "Key", "Access key");
		desc.add(Parameters.USEMT, "Provide also machine translation result", null);
		return desc;
	}

	@Override
	public EditorDescription createEditorDescription (ParametersDescription paramsDesc) {
		EditorDescription desc = new EditorDescription("MyMemory TM Connector Settings");
		TextInputPart tip = desc.addTextInputPart(paramsDesc.get(Parameters.KEY));
		tip.setPassword(true);
		desc.addCheckboxPart(paramsDesc.get(Parameters.USEMT));
		return desc;
	}
}
