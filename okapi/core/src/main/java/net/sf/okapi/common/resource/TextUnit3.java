/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.sf.okapi.common.resource;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import net.sf.okapi.common.IResource;
import net.sf.okapi.common.ISegmenter;
import net.sf.okapi.common.ISkeleton;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.annotation.Annotations;
import net.sf.okapi.common.annotation.IAnnotation;
import net.sf.okapi.common.exceptions.OkapiMisAlignmentException;

/**
 * EXPERIMENTAL class. Do not use yet.
 * Basic unit of extraction from a filter and also the resource associated with\
 * the filter event TEXT_UNIT.
 * The TextUnit object holds the extracted source text, all its properties and
 * annotations, and any target corresponding data.
 * @author David Mason, dr.d.mason@gmail.com
 */
public class TextUnit3 implements ITextUnit {
    
    //copied most from TextUnit2
    
    public static final String TYPE_PARA = "paragraph";
    public static final String TYPE_LIST_ELEMENT = "list_element";
    public static final String TYPE_TITLE = "title";
    public static final String TYPE_HEADER = "header";

    private static final int TARGETS_INITCAP = 2;

    private String id;
    private int refCount;
    private String name;
    private String type;
    private boolean isTranslatable = true;
    private boolean preserveWS;
    private ISkeleton skeleton;
    private LinkedHashMap<String, Property> properties;
    private Annotations annotations;
    private String mimeType;

    //this keeps track of the currently selected locale the user is working with
    //TODO consider whether using null when no locale has been selected is a good idea
    //     it is primarily used for accessing source, so it should be fine as
    //     long as it is only used when more sources are added.
    private LocaleId activeLocale = null;
    
    //this holds the default source that is used when the TextUnit is initially
    // created, and will be cloned whenever a target is created without specifying
    // a unique source.
    //This should allow most cases (with all targets using a single source) to
    // be simple and efficient (source map only used if required).
    private TextContainer defaultSource;

    //TODO this is just so make IAlignedSegments work without too much messing
    // about. It will just be set to point to the source for the current locale
    // whenever the current locale is changed.
    // Could actually do this as standard - would probably be more efficient and
    // have saved me some adjusting of code (but the refactoring was good for learning)
    private TextContainer source;

    //this indicates the number of locales that use a source other than the default,
    // so that hash lookups can be avoided in the majority of cases
    private int customSourceCount = 0;

    private ConcurrentHashMap<LocaleId, TextContainer> sources;
    private ConcurrentHashMap<LocaleId, TextContainer> targets;

    /* TODO: consider refering to sources and targets from hashes rather than storing them in there.
     * this would mean the same source/target object could be referenced for different locales.
     * could be handy for use where some locales share a lot of the same text and segmentation
     * such as same language in different locales. Would then need to be able to
     * specify when updating a target whether to split off a new target, or apply
     * changed content to all locales using the target.
     * 
     * For now the implementation will produce a full source/target pair for each
     * locale.
     */



    //TODO this method is the main one that would need adding to the interface
    // to allow multiple sources. It could even be made a sub-interface of
    // ITextUnit, so that if only single sources are to be provided it can be
    // safely ignored.
    public void setActiveLocale(LocaleId loc) {
        activeLocale = loc;
        source = getSource(loc);
    }

    //custom sources are added and manipulated by changing active locale then
    //setting the source. A target can then be created from the new source.
    //Current implementation means that SetSourceContent will not create a new
    //custom source.



    //This method duplicates the text of the default source in a custom source
    public TextContainer createSource(LocaleId locId, boolean overwriteExisting) {
        return createSource(defaultSource.toString(), locId, overwriteExisting);
    }


    //This method creates a new custom source from the given source text. Null locId will lead to the default source being updated.
    public TextContainer createSource(String sourceText,
                                      LocaleId locId,
                                      boolean overwriteExisting) {
        TextContainer tc;
        if (locId == null) {
            if (overwriteExisting) {
                defaultSource = new TextContainer(sourceText);
            }
            tc = defaultSource;
        } else {
            if (sources == null) {
                sources = new ConcurrentHashMap<LocaleId, TextContainer>(TARGETS_INITCAP);
            }
            tc = sources.get(locId);
            if (tc == null) {
                customSourceCount ++;
                tc = new TextContainer(sourceText);
                sources.put(locId, tc);
            } else if (overwriteExisting) {
                tc = new TextContainer(sourceText);
                sources.put(locId, tc);
            }
        }
        return tc;
    }




