/*===========================================================================
  Copyright (C) 2008-2009 by the Okapi Framework contributors
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

package net.sf.okapi.common.resource;

import java.util.List;

import net.sf.okapi.common.Util;
import net.sf.okapi.common.annotation.IAnnotation;
import net.sf.okapi.common.skeleton.GenericSkeleton;
import net.sf.okapi.common.skeleton.GenericSkeletonPart;
import net.sf.okapi.common.skeleton.SkeletonUtil;

/**
 * Helper methods to manipulate {@link TextFragment} objects. 
 */
public class TextUnitUtil {
	
	/**
	 * 
	 * @param textFragment
	 */
	//TODO: javadoc
	public static void trimLeading(TextFragment textFragment) {
		
		trimLeading(textFragment, null);
	}
	
	/**
	 * 
	 * @param textFragment
	 * @param skel
	 */
	//TODO: javadoc
	public static void trimLeading(TextFragment textFragment, GenericSkeleton skel) {
		
		if (textFragment == null) return;
		String st = textFragment.getCodedText();
		TextFragment skelTF;
		
		int pos = TextFragment.indexOfFirstNonWhitespace(st, 0, -1, true, true, true, true);		
		if (pos == -1) { // Whole string is whitespaces
			skelTF = new TextFragment(st);
			textFragment.setCodedText("");			
		}
		else {
			skelTF = textFragment.subSequence(0, pos);
			textFragment.setCodedText(st.substring(pos));			
		}
			
		if (skel == null) return;
		if (skelTF == null) return;
		
		st = skelTF.toString();
		if (!Util.isEmpty(st))
			skel.append(st);  // Codes get removed
	}
	
	/**
	 * 
	 * @param textFragment
	 */
	//TODO: javadoc
	public static void trimTrailing(TextFragment textFragment) {
		trimTrailing(textFragment, null);
	}
	
	/**
	 * 
	 * @param textFragment
	 * @param skel
	 */
	//TODO: javadoc
	public static void trimTrailing(TextFragment textFragment, GenericSkeleton skel) {
		
		if (textFragment == null) return;
		
		String st = textFragment.getCodedText();
		TextFragment skelTF;
		
		int pos = TextFragment.indexOfLastNonWhitespace(st, -1, 0, true, true, true, true);
		if (pos == -1) { // Whole string is whitespaces
			skelTF = new TextFragment(st);
			textFragment.setCodedText("");			
		}
		else {
			skelTF = textFragment.subSequence(pos + 1, st.length());
			textFragment.setCodedText(st.substring(0, pos + 1));			
		}
						
		if (skel == null) return;
		if (skelTF == null) return;
		
		st = skelTF.toString();
		if (!Util.isEmpty(st))
			skel.append(st);  // Codes get removed
	}

	/**
	 * Indicates if a given text fragment ends with a given sub-string.
	 * <b>Trailing spaces are not counted</b>.
	 * @param textFragment the text fragment to examine.
	 * @param substr the text to lookup.
	 * @return true if the given text fragment ends with the given sub-string.
	 */
	public static boolean endsWith(TextFragment textFragment, String substr) {
		
		if (textFragment == null) return false;
		if (Util.isEmpty(substr)) return false;
		
		String st = textFragment.getCodedText();
		
		int pos = TextFragment.indexOfLastNonWhitespace(st, -1, 0, true, true, true, true);
		if (pos == -1) return false;
		
		return st.lastIndexOf(substr) == pos - substr.length() + 1;
	}

	//TODO: javadoc
	public static boolean isEmpty(TextUnit textUnit) {
		
		return ((textUnit == null) || Util.isEmpty(getSourceText(textUnit)));
	}
	
	//TODO: javadoc
	public static boolean hasSource(TextUnit textUnit) {
		
		return !isEmpty(textUnit, true);
	}
	
	//TODO: javadoc
	public static boolean isEmpty(TextUnit textUnit, boolean ignoreWS) {
		
		return ((textUnit == null) || Util.isEmpty(getSourceText(textUnit), ignoreWS));
	}
	
