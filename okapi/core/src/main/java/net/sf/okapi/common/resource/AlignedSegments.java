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

import java.util.EnumSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import static net.sf.okapi.common.IResource.*;
import static net.sf.okapi.common.resource.IAlignedSegments.VariantOptions.*;
import static net.sf.okapi.common.resource.IAlignedSegments.CopyOptions.*;
import net.sf.okapi.common.ISegmenter;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.exceptions.OkapiMisAlignmentException;

//TODO code will be more readable and maintainable after significant refactoring.

/**
 * EXPERIMENTAL implementation, Do not use yet.
 * <p>
 * Provides a standard implementation of the IAlignedSegments interface that
 * works with variant sources.
 * <p>
 * Currently tightly coupled to ITextUnit.
 */
public class AlignedSegments implements IAlignedSegments {
    private ITextUnit myParent;

    public AlignedSegments(ITextUnit parent) {
        myParent = parent;
    }

    @Override
    public void append(Segment srcSeg,
                       Segment trgSeg,
                       LocaleId trgLoc,
                       EnumSet<VariantOptions> variantOptions,
                       EnumSet<CopyOptions> copyOptions) {

        if (srcSeg == null && trgSeg == null)
            throw new IllegalArgumentException("srcSeg and trgSeg cannot both be null");

        insertOrAppend(true, -1, srcSeg, trgSeg, trgLoc, variantOptions, copyOptions);
    }

    @Override
    public void insert (int index,
                        Segment srcSeg,
                        Segment trgSeg,
                        LocaleId trgLoc,
                        EnumSet<VariantOptions> variantOptions,
                        EnumSet<CopyOptions> copyOptions) {

        if (srcSeg == null)
            throw new IllegalArgumentException("srcSeg cannot be null");

        insertOrAppend(false, index, srcSeg, trgSeg, trgLoc, variantOptions, copyOptions);
    }

    private void insertOrAppend(boolean append,
                                int index,
                                Segment srcSeg,
                                Segment trgSeg,
                                LocaleId trgLoc,
                                EnumSet<VariantOptions> variantOptions,
                                EnumSet<CopyOptions> copyOptions) {
        
        //calling methods handle exceptions

        Segment sourceSeg = (srcSeg != null ? srcSeg : trgSeg.clone());
        Segment targetSeg = (trgSeg != null ? trgSeg : srcSeg.clone());

        Segment copySeg = sourceSeg.clone(); //copy of original source seg that will not be changed
        Segment tempSeg;

        String originalId = null;
        String insertedId = null;

        if (append)
            targetSeg.id = sourceSeg.id; //make sure ids match
        else
            originalId = getSource(trgLoc).getSegments().get(index).id; //get id at insertion location

        if (!continueWithOperation(trgLoc, variantOptions)) return;

        if (variantOptions.contains(MODIFY_SOURCE)) {
            if ( !copyOptions.contains(COPY_TO_SOURCE) )
                sourceSeg.getContent().clear();
            if (append)
                getSource(trgLoc).append(sourceSeg);
            else
                insertedId = doInsert(getSource(trgLoc), index, null, null, sourceSeg);
        }

        if (variantOptions.contains(MODIFY_TARGET)) {
            if ( !copyOptions.contains(COPY_TO_TARGET))
                targetSeg.getContent().clear();
            if (append)
                myParent.getTarget_DIFF(trgLoc).getSegments().append(targetSeg); //using actual target segment
            else
                insertedId = doInsert(myParent.getTarget_DIFF(trgLoc), index, originalId, insertedId, targetSeg);
        }

        tempSeg = copySeg.clone();
        if ( variantOptions.contains(MODIFY_TARGETS_WITH_SAME_SOURCE) ) {
            if ( !copyOptions.contains(COPY_TO_TARGETS_WITH_SAME_SOURCE))
                tempSeg.getContent().clear();
            for ( TextContainer container : getSameSourceTargets(trgLoc) )
                if (append)
                    container.getSegments().append(tempSeg.clone());
                else
                    insertedId = doInsert(container, index, originalId, insertedId, tempSeg.clone());
        }

        tempSeg = copySeg.clone();
        if ( variantOptions.contains(MODIFY_VARIANT_SOURCES)) {
            if ( !copyOptions.contains(COPY_TO_VARIANT_SOURCES))
                tempSeg.getContent().clear();
            for (TextContainer container : getOtherSources(trgLoc))
                if (append)
                    container.getSegments().append(tempSeg.clone());
                else
                    insertedId = doInsert(container, index, originalId, insertedId, tempSeg.clone());
        }

        tempSeg = copySeg.clone();
        if ( variantOptions.contains(MODIFY_TARGETS_OF_VARIANT_SOURCES)) {
            if ( !copyOptions.contains(COPY_TO_TARGETS_OF_VARIANT_SOURCES))
                tempSeg.getContent().clear();
            for (TextContainer container : getTargetsOfOtherSources(trgLoc))
                if (append)
                    container.getSegments().append(tempSeg.clone());
                else
                    insertedId = doInsert(container, index, originalId, insertedId, tempSeg.clone());
        }
    }

