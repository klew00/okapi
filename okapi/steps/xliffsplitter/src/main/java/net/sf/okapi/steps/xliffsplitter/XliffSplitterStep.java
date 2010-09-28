/*===========================================================================
  Copyright (C) 2010 by the Okapi Framework contributors
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

package net.sf.okapi.steps.xliffsplitter;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.htmlparser.jericho.Attributes;
import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.OutputDocument;
import net.htmlparser.jericho.Source;
import net.htmlparser.jericho.StartTag;
import net.sf.okapi.common.Event;
import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.UsingParameters;
import net.sf.okapi.common.Util;
import net.sf.okapi.common.exceptions.OkapiFileNotFoundException;
import net.sf.okapi.common.exceptions.OkapiIOException;
import net.sf.okapi.common.pipeline.BasePipelineStep;
import net.sf.okapi.common.pipeline.annotations.StepParameterMapping;
import net.sf.okapi.common.pipeline.annotations.StepParameterType;
import net.sf.okapi.common.resource.RawDocument;

/**
 * Splits a single XLIFF file into multiple files, split on the file element. All other content (outside the file
 * element) is copied as-is to each split file. Expects a {@link RawDocument} as input and sends the {@link RawDocument}
 * {@link Event} unaltered. Will output multiple split XLIFF files in the set output path.
 * An XLIFF file with only one file element is written out unaltered.
 *
 * @author Greg Perkins
 * @author HargraveJE
 * 
 */
@UsingParameters(Parameters.class)
public class XliffSplitterStep extends BasePipelineStep {

	private Parameters params;
	private boolean done = false;
	private URI outputURI;

	public XliffSplitterStep() {
		params = new Parameters();
	}

	@StepParameterMapping(parameterType = StepParameterType.OUTPUT_URI)
	public void setOutputURI(final URI outputURI) {
		this.outputURI = outputURI;
	}

	@Override
	public String getDescription() {
		return "Split an XLIFF document into separate files for each <file> element. Expects: raw document. Sends back: raw document.";
	}

	@Override
	public String getName() {
		return "XLIFF Splitter";
	}

	@Override
	public IParameters getParameters() {
		return params;
	}

	@Override
	public void setParameters(final IParameters params) {
		this.params = (Parameters) params;
	}

	@Override
	protected Event handleStartBatch(final Event event) {
		done = true;
		return event;
	}

	@Override
	protected Event handleStartBatchItem(final Event event) {
		done = false;
		return event;
	}

	@Override
	public boolean isDone() {
		return done;
	}

	@Override
	protected Event handleRawDocument(final Event event) {
		final RawDocument rawDoc = event.getRawDocument();
		final List<Element> fileElements = new ArrayList<Element>();

		Source source;
		try {
			source = new Source(rawDoc.getReader());
		} catch (final IOException e) {
			throw new OkapiIOException("Error creating Jericho Source object", e);
		}

		// Find all <file> elements
		// TODO: Are there any <file> elements that aren't children of <xliff>? Don't want to get too many.
		fileElements.addAll(source.getAllElements("file"));

		// Mark the insertion point
		final int insertPosition = fileElements.get(0).getBegin();

		// Write out a separate xliff file for each <file>
		int count = 1;
		for (final Element element : fileElements) {
			// Create an output document for modification and writing
			final OutputDocument skeletonDocument = new OutputDocument(source);

			// Remove all <file> elements from the document
			skeletonDocument.remove(fileElements);

			// Update the translation status in the current <file>
			String file;
			if (params.isUpdateSDLTranslationStatus()) {
				file = updateTranslationStatus(element.toString());
			} else {
				file = element.toString();
			}

			// Add the <file> element
			skeletonDocument.insert(insertPosition, file);

			String filename = Util.getDirectoryName(outputURI.getPath()) + File.separator
					+ Util.getFilename(outputURI.getPath(), false);
			filename += "." + String.valueOf(count++) + Util.getExtension(outputURI.getPath());

			PrintWriter writer = null;
			try {
				// Open a writer for the new file
				Util.createDirectories(filename);
				final OutputStream output = new BufferedOutputStream(new FileOutputStream(filename));
				writer = new PrintWriter(new OutputStreamWriter(output, "UTF-8"));
				skeletonDocument.writeTo(writer);
			} catch (final IOException e) {
				throw new OkapiIOException(e);
			} catch (final NullPointerException e) {
				throw new OkapiFileNotFoundException(e);
			} finally {
				done = true;
				if (writer != null) {
					writer.close();
					writer = null;
				}
			}
		}

		return event;
	}

	private String updateTranslationStatus(final String file) {
		// lets now take the XLIFF with a single file element and update some attributes
		// TODO: is there an easier way than to re-parse the new document?
		final Source s = new Source(file);
		// Create an output document for modification and writing
		final OutputDocument outputFile = new OutputDocument(s);

		// change all translation_type to <iws:status translation_type="manual_translation">
		final List<Element> segment_metadataElements = s.getAllElements("iws:segment-metadata");
		for (final Element segment_metadata : segment_metadataElements) {
			final List<StartTag> statusTags = segment_metadata.getAllStartTags("iws:status");
			// should only be one status tag - but just in case
			for (final StartTag statusTag : statusTags) {
				final Attributes attributes = statusTag.getAttributes();
				final Map<String, String> attributesMap = outputFile.replace(attributes, true);
				attributesMap.put("translation_type", "manual_translation");
				attributesMap.put("translation_status", "finished");
				attributesMap.remove("target_content");
			}
		}

		return outputFile.toString();
	}
}
