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
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.Stack;
import static net.sf.okapi.common.IResource.*;
import static net.sf.okapi.common.resource.IAlignedSegments.VariantOptions.*;
import static net.sf.okapi.common.resource.IAlignedSegments.CopyOptions.*;
import net.sf.okapi.common.IResource;
import net.sf.okapi.common.ISegmenter;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.ReversedIterator;
import net.sf.okapi.common.exceptions.OkapiMisAlignmentException;

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
        if (!continueWithOperation(trgLoc, variantOptions)) return;


        Segment sourceSeg = (srcSeg != null ? srcSeg : trgSeg.clone());
        Segment targetSeg = (trgSeg != null ? trgSeg : srcSeg.clone());

        Segment copySeg = sourceSeg.clone(); //copy of original source seg that will not be changed

        String originalId = null;
        String insertedId = null;

        ContainerIterator ci = new ContainerIterator(trgLoc, variantOptions, copyOptions);

        if (append) {
            targetSeg.id = sourceSeg.id; //make sure ids match

            if (ci.hasSource())
                ci.getSource().getSegments().append(ci.sourceSeg(sourceSeg));

            if (ci.hasTarget())
                ci.getTarget().getSegments().append(ci.targetSeg(targetSeg));

            while (ci.hasNextOtherLocale())
                ci.getNextOtherLocale().getSegments().append(ci.otherLocaleSeg(copySeg.clone()));

        }  else { //insert
            originalId = getSource(trgLoc).getSegments().get(index).id; //get id at insertion location

            if (ci.hasSource())
                insertedId = doInsert(ci.getSource(), index, null, null, ci.sourceSeg(sourceSeg));

            if (ci.hasTarget())
                insertedId = doInsert(ci.getTarget(), index, originalId, insertedId, ci.targetSeg(targetSeg));

            while (ci.hasNextOtherLocale())
                insertedId = doInsert(ci.getNextOtherLocale(), index, originalId, insertedId, ci.otherLocaleSeg(copySeg.clone()));
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

        ContainerIterator ci = new ContainerIterator(trgLoc, variantOptions, null);

        if (ci.hasSource())
            ci.getSource().getSegments().set(theIndex, seg.clone());
        if (ci.hasTarget()) {
            segs = ci.getTarget().getSegments();
            segs.set(segs.getIndex(oldId), seg.clone());
        }
        while (ci.hasNextOtherLocale()) {
            segs = ci.getNextOtherLocale().getSegments();
            segs.set(segs.getIndex(oldId), seg.clone());
        }

        //update the ids
        if (idChanged) {
            ci = new ContainerIterator(trgLoc, idUpdateOptions, null);

            if (ci.hasSource()) {
                tempSeg = ci.getSource().getSegments().get(oldId);
                if (tempSeg != null) tempSeg.id = newId;
            }
            if (ci.hasTarget()) {
                tempSeg = ci.getTarget().getSegments().get(oldId);
                if (tempSeg != null) tempSeg.id = newId;
            }
            while (ci.hasNextOtherLocale()) {
                tempSeg = ci.getNextOtherLocale().getSegments().get(oldId);
                if (tempSeg != null) tempSeg.id = newId;
            }
        }
    }

    @Override
    public boolean remove(Segment seg,
                          LocaleId trgLoc,
                          EnumSet<VariantOptions> variantOptions) {
        int count = 0;
        ContainerIterator ci = new ContainerIterator(trgLoc, variantOptions, null);

        if (ci.hasSource())
            count += removeSegment(ci.getSource(), seg.id);
        if (ci.hasTarget())
            count += removeSegment(ci.getTarget(), seg.id);
        while (ci.hasNextOtherLocale())
            count += removeSegment(ci.getNextOtherLocale(), seg.id);
        
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
        ISegments trgSegs = myParent.getTargetSegments(trgLoc);
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
	public Segment getCorrespondingTarget(Segment seg, LocaleId trgLoc) {
		return getCorrespondingSource(seg, trgLoc, IAlignedSegments.MODIFY_SOURCE_AND_ASSOCIATED_TARGET, 
					IAlignedSegments.COPY_TO_NONE);
	}

	@Override
	public Segment getCorrespondingSource(Segment trgSeg, LocaleId trgLoc) {
		return getCorrespondingSource(trgSeg, trgLoc, IAlignedSegments.MODIFY_SOURCE_AND_ASSOCIATED_TARGET, 
				IAlignedSegments.COPY_TO_NONE);
	}

    @Override
    public void align(List<AlignedPair> alignedSegmentPairs,
                      LocaleId trgLoc) {
        //Based on TextUnitUtil.createMultilingualTextUnit(...)

        //Note: this implementation will wipe out any content that exists for this locale
        //TODO check that this is the desired behaviour for this method.

        TextContainer src;
        TextContainer trg;
        String srcSegId;

        //add source and target if required
        if (!hasVariant(trgLoc)) {
            myParent.getVariantSources().create(trgLoc, true, COPY_ALL);
        }
        myParent.createTarget(trgLoc, false, COPY_ALL); //no check required, see method description

        src = getSource(trgLoc);
        trg = myParent.getTarget(trgLoc);

        //clear content ready for new segments
        src.clear();
        trg.clear();
        
        //iterate through the aligned pairs, adding content to both containers
        for (AlignedPair alignedPair : alignedSegmentPairs) {
            //use the id from the source as the id for the target
            srcSegId = appendPartsToContainer(alignedPair.getSourceParts(), src, null);
            appendPartsToContainer(alignedPair.getTargetParts(), trg, srcSegId);
        }
        
        // We now consider the source and target content to be segmented
        // if nothing else we need to prevent re-segmentation as that
        // will break the alignments
        src.setHasBeenSegmentedFlag(true);
        trg.setHasBeenSegmentedFlag(true);

        //the target and source should now be aligned since their content is all
        //from aligned pairs.
        trg.getSegments().setAlignmentStatus(AlignmentStatus.ALIGNED);
    }

    /**
     * Appends the given {@link TextPart}s to the given {@link TextContainer}.
     *
     * @param parts
     * @param container
     * @param segId the id to use for the segment component of parts
     * @return the identifier for the segment in parts
     */
    private String appendPartsToContainer(List<TextPart> parts, TextContainer container, String segId) {
        // make a shallow copy because we may modify the list elements
        List<TextPart> partsCopy = new LinkedList<TextPart>(parts);

        // calculate indexes of the source before and after inter-segment TextParts
        int beforeIndex = 0;
        int afterIndex = partsCopy.size();
        for (TextPart part : partsCopy) {
            if (part.isSegment()) {
                break;
            }
            beforeIndex++;
        }
        ReversedIterator<TextPart> ri = new ReversedIterator<TextPart>(partsCopy);
        for (TextPart part : ri) {
            if (part.isSegment()) {
                break;
            }
            afterIndex--;
        }

        // append the before inter-segment TextParts
        for (TextPart part : partsCopy.subList(0, beforeIndex)) {
            container.append(part);
        }

        // append segment parts
        TextFragment frag = new TextFragment();
        for (TextPart part : partsCopy.subList(beforeIndex, afterIndex)) {
            frag.append(part.getContent());
        }
        Segment seg = new Segment(segId, frag);
        container.getSegments().append(seg);

        // append the after inter-segment TextParts
        for (TextPart part : partsCopy.subList(afterIndex, partsCopy.size())) {
            container.append(part);
        }

        return seg.getId();
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
        Iterator<Segment> trgSegsIt = myParent.createTarget(trgLoc, false, IResource.COPY_SEGMENTATION).getSegments().iterator();
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
        myParent.getTargetSegments(trgLoc).setAlignmentStatus(AlignmentStatus.ALIGNED);
    }


    @Override
    public void alignCollapseAll(LocaleId trgLoc,
                                 EnumSet<VariantOptions> variantOptions) {

        if (continueWithOperation(trgLoc, variantOptions));

        ContainerIterator ci = new ContainerIterator(trgLoc, variantOptions, COPY_TO_NONE);

        //keeping track of collapsed containers to check which to set to ALIGNED
        LinkedList<TextContainer> collapsed = new LinkedList<TextContainer>();


        //TODO decide if source/target should always collapse regardless of variantOptions
        // (currently they depend on variantOptions)
        if (ci.hasSource()) {
            ci.getSource().joinAll();
            ci.getSource().setHasBeenSegmentedFlag(false);
            collapsed.add(ci.getSource());
        }
        if (ci.hasTarget()) {
            ci.getTarget().joinAll();
            ci.getTarget().setHasBeenSegmentedFlag(false);
            collapsed.add(ci.getTarget());
        }

        TextContainer container;
        while (ci.hasNextOtherLocale()) {
            container = ci.getNextOtherLocale();
            container.joinAll();
            container.setHasBeenSegmentedFlag(false);
            collapsed.add(container);
        }
        
        //mark target/source pairs aligned if both have been collapsed
        TextContainer src, trg;
        for (LocaleId loc : myParent.getTargetLocales()) {
            src = getSource(loc);
            if (collapsed.contains(src)) {
                trg = myParent.getTarget(loc);
                if (collapsed.contains(trg)) {
                    trg.getSegments().setAlignmentStatus(AlignmentStatus.ALIGNED);
                    trg.getFirstSegment().id = src.getFirstSegment().getId(); //TODO check that this is the desired behaviour
                }
            }
        }
        
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
        ISegments currentSegs;
        Segment currentSeg;

        ContainerIterator ci = new ContainerIterator(trgLoc, variantOptions, copyOptions);

        //inserting new segments in all the places they go
        if (ci.hasTarget()) {
            currentSegs = ci.getTarget().getSegments();
            currentSeg = currentSegs.get(srcSeg.id);
            if (currentSeg != null)
                currentSegs.insert(currentSegs.getIndex(srcSeg.id)+1, ci.targetSeg(newSeg.clone()));
        }
        propagateChangesForSplit(newSeg, srcSeg.id, ci);

        return newSeg;
    }

    @Override
    public Segment splitTarget (LocaleId trgLoc,
                                Segment trgSeg,
                                int splitPos,
                                EnumSet<VariantOptions> variantOptions,
                                EnumSet<CopyOptions> copyOptions) {

        if (!continueWithOperation(trgLoc, variantOptions)) return null;

        TextContainer theTarget = myParent.createTarget(trgLoc, false, IResource.COPY_SEGMENTATION);
        ISegments trgSegs = theTarget.getSegments();
        int segIndex = trgSegs.getIndex(trgSeg.id);
        if (segIndex == -1) return null; //segment id not found in the container
        int partIndex = trgSegs.getPartIndex(segIndex);

        //split the source
        theTarget.split(partIndex, splitPos, splitPos, false);

        Segment newSeg = trgSegs.get(segIndex+1);

        ISegments currentSegs;
        Segment currentSeg;

        ContainerIterator ci = new ContainerIterator(trgLoc, variantOptions, copyOptions);

        //inserting new segments in all the places they go
        if (ci.hasSource()) {
            currentSegs = ci.getSource().getSegments();
            currentSeg = currentSegs.get(trgSeg.id);
            if (currentSeg != null)
                currentSegs.insert(currentSegs.getIndex(trgSeg.id)+1, ci.sourceSeg(newSeg.clone()));
        }

        propagateChangesForSplit(newSeg, trgSeg.id, ci);

        return newSeg;
    }
    
    private void propagateChangesForSplit(Segment newSeg,
                                          String segId,
                                          ContainerIterator ci) {
        Segment currentSeg;
        ISegments currentSegs;

        while (ci.hasNextOtherLocale()) {
            currentSegs = ci.getNextOtherLocale().getSegments();
                currentSeg = currentSegs.get(segId);
                if (currentSeg != null)
                    currentSegs.insert(currentSegs.getIndex(segId)+1, ci.otherLocaleSeg(newSeg.clone()));
        }
    }

    @Override
    public void joinWithNext (Segment seg,
                              LocaleId trgLoc,
                              EnumSet<VariantOptions> variantOptions) {

        if (!continueWithOperation(trgLoc, variantOptions)) return;

        ContainerIterator ci = new ContainerIterator(trgLoc, variantOptions, null);

        if (ci.hasSource())
            doJoinWithNext(ci.getSource(), seg.id);
        if (ci.hasTarget())
            doJoinWithNext(ci.getTarget(), seg.id);
        while (ci.hasNextOtherLocale())
            doJoinWithNext(ci.getNextOtherLocale(), seg.id);
    }

    private void doJoinWithNext(TextContainer cont, String segId) {
        int segIndex;
        ISegments segs = cont.getSegments();
        segIndex = segs.getIndex(segId);
        if (segIndex != -1)
            segs.joinWithNext(segIndex);
    }

    @Override
    public void joinAll(LocaleId trgLoc,
                        EnumSet<VariantOptions> variantOptions) {

        if (!continueWithOperation(trgLoc, variantOptions)) return;

        ContainerIterator ci = new ContainerIterator(trgLoc, variantOptions, null);
        if (ci.hasSource())
            ci.getSource().joinAll();
        if (ci.hasTarget())
            ci.getTarget().joinAll();
        while (ci.hasNextOtherLocale())
            ci.getNextOtherLocale().joinAll();
    }

    @Override
    public AlignmentStatus getAlignmentStatus () {
        for ( LocaleId loc : myParent.getTargetLocales() ) {
            ISegments trgSegs = myParent.getTargetSegments(loc);
            if (trgSegs.getAlignmentStatus() == AlignmentStatus.NOT_ALIGNED) {
                return AlignmentStatus.NOT_ALIGNED;
            }
        }
        return AlignmentStatus.ALIGNED;
    }

    @Override
    public AlignmentStatus getAlignmentStatus(LocaleId trgLoc) {
        return myParent.getTargetSegments(trgLoc).getAlignmentStatus();
    }

    @Override
    public void segmentSource(ISegmenter segmenter, LocaleId targetLocale) {
        TextContainer theSource = getSource(targetLocale);
        segmenter.computeSegments(theSource);
        theSource.getSegments().create(segmenter.getRanges());
    }

    @Override
    public void segmentTarget(ISegmenter segmenter, LocaleId targetLocale) {
        TextContainer theTarget = myParent.createTarget(targetLocale, false, IResource.COPY_SEGMENTATION);
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

    /**
     * Indicates whether the source for the given locale is used by multiple targets.
     *
     * @param targetLocale the locale to check
     * @return true if the source for the given locale is used by multiple targets
     */
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

    /**
     * Indicates whether the parent has a variant source for the given locale
     *
     * @param loc locale to check for variant source
     * @return true if the parent has a variant source for the locale, otherwise false
     */
    private boolean hasVariant(LocaleId loc) {
        if (myParent.hasVariantSources())
            return myParent.getVariantSources().hasVariant(loc);
        return false;
    }
    
    /**
     * Returns the source {@link TextContainer} for the given locale (may be the
     * default source).
     * 
     * @param loc
     * @return
     */
    private TextContainer getSource(LocaleId loc) {
        if (hasVariant(loc))
            return myParent.getVariantSources().get(loc);
        return myParent.getSource();
    }

    /**
     * Used to easily access the 5 categories of TextContainer objects:
     * source for the given target locale, target for the given target locale,
     * other targets that use the source, other sources, and targets of other
     * sources
     */
    private class ContainerIterator {

        private TextContainer theSource = null;
        private TextContainer theTarget = null;

        private Stack<TextContainer> sameSourceTargets;
        private Stack<TextContainer> otherSources;
        private Stack<TextContainer> otherTargets;

        //type of the most recent 'other container' that was retrieved
        private int otherContainerType = -1;

        private final int sameSourceTarget = 0;
        private final int otherSource = 1;
        private final int otherTarget = 2;

        private EnumSet<CopyOptions> myCopyOptions;

        /**
         *
         * @param targetLocale used to determine which TextContainers are in which
         *                     categories
         * @param variantOptions
         * @param copyOptions
         */
        public ContainerIterator(LocaleId trgLoc,
                                EnumSet<VariantOptions> variantOptions,
                                EnumSet<CopyOptions> copyOptions) {

            myCopyOptions = copyOptions;

            //put everything into its category
            if (variantOptions.contains(MODIFY_SOURCE)) {
                theSource = AlignedSegments.this.getSource(trgLoc);
            }
            if (variantOptions.contains(MODIFY_TARGET)) {
                theTarget = myParent.getTarget(trgLoc);
            }
            if ( variantOptions.contains(MODIFY_TARGETS_WITH_SAME_SOURCE) ) {
                sameSourceTargets = getSameSourceTargets(trgLoc);
            } else {
                 sameSourceTargets = new Stack<TextContainer>();
            }
            if ( variantOptions.contains(MODIFY_VARIANT_SOURCES)) {
                otherSources = getOtherSources(trgLoc);
            } else {
                otherSources = new Stack<TextContainer>();
            }
            if ( variantOptions.contains(MODIFY_TARGETS_OF_VARIANT_SOURCES)) {
                otherTargets = getOtherSourceTargets(trgLoc);
            } else {
                otherTargets = new Stack<TextContainer>();
            }
        }

        public boolean hasSource() { return theSource != null; }

        public TextContainer getSource() {
            if (theSource == null)
                throw new IllegalStateException("this method can only be called after hasSource() returns true");
            return theSource;
        }

        public boolean hasTarget() { return theTarget != null; }

        public TextContainer getTarget() {
            if (theTarget == null)
                throw new IllegalStateException("this method can only be called after hasTarget() returns true");
            return theTarget;
        }

        public boolean hasSameSourceTarget() { return !sameSourceTargets.empty(); }

        public TextContainer getSameSourceTarget() { return sameSourceTargets.pop(); }

        public boolean hasOtherSource() { return !otherSources.empty(); }

        public TextContainer getOtherSource() { return otherSources.pop(); }

        public boolean hasOtherTarget() { return !otherTargets.empty(); }

        public TextContainer getOtherTarget() { return otherTargets.pop(); }

        //for iterating over everything but the source and target for the given locale
        public boolean hasNextOtherLocale() {

            if (hasSameSourceTarget()) {
                otherContainerType = sameSourceTarget;
                return true;
            }
            if (hasOtherSource()) {
                otherContainerType = otherSource;
                return true;
            }
            if (hasOtherTarget()) {
                otherContainerType = otherTarget;
                return true;
            }
            return false;
        }

        public TextContainer getNextOtherLocale() {
            if (hasSameSourceTarget())
                return getSameSourceTarget();
            if (hasOtherSource())
                return getOtherSource();
            //no check as we want an EmptyStackException from getOtherTarget
            //if the call is trying to get next when there is no next
            return getOtherTarget();
        }

        private Stack<TextContainer> getOtherSourceTargets(LocaleId loc) {
            Set<LocaleId> locales = new HashSet<LocaleId>();
            if ( hasVariant(loc) )
                locales = myParent.getTargetLocales();
            else if (myParent.hasVariantSources())
                locales = myParent.getVariantSources().getLocales();

            locales.remove(loc);
            Stack<TextContainer> targets = new Stack<TextContainer>();
            for (LocaleId targLoc : locales)
                targets.push(myParent.getTarget(targLoc));
            return targets;
        }

        private Stack<TextContainer> getSameSourceTargets(LocaleId targetLocale) {
            Set<LocaleId> locales = new HashSet<LocaleId>();
            if (hasMultipleTargets(targetLocale)) {
                locales.addAll(myParent.getTargetLocales());
                locales.remove(targetLocale);
                if (myParent.hasVariantSources())
                    locales.removeAll(myParent.getVariantSources().getLocales());
            }
            Stack<TextContainer> targets = new Stack<TextContainer>();
            for (LocaleId loc : locales)
                targets.push(myParent.getTarget(loc));
            return targets;
        }

        private Stack<TextContainer> getOtherSources(LocaleId loc) {
            Stack<TextContainer> sources = new Stack<TextContainer>();
            if ( !myParent.hasVariantSources())
                return sources;
            for (LocaleId varLoc : myParent.getVariantSources().getLocales() )
                sources.add(myParent.getVariantSources().get(varLoc));
            sources.add(myParent.getSource());
            sources.remove(AlignedSegments.this.getSource(loc));
            return sources;
        }


        //these return the given Segment, with content cleared if required by copy options
        
        public Segment sourceSeg(Segment seg) {
            if ( !myCopyOptions.contains(COPY_TO_SOURCE) )
                seg.getContent().clear();
            return seg;
        }

        public Segment targetSeg(Segment seg) {
            if ( !myCopyOptions.contains(COPY_TO_TARGET))
                seg.getContent().clear();
            return seg;
        }

        public Segment sameSourceTargetSeg(Segment seg) {
            if ( !myCopyOptions.contains(COPY_TO_TARGETS_WITH_SAME_SOURCE))
                seg.getContent().clear();
            return seg;
        }

        public Segment otherSourceSeg(Segment seg) {
            if ( !myCopyOptions.contains(COPY_TO_VARIANT_SOURCES))
                seg.getContent().clear();
            return seg;
        }

        public Segment otherTargetSeg(Segment seg) {
            if ( !myCopyOptions.contains(COPY_TO_TARGETS_OF_VARIANT_SOURCES))
                seg.getContent().clear();
            return seg;
        }

        public Segment otherLocaleSeg(Segment seg) {
            if (otherContainerType == sameSourceTarget)
                return sameSourceTargetSeg(seg);
            else if (otherContainerType == otherSource)
                return otherSourceSeg(seg);
            else if (otherContainerType == otherTarget)
                return otherTargetSeg(seg);

            //hasn't been set yet
            throw new IllegalStateException("this method can only be called after hasNextOtherLocale()");
        }
    }
};