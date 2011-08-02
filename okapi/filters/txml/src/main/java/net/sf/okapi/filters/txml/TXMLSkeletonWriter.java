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

package net.sf.okapi.filters.txml;

import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.MimeTypeMapper;
import net.sf.okapi.common.annotation.AltTranslation;
import net.sf.okapi.common.annotation.AltTranslationsAnnotation;
import net.sf.okapi.common.encoder.EncoderManager;
import net.sf.okapi.common.filterwriter.ILayerProvider;
import net.sf.okapi.common.resource.Code;
import net.sf.okapi.common.resource.ISegments;
import net.sf.okapi.common.resource.ITextUnit;
import net.sf.okapi.common.resource.Segment;
import net.sf.okapi.common.resource.StartDocument;
import net.sf.okapi.common.resource.TextContainer;
import net.sf.okapi.common.resource.TextFragment;
import net.sf.okapi.common.resource.TextPart;
import net.sf.okapi.common.skeleton.GenericSkeleton;
import net.sf.okapi.common.skeleton.GenericSkeletonWriter;

public class TXMLSkeletonWriter extends GenericSkeletonWriter {

	private boolean forceSegmentedOutput;
	private boolean fallBackToSource;
	
	public TXMLSkeletonWriter (boolean forceSegments,
		boolean fallBackToSource)
	{
		this.forceSegmentedOutput = forceSegments;
		this.fallBackToSource = fallBackToSource;
	}
	
	@Override
	public String processStartDocument (LocaleId outputLocale,
		String outputEncoding,
		ILayerProvider layer,
		EncoderManager encoderManager,
		StartDocument resource)
	{
		// Always use UTF-8 for the output encoding
		outputEncoding = "UTF-8";
		return super.processStartDocument(outputLocale, outputEncoding, layer, encoderManager, resource);
	}
	
	@Override
	public String processTextUnit (ITextUnit tu) {
		if ( tu == null ) return "";
		// Update the encoder from the TU's MIME type
		if ( encoderManager != null ) {
			encoderManager.updateEncoder(tu.getMimeType());
		}
		
		StringBuilder tmp = new StringBuilder();

		// The skeleton for the TU has three parts: before, reference and after
		// Process the first part
		GenericSkeleton skel = (GenericSkeleton)tu.getSkeleton();
		tmp.append(getString(skel.getParts().get(0), 1));
		
		TextContainer srcCont = tu.getSource();
		if ( forceSegmentedOutput && !srcCont.hasBeenSegmented() ) {
			// Work from a clone if we need to change the segmentation
			srcCont = srcCont.clone();
			srcCont.setHasBeenSegmentedFlag(true);
		}
		ensureTxmlPattern(srcCont);
		
		TextContainer trgCont = null;
		if ( tu.hasTarget(outputLoc) ) {
			trgCont = tu.getTarget(outputLoc);
			if ( forceSegmentedOutput && !trgCont.hasBeenSegmented() ) {
				trgCont = trgCont.clone();
				trgCont.setHasBeenSegmentedFlag(true);
			}
			ensureTxmlPattern(srcCont);
		}
		else if ( fallBackToSource ) { // Fall back to source if we have no target and it's requested
			trgCont = srcCont;
		}
		
		// Go through the source, segment by segment
		// We can do this because the pattern has been reduced to the Txml pattern
		for ( int i=0; i<srcCont.count(); i++ ) {
			TextPart part = srcCont.get(i);
			// Skip non-segment part: they will be treated with the segments
			if ( !part.isSegment() ) continue;
			
			// This is a segment: treat it now
			Segment srcSeg = (Segment)part;
			// Get the target, so we can get all information to output the segment attributes
			AltTranslation altTrans = null;
			Segment trgSeg = null;
			if ( trgCont != null ) {
				// Get the target segment if possible
				trgSeg = trgCont.getSegments().get(srcSeg.id);
				if ( trgSeg == null ) {
					// Fall back to the source if requested
					if ( fallBackToSource ) trgSeg = srcSeg;
				}
				// Get the alt-trans possible
				if ( trgSeg != null ) {
					AltTranslationsAnnotation ann = trgSeg.getAnnotation(AltTranslationsAnnotation.class);
					if ( ann != null ) {
						altTrans = ann.getFirst();
					}
				}
			}
			
			// Output the segment element
			tmp.append("<segment segmentId=\""+srcSeg.getId()+"\"");
			// Output the gtmt attribute
			boolean gtmt = false;
			if ( altTrans != null ) {
				gtmt = altTrans.getFromOriginal();
			}
			tmp.append(" gtmt=\"" + (gtmt ? "true" : "false") + "\"");
			tmp.append(">");
			
			// Do we have a part before this segment?
			if ( i > 0 ) {
				part = srcCont.get(i-1);
				if ( !part.isSegment() ) {
					// Yes, output it as WS
					tmp.append("<ws>");
					tmp.append(processFragment(part.getContent(), 1));
					tmp.append("</ws>");
				}
			}
			
			// Now output the segment source and target
			tmp.append("<source>");
			tmp.append(processFragment(srcSeg.getContent(), 1));
			tmp.append("</source>");
			
			// Output the target (if any)
			if ( trgSeg != null ) {
				tmp.append("<target>");
				tmp.append(processFragment(trgSeg.getContent(), 0));
				tmp.append("</target>");
			}
			
			// Do we have a part after the segment?
			if ( i+1 < srcCont.count() ) {
				part = srcCont.get(i+1);
				if ( !part.isSegment() ) {
					// Yes, output it as WS
					tmp.append("<ws>");
					tmp.append(processFragment(part.getContent(), 1));
					tmp.append("</ws>");
					i++; // This part is done
				}
			}

			// Close the segment
			tmp.append("</segment>");
		}

		// Process the last skeleton part (the third)
		tmp.append(getString(skel.getParts().get(2), 1));

		// Done
		return tmp.toString();
	}
	
