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

package net.sf.okapi.steps.rainbowkit.transifex;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.Util;
import net.sf.okapi.common.resource.TextUnit;
import net.sf.okapi.filters.po.POFilterWriter;
import net.sf.okapi.filters.po.Parameters;
import net.sf.okapi.filters.rainbowkit.Manifest;
import net.sf.okapi.filters.rainbowkit.MergingInfo;
import net.sf.okapi.lib.transifex.TransifexClient;
import net.sf.okapi.steps.rainbowkit.common.BasePackageWriter;

public class TransifexPackageWriter extends BasePackageWriter {

	private POFilterWriter potWriter;
	private POFilterWriter trgWriter;

	public TransifexPackageWriter () {
		super(Manifest.EXTRACTIONTYPE_TRANSIFEX);
	}
	
	@Override
	protected void processStartBatch () {
		manifest.setSubDirectories("original", "uploads", "downloads", "done", null, true);
		setTMXPaths(null, null, null, null);
		super.processStartBatch();
	}

	@Override
	protected void processEndBatch () {
		super.processEndBatch();
		
		// Get the parameters/options for the Transifex project
		net.sf.okapi.lib.transifex.Parameters options = new net.sf.okapi.lib.transifex.Parameters();
		// Get the options from the parameters
		if ( !Util.isEmpty(params.getWriterOptions()) ) {
			options.fromString(params.getWriterOptions());
		}

		TransifexClient cli = new TransifexClient(options.getServer());
		cli.setCredentials(options.getUser(), options.getPassword(), "okapi@opentag.com");
		
		String[] res = cli.createProject(options.getProjectId(), options.getProjectName(), "TODO short desc", "TODO Long desc");
		if ( res[0] == null ) {
			// Could not create the project
			logger.severe(res[1]);
			return;
		}
		for ( int id : manifest.getItems().keySet() ) {
			MergingInfo info = manifest.getItem(id);
			String poPath = manifest.getSourceDirectory() + info.getRelativeInputPath() + ".po";
			res = cli.putResource(poPath, manifest.getSourceLocale());
			if ( res[0] != null ) {
				info.setResourceId(res[1]);
			}
			else {
				logger.severe(res[1]);
			}
		}
		
		// Save the manifest again (for the esourceId)
		if ( params.getOutputManifest() ) {
			manifest.Save();
		}
	}
	
	@Override
	protected void processStartDocument (Event event) {
		super.processStartDocument(event);

		// Set the source POT file
		potWriter = new POFilterWriter();
		Parameters params = (Parameters)potWriter.getParameters();
		params.outputGeneric = true;
		potWriter.setMode(true, true);
		potWriter.setOptions(manifest.getSourceLocale(), "UTF-8");

		MergingInfo item = manifest.getItem(docId);
		String path = manifest.getSourceDirectory() + item.getRelativeInputPath() + ".po";
		potWriter.setOutput(path);

		// Set the target PO file
		trgWriter = new POFilterWriter();
		params = (Parameters)trgWriter.getParameters();
		params.outputGeneric = true;
		trgWriter.setMode(true, false);
		trgWriter.setOptions(manifest.getTargetLocale(), "UTF-8");
		
		String ex = Util.getExtension(item.getRelativeInputPath());
		String sd = Util.getDirectoryName(item.getRelativeInputPath());
		String fn = Util.getFilename(item.getRelativeInputPath(), false);
		path = manifest.getSourceDirectory()
			+ ( sd.isEmpty() ? "" : sd + "/" )
			+ fn + "_" + manifest.getTargetLocale().toPOSIXLocaleId()
			+ ex + ".po";
		
		trgWriter.setOutput(path);
		
		potWriter.handleEvent(event);
		trgWriter.handleEvent(event);
	}
	
	@Override
	protected void processEndDocument (Event event) {
		potWriter.handleEvent(event);
		trgWriter.handleEvent(event);
		if ( potWriter != null ) {
			potWriter.close();
			potWriter = null;
		}
		if ( trgWriter != null ) {
			trgWriter.close();
			trgWriter = null;
		}
		
		// Call the base method, in case there is something common to do
		super.processEndDocument(event);
	}

	@Override
	protected void processStartSubDocument (Event event) {
		potWriter.handleEvent(event);
		trgWriter.handleEvent(event);
	}
	
	@Override
	protected void processEndSubDocument (Event event) {
		potWriter.handleEvent(event);
		trgWriter.handleEvent(event);
	}
	
	@Override
	protected void processTextUnit (Event event) {
		// Skip non-translatable
		TextUnit tu = event.getTextUnit();
		if ( !tu.isTranslatable() ) return;
		
		potWriter.handleEvent(event);
		trgWriter.handleEvent(event);
		writeTMXEntries(event.getTextUnit());
	}

	@Override
	public void close () {
		if ( potWriter != null ) {
			potWriter.close();
			potWriter = null;
		}
		if ( trgWriter != null ) {
			trgWriter.close();
			trgWriter = null;
		}
	}

	@Override
	public String getName () {
		return getClass().getName();
	}

}