    /* indicates whether to continue based on variant options and source structure
     * also creates a variant source if required
     */
    private boolean continueWithOperation(LocaleId trgLoc,
                                          EnumSet<VariantOptions> variantOptions) {
        if (hasMultipleTargets(trgLoc)) {
            if (variantOptions.contains(CANCEL_IF_MULTIPLE_TARGETS))
                return false;
            if (variantOptions.contains(CREATE_VARIANT_IF_MULTIPLE_TARGETS))
                myParent.getVariantSources().create(trgLoc, false, COPY_ALL);
        }
        return true;
    }

    /*
     * @param originalId use null for source of target locale
     * @param insertedId use null if a new inserted id is needed
     * @returns null id or inserted id
     */
    private String doInsert(TextContainer container, int index, String originalId, String insertedId, Segment seg) {
        ISegments segs = container.getSegments();
        Segment currentSeg;

        //handle source for target locale
        if (originalId == null) {
            //must be the first source
            segs.insert(index, seg);
            return seg.id; //return validated Id
        }
        //handle insertion for any other container
        currentSeg = segs.get(originalId);
        if (currentSeg != null) {
            segs.insert(segs.getIndex(originalId), seg);
            if (insertedId != null)
                seg.id = insertedId;
            return seg.id;
        }
        //append if unable to insert
        segs.append(seg);
        return insertedId; //return the most up-to-date insertedId
    }

    @Override
    public void setSegment(int index,
                           Segment seg,
                           LocaleId trgLoc,
                           EnumSet<VariantOptions> variantOptions,
                           EnumSet<VariantOptions> idUpdateOptions) {
        
        if (!continueWithOperation(trgLoc, variantOptions)) return;
        
        ISegments segs = getSource(trgLoc).getSegments();
        String oldId = segs.get(index).id;
        String newId = seg.id;
        boolean idChanged = !newId.equals(oldId);
        int theIndex = index;

        Segment tempSeg;

        if (variantOptions.contains(MODIFY_SOURCE))
            segs.set(theIndex, seg.clone());
        
        if (variantOptions.contains(MODIFY_TARGET)) {
            segs = myParent.getTarget_DIFF(trgLoc).getSegments();
            tempSeg = seg.clone();
            segs.set(segs.getIndex(oldId), tempSeg);
        }
        
        if ( variantOptions.contains(MODIFY_TARGETS_WITH_SAME_SOURCE) )
            for ( TextContainer targ : getSameSourceTargets(trgLoc) ) {
                segs = targ.getSegments();
                tempSeg = seg.clone();
                segs.set(segs.getIndex(oldId), tempSeg);
            }

        if ( variantOptions.contains(MODIFY_VARIANT_SOURCES))
            for ( TextContainer targ : getOtherSources(trgLoc) ) {
                segs = targ.getSegments();
                tempSeg = seg.clone();
                segs.set(segs.getIndex(oldId), tempSeg);
            }

        if ( variantOptions.contains(MODIFY_TARGETS_OF_VARIANT_SOURCES))
            for ( TextContainer targ : getTargetsOfOtherSources(trgLoc) ) {
                segs = targ.getSegments();
                tempSeg = seg.clone();
                segs.set(segs.getIndex(oldId), tempSeg);
            }

        //update the ids
        if (idChanged) {
            if (idUpdateOptions.contains(MODIFY_SOURCE)) {
                tempSeg = getSource(trgLoc).getSegments().get(oldId);
                if (tempSeg != null) tempSeg.id = newId;
            }
            if (idUpdateOptions.contains(MODIFY_TARGET)) {
                tempSeg = myParent.getTarget_DIFF(trgLoc).getSegments().get(oldId);
                if (tempSeg != null) tempSeg.id = newId;
            }
            if (idUpdateOptions.contains(MODIFY_TARGETS_WITH_SAME_SOURCE))
                for ( TextContainer targ : getSameSourceTargets(trgLoc) ) {
                    tempSeg = targ.getSegments().get(oldId);
                    if (tempSeg != null) tempSeg.id = newId;
                }
            if (idUpdateOptions.contains(MODIFY_VARIANT_SOURCES))
                for ( TextContainer targ : getOtherSources(trgLoc) ) {
                    tempSeg = targ.getSegments().get(oldId);
                    if (tempSeg != null) tempSeg.id = newId;
                }
            if (idUpdateOptions.contains(MODIFY_TARGETS_OF_VARIANT_SOURCES))
                for ( TextContainer targ : getTargetsOfOtherSources(trgLoc) ) {
                    tempSeg = targ.getSegments().get(oldId);
                    if (tempSeg != null) tempSeg.id = newId;
                }
        }
    }