	private Segment fetchNextSegment (TextContainer tc,
		int fromIndex)
	{
		for ( int i=fromIndex; i<tc.count(); i++ ) {
			TextPart part = tc.get(i);
			if ( part.isSegment() ) {
				return (Segment)part;
			}
		}
		return null;
	}
	
	private int fetchNextSegmentIndex (TextContainer tc,
		int fromIndex)
	{
		for ( int i=fromIndex; i<tc.count(); i++ ) {
			TextPart part = tc.get(i);
			if ( part.isSegment() ) {
				return i;
			}
		}
		return -1;
	}
	
	protected void ensureTxmlPattern (TextContainer tc) {
		int i = 0;
		int diff;
		while ( i<tc.count() ) {
			// If the part is a segment, move to the next part
			if ( tc.get(i).isSegment() ) {
				i++;
				continue;
			}
			// Else: it's not a segment, and we try to collapse if needed
			
			// Where is the next segment
			int nextSegIndex = fetchNextSegmentIndex(tc, i);
			if ( nextSegIndex == -1 ) {
				// No next segment
				// Collapse all remaining parts into one
				diff = (tc.count()-i)-1;
			}
			else if ( i == 0 ) {
				// If this is the first part before the first index
				// Collapse until first segment
				diff = (nextSegIndex-i)-1;
			}
			else {
				// Else: it a part between segments
				// Allowing 2 parts
				diff = (nextSegIndex-i)-2;
			}

			// Collapse if needed
			if ( diff > 0 ) {
				tc.joinWithNext(i, diff);
			}
			i++;
		}
	}
	
	private String processSegment (Segment srcSeg,
		TextFragment trgFrag,
		AltTranslation altTrans)
	{
		TextFragment srcFrag = srcSeg.getContent();
		if ( trgFrag == null ) { // No target available: use the source
			trgFrag = srcFrag;
		}

		StringBuilder tmp = new StringBuilder();
		tmp.append("<segment segmentId=\""+srcSeg.getId()+"\"");
		if ( altTrans != null ) {
//			tmp.append(String.format("MatchPercent=\"%d\">", altTrans.getScore()));
		}
		
		tmp.append("><source>");
		tmp.append(processFragment(srcFrag, 1));
		tmp.append("</source>");
		
		tmp.append("<target>");

		if ( layer != null ) {
			if ( altTrans != null ) {
				// This is an entry with source and target
				tmp.append(layer.endCode());
				tmp.append(layer.startSegment());
				tmp.append(processFragment(srcFrag, 1));
				tmp.append(layer.midSegment(altTrans.getScore()));
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
		
		tmp.append("</target>");
		tmp.append("</segment>");
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
