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

package net.sf.okapi.common.resource;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import net.sf.okapi.common.ISegmenter;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.exceptions.OkapiMisAlignmentException;

/**
 * EXPERIMENTAL implementation. Do not use yet.
 *
 * Provides a standard implementation of the IAlignedSegments interface that
 * aims to work with targets using either the default source or a variant source
 */
public class AlignedSegments implements IAlignedSegments {
    private LocaleId trgLoc;
    private ITextUnit myParent;
    private TextContainer source;
    private Map<LocaleId, TextContainer> targets;

    public AlignedSegments(ITextUnit parent, LocaleId targetLocale) {
        myParent = parent;
        trgLoc = targetLocale;
        source = myParent.getVariantSources().getSource(trgLoc);
        targets = new HashMap<LocaleId, TextContainer>();
        for (LocaleId loc : myParent.getTargetLocales()) {
            targets.put(loc, myParent.getTarget_DIFF(loc));
        }
    }

    //TODO add method (to interface) to refresh the source, to be used when
    // the source is deleted (so it will get the default source) or if the
    // source object changes.
    // That or just use myParent.getCustomSources().getSource(trgLoc) every time
    // but that's rather tedious


    //Temporary methods to get things working quick:
    private TextContainer getTarget_DIFF(LocaleId targetLocale) {
        return myParent.getTarget_DIFF(targetLocale);
    }

    private Set<LocaleId> getTargetLocales() {

        //checking if these are the same object (using the default source)
        if (source == myParent.getSource()) {
            Set<LocaleId> allTargs = myParent.getTargetLocales();

            //only want to return targets associated with the default source
            //so remove any with custom source
            allTargs.removeAll(myParent.getVariantSources().getTargetLocalesWithVariantSource());
            return allTargs;
        } else {
            //must be a custom source, so only return the associated target
            Set<LocaleId> oneTarg = new HashSet<LocaleId>();
            oneTarg.add(trgLoc);
            return oneTarg;
        }
    }


    @Override
    public Segment splitTarget (LocaleId trgLoc,
            Segment trgSeg,
            int splitPos)
    {
            ISegments trgSegs = myParent.getTarget_DIFF(trgLoc).getSegments();
            int segIndex = trgSegs.getIndex(trgSeg.id);
            if ( segIndex == -1 ) return null; // Not a segment in this container.
            int partIndex = trgSegs.getPartIndex(segIndex);
            // Split the segment (with no spanned part)
            myParent.getTarget_DIFF(trgLoc).split(partIndex, splitPos, splitPos, false);
            // New segment is on the right of original (so: segIndex+1)
            Segment newTrgSeg = trgSegs.get(segIndex+1);

            // Create the corresponding source segment
            Segment srcSeg = getCorrespondingSource(trgSeg);
            ISegments srcSegs = myParent.getVariantSources().getSource(trgLoc).getSegments();
            segIndex = srcSegs.getIndex(srcSeg.id);
            srcSegs.insert(segIndex+1, new Segment(newTrgSeg.id));

            // Create the corresponding segments in the other targets
            //TODO this should only be done if source is the default source,
            // and should not be done to targets that have a custom source
            for ( LocaleId loc : getTargetLocales() ) {
                    if ( loc.equals(trgLoc) ) continue;
                    Segment otherTrgSeg = getCorrespondingTarget(srcSeg, loc);
                    trgSegs = targets.get(loc).getSegments();
                    segIndex = trgSegs.getIndex(otherTrgSeg.id);
                    trgSegs.insert(segIndex+1, new Segment(newTrgSeg.id));
            }
            return newTrgSeg;
    }

    @Override
    public Segment splitSource (Segment srcSeg,
            int splitPos)
    {
            ISegments srcSegs = source.getSegments();
            int segIndex = srcSegs.getIndex(srcSeg.id);
            if ( segIndex == -1 ) return null; // Not a segment in this container.
            int partIndex = srcSegs.getPartIndex(segIndex);
            // Split the segment (with no spanned part)
            source.split(partIndex, splitPos, splitPos, false);
            // New segment is on the right of original (so: segIndex+1)
            Segment newSrcSeg = srcSegs.get(segIndex+1);

            // Create empty new segments for each target
            //TODO this should only be done if source is the default source,
            // and should not be done to targets that have a custom source
            for ( LocaleId loc : getTargetLocales() ) {
                    Segment trgSeg = getCorrespondingTarget(srcSeg, loc);
                    TextContainer tc = targets.get(loc);
                    ISegments trgSegs = tc.getSegments();
                    segIndex = trgSegs.getIndex(trgSeg.id);
                    trgSegs.insert(segIndex+1, new Segment(newSrcSeg.id));
            }
            return newSrcSeg;
    }

