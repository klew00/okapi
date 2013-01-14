/*===========================================================================
  Copyright (C) 2013 by the Okapi Framework contributors
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

package net.sf.okapi.filters.its.html5;

import java.util.ArrayList;
import java.util.List;

import org.w3c.its.ITSEngine;

import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.Util;
import net.sf.okapi.common.annotation.GenericAnnotation;
import net.sf.okapi.common.annotation.GenericAnnotationType;
import net.sf.okapi.common.annotation.GenericAnnotations;
import net.sf.okapi.common.encoder.EncoderContext;
import net.sf.okapi.common.filterwriter.ITSContent;
import net.sf.okapi.common.resource.Code;
import net.sf.okapi.common.resource.DocumentPart;
import net.sf.okapi.common.resource.TextFragment;
import net.sf.okapi.common.skeleton.GenericSkeleton;
import net.sf.okapi.common.skeleton.GenericSkeletonPart;
import net.sf.okapi.common.skeleton.GenericSkeletonWriter;
import net.sf.okapi.filters.its.ITSFilter;

/**
 * Implements ISkeletonWriter for the ITS filters 
 */
public class HTML5SkeletonWriter extends GenericSkeletonWriter {

	public static final String REF_PREFIX = "REF:";

	private List<GenericAnnotations> standoff;
	private ITSContent itsCont;

	/**
	 * Gets the original content of a TextFragment.
	 * @param tf the TextFragment to process.
	 * @param locToUse locale to output. Use null for the source, or the locale
	 * for the target locales. This is used for referenced content in inline codes.
	 * @param context Context flag: 0=text, 1=skeleton, 2=inline.
	 * @return The string representation of the text unit content.
	 */
	@Override
	public String getContent (TextFragment tf,
		LocaleId locToUse,
		EncoderContext context)
	{
		// Output simple text
		if ( !tf.hasCode() ) {
			if ( encoderManager == null ) {
				if ( layer == null ) {
					return tf.toText();
				}
				else {
					return layer.encode(tf.toText(), context);
				}
			}
			else {
				if ( layer == null ) {
					return encoderManager.encode(tf.toText(), context);
				}
				else {
					return layer.encode(
						encoderManager.encode(tf.toText(), context), context);
				}
			}
		}

		// Output text with in-line codes
		List<Code> codes = tf.getCodes();
		StringBuilder tmp = new StringBuilder();
		String text = tf.getCodedText();
		Code code;
		char ch;
		for ( int i=0; i<text.length(); i++ ) {
			ch = text.charAt(i);
			switch ( ch ) {
			case TextFragment.MARKER_OPENING:
				code = codes.get(TextFragment.toIndex(text.charAt(++i)));
				if ( code.hasOnlyAnnotation() ) {
					tmp.append("<span");
					outputAnnotation(code, tmp);
					tmp.append(">");
				}
				else {
					tmp.append(expandCodeContent(code, locToUse, context));
				}
				break;
			case TextFragment.MARKER_CLOSING:
				code = codes.get(TextFragment.toIndex(text.charAt(++i)));
				if ( code.hasOnlyAnnotation() ) {
					tmp.append("</span>");
				}
				else {
					tmp.append(expandCodeContent(code, locToUse, context));
				}
				break;
			case TextFragment.MARKER_ISOLATED:
				code = codes.get(TextFragment.toIndex(text.charAt(++i)));
				tmp.append(expandCodeContent(code, locToUse, context));
				break;
			default:
				if ( Character.isHighSurrogate(ch) ) {
					int cp = text.codePointAt(i);
					i++; // Skip low-surrogate
					if ( encoderManager == null ) {
						if ( layer == null ) {
							tmp.append(new String(Character.toChars(cp)));
						}
						else {
							tmp.append(layer.encode(cp, context));
						}
					}
					else {
						if ( layer == null ) {
							tmp.append(encoderManager.encode(cp, context));
						}
						else {
							tmp.append(layer.encode(
								encoderManager.encode(cp, context),
								context));
						}
					}
				}
				else { // Non-supplemental case
					if ( encoderManager == null ) {
						if ( layer == null ) {
							tmp.append(ch);
						}
						else {
							tmp.append(layer.encode(ch, context));
						}
					}
					else {
						if ( layer == null ) {
							tmp.append(encoderManager.encode(ch, context));
						}
						else {
							tmp.append(layer.encode(
								encoderManager.encode(ch, context),
								context));
						}
					}
				}
				break;
			}
		}
		return tmp.toString();
	}
	
