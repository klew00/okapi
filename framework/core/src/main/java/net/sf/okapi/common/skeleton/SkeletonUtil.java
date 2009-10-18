/*===========================================================================
  Copyright (C) 2009 by the Okapi Framework contributors
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

package net.sf.okapi.common.skeleton;

import java.util.List;

import net.sf.okapi.common.ListUtil;
import net.sf.okapi.common.Util;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.resource.TextFragment;

public class SkeletonUtil {

	/**
	 * Finds source reference in the skeleton.
	 * @param skel the skeleton is being sought for the reference
	 * @return index in the list of skeleton parts for the skeleton part containing the reference 
	 */
	public static int findTuRefInSkeleton(GenericSkeleton skel) {
		return findTuRefInSkeleton(skel, null);
	}
	
	/**
	 * Finds either source or target reference in the skeleton. If language is specified, then its target reference is sought for.
	 * @param skel the skeleton is being sought for the reference.
	 * @param language the language to search the reference for.
	 * @return index in the list of skeleton parts for the skeleton part containing the reference. 
	 */
	public static int findTuRefInSkeleton(GenericSkeleton skel,
		LocaleId language)
	{
		if ( skel == null ) return -1; 		
		List<GenericSkeletonPart> list = skel.getParts();
		String tuRef = TextFragment.makeRefMarker("$self$");
		
		for ( int i=0; i<list.size(); i++ ) {
			GenericSkeletonPart part = list.get(i);				
			String st = part.toString();
			if ( Util.isEmpty(st) ) continue;
			if ( st.equalsIgnoreCase(tuRef) ) {
				if ( Util.isNullOrEmpty(language) ) {
					return i;
				}
				else if ( language.equals(part.getLanguage()) ) {
					return i;
				}
			}
		}
		return -1;
	}
		
	public static boolean hasTuRef (GenericSkeleton skel) {
		return findTuRefInSkeleton(skel) != -1;
	}
	
	public static boolean hasTuRef (GenericSkeleton skel, LocaleId language) {
		return findTuRefInSkeleton(skel, language) != -1;
	}
	
	public static boolean replaceSkeletonPart (GenericSkeleton skel,
		int index,
		GenericSkeleton replacement)
	{
		if ( skel == null ) return false;
		if ( replacement == null ) return false;
		
		List<GenericSkeletonPart> list = skel.getParts();
		if ( !Util.checkIndex(index, list) ) return false;

		List<GenericSkeletonPart> list2 = (List<GenericSkeletonPart>) ListUtil.moveItems(list); // clears the original list
		for (int i = 0; i < list2.size(); i++) {
			if ( i == index )						
				skel.add(replacement);
			else
				list.add(list2.get(i));
		}
		return true;
	}
	
}
