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

package net.sf.okapi.filters.xliff;

import java.util.logging.Logger;

import net.sf.okapi.common.IResource;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.Util;
import net.sf.okapi.common.annotation.AltTranslation;
import net.sf.okapi.common.annotation.AltTranslationsAnnotation;
import net.sf.okapi.common.filterwriter.XLIFFContent;
import net.sf.okapi.common.filterwriter.XLIFFWriter;
import net.sf.okapi.common.query.MatchType;
import net.sf.okapi.common.resource.INameable;
import net.sf.okapi.common.resource.IReferenceable;
import net.sf.okapi.common.resource.Property;
import net.sf.okapi.common.resource.Segment;
import net.sf.okapi.common.resource.ISegments;
import net.sf.okapi.common.resource.TextContainer;
import net.sf.okapi.common.resource.TextFragment;
import net.sf.okapi.common.resource.TextPart;
import net.sf.okapi.common.resource.TextUnit;
import net.sf.okapi.common.skeleton.GenericSkeleton;
import net.sf.okapi.common.skeleton.GenericSkeletonPart;
import net.sf.okapi.common.skeleton.GenericSkeletonWriter;
import net.sf.okapi.common.skeleton.StorageList;

public class XLIFFSkeletonWriter extends GenericSkeletonWriter {

	public static final String SEGSOURCEMARKER = "[@#$SEGSRC$#@]";
	public static final String ALTTRANSMARKER = "[@#$ALTTRANS$#@]";
	
	private final Logger logger = Logger.getLogger(getClass().getName());
	
	private Parameters params;
	private XLIFFContent fmt;

	// For serialization
	public XLIFFSkeletonWriter () {
		params = new Parameters();
		fmt = new XLIFFContent();
	}
	
	public XLIFFSkeletonWriter (Parameters params) {
		this.params = params;
		fmt = new XLIFFContent();
	}
	
	@Override
	protected String getString (GenericSkeletonPart part,
		int context)
	{
		// Check for the seg-source special case
		if ( part.toString().startsWith(SEGSOURCEMARKER) ) {
			// There is normally a text unit associated
			return getSegSourceOutput((TextUnit)part.getParent());
		}
		else if ( part.toString().startsWith(ALTTRANSMARKER) ) {
			return getNewAltTransOutput((TextUnit)part.getParent());
		}
		
		// If it is not a reference marker, just use the data
		if ( !part.toString().startsWith(TextFragment.REFMARKER_START) ) {
			if ( layer == null ) {
				return part.toString();
			}
			else {
				return layer.encode(part.toString(), context);
			}
		}
		
		// Get the reference info
		Object[] marker = TextFragment.getRefMarker(part.getData());
		// Check for problem
		if ( marker == null ) {
			return "-ERR:INVALID-REF-MARKER-";
		}
		String propName = (String)marker[3];

		// If we have a property name: It's a reference to a property of 
		// the resource holding this skeleton
		if ( propName != null ) { // Reference to the content of the referent
			return getString((INameable)part.getParent(), propName, part.getLocale(), context);
		}

		// Set the locToUse and the contextToUse parameters
		// If locToUse==null: it's source, so use output locale for monolingual
		LocaleId locToUse = (part.getLocale()==null) ? outputLoc : part.getLocale();
		int contextToUse = context;
		if ( isMultilingual ) {
			locToUse = part.getLocale();
			// If locToUse==null: it's source, so not text in multilingual
			contextToUse = (locToUse==null) ? 0 : context;
		}
		
		// If a parent if set, it's a reference to the content of the resource
		// holding this skeleton. And it's always a TextUnit
		if ( part.getParent() != null ) {
			if ( part.getParent() instanceof TextUnit ) {
				return getContent((TextUnit)part.getParent(), locToUse, contextToUse);
			}
			else {
				throw new RuntimeException("The self-reference to this skeleton part must be a text-unit.");
			}
		}
		
		// Else this is a true reference to a referent
		IReferenceable ref = getReference((String)marker[0]);
		if ( ref == null ) {
			logger.warning(String.format("Reference '%s' not found.", (String)marker[0]));
			return "-ERR:REF-NOT-FOUND-";
		}
		if ( ref instanceof TextUnit ) {
			return getString((TextUnit)ref, locToUse, contextToUse);
		}
		if ( ref instanceof GenericSkeletonPart ) {
			return getString((GenericSkeletonPart)ref, contextToUse);
		}
		if ( ref instanceof StorageList ) { // == StartGroup
			return getString((StorageList)ref, locToUse, contextToUse);
		}
		// Else: DocumentPart, StartDocument, StartSubDocument 
		return getString((GenericSkeleton)((IResource)ref).getSkeleton(), context);
	}
	