	//TODO: javadoc
	public static String getSourceText(TextUnit textUnit) {
		
		if (textUnit == null) return "";
		
		return getCodedText(textUnit.getSourceContent());
	}
	
	//TODO: javadoc
	public static String getSourceText(TextUnit textUnit, boolean removeCodes) {
		
		if (textUnit == null) return "";
		
		if (removeCodes)
			return getText(textUnit.getSourceContent());
		else
			return getCodedText(textUnit.getSourceContent());
	}
	
	//TODO: javadoc
	public static String getTargetText(TextUnit textUnit, String language) {
		
		if (textUnit == null) return "";
		if (Util.isEmpty(language)) return "";
		
		return getCodedText(textUnit.getTargetContent(language));
	}
	
	//TODO: javadoc
	public static String getCodedText(TextFragment textFragment) {
		
		if (textFragment == null) return "";
		
		return textFragment.getCodedText();
	}
	
	/**
	 * Extracts text from the given text fragment. Used to create a copy of the original string but without code markers.
	 * The original string is not stripped of code markers, and remains intact.
	 * @param textFragment TextFragment object with possible codes inside
	 * @param markerPositions List to store initial positions of removed code markers 
	 * @return The copy of the string, contained in TextFragment, but w/o code markers
	 */
	public static String getText(TextFragment textFragment, List<Integer> markerPositions) {		
		
		if (textFragment == null) return "";
				
		String res = textFragment.getCodedText();
		
		StringBuilder sb = new StringBuilder();
		
		if (markerPositions != null) 			
			markerPositions.clear();
			
			// Collect marker positions & remove markers			
			int startPos = 0;
			
			for (int i = 0; i < res.length(); i++) {
				
				switch (res.charAt(i) ) {
				
				case TextFragment.MARKER_OPENING:
				case TextFragment.MARKER_CLOSING:
				case TextFragment.MARKER_ISOLATED:
				case TextFragment.MARKER_SEGMENT:
				
					if (markerPositions != null)
						markerPositions.add(i);
				
					if (i > startPos)
						sb.append(res.substring(startPos, i));
					
					startPos = i + 2;
					i = startPos;
				}
			
			}
			
			if (startPos < res.length())
				sb.append(res.substring(startPos));
				
		return sb.toString();
	}
	
	/**
	 * Extracts text from the given text fragment. Used to create a copy of the original string but without code markers.
	 * The original string is not stripped of code markers, and remains intact.
	 * @param textFragment TextFragment object with possible codes inside
	 * @return The copy of the string, contained in TextFragment, but w/o code markers
	 */
	public static String getText(TextFragment textFragment) {
		
		return getText(textFragment, null);
	}
	
	/**
	 * Gets the last character of a given text fragment.
	 * @param textFragment the text fragment to examin.
	 * @return the last character of the given text fragment, or '\0'.
	 */
	public static char getLastChar(TextFragment textFragment) {
		
		if (textFragment == null) return '\0';
		
		String st = textFragment.getCodedText();
		
		int pos = TextFragment.indexOfLastNonWhitespace(st, -1, 0, true, true, true, true);
		if (pos == -1) return '\0';
		
		return st.charAt(pos);
	}

	/**
	 * Deletes the last non-whitespace and non-code character of a given text fragment.
	 * @param textFragment the text fragment to examine.
	 */
	public static void deleteLastChar(TextFragment textFragment) {
		
		if (textFragment == null) return;
		String st = textFragment.getCodedText();
		
		int pos = TextFragment.indexOfLastNonWhitespace(st, -1, 0, true, true, true, true);
		if (pos == -1) return;
		
		textFragment.remove(pos, pos + 1);
	}
		
	/**
	 * 
	 * @param textFragment
	 * @param findWhat
	 * @return
	 */
	//TODO: javadoc
	public static int lastIndexOf(TextFragment textFragment, String findWhat) {
		
		if (textFragment == null) return -1;
		if (Util.isEmpty(findWhat)) return -1;
		if (Util.isEmpty(textFragment.getCodedText())) return -1;
		
		return (textFragment.getCodedText()).lastIndexOf(findWhat);
	}
				
