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

package net.sf.okapi.filters.idml;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Stack;

import net.sf.okapi.common.resource.Code;
import net.sf.okapi.common.resource.TextFragment;

public class Simplifier {

	TextFragment oriFrag;
	ArrayDeque<Slice> slices;
	
	class Slice {
		
		public Slice (boolean isString) {
			this.isString = isString;
			if ( isString ) buffer = new StringBuffer();
		}
		
		boolean isString;
		StringBuffer buffer;
		
	}
	
	public void simplify (TextFragment fragment) {
		oriFrag = fragment;

		int state = 0;
		Stack<String> left = new Stack<String>();
		ArrayList<String> right = new ArrayList<String>();
		slices = new ArrayDeque<Slice>();
		String ctext = oriFrag.getCodedText();
		
		for ( int i=0; i<ctext.length(); i++ ) {
			switch ( ctext.charAt(i) ) {
			case TextFragment.MARKER_OPENING:
			case TextFragment.MARKER_CLOSING:
			case TextFragment.MARKER_ISOLATED:
				char m = ctext.charAt(i);
				if ( state == 0 ) {
					if ( left.isEmpty() ) state = 1; // Left side
					else state = 2; // Right side of the text
				}
				Code code = oriFrag.getCode(ctext.charAt(++i));
				String sig = String.format("%c%d", m, code.getId());
				if ( state == 1 ) { // Left mode: Stack the signatures
					left.push(sig);
				}
				else { // state = 2, right side
					if ( m != TextFragment.MARKER_ISOLATED ) {
						if ( !left.isEmpty() && right.isEmpty() ) {
							if ((( m == TextFragment.MARKER_CLOSING ) && ( left.peek().charAt(0) == TextFragment.MARKER_OPENING ))
								&& ( sig.substring(1).equals(left.peek().substring(1) )))
							{ // If we have open/close of the same code: pop it and go to the next
								left.pop();
								continue;
							}
							// Else: fall thru to fill right list
						}
						// Else: fall thru to fill right list
					}
					// Else: No more right/left matches, start right list
					right.add(sig);
				}
				break;
			default: // Text content
				if ( state == 2 ) { // End of right
					// left stack and right list have the non-matching codes
					//TODO: create slices
					// Clear the sides for next time
					left.clear();
					right.clear();
				}
				if ( state != 0 ) {
					state = 0; // Back to text mode
					slices.add(new Slice(true));
				}
				if ( state == 0 ) {
					slices.peekLast().buffer.append(ctext.charAt(i));
				}
			}
		}
		
		// Treat the possible leftover
		if ( state == 2 ) {
			
		}
		
	}
	
}
