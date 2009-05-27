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

package net.sf.okapi.steps.common;

import java.io.File;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.Util;
import net.sf.okapi.common.exceptions.OkapiBadStepInputException;
import net.sf.okapi.common.exceptions.OkapiIOException;
import net.sf.okapi.common.pipeline.BasePipelineStep;
import net.sf.okapi.common.resource.RawDocument;

/**
 * Writes a {@link RawDocument} to an output file.
 * This class implements the {@link net.sf.okapi.common.pipeline.IPipelineStep}
 * interface for a step that takes a {@link RawDocument} and creates an output 
 * file from it. The generated file is passed on through a new {@link RawDocument}.
 */
public class RawDocumentWriterStep extends BasePipelineStep {

	private boolean isDone;
	
	/**
	 * Creates a new RawDocumentWriterStep object.
	 * This constructor is needed to be able to instantiate an object from newInstance()
	 */
	public RawDocumentWriterStep () {
	}
	
	public String getDescription() {
		return "Write a raw document to an output file";
	}

	public String getName () {
		return "RawDocument Writer";
	}

	@Override
	public boolean needsOutput (int inputIndex) {
		return pipeline.isLastStep(this);
	}

	@Override
	public boolean isDone () {
		return isDone;
	}

	@Override
	protected void handleStartBatch (Event event) {
		isDone = true;
	}
	
	@Override
	protected void handleStartBatchItem (Event event) {
		isDone = false;
	}

	@Override
	protected void handleRawDocument (Event event) {
		RawDocument rawDoc = (RawDocument)event.getResource();
		try {
			File outFile;
			rawDoc = (RawDocument)event.getResource();
			
			if ( rawDoc.getInputCharSequence() != null ) {
				//TODO
				throw new RuntimeException("Not implemented yet");
			}
			else if ( rawDoc.getInputURI() != null ) {
				if ( pipeline.isLastStep(this) ) {
					outFile = new File(getContext().getOutputURI(0));
					Util.createDirectories(outFile.getAbsolutePath());
				}
				else {
					try {
						outFile = File.createTempFile("okp-rdw_", ".tmp");
					}
					catch ( Throwable e ) {
						throw new OkapiIOException("Cannot create temporary output.", e);
					}
					outFile.deleteOnExit();
				}
				// Faster to copy using channels
				String inputPath = rawDoc.getInputURI().getPath();
				Util.copyFile(inputPath, outFile.getAbsolutePath(), false); // Copy, do not move
			}
			else if ( rawDoc.getInputStream() != null ) {
				throw new RuntimeException("Not implemented yet");
			}
			else {
				// Change this exception to more generic (not just filter)
				throw new OkapiBadStepInputException("RawDocument has no input defined.");
			}
				
			// Set the new raw-document URI and the encoding (in case one was auto-detected)
			// Other info stays the same
			rawDoc.setInputURI(outFile.toURI());
			isDone = true;
		}
		catch ( Throwable e ) {
			throw new OkapiIOException("Error writing or copying a RawDocument.", e);
		}
	}

}
