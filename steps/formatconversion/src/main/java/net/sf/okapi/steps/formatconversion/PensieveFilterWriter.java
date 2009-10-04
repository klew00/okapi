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

package net.sf.okapi.steps.formatconversion;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.Util;
import net.sf.okapi.common.exceptions.OkapiIOException;
import net.sf.okapi.common.exceptions.OkapiNotImplementedException;
import net.sf.okapi.common.filterwriter.IFilterWriter;
import net.sf.okapi.common.resource.Segment;
import net.sf.okapi.common.resource.StartDocument;
import net.sf.okapi.common.resource.TextContainer;
import net.sf.okapi.common.resource.TextUnit;
import net.sf.okapi.tm.pensieve.common.PensieveUtil;
import net.sf.okapi.tm.pensieve.common.TranslationUnit;
import net.sf.okapi.tm.pensieve.common.TranslationUnitVariant;
import net.sf.okapi.tm.pensieve.writer.ITmWriter;
import net.sf.okapi.tm.pensieve.writer.TmWriterFactory;

/**
 * Implementation of the {@link IFilterWriter} interface for Pensieve TM.
 * The resources are expected to have a target entry for the given target language.
 */
public class PensieveFilterWriter implements IFilterWriter {

	private ITmWriter writer;
	private String directory;
	private String srcLang;
	private String trgLang;
	
	public void cancel () {
		//TODO: support cancel
	}

	public void close () {
		if ( writer != null ) {
			try {
				writer.endIndex();
			}
			catch ( IOException e ) {
				throw new OkapiIOException("Error when closing the TM index.", e);
			}
		}
	}

	public String getName () {
		return "PensieveFilterWriter";
	}

	public IParameters getParameters () {
		return null;
	}

	public Event handleEvent (Event event) {
		switch ( event.getEventType() ) {
		case START_DOCUMENT:
			handleStartDocument(event);
			break;
		case TEXT_UNIT:
			handleTextUnit(event);
			break;
		case END_DOCUMENT:
			close();
			break;
		}
		return event;
	}

	/**
	 * Sets the options for this writer.
	 * @param language code of the output language.
	 * @param defaultEncoding encoding is ignored for this writer (it can be null).
	 */
	public void setOptions (String language,
		String defaultEncoding)
	{
		trgLang = language;
		// Encoding is ignored in this writer
	}

	/**
	 * Sets the output directory for the TM
	 * @param path full path of the output directory.
	 */
	public void setOutput (String path) {
		File f = new File(path);
		// We need to make sure this is absolute
		directory = f.getAbsolutePath(); // We assume it is a directory
	}

	/**
	 * This method is not supported by this writer and will
	 * throw and exception if called.
	 */
	public void setOutput (OutputStream output) {
		throw new OkapiNotImplementedException("Output type not supported.");
	}

	public void setParameters (IParameters params) {
		// No parameters for now.
	}

	private void handleStartDocument (Event event) {
		Util.createDirectories(directory+File.separator);
		//TODO: Move this check at the pensieve package level
		File file = new File(directory+File.separator+"segments.gen");
		// Create a new index only if one does not exists yet
		// If one exists we pass false to append to it
		writer = TmWriterFactory.createFileBasedTmWriter(directory, !file.exists());
		StartDocument sd = (StartDocument)event.getResource();
		srcLang = sd.getLanguage();
	}
	
	private void handleTextUnit (Event event) {
		TextUnit tu = (TextUnit)event.getResource();

		//TODO: What do we do with entries with empty/non-existing target?
		if ( !tu.hasTarget(trgLang) ) return;
		//if ( tu.getTarget(trgLang).isEmpty() ) return;

		try {
			TextContainer srcCont = tu.getSource();
			// If not segmented: index the whole entry
			if ( !srcCont.isSegmented() ) {
				writer.indexTranslationUnit2(PensieveUtil.convertToTranslationUnit(srcLang, trgLang, tu));
				return;
			}
			
			// Else: check if we have the same number of segments
			List<Segment> trgList = tu.getTarget(trgLang).getSegments();
			if ( trgList.size() != srcCont.getSegmentCount() ) {
				// Fall back to full entry
				writer.indexTranslationUnit2(PensieveUtil.convertToTranslationUnit(srcLang, trgLang, tu));
				//TODO: Log a warning
				return;
			}
			
			// Index each segment
			int i = 0;
			for ( Segment segment : srcCont.getSegments() ) {
				TranslationUnitVariant source = new TranslationUnitVariant(srcLang, segment.text);
				TranslationUnitVariant target = new TranslationUnitVariant(trgLang, trgList.get(i).text);
				TranslationUnit trUnit = new TranslationUnit(source, target);
				//TODO: what do we do with properties? e.g. tuid should not be used as it
				//PensieveUtil.populateMetaDataFromProperties(tu, trUnit);
				writer.indexTranslationUnit2(trUnit);
				i++;
			}
		}
		catch ( IOException e ) {
			throw new OkapiIOException("Error when indexing a text unit.", e);
		}
	}

}
