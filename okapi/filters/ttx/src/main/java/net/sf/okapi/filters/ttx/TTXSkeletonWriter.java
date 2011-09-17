/*===========================================================================
  Copyright (C) 2008-2011 by the Okapi Framework contributors
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
import net.sf.okapi.common.annotation.AltTranslation;
import net.sf.okapi.common.annotation.AltTranslationsAnnotation;
import net.sf.okapi.common.encoder.EncoderManager;
import net.sf.okapi.common.resource.Code;
import net.sf.okapi.common.resource.ITextUnit;
import net.sf.okapi.common.resource.Segment;
import net.sf.okapi.common.resource.TextContainer;
import net.sf.okapi.common.resource.TextFragment;
import net.sf.okapi.common.resource.TextPart;
import net.sf.okapi.common.skeleton.GenericSkeleton;
import net.sf.okapi.common.skeleton.GenericSkeletonWriter;

public class TTXSkeletonWriter extends GenericSkeletonWriter {

	private String srcLangCode;
	private String trgLangCode;
	
	public void setSourceLanguageCode (String langCode) {
		srcLangCode = langCode;
	}
	
	public void setTargetLanguageCode (String langCode) {
		trgLangCode = langCode;
	}
	
	@Override
	public String processTextUnit (ITextUnit tu) {
		if ( tu == null ) return "";
		// Update the encoder from the TU's MIME type
		if ( encoderManager != null ) {
			encoderManager.updateEncoder(tu.getMimeType());
		}

		StringBuilder tmp = new StringBuilder();
		// Write the possible part before the text-unit
		GenericSkeleton skel = (GenericSkeleton)tu.getSkeleton();
		if ( skel != null ) {
			if ( skel.getParts().size() > 1 ) {
				tmp.append(getString(skel.getParts().get(0), 1));
			}
		}
		
		TextContainer srcCont = tu.getSource();
		if ( !srcCont.hasBeenSegmented() ) {
			// Work from a clone if we need to change the segmentation
			srcCont = srcCont.clone();
			srcCont.setHasBeenSegmentedFlag(true);
		}
		
		TextContainer trgCont;
		if ( tu.hasTarget(outputLoc) ) {
			trgCont = tu.getTarget(outputLoc);
			if ( !trgCont.hasBeenSegmented() ) {
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
				Segment srcSeg = srcCont.getSegments().get(trgSeg.id);
				if ( srcSeg == null ) {
					//TODO: Warning
					// Fall back to the target
					srcSeg = trgSeg;
				}
				if ( trgSeg.text.isEmpty() && !srcSeg.text.isEmpty() ) {
					// Target has no content while source does: fall back to the source
					trgSeg.text = srcSeg.text.clone();
				}
				if ( trgCont.hasBeenSegmented() ) {
					AltTranslation altTrans = null;
					AltTranslationsAnnotation ann = trgSeg.getAnnotation(AltTranslationsAnnotation.class);
					if ( ann != null ) {
						altTrans = ann.getFirst();
					}
					tmp.append(processSegment(srcSeg.text, trgSeg.text, altTrans));
				}
				else {
// This should never be called now					
					tmp.append(processFragment(part.getContent(), 0)); // Normal text
				}
			}
			else { // Inter-segment parts
				tmp.append(processFragment(part.getContent(), 1));
			}
			i++;
		}

		return tmp.toString();
	}
	
	private String processSegment (TextFragment srcFrag,
		TextFragment trgFrag,
		AltTranslation altTrans)
	{
		if ( trgFrag == null ) { // No target available: use the source
			trgFrag = srcFrag;
		}

		StringBuilder tmp = new StringBuilder();

		if ( altTrans != null ) {
			String origin = altTrans.getOrigin();
			if (( origin != null ) && origin.equals(AltTranslation.ORIGIN_SOURCEDOC) ) {
				// Remove the origin if it was AltTranslation.ORIGIN_SOURCEDOC
				// as it corresponds to empty one in TTX
				origin = null;
			}
			if ( !Util.isEmpty(origin) ) {
				tmp.append("<Tu Origin=\""+altTrans.getOrigin()+"\" ");
			}
			else {
				tmp.append("<Tu ");
			}
			tmp.append(String.format("MatchPercent=\"%d\">", altTrans.getCombinedScore()));
		}
		else {
			tmp.append("<Tu MatchPercent=\"0\">");
		}
		
		tmp.append(String.format("<Tuv Lang=\"%s\">", srcLangCode));
		tmp.append(processFragment(srcFrag, 1));
		tmp.append("</Tuv>");
		
		tmp.append(String.format("<Tuv Lang=\"%s\">", trgLangCode));
		
		if ( layer != null ) {
			if ( altTrans != null ) {
				// This is an entry with source and target
				tmp.append(layer.endCode());
				tmp.append(layer.startSegment());
				tmp.append(processFragment(srcFrag, 1));
				tmp.append(layer.midSegment(altTrans.getCombinedScore()));
				tmp.append(processFragment(trgFrag, 0));
				tmp.append(layer.endSegment());
				tmp.append(layer.startCode());
			}
			else {
				// Write target only
				tmp.append(layer.endCode()); 
				tmp.append(processFragment(trgFrag, 0));
				tmp.append(layer.startCode()); 
			}
		}
		else {
			tmp.append(processFragment(trgFrag, 0));
		}
		
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
	
	/**
	 * Outputs a fragment.
	 * @param frag the fragment to output
	 * @param context output context: 0=text, 1=skeleton
	 * @return the output string.
	 */
	protected String processFragment (TextFragment frag,
		int context)
	{
		StringBuilder tmp = new StringBuilder();
		String text = frag.getCodedText();

		for ( int i=0; i<text.length(); i++ ) {
			char ch = text.charAt(i);
			switch ( ch ) {
			case TextFragment.MARKER_ISOLATED:
			case TextFragment.MARKER_OPENING:
			case TextFragment.MARKER_CLOSING:
				tmp.append(expandCode(frag.getCode(text.charAt(++i)), context));
				continue;
			default:
				tmp.append(encoderManager.encode(ch, context));
				continue;
			}
		}
		
		return tmp.toString(); 
	}

	private String expandCode (Code code,
		int context)
	{
		if ( layer != null ) {
			if ( context == 0 ) { // Parent is text -> codes are inline
				return layer.startInline() 
					+ layer.encode(code.getOuterData(), 2)
					+ layer.endInline();
			}
			else {
				return layer.encode(code.getOuterData(), 1);
			}
		}
		// Else: no layer
		return code.getOuterData();
	}

}
