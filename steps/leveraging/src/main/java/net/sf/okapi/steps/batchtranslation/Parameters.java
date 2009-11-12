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

package net.sf.okapi.steps.batchtranslation;

import net.sf.okapi.common.BaseParameters;
import net.sf.okapi.common.ParametersDescription;
import net.sf.okapi.common.uidescription.CheckboxPart;
import net.sf.okapi.common.uidescription.EditorDescription;
import net.sf.okapi.common.uidescription.IEditorDescriptionProvider;
import net.sf.okapi.common.uidescription.PathInputPart;
import net.sf.okapi.common.uidescription.TextInputPart;

public class Parameters extends BaseParameters implements IEditorDescriptionProvider {

	private static final String COMMAND = "command";
	private static final String MAKETM = "makeTM";
	private static final String TMDIRECTORY = "tmDirectory";
	private static final String MAKETMX = "makeTMX";
	private static final String TMXPATH = "tmxPath";
	
	private String command;
	private boolean makeTM;
	private String tmDirectory;
	private boolean makeTMX;
	private String tmxPath;
	
	public Parameters () {
		reset();
	}
	
	public String getCommand () {
		return command;
	}

	public void setCommand (String command) {
		this.command = command;
	}

	public boolean getMakeTMX () {
		return makeTMX;
	}

	public void setMakeTMX (boolean makeTMX) {
		this.makeTMX = makeTMX;
	}

	public String getTmxPath () {
		return tmxPath;
	}

	public void setTmxPath (String tmxPath) {
		this.tmxPath = tmxPath;
	}

	public boolean getMakeTM () {
		return makeTM;
	}

	public void setMakeTM (boolean makeTM) {
		this.makeTM = makeTM;
	}

	public String getTmDirectory () {
		return tmDirectory;
	}

	public void setTmDirectory (String tmDirectory) {
		this.tmDirectory = tmDirectory;
	}

	public void reset() {
		command = "\"C:\\Program Files\\PRMT8\\FILETRANS\\FileTranslator.exe\" \"${input}\" /as /ac \"/o:${output}\" /d:${srcLangName}-${trgLangName}";
		makeTM = false;
		tmDirectory = "mttm";
		makeTMX = false;
		tmxPath = "pretrans.tmx";
	}

	public void fromString (String data) {
		reset();
		buffer.fromString(data);
		command = buffer.getString(COMMAND, command);
		makeTM = buffer.getBoolean(MAKETM, makeTM);
		tmDirectory = buffer.getString(TMDIRECTORY, tmDirectory);
		makeTMX = buffer.getBoolean(MAKETMX, makeTMX);
		tmxPath = buffer.getString(TMXPATH, tmxPath);
	}

	@Override
	public String toString() {
		buffer.reset();
		buffer.setString(COMMAND, command);
		buffer.setBoolean(MAKETM, makeTM);
		buffer.setString(TMDIRECTORY, tmDirectory);
		buffer.setBoolean(MAKETMX, makeTMX);
		buffer.setString(TMXPATH, tmxPath);
		return buffer.toString();
	}

	@Override
	public ParametersDescription getParametersDescription () {
		ParametersDescription desc = new ParametersDescription(this);
		desc.add(COMMAND, "Command line", "Command line to execute the batch translation");
		desc.add(MAKETM, "Create a Pensieve TM", null);
		desc.add(TMDIRECTORY, "TM directory", "Location of the TM to create");
		desc.add(MAKETMX, "Create a TMX document", null);
		desc.add(TMXPATH, "TMX path", "Full path of the TMX document to create");
		return desc;
	}

	public EditorDescription createEditorDescription (ParametersDescription paramDesc) {
		EditorDescription desc = new EditorDescription("Batch Translation", true, false);

		desc.addTextInputPart(paramDesc.get(COMMAND));
		
		CheckboxPart cbp = desc.addCheckboxPart(paramDesc.get(MAKETM));
		TextInputPart tip = desc.addTextInputPart(paramDesc.get(TMDIRECTORY));
		tip.setMasterPart(cbp, true);

//TODO: to implement		
//		cbp = desc.addCheckboxPart(paramDesc.get(MAKETMX));
//		PathInputPart pip = desc.addPathInputPart(paramDesc.get(TMXPATH), "TMX Path", true);
//		pip.setMasterPart(cbp, true);
		
		return desc;
	}

}
