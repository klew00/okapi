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

package net.sf.okapi.steps.common;

import java.util.List;
import java.util.Set;

import net.sf.okapi.common.IResource;
import net.sf.okapi.common.ISkeleton;
import net.sf.okapi.common.resource.Code;
import net.sf.okapi.common.resource.INameable;
import net.sf.okapi.common.resource.IReferenceable;
import net.sf.okapi.common.resource.ISegments;
import net.sf.okapi.common.resource.Property;
import net.sf.okapi.common.resource.Segment;
import net.sf.okapi.common.resource.TextContainer;
import net.sf.okapi.common.resource.TextFragment;
import net.sf.okapi.common.resource.TextUnit;

/**
 *	Copied from the test scoped FilterTestDriver. Should be moved to the resources themselves or into a helper class.   
 *
 */
public class ExtractionVerificationUtil {

	public static boolean compareTextUnit(TextUnit tu1, TextUnit tu2, boolean includeSkeleton) {

		if (!compareINameable(tu1, tu2, includeSkeleton)) {
			System.err.println("Difference in INameable");
			return false;
		}
		if (!compareIReferenceable(tu1, tu2)) {
			System.err.println("Difference in IReferenceable");
			return false;
		}
		// TextUnit tests
		if (tu1.preserveWhitespaces() != tu2.preserveWhitespaces()) {
			System.err.println("preserveWhitespaces difference");
			return false;
		}
		if (!compareTextContainer(tu1.getSource(), tu2.getSource())) {
			System.err.println("TextContainer difference");
			return false;
		}
		// TODO: target, but we have to take re-writing of source as target in account
		return true;
	}
	

	public static boolean compareIReferenceable(IReferenceable item1, IReferenceable item2) {
		
		if (item1 == null) {
			return (item2 == null);
		}
		if (item2 == null)
			return false;
		
		if (item1.isReferent() != item2.isReferent()) {
			System.err.println("isReferent difference");
			return false;
		}
		
		if (item1.getReferenceCount() != item2.getReferenceCount()) {
			System.err.println("referenceCount difference");
			return false;
		}
		
		return true;
	}

	
	public static boolean compareINameable(INameable item1, INameable item2, boolean includeSkeleton) {
		
		if (item1 == null) {
			return (item2 == null);
		}
		if (item2 == null)
			return false;

		if (!compareIResource(item1, item2, includeSkeleton)) {
			System.err.println("Difference in IResource");
			return false;
		}
		
		// Resource-level properties
		Set<String> names1 = item1.getPropertyNames();
		Set<String> names2 = item2.getPropertyNames();
		if (names1.size() != names2.size()) {
			System.err.println("Number of resource-level properties difference");
			return false;
		}
		for (String name : item1.getPropertyNames()) {
			Property p1 = item1.getProperty(name);
			Property p2 = item2.getProperty(name);
			if (!compareProperty(p1, p2)) {
				return false;
			}
		}

		// Source properties
		names1 = item1.getSourcePropertyNames();
		names2 = item2.getSourcePropertyNames();
		if (names1.size() != names2.size()) {
			System.err.println("Number of source properties difference");
			return false;
		}
		for (String name : item1.getSourcePropertyNames()) {
			Property p1 = item1.getSourceProperty(name);
			Property p2 = item2.getSourceProperty(name);
			if (!compareProperty(p1, p2)) {
				return false;
			}
		}

		// Target properties
		// TODO: Target properties

		// Name
		String tmp1 = item1.getName();
		String tmp2 = item2.getName();
		if (tmp1 == null) {
			if (tmp2 != null) {
				System.err.println("Name null difference");
				return false;
			}
		} else {
			if (tmp2 == null) {
				System.err.println("Name null difference");
				return false;
			}
			if (!tmp1.equals(tmp2)) {
				System.err.println("Name difference");
				return false;
			}
		}

		// Type
		tmp1 = item1.getType();
		tmp2 = item2.getType();
		if (tmp1 == null) {
			if (tmp2 != null) {
				System.err.println("Type null difference");
				return false;
			}
		} else {
			if (tmp2 == null) {
				System.err.println("Type null difference");
				return false;
			}
			if (!tmp1.equals(tmp2)) {
				System.err.println("Type difference");
				return false;
			}
		}

		// MIME type
		tmp1 = item1.getMimeType();
		tmp2 = item2.getMimeType();
		if (tmp1 == null) {
			if (tmp2 != null) {
				System.err.println("Mime-type null difference");
				return false;
			}
		} else {
			if (tmp2 == null) {
				System.err.println("Mime-type null difference");
				return false;
			}
			if (!tmp1.equals(tmp2)) {
				System.err.println("Mime-type difference");
				return false;
			}
		}

		// Is translatable
		if (item1.isTranslatable() != item2.isTranslatable()) {
			System.err.println("isTranslatable difference");
			return false;
		}

		return true;
	}
	
	
	public static boolean compareIResource(IResource item1, IResource item2, boolean includeSkeleton){

		if (item1 == null) {
			return (item2 == null);
		}
		if (item2 == null)
			return false;

		// ID
		String tmp1 = item1.getId();
		String tmp2 = item2.getId();
		if (tmp1 == null) {
			if (tmp2 != null)
				return false;
		} else {
			if (tmp2 == null)
				return false;
			if (!tmp1.equals(tmp2))
				return false;
		}

		// Skeleton
		if ( !includeSkeleton ) {
			return true;
		}

		ISkeleton skl1 = item1.getSkeleton();
		ISkeleton skl2 = item2.getSkeleton();
		if (skl1 == null) {
			if (skl2 != null)
				return false;
		}
		else {
			if (skl2 == null)
				return false;
			tmp1 = skl1.toString();
			tmp2 = skl2.toString();
			if (tmp1 == null) {
				if (tmp2 != null)
					return false;
			} else {
				if (tmp2 == null)
					return false;
				if (!tmp1.equals(tmp2)) {
					System.err.println("Skeleton differences: 1='" + tmp1 + "'\n2='" + tmp2 + "'");
					return false;
				}
			}
		}

		//--TODO: How about annotations
		
		return true;
	}
	
	
	public static boolean compareProperty(Property p1, Property p2) {
		if (p1 == null) {
			if (p2 != null) {
				System.err.println("Property name null difference");
				return false;
			}
			return true;
		}
		if (p2 == null) {
			System.err.println("Property name null difference");
			return false;
		}

		if (!p1.getName().equals(p2.getName())) {
			System.err.println("Property name difference");
			return false;
		}
		if (p1.isReadOnly() != p2.isReadOnly()) {
			System.err.println("Property isReadOnly difference");
			return false;
		}
		if (p1.getValue() == null) {
			if (p2.getValue() != null) {
				System.err.println("Property value null difference");
				return false;
			}
			return true;
		}
		if (!p1.getValue().equals(p2.getValue())) {
			System.err.println("Property value difference");
		}
		return true;
	}
	