    @Override
    public void setTarget (int index,
            Segment trgSeg,
            LocaleId trgLoc)
    {
            ISegments trgSegs = getTarget_DIFF(trgLoc).getSegments();
            // Get the existing segment's ID
            String oldId = trgSegs.get(index).id;
            // Set the new segment. its ID is updated internally if needed
            trgSegs.set(index, trgSeg);
            if ( !oldId.equals(trgSeg.id) ) {
                    // Change the source ID too
                    Segment srcSeg = source.getSegments().get(oldId);
                    srcSeg.id = trgSeg.id;
                    // If needed update the target IDs for that segment
                    //TODO only do this with the default source to targets without custom source
                    for ( LocaleId loc : getTargetLocales() ) {
                            if ( loc.equals(trgLoc) ) continue;
                            ISegments otherSegs = targets.get(loc).getSegments();
                            Segment otherSeg = otherSegs.get(oldId);
                            otherSeg.id = trgSeg.id;
                    }
            }
    }

    @Override
    public void setSource (int index,
            Segment srcSeg)
    {
            ISegments srcSegs = source.getSegments();
            // Get the existing segment's ID
            String oldId = srcSegs.get(index).id;
            // Set the new segment. its ID is updated internally if needed
            srcSegs.set(index, srcSeg);
            if ( !oldId.equals(srcSeg.id) ) {
                    // If needed update the target IDs for that segment
                //TODO only do this with the default source to targets without custom source
                    for ( LocaleId loc : getTargetLocales() ) {
                            ISegments trgSegs = targets.get(loc).getSegments();
                            Segment trgSeg = trgSegs.get(oldId);
                            trgSeg.id = srcSeg.id;
                    }
            }
    }

    @Override
    public void segmentTarget (ISegmenter segmenter,
            LocaleId targetLocale)
    {
            TextContainer tc = getTarget_DIFF(targetLocale);
            segmenter.computeSegments(tc);
            tc.getSegments().create(segmenter.getRanges());
//TODO: invalidate source and other targets? or this one.
// but then there is no way to call segmentTarget and get all in synch
    }

    @Override
    public void segmentSource (ISegmenter segmenter) {
            segmenter.computeSegments(source);
            source.getSegments().create(segmenter.getRanges());
    }

    @Override
    public boolean remove (Segment seg) {
            int count = 0;
            // Remove the source segment
            ISegments srcSegs = source.getSegments();
            int n = srcSegs.getIndex(seg.id);
            if ( n > -1 ) {
                    n = srcSegs.getPartIndex(n);
                    source.remove(n);
                    count++;
            }
            // Remove the same segment in the target
            //TODO only do this with the default source to targets without custom source
            for ( LocaleId loc : getTargetLocales() ) {
                    TextContainer tc = targets.get(loc);
                    ISegments trgSegs = tc.getSegments();
                    n = trgSegs.getIndex(seg.id);
                    if ( n > -1 ) {
                            n = trgSegs.getPartIndex(n);
                            tc.remove(n);
                            count++;
                    }
            }
            return (count>0);
    }

    @Override
    public void joinWithNext (Segment seg) {
            ISegments srcSegs = source.getSegments();
            int n = srcSegs.getIndex(seg.id);
            if ( n == -1 ) return; // Not found
            srcSegs.joinWithNext(n);

            // Do the same for the target
            //TODO only do this with the default source to targets without custom source
            for ( LocaleId loc : getTargetLocales() ) {
                    ISegments trgSegs = targets.get(loc).getSegments();
                    // Get the target index, skip it if not found
                    if ( (n = trgSegs.getIndex(seg.id)) == -1 ) continue;
                    trgSegs.joinWithNext(n);
            }
    }

    @Override
    public void joinAll () {
            source.joinAll();
            for ( LocaleId loc : getTargetLocales() ) {
                    targets.get(loc).joinAll();
            }
    }

