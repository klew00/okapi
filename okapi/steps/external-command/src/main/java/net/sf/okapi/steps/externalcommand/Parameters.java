/*===========================================================================
  Copyright (C) 2009-2010 by the Okapi Framework contributors
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

package net.sf.okapi.steps.externalcommand;

import net.sf.okapi.common.BaseParameters;
import net.sf.okapi.common.EditorFor;
import net.sf.okapi.common.ParametersDescription;
import net.sf.okapi.common.uidescription.EditorDescription;
import net.sf.okapi.common.uidescription.IEditorDescriptionProvider;
import net.sf.okapi.common.uidescription.TextInputPart;

@EditorFor(Parameters.class)
public class Parameters extends BaseParameters implements IEditorDescriptionProvider {
	
	private static final String COMMAND = "command";
	private static final String TIMEOUT = "timeout";

	private String command;	
	private int timeout;

	public Parameters () {
		reset();
	}
	
	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}

	public int getTimeout() {
		return timeout;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	@Override
	public void reset() {
		command = "";	
		timeout = -1; // default is no timeout
	}

	@Override
	public void fromString (String data) {
		reset();
		buffer.fromString(data);
		command = buffer.getString(COMMAND, command);
		timeout = buffer.getInteger(TIMEOUT, -1);
	}

	@Override
	public String toString() {
		buffer.reset();
		buffer.setParameter(COMMAND, command);
		buffer.setParameter(TIMEOUT, timeout);	
		return buffer.toString();
	}

	@Override
	public ParametersDescription getParametersDescription () {
		ParametersDescription desc = new ParametersDescription(this);
		desc.add(COMMAND, "Command line", "Command path to execute");
		desc.add(TIMEOUT, "Timeout", "Timeout in seconds after which the command is cancelled (use -1 for no timeout)");
		return desc;
	}
	
	@Override
	public EditorDescription createEditorDescription (ParametersDescription paramsDesc) {
		EditorDescription desc = new EditorDescription("External Command", true, false);
		desc.addTextInputPart(paramsDesc.get(COMMAND));
		TextInputPart tip = desc.addTextInputPart(paramsDesc.get(TIMEOUT));
		tip.setVertical(false);
		return desc;
	}

}