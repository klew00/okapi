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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import net.sf.okapi.common.IResource;
import net.sf.okapi.common.ISegmenter;
import net.sf.okapi.common.ISkeleton;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.annotation.Annotations;
import net.sf.okapi.common.annotation.IAnnotation;

/**
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
	 * Gets the string representation of the source text of this TextUnit.
	 * @return the source text of this TextUnit.
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
	 * @param annotations
	 */
	protected void setAnnotations(Annotations annotations) {
		this.annotations = annotations;
	}
	
	public String getId () {
		return id;
	}

	public void setId (String id) {
		this.id = id;
	}

	public ISkeleton getSkeleton () {
		return skeleton;
	}

	public void setSkeleton (ISkeleton skeleton) {
		this.skeleton = skeleton;
	}

	public String getName () {
		return name;
	}

	public void setName (String name) {
		this.name = name;
	}

	public String getType () {
		return type;
	}
	
	public void setType (String value) {
		type = value;
	}
	
	public <A extends IAnnotation> A getAnnotation (Class<A> annotationType) {
		if ( annotations == null ) return null;
		return annotationType.cast(annotations.get(annotationType) );
	}

	public void setAnnotation (IAnnotation annotation) {
		if ( annotations == null ) {
			annotations = new Annotations();
		}
		annotations.set(annotation);
	}

	public Property getProperty (String name) {
		if ( properties == null ) return null;
		return properties.get(name);
	}

	public Property setProperty (Property property) {
		if ( properties == null ) properties = new LinkedHashMap<String, Property>();
		properties.put(property.getName(), property);
		return property;
	}
	
	public void removeProperty (String name) {
		if ( properties != null ) {
			properties.remove(name);
		}
	}
	
	public Set<String> getPropertyNames () {
		if ( properties == null ) properties = new LinkedHashMap<String, Property>();
		return properties.keySet();
	}

	public boolean hasProperty (String name) {
		if ( properties == null ) return false;
		return properties.containsKey(name);
	}

	public Property getSourceProperty (String name) {
		return source.getProperty(name);
	}

	public Property setSourceProperty (Property property) {
		return source.setProperty(property);
	}
	
	public Set<String> getSourcePropertyNames () {
		return source.getPropertyNames();
	}
	
	public void removeSourceProperty (String name) {
		source.removeProperty(name);
	}
	
	public boolean hasSourceProperty (String name) {
		return source.hasProperty(name);
	}

	public Property getTargetProperty (LocaleId locId,
		String name)
	{
		if ( !hasTarget(locId) ) return null;
		return getTarget_DIFF(locId).getProperty(name);
	}

	public Property setTargetProperty (LocaleId locId,
		Property property)
	{
		return createTarget(locId, false, IResource.CREATE_EMPTY).setProperty(property);
	}

	public void removeTargetProperty (LocaleId locId,
		String name)
	{
		if ( hasTarget(locId) ) {
			getTarget_DIFF(locId).removeProperty(name);
		}
	}
	
	public Set<String> getTargetPropertyNames (LocaleId locId) {
		if ( hasTarget(locId) ) {
			return getTarget_DIFF(locId).getPropertyNames();
		}
		// Else:
		return Collections.emptySet();
	}

	public boolean hasTargetProperty (LocaleId locId,
		String name)
	{
		TextContainer tc = getTarget_DIFF(locId);
		if ( tc == null ) return false;
		return (tc.getProperty(name) != null);
	}

	public Set<LocaleId> getTargetLocales () {
		return targets.keySet();
	}

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

	public boolean isTranslatable () {
		return isTranslatable;
	}
	
	public void setIsTranslatable (boolean value) {
		isTranslatable = value;
	}

	public boolean isReferent () {
		return (refCount > 0);
	}

	public void setIsReferent (boolean value) {
		refCount = (value ? 1 : 0 );
	}
	
	public int getReferenceCount () {
		return refCount;
	}
	
	public void setReferenceCount (int value) {
		refCount = value;
	}

	/**
	 * Gets the source object for this TextUnit (a {@link TextContainer} object).
	 * @return the source object for this TextUnit.
	 */
	public TextContainer getSource () {
		return source;
	}

	/**
	 * Sets the source object for this TextUnit. Any existing source object is overwritten.
	 * @param textContainer the source object to set.
	 * @return the source object that has been set.
	 */
	public TextContainer setSource (TextContainer textContainer) {
		if ( textContainer == null ) {
			throw new NullPointerException("The source container of a TextUnit cannot be null.");
		}
		source = textContainer;
		return source;
	}

    /**
	 * Gets the target object for this TextUnit for a given locale. If the target does not exists
	 * one is created automatically.
	 * @param locId the locale to query.
	 * @return the target object for this text unit for the given locale. Never returns null.
	 */
	public TextContainer getTarget_DIFF (LocaleId locId) {
		return createTarget(locId, false, IResource.CREATE_EMPTY);
	}

    /**
	 * Sets the target object for this TextUnit for a given locale.
	 * Any existing target object for the given locale is overwritten.
	 * To set a target object based on the source, use the
	 * {@link #createTarget(LocaleId, boolean, int)} method.
	 * @param locId the target locale.
	 * @param text the target object to set.
	 * @return the target object that has been set.
	 */
	public TextContainer setTarget (LocaleId locId,
		TextContainer text)
	{
		targets.put(locId, text);
		return text;
	}

    /**
	 * Removes a given target object from this TextUnit.
	 * @param locId the target locale to remove.
	 */
	public void removeTarget (LocaleId locId) {
		if ( hasTarget(locId) ) {
			targets.remove(locId);
		}
	}

    /**
	 * Indicates if there is a target object for a given locale for this TextUnit.
	 * @param locId the locale to query.
	 * @return true if a target object exists for the given locale, false otherwise.
	 */
	public boolean hasTarget (LocaleId locId) {
		return (targets.get(locId) != null);
	}

    /**
	 * Creates or get the target for this TextUnit.
	 * @param locId the target locale.
	 * @param overwriteExisting true to overwrite any existing target for the given locale.
	 * False to not create a new target object if one already exists for the given locale.
	 * @param creationOptions creation options:
	 * <ul><li>CREATE_EMPTY: Create an empty target object.</li>
	 * <li>COPY_CONTENT: Copy the text of the source (and any associated in-line code).</li>
	 * <li>COPY_PROPERTIES: Copy the source properties.</li>
	 * <li>COPY_ALL: Same as (COPY_CONTENT|COPY_PROPERTIES).</li></ul>
	 * @return the target object that was created, or retrieved.
	 */
	public TextContainer createTarget (LocaleId locId,
		boolean overwriteExisting,
		int creationOptions)
	{
//TODO: need to create empty/segments if needed for CREATE_EMPTY		
		TextContainer trgCont = targets.get(locId);
		if (( trgCont == null ) || overwriteExisting ) {
			trgCont = getSource().clone((creationOptions & COPY_PROPERTIES) == COPY_PROPERTIES);
			if (( creationOptions == CREATE_EMPTY ) || ( creationOptions == COPY_PROPERTIES )) {
				// Remove content, but keep segments
				if ( trgCont.contentIsOneSegment() ) {
					trgCont.clear();
				}
				else { // Remove the content of the segments
					// Note that inter-segment parts are still not 
					for ( Segment seg : trgCont.getSegments() ) {
						seg.text.clear();
					}
				}
			}
			targets.put(locId, trgCont);
		}
		return trgCont;
	}

	/**
	 * Sets the content of the source for this TextUnit.
	 * @param content the new content to set.
	 * @return the new content of the source for this TextUnit. 
	 */
	public TextFragment setSourceContent (TextFragment content) {
        source.setContent(content);
        // We can use this because the setContent() removed any segmentation
        TextFragment tf = source.getSegments().getFirstContent();
        // Remove segmentation on all targets
//TODO: do we really want to do that? This has possibly a lot of side effects!!!        
        return tf;
	}

	/**
	 * Sets the content of the target for a given locale for this TextUnit.
	 * @param locId the locale to set.
	 * @param content the new content to set.
	 * @return the new content for the given target locale for this text unit. 
	 */
	public TextFragment setTargetContent (LocaleId locId,
		TextFragment content)
	{
		TextContainer tc = createTarget(locId, false, CREATE_EMPTY);
		tc.setContent(content);
        // We can use this because the setContent() removed any segmentation
		return tc.getSegments().getFirstContent();
	}

	public String getMimeType () {
		return mimeType;
	}
	
	public void setMimeType (String mimeType) {
		this.mimeType = mimeType;
	}	

	public boolean isEmpty () {
		return source.isEmpty();
	}

	public boolean preserveWhitespaces () {
		return preserveWS;
	}
	
	public void setPreserveWhitespaces (boolean value) {
		preserveWS = value;
	}

	/**
	 * Segments the source content based on the rules provided by a given ISegmenter.
	 * @param segmenter the segmenter to use to create the segments.
	 */
	public void segmentSource (ISegmenter segmenter) {
		segmenter.computeSegments(source);
		source.getSegments().create(segmenter.getRanges());
		//TODO: invalidate targets
	}
	
	/**
	 * Segments the specified target content based on the rules provided by a given ISegmenter.
	 * @param targetLocale {@link LocaleId} of the target we want to segment.
	 */
	public void segmentTarget (ISegmenter segmenter,
		LocaleId targetLocale)
	{
//TODO: what do we do if target doesn't exist?
		// Exception or just create empty segmented copy from source?
		if ( !hasTarget(targetLocale) ) {
			throw new RuntimeException(String.format("There is no target content for '%s'", targetLocale.toString()));
		}
		// else: segment
		TextContainer tc = getTarget_DIFF(targetLocale);
		segmenter.computeSegments(tc);
		tc.getSegments().create(segmenter.getRanges());
//TODO: reset all other segmentations?
		
	}
	
	/**
	 * Removes all segmentations (source and targets) in this text unit.
	 * All entries are converted to non-segmented entries.
	 */
	public void joinAll () {
		// Desegment the source if needed
		if ( getSource().hasBeenSegmented() ) {
			getSource().joinAll();
		}
		// Desegment all targets as needed
		for ( Entry<LocaleId, TextContainer> entry : targets.entrySet() ) {
			if ( entry.getValue().hasBeenSegmented() ) {
				entry.getValue().joinAll();
			}
		}
	}

	@Override
	public Iterable<IAnnotation> getAnnotations () {
		if ( annotations == null ) {
			return Collections.emptyList();
		}
		return annotations;
	}

	@Override
	public void align (List<AlignedPair> alignedSegmentPairs,
		LocaleId trgLoc)
	{
		// TODO Auto-generated method stub
	}

	@Override
	public void appendSegment (Segment srcSeg) {
		// Append the segment to the source
		source.getSegments().append(srcSeg);
		// Append a new empty segment to all targets
		for ( LocaleId loc : getTargetLocales() ) {
			ISegments trgSegs = targets.get(loc).getSegments();
			trgSegs.append(new Segment(srcSeg.id));
		}
	}

	@Override
	public void appendSegment (Segment srcSeg,
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
	public int getAlignemntStatus () {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Segment getCorrespondingSource (Segment trgSeg) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Segment getCorrespondingTarget (Segment srcSeg,
		LocaleId trgLoc)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void insertSegment (int index,
		Segment srcSeg)
	{
		// TODO Auto-generated method stub
	}

	@Override
	public void insertSegment (int index,
		Segment srcSeg,
		Segment trgSeg,
		LocaleId trgLoc)
	{
		// TODO Auto-generated method stub
	}

	@Override
	public void mergeSource (Segment srcSeg1,
		Segment srcSeg2)
	{
		// TODO Auto-generated method stub
	}

	@Override
	public boolean removeSegment (Segment seg) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setSourceSegment (int index,
		Segment srcSeg)
	{
		// TODO Auto-generated method stub
	}

	@Override
	public void setTargetSegment (int index,
		Segment trgSeg,
		LocaleId trgLoc)
	{
		// TODO Auto-generated method stub
	}

	@Override
	public void splitSource (Segment srcSeg,
		int splitPos)
	{
		// TODO Auto-generated method stub
	}

	@Override
	public void splitTarget (LocaleId trgLoc,
		Segment trgSeg,
		int splitPos)
	{
		// TODO Auto-generated method stub
	}

}
