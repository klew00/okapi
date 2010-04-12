/*===========================================================================
  Copyright (C) 2008-2010 by the Okapi Framework contributors
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

package net.sf.okapi.filters.ttx;

import net.sf.okapi.common.MimeTypeMapper;
import net.sf.okapi.common.Util;
import net.sf.okapi.common.annotation.ScoreInfo;
import net.sf.okapi.common.annotation.ScoresAnnotation;
import net.sf.okapi.common.encoder.EncoderManager;
import net.sf.okapi.common.resource.Code;
import net.sf.okapi.common.resource.Segment;
import net.sf.okapi.common.resource.TextContainer;
import net.sf.okapi.common.resource.TextFragment;
import net.sf.okapi.common.resource.TextPart;
import net.sf.okapi.common.resource.TextUnit;
import net.sf.okapi.common.skeleton.GenericSkeletonWriter;

public class TTXSkeletonWriter extends GenericSkeletonWriter {

	private String srcLangCode;
	private String trgLangCode;
	private boolean forceSegmentedOutput;
	
	public TTXSkeletonWriter (boolean forceSegments) {
		forceSegmentedOutput = forceSegments;
	}
	
	public void setSourceLanguageCode (String langCode) {
		srcLangCode = langCode;
	}
	
	public void setTargetLanguageCode (String langCode) {
		trgLangCode = langCode;
	}
	
	@Override
	public String processTextUnit (TextUnit tu) {
		if ( tu == null ) return "";
		// Update the encoder from the TU's MIME type
		if ( encoderManager != null ) {
			encoderManager.updateEncoder(tu.getMimeType());
		}

		StringBuilder tmp = new StringBuilder();

		TextContainer srcCont = tu.getSource();
		if ( forceSegmentedOutput && !srcCont.hasBeenSegmented() ) {
			// Work from a clone if we need to change the segmentation
			srcCont = srcCont.clone();
			srcCont.setHasBeenSegmentedFlag(true);
		}
		
		TextContainer trgCont;
		ScoresAnnotation scores = null;
		if ( tu.hasTarget(outputLoc) ) {
			trgCont = tu.getTarget(outputLoc);
			scores = trgCont.getAnnotation(ScoresAnnotation.class);
			if ( forceSegmentedOutput && !trgCont.hasBeenSegmented() ) {
				trgCont = trgCont.clone();
				trgCont.setHasBeenSegmentedFlag(true);
			}
		}
		else { // Fall back to source if we have no target
			trgCont = srcCont;
		}
		
		// Drive from target
		int i = 0;
		for ( TextPart part : trgCont ) {
			if ( part.isSegment() ) {
				Segment trgSeg = (Segment)part;
				Segment srcSeg = srcCont.getSegment(trgSeg.id);
				if ( srcSeg == null ) {
					//TODO: Warning
					// Fall back to the target
					srcSeg = trgSeg;
				}
				ScoreInfo si = null;
				if ( scores != null ) {
					si = scores.get(i);
				}
				if ( trgCont.hasBeenSegmented() ) {
					tmp.append(processSegment(srcSeg.text, trgSeg.text, si));
				}
				else {
					tmp.append(processFragment(part.getContent()));
				}
			}
			else {
				tmp.append(processFragment(part.getContent()));
			}
			i++;
		}
		
//		//==================
//		TextContainer srcCont = tu.getSource();
//		TextFragment srcFrag;
//		if ( forceSegmentedOutput && !srcCont.isSegmented() ) {
//			srcCont = srcCont.clone();
//			srcCont.createSegment(0, -1);
//		}
//		List<Segment> srcSegments = srcCont.getSegments();
//		String text = srcCont.getCodedText();
//
//		ScoresAnnotation scores = null;
//		TextFragment trgFrag;
//		List<Segment> trgSegments = null;
//		if ( tu.hasTarget(outputLoc) ) {
//			TextContainer trgCont = tu.getTarget(outputLoc);
//			scores = trgCont.getAnnotation(ScoresAnnotation.class);
//			if ( !trgCont.isSegmented() ) {
//				trgCont = trgCont.clone();
//				trgCont.createSegment(0, -1);
//			}
//			trgSegments = trgCont.getSegments();
//		}
//		else {
//			trgSegments = srcSegments;
//		}
//
//		Code code;
//		for ( int i=0; i<text.length(); i++ ) {
//			switch ( text.charAt(i) ) {
//			case TextFragment.MARKER_ISOLATED:
//			case TextFragment.MARKER_OPENING:
//			case TextFragment.MARKER_CLOSING:
//				tmp.append(expandCode(srcCont.getCode(text.charAt(++i))));
//				break;
//				
//			case TextFragment.MARKER_SEGMENT:
//				code = srcCont.getCode(text.charAt(++i));
//				int n = Integer.valueOf(code.getData());
//				// Get segments source/target
//				srcFrag = srcSegments.get(n).text;
//				trgFrag = trgSegments.get(n).text;
//				// Get score info if possible
//				ScoreInfo si = null;
//				if ( scores != null ) {
//					si = scores.get(n);
//				}
//				tmp.append(processSegment(srcFrag, trgFrag, si));
//				break;
//
//			default:
//				tmp.append(encoderManager.encode(text.charAt(i), 0));
//				break;
//			}
//		}

		return tmp.toString();
	}
	
	private String processSegment (TextFragment srcFrag,
		TextFragment trgFrag,
		ScoreInfo scoreInfo)
	{
		if ( trgFrag == null ) { // No target available: use the source
			trgFrag = srcFrag;
		}

		StringBuilder tmp = new StringBuilder();

		if ( scoreInfo != null ) {
			if ( !Util.isEmpty(scoreInfo.origin) ) {
				tmp.append("<Tu Origin=\""+scoreInfo.origin+"\" ");
			}
			else {
				tmp.append("<Tu ");
			}
			tmp.append(String.format("MatchPercent=\"%d\">", scoreInfo.score));
		}
		else {
			tmp.append("<Tu MatchPercent=\"0\">");
		}
		
		tmp.append(String.format("<Tuv Lang=\"%s\">", srcLangCode));
		tmp.append(processFragment(srcFrag));
		tmp.append("</Tuv>");
		
		tmp.append(String.format("<Tuv Lang=\"%s\">", trgLangCode));
		tmp.append(processFragment(trgFrag));
		tmp.append("</Tuv>");

		tmp.append("</Tu>");
		return tmp.toString();
	}

	/**
	 * Verifies that this skeleton writer can be used for internal purpose by the TTXFilter
	 * for outputting skeleton chunks.
	 * @param lineBreak the type of line-break to use.
	 */
	protected void checkForFilterInternalUse (String lineBreak) {
		if ( encoderManager == null ) {
			encoderManager = new EncoderManager();
			encoderManager.setMapping(MimeTypeMapper.TTX_MIME_TYPE, "net.sf.okapi.common.encoder.XMLEncoder");
			encoderManager.setDefaultOptions(null, "US-ASCII", lineBreak); // Make sure we escape extended
			encoderManager.updateEncoder(MimeTypeMapper.TTX_MIME_TYPE);
		}
	}
	
	protected String processFragment (TextFragment frag) {
		StringBuilder tmp = new StringBuilder();
		String text = frag.getCodedText();

		// No MARKER_SEGMENT at this stage
		for ( int i=0; i<text.length(); i++ ) {
			char ch = text.charAt(i);
			switch ( ch ) {
			case TextFragment.MARKER_ISOLATED:
			case TextFragment.MARKER_OPENING:
			case TextFragment.MARKER_CLOSING:
				tmp.append(expandCode(frag.getCode(text.charAt(++i))));
				continue;
			default:
				tmp.append(encoderManager.encode(ch, 0));
				continue;
			}
		}
		
		return tmp.toString(); 
	}

	private String expandCode (Code code) {
		if ( layer != null ) {
			return layer.startInline() 
				+ layer.encode(code.getOuterData(), 2)
				+ layer.endInline();
		}
		return code.getOuterData();
	}

}