	@Override
	protected String getContent (TextUnit tu,
		LocaleId locToUse,
		int context)
	{
		// Update the encoder from the TU's MIME type
		if ( encoderManager != null ) {
			encoderManager.updateEncoder(tu.getMimeType());
		}
		
		if ( !tu.isTranslatable() ) {
			context = 0; // Keep skeleton context
		}
		
		// Get the source container
		TextContainer srcCont = tu.getSource();
		
		// Process the case of an output for the source
		if ( locToUse == null ) {
			return getUnsegmentedOutput(srcCont, locToUse, context);
		}
		
		// Else: Case of a target output

		// Determine whether we output segmentation or not
		boolean doSegments = doSegments(tu);
		
		TextContainer trgCont = tu.getTarget(locToUse);
		if ( trgCont == null ) {
			// If there is no target available
			// We fall back to source
			trgCont = srcCont;
		}
		// We if show segments, make sure both entries are actually segmented
//		if ( doSegments ) {
//			doSegments = trgCont.hasBeenSegmented();
//			if ( doSegments ) {
//				doSegments = srcCont.hasBeenSegmented();
//			}
//		}

		// Process the target content: either with or without segments
		if ( doSegments ) {
			return getSegmentedOutput(srcCont, trgCont, locToUse, context);
		}
		else {
			return getUnsegmentedOutput(trgCont, locToUse, context);
		}
	}

	private String getSegSourceOutput (TextUnit tu) {
		if ( !doSegments(tu) ) {
			return ""; // No segmentation: no seg-source element
		}
		
		TextContainer srcCont = tu.getSource();
		if ( srcCont.isEmpty() ) return ""; // No segmented entry if it's empty
//		if ( srcCont.isEmpty() || !srcCont.hasBeenSegmented() ) return ""; // No segmented entry if it's empty
		
		// Else: output new seg-source
		StringBuilder tmp = new StringBuilder("<seg-source>");
		
		for ( TextPart part : srcCont ) {
			if ( part.isSegment() ) {
				Segment srcSeg = (Segment)part;
				// Opening marker
				tmp.append(String.format("<mrk mid=\"%s\" mtype=\"seg\">", srcSeg.id));
				// Write the segment (note: srcSeg can be null)
				// If no layer or layer: just write the target
				tmp.append(getContent(srcSeg.text, null, 1));
				// Closing marker
				tmp.append("</mrk>");
			}
			else { // Normal text fragment
				// Target fragment is used
				tmp.append(getContent(part.text, null, 1));
			}
		}
		
		tmp.append("</seg-source>");
		// Add line-break only if the seg-source was not in the original
		if ( tu.getProperty(XLIFFFilter.PROP_WASSEGMENTED) == null ) {
			tmp.append(encoderManager.getLineBreak());
		}
		
		return tmp.toString();
	}
	
	private String getNewAltTransOutput (TextUnit tu) {
		if ( !params.getAddAltTrans() ) {
			return "";
		}
		// Empty?
		if ( tu.getSource().isEmpty() ) {
			return ""; // Empty
		}
		if ( !tu.hasTarget(outputLoc) ) {
			return ""; // Nothing to output
		}
		
		StringBuilder tmp = new StringBuilder();
		TextContainer tc = tu.getTarget(outputLoc);
		// From the target container
		formatAltTranslations(tc.getAnnotation(AltTranslationsAnnotation.class), null, tmp);
		// From the segments
		for ( Segment seg : tc.getSegments() ) {
			formatAltTranslations(seg.getAnnotation(AltTranslationsAnnotation.class), seg, tmp);
		}
		
		return tmp.toString();
	}

	private void formatAltTranslations (AltTranslationsAnnotation ann,
		Segment segment,
		StringBuilder sb)
	{
		if ( ann == null ) {
			return; // No annotation
		}
		for ( AltTranslation alt : ann ) {
			if ( alt.getScore() <= 0 ) continue;
			if ( alt.getFromOriginal() ) continue; // New alt-trans only
			
			sb.append("<alt-trans");
			if ( segment != null ) {
				sb.append(String.format(" mid=\"%s\":", Util.escapeToXML(segment.getId(), 0, false, null)));
			}
			sb.append(String.format(" match-quality=\"%d\"", alt.getScore()));
			if ( !Util.isEmpty(alt.getOrigin()) ) {
				sb.append(String.format(" origin=\"%s\"", Util.escapeToXML(alt.getOrigin(), 0, false, null)));
			}
			if ( alt.getType() != MatchType.UKNOWN ) {
				sb.append(" xmlns:okp=\""+XLIFFWriter.NS_XLIFFOKAPI+"\"");
				sb.append(String.format(" okp:"+XLIFFWriter.OKP_MATCHTYPE+"=\"%s\"", alt.getType().toString()));
			}
			sb.append(">"+encoderManager.getLineBreak());
			TextContainer cont = alt.getSource();
			if ( !cont.isEmpty() ) {
				sb.append(String.format("<source xml:lang=\"%s\">", alt.getSourceLocale().toString()));
				// Write full source content (never with segments markers)
				sb.append(fmt.toSegmentedString(cont, 0, false, false, true));
				sb.append("</source>"+encoderManager.getLineBreak()); // source
			}
			sb.append(String.format("<target xml:lang=\"%s\">", alt.getTargetLocale().toString()));
			sb.append(fmt.toSegmentedString(alt.getTarget(), 0, false, false, true));
			sb.append("</target>"+encoderManager.getLineBreak()); // target
			sb.append("</alt-trans>"+encoderManager.getLineBreak()); // alt-trans
		}
	}
	
