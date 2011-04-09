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

import net.sf.okapi.common.Event;
import net.sf.okapi.common.Util;
import net.sf.okapi.common.resource.Code;
import net.sf.okapi.common.resource.Segment;
import net.sf.okapi.common.resource.TextContainer;
import net.sf.okapi.common.resource.TextPart;
import net.sf.okapi.common.resource.TextUnit;
import net.sf.okapi.common.resource.TextFragment;
import net.sf.okapi.filters.rainbowkit.Manifest;
import net.sf.okapi.filters.rainbowkit.MergingInfo;
import net.sf.okapi.lib.xliff.Fragment;
import net.sf.okapi.lib.xliff.Unit;
import net.sf.okapi.lib.xliff.XLIFFWriter;
import net.sf.okapi.steps.rainbowkit.common.BasePackageWriter;

public class XLIFF2PackageWriter extends BasePackageWriter {

	private XLIFFWriter writer;

	public XLIFF2PackageWriter () {
		super(Manifest.EXTRACTIONTYPE_XLIFF2);
	}

	@Override
	protected void processStartBatch () {
		manifest.setSubDirectories("original", "work", "work", "done", null, false);
		setTMXInfo(true, null, null, null, null);
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
		Options options = new Options();
		if ( !Util.isEmpty(params.getWriterOptions()) ) {
			options.fromString(params.getWriterOptions());
		}
//		//TODO: Would be easier to use IParameters in XLIFFWriter.
//		writer.setPlaceholderMode(options.getPlaceholderMode());
//		writer.setCopySource(options.getCopySource());
//		writer.setIncludeAltTrans(options.getIncludeAltTrans());
//		writer.setSetApprovedasNoTranslate(options.getSetApprovedAsNoTranslate());
//		writer.setIncludeNoTranslate(options.getIncludeNoTranslate());
		
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
//		writer.handleEvent(event);
	}
	
	@Override
	protected void processEndSubDocument (Event event) {
//		writer.handleEvent(event);
	}
	
	@Override
	protected void processStartGroup (Event event) {
//		writer.handleEvent(event);
	}
	
	@Override
	protected void processEndGroup (Event event) {
//		writer.handleEvent(event);
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

	private Unit toXLIFF2Unit (TextUnit tu) {
		Unit unit = new Unit(tu.getId());

		TextContainer srcTc = tu.getSource();
		TextContainer trgTc = null;
		if ( tu.hasTarget(manifest.getTargetLocale()) ) {
			trgTc = tu.getTarget(manifest.getTargetLocale());
		}
		
		boolean afterFirst = false;
		boolean afterSeg = false;
		net.sf.okapi.lib.xliff.Segment xSeg = null;

		for ( TextPart part : srcTc ) {
			if ( part.isSegment() ) {
				if ( afterFirst ) {
					// New segment: push the current one and create a new one
					unit.add(xSeg);
				}
				else { // First segment
					afterFirst = true;
				}
				xSeg = new net.sf.okapi.lib.xliff.Segment();
				xSeg.setSource(toXLIFF2Fragment(part.text));
				afterSeg = true;
				xSeg.setId(((Segment)part).getId());
				// Target
				if ( trgTc != null ) {
					Segment trgSeg = trgTc.getSegments().get(xSeg.getId());
					if ( trgSeg != null ) {
						xSeg.setTarget(toXLIFF2Fragment(trgSeg.text));
					}
				}
			}
			else { // Non-segment part
				if ( xSeg == null ) {
					// First part
					afterFirst = true;
					xSeg = new net.sf.okapi.lib.xliff.Segment();
				}
				if ( afterSeg ) {
					xSeg.addAfter(toXLIFF2Fragment(part.text));
				}
				else {
					xSeg.addBefore(toXLIFF2Fragment(part.text));
				}
			}
		}
		
		if ( xSeg != null ) {
			unit.add(xSeg);
		}
		
		return unit;
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
			switch ( ctext.codePointAt(i) ) {
			case TextFragment.MARKER_OPENING:
				index = TextFragment.toIndex(ctext.charAt(++i));
				code = codes.get(index);
				frag.append(0, code.getData());
				break;
			case TextFragment.MARKER_CLOSING:
				index = TextFragment.toIndex(ctext.charAt(++i));
				code = codes.get(index);
				frag.append(1, code.getData());
				break;
			case TextFragment.MARKER_ISOLATED:
				index = TextFragment.toIndex(ctext.charAt(++i));
				code = codes.get(index);
				frag.append(2, code.getData());
				break;
			default:
				frag.append(ctext.charAt(i));
				break;
			}
		}

		return frag;
	}

}
