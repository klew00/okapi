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

package net.sf.okapi.steps.simplekit.rtf;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.security.InvalidParameterException;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.Util;
import net.sf.okapi.common.exceptions.OkapiIOException;
import net.sf.okapi.common.filterwriter.ILayerProvider;
import net.sf.okapi.common.resource.StartDocument;
import net.sf.okapi.common.resource.StartSubDocument;
import net.sf.okapi.common.skeleton.GenericSkeletonWriter;
import net.sf.okapi.common.skeleton.ISkeletonWriter;
import net.sf.okapi.filters.simplekit.Manifest;
import net.sf.okapi.filters.simplekit.MergingInfo;
import net.sf.okapi.steps.simplekit.common.BasePackageWriter;

public class RTFPackageWriter extends BasePackageWriter {

	private ILayerProvider layer;
	private PrintWriter writer;

	public RTFPackageWriter () {
		super(Manifest.EXTRACTIONTYPE_RTF);
	}
	
	@Override
	public void setDocumentInformation (String relativeInputPath,
		String filterConfigId,
		String filterParameters,
		String inputEncoding,
		String relativeTargetPath,
		String targetEncoding,
		ISkeletonWriter skelWriter)
	{
		super.setDocumentInformation(relativeInputPath, filterConfigId, filterParameters,
			inputEncoding, relativeTargetPath, targetEncoding, skelWriter);

		//TODO: Fix encoding so we use a windows-enabled one (especially for UTF-16)
		layer = new LayerProvider();
		layer.setOptions(null, targetEncoding, null);
	}
	
	@Override
	protected void processStartBatch () {
		manifest.setSubDirectories("original", "work", "work", "done");
		super.processStartBatch();
	}
	
	@Override
	protected void processStartDocument (Event event) {
		super.processStartDocument(event);
		
		if ( skelWriter == null ) {
			throw new InvalidParameterException("You cannot use the RTF writer with no skeleton writer.\n"
				+ "The filter you are trying to use may be incompatible with an RTF output.");
		}
		// Keep 2 copies of the referents for RTF: source and target
		if ( skelWriter instanceof GenericSkeletonWriter ) {
			((GenericSkeletonWriter)this.skelWriter).setReferentCopies(2);
		}
		
		MergingInfo item = manifest.getItem(docId);
		String path = manifest.getSourceDirectory() + item.getRelativeInputPath() + ".rtf";

		StartDocument sd = event.getStartDocument();
		try {
			Util.createDirectories(path);
			writer = new PrintWriter(path, item.getTargetEncoding());
		}
		catch ( FileNotFoundException e ) {
			throw new OkapiIOException("Error creating RTF writer.", e);
		}
		catch ( UnsupportedEncodingException e ) {
			throw new OkapiIOException("Error creating RTF writer.", e);
		}

		//TODO: change codepage
		// Write RTF header and stylesheet
		writer.write("{\\rtf1\\ansi\\ansicpg" + "1252" + "\\uc1\\deff1 \n"+
			"{\\fonttbl \n"+
			"{\\f1 \\fmodern\\fcharset0\\fprq1 Courier New;}\n"+
			"{\\f2 \\fswiss\\fcharset0\\fprq2 Arial;}\n"+
			"{\\f3 \\froman\\fcharset0\\fprq2 Times New Roman;}}\n"+
			"{\\colortbl \\red0\\green0\\blue0;\\red0\\green0\\blue0;\\red0\\green0\\blue255;"+
			"\\red0\\green255\\blue255;\\red0\\green255\\blue0;\\red255\\green0\\blue255;"+
			"\\red255\\green0\\blue0;\\red255\\green255\\blue0;\\red255\\green255\\blue255;"+
			"\\red0\\green0\\blue128;\\red0\\green128\\blue128;\\red0\\green128\\blue0;"+
			"\\red128\\green0\\blue128;\\red128\\green0\\blue0;\\red128\\green128\\blue0;"+
			"\\red128\\green128\\blue128;\\red192\\green192\\blue192;}\n"+
			"{\\stylesheet \n"+
			"{\\s0 \\sb80\\slmult1\\widctlpar\\fs20\\f1 \\snext0 Normal;}\n"+
			"{\\cs1 \\additive \\v\\cf12\\sub\\f1 tw4winMark;}\n"+
			"{\\cs2 \\additive \\cf4\\fs40\\f1 tw4winError;}\n"+
			"{\\cs3 \\additive \\f1\\cf11 tw4winPopup;}\n"+
			"{\\cs4 \\additive \\f1\\cf10 tw4winJump;}\n"+
			"{\\cs5 \\additive \\cf15\\f1\\lang1024\\noproof tw4winExternal;}\n"+
			"{\\cs6 \\additive \\cf6\\f1\\lang1024\\noproof tw4winInternal;}\n"+
			"{\\cs7 \\additive \\cf2 tw4winTerm;}\n"+
			"{\\cs8 \\additive \\cf13\\f1\\lang1024\\noproof DO_NOT_TRANSLATE;}\n"+
			"{\\cs9 \\additive Default Paragraph Font;}"+
			"{\\cs15 \\additive \\v\\f1\\cf12\\sub tw4winMark;}"+
			"}\n"+
			"\\paperw11907\\paperh16840\\viewkind4\\viewscale100\\pard\\plain\\s0\\sb80\\slmult1\\widctlpar\\fs20\\f1 \n"+
			Util.RTF_STARTCODE);

		// Write the skeleton
		writer.write(skelWriter.processStartDocument(manifest.getTargetLocale(), item.getTargetEncoding(),
			layer, sd.getFilterWriter().getEncoderManager(), sd));
	}
	
	@Override
	protected void processEndDocument (Event event) {
		writer.write(skelWriter.processEndDocument(event.getEnding()));
		writer.write(Util.RTF_ENDCODE+"}\n");
		writer.close();
		
		// Call the base method, in case there is something common to do
		super.processEndDocument(event);
	}

	@Override
	protected void processStartSubDocument (Event event) {
		writer.write(skelWriter.processStartSubDocument((StartSubDocument)event.getResource()));
	}
	
	@Override
	protected void processEndSubDocument (Event event) {
		writer.write(skelWriter.processEndSubDocument(event.getEnding()));
	}
	
	@Override
	protected void processTextUnit (Event event) {
		// Write skeleton and its content
		writer.write(skelWriter.processTextUnit(event.getTextUnit()));
	}

	@Override
	protected void processStartGroup (Event event) {
		writer.write(skelWriter.processStartGroup(event.getStartGroup()));
	}

	@Override
	protected void processEndGroup (Event event) {
		writer.write(skelWriter.processEndGroup(event.getEnding()));
	}

	protected void processDocumentPart (Event event) {
		writer.write(skelWriter.processDocumentPart(event.getDocumentPart()));
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
