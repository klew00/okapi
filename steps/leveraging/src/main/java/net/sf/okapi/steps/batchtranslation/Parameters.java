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
	private static final String ORIGIN = "origin";
	private static final String MAKETM = "makeTM";
	private static final String TMDIRECTORY = "tmDirectory";
	private static final String MAKETMX = "makeTMX";
	private static final String TMXPATH = "tmxPath";
	private static final String BLOCKSIZE = "blockSize";
	private static final String CHECKEXISTINGTM = "checkExistingTm";
	private static final String EXISTINGTM = "existingTm";
	
	private String command;
	private String origin;
	private boolean makeTM;
	private String tmDirectory;
	private boolean makeTMX;
	private String tmxPath;
	private int blockSize;
	private boolean checkExistingTm;
	private String existingTm;
	
	public Parameters () {
		reset();
	}
	
	public String getCommand () {
		return command;
	}

	public void setCommand (String command) {
		this.command = command;
	}

	public String getOrigin () {
		return origin;
	}

	public void setOrigin (String origin) {
		this.origin = origin;
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

	public int getBlockSize () {
		return blockSize;
	}

	public void setBlockSize (int blockSize) {
		this.blockSize = blockSize;
	}

	public boolean getCheckExistingTm () {
		return checkExistingTm;
	}

	public void setCheckExistingTm (boolean chekExistingTm) {
		this.checkExistingTm = chekExistingTm;
	}

	public String getExistingTm () {
		return existingTm;
	}

	public void setExistingTm (String existingTm) {
		this.existingTm = existingTm;
	}

	public void reset() {
		command = "\"C:\\Program Files\\PRMT8\\FILETRANS\\FileTranslator.exe\" \"${input}\" /as /ac \"/o:${output}\" /d:${srcLangName}-${trgLangName}";
		origin = "promt";
		makeTM = false;
		tmDirectory = "mttm";
		makeTMX = false;
		tmxPath = "pretrans.tmx";
		blockSize = 1000;
		checkExistingTm = false;
		existingTm = "";
	}

	public void fromString (String data) {
		reset();
		buffer.fromString(data);
		command = buffer.getString(COMMAND, command);
		origin = buffer.getString(ORIGIN, origin);
		makeTM = buffer.getBoolean(MAKETM, makeTM);
		tmDirectory = buffer.getString(TMDIRECTORY, tmDirectory);
		makeTMX = buffer.getBoolean(MAKETMX, makeTMX);
		tmxPath = buffer.getString(TMXPATH, tmxPath);
		blockSize = buffer.getInteger(BLOCKSIZE, blockSize);
		checkExistingTm = buffer.getBoolean(CHECKEXISTINGTM, checkExistingTm);
		existingTm = buffer.getString(EXISTINGTM, existingTm);
	}

	@Override
	public String toString() {
		buffer.reset();
		buffer.setString(COMMAND, command);
		buffer.setString(ORIGIN, origin);
		buffer.setBoolean(MAKETM, makeTM);
		buffer.setString(TMDIRECTORY, tmDirectory);
		buffer.setBoolean(MAKETMX, makeTMX);
		buffer.setString(TMXPATH, tmxPath);
		buffer.setInteger(BLOCKSIZE, blockSize);
		buffer.setBoolean(CHECKEXISTINGTM, checkExistingTm);
		buffer.setString(EXISTINGTM, existingTm);
		return buffer.toString();
	}

	@Override
	public ParametersDescription getParametersDescription () {
		ParametersDescription desc = new ParametersDescription(this);
		desc.add(COMMAND, "Command line", "Command line to execute the batch translation");
		desc.add(ORIGIN, "Origin identifier", "String that identifies the origin of the translation");
		desc.add(BLOCKSIZE, "Block size", "Maximum number of text units to process together");
		desc.add(MAKETM, "Import into a Pensieve TM", null);
		desc.add(TMDIRECTORY, "Directory of the TM where to import", "Location of the TM to create or use");
		desc.add(MAKETMX, "Create a TMX document", null);
		desc.add(TMXPATH, "TMX path", "Full path of the new TMX document to create");
		desc.add(CHECKEXISTINGTM, "Check for existing entries in a given TM", null);
		desc.add(EXISTINGTM, "Directory of the existing TM", "Location of the TM to lookup first");
		return desc;
	}

	public EditorDescription createEditorDescription (ParametersDescription paramDesc) {
		EditorDescription desc = new EditorDescription("Batch Translation", true, false);

		desc.addTextInputPart(paramDesc.get(COMMAND));

		TextInputPart tip = desc.addTextInputPart(paramDesc.get(BLOCKSIZE));
		tip.setVertical(false);
		tip.setRange(1, Integer.MAX_VALUE);
		
		tip = desc.addTextInputPart(paramDesc.get(ORIGIN));
		tip.setAllowEmpty(true);
		tip.setVertical(false);
		
		CheckboxPart cbp = desc.addCheckboxPart(paramDesc.get(MAKETM));
		tip = desc.addTextInputPart(paramDesc.get(TMDIRECTORY));
		tip.setMasterPart(cbp, true);

		cbp = desc.addCheckboxPart(paramDesc.get(MAKETMX));
		PathInputPart pip = desc.addPathInputPart(paramDesc.get(TMXPATH), "TMX Path", true);
		pip.setMasterPart(cbp, true);
		
		cbp = desc.addCheckboxPart(paramDesc.get(CHECKEXISTINGTM));
		tip = desc.addTextInputPart(paramDesc.get(EXISTINGTM));
		tip.setMasterPart(cbp, true);

		return desc;
	}

}