    @Override
    public boolean remove(Segment seg,
                          LocaleId trgLoc,
                          EnumSet<VariantOptions> variantOptions) {
        int count = 0;

        if (variantOptions.contains(MODIFY_SOURCE))
            count += removeSegment(getSource(trgLoc), seg.id);
        if (variantOptions.contains(MODIFY_TARGET))
            count += removeSegment(myParent.getTarget_DIFF(trgLoc), seg.id);
        if ( variantOptions.contains(MODIFY_TARGETS_WITH_SAME_SOURCE) )
            for ( TextContainer targ : getSameSourceTargets(trgLoc) )
                count += removeSegment(targ, seg.id);
        if ( variantOptions.contains(MODIFY_VARIANT_SOURCES))
            for ( TextContainer targ : getOtherSources(trgLoc) )
                count += removeSegment(targ, seg.id);
        if ( variantOptions.contains(MODIFY_TARGETS_OF_VARIANT_SOURCES))
            for ( TextContainer targ : getTargetsOfOtherSources(trgLoc) )
                count += removeSegment(targ, seg.id);
        return (count > 0);
    }

    /* Removes a segment witht he given id from the given container. Returns
     * 1 if a segment was removed, 0 otherwise.
     */
    private int removeSegment(TextContainer container, String segId) {
        ISegments segs = container.getSegments();
        int segIndex = segs.getIndex(segId);
        if (segIndex > -1) {
            container.remove(segs.getPartIndex(segIndex));
            return 1;
        }
        return 0;
    }

    @Override
    public Segment getSource(int index, LocaleId trgLoc) {
        return getSource(trgLoc).getSegments().get(index);
    }

    @Override
    public Segment getCorrespondingTarget(Segment srcSeg,
                                          LocaleId trgLoc,
                                          EnumSet<VariantOptions> variantOptions,
                                          EnumSet<CopyOptions> copyOptions) {
        //avoid modifying the EnumSet that is passed in.
        variantOptions = variantOptions.clone();

        // Get the target segments (creates them if needed)
        ISegments trgSegs = myParent.getTarget_DIFF(trgLoc).getSegments();
        Segment trgSeg = trgSegs.get(srcSeg.id);
        if (trgSeg == null) { // If no corresponding segment found: create one
            variantOptions.remove(MODIFY_SOURCE); //prevent source being overwritten
            variantOptions.add(MODIFY_TARGET); //cause target to be added
            append(srcSeg, null, trgLoc, variantOptions, copyOptions);
        }
        return trgSegs.get(srcSeg.id);
    }

    @Override
    public Segment getCorrespondingSource(Segment trgSeg,
                                          LocaleId trgLoc,
                                          EnumSet<VariantOptions> variantOptions,
                                          EnumSet<CopyOptions> copyOptions) {
        //avoid modifying the EnumSet that is passed in.
        variantOptions = variantOptions.clone();

        ISegments srcSegs = getSource(trgLoc).getSegments();
        Segment srcSeg = srcSegs.get(trgSeg.id);
        if ( srcSeg == null ) { // If no corresponding segment found: create one
            variantOptions.add(MODIFY_SOURCE); //cause source segment to be added
            variantOptions.remove(MODIFY_TARGET); //prevent target being overwritten
            append(null, trgSeg, trgLoc, variantOptions, copyOptions);
        }
        return srcSegs.get(trgSeg.id);
    }