	@Override
	public String processDocumentPart (DocumentPart resource) {
		GenericSkeleton skel = (GenericSkeleton)resource.getSkeleton();
		if ( skel != null ) {
			GenericSkeletonPart part = skel.getFirstPart();
			// Is it a standoff placeholder
			if ( part.getData().toString().equals(ITSFilter.STANDOFFMARKER) ) {
				part.setData(""); // Clear the marker in all cases
				// And add the markup if there is something to output
				if ( !Util.isEmpty(standoff) ) {
					if ( itsCont == null ) itsCont = new ITSContent(encoderManager.getCharsetEncoder(), true);
					part.append(itsCont.writeStandoffLQI(standoff));
					part.append(itsCont.writeStandoffProvenance(standoff));
				}
			}
		}

		// Then do the normal process
		return super.processDocumentPart(resource);
	}
	
	private void outputAnnotation (Code code,
		StringBuilder output)
	{
		GenericAnnotations anns = (GenericAnnotations)code.getAnnotation(GenericAnnotationType.GENERIC);
		if ( anns == null ) return;
		
		for ( GenericAnnotation ann : anns ) {
			// AnnotatorsRef
			//TODO
			// Disambiguation
			if ( ann.getType().equals(GenericAnnotationType.DISAMB) ) {
				printITSStringAttribute(ann.getString(GenericAnnotationType.DISAMB_CLASS), "disambig-class", output);
				printITSDoubleAttribute(ann.getDouble(GenericAnnotationType.DISAMB_CONFIDENCE), "disambigConfidence", output);
				//TODO: needs annotatorsRef if confidence is there
				String value = ann.getString(GenericAnnotationType.DISAMB_GRANULARITY);
				if ( !value.equals(GenericAnnotationType.DISAMB_GRANULARITY_ENTITY) ) { // Output only the non-default value
					printITSStringAttribute(value, "disambig-granularity", output);
				}
				printITSStringAttribute(ann.getString(GenericAnnotationType.DISAMB_IDENT), "disambig-ident", output);
				printITSStringAttribute(ann.getString(GenericAnnotationType.DISAMB_SOURCE), "disambig-source", output);
			}
			
			// Terminology
			else if ( ann.getType().equals(GenericAnnotationType.TERM) ) {
				printITSBooleanAttribute(true, "term", output);
				printITSDoubleAttribute(ann.getDouble(GenericAnnotationType.TERM_CONFIDENCE), "term-confidence", output);
				printITSStringAttribute(ann.getString(GenericAnnotationType.TERM_INFO), "term-info", output);
			}
			
			// Allowed Characters
			else if ( ann.getType().equals(GenericAnnotationType.ALLOWEDCHARS) ) {
				printITSStringAttribute(ann.getString(GenericAnnotationType.ALLOWEDCHARS_VALUE), "allowed-characters", output);
			}
			
			// Storage Size
			else if ( ann.getType().equals(GenericAnnotationType.STORAGESIZE) ) {
				printITSIntegerAttribute(ann.getInteger(GenericAnnotationType.STORAGESIZE_SIZE), "storage-size", output);
				String tmp = ann.getString(GenericAnnotationType.STORAGESIZE_ENCODING);
				if ( !tmp.equals("UTF-8") ) printITSStringAttribute(tmp, "storage-encoding", output);
				tmp = ann.getString(GenericAnnotationType.STORAGESIZE_LINEBREAK);
				if ( !tmp.equals("lf") ) printITSStringAttribute(tmp, "storage-linebreak", output);
			}
			
			// Localization Quality issue
			else if ( ann.getType().equals(GenericAnnotationType.LQI) ) {
				continue; // LQI are dealt with separately
			}
			
			// Provenance
			else if ( ann.getType().equals(GenericAnnotationType.PROV) ) {
				continue; // Provenance are dealt with separately
			}
		}
			
		// Deal with LQI information
		List<GenericAnnotation> list = anns.getAnnotations(GenericAnnotationType.LQI);
		if ( list.size() == 1 ) {
			// If there is only one QI entry: we output it locally
			GenericAnnotation ann = list.get(0);
			printITSStringAttribute(ann.getString(GenericAnnotationType.LQI_COMMENT), "loc-quality-issue-comment", output);
			Boolean booVal = ann.getBoolean(GenericAnnotationType.LQI_ENABLED);
			if (( booVal != null ) && !booVal ) { // Output only non-default value (if one is set)
				printITSBooleanAttribute(booVal, "loc-quality-issue-enabled", output);
			}
			printITSStringAttribute(ann.getString(GenericAnnotationType.LQI_PROFILEREF), "loc-quality-issue-profile", output);
			printITSDoubleAttribute(ann.getDouble(GenericAnnotationType.LQI_SEVERITY), "loc-quality-issue-severity", output);
			printITSStringAttribute(ann.getString(GenericAnnotationType.LQI_TYPE), "loc-quality-issue-type", output);
		}
		else if ( list.size() > 1 ) {
			// If there are 2 or more: the items need to be output as standoff markup.
			// Inside a ,script> at the end of the document.
			String refId = anns.getData(); // ID to use is already generated in the annotation
			output.append(" its-loc-quality-issues-ref=\"#"+refId+"\"");
			if ( standoff == null ) standoff = new ArrayList<GenericAnnotations>();
			GenericAnnotations newSet = new GenericAnnotations();
			standoff.add(newSet);
			newSet.setData(refId);
			newSet.addAll(list);
		}

		// Deal with Provenance information
		list = anns.getAnnotations(GenericAnnotationType.PROV);
		if ( list.size() == 1 ) {
//TODO			
			// If there is only one QI entry: we output it locally
//			GenericAnnotation ann = list.get(0);
//			printITSStringAttribute(ann.getString(GenericAnnotationType.LQI_COMMENT), "loc-quality-issue-comment", output);
//			Boolean booVal = ann.getBoolean(GenericAnnotationType.LQI_ENABLED);
//			if (( booVal != null ) && !booVal ) { // Output only non-default value (if one is set)
//				printITSBooleanAttribute(booVal, "loc-quality-issue-enabled", output);
//			}
//			printITSStringAttribute(ann.getString(GenericAnnotationType.LQI_PROFILEREF), "loc-quality-issue-profile", output);
//			printITSDoubleAttribute(ann.getDouble(GenericAnnotationType.LQI_SEVERITY), "loc-quality-issue-severity", output);
//			printITSStringAttribute(ann.getString(GenericAnnotationType.LQI_TYPE), "loc-quality-issue-type", output);
		}
		else if ( list.size() > 1 ) {
			// If there are 2 or more: the items need to be output as standoff markup.
			// Inside a ,script> at the end of the document.
			String refId = anns.getData(); // ID to use is already generated in the annotation
			output.append(" its-provenance-records-ref=\"#"+refId+"\"");
			if ( standoff == null ) standoff = new ArrayList<GenericAnnotations>();
			GenericAnnotations newSet = new GenericAnnotations();
			standoff.add(newSet);
			newSet.setData(refId);
			newSet.addAll(list);
		}
	}

	private void printITSStringAttribute (String value,
		String attrName,
		StringBuilder output)
	{
		if ( value != null ) {
			String ref = "";
			if ( value.startsWith(REF_PREFIX) ) {
				ref = "-ref";
				value = value.substring(REF_PREFIX.length());
			}
			output.append(" its-"+attrName+ref+"=\""+encoderManager.encode(value, EncoderContext.INLINE)+"\"");
		}
	}

	private void printITSDoubleAttribute (Double value,
		String attrName,
		StringBuilder output)
	{
		if ( value != null ) {
			output.append(" its-"+attrName+"=\""+Util.formatDouble(value)+"\"");
		}
	}

	private void printITSIntegerAttribute (Integer value,
		String attrName,
		StringBuilder output)
	{
		if ( value != null ) {
			output.append(" its-"+attrName+"=\""+value+"\"");
		}
	}

	private void printITSBooleanAttribute (Boolean value,
		String attrName,
		StringBuilder output)
	{
		if ( value != null ) {
			output.append(" its-"+attrName+"=\""+(value ? "yes" : "no")+"\"");
		}
	}

	
}
