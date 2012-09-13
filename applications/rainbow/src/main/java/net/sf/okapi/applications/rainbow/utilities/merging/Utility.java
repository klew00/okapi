/*===========================================================================
  Copyright (C) 2008 by the Okapi Framework contributors
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
============================================================================*/

package net.sf.okapi.applications.rainbow.utilities.merging;

import java.util.Iterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.okapi.applications.rainbow.packages.Manifest;
import net.sf.okapi.applications.rainbow.utilities.BaseUtility;
import net.sf.okapi.applications.rainbow.utilities.ISimpleUtility;
import net.sf.okapi.common.IParameters;

public class Utility extends BaseUtility implements ISimpleUtility {
	private final Logger logger = LoggerFactory.getLogger(getClass());
	private String manifestPath;
	private Manifest manifest;
	private Merger merger;
	
	public String getName () {
		return "oku_merging";
	}
	
	public void preprocess () {
		manifest = new Manifest();
		merger = new Merger();
	}

	public void postprocess () {
	}

	public IParameters getParameters () {
		return null;
	}

	public boolean hasParameters () {
		return true;
	}

	public boolean needsRoots () {
		return false;
	}

	public void setParameters (IParameters paramsObject) {
		// Not used in this utility
	}

	public boolean isFilterDriven () {
		return false;
	}
	
	public int requestInputCount () {
		return 1;
	}
	
	public void processInput () {
		manifestPath = getInputPath(0);
		// Load the manifest file to use
		manifest.load(manifestPath);
		// Check the package where the manifest has been found
		manifest.checkPackageContent();
		
		// UI check
		if ( canPrompt ) {
			ManifestDialog dlg = new ManifestDialog(shell, help);
			if ( !dlg.showDialog(manifest) ) {
				return;
			}
		}
		
		// Initialize the merger for this manifest
		merger.initialize(manifest);
		
		// One target language only, and take it from the manifest
		logger.info("Target: " + manifest.getTargetLanguage());
		
		// Process each selected document in the manifest
		Iterator<Integer> iter = manifest.getItems().keySet().iterator();
		while ( iter.hasNext() ) {
			merger.execute(iter.next());
		}
	}

}