    @Override
    public void align (List<AlignedPair> alignedSegmentPairs,
                       LocaleId trgLoc) {
        //TODO implement. Approach: set target seg ids to match source id in aligned pair
        //TODO check that alignedpair doesn't already do this
        //iterate over the list
            //get target seg by id
            //set target seg id to id in alignedpair source

        // these target segments are now aligned with their source counterparts
        myParent.getTarget_DIFF(trgLoc).getSegments().setAlignmentStatus(AlignmentStatus.ALIGNED);
    }

    
    /**
     * Force one to one alignment. Assume that both source and target
     * have the same number of segments.
     *
     * @param trgLoc target locale used to align with the source
     */
    @Override
    public void align(LocaleId trgLoc) {
        Iterator<Segment> srcSegsIt = getSource(trgLoc).getSegments().iterator();
        Iterator<Segment> trgSegsIt = myParent.getTarget_DIFF(trgLoc).getSegments().iterator();
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
        myParent.getTarget_DIFF(trgLoc).getSegments().setAlignmentStatus(AlignmentStatus.ALIGNED);
    }

    
    /**
     * Collapse all segments for the source and target
     */
    @Override
    public void alignCollapseAll(LocaleId trgLoc,
                                 EnumSet<VariantOptions> variantOptions) {
        //TODO actually collapse first

        // these target segments are now aligned with their source counterparts
        myParent.getTarget_DIFF(trgLoc).getSegments().setAlignmentStatus(AlignmentStatus.ALIGNED);
    }



    @Override
    public Segment splitSource(LocaleId trgLoc,
                               Segment srcSeg,
                               int splitPos,
                               EnumSet<VariantOptions> variantOptions,
                               EnumSet<CopyOptions> copyOptions) {

        if (!continueWithOperation(trgLoc, variantOptions)) return null;

        TextContainer theSource = getSource(trgLoc);
        ISegments srcSegs = theSource.getSegments();
        int segIndex = srcSegs.getIndex(srcSeg.id);
        if (segIndex == -1) return null; //segment id not found in the container
        int partIndex = srcSegs.getPartIndex(segIndex);

        //split the source
        theSource.split(partIndex, splitPos, splitPos, false);

        Segment newSeg = srcSegs.get(segIndex+1);
        
        Segment tempSeg;
        ISegments currentSegs;
        Segment currentSeg;

        //inserting new segments in all the places they go
        if (variantOptions.contains(MODIFY_TARGET)) {
            tempSeg = newSeg.clone();
            if ( !copyOptions.contains(COPY_TO_TARGET))
                tempSeg.getContent().clear();

            currentSegs = myParent.getTarget_DIFF(trgLoc).getSegments();
            currentSeg = currentSegs.get(srcSeg.id);
            if (currentSeg != null) {
                currentSegs.insert(currentSegs.getIndex(srcSeg.id)+1, tempSeg);
            }
        }
        propagateChangesForSplit(newSeg, srcSeg.id, trgLoc, variantOptions, copyOptions);

        return newSeg;
    }


    private void propagateChangesForSplit(Segment newSeg,
                                          String segId,
                                          LocaleId trgLoc,
                                          EnumSet<VariantOptions> variantOptions,
                                          EnumSet<CopyOptions> copyOptions) {
        Segment tempSeg;
        Segment currentSeg;
        ISegments currentSegs;
        
        tempSeg = newSeg.clone();
        if ( variantOptions.contains(MODIFY_TARGETS_WITH_SAME_SOURCE) ) {
            if ( !copyOptions.contains(COPY_TO_TARGETS_WITH_SAME_SOURCE))
                tempSeg.getContent().clear();
            for ( TextContainer targ : getSameSourceTargets(trgLoc) ) {
                currentSegs = targ.getSegments();
                currentSeg = currentSegs.get(segId);
                if (currentSeg != null)
                    currentSegs.insert(currentSegs.getIndex(segId)+1, tempSeg.clone());
            }
        }

        tempSeg = newSeg.clone();
        if ( variantOptions.contains(MODIFY_VARIANT_SOURCES)) {
            if ( !copyOptions.contains(COPY_TO_VARIANT_SOURCES))
                tempSeg.getContent().clear();
            for (TextContainer variant : getOtherSources(trgLoc)) {
                currentSegs = variant.getSegments();
                currentSeg = currentSegs.get(segId);
                if (currentSeg != null)
                    currentSegs.insert(currentSegs.getIndex(segId)+1, tempSeg.clone());
            }
        }

        tempSeg = newSeg.clone();
        if ( variantOptions.contains(MODIFY_TARGETS_OF_VARIANT_SOURCES)) {
            if ( !copyOptions.contains(COPY_TO_TARGETS_OF_VARIANT_SOURCES))
                tempSeg.getContent().clear();
            for (TextContainer variantTarg : getTargetsOfOtherSources(trgLoc)) {
                currentSegs = variantTarg.getSegments();
                currentSeg = currentSegs.get(segId);
                if (currentSeg != null)
                    currentSegs.insert(currentSegs.getIndex(segId)+1, tempSeg.clone());
            }
        }
    }



