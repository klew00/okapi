/*===========================================================================
  Copyright (C) 2008-2009 by the Okapi Framework contributors
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

package net.sf.okapi.applications.rainbow.utilities.merging;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.okapi.applications.rainbow.lib.FilterAccess;
import net.sf.okapi.applications.rainbow.lib.Utils;
import net.sf.okapi.applications.rainbow.packages.IReader;
import net.sf.okapi.applications.rainbow.packages.Manifest;
import net.sf.okapi.applications.rainbow.packages.ManifestItem;
import net.sf.okapi.common.Event;
import net.sf.okapi.common.EventType;
import net.sf.okapi.common.IResource;
import net.sf.okapi.common.Util;
import net.sf.okapi.common.filters.IFilter;
import net.sf.okapi.common.filters.IFilterWriter;
import net.sf.okapi.common.resource.TextUnit;

public class Merger {

	private Manifest manifest;
	private IReader reader;
	private FilterAccess fa;
	private IFilter inpFilter;
	private IFilterWriter outFilter;
	private final Logger logger = LoggerFactory.getLogger("net.sf.okapi.logging");
	private boolean skipNoTranslate;
	private String trgLang;

	public Merger () {
		fa = new FilterAccess();

		// Get the location of the class source
		File file = new File(getClass().getProtectionDomain().getCodeSource().getLocation().getFile());
	    String rootFolder = file.getAbsolutePath();
	    // Remove the JAR file if running an installed version
	    if ( rootFolder.endsWith(".jar") ) rootFolder = Util.getDirectoryName(rootFolder);
	    // Remove the application folder in all cases
	    rootFolder = Util.getDirectoryName(rootFolder);
		String sharedFolder = Utils.getOkapiSharedFolder(rootFolder);

		// Load the FilterAccess list
		fa.loadList(sharedFolder + File.separator + "filters.xml");
	}

	public void initialize (Manifest manifest) {
		// Close any previous reader
		if ( reader != null ) {
			reader.closeDocument();
			reader = null;
		}
		// Set the manifest and the options
		this.manifest = manifest;
		skipNoTranslate = "omegat".equals(manifest.getPackageType());
		trgLang = manifest.getTargetLanguage();
	}
	
	public void merge (int docID) {
		try {
			ManifestItem item = manifest.getItem(docID);
			// Skip items not selected for merge
			if ( !item.selected() ) return;
			
			// File to merge
			String fileToMerge = manifest.getFileToMergePath(docID);
			// Instantiate a package reader of the proper type
			if ( reader == null ) {
				reader = (IReader)Class.forName(manifest.getReaderClass()).newInstance();
			}
			logger.info("\nMerging: " + fileToMerge);

			// Original and parameters files
			String originalFile = manifest.getRoot() + File.separator + manifest.getOriginalLocation()
				+ File.separator + String.format("%d.ori", docID);
			String paramsFile = manifest.getRoot() + File.separator + manifest.getOriginalLocation()
				+ File.separator + String.format("%d.fprm", docID);
			// Load the relevant filter
			inpFilter = fa.loadFilter(item.getFilterID(), paramsFile, inpFilter);
			
			reader.openDocument(fileToMerge, manifest.getSourceLanguage(), manifest.getTargetLanguage());
			
			// Initializes the input
			inpFilter.setOptions(manifest.getSourceLanguage(), trgLang,
				item.getInputEncoding(), true);
			File f = new File(originalFile);
			inpFilter.open(f.toURI());
			
			// Initializes the output
			String outputFile = manifest.getFileToGeneratePath(docID);
			Util.createDirectories(outputFile);
			outFilter = inpFilter.createFilterWriter();
			outFilter.setOptions(trgLang, item.getOutputEncoding());
			outFilter.setOutput(outputFile);
			
			// Do it
			Event event;			
			while ( inpFilter.hasNext() ) {
				event = inpFilter.next();
				if ( event.getEventType() == EventType.TEXT_UNIT ) {
					processTextUnit((TextUnit)event.getResource());
				}
				outFilter.handleEvent(event);
			}
		}
		catch ( Exception e ) {
			// Log and move on to the next file
			Throwable e2 = e.getCause();
			logger.error("Merging error. " + ((e2!=null) ? e2.getMessage() : e.getMessage()), e);
		}
		finally {
			if ( reader != null ) {
				reader.closeDocument();
				reader = null;
			}
			if ( inpFilter != null ) {
				inpFilter.close();
				inpFilter = null;
			}
			if ( outFilter != null ) {
				outFilter.close();
				outFilter = null;
			}
		}
	}

	private void processTextUnit (TextUnit tu) {
		// Skip the non-translatable if they are not included in the package
		if ( skipNoTranslate && !tu.isTranslatable() ) return;
			
		// Get item from the package document
		if ( !reader.readItem() ) {
			// Problem: 
			logger.warn("There is no more package item to merge (for id=\"{}\")",
				tu.getId());
			// Keep the source
			return;
		}

		// Update the item if needed
		if ( tu.isTranslatable() ) {
			TextUnit srcPkgItem = reader.getItem();
			
			if ( !tu.getId().equals(srcPkgItem.getId()) ) {
				// Problem: different IDs
				logger.warn("ID mismatch: original item id=\"{}\" package item id=\"{}\"",
					tu.getId(), srcPkgItem.getId());
				// Keep the source
				return;
			}

			if ( srcPkgItem.hasTarget(trgLang) ) {
				// Create the target entry for the output if it does not exist yet
				tu.createTarget(trgLang, false, IResource.COPY_ALL);
				// Set the codedText part of the content only. Do not modify the codes.
				//TODO: in-line could be clones: the code should come from the translation not the original then.
				try {
					tu.getTarget(trgLang).setCodedText(
						srcPkgItem.getTargetContent(trgLang).getCodedText(),
						tu.getSourceContent().getCodes(), false);
				}
				catch ( RuntimeException e ) {
					logger.error("Error with item id=\"{}\".", tu.getId());
					// Use the source instead, continue the merge
					tu.setTarget(trgLang, tu.getSource());
				}
			}
			else { // No translation in package
				if ( !tu.isEmpty() ) {
					logger.warn("Item id=\"{}\": No translation provided.", tu.getId());
					tu.setTarget(trgLang, tu.getSource());
				}
			}
		}
	}

}