	//TODO: javadoc
	public static boolean isEmpty(TextFragment textFragment) {
		
		return (textFragment == null || (textFragment != null && textFragment.isEmpty()));		
	}
	
	//TODO: javadoc
	public static TextUnit buildTU (TextContainer source) {
			
		return buildTU(null, "", source, null, "", "");
	}
	
	//TODO: javadoc
	public static TextUnit buildTU (String source) {
		
		return buildTU(new TextContainer(source));
	}
	
	/**
	 * @param srcPart
	 * @param skelPart
	 * @return
	 */
	//TODO: javadoc
	public static TextUnit buildTU(String srcPart, String skelPart) {
		
		TextUnit res = buildTU(srcPart);
		if (res == null) return null;
		
		GenericSkeleton skel = (GenericSkeleton) res.getSkeleton();
		if (skel == null) return null;
				
		skel.addContentPlaceholder(res);
		skel.append(skelPart);
		
		return res;
	}	
	
	//TODO: javadoc
	public static TextUnit buildTU(
			TextUnit textUnit, 
			String name, 
			TextContainer source, 
			TextContainer target, 
			String language, 
			String comment) {
		
		if (textUnit == null) {
			
			textUnit = new TextUnit("");			
		}
		
		if (textUnit.getSkeleton() == null) {
			
			GenericSkeleton skel = new GenericSkeleton();
			textUnit.setSkeleton(skel);
		}		
		
		if (!Util.isEmpty(name))
			textUnit.setName(name);
		
		if (source != null)
			textUnit.setSource(source);
		
		if (target != null && !Util.isEmpty(language))
			textUnit.setTarget(language, target);
		
		if (!Util.isEmpty(comment))
			textUnit.setProperty(new Property(Property.NOTE, comment));
		
		return textUnit;
	}

	public static GenericSkeleton forceSkeleton(TextUnit tu) {
	//TODO: javadoc
		
		if (tu == null) return null;
		
		GenericSkeleton skel = (GenericSkeleton) tu.getSkeleton();
		if (skel == null) {
			
			skel = new GenericSkeleton();
			if (skel == null) return null;
			
			tu.setSkeleton(skel);			
		}
		
		if (!SkeletonUtil.hasTuRef(skel))
			skel.addContentPlaceholder(tu);
		
		return skel;
	}

	/**
	 * 
	 * @param textUnit
	 * @return
	 */
	//TODO: javadoc
	public static GenericSkeleton convertToSkeleton(TextUnit textUnit) {
		
		if (textUnit == null) return null;
		
		GenericSkeleton skel = (GenericSkeleton) textUnit.getSkeleton();
		
		if (skel == null) 
			return new GenericSkeleton(textUnit.toString());
		
		List<GenericSkeletonPart> list = skel.getParts();
		if (list.size() == 0) 
			return new GenericSkeleton(textUnit.toString());
		
		String tuRef = TextFragment.makeRefMarker("$self$");
				
		GenericSkeleton res = new GenericSkeleton();
		
		List<GenericSkeletonPart> list2 = res.getParts();

		for (GenericSkeletonPart part : list) {
			
			String st = part.toString();
			
			if (Util.isEmpty(st)) continue;
			
			if (st.equalsIgnoreCase(tuRef)) {
				
				String language = part.getLanguage();
				if (Util.isEmpty(language))
					res.add(TextUnitUtil.getSourceText(textUnit));
				else
					res.add(TextUnitUtil.getTargetText(textUnit, language));
				
				continue;
			}
			
			list2.add(part);
		}
		
//		GenericSkeletonWriter writer = new GenericSkeletonWriter();
//		if (writer == null) return null;
		
//		return new GenericSkeleton(writer.processTextUnit(textUnit));
		
		
//		if (textUnit == null) return null;
//		
//		GenericSkeleton skel = (GenericSkeleton) textUnit.getSkeleton();
//		if (skel == null) return null;
//		
//		return new GenericSkeleton(skel.toString());
		return res;
	}
	
