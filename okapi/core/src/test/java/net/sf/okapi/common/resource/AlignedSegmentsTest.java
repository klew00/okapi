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
============================================================================*/

package net.sf.okapi.common.resource;

import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.filterwriter.GenericContent;
import static org.junit.Assert.*;

import org.junit.Test;

public class AlignedSegmentsTest {

    private static final LocaleId locFR = LocaleId.FRENCH;
    private static final LocaleId locES = LocaleId.SPANISH;
    private static final String TU1 = "tu1";
    private GenericContent fmt;

    public AlignedSegmentsTest () {
    	fmt = new GenericContent();
    }
    
    @Test
    public void loopThroughSegments () {
    	ITextUnit tu = createSegmentedTUAndTarget();
    	Segment trgSeg;
    	IAlignedSegments as = tu.getSegments();
    	for ( Segment srcSeg : as ) {
    		if ( srcSeg.id.equals("0") ) {
    			assertEquals("Part 1.", srcSeg.text.toString());
    			trgSeg = as.getCorrespondingTarget(srcSeg, locFR);
    			assertEquals("Trg 1.", trgSeg.text.toString());
    		}
    		else {
    			assertEquals("Part 2.", srcSeg.text.toString());
    			trgSeg = as.getCorrespondingTarget(srcSeg, locES);
    			assertEquals("Objetivo 2.", trgSeg.text.toString());
    		}
    	}
    }

    @Test
    public void getSegmentsTest () {
    	ITextUnit tu = new TextUnit4(TU1);
    	tu.setSourceContent(new TextFragment("text"));
    	IAlignedSegments as = tu.getSegments();
    	assertNotNull(as);
    }
    
    @Test
    public void removeSegmentsTest () {
    	ITextUnit tu = createSegmentedTU();
    	IAlignedSegments as = tu.getSegments();
    	Segment seg = as.getSource(0);
    	assertEquals("0", seg.id);
    	as.remove(seg);
    	seg = as.getSource(0);
    	assertEquals("s2", seg.id);
    }
    
    @Test
    public void removeSegmentsWithTargetTest () {
    	ITextUnit tu = createSegmentedTUAndTarget();
    	IAlignedSegments as = tu.getSegments();
    	Segment seg = as.getSource(0);
    	assertEquals("0", seg.id);
    	as.remove(seg);
    	ISegments segs = tu.getTargetSegments(locFR);
    	seg = segs.get(0);
    	assertEquals("s2", seg.id);
    }
    
    @Test
    public void insertSourceSegmentTest () {
    	ITextUnit tu = createSegmentedTUAndTarget();
    	IAlignedSegments as = tu.getSegments();
    	Segment seg1 = new Segment("newId");
    	as.insert(1, seg1);
    	Segment seg2 = as.getSource(1);
    	assertSame(seg1, seg2); // Check insertion
    	Segment seg3 = as.getSource(2);
    	assertEquals("s2", seg3.id); // Check old seg(1) was move down
    	Segment seg4 = as.getCorrespondingTarget(seg2, locFR);
    	assertEquals(seg1.id, seg4.id); // Check target was added
    }
    
    @Test
    public void insertSourceSegmentChangeIdTest () {
    	ITextUnit tu = createSegmentedTUAndTarget();
    	IAlignedSegments as = tu.getSegments();
    	Segment seg1 = new Segment("s2"); // "s2" id exists already
    	as.insert(1, seg1);
    	Segment seg2 = as.getSource(1);
    	assertSame(seg1, seg2);
    	assertEquals("1", seg2.id); // Id was changed to a valid one
    	Segment seg4 = as.getCorrespondingTarget(seg2, locFR);
    	assertEquals(seg1.id, seg4.id); // Check target was added with validated id
    }

    @Test
    public void insertSegmentsChangeIdTest () {
    	ITextUnit tu = createSegmentedTUAndTarget();
    	IAlignedSegments as = tu.getSegments();
    	Segment seg1 = new Segment("s2"); // "s2" id exists already
    	Segment trgSeg = new Segment("zzId", new TextFragment("[text]"));
    	as.insert(1, seg1, trgSeg, locFR);
    	Segment seg2 = as.getSource(1);
    	assertSame(seg1, seg2);
    	assertEquals("1", seg2.id); // Id was changed to a valid one
    	Segment seg4 = as.getCorrespondingTarget(seg2, locFR);
    	assertEquals("[text]", seg4.toString());
    	assertEquals(seg1.id, seg4.id); // Check target was added with validated id
    }

    @Test
    public void splitSourceSegmentTest () {
    	ITextUnit tu = createSegmentedTUAndTarget();
    	IAlignedSegments as = tu.getSegments();
    	Segment srcSeg = as.getSource(1);
    	Segment newSrcSeg = as.splitSource(srcSeg, 5); // "Part ][2."
    	assertNotNull(newSrcSeg);
    	assertEquals("2.", newSrcSeg.text.toString()); // Check new segment content
    	assertEquals("Part ", srcSeg.text.toString()); // Check original segment content
    	// Check the target
    	Segment newTrgSeg = as.getCorrespondingTarget(newSrcSeg, locFR);
    	assertNotNull(newTrgSeg);
    	assertTrue(newTrgSeg.text.isEmpty());
    	assertEquals("[Part 1.] a [Part ][2.]", fmt.printSegmentedContent(tu.getSource(), true));
    	assertEquals("[Trg 1.] a [Trg 2.][]", fmt.printSegmentedContent(tu.getTarget_DIFF(locFR), true));
    	assertEquals("[Objetivo 1.] a [Objetivo 2.][]", fmt.printSegmentedContent(tu.getTarget_DIFF(locES), true));
    }
    
    @Test
    public void splitTargetSegmentTest () {
    	ITextUnit tu = createSegmentedTUAndTarget();
    	IAlignedSegments as = tu.getSegments();
    	Segment trgSeg = tu.getTargetSegments(locFR).get(1);
    	Segment newTrgSeg = as.splitTarget(locFR, trgSeg, 4); // "Trg ][2."
    	assertNotNull(newTrgSeg);
    	assertEquals("2.", newTrgSeg.text.toString()); // Check new segment content
    	assertEquals("Trg ", trgSeg.text.toString()); // Check original segment content
    	// Check the source
    	Segment newSrcSeg = as.getCorrespondingSource(newTrgSeg);
    	assertNotNull(newSrcSeg);
    	assertTrue(newSrcSeg.text.isEmpty());
    	assertEquals("[Part 1.] a [Part 2.][]", fmt.printSegmentedContent(tu.getSource(), true));
    	assertEquals("[Trg 1.] a [Trg ][2.]", fmt.printSegmentedContent(tu.getTarget_DIFF(locFR), true));
    	assertEquals("[Objetivo 1.] a [Objetivo 2.][]", fmt.printSegmentedContent(tu.getTarget_DIFF(locES), true));
    }
    
    @Test
    public void setSourceTest () {
    	ITextUnit tu = createSegmentedTUAndTarget();
    	IAlignedSegments as = tu.getSegments();
    	Segment newSeg = new Segment("newId", new TextFragment("newText"));
    	as.setSource(1, newSeg);
    	Segment seg = as.getSource(1);
    	assertEquals("newText", seg.toString());
    	assertEquals("newId", seg.id);
    	seg = as.getCorrespondingTarget(newSeg, locES);
    	assertEquals("newId", seg.id);
    	seg = as.getCorrespondingTarget(newSeg, locFR);
    	assertEquals("newId", seg.id);
    }

    @Test
    public void setTargetTest () {
    	ITextUnit tu = createSegmentedTUAndTarget();
    	IAlignedSegments as = tu.getSegments();
    	Segment newSeg = new Segment("newId", new TextFragment("newText"));
    	as.setTarget(1, newSeg, locFR);
    	Segment seg = as.getSource(1);
    	assertEquals("newId", seg.id);
    	seg = as.getCorrespondingTarget(seg, locFR);
    	assertEquals("newText", seg.toString());
    	seg = as.getCorrespondingTarget(newSeg, locES);
    	assertNotNull(seg);
    	assertEquals("newId", seg.id);
    }

    @Test
    public void getSourceTest () {
    	ITextUnit tu = createSegmentedTUAndTarget();
    	IAlignedSegments as = tu.getSegments();
    	assertEquals("Part 2.", as.getSource(1).text.toString());
    }
    
    @Test
    public void getCorrespondingTargetTest () {
    	ITextUnit tu = createSegmentedTUAndTarget();
    	IAlignedSegments as = tu.getSegments();
    	Segment srcSeg = as.getSource(1);
    	Segment trgSeg = as.getCorrespondingTarget(srcSeg, locFR);
    	assertEquals("Trg 2.", trgSeg.text.toString());
    	trgSeg = as.getCorrespondingTarget(trgSeg, locES); // target on target
    	assertEquals("Objetivo 2.", trgSeg.text.toString());
    }
    
    @Test
    public void getCorrespondingSourceTest () {
    	ITextUnit tu = createSegmentedTUAndTarget();
    	IAlignedSegments as = tu.getSegments();
    	Segment srcSeg1 = as.getSource(1);
    	Segment trgSeg = as.getCorrespondingTarget(srcSeg1, locES);
    	Segment srcSeg2 = as.getCorrespondingSource(trgSeg);
    	assertSame(srcSeg1, srcSeg2);
    }
    
    @Test
    public void joinWithNextTest () {
    	ITextUnit tu = createSegmentedTUAndTarget();
    	IAlignedSegments as = tu.getSegments();
    	// First: add a new segment
    	as.append(new Segment("nId", new TextFragment("newSrcText")),
    		new Segment("nId", new TextFragment("newTrgText")), locFR);
    	assertEquals("[Part 1.] a [Part 2.][newSrcText]", fmt.printSegmentedContent(tu.getSource(), true));
    	assertEquals("[Trg 1.] a [Trg 2.][newTrgText]", fmt.printSegmentedContent(tu.getTarget_DIFF(locFR), true));
    	assertEquals("[Objetivo 1.] a [Objetivo 2.][]", fmt.printSegmentedContent(tu.getTarget_DIFF(locES), true));
    	// First join 
    	Segment srcSeg = as.getSource(1);
    	as.joinWithNext(srcSeg);
    	assertEquals("[Part 1.] a [Part 2.newSrcText]", fmt.printSegmentedContent(tu.getSource(), true));
    	assertEquals("[Trg 1.] a [Trg 2.newTrgText]", fmt.printSegmentedContent(tu.getTarget_DIFF(locFR), true));
    	assertEquals("[Objetivo 1.] a [Objetivo 2.]", fmt.printSegmentedContent(tu.getTarget_DIFF(locES), true));
    	// Second join 
    	srcSeg = as.getSource(0);
    	as.joinWithNext(srcSeg);
    	assertEquals("[Part 1. a Part 2.newSrcText]", fmt.printSegmentedContent(tu.getSource(), true));
    	assertEquals("[Trg 1. a Trg 2.newTrgText]", fmt.printSegmentedContent(tu.getTarget_DIFF(locFR), true));
    	assertEquals("[Objetivo 1. a Objetivo 2.]", fmt.printSegmentedContent(tu.getTarget_DIFF(locES), true));
    }

    @Test
    public void joinAllTest () {
    	ITextUnit tu = createSegmentedTUAndTarget();
    	IAlignedSegments as = tu.getSegments();
    	as.joinAll();
    	assertEquals("[Part 1. a Part 2.]", fmt.printSegmentedContent(tu.getSource(), true));
    	assertEquals("[Trg 1. a Trg 2.]", fmt.printSegmentedContent(tu.getTarget_DIFF(locFR), true));
    	assertEquals("[Objetivo 1. a Objetivo 2.]", fmt.printSegmentedContent(tu.getTarget_DIFF(locES), true));
    	
    }

    @Test
    public void appendSegmentTest1 () {
    	ITextUnit tu = createSegmentedTUAndTarget();
    	IAlignedSegments as = tu.getSegments();
    	Segment seg = new Segment("nId", new TextFragment("newSrcText"));
    	as.append(seg);
    	assertEquals("newSrcText", as.getSource(2).toString());
    	assertEquals("nId", as.getCorrespondingTarget(seg, locFR).id);
    	assertEquals("[Part 1.] a [Part 2.][newSrcText]", fmt.printSegmentedContent(tu.getSource(), true));
    	assertEquals("[Trg 1.] a [Trg 2.][]", fmt.printSegmentedContent(tu.getTarget_DIFF(locFR), true));
    	assertEquals("[Objetivo 1.] a [Objetivo 2.][]", fmt.printSegmentedContent(tu.getTarget_DIFF(locES), true));
    }
    
    @Test
    public void appendSegmentTest2 () {
    	ITextUnit tu = createSegmentedTUAndTarget();
    	IAlignedSegments as = tu.getSegments();
    	Segment seg = new Segment("nId", new TextFragment("newSrcText"));
    	as.append(seg, new Segment("nId", new TextFragment("newTrgText")), locFR);
    	assertEquals("newSrcText", as.getSource(2).toString());
    	assertEquals("nId", as.getCorrespondingTarget(seg, locFR).id);
    	assertEquals("newTrgText", as.getCorrespondingTarget(seg, locFR).toString());
    	assertEquals("[Part 1.] a [Part 2.][newSrcText]", fmt.printSegmentedContent(tu.getSource(), true));
    	assertEquals("[Trg 1.] a [Trg 2.][newTrgText]", fmt.printSegmentedContent(tu.getTarget_DIFF(locFR), true));
    	assertEquals("[Objetivo 1.] a [Objetivo 2.][]", fmt.printSegmentedContent(tu.getTarget_DIFF(locES), true));
    }
    
    private ITextUnit createSegmentedTU () {
    	ITextUnit tu = new TextUnit4("id", "Part 1.");
    	tu.getSource().getSegments().append(new Segment("s2", new TextFragment("Part 2.")), " a ");
    	return tu;
    }
    
    private ITextUnit createSegmentedTUAndTarget () {
    	ITextUnit tu = createSegmentedTU();
    	// Add the target segments
    	ISegments segs = tu.getTarget_DIFF(locFR).getSegments();
    	segs.get(0).text.append("Trg 1.");
    	segs.get(1).text.append("Trg 2.");
    	segs = tu.getTarget_DIFF(locES).getSegments();
    	segs.get(0).text.append("Objetivo 1.");
    	segs.get(1).text.append("Objetivo 2.");
    	return tu;
    }
}
