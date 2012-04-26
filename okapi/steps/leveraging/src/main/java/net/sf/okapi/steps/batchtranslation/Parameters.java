/*===========================================================================
  Copyright (C) 2009-2012 by the Okapi Framework contributors
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
import net.sf.okapi.common.uidescription.SpinInputPart;
import net.sf.okapi.common.uidescription.TextInputPart;

@EditorFor(Parameters.class)
public class Parameters extends BaseParameters implements IEditorDescriptionProvider {

	private static final String COMMAND = "command";
	private static final String ORIGIN = "origin";
	private static final String MARKASMT = "markAsMT";
	private static final String MAKETM = "makeTM";
	private static final String TMDIRECTORY = "tmDirectory";
	private static final String MAKETMX = "makeTMX";
	private static final String TMXPATH = "tmxPath";
	private static final String SENDTMX = "sendTMX";
	private static final String BLOCKSIZE = "blockSize";
	private static final String CHECKEXISTINGTM = "checkExistingTm";
	private static final String EXISTINGTM = "existingTm";
	private static final String SEGMENT = "segment";
	private static final String SRXPATH = "srxPath";
	
	private String command;
	private String origin;
	private boolean markAsMT;
	private boolean makeTM;
	private String tmDirectory;
	private boolean makeTMX;
	private String tmxPath;
	private boolean sendTMX;
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

	public boolean getMarkAsMT () {
		return markAsMT;
	}

	public void setMarkAsMT (boolean markAsMT) {
		this.markAsMT = markAsMT;
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

	public boolean getSendTMX () {
		return sendTMX;
	}

	public void setSendTMX (boolean sendTMX) {
		this.sendTMX = sendTMX;
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
		markAsMT = true;
		makeTM = false;
		tmDirectory = "";
		makeTMX = false;
		tmxPath = "";
		sendTMX = false;
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
		markAsMT = buffer.getBoolean(MARKASMT, markAsMT);
		makeTM = buffer.getBoolean(MAKETM, makeTM);
		tmDirectory = buffer.getString(TMDIRECTORY, tmDirectory);
		makeTMX = buffer.getBoolean(MAKETMX, makeTMX);
		tmxPath = buffer.getString(TMXPATH, tmxPath);
		sendTMX = buffer.getBoolean(SENDTMX, sendTMX);
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
		buffer.setBoolean(MARKASMT, markAsMT);
		buffer.setBoolean(MAKETM, makeTM);
		buffer.setString(TMDIRECTORY, tmDirectory);
		buffer.setBoolean(MAKETMX, makeTMX);
		buffer.setString(TMXPATH, tmxPath);
		buffer.setBoolean(SENDTMX, sendTMX);
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
		desc.add(MARKASMT, "Mark the generated translation as machine translation results", null);
		desc.add(SEGMENT, "Segment the text units, using the following SRX rules:", null);
		desc.add(SRXPATH, "", "Full path of the segmentation rules file to use");
		desc.add(MAKETM, "Import into the following Pensieve TM:", null);
		desc.add(TMDIRECTORY, "", "Location of the TM to create or use");
		desc.add(MAKETMX, "Create the following TMX document:", null);
		desc.add(TMXPATH, "", "Full path of the new TMX document to create");
		desc.add(SENDTMX, "Send the TMX document to the next step", null);
		desc.add(CHECKEXISTINGTM, "Check for existing entries in an existing Pensieve TM:", null);
		desc.add(EXISTINGTM, "", "Location of the TM to lookup");
		return desc;
	}

	public EditorDescription createEditorDescription (ParametersDescription paramDesc) {
		EditorDescription desc = new EditorDescription("Batch Translation", true, false);

		desc.addTextInputPart(paramDesc.get(COMMAND));

		SpinInputPart sip = desc.addSpinInputPart(paramDesc.get(BLOCKSIZE));
		sip.setVertical(false);
		sip.setRange(1, Integer.MAX_VALUE);
		
		TextInputPart tip = desc.addTextInputPart(paramDesc.get(ORIGIN));
		tip.setAllowEmpty(true);
		tip.setVertical(false);
		
		desc.addCheckboxPart(paramDesc.get(MARKASMT));
		
		desc.addSeparatorPart();
		
		CheckboxPart cbp = desc.addCheckboxPart(paramDesc.get(SEGMENT));
		PathInputPart pip = desc.addPathInputPart(paramDesc.get(SRXPATH), "SRX Path", false);
		pip.setBrowseFilters("SRX Documents (*.srx)\tAll Files (*.*)", "*.srx\t*.*");
		pip.setMasterPart(cbp, true);
		pip.setWithLabel(false);

		cbp = desc.addCheckboxPart(paramDesc.get(MAKETM));
		FolderInputPart fip = desc.addFolderInputPart(paramDesc.get(TMDIRECTORY), "TM Directory");
		fip.setMasterPart(cbp, true);
		fip.setWithLabel(false);

		cbp = desc.addCheckboxPart(paramDesc.get(MAKETMX));
		pip = desc.addPathInputPart(paramDesc.get(TMXPATH), "TMX Path", true);
		pip.setBrowseFilters("TMX Documents (*.tmx)\tAll Files (*.*)", "*.tmx\t*.*");
		pip.setMasterPart(cbp, true);
		pip.setWithLabel(false);
		CheckboxPart cbp2 = desc.addCheckboxPart(paramDesc.get(SENDTMX));
		cbp2.setMasterPart(cbp, true);
		
		cbp = desc.addCheckboxPart(paramDesc.get(CHECKEXISTINGTM));
		fip = desc.addFolderInputPart(paramDesc.get(EXISTINGTM), "TM Directory");
		fip.setMasterPart(cbp, true);
		fip.setWithLabel(false);
		
		return desc;
	}

}
