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

package net.sf.okapi.steps.rainbowkit.po;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.resource.ITextUnit;
import net.sf.okapi.filters.po.POWriter;
import net.sf.okapi.filters.po.Parameters;
import net.sf.okapi.filters.rainbowkit.Manifest;
import net.sf.okapi.filters.rainbowkit.MergingInfo;
import net.sf.okapi.steps.rainbowkit.common.BasePackageWriter;

public class POPackageWriter extends BasePackageWriter {

	private POWriter writer;
	private String rawDocPath;

	public POPackageWriter () {
		super(Manifest.EXTRACTIONTYPE_PO);
	}
	
	@Override
	protected void processStartBatch () {
		manifest.setSubDirectories("original", "work", "work", "done", null, true);
		setTMXInfo(true, null, true, true);
		super.processStartBatch();
	}
	
	@Override
	protected void processStartDocument (Event event) {
		super.processStartDocument(event);
		
		writer = new POWriter();
		Parameters wparams = (Parameters)writer.getParameters();
		wparams.setOutputGeneric(true);
		
		writer.setMode(true, false, true);
		writer.setOptions(manifest.getTargetLocale(), "UTF-8");
		
		MergingInfo item = manifest.getItem(docId);
		rawDocPath = manifest.getTempSourceDirectory() + item.getRelativeInputPath() + ".po";
		writer.setOutput(rawDocPath);

		writer.handleEvent(event);
	}
	
	@Override
	protected Event processEndDocument (Event event) {
		writer.handleEvent(event);
		if ( writer != null ) {
			writer.close();
			writer = null;
		}
		
		if ( params.getSendOutput() ) {
			return super.creatRawDocumentEventSet(rawDocPath, "UTF-8", manifest.getSourceLocale(), manifest.getTargetLocale());
		}
		else {
			return event;
		}
	}

	@Override
	protected void processStartSubDocument (Event event) {
		writer.handleEvent(event);
	}
	
	@Override
	protected void processEndSubDocument (Event event) {
		writer.handleEvent(event);
	}
	
	@Override
	protected void processTextUnit (Event event) {
		// Skip non-translatable
		ITextUnit tu = event.getTextUnit();
		if ( !tu.isTranslatable() ) return;
		
		writer.handleEvent(event);
		writeTMXEntries(event.getTextUnit());
	}

	@Override
	public void close () {
		if ( writer != null ) {
			writer.close();
			writer = null;
		}
	}

	@Override
	public String getName () {
		return getClass().getName();
	}

}
