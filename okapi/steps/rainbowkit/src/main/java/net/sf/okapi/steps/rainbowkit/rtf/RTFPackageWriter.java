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

package net.sf.okapi.steps.rainbowkit.rtf;

import net.sf.okapi.common.Event;
import net.sf.okapi.filters.rainbowkit.Manifest;
import net.sf.okapi.filters.rainbowkit.MergingInfo;
import net.sf.okapi.steps.rainbowkit.common.BasePackageWriter;

public class RTFPackageWriter extends BasePackageWriter {

	private RTFLayerWriter layerWriter;

	public RTFPackageWriter () {
		super(Manifest.EXTRACTIONTYPE_RTF);
	}
	
	@Override
	protected void processStartBatch () {
		manifest.setSubDirectories("original", "work", "work", "done", null, true);
		setTMXInfo(true, null, false, false);
		super.processStartBatch();
	}
	
	@Override
	protected void processStartDocument (Event event) {
		super.processStartDocument(event);
		MergingInfo item = manifest.getItem(docId);
		String path = manifest.getTempSourceDirectory() + item.getRelativeInputPath() + ".rtf";
		layerWriter = new RTFLayerWriter(skelWriter, path, manifest.getTargetLocale(), item.getTargetEncoding());
		layerWriter.writeEvent(event);
	}
	
	@Override
	protected void processEndDocument (Event event) {
		layerWriter.writeEvent(event);
		// Call the base method, in case there is something common to do
		super.processEndDocument(event);
	}

	@Override
	protected void processStartSubDocument (Event event) {
		layerWriter.writeEvent(event);
	}
	
	@Override
	protected void processEndSubDocument (Event event) {
		layerWriter.writeEvent(event);
	}
	
	@Override
	protected void processTextUnit (Event event) {
		layerWriter.writeEvent(event);
		writeTMXEntries(event.getTextUnit());
	}

	@Override
	protected void processStartGroup (Event event) {
		layerWriter.writeEvent(event);
	}

	@Override
	protected void processEndGroup (Event event) {
		layerWriter.writeEvent(event);
	}

	protected void processDocumentPart (Event event) {
		layerWriter.writeEvent(event);
	}

	@Override
	public void close () {
		if ( layerWriter != null ) {
			layerWriter.close();
			layerWriter = null;
		}
	}

	@Override
	public String getName () {
		return getClass().getName();
	}

}
