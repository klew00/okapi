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
import net.sf.okapi.common.Range;
import net.sf.okapi.common.annotation.Annotations;
import net.sf.okapi.common.annotation.IAnnotation;

/**
 * Basic unit of extraction from a filter and also the resource associated with the filter event TEXT_UNIT.
 * The TextUnit object holds the extracted source text, all its properties and 
 * annotations, and any target corresponding data.
 */
public class TextUnit implements INameable, IReferenceable {

	/**
	 * Resource type value for a paragraph.
	 */
	public static final String TYPE_PARA = "paragraph";
	/**
	 * Resource type value for a list.
	 */
	public static final String TYPE_LIST_ELEMENT = "list_element";
	/**
	 * Resource type value for a title.
	 */
	public static final String TYPE_TITLE = "title";
	/**
	 * Resource type value for a header.
	 */
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
	
	private List<Range> srcSegRanges;
	private ConcurrentHashMap<LocaleId, List<Range>> trgSegRanges;
	// Do not serialize. This variable is used internally for efficiency
	private transient LocaleId syncLoc;

	/**
	 * Creates a new TextUnit object with its identifier.
	 * @param id the identifier of this resource.
	 */
	public TextUnit (String id) {
		create(id, null, false, null);
	}

	/**
	 * Creates a new TextUnit object with its identifier and a text.
	 * @param id the identifier of this resource.
	 * @param sourceText the initial text of the source.
	 */
	public TextUnit (String id,
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
	public TextUnit (String id,
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
	public TextUnit (String id,
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
	public TextUnit clone () {
		TextUnit tu = new TextUnit(getId());
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
		TextContainer tc = getTarget(locId);
		if ( tc == null ) return null;
		return tc.getProperty(name);
	}

	public Property setTargetProperty (LocaleId locId,
		Property property)
	{
		return createTarget(locId, false, IResource.CREATE_EMPTY).setProperty(property);
	}

	public void removeTargetProperty (LocaleId locId,
		String name)
	{
		TextContainer tc = getTarget(locId);
		if ( tc != null ) {
			tc.removeProperty(name);
		}
	}
	
	public Set<String> getTargetPropertyNames (LocaleId locId) {
		TextContainer tc = createTarget(locId, false, IResource.CREATE_EMPTY);
		return tc.getPropertyNames();
	}

	public boolean hasTargetProperty (LocaleId locId,
		String name)
	{
		TextContainer tc = getTarget(locId);
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
	 * Gets the target object for this TextUnit for a given locale.
	 * @param locId the locale to query.
	 * @return the target object for this text unit for the given locale,
	 * or null if it does not exist.
	 */
	public TextContainer getTarget (LocaleId locId) {
		return targets.get(locId);
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
		// Remove associated segmentation info if needed
		if ( trgSegRanges != null ) {
			trgSegRanges.remove(locId);
		}
		if ( syncLoc != null ) {
			if ( syncLoc.equals(locId) ) syncLoc = null;
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
		TextContainer trgCont = targets.get(locId);
		if (( trgCont == null ) || overwriteExisting ) {
			trgCont = getSource().clone((creationOptions & COPY_PROPERTIES) == COPY_PROPERTIES);
			if (( creationOptions == CREATE_EMPTY ) || ( creationOptions == COPY_PROPERTIES )) {
				trgCont.clear();
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
		return source.getSegments().getFirstContent();
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

	/**
	 * Indicates if the source text of this TextUnit is empty.
	 * @return true if the source text of this TextUnit is empty, false otherwise.
	 */
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
	 * <p>This methods also stores the boundaries for the segments so they can be re-applied later.
	 * for example when calling {@link #synchronizeSourceSegmentation(LocaleId)}.
	 * @param segmenter the segmenter to use to create the segments.
	 */
	public void createSourceSegmentation (ISegmenter segmenter) {
		segmenter.computeSegments(source);
		srcSegRanges = segmenter.getRanges();
		source.getSegments().create(srcSegRanges);
		syncLoc = null;
	}
	
	/**
	 * Segments the specified target content based on the rules provided by a given ISegmenter.
	 * <p>This method may cause the source and target segments to be desynchronized, that is:
	 * That each source segment may or may not be aligned with a corresponding target segment.
	 * You can associate a target-specific segmentation for the source using
	 * {@link #setSourceSegmentationForTarget(LocaleId, List)}.
	 * @param segmenter the segmenter to use to create the segments.
	 * @param targetLocale {@link LocaleId} of the target we want to segment.
	 */
	public void createTargetSegmentation (ISegmenter segmenter,
		LocaleId targetLocale)
	{
		TextContainer tc = getTarget(targetLocale);
		if ( tc == null ) {
			throw new RuntimeException(String.format("There is no target content for '%s'", targetLocale.toString()));
		}
		segmenter.computeSegments(tc);
		tc.getSegments().create(segmenter.getRanges());
	}
	
	/**
	 * Saves the current segment boundaries for the source.
	 * <p>This methods stores the boundaries for the segments so they can be re-applied later,
	 * for example when calling {@link #synchronizeSourceSegmentation(LocaleId)}.
	 * @return the boundaries that have been saved.
	 * @see #createSourceSegmentation(ISegmenter)
	 * @see #synchronizeSourceSegmentation(LocaleId)
	 */
	public List<Range> saveCurrentSourceSegmentation () {
		srcSegRanges = source.getSegments().getRanges();
		return srcSegRanges;
	}

	/**
	 * Sets the segments ranges for the source container so it matches the segmentation of a given target content.
	 * <p>This method does not synchronize the current segmentation of the source with that target.
	 * Use {@link #synchronizeSourceSegmentation(LocaleId)} for that.
	 * @param locId the locale to match.
	 * @param ranges the source segment ranges for matching the segments of that locale. 
	 */
	public void setSourceSegmentationForTarget (LocaleId locId,
		List<Range> ranges)
	{
		if ( trgSegRanges == null ) {
			trgSegRanges = new ConcurrentHashMap<LocaleId, List<Range>>();
		}
		trgSegRanges.put(locId, ranges);
		if ( syncLoc != null ) {
			if ( syncLoc.equals(locId) ) syncLoc = null;
		}
	}
	
	/**
	 * Sets the segmentation in the source content, in a way it matches the segmentation for a given locale.
	 * You must have called {@link #setSourceSegmentationForTarget(LocaleId, List)} or
	 * {@link #saveCurrentSourceSegmentation()} before.
	 * If the given locale has no corresponding segment ranges, the default source segmentation (defined
	 * when calling {@link #createSourceSegmentation(ISegmenter)}) is used.
	 * @param locId the locale to synchronize with.
	 * @see #createSourceSegmentation(ISegmenter)
	 * @see #setSourceSegmentationForTarget(LocaleId, List)
	 */
	public void synchronizeSourceSegmentation (LocaleId locId) {
		if ( syncLoc != null ) {
			// Avoid re-segmentation if possible
			if ( syncLoc.equals(locId) ) return;
		}
		List<Range> ranges = null;
		if ( trgSegRanges != null ) {
			// Try to get target-corresponding segmentation
			ranges = trgSegRanges.get(locId);
		}
		if ( ranges == null ) {
			// No target-specific ranges available: use the source
			ranges = srcSegRanges;
		}
		source.getSegments().create(ranges); // Ranges can be null: no segmentation occurs then
		syncLoc = locId;
	}
	
	/**
	 * Removes all segmentations (source and targets) in this text unit.
	 * All entries are converted to non-segmented entries.
	 */
	public void removeAllSegmentations () {
		// Desegment the source if needed
		if ( getSource().hasBeenSegmented() ) {
			getSource().joinAll();
		}
		// Remove default source segmentation ranges
		srcSegRanges = null;
		
		// Desegment all targets as needed
		for ( Entry<LocaleId, TextContainer> entry : targets.entrySet() ) {
			if ( entry.getValue().hasBeenSegmented() ) {
				entry.getValue().joinAll();
			}
		}
		// Removes all target-specific source segmentations 
		if ( trgSegRanges != null ) {
			trgSegRanges.clear();
		}
		// Re-synch to nothing
		syncLoc = null;
	}

	@Override
	public Iterable<IAnnotation> getAnnotations () {
		if ( annotations == null ) {
			return Collections.emptyList();
		}
		return annotations;
	}

}
