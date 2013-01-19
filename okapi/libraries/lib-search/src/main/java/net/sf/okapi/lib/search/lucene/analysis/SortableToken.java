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

package net.sf.okapi.lib.search.lucene.analysis;

import org.apache.lucene.analysis.Token;

/**
 * 
 * @author HargraveJE
 */
@SuppressWarnings("serial")
public class SortableToken extends Token implements Comparable<SortableToken> {	
	
	@Override
	public int compareTo(SortableToken o) {
		SortableToken t = (SortableToken) o;
		if (t.startOffset() < startOffset()) {
			return -1;
		} else if (t.startOffset() > startOffset()) {
			return 1;
		}
		// must be equal
		return 0;
	}
}