	public static boolean compareTextContainer(TextContainer t1, TextContainer t2) {
		if (t1 == null) {
			System.err.println("Text container null difference");
			return (t2 == null);
		}
		if (t2 == null) {
			System.err.println("Text container null difference");
			return false;
		}

		if (!compareTextFragment(t1.getUnSegmentedContentCopy(), t2.getUnSegmentedContentCopy())) {
			System.err.println("Fragment difference");
			return false;
		}

		if (t1.hasBeenSegmented()) {
			if (!t2.hasBeenSegmented()) {
				System.err.println("isSegmented difference");
				return false;
			}
			ISegments t1Segments = t1.getSegments();
			ISegments t2Segments = t2.getSegments();
			if (t1Segments.count() != t2Segments.count()) {
				System.err.println("Number of segments difference");
				return false;
			}

			for (Segment seg1 : t1Segments) {
				Segment seg2 = t2Segments.get(seg1.id);
				if (seg2 == null) {
					System.err.println("Segment in t2 not found.");
					return false;
				}
				if (!compareTextFragment(seg1.text, seg2.text)) {
					System.err.println("Text fragment difference");
					return false;
				}
			}
		} else {
			if (t2.hasBeenSegmented()) {
				System.err.println("Segmentation difference");
				return false;
			}
		}

		return true;
	}

	public static boolean compareTextFragment(TextFragment tf1, TextFragment tf2) {
		if (tf1 == null) {
			if (tf2 != null) {
				System.err.println("Fragment null difference");
				return false;
			}
			return true;
		}
		if (tf2 == null) {
			System.err.println("Fragment null difference");
			return false;
		}

		List<Code> codes1 = tf1.getCodes();
		List<Code> codes2 = tf2.getCodes();
		if (codes1.size() != codes2.size()) {
			System.err.println("Number of codes difference");
			System.err.println("original codes=" + codes1.toString());
			System.err.println("     new codes=" + codes2.toString());
			return false;
		}
		for (int i = 0; i < codes1.size(); i++) {
			Code code1 = codes1.get(i);
			Code code2 = codes2.get(i);
			if (code1.getId() != code2.getId()) {
				System.err.println("ID difference");
				return false;
			}
			// Data
			String tmp1 = code1.getData();
			String tmp2 = code2.getData();
			if (tmp1 == null) {
				if (tmp2 != null) {
					System.err.println("Data null difference");
					return false;
				}
			} else {
				if (tmp2 == null) {
					System.err.println("Data null difference");
					return false;
				}
				if (!tmp1.equals(tmp2)) {
					System.err.println("Data difference: 1=[" + tmp1 + "] and 2=[" + tmp2 + "]");					
					return false;
				}
			}
			// Outer data
			tmp1 = code1.getOuterData();
			tmp2 = code2.getOuterData();
			if (tmp1 == null) {
				if (tmp2 != null) {
					System.err.println("Outer data null difference");
					return false;
				}
			} else {
				if (tmp2 == null) {
					System.err.println("Outer data null difference");
					return false;
				}
				if (!tmp1.equals(tmp2)) {
					System.err.println("Outer data difference");
					return false;
				}
			}
			// Type
			tmp1 = code1.getType();
			tmp2 = code2.getType();
			if (tmp1 == null) {
				if (tmp2 != null) {
					System.err.println("Type null difference");
					return false;
				}
			} else {
				if (tmp2 == null) {
					System.err.println("Type null difference");
					return false;
				}
				if (!tmp1.equals(tmp2)) {
					System.err.println("Type difference");
					return false;
				}
			}
			// Tag type
			if (code1.getTagType() != code2.getTagType()) {
				System.err.println("Tag-type difference");
				return false;
			}
			if (code1.hasReference() != code2.hasReference()) {
				System.err.println("hasReference difference");
				return false;
			}
			if (code1.isCloneable() != code2.isCloneable()) {
				System.err.println("isCloenable difference");
				return false;
			}
			if (code1.isDeleteable() != code2.isDeleteable()) {
				System.err.println("isDeleteable difference");
				return false;
			}
			if (code1.hasAnnotation() != code2.hasAnnotation()) {
				System.err.println("annotation difference");
				return false;
			}
			// TODO: compare annotations
		}

		// Coded text
		if (!tf1.getCodedText().equals(tf2.getCodedText())) {
			System.err.println("Coded text difference:\n1=\"" + tf1.getCodedText() + "\"\n2=\""
					+ tf2.getCodedText() + "\"");
			return false;
		}
		return true;
	}
}