	//TODO: javadoc
	public static <A extends IAnnotation> A getSourceAnnotation(TextUnit textUnit, Class<A> type) {
	
		if (textUnit == null) return null;
		if (textUnit.getSource() == null) return null;
		
		return textUnit.getSource().getAnnotation(type);		
	}
	
	//TODO: javadoc
	public static void setSourceAnnotation(TextUnit textUnit, IAnnotation annotation) {
		
		if (textUnit == null) return;
		if (textUnit.getSource() == null) return;
		
		textUnit.getSource().setAnnotation(annotation);		
	}
	
	//TODO: javadoc
	public static <A extends IAnnotation> A getTargetAnnotation(TextUnit textUnit, String language, Class<A> type) {
		
		if (textUnit == null) return null;
		if (Util.isEmpty(language)) return null;
		if (textUnit.getTarget(language) == null) return null;
		
		return textUnit.getTarget(language).getAnnotation(type);		
	}
	
	//TODO: javadoc
	public static void setTargetAnnotation(TextUnit textUnit, String language, IAnnotation annotation) {
		
		if (textUnit == null) return;
		if (Util.isEmpty(language)) return;
		if (textUnit.getTarget(language) == null) return;
		
		textUnit.getTarget(language).setAnnotation(annotation);		
	}
	
	//TODO: javadoc
	public static void setSourceText(TextUnit textUnit, String text) {
		
		if (textUnit == null) return;
		
		TextFragment source = textUnit.getSource(); 
		if (source == null) return;
		
		source.setCodedText(text);
	}
	
	//TODO: javadoc
	public static void setTargetText(TextUnit textUnit, String language, String text) {
		
		if (textUnit == null) return;
		if (Util.isEmpty(language)) return;
		
		TextFragment target = textUnit.getTargetContent(language); 
		if (target == null) return;
		
		target.setCodedText(text);
	}

	//TODO: javadoc
	public static void trimTU(TextUnit textUnit, boolean trimLeading, boolean trimTrailing) {
		
		if (textUnit == null) return;
		if (!trimLeading && !trimTrailing) return;
		
		TextContainer source = textUnit.getSource();
		GenericSkeleton tuSkel = TextUnitUtil.forceSkeleton(textUnit);
		GenericSkeleton skel = new GenericSkeleton();
		
		if (trimLeading)						
			trimLeading(source, skel);
		
		skel.addContentPlaceholder(textUnit);
					
		if (trimTrailing) 
			trimTrailing(source, skel);
		
		int index = SkeletonUtil.findTuRefInSkeleton(tuSkel);
		if (index != -1)
			SkeletonUtil.replaceSkeletonPart(tuSkel, index, skel);
		else
			tuSkel.add(skel);
	}
	
	//TODO: javadoc
	public static void removeQualifiers(TextUnit textUnit, String qualifier) {
		
		if (textUnit == null) return;		
		if (Util.isEmpty(qualifier)) return;
		
		String st = getSourceText(textUnit);
		if (st == null) return;
		
		int qualifierLen = qualifier.length();
		
		if (st.startsWith(qualifier) && st.endsWith(qualifier)) {
			
			GenericSkeleton tuSkel = TextUnitUtil.forceSkeleton(textUnit);
			GenericSkeleton skel = new GenericSkeleton();
			
			skel.add(qualifier);
			skel.addContentPlaceholder(textUnit);
			skel.add(qualifier);
			
			setSourceText(textUnit, st.substring(qualifierLen, Util.getLength(st) - qualifierLen));
			
			int index = SkeletonUtil.findTuRefInSkeleton(tuSkel);
			if (index != -1)
				SkeletonUtil.replaceSkeletonPart(tuSkel, index, skel);
			else
				tuSkel.add(skel);
		}
	}

}