    // TODO: this is too big to be an inner/anonymous class - can we make it a
    // protected class in the same package?
    private final IAlignedSegments segments = new IAlignedSegments () {
        @Override
        public Segment splitTarget (LocaleId trgLoc,
                Segment trgSeg,
                int splitPos)
        {
                ISegments trgSegs = getTarget_DIFF(trgLoc).getSegments();
                int segIndex = trgSegs.getIndex(trgSeg.id);
                if ( segIndex == -1 ) return null; // Not a segment in this container.
                int partIndex = trgSegs.getPartIndex(segIndex);
                // Split the segment (with no spanned part)
                getTarget_DIFF(trgLoc).split(partIndex, splitPos, splitPos, false);
                // New segment is on the right of original (so: segIndex+1)
                Segment newTrgSeg = trgSegs.get(segIndex+1);

                // Create the corresponding source segment
                Segment srcSeg = getCorrespondingSource(trgSeg);
                ISegments srcSegs = source.getSegments();
                segIndex = srcSegs.getIndex(srcSeg.id);
                srcSegs.insert(segIndex+1, new Segment(newTrgSeg.id));

                // Create the corresponding segments in the other targets
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





    /**
     * Creates a new TextUnit object with its identifier.
     * @param id the identifier of this resource.
     */
    public TextUnit3 (String id) {
            create(id, null, false, null);
    }

    /**
     * Creates a new TextUnit object with its identifier and a text.
     * @param id the identifier of this resource.
     * @param sourceText the initial text of the source.
     */
    public TextUnit3 (String id,
            String sourceText)
    {
            create(id, sourceText, false, null);
    }

    /**
     * Creates a new TextUnit object with its ID, a text, and a flag indicating if it is a referent or not.
     * @param id the identifier of this resource.
     * @param sourceText the initial text of the source (can be null).
     * @param isReferent indicates if this resource is a referent (i.e. is referred to
     * by another resource) or not.
     */
    public TextUnit3 (String id,
            String sourceText,
            boolean isReferent)
    {
            create(id, sourceText, isReferent, null);
    }

    /**
     * Creates a new TextUnit object with its identifier, a text, a flag indicating
     * if it is a referent or not, and a given MIME type.
     * @param id the identifier of this resource.
     * @param sourceText the initial text of the source (can be null).
     * @param isReferent indicates if this resource is a referent (i.e. is referred to
     * by another resource) or not.
     * @param mimeType the MIME type identifier for the content of this TextUnit.
     */
    public TextUnit3 (String id,
            String sourceText,
            boolean isReferent,
            String mimeType)
    {
            create(id, sourceText, isReferent, mimeType);
    }

    private void create (String id,
                         String sourceText,
                         boolean isReferent,
                         String mimeType)
    {
        targets = new ConcurrentHashMap<LocaleId, TextContainer>(TARGETS_INITCAP);
        this.id = id;
        refCount = (isReferent ? 1 : 0);
        this.mimeType = mimeType;

        defaultSource = new TextContainer(sourceText);
        source = defaultSource;
    }

    /**
     * Gets the string representation of the source container.
     * If there are multiple sources, the source for the active locale is used.
     * If the container is segmented, the representation shows the merged
     * segments. Inline codes are also included.
     * @return the string representation of the source container.
     */
    @Override
    public String toString () {
        return getSource().toString();
    }

    //TODO add clone method


    //TODO this method loses its meaning to some degree. Should it report empty
    // when there is no source for the active locale even though there is a
    // default source?
    @Override
    public boolean isEmpty() {
        return getSource().isEmpty();
    }

    /*
     * Will return the source associated with the active locale (may be the default source).
     */
    @Override
    public TextContainer getSource() {
        return getSource(activeLocale);
    }

    /*
     * Will return the source associated with the given locale. Default if no
     * custom source is present for the locale.
     * @param loc the locale used to retrieve an appropriate source
     * @return the source associated with the given locale (may be default source)
     */
    private TextContainer getSource(LocaleId loc) {
        TextContainer theSource = null;

        if (customSourceCount > 0 && loc != null) {
            if (sources.containsKey(loc)) {
                theSource = sources.get(loc);
            }
        }

        return ((theSource != null) ? theSource : defaultSource);
    }


    /*
     * Making this always set the source for the activeLocale. This means locale
     * should be set to null to set the default text.
     *
     * TODO: Another method would be
     * better here that sets the source for a particular locale, so that this
     * will always set the default source, or modify this signature so that it
     * takes a LocaleId (null for default locale source).
     */
    
    @Override
    public TextContainer setSource(TextContainer textContainer) {
        if (activeLocale == null) {
            defaultSource = textContainer;
        } else {
            if (sources == null) {
                sources = new ConcurrentHashMap<LocaleId, TextContainer>(TARGETS_INITCAP);
            }
            //add a custom source for the current locale, and record whether the
            // number of custom sources has increased.
            customSourceCount += (sources.containsKey(activeLocale)) ? 0 : 1;
            sources.put(activeLocale, textContainer);
        }
        return textContainer;
    }

    @Override
    public TextContainer getTarget_DIFF(LocaleId locId) {
        //tries to create a new target from source, but set to fail if none exists
        return createTarget(locId, false, IResource.COPY_SEGMENTS);
    }

    @Override
    public TextContainer setTarget(LocaleId locId, TextContainer text) {
        targets.put(locId, text);
	return text;
    }

    //This will also remove any custom source associated with this locale
    //TODO for the border cases it would be more appropriate to move the
    //     source deletion to another method or rename this method.
    @Override
    public void removeTarget(LocaleId locId) {
        if ( hasTarget(locId) ) {
	    targets.remove(locId);
	}
        if (customSourceCount > 0) {
            //decrement custom source count if a source can be removed
            customSourceCount -= (sources.remove(locId) != null ) ? 1 : 0 ;
        }
    }

    @Override
    public boolean hasTarget(LocaleId locId) {
        //ConcurrentHashMap doesn't allow nulls so no need to check for null
        return targets.containsKey(locId);
    }

    //This creates a target associated with the default source
    //TODO could make it use the source for the currently selected locale so that
    // source could be crated before target and used in the cration of the target.
    @Override
    public TextContainer createTarget(LocaleId locId, boolean overwriteExisting, int creationOptions) {
        TextContainer trgCont = targets.get(locId);
        if (( trgCont == null ) || overwriteExisting ) {
            trgCont = getSource(locId).clone((creationOptions & COPY_PROPERTIES) == COPY_PROPERTIES);
            if ( (creationOptions & COPY_SEGMENTS) != COPY_SEGMENTS ) {
                trgCont.joinAll();
            }
            if ( (creationOptions & COPY_CONTENT) != COPY_CONTENT ) {
                for ( Segment seg : trgCont.getSegments() ) {
                    seg.text.clear();
                }
            }
            targets.put(locId, trgCont);
        }
        return trgCont;
    }
    
    
    

    //Sets for current locale
    @Override
    public TextFragment setSourceContent(TextFragment content) {
        TextContainer theSource = getSource();
        theSource.setContent(content);
        // We can use this because the setContent() removed any segmentation
        TextFragment tf = theSource.getSegments().getFirstContent();
        return tf;
    }

    @Override
    public TextFragment setTargetContent(LocaleId locId, TextFragment content) {
        TextContainer tc = createTarget(locId, false, CREATE_EMPTY);
	tc.setContent(content);
        // We can use this because the setContent() removed any segmentation
	return tc.getSegments().getFirstContent();
    }

    //TODO ensure that the segments returned will work consistently on a pair of
    //source/target without breaking when the locale is changed
    @Override
    public IAlignedSegments getSegments() {
        return segments;
    }


    //for source of active locale
    @Override
    public ISegments getSourceSegments() {
        return getSource().getSegments();
    }

    @Override
    public ISegments getTargetSegments(LocaleId trgLoc) {
        return getTarget_DIFF(trgLoc).getSegments();
    }

    //for source of active locale
    @Override
    public Segment getSourceSegment(String segId, boolean createIfNeeded) {
        Segment seg = getSource().getSegments().get(segId);
        if (( seg == null ) && createIfNeeded ) {
            // If the segment does not exists: create a new one if requested
            seg = new Segment(segId);
            getSource().getSegments().append(seg);
        }
        return seg;
    }

    @Override
    public Segment getTargetSegment(LocaleId trgLoc, String segId, boolean createIfNeeded) {
        Segment seg = getTarget_DIFF(trgLoc).getSegments().get(segId);
        if (( seg == null ) && createIfNeeded ) {
            // If the segment does not exists: create a new one if requested
            seg = new Segment(segId);
            getTarget_DIFF(trgLoc).getSegments().append(seg);
        }
        return seg;
    }

    @Override
    public String getName () {
        return name;
    }

    @Override
    public void setName (String name) {
        this.name = name;
    }

    @Override
    public String getType () {
        return type;
    }

    @Override
    public void setType (String value) {
        type = value;
    }

    @Override
    public String getMimeType () {
        return mimeType;
    }

    @Override
    public void setMimeType (String mimeType) {
        this.mimeType = mimeType;
    }

    @Override
    public Property getProperty (String name) {
        if ( properties == null ) return null;
        return properties.get(name);
    }

    @Override
    public Property setProperty (Property property) {
        if ( properties == null ) properties = new LinkedHashMap<String, Property>();
        properties.put(property.getName(), property);
        return property;
    }

    @Override
    public void removeProperty (String name) {
        if ( properties != null ) {
            properties.remove(name);
        }
    }

    @Override
    public Set<String> getPropertyNames () {
        if ( properties == null ) properties = new LinkedHashMap<String, Property>();
        return properties.keySet();
    }

    @Override
    public boolean hasProperty (String name) {
        if ( properties == null ) return false;
        return properties.containsKey(name);
    }

    //All for source associated with active locale
    @Override
    public Property getSourceProperty (String name) {
        return source.getProperty(name);
    }

    @Override
    public Property setSourceProperty (Property property) {
        return source.setProperty(property);
    }

    @Override
    public void removeSourceProperty (String name) {
        source.removeProperty(name);
    }

    @Override
    public Set<String> getSourcePropertyNames () {
        return source.getPropertyNames();
    }

    @Override
    public boolean hasSourceProperty (String name) {
        return source.hasProperty(name);
    }

    @Override
    public Property getTargetProperty (LocaleId locId,
            String name)
    {
        if ( !hasTarget(locId) ) return null;
        return getTarget_DIFF(locId).getProperty(name);
    }

    @Override
    public Property setTargetProperty (LocaleId locId,
            Property property)
    {
        return createTarget(locId, false, IResource.COPY_SEGMENTS).setProperty(property);
    }

    @Override
    public void removeTargetProperty (LocaleId locId,
            String name)
    {
        if ( hasTarget(locId) ) {
            getTarget_DIFF(locId).removeProperty(name);
        }
    }

    @Override
    public Set<String> getTargetPropertyNames (LocaleId locId) {
        if ( hasTarget(locId) ) {
            return getTarget_DIFF(locId).getPropertyNames();
        }
        // Else:
        return Collections.emptySet();
    }

    @Override
    public Set<LocaleId> getTargetLocales () {
        return targets.keySet();
    }

    @Override
    public boolean hasTargetProperty (LocaleId locId,
            String name)
    {
        TextContainer tc = getTarget_DIFF(locId);
        if ( tc == null ) return false;
        return (tc.getProperty(name) != null);
    }

    @Override
    public Property createTargetProperty (LocaleId locId,
            String name,
            boolean overwriteExisting,
            int creationOptions)
    {
        // Get the target or create an empty one
        TextContainer tc = createTarget(locId, false, CREATE_EMPTY);
        // Get the property if it exists
        Property prop = tc.getProperty(name);
        // If it does not exists or if we overwrite: create a new one
        if (( prop == null ) || overwriteExisting ) {
            // Get the source property
            prop = source.getProperty(name);
            if ( prop == null ) {
                // If there is no source, create an empty property
                return tc.setProperty(new Property(name, "", false));
            }
            else { // If there is a source property
                // Create a copy, empty or not depending on the options
                if ( creationOptions == CREATE_EMPTY ) {
                    return tc.setProperty(new Property(name, "", prop.isReadOnly()));
                }
                else {
                    return tc.setProperty(prop.clone());
                }
            }
        }
        return prop;
    }

    @Override
    public boolean isTranslatable () {
        return isTranslatable;
    }

    @Override
    public void setIsTranslatable (boolean value) {
        isTranslatable = value;
    }

    @Override
    public boolean preserveWhitespaces () {
        return preserveWS;
    }

    @Override
    public void setPreserveWhitespaces (boolean value) {
        preserveWS = value;
    }

    @Override
    public String getId () {
        return id;
    }

    @Override
    public void setId (String id) {
        this.id = id;
    }

    @Override
    public ISkeleton getSkeleton () {
        return skeleton;
    }

    @Override
    public void setSkeleton (ISkeleton skeleton) {
        this.skeleton = skeleton;
    }

    @Override
    public <A extends IAnnotation> A getAnnotation (Class<A> annotationType) {
        if ( annotations == null ) return null;
        return annotationType.cast(annotations.get(annotationType) );
    }

    @Override
    public void setAnnotation (IAnnotation annotation) {
        if ( annotations == null ) {
            annotations = new Annotations();
        }
        annotations.set(annotation);
    }

    @Override
    public Iterable<IAnnotation> getAnnotations () {
        if ( annotations == null ) {
            return Collections.emptyList();
        }
        return annotations;
    }

    @Override
    public boolean isReferent () {
        return (refCount > 0);
    }

    @Override
    public void setIsReferent (boolean value) {
        refCount = (value ? 1 : 0 );
    }

    @Override
    public int getReferenceCount () {
        return refCount;
    }

    @Override
    public void setReferenceCount (int value) {
        refCount = value;
    }

}