    @Override
    public Segment splitTarget (LocaleId trgLoc,
                                Segment trgSeg,
                                int splitPos,
                                EnumSet<VariantOptions> variantOptions,
                                EnumSet<CopyOptions> copyOptions) {

        if (!continueWithOperation(trgLoc, variantOptions)) return null;

        TextContainer theTarget = myParent.getTarget_DIFF(trgLoc);
        ISegments trgSegs = theTarget.getSegments();
        int segIndex = trgSegs.getIndex(trgSeg.id);
        if (segIndex == -1) return null; //segment id not found in the container
        int partIndex = trgSegs.getPartIndex(segIndex);

        //split the source
        theTarget.split(partIndex, splitPos, splitPos, false);

        Segment newSeg = trgSegs.get(segIndex+1);

        Segment tempSeg;
        ISegments currentSegs;
        Segment currentSeg;

        //inserting new segments in all the places they go
        if (variantOptions.contains(MODIFY_TARGET)) {
            tempSeg = newSeg.clone();
            if ( !copyOptions.contains(COPY_TO_TARGET))
                tempSeg.getContent().clear();

            currentSegs = getSource(trgLoc).getSegments();
            currentSeg = currentSegs.get(trgSeg.id);
            if (currentSeg != null) {
                currentSegs.insert(currentSegs.getIndex(trgSeg.id)+1, tempSeg);
            }
        }
        propagateChangesForSplit(newSeg, trgSeg.id, trgLoc, variantOptions, copyOptions);

        return newSeg;
    }

    @Override
    public void joinWithNext (Segment seg,
                              LocaleId trgLoc,
                              EnumSet<VariantOptions> variantOptions) {

        if (!continueWithOperation(trgLoc, variantOptions)) return;

        if (variantOptions.contains(MODIFY_SOURCE))
            doJoinWithNext(getSource(trgLoc), seg.id);

        if (variantOptions.contains(MODIFY_TARGET))
            doJoinWithNext(myParent.getTarget_DIFF(trgLoc), seg.id);

        if ( variantOptions.contains(MODIFY_TARGETS_WITH_SAME_SOURCE) )
            for ( TextContainer targ : getSameSourceTargets(trgLoc) )
                doJoinWithNext(targ, seg.id);


        if ( variantOptions.contains(MODIFY_VARIANT_SOURCES))
            for (TextContainer variant : getOtherSources(trgLoc))
                doJoinWithNext(variant, seg.id);

        if ( variantOptions.contains(MODIFY_TARGETS_OF_VARIANT_SOURCES))
            for (TextContainer variantTarg : getTargetsOfOtherSources(trgLoc))
                doJoinWithNext(variantTarg, seg.id);

    }

    private void doJoinWithNext(TextContainer targ, String segId) {
        ISegments segs;
        int segIndex;
        segs = targ.getSegments();
        segIndex = segs.getIndex(segId);
        if (segIndex != -1)
            segs.joinWithNext(segIndex);
    }

    @Override
    public void joinAll(LocaleId trgLoc,
                        EnumSet<VariantOptions> variantOptions) {

        if (!continueWithOperation(trgLoc, variantOptions)) return;

        if (variantOptions.contains(MODIFY_SOURCE))
            getSource(trgLoc).joinAll();

        if (variantOptions.contains(MODIFY_TARGET))
            myParent.getTarget_DIFF(trgLoc).joinAll();

        if ( variantOptions.contains(MODIFY_TARGETS_WITH_SAME_SOURCE) )
            for ( TextContainer targ : getSameSourceTargets(trgLoc) )
                targ.joinAll();


        if ( variantOptions.contains(MODIFY_VARIANT_SOURCES))
            for (TextContainer variant : getOtherSources(trgLoc))
                variant.joinAll();

        if ( variantOptions.contains(MODIFY_TARGETS_OF_VARIANT_SOURCES))
            for (TextContainer variantTarg : getTargetsOfOtherSources(trgLoc))
                variantTarg.joinAll();

    }

