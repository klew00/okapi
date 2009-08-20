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

import java.io.File;

import net.sf.okapi.common.Range;
import net.sf.okapi.common.resource.Code;
import net.sf.okapi.common.resource.Segment;
import net.sf.okapi.common.resource.TextContainer;
import net.sf.okapi.common.resource.TextFragment;
import net.sf.okapi.common.resource.TextFragment.TagType;
import net.sf.okapi.lib.segmentation.ISegmenter;
import net.sf.okapi.lib.segmentation.SRXDocument;

public class Main {
	public static void main (String[] args) {
		try {
			// Create and load the SRX document
			SRXDocument doc = new SRXDocument();
			File f = new File("myRules.srx");
			doc.loadRules(f.getAbsolutePath());
			
			// Obtain a segmenter for English
			ISegmenter segmenter = doc.compileLanguageRules("en", null);

			// Plain text case
			int count = segmenter.computeSegments("Part 1. Part 2.");
			System.out.println("count="+String.valueOf(count));
			for ( Range range : segmenter.getRanges() ) {
				System.out.println(String.format("start=%d, end=%d",
					range.start, range.end));
			}
			
			// TextContainer case
			TextContainer tc = new TextContainer();
			tc.append(TagType.OPENING, "span", "<span>");
			tc.append("Part 1.");
			tc.append(TagType.CLOSING, "span", "</span>");
			tc.append(" Part 2.");
			tc.append(TagType.PLACEHOLDER, "alone", "<alone/>");
			tc.append(" Part 3.");
			segmenter.computeSegments(tc);
			tc.createSegments(segmenter.getRanges());
			for ( Segment seg : tc.getSegments() ) {
				System.out.println("segment=[" + seg.toString() + "]");
			}

			// Accessing the segment from the coded text
			String text = tc.getCodedText();
			for ( int i=0; i<text.length(); i++ ) {
				if ( TextFragment.isMarker(text.charAt(i)) ) {
					i++; // Skip marker in all cases
					if ( text.charAt(i-1) == TextFragment.MARKER_SEGMENT ) {
						Code code = tc.getCode(text.charAt(i));
						int segIndex = Integer.valueOf(code.getData());
						Segment seg = tc.getSegments().get(segIndex);
						System.out.println("segment->[" + seg.toString() + "]");
					}
				}
			}
		}
		catch ( Throwable e ) {
			e.printStackTrace();
		}
	}

}
