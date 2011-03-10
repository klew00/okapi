/*===========================================================================
  Copyright (C) 2008-2010 by the Okapi Framework contributors
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

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.Map.Entry;
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
 * Basic unit of extraction from a filter and also the resource associated with the filter event TEXT_UNIT.
 * The TextUnit object holds the extracted source text, all its properties and 
 * annotations, and any target corresponding data.
 */
public class TextUnit2 implements ITextUnit {

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
	private TextContainer source;
	private String mimeType;
	private ConcurrentHashMap<LocaleId, TextContainer> targets;
	
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
	public TextUnit2 (String id) {
		create(id, null, false, null);
	}

	/**
	 * Creates a new TextUnit object with its identifier and a text.
	 * @param id the identifier of this resource.
	 * @param sourceText the initial text of the source.
	 */
	public TextUnit2 (String id,
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
	public TextUnit2 (String id,
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
	public TextUnit2 (String id,
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
		source = new TextContainer(sourceText);
	}

	/**
	 * Gets the string representation of the source container.
	 * If the container is segmented, the representation shows the merged
	 * segments. Inline codes are also included.
	 * @return the string representation of the source container.
	 */
	@Override
	public String toString () {
		return source.toString();
	}
	
	/**
	 * Clones this TextUnit.
	 * @return A new TextUnit object that is a copy of this one. 
	 */
	@Override
	public TextUnit2 clone () {
		TextUnit2 tu = new TextUnit2(getId());
		if ( annotations != null ) {
			tu.setAnnotations(annotations.clone());
		}
		tu.setIsReferent(isReferent());
		tu.setIsTranslatable(isTranslatable);
		tu.setMimeType(getMimeType());
		tu.setName(getName());
		tu.setPreserveWhitespaces(preserveWS);
		tu.setReferenceCount(getReferenceCount());
		tu.setSkeleton(getSkeleton());
		tu.setSource(getSource().clone());
		tu.setType(getType());
		
		// Set all the main level properties
		if ( properties != null ) {
			for (Property prop : properties.values()) {
				tu.setProperty(prop.clone());
			}
		}
		
		// Set all the targets
		for (Entry<LocaleId, TextContainer> entry : targets.entrySet()) {
			tu.setTarget(entry.getKey(), entry.getValue().clone());
		}
		
		return tu;
	}
	
	/**
	 * Used by TextUnit clone method to copy over all annotations at once. 
	 * @param annotations the new annotations to set.
	 */
	protected void setAnnotations (Annotations annotations) {
		this.annotations = annotations;
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

	@Override
	public Property getSourceProperty (String name) {
		return source.getProperty(name);
	}

	@Override
	public Property setSourceProperty (Property property) {
		return source.setProperty(property);
	}
	
	@Override
	public Set<String> getSourcePropertyNames () {
		return source.getPropertyNames();
	}
	
	@Override
	public void removeSourceProperty (String name) {
		source.removeProperty(name);
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
	public boolean hasTargetProperty (LocaleId locId,
		String name)
	{
		TextContainer tc = getTarget_DIFF(locId);
		if ( tc == null ) return false;
		return (tc.getProperty(name) != null);
	}

	@Override
	public Set<LocaleId> getTargetLocales () {
		return targets.keySet();
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

	@Override
	public TextContainer getSource () {
		return source;
	}

	@Override
	public TextContainer setSource (TextContainer textContainer) {
		if ( textContainer == null ) {
			throw new NullPointerException("The source container of a TextUnit cannot be null.");
		}
		source = textContainer;
		//TODO: invalidate targets status???
		return source;
	}

//TODO: Change name after integration
// The name is different so we safely detect any place in the code where it's call and can fix the behavior if needed.
	@Override
	public TextContainer getTarget_DIFF (LocaleId locId) {
		return createTarget(locId, false, IResource.COPY_SEGMENTS);
	}

	@Override
	public TextContainer setTarget (LocaleId locId,
		TextContainer text)
	{
		targets.put(locId, text);
		return text;
	}

	@Override
	public void removeTarget (LocaleId locId) {
		if ( hasTarget(locId) ) {
			targets.remove(locId);
		}
	}

	@Override
	public boolean hasTarget (LocaleId locId) {
		return (targets.get(locId) != null);
	}

	@Override
	public TextContainer createTarget (LocaleId locId,
		boolean overwriteExisting,
		int creationOptions)
	{
		TextContainer trgCont = targets.get(locId);
		if (( trgCont == null ) || overwriteExisting ) {
			trgCont = getSource().clone((creationOptions & COPY_PROPERTIES) == COPY_PROPERTIES);
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

	@Override
	public TextFragment setSourceContent (TextFragment content) {
        source.setContent(content);
        // We can use this because the setContent() removed any segmentation
        TextFragment tf = source.getSegments().getFirstContent();
        return tf;
	}

	@Override
	public TextFragment setTargetContent (LocaleId locId,
		TextFragment content)
	{
		TextContainer tc = createTarget(locId, false, CREATE_EMPTY);
		tc.setContent(content);
        // We can use this because the setContent() removed any segmentation
		return tc.getSegments().getFirstContent();
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
	public boolean isEmpty () {
		return source.isEmpty();
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
	public Iterable<IAnnotation> getAnnotations () {
		if ( annotations == null ) {
			return Collections.emptyList();
		}
		return annotations;
	}

	@Override
	public IAlignedSegments getSegments () {
		return segments;
	}

	@Override
	public ISegments getSourceSegments () {
		return source.getSegments();
	}

	@Override
	public Segment getTargetSegment (LocaleId trgLoc,
		String segId,
		boolean createIfNeeded)
	{
		Segment seg = getTarget_DIFF(trgLoc).getSegments().get(segId);
		if (( seg == null ) && createIfNeeded ) {
			// If the segment does not exists: create a new one if requested
			seg = new Segment(segId);
			getTarget_DIFF(trgLoc).getSegments().append(seg);
		}
		return seg;
	}

	@Override
	public ISegments getTargetSegments (LocaleId trgLoc) {
		return getTarget_DIFF(trgLoc).getSegments();
	}
	
	@Override
	public Segment getSourceSegment (String segId,
		boolean createIfNeeded)
	{
		Segment seg = source.getSegments().get(segId);
		if (( seg == null ) && createIfNeeded ) {
			// If the segment does not exists: create a new one if requested
			seg = new Segment(segId);
			source.getSegments().append(seg);
		}
		return seg;
	}

    @Override
    public boolean hasVariantSource() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public IVariantSources getVariantSources() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
	
}
