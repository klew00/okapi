/*===========================================================================
  Copyright (C) 2009-2010 by the Okapi Framework contributors
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
import java.net.URI;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.UsingParameters;
import net.sf.okapi.common.Util;
import net.sf.okapi.common.exceptions.OkapiIOException;
import net.sf.okapi.common.pipeline.BasePipelineStep;
import net.sf.okapi.common.pipeline.annotations.StepParameterMapping;
import net.sf.okapi.common.pipeline.annotations.StepParameterType;
import net.sf.okapi.common.resource.RawDocument;

/**
 * Writes a {@link RawDocument} to an output file.
 * This class implements the {@link net.sf.okapi.common.pipeline.IPipelineStep}
 * interface for a step that takes a {@link RawDocument} and creates an output 
 * file from it. The generated file is passed on through a new {@link RawDocument}.
 */
@UsingParameters() // No parameters
public class RawDocumentWriterStep extends BasePipelineStep {
	private URI outputURI;
	
	/**
	 * Creates a new RawDocumentWriterStep object.
	 * This constructor is needed to be able to instantiate an object from newInstance()
	 */
	public RawDocumentWriterStep () {
	}
	
	@StepParameterMapping(parameterType = StepParameterType.OUTPUT_URI)
	public void setOutputURI (URI outputURI) {
		this.outputURI = outputURI;
	}
	
	@Override
	public String getDescription() {
		return "Write a RawDocument to an output file.";
	}

	@Override
	public String getName () {
		return "RawDocument Writer";
	}

	@Override
	protected Event handleRawDocument (Event event) {
		RawDocument rawDoc = (RawDocument)event.getResource();
		try {
			File outFile;
			rawDoc = (RawDocument)event.getResource();
			
			if ( isLastOutputStep() ) {
				outFile = new File(outputURI);
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
			Util.copy(rawDoc.getStream(), outFile);
				
			// Set the new raw-document URI and the encoding (in case one was auto-detected)
			// Other info stays the same
			event.setResource(new RawDocument(outFile.toURI(), rawDoc.getEncoding(), 
					rawDoc.getSourceLocale(), rawDoc.getTargetLocale()));
		}
		catch ( Throwable e ) {
			throw new OkapiIOException("Error writing or copying a RawDocument.", e);
		}
		return event;
	}

}
