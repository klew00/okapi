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
import net.sf.okapi.common.resource.Segment;
import net.sf.okapi.common.resource.TextContainer;
import net.sf.okapi.common.resource.TextPart;
import net.sf.okapi.common.resource.ITextUnit;
import net.sf.okapi.common.resource.TextFragment;
import net.sf.okapi.filters.rainbowkit.Manifest;
import net.sf.okapi.filters.rainbowkit.MergingInfo;
import net.sf.okapi.lib.xliff.Alternate;
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
		setTMXInfo(false, null, null, null, null);
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
		writer.create(new File(path));
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
		writer.writeStartFile();
	}
	
	@Override
	protected void processEndSubDocument (Event event) {
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
		for ( int i=0; i<srcTc.count(); i++ ) {
			part = srcTc.get(i);
			if ( part.isSegment() ) {
				Segment srcSeg = (Segment)part;
				net.sf.okapi.lib.xliff.Segment xSeg = new net.sf.okapi.lib.xliff.Segment();
				unit.add(xSeg);
				xSeg.setSource(toXLIFF2Fragment(srcSeg.text));
				xSeg.setId(srcSeg.getId());
				
				// Target
				if ( trgTc != null ) {
					Segment trgSeg = trgTc.getSegments().get(xSeg.getId());
					if ( trgSeg != null ) {
						xSeg.setTarget(toXLIFF2Fragment(trgSeg.text));
						// Check if the order is the same as the source
						if (( i >= trgTc.count() ) || ( !trgTc.get(i).equals(trgSeg) )) {
							// Target is cross=aligned
							xSeg.setTargetOrder(i);
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
				Part xPart = new net.sf.okapi.lib.xliff.Part();
				unit.add(xPart);
				xPart.setSource(toXLIFF2Fragment(part.text));
				// Target
				if ( trgTc != null ) {
					
				}
			}
		}
		
		return unit;
	}
	
	private void copyData (AltTranslation alt,
		TextFragment oriSource,
		net.sf.okapi.lib.xliff.Segment xSeg)
	{
		Fragment src = null;
		Fragment trg = null;
		if ( alt.getSource().isEmpty() ) {
			// Same as the base source
			src = toXLIFF2Fragment(oriSource);
		}
		else {
			src = toXLIFF2Fragment(alt.getSource().getFirstContent());
		}
		trg = toXLIFF2Fragment(alt.getTarget().getFirstContent());
		xSeg.addCandidate(new Alternate(src, trg));
	}
	
	private Fragment toXLIFF2Fragment (TextFragment tf) {
		// fast track for content without codes
		if ( !tf.hasCode() ) {
			return new Fragment(tf.getCodedText());
		}
		
		// Otherwise: we map the codes
		Fragment frag = new Fragment();
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
					frag.append(net.sf.okapi.lib.xliff.Code.TYPE.OPENING, code.getData());
					break;
				case CLOSING:
					frag.append(net.sf.okapi.lib.xliff.Code.TYPE.CLOSING, code.getData());
					break;
				case PLACEHOLDER:
					frag.append(net.sf.okapi.lib.xliff.Code.TYPE.PLACEHOLDER, code.getData());
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
