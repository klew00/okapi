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

package net.sf.okapi.steps.rainbowkit.xliff;

import java.io.File;
import java.util.List;
import java.util.logging.Logger;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.Util;
import net.sf.okapi.common.annotation.AltTranslation;
import net.sf.okapi.common.annotation.AltTranslationsAnnotation;
import net.sf.okapi.common.resource.Code;
import net.sf.okapi.common.resource.ISegments;
import net.sf.okapi.common.resource.Segment;
import net.sf.okapi.common.resource.TextContainer;
import net.sf.okapi.common.resource.TextPart;
import net.sf.okapi.common.resource.ITextUnit;
import net.sf.okapi.common.resource.TextFragment;
import net.sf.okapi.filters.rainbowkit.Manifest;
import net.sf.okapi.filters.rainbowkit.MergingInfo;
import net.sf.okapi.lib.xliff.Alternate;
import net.sf.okapi.lib.xliff.CodesStore;
import net.sf.okapi.lib.xliff.Fragment;
import net.sf.okapi.lib.xliff.Part;
import net.sf.okapi.lib.xliff.Unit;
import net.sf.okapi.lib.xliff.XLIFFWriter;
import net.sf.okapi.steps.rainbowkit.common.BasePackageWriter;

public class XLIFF2PackageWriter extends BasePackageWriter {

	private static final Logger LOGGER = Logger.getLogger(XLIFF2PackageWriter.class.getName());
	private XLIFFWriter writer;

	public XLIFF2PackageWriter () {
		super(Manifest.EXTRACTIONTYPE_XLIFF2);
	}

	@Override
	protected void processStartBatch () {
		manifest.setSubDirectories("original", "work", "work", "done", null, false);
		setTMXInfo(false, null, null, null, null, false);
		super.processStartBatch();
	}
	
	@Override
	protected void processStartDocument (Event event) {
		super.processStartDocument(event);
		
		writer = new XLIFFWriter();

//		writer.setOptions(manifest.getTargetLocale(), "UTF-8");
		MergingInfo item = manifest.getItem(docId);
		String path = manifest.getSourceDirectory() + item.getRelativeInputPath() + ".xlf";
		
		// Set the writer's options
		// Get the options from the parameters
		XLIFF2Options options = new XLIFF2Options();
		if ( !Util.isEmpty(params.getWriterOptions()) ) {
			options.fromString(params.getWriterOptions());
		}
		writer.setInlineStyle(options.getInlineStyle());
		
//		StartDocument sd = event.getStartDocument();
//		writer.create(path, null, manifest.getSourceLocale(), manifest.getTargetLocale(),
//			sd.getMimeType(), item.getRelativeInputPath(), null);
		writer.create(new File(path), manifest.getSourceLocale().toBCP47());
		writer.setLanguages(manifest.getSourceLocale().toBCP47(), manifest.getTargetLocale().toBCP47());
		writer.setIsIndented(true);
		writer.writeStartDocument();
	}
	
	@Override
	protected void processEndDocument (Event event) {
		writer.writeEndDocument();
		writer.close();
		writer = null;
		super.processEndDocument(event);
	}

	@Override
	protected void processStartSubDocument (Event event) {
		// Do not start one explicitly
		// Let the first unit to trigger the start of the file
		// otherwise we may get empty file elements
		// writer.writeStartFile();
	}
	
	@Override
	protected void processEndSubDocument (Event event) {
		// Safe to call even if writestartFile(0 was not called
		writer.writeEndFile();
	}
	
	@Override
	protected void processStartGroup (Event event) {
		writer.writeStartGroup();
	}
	
	@Override
	protected void processEndGroup (Event event) {
		writer.writeEndGroup();
	}
	
