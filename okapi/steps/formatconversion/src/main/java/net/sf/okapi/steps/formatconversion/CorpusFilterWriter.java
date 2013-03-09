/*===========================================================================
  Copyright (C) 2009-2011 by the Okapi Framework contributors
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

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.Util;
import net.sf.okapi.common.encoder.EncoderManager;
import net.sf.okapi.common.exceptions.OkapiIOException;
import net.sf.okapi.common.exceptions.OkapiNotImplementedException;
import net.sf.okapi.common.filterwriter.IFilterWriter;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.resource.Segment;
import net.sf.okapi.common.resource.ISegments;
import net.sf.okapi.common.resource.StartDocument;
import net.sf.okapi.common.resource.TextContainer;
import net.sf.okapi.common.resource.TextFragment;
import net.sf.okapi.common.resource.ITextUnit;
import net.sf.okapi.common.resource.TextUnitUtil;
import net.sf.okapi.common.skeleton.ISkeletonWriter;

/**
 * Implementation of the {@link IFilterWriter} interface for corpus-type output.
 * The corpus output is made of plain text UTF-8 document with one line per segment
 * or text unit and one output file per language in the input.
 */
public class CorpusFilterWriter implements IFilterWriter {

	private OutputStreamWriter srcWriter;
	private OutputStream srcOutputStream;
	private String srcOutputPath;
	private File srcTempFile;
	private OutputStreamWriter trgWriter;
	private OutputStream trgOutputStream;
	private String trgOutputPath;
	private File trgTempFile;
	private String baseOutputPath;
	private LocaleId trgLoc;
	private String linebreak = System.getProperty("line.separator");

	public CorpusFilterWriter () {
	}
	
	public void cancel () {
		//TODO: support cancel
	}

	public void close () {
		if ( srcWriter == null ) return;
		try {
			// Source writer
			srcWriter.close();
			srcWriter = null;
			if ( srcOutputStream != null ) {
				srcOutputStream.close();
				srcOutputStream = null;
			}
			// If it was in a temporary file, copy it over the existing one
			// If the IFilter.close() is called before IFilterWriter.close()
			// this should allow to overwrite the input.
			if ( srcTempFile != null ) {
				Util.copy(new FileInputStream(srcTempFile), srcOutputPath);
			}

			// target writer
			trgWriter.close();
			trgWriter = null;
			if ( trgOutputStream != null ) {
				trgOutputStream.close();
				trgOutputStream = null;
			}
			if ( trgTempFile != null ) {
				Util.copy(new FileInputStream(trgTempFile), trgOutputPath);
			}
		}
		catch ( IOException e ) {
			throw new OkapiIOException("Error closing the output files.", e);
		}
	}

	public String getName () {
		return "CorpusFilterWriter";
	}

	public EncoderManager getEncoderManager () {
		return null;
	}
	
	@Override
	public ISkeletonWriter getSkeletonWriter () {
		return null;
	}

	public IParameters getParameters () {
		return null;
	}

	public Event handleEvent (Event event) {
		switch ( event.getEventType() ) {
		case START_BATCH:
			initialize();
			break;
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

	public void setOptions (LocaleId locale,
		String defaultEncoding)
	{
		trgLoc = locale;
		// Encoding not used: use always UTF-8
	}

	public void setOutput (String path) {
		baseOutputPath = path;
	}

	public void setOutput (OutputStream output) {
		throw new OkapiNotImplementedException("The method setOutput(OutputStream) is not supported for this filter-writer.");
	}

	public void setParameters (IParameters params) {
		//todo
	}

	private void initialize () {
		//TODO ???
	}
	
	private void handleStartDocument (Event event) {
		try {
			srcTempFile = null;
			trgTempFile = null;

			StartDocument sd = (StartDocument)event.getResource();
			srcOutputPath = baseOutputPath + "." + sd.getLocale().toBCP47().toLowerCase(); 
			trgOutputPath = baseOutputPath + "." + trgLoc.toBCP47().toLowerCase();
			
			//--- Create the source output stream
			boolean useTemp = false;
			File f = new File(srcOutputPath);
			if ( f.exists() ) {
				// If the file exists, try to remove
				useTemp = !f.delete();
			}
			if ( useTemp ) {
				// Use a temporary output if we can overwrite for now
				// If it's the input file, IFilter.close() will free it before we
				// call close() here (that is if IFilter.close() is called correctly
				srcTempFile = File.createTempFile("gfwTmp", null);
				srcOutputStream = new BufferedOutputStream(new FileOutputStream(srcTempFile.getAbsolutePath()));
			}
			else { // Make sure the directory exists
				Util.createDirectories(srcOutputPath);
				srcOutputStream = new BufferedOutputStream(new FileOutputStream(srcOutputPath));
			}
			// Create the output, always UTF-8 and without BOM
			srcWriter = new OutputStreamWriter(srcOutputStream, "UTF-8");

			//--- Create the source output stream
			useTemp = false;
			f = new File(trgOutputPath);
			if ( f.exists() ) {
				// If the file exists, try to remove
				useTemp = !f.delete();
			}
			if ( useTemp ) {
				// Use a temporary output if we can overwrite for now
				// If it's the input file, IFilter.close() will free it before we
				// call close() here (that is if IFilter.close() is called correctly
				trgTempFile = File.createTempFile("gfwTmp", null);
				trgOutputStream = new BufferedOutputStream(new FileOutputStream(trgTempFile.getAbsolutePath()));
			}
			else { // Make sure the directory exists
				Util.createDirectories(trgOutputPath);
				trgOutputStream = new BufferedOutputStream(new FileOutputStream(trgOutputPath));
			}
			// Create the output, always UTF-8 and without BOM
			trgWriter = new OutputStreamWriter(trgOutputStream, "UTF-8");
		}
		catch ( FileNotFoundException e ) {
			throw new RuntimeException(e);
		}
		catch ( IOException e ) {
			throw new RuntimeException(e);
		}
	}
	
	private void handleTextUnit (Event event) {
		ITextUnit tu = event.getTextUnit();
		if ( !tu.isTranslatable() ) return;

		TextContainer srcCont = tu.getSource();
		TextContainer trgCont = tu.getTarget(trgLoc);
		if ( trgCont == null ) {
			// Use an empty target when we have none
			trgCont = new TextContainer("");
		}

		// If not segmented: use the whole entry
		if ( !srcCont.contentIsOneSegment() ) {
			writeLine(srcCont.getFirstContent(), trgCont.getFirstContent());
			return;
		}

		// Else: go by segments
		ISegments trgSegs = trgCont.getSegments();
		for ( Segment srcSeg : srcCont.getSegments() ) {
			Segment trgSeg = trgSegs.get(srcSeg.id);
			if ( trgSeg == null ) {
				// Use an empty target segment if we have none 
				trgSeg = new Segment();
			}
			writeLine(srcSeg.text, trgSeg.text);
		}
	}

	private void writeLine (TextFragment srcFrag,
		TextFragment trgFrag)
	{
		try {
			srcWriter.write(format(srcFrag));
			srcWriter.write(linebreak);
			
			trgWriter.write(format(trgFrag));
			trgWriter.write(linebreak);
		}
		catch ( IOException e ) {
			throw new OkapiIOException("Error writing line.", e);
		}
	}

	private String format (TextFragment frag) {
		// Strip the inline codes
		return TextUnitUtil.getText(frag);
	}

}