    @Override
    public void insert (int index,
            Segment srcSeg,
            Segment trgSeg,
            LocaleId trgLoc)
    {
        // Insert the source segment
        ISegments segs = source.getSegments();
        Segment currentSrc = segs.get(index);
        segs.insert(index, srcSeg);
        String srcId = srcSeg.id; // Get validated id

        // Add empty segments in targets
        for ( LocaleId loc : getTargetLocales() ) {
            segs = targets.get(loc).getSegments();
            // Get the corresponding target segment based on the original source segment id
            Segment currentTrg = segs.get(currentSrc.id);

            // Prepare the target segment
            Segment newSeg = null;
            if (( trgLoc != null ) && trgLoc.equals(loc) ) {
                newSeg = trgSeg;
                newSeg.id = srcId;
            }
            if ( newSeg == null ) {
                newSeg = new Segment(srcId);
            }

            if ( currentTrg == null ) {
                // If it does not exists: add the new target at the end
                segs.append(newSeg);
            }
            else { // If it exists
                // Get its index position
                int n = segs.getIndex(currentTrg.id);
                // And insert a new segment there
                segs.insert(n, newSeg);
            }
        }
    }

    @Override
    public void insert (int index,
            Segment srcSeg)
    {
        insert(index, srcSeg, null, null);
    }

    @Override
    public Segment getSource (int index) {
        return source.getSegments().get(index);
    }

    @Override
    public Segment getCorrespondingTarget (Segment srcSeg,
            LocaleId trgLoc)
    {
        // Get the target segments (creates them if needed)
        ISegments trgSegs = getTarget_DIFF(trgLoc).getSegments();
        Segment res = trgSegs.get(srcSeg.id);
        if ( res == null ) { // If no corresponding segment found: create one
            res = new Segment(srcSeg.id);
            trgSegs.append(res);
        }
        return res;
    }

    @Override
    public Segment getCorrespondingSource (Segment trgSeg) {
        Segment res = source.getSegments().get(trgSeg.id);
        if ( res == null ) { // If no corresponding segment found: create one
            res = new Segment(trgSeg.id);
            source.getSegments().append(res);
        }
        return res;
    }

    @Override
    public AlignmentStatus getAlignmentStatus () {
        for ( LocaleId loc : getTargetLocales() ) {
            ISegments trgSegs = targets.get(loc).getSegments();
            if (trgSegs.getAlignmentStatus() == AlignmentStatus.NOT_ALIGNED) {
                return AlignmentStatus.NOT_ALIGNED;
            }
        }
        return AlignmentStatus.ALIGNED;
    }

    @Override
    public void append (Segment srcSeg,
            Segment trgSeg,
            LocaleId trgLoc)
    {
        // Append the segment to the source
        source.getSegments().append(srcSeg);
        // Make sure the target segment id matches the source
        trgSeg.id = srcSeg.id;
        // Append a new empty segment to all targets
        for ( LocaleId loc : getTargetLocales() ) {
            ISegments trgSegs = targets.get(loc).getSegments();
            if ( loc.equals(trgLoc) ) trgSegs.append(trgSeg);
            else trgSegs.append(new Segment(srcSeg.id));
        }
    }

    @Override
    public void append (Segment srcSeg) {
        // Append the segment to the source
        source.getSegments().append(srcSeg);
        // Append a new empty segment to all targets
        for ( LocaleId loc : getTargetLocales() ) {
            ISegments trgSegs = targets.get(loc).getSegments();
            trgSegs.append(new Segment(srcSeg.id));
        }
    }

    @Override
    public Iterator<Segment> iterator () {
        return source.getSegments().iterator();
    }

    @Override
    public void align (List<AlignedPair> alignedSegmentPairs,
            LocaleId trgLoc)
    {

        // these target segments are now aligned with their source counterparts
        targets.get(trgLoc).getSegments().setAlignmentStatus(AlignmentStatus.ALIGNED);
    }

    /**
     * Force one to one alignment. Assume that both source and target
     * have the same number of segments.
     *
     * @param trgLoc target locale used to align with the source
     */
    @Override
    public void align(LocaleId trgLoc) {
        Iterator<Segment> srcSegsIt = source.getSegments().iterator();
        Iterator<Segment> trgSegsIt = targets.get(trgLoc).getSegments().iterator();
        while (srcSegsIt.hasNext()) {
            try {
                Segment srcSeg = srcSegsIt.next();
                Segment trgSeg = trgSegsIt.next();
                trgSeg.id = srcSeg.id;
            } catch (NoSuchElementException e) {
                throw new OkapiMisAlignmentException("Different number of source and target segments", e);
            }
        }

        // these target segments are now aligned with their source counterparts
        targets.get(trgLoc).getSegments().setAlignmentStatus(AlignmentStatus.ALIGNED);
    }

    /**
     * Collapse all segments for the source and target
     */
    @Override
    public void alignCollapseAll(LocaleId trgLoc) {
        // these target segments are now aligned with their source counterparts
        targets.get(trgLoc).getSegments().setAlignmentStatus(AlignmentStatus.ALIGNED);
    }
};