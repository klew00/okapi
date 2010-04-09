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
import net.sf.okapi.common.EditorFor;
import net.sf.okapi.common.ParametersDescription;
import net.sf.okapi.common.uidescription.CheckboxPart;
import net.sf.okapi.common.uidescription.EditorDescription;
import net.sf.okapi.common.uidescription.FolderInputPart;
import net.sf.okapi.common.uidescription.IEditorDescriptionProvider;
import net.sf.okapi.common.uidescription.PathInputPart;
import net.sf.okapi.common.uidescription.TextInputPart;

@EditorFor(Parameters.class)
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
	private static final String SEGMENT = "segment";
	private static final String SRXPATH = "srxPath";
	
	private String command;
	private String origin;
	private boolean makeTM;
	private String tmDirectory;
	private boolean makeTMX;
	private String tmxPath;
	private int blockSize;
	private boolean checkExistingTm;
	private String existingTm;
	private boolean segment;
	private String srxPath;
	
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
	
	public boolean getSegment () {
		return segment;
	}

	public void setSegment (boolean segment) {
		this.segment = segment;
	}
	
	public String getSrxPath () {
		return srxPath;
	}

	public void setSrxPath (String srxPath) {
		this.srxPath = srxPath;
	}

	public void reset() {
		command = "";
		origin = "";
		makeTM = false;
		tmDirectory = "";
		makeTMX = false;
		tmxPath = "";
		blockSize = 1000;
		checkExistingTm = false;
		existingTm = "";
		segment = false;
		srxPath = "";
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
		segment = buffer.getBoolean(SEGMENT, segment);
		srxPath = buffer.getString(SRXPATH, srxPath);
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
		buffer.setBoolean(SEGMENT, segment);
		buffer.setString(SRXPATH, srxPath);
		return buffer.toString();
	}

	@Override
	public ParametersDescription getParametersDescription () {
		ParametersDescription desc = new ParametersDescription(this);
		desc.add(COMMAND, "Command line", "Command line to execute the batch translation");
		desc.add(ORIGIN, "Origin identifier", "String that identifies the origin of the translation");
		desc.add(BLOCKSIZE, "Block size", "Maximum number of text units to process together");
		desc.add(SEGMENT, "Segment the text units", null);
		desc.add(SRXPATH, "SRX path", "Full path of the segmentation rules file to use");
		desc.add(MAKETM, "Import into a TM", null);
		desc.add(TMDIRECTORY, "Directory of the TM where to import", "Location of the TM to create or use");
		desc.add(MAKETMX, "Create a TMX document", null);
		desc.add(TMXPATH, "TMX path", "Full path of the new TMX document to create");
		desc.add(CHECKEXISTINGTM, "Check for existing entries in an existing TM", null);
		desc.add(EXISTINGTM, "Directory of the existing TM", "Location of the TM to lookup");
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
		
		CheckboxPart cbp = desc.addCheckboxPart(paramDesc.get(SEGMENT));
		PathInputPart pip = desc.addPathInputPart(paramDesc.get(SRXPATH), "SRX Path", false);
		pip.setBrowseFilters("SRX Documents (*.srx)\tAll Files (*.*)", "*.srx\t*.*");
		pip.setMasterPart(cbp, true);

		cbp = desc.addCheckboxPart(paramDesc.get(MAKETM));
		FolderInputPart fip = desc.addFolderInputPart(paramDesc.get(TMDIRECTORY), "TM Directory");
		fip.setMasterPart(cbp, true);

		cbp = desc.addCheckboxPart(paramDesc.get(MAKETMX));
		pip = desc.addPathInputPart(paramDesc.get(TMXPATH), "TMX Path", true);
		pip.setBrowseFilters("TMX Documents (*.tmx)\tAll Files (*.*)", "*.tmx\t*.*");
		pip.setMasterPart(cbp, true);
		
		cbp = desc.addCheckboxPart(paramDesc.get(CHECKEXISTINGTM));
		fip = desc.addFolderInputPart(paramDesc.get(EXISTINGTM), "TM Directory");
		fip.setMasterPart(cbp, true);

		return desc;
	}

}