	@Override
	protected void processTextUnit (Event event) {
		
		Unit unit = toXLIFF2Unit(event.getTextUnit());
		writer.writeUnit(unit);
		
//		event = writer.handleEvent(event);
		writeTMXEntries(event.getTextUnit());
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

	private Unit toXLIFF2Unit (ITextUnit tu) {
		Unit unit = new Unit(tu.getId());

		TextContainer srcTc = tu.getSource();
		TextContainer trgTc = null;
		if ( tu.hasTarget(manifest.getTargetLocale()) ) {
			trgTc = tu.getTarget(manifest.getTargetLocale());
			if ( trgTc.getSegments().count() != srcTc.getSegments().count() ) {
				// Use un-segmented entry if we have different number of segments
				LOGGER.warning(String.format("Text unit id='%s' has different number of segments in source and target.\n"
					+"This entry will be output un-segmented.", tu.getId()));
				srcTc = tu.getSource().clone(); srcTc.joinAll();
				trgTc = tu.getTarget(manifest.getTargetLocale()).clone(); trgTc.joinAll();
			}
		}

		// Go through the parts: Use the source to drive the order
		// But match on segment ids
		TextPart part;
		ISegments trgSegs = null;
		if ( trgTc != null ) {
			trgSegs = trgTc.getSegments();
		}
		int srcSegIndex = -1;
		for ( int i=0; i<srcTc.count(); i++ ) {
			part = srcTc.get(i);
			if ( part.isSegment() ) {
				Segment srcSeg = (Segment)part;
				srcSegIndex++;
				net.sf.okapi.lib.xliff.Segment xSeg = unit.appendNewSegment();
				xSeg.setSource(toXLIFF2Fragment(srcSeg.text, unit.getCodesStore(), false));
				xSeg.setId(srcSeg.getId());
				
				// Target
				if ( trgSegs != null ) {
					Segment trgSeg = trgSegs.get(xSeg.getId());
					if ( trgSeg != null ) {
						xSeg.setTarget(toXLIFF2Fragment(trgSeg.text, unit.getCodesStore(), true));
						// Check if the order is the same as the source
						int trgSegIndex = trgSegs.getIndex(xSeg.getId());
						if ( srcSegIndex != trgSegIndex ) {
							// Target is cross-aligned
							int trgPartIndex = trgSegs.getPartIndex(trgSegIndex);
							xSeg.setTargetOrder(trgPartIndex+1);
						}
					}
					// Alt-trans annotation?
					AltTranslationsAnnotation ann = trgSeg.getAnnotation(AltTranslationsAnnotation.class);
					if ( ann != null ) {
						for ( AltTranslation alt : ann ) {
							copyData(alt, srcSeg.text, xSeg);
						}
					}
				}
			}
			else { // Non-segment part
				Part xPart = unit.appendNewIgnorable();
				xPart.setSource(toXLIFF2Fragment(part.text, unit.getCodesStore(), false));
				// Target
				if ( trgTc != null ) {
//todo				trcTc.get
				}
			}
		}
		
		return unit;
	}
	
	private void copyData (AltTranslation alt,
		TextFragment oriSource,
		net.sf.okapi.lib.xliff.Segment xSeg)
	{
		Alternate xAlt = new Alternate();
		CodesStore cs = xAlt.getCodesStore();
		if ( alt.getSource().isEmpty() ) { // Same as the base source
			xAlt.setSource(toXLIFF2Fragment(oriSource, cs, false));
		}
		else {
			xAlt.setSource(toXLIFF2Fragment(alt.getSource().getFirstContent(), cs, false));
			
		}
		xAlt.setTarget(toXLIFF2Fragment(alt.getTarget().getFirstContent(), cs, true));
		xSeg.addCandidate(xAlt);
	}
	
	private Fragment toXLIFF2Fragment (TextFragment tf,
		CodesStore store,
		boolean isTarget)
	{
		// fast track for content without codes
		if ( !tf.hasCode() ) {
			return new Fragment(store, isTarget, tf.getCodedText());
		}
		
		// Otherwise: we map the codes
		Fragment frag = new Fragment(store, isTarget);
		String ctext = tf.getCodedText();
		List<Code> codes = tf.getCodes();

		int index;
		Code code;
		for ( int i=0; i<ctext.length(); i++ ) {
			if ( TextFragment.isMarker(ctext.charAt(i)) ) {
				index = TextFragment.toIndex(ctext.charAt(++i));
				code = codes.get(index);
				switch ( code.getTagType() ) {
				case OPENING:
					frag.append(net.sf.okapi.lib.xliff.CodeType.OPENING, String.valueOf(code.getId()), code.getData());
					break;
				case CLOSING:
					frag.append(net.sf.okapi.lib.xliff.CodeType.CLOSING, String.valueOf(code.getId()), code.getData());
					break;
				case PLACEHOLDER:
					frag.append(net.sf.okapi.lib.xliff.CodeType.PLACEHOLDER, String.valueOf(code.getId()), code.getData());
					break;
				}
			}
			else {
				frag.append(ctext.charAt(i));
			}
		}

		return frag;
	}

}
