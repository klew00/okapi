/*===========================================================================
  Copyright (C) 2010-2012 by the Okapi Framework contributors
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

package net.sf.okapi.steps.rainbowkit.xliff;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.Util;
import net.sf.okapi.common.filterwriter.XLIFFWriter;
import net.sf.okapi.common.resource.StartDocument;
import net.sf.okapi.filters.rainbowkit.Manifest;
import net.sf.okapi.filters.rainbowkit.MergingInfo;
import net.sf.okapi.steps.rainbowkit.common.BasePackageWriter;

public class XLIFFPackageWriter extends BasePackageWriter {

	protected XLIFFWriter writer;

	private boolean preSegmented = false;
	private boolean forOmegat = false;
	private String rawDocPath;

	public XLIFFPackageWriter () {
		super(Manifest.EXTRACTIONTYPE_XLIFF);
	}

	/**
	 * Indicates if at least one text unit so far has been segmented.
	 * @return true if at least one text unit so far has been segmented
	 */
	public boolean getPreSegmented () {
		return preSegmented;
	}
	
	public void setForOmegat (boolean forOmegat) {
		this.forOmegat = forOmegat;
	}
	
	@Override
	protected void processStartBatch () {
		manifest.setSubDirectories("original", "work", "work", "done", null, false);
		setTMXInfo(true, null, (forOmegat ? true : false), true);
		super.processStartBatch();
	}
	
	@Override
	protected void processStartDocument (Event event) {
		super.processStartDocument(event);
		
		writer = new XLIFFWriter();

		writer.setOptions(manifest.getTargetLocale(), "UTF-8");
		MergingInfo item = manifest.getItem(docId);
		rawDocPath = manifest.getTempSourceDirectory() + item.getRelativeInputPath() + ".xlf";
		writer.setOutput(rawDocPath); // Not really used, but doesn't hurt just in case

		// Set the writer's options
		Options options = new Options();
		if ( forOmegat ) {
			// XLIFF options for OmegaT
			options.setCopySource(true);
			options.setPlaceholderMode(true);
			options.setIncludeAltTrans(false);
			// Direct setting for the writer (not an XLIFF option)
			writer.setUseSourceForTranslated(true);
		}
		else {
			// Get the options from the parameters
			if ( !Util.isEmpty(params.getWriterOptions()) ) {
				options.fromString(params.getWriterOptions());
			}
		}
		//TODO: Would be easier to use IParameters in XLIFFWriter.
		writer.setPlaceholderMode(options.getPlaceholderMode());
		writer.setCopySource(options.getCopySource());
		writer.setIncludeAltTrans(options.getIncludeAltTrans());
		writer.setSetApprovedAsNoTranslate(options.getSetApprovedAsNoTranslate());
		writer.setIncludeNoTranslate(options.getIncludeNoTranslate());
		
		StartDocument sd = event.getStartDocument();
		writer.create(rawDocPath, null, manifest.getSourceLocale(), manifest.getTargetLocale(),
			sd.getMimeType(), item.getRelativeInputPath(), null);
	}
	
	@Override
	protected Event processEndDocument (Event event) {
		if ( writer != null ) {
			writer.handleEvent(event);
			writer.close();
			writer = null;
		}

		if ( params.getSendOutput() ) {
			return super.creatRawDocumentEventSet(rawDocPath, "UTF-8",
				manifest.getSourceLocale(), manifest.getTargetLocale());
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
	protected void processStartGroup (Event event) {
		writer.handleEvent(event);
	}
	
	@Override
	protected void processEndGroup (Event event) {
		writer.handleEvent(event);
	}
	
	@Override
	protected void processTextUnit (Event event) {
		event = writer.handleEvent(event);
		writeTMXEntries(event.getTextUnit());
		
		// Check if it has been segmented (if not set already)
		if ( !preSegmented ) {
			preSegmented = event.getTextUnit().getSource().hasBeenSegmented();
		}
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