	private String getUnsegmentedOutput (TextContainer cont,
		LocaleId locToUse,
		int context)
	{
		TextFragment tf = null;
		if ( cont.contentIsOneSegment() ) {
			// One part that is a segment, just get the content
			tf = cont.getFirstContent();
		}
		else { // Else: get a copy of the un-segmented entry
			tf = cont.getUnSegmentedContentCopy();
		}
		// Apply the layer if there is one
		if ( layer == null ) {
			return getContent(tf, locToUse, context);
		}
		else {
			switch ( context ) {
			case 1:
				return layer.endCode()
					+ getContent(tf, locToUse, 0)
					+ layer.startCode();
			case 2:
				return layer.endInline()
					+ getContent(tf, locToUse, 0)
					+ layer.startInline();
			default:
				return getContent(tf, locToUse, context);
			}
		}
	}
	
	// This method assumes bi-lingual pairs are 1-1 and in the same order
	private String getSegmentedOutput (TextContainer srcCont,
		TextContainer trgCont,
		LocaleId locToUse,
		int context)
	{
		StringBuilder tmp = new StringBuilder();

		// The output is driven by the target, not the source, so the interstices parts
		// are the ones of the target, no the one of the source
		int scoreIndex = -1;
		ISegments srcSegs = srcCont.getSegments();
		for ( TextPart part : trgCont ) {
			if ( part.isSegment() ) {
				scoreIndex++;
				int lev = 0; //TODO: score values for RTF
				Segment trgSeg = (Segment)part;
				Segment srcSeg = srcSegs.get(trgSeg.id);
				if ( srcSeg == null ) {
					// A target segment without a corresponding source: give warning
					logger.warning(String.format("No source segment found for target segment id='%s':\n\"%s\".",
						trgSeg.id, trgSeg.text.toText()));
				}

				// Opening marker
				tmp.append(String.format("<mrk mid=\"%s\" mtype=\"seg\">", trgSeg.id));
				// Write the segment (note: srcSeg can be null)
				if ( layer == null ) {
					// If no layer: just write the target
					tmp.append(getContent(trgSeg.text, locToUse, context));
				}
				else { // If layer: write the bilingual entry
					switch ( context ) {
					case 1:
						tmp.append(layer.endCode()
							+ layer.startSegment()
							+ ((srcSeg==null) ? "" : getContent(srcSeg.text, locToUse, 0))
							+ layer.midSegment(lev)
							+ getContent(trgSeg.text, locToUse, 0)
							+ layer.endSegment()
							+ layer.startCode());
						break;
					case 2:
						tmp.append(layer.endInline()
							+ layer.startSegment()
							+ ((srcSeg==null) ? "" : getContent(srcSeg.text, locToUse, 0))
							+ layer.midSegment(lev)
							+ getContent(trgSeg.text, locToUse, 0)
							+ layer.endSegment()
							+ layer.startInline());
						break;
					default:
						tmp.append(layer.startSegment()
							+ ((srcSeg==null) ? "" : getContent(srcSeg.text, locToUse, 0))
							+ layer.midSegment(lev)
							+ getContent(trgSeg.text, locToUse, 0)
							+ layer.endSegment());
						break;
					}
				}
				// Closing marker
				tmp.append("</mrk>");
			}
			else { // Normal text fragment
				// Target fragment is used
				tmp.append(getContent(part.text, locToUse, context));
			}
		}

		return tmp.toString();
	}
	
	private boolean doSegments (TextUnit tu) {
		// Do we always segment?
		switch ( params.getOutputSegmentationType() ) {
		case Parameters.SEGMENTATIONTYPE_SEGMENTED:
			return true;
		case Parameters.SEGMENTATIONTYPE_NOTSEGMENTED:
			return false;
		}
		// Otherwise: do as in the input
		// So check the property with the info on whether it was segmented or not
		Property prop = tu.getProperty(XLIFFFilter.PROP_WASSEGMENTED);
		if ( prop != null ) {
			return prop.getValue().equals("true");
		}
		return false;
	}

}
