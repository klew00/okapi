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

package net.sf.okapi.steps.simpletm2tmx;

import java.io.File;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.UsingParameters;
import net.sf.okapi.common.pipeline.BasePipelineStep;
import net.sf.okapi.common.resource.RawDocument;
import net.sf.okapi.tm.simpletm.Database;

@UsingParameters() // No parameters
public class SimpleTM2TMXStep extends BasePipelineStep {

	private Database db;
	private boolean isDone;

	public String getDescription() {
		return "Generates a TMX document from a SimpleTM database. "
			+ "Expects: raw document. Sends back: raw document.";
	}

	public String getName() {
		return "SimpleTM to TMX";
	}

	@Override
	public boolean isDone () {
		return isDone;
	}

	@Override
	protected Event handleStartBatchItem (Event event) {
		isDone = false;
		return event;
	}

	@Override
	protected Event handleRawDocument (Event event) {
		try {
			if ( db == null ) { // Create the db if needed
				db = new Database();
			}
			else { // Just in case, make sure the previous is closed
				db.close();
			}
			
			// Export the db to TMX
			RawDocument rd = (RawDocument)event.getResource(); 
			String path = new File(rd.getInputURI()).getPath(); 
			db.open(path);
			LocaleId srcLang = rd.getSourceLocale();
			LocaleId trgLang = rd.getTargetLocale();
			String outPath = path+".tmx";
			db.exportToTMX(outPath, srcLang, trgLang);
			db.close();
	
			// Create the new resource for the RawDocument
			// It is now a TMX file, not a SimpleTM file
			File file = new File(outPath);
			event.setResource(new RawDocument(file.toURI(), "UTF-8", srcLang, trgLang));
		}
		finally {
			isDone = true;
		}
		
		return event;
	}
	
}