    @Override
    public AlignmentStatus getAlignmentStatus () {
        for ( LocaleId loc : myParent.getTargetLocales() ) {
            ISegments trgSegs = myParent.getTarget_DIFF(loc).getSegments();
            if (trgSegs.getAlignmentStatus() == AlignmentStatus.NOT_ALIGNED) {
                return AlignmentStatus.NOT_ALIGNED;
            }
        }
        return AlignmentStatus.ALIGNED;
    }

    @Override
    public AlignmentStatus getAlignmentStatus(LocaleId trgLoc) {
        return myParent.getTarget_DIFF(trgLoc).getSegments().getAlignmentStatus();
    }



    @Override
    public void segmentSource (ISegmenter segmenter, LocaleId targetLocale) {
        TextContainer theSource = getSource(targetLocale);
        segmenter.computeSegments(theSource);
        theSource.getSegments().create(segmenter.getRanges());
    }

    @Override
    public void segmentTarget (ISegmenter segmenter,
                               LocaleId targetLocale) {
        TextContainer theTarget = myParent.getTarget_DIFF(targetLocale);
        segmenter.computeSegments(theTarget);
        theTarget.getSegments().create(segmenter.getRanges());
//TODO: invalidate source and other targets? or this one.
// but then there is no way to call segmentTarget and get all in synch
    }


    @Override
    public Iterator<Segment> iterator () {
        return myParent.getSource().getSegments().iterator();
    }

    @Override
    public Iterator<Segment> iterator(LocaleId trgLoc) {
        return getSource(trgLoc).getSegments().iterator();
    }


    private boolean hasMultipleTargets(LocaleId targetLocale) {
        //check if default source
        if (hasVariant(targetLocale)) { return false; } //variants have only one target
        
        //else locale must use default source
        //check if more than one target uses default source
        int targetsOfDefault = 0;
        for (LocaleId loc : myParent.getTargetLocales()) {
            if (!myParent.getVariantSources().getLocales().contains(loc))
                targetsOfDefault++;
            if (targetsOfDefault > 1) return true;
        }
        return false;
    }

    private Set<TextContainer> getSameSourceTargets(LocaleId targetLocale) {
        Set<LocaleId> locales = new HashSet<LocaleId>();
        if (hasMultipleTargets(targetLocale)) {
            locales.addAll(myParent.getTargetLocales());
            locales.remove(targetLocale);
            if (myParent.hasVariantSources())
                locales.removeAll(myParent.getVariantSources().getLocales());
        }
        Set<TextContainer> targets = new HashSet<TextContainer>();
        for (LocaleId loc : locales)
            targets.add(myParent.getTarget_DIFF(loc));
        return targets;
    }

    private Set<TextContainer> getOtherSources(LocaleId loc) {
        Set<TextContainer> sources = new HashSet<TextContainer>();
        if ( !myParent.hasVariantSources())
            return sources;
        for (LocaleId varLoc : myParent.getVariantSources().getLocales() )
            sources.add(myParent.getVariantSources().get(varLoc));
        sources.add(myParent.getSource());
        sources.remove(getSource(loc));
        return sources;
    }

    private Set<TextContainer> getTargetsOfOtherSources(LocaleId loc) {
        Set<LocaleId> locales = new HashSet<LocaleId>();
        if ( hasVariant(loc) )
            locales = myParent.getTargetLocales();
        else if (myParent.hasVariantSources())
            locales = myParent.getVariantSources().getLocales();

        locales.remove(loc);
        Set<TextContainer> targets = new HashSet<TextContainer>();
        for (LocaleId targLoc : locales)
            targets.add(myParent.getTarget_DIFF(targLoc));
        return targets;
    }
    
    private boolean hasVariant(LocaleId loc) {
        if (myParent.hasVariantSources())
            return myParent.getVariantSources().hasVariant(loc);
        return false;
    }

    private TextContainer getSource(LocaleId loc) {
        if (hasVariant(loc))
            return myParent.getVariantSources().get(loc);
        return myParent.getSource();
    }
};