/*===========================================================================
  Copyright (C) 2010-2011 by the Okapi Framework contributors
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

import java.util.EnumSet;
import java.util.LinkedList;
import java.util.List;

import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.filterwriter.GenericContent;
import static net.sf.okapi.common.resource.IAlignedSegments.*;
import static net.sf.okapi.common.resource.IAlignedSegments.VariantOptions.*;
import static net.sf.okapi.common.resource.IAlignedSegments.CopyOptions.*;

import static org.junit.Assert.*;

import org.junit.Test;

public class AlignedSegmentsTest {

    //TODO tests to more thoroughly cover different permutations of VariantOptions
    // and CopyOptions


    private static final LocaleId locFR = LocaleId.FRENCH;
    private static final LocaleId locES = LocaleId.SPANISH;
    private static final LocaleId locEL = LocaleId.fromString("el-GR");
    private static final String TU1 = "tu1";
    private GenericContent fmt;

    public AlignedSegmentsTest () {
    	fmt = new GenericContent();
    }
    
    @Test
    public void loopThroughSegments () {

    	ITextUnit tu = createSegmentedTUAndTarget();
    	Segment trgSeg;
    	IAlignedSegments as = tu.getAlignedSegments();
    	for ( Segment srcSeg : as ) {
    		if ( srcSeg.id.equals("0") ) {
    			assertEquals("Part 1.", srcSeg.text.toString());
    			trgSeg = as.getCorrespondingTarget(srcSeg, locFR,
                                EnumSet.of(CANCEL_IF_MULTIPLE_TARGETS),
                                COPY_TO_NONE);
    			assertEquals("Trg 1.", trgSeg.text.toString());
    		}
    		else {
    			assertEquals("Part 2.", srcSeg.text.toString());
    			trgSeg = as.getCorrespondingTarget(srcSeg, locES,
                                EnumSet.of(CANCEL_IF_MULTIPLE_TARGETS),
                                COPY_TO_NONE);
    			assertEquals("Objetivo 2.", trgSeg.text.toString());
    		}
    	}
    }

    @Test
    public void getSegmentsTest () {
    	ITextUnit tu = new TextUnit(TU1);
    	tu.setSourceContent(new TextFragment("text"));
    	IAlignedSegments as = tu.getAlignedSegments();
    	assertNotNull(as);
    }

    @Test
    public void removeSegmentsTest () {
    	ITextUnit tu = createSegmentedTU();
    	IAlignedSegments as = tu.getAlignedSegments();
    	Segment seg = as.getSource(0, locFR);
    	assertEquals("0", seg.id);
    	as.remove(seg, locFR, MODIFY_ALL);
    	seg = as.getSource(0, locFR);
    	assertEquals("s2", seg.id);
    }

    @Test
    public void removeSegmentsWithTargetTest () {
    	ITextUnit tu = createSegmentedTUAndTarget();
    	IAlignedSegments as = tu.getAlignedSegments();
    	Segment seg = as.getSource(0, locFR);
    	assertEquals("0", seg.id);
    	as.remove(seg, locFR, MODIFY_ALL);
    	ISegments segs = tu.getTargetSegments(locFR);
    	seg = segs.get(0);
    	assertEquals("s2", seg.id);
    }

    @Test
    public void insertSourceSegmentTest () {
    	ITextUnit tu = createSegmentedTUAndTarget();
    	IAlignedSegments as = tu.getAlignedSegments();
    	Segment seg1 = new Segment("newId");
    	as.insert(1, seg1, null, locFR, MODIFY_ALL, EnumSet.of(COPY_TO_SOURCE));
    	Segment seg2 = as.getSource(1, locFR);
    	assertSame(seg1, seg2); // Check insertion
    	Segment seg3 = as.getSource(2, locFR);
    	assertEquals("s2", seg3.id); // Check old seg(1) was move down
    	Segment seg4 = as.getCorrespondingTarget(seg2, locFR, MODIFY_ALL, COPY_TO_NONE);
    	assertEquals(seg1.id, seg4.id); // Check target was added
    }

    @Test
    public void insertSourceSegmentChangeIdTest () {
    	ITextUnit tu = createSegmentedTUAndTarget();
    	IAlignedSegments as = tu.getAlignedSegments();
    	Segment seg1 = new Segment("s2"); // "s2" id exists already
    	as.insert(1, seg1, null, locFR, MODIFY_ALL, EnumSet.of(COPY_TO_SOURCE));
    	Segment seg2 = as.getSource(1, locFR);
    	assertSame(seg1, seg2);
    	assertEquals("1", seg2.id); // Id was changed to a valid one
    	Segment seg4 = as.getCorrespondingTarget(seg2, locFR, MODIFY_ALL, COPY_TO_NONE);
    	assertEquals(seg1.id, seg4.id); // Check target was added with validated id
    }

    @Test
    public void insertSegmentsChangeIdTest () {
    	ITextUnit tu = createSegmentedTUAndTarget();
    	IAlignedSegments as = tu.getAlignedSegments();
    	Segment insertSrcSeg = new Segment("s2"); // "s2" id exists already
    	Segment insertTrgSeg = new Segment("zzId", new TextFragment("[text]"));
    	as.insert(1, insertSrcSeg, insertTrgSeg, locFR, MODIFY_ALL, COPY_TO_SOURCE_AND_TARGET);
    	Segment srcAtInsertedIndex = as.getSource(1, locFR);
    	assertSame("The given source should be inserted at the index (not a copy)", insertSrcSeg, srcAtInsertedIndex);
    	assertEquals("1", srcAtInsertedIndex.id); // Id was changed to a valid one
    	Segment trgAtInsertedIndex = as.getCorrespondingTarget(srcAtInsertedIndex, locFR, MODIFY_ALL, COPY_TO_NONE);
    	assertEquals("[text]", trgAtInsertedIndex.toString());
    	assertEquals(insertSrcSeg.id, trgAtInsertedIndex.id); // Check target was added with validated id
    }

    @Test
    public void splitSourceSegmentTest () {
    	ITextUnit tu = createSegmentedTUAndTarget();
    	IAlignedSegments as = tu.getAlignedSegments();
    	Segment srcSeg = as.getSource(1, locFR);
    	Segment newSrcSeg = as.splitSource(locFR, srcSeg, 5, MODIFY_ALL, COPY_TO_NONE); // "Part ][2."
    	assertNotNull(newSrcSeg);
    	assertEquals("2.", newSrcSeg.text.toString()); // Check new segment content
    	assertEquals("Part ", srcSeg.text.toString()); // Check original segment content
    	// Check the target
    	Segment newTrgSeg = as.getCorrespondingTarget(newSrcSeg, locFR, MODIFY_ALL, COPY_TO_NONE);
    	assertNotNull(newTrgSeg);
    	assertTrue(newTrgSeg.text.isEmpty());
    	assertEquals("[Part 1.] a [Part ][2.]", fmt.printSegmentedContent(tu.getSource(), true));
    	assertEquals("[Trg 1.] a [Trg 2.][]", fmt.printSegmentedContent(tu.getTarget(locFR), true));
    	assertEquals("[Objetivo 1.] a [Objetivo 2.][]", fmt.printSegmentedContent(tu.getTarget(locES), true));
    }

    @Test
    public void splitTargetSegmentTest () {
    	ITextUnit tu = createSegmentedTUAndTarget();
    	IAlignedSegments as = tu.getAlignedSegments();
    	Segment trgSeg = tu.getTargetSegments(locFR).get(1);
    	Segment newTrgSeg = as.splitTarget(locFR, trgSeg, 4, MODIFY_ALL, COPY_TO_NONE); // "Trg ][2."
    	assertNotNull(newTrgSeg);
    	assertEquals("2.", newTrgSeg.text.toString()); // Check new segment content
    	assertEquals("Trg ", trgSeg.text.toString()); // Check original segment content
    	// Check the source
    	Segment newSrcSeg = as.getCorrespondingSource(newTrgSeg, locFR, MODIFY_ALL, COPY_TO_NONE);
    	assertNotNull(newSrcSeg);
    	assertTrue(newSrcSeg.text.isEmpty());
    	assertEquals("[Part 1.] a [Part 2.][]", fmt.printSegmentedContent(tu.getSource(), true));
    	assertEquals("[Trg 1.] a [Trg ][2.]", fmt.printSegmentedContent(tu.getTarget(locFR), true));
    	assertEquals("[Objetivo 1.] a [Objetivo 2.][]", fmt.printSegmentedContent(tu.getTarget(locES), true));
    }

    @Test
    public void setSourceTest () {
    	ITextUnit tu = createSegmentedTUAndTarget();
    	IAlignedSegments as = tu.getAlignedSegments();
    	Segment newSeg = new Segment("newId", new TextFragment("newText"));
    	as.setSegment(1, newSeg, locFR, EnumSet.of(MODIFY_SOURCE), MODIFY_ALL);
    	Segment seg = as.getSource(1, locFR);
    	assertEquals("newText", seg.toString());
    	assertEquals("newId", seg.id);
    	seg = as.getCorrespondingTarget(newSeg, locES, MODIFY_ALL, COPY_TO_NONE);
    	assertEquals("newId", seg.id);
    	seg = as.getCorrespondingTarget(newSeg, locFR, MODIFY_ALL, COPY_TO_NONE);
    	assertEquals("newId", seg.id);
    }

    @Test
    public void setTargetTest () {
    	ITextUnit tu = createSegmentedTUAndTarget();
    	IAlignedSegments as = tu.getAlignedSegments();
    	Segment newSeg = new Segment("newId", new TextFragment("newText"));
    	as.setSegment(1, newSeg, locFR, EnumSet.of(MODIFY_TARGET), MODIFY_ALL);
    	Segment seg = as.getSource(1, locFR);
    	assertEquals("newId", seg.id);
    	seg = as.getCorrespondingTarget(seg, locFR, MODIFY_ALL, COPY_TO_NONE);
    	assertEquals("newText", seg.toString());
    	seg = as.getCorrespondingTarget(newSeg, locES, MODIFY_ALL, COPY_TO_NONE);
    	assertNotNull(seg);
    	assertEquals("newId", seg.id);
    }

    @Test
    public void getSourceTest () {
    	ITextUnit tu = createSegmentedTUAndTarget();
    	IAlignedSegments as = tu.getAlignedSegments();
    	assertEquals("Part 2.", as.getSource(1, locFR).text.toString());
    }

    @Test
    public void getCorrespondingTargetTest () {
    	ITextUnit tu = createSegmentedTUAndTarget();
    	IAlignedSegments as = tu.getAlignedSegments();
    	Segment srcSeg = as.getSource(1, locFR);
    	Segment trgSeg = as.getCorrespondingTarget(srcSeg, locFR, MODIFY_ALL, COPY_TO_NONE);
    	assertEquals("Trg 2.", trgSeg.text.toString());
    	trgSeg = as.getCorrespondingTarget(trgSeg, locES, MODIFY_ALL, COPY_TO_NONE); // target on target
    	assertEquals("Objetivo 2.", trgSeg.text.toString());
    }

    @Test
    public void getCorrespondingSourceTest () {
    	ITextUnit tu = createSegmentedTUAndTarget();
    	IAlignedSegments as = tu.getAlignedSegments();
    	Segment srcSeg1 = as.getSource(1, locFR);
    	Segment trgSeg = as.getCorrespondingTarget(srcSeg1, locES, MODIFY_ALL, COPY_TO_NONE);
    	Segment srcSeg2 = as.getCorrespondingSource(trgSeg, locFR, MODIFY_ALL, COPY_TO_NONE);
    	assertSame(srcSeg1, srcSeg2);
    }

    @Test
    public void joinWithNextTest () {
    	ITextUnit tu = createSegmentedTUAndTarget();
    	IAlignedSegments as = tu.getAlignedSegments();
    	// First: add a new segment
    	as.append(new Segment("nId", new TextFragment("newSrcText")),
    		new Segment("nId", new TextFragment("newTrgText")), locFR,
                MODIFY_ALL, COPY_TO_SOURCE_AND_TARGET);
    	assertEquals("[Part 1.] a [Part 2.][newSrcText]", fmt.printSegmentedContent(tu.getSource(), true));
    	assertEquals("[Trg 1.] a [Trg 2.][newTrgText]", fmt.printSegmentedContent(tu.getTarget(locFR), true));
    	assertEquals("[Objetivo 1.] a [Objetivo 2.][]", fmt.printSegmentedContent(tu.getTarget(locES), true));
    	// First join
    	Segment srcSeg = as.getSource(1, locFR);
    	as.joinWithNext(srcSeg, locFR, MODIFY_ALL);
    	assertEquals("[Part 1.] a [Part 2.newSrcText]", fmt.printSegmentedContent(tu.getSource(), true));
    	assertEquals("[Trg 1.] a [Trg 2.newTrgText]", fmt.printSegmentedContent(tu.getTarget(locFR), true));
    	assertEquals("[Objetivo 1.] a [Objetivo 2.]", fmt.printSegmentedContent(tu.getTarget(locES), true));
    	// Second join
    	srcSeg = as.getSource(0, locFR);
    	as.joinWithNext(srcSeg, locFR, MODIFY_ALL);
    	assertEquals("[Part 1. a Part 2.newSrcText]", fmt.printSegmentedContent(tu.getSource(), true));
    	assertEquals("[Trg 1. a Trg 2.newTrgText]", fmt.printSegmentedContent(tu.getTarget(locFR), true));
    	assertEquals("[Objetivo 1. a Objetivo 2.]", fmt.printSegmentedContent(tu.getTarget(locES), true));
    }

    @Test
    public void joinAllTest () {
    	ITextUnit tu = createSegmentedTUAndTarget();
    	IAlignedSegments as = tu.getAlignedSegments();
    	as.joinAll(locFR, MODIFY_ALL);
    	assertEquals("[Part 1. a Part 2.]", fmt.printSegmentedContent(tu.getSource(), true));
    	assertEquals("[Trg 1. a Trg 2.]", fmt.printSegmentedContent(tu.getTarget(locFR), true));
    	assertEquals("[Objetivo 1. a Objetivo 2.]", fmt.printSegmentedContent(tu.getTarget(locES), true));

    }

    @Test
    public void appendSegmentTest1 () {
    	ITextUnit tu = createSegmentedTUAndTarget();
    	IAlignedSegments as = tu.getAlignedSegments();
    	Segment seg = new Segment("nId", new TextFragment("newSrcText"));
    	as.append(seg, null, locFR, MODIFY_ALL, EnumSet.of(COPY_TO_SOURCE));
        //why isn't the thing appending?
    	assertEquals("newSrcText", as.getSource(2, locFR).toString());
    	assertEquals("nId", as.getCorrespondingTarget(seg, locFR, MODIFY_ALL, COPY_TO_NONE).id);
    	assertEquals("[Part 1.] a [Part 2.][newSrcText]", fmt.printSegmentedContent(tu.getSource(), true));
    	assertEquals("[Trg 1.] a [Trg 2.][]", fmt.printSegmentedContent(tu.getTarget(locFR), true));
    	assertEquals("[Objetivo 1.] a [Objetivo 2.][]", fmt.printSegmentedContent(tu.getTarget(locES), true));
    }

    @Test
    public void appendSegmentTest2 () {
    	ITextUnit tu = createSegmentedTUAndTarget();
    	IAlignedSegments as = tu.getAlignedSegments();
    	Segment seg = new Segment("nId", new TextFragment("newSrcText"));
    	as.append(seg, new Segment("nId", new TextFragment("newTrgText")), locFR, MODIFY_ALL, COPY_TO_SOURCE_AND_TARGET);
    	assertEquals("newSrcText", as.getSource(2, locFR).toString());
    	assertEquals("nId", as.getCorrespondingTarget(seg, locFR, MODIFY_ALL, COPY_TO_NONE).id);
    	assertEquals("newTrgText", as.getCorrespondingTarget(seg, locFR, MODIFY_ALL, COPY_TO_NONE).toString());
    	assertEquals("[Part 1.] a [Part 2.][newSrcText]", fmt.printSegmentedContent(tu.getSource(), true));
    	assertEquals("[Trg 1.] a [Trg 2.][newTrgText]", fmt.printSegmentedContent(tu.getTarget(locFR), true));
    	assertEquals("[Objetivo 1.] a [Objetivo 2.][]", fmt.printSegmentedContent(tu.getTarget(locES), true));
    }

    @Test
    public void alignWithAlignedPairs() {
        //create aligned pairs
        Segment srcSeg, trgSeg;
        List<AlignedPair> alignedPairs = new LinkedList<AlignedPair>();

        String[] source = {"apSource 1.", "apSource 2.", "apSource 3."};
        String[] target = {"apTarg 1.", "apTarg 2.", "apTarg 3."};

        srcSeg = new Segment("sA", new TextFragment(source[0]));
        trgSeg = new Segment("sAlpha", new TextFragment(target[0]));
        alignedPairs.add(new AlignedPair(srcSeg, trgSeg, locEL));

        srcSeg = new Segment("sB", new TextFragment(source[1]));
        trgSeg = new Segment("sBeta", new TextFragment(target[1]));
        alignedPairs.add(new AlignedPair(srcSeg, trgSeg, locEL));

        srcSeg = new Segment("sC", new TextFragment(source[2]));
        trgSeg = new Segment("sChuppa?", new TextFragment(target[2]));
        alignedPairs.add(new AlignedPair(srcSeg, trgSeg, locEL));
        
        //create tu
        ITextUnit tu = createSegmentedTUAndTarget();

        //call method
        tu.getAlignedSegments().align(alignedPairs, locEL);
        
        //creates if not present
        assertTrue("a new variant source should be created if none is present "
                 + "for the given locale",
                   tu.getVariantSources().hasVariant(locEL));

        assertTrue("a new target should be created if nont is present for the given "
                 + "locale",
                   tu.hasTarget(locEL));

        //replaces content

        String[] actualSources = {
            tu.getVariantSources().get(locEL).getSegments().get(0).toString(),
            tu.getVariantSources().get(locEL).getSegments().get(1).toString(),
            tu.getVariantSources().get(locEL).getSegments().get(2).toString()};

        assertArrayEquals("the source segments of the aligned pairs should be "
                        + "used in the source content",
                        source,
                        actualSources);

        String[] actualTargets = {
            tu.getTarget(locEL).getSegments().get(0).toString(),
            tu.getTarget(locEL).getSegments().get(1).toString(),
            tu.getTarget(locEL).getSegments().get(2).toString()};

        assertArrayEquals("the target segments of the aligned pairs should be "
                        + "used in the target content",
                          target,
                          actualTargets);

        //target is aligned after
        assertEquals("the target should have a status of ALIGNED after the "
                   + "align() method is called",
                     AlignmentStatus.ALIGNED,
                     tu.getTarget(locEL).getSegments().getAlignmentStatus());

        //default source left alone
        assertEquals("the default source should not be changed by this method",
                     "Part 1.",
                     tu.getSource().getFirstSegment().toString());

        //other locales left alone
        assertEquals("the default source should not be changed by this method",
                     "Trg 1.",
                     tu.getTarget(locFR).getFirstSegment().toString());

        assertEquals("the default source should not be changed by this method",
                     "Objetivo 1.",
                     tu.getTarget(locES).getFirstSegment().toString());

    }

    @Test
    public void alignCollapseAll() {
        ITextUnit tu = createSegmentedTUAndTarget();
        tu.getAlignedSegments().alignCollapseAll(locFR, MODIFY_AS_VARIANT);

        //locFR should be aligned, with source and target collapsed
        assertTrue("a variant source should exist after calling a method with "
                 + "MODIFY_AS_VARIANT",
                   tu.hasVariantSources());

        assertTrue("the source content should be one segment after alignCollapseAll()",
                   tu.getVariantSources().get(locFR).contentIsOneSegment());

        assertFalse("the target should not be flagged as segmented after it "
                  + "has been collapsed",
                    tu.getVariantSources().get(locFR).hasBeenSegmented());
        
        
        assertTrue("the target content should be one segment after alignCollapseAll()",
                   tu.getTarget(locFR).contentIsOneSegment());

        assertFalse("the target should not be flagged as segmented after it "
                  + "has been collapsed",
                    tu.getTarget(locFR).hasBeenSegmented());

        assertEquals("the target should be marked as ALIGNED after alignCollapseAll()",
                     AlignmentStatus.ALIGNED,
                     tu.getTarget(locFR).getSegments().getAlignmentStatus());

        //default source should not be collapsed
        assertFalse("the default source should not be changed when the method "
                  + "is called with MODIFY_AS_VARIANT",
                    tu.getSource().contentIsOneSegment());
        
        //locES should not be collapsed or aligned
        assertFalse("targets of other locales should not be changed when the "
                  + "method is called with MODIFY_AS_VARIANT",
                    tu.getTarget(locES).contentIsOneSegment());

        assertEquals("other targets should not be marked as ALIGNED when the "
                   + "method is called with MODIFY_AS_VARIANT",
                     AlignmentStatus.NOT_ALIGNED,
                     tu.getTarget(locES).getSegments().getAlignmentStatus());
        
    }

    private ITextUnit createSegmentedTU () {
    	ITextUnit tu = new TextUnit("id", "Part 1.");
    	tu.getSource().getSegments().append(new Segment("s2", new TextFragment("Part 2.")), " a ");
    	return tu;
    }
    
    private ITextUnit createSegmentedTUAndTarget () {
    	ITextUnit tu = createSegmentedTU();
    	// Add the target segments
    	ISegments segs = tu.getTargetSegments(locFR);
    	segs.get(0).text.append("Trg 1.");
    	segs.get(1).text.append("Trg 2.");
    	segs = tu.getTargetSegments(locES);
    	segs.get(0).text.append("Objetivo 1.");
    	segs.get(1).text.append("Objetivo 2.");
    	return tu;
    }
}
