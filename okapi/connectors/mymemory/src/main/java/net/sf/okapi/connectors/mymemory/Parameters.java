/*===========================================================================
  Copyright (C) 2009-2011 by the Okapi Framework contributors
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

public class Parameters extends BaseParameters implements IEditorDescriptionProvider {

	public static final String KEY = "key";
	public static final String USEMT = "useMT";
	public static final String SENDIP = "sendIP";
	
	private String key;
	private int useMT;
	private boolean sendIP;
	
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
	
	public boolean getSendIP () {
		return sendIP;
	}
	
	public void setSendIP (boolean sendIP) {
		this.sendIP = sendIP;
	}

	@Override
	public void fromString (String data) {
		reset();
		buffer.fromString(data);
		key = buffer.getEncodedString(KEY, key);
		useMT = buffer.getInteger(USEMT, useMT);
		sendIP = buffer.getBoolean(SENDIP, sendIP);
	}

	@Override
	public void reset () {
		key = "mmDemo123";
		useMT = 1;
		sendIP = true;
	}

	@Override
	public String toString () {
		buffer.reset();
		buffer.setEncodedString(KEY, key);
		buffer.setInteger(USEMT, useMT);
		buffer.setBoolean(SENDIP, sendIP);
		return buffer.toString();
	}

	@Override
	public ParametersDescription getParametersDescription () {
		ParametersDescription desc = new ParametersDescription(this);
		desc.add(KEY, "Key", "Access key");
		desc.add(USEMT, "Provide also machine translation result", null);
		desc.add(SENDIP, "Send IP address (recommended for large volumes)", null);
		return desc;
	}

	@Override
	public EditorDescription createEditorDescription (ParametersDescription paramsDesc) {
		EditorDescription desc = new EditorDescription("MyMemory TM Connector Settings");
		// Key is used for setting translations, which is not implemented
		// The key is not used with REST interface
//		TextInputPart tip = desc.addTextInputPart(paramsDesc.get(Parameters.KEY));
//		tip.setPassword(true);
		desc.addCheckboxPart(paramsDesc.get(USEMT));
		desc.addCheckboxPart(paramsDesc.get(SENDIP));
		return desc;
	}

}