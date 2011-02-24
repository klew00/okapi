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
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;
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
 *
 * Basic unit of extraction from a filter and also the resource associated with\
 * the filter event TEXT_UNIT.
 * The TextUnit object holds the extracted source text in one or more versions,
 * all its properties and annotations, and any target corresponding data.
 * 
 * @author David Mason, dr.d.mason@gmail.com
 */
public class TextUnit3 implements IMultiSourceTextUnit {
    
    //copied most from TextUnit2
    
    public static final String TYPE_PARA = "paragraph";
    public static final String TYPE_LIST_ELEMENT = "list_element";
    public static final String TYPE_TITLE = "title";
    public static final String TYPE_HEADER = "header";

    private static final int TARGETS_INITCAP = 2;
    private static final int SOURCES_INITCAP = 2;

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

    private LocaleId activeTargetLocale = null;
    private TextContainer defaultSource;

    private int customSourceCount = 0;
    private ConcurrentHashMap<LocaleId, TextContainer> customSources;
    private ConcurrentHashMap<LocaleId, TextContainer> targets;

    //TODO temporary field to make IAlignedSegments work. Remove when
    // IAlignedSegments has been updated
    private TextContainer source;



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

    //TODO make a single method for instantiating customSources when necessary

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

    @Override
    public void copyFromSingleSource(ITextUnit original) {
        if (original == null) throw new NullPointerException("cannot copy from a null source");

        IMultiSourceTextUnit mstu;

        this.setId(original.getId());
        this.setSource(original.getSource().clone());
        this.setIsReferent(original.isReferent());
        this.setMimeType(original.getMimeType());

        if (this.targets != null) this.targets.clear();
        for (LocaleId loc : original.getTargetLocales() ) {
            this.setTarget(loc, original.getTarget_DIFF(loc).clone());
        }

        if (this.properties != null) this.properties.clear();
        for (String propName : original.getPropertyNames()) {
            this.setProperty(original.getProperty(propName));
        }

        if (this.annotations != null) this.annotations.clear();
        for (IAnnotation annot : original.getAnnotations()) {
            this.setAnnotation(annot);
        }

        this.customSources = null;
        this.customSourceCount = 0;
        if (IMultiSourceTextUnit.class.isInstance(original)) {
            mstu = (IMultiSourceTextUnit)original;
            for (LocaleId loc : mstu.getTargetLocalesWithCustomSource()) {
                this.setCustomSource(loc, mstu.getSource(loc).clone());
            }
        }
    }



    @Override
    public void setActiveTargetLocale(LocaleId targetLocale) {
        activeTargetLocale = targetLocale;
        source = getSource(targetLocale);
    }


    //applies to source of active target locale
    @Override
    public boolean isEmpty() {
        return getSource().isEmpty();
    }

    @Override
    public boolean isSourceEmpty(LocaleId targetLocale) {
        return getSource(targetLocale).isEmpty();
    }


    @Override
    public TextContainer createCustomSource(LocaleId targetLocale, boolean overwriteExisting) {
        return createCustomSource(defaultSource.toString(), targetLocale, overwriteExisting);
    }

    @Override
    public TextContainer createCustomSource(String sourceText, LocaleId targetLocale, boolean overwriteExisting) {
        TextContainer customSource;
        if (targetLocale == null) {
            return null;
        } else {
            if (customSources == null)
                customSources = new ConcurrentHashMap<LocaleId, TextContainer>(SOURCES_INITCAP);
            customSource = customSources.get(targetLocale);
            if (customSource == null) {
                customSourceCount ++;
                customSource = new TextContainer(sourceText);
                customSources.put(targetLocale, customSource);
            } else if (overwriteExisting) {
                customSource = new TextContainer(sourceText);
                customSources.put(targetLocale, customSource);
            }
        }
        return customSource;
    }

    //gets source for active locale
    @Override
    public TextContainer getSource() {
        return getSource(activeTargetLocale);
    }

    @Override
    public TextContainer getSource(LocaleId targetLocale) {
        TextContainer theSource = null;

        if (customSourceCount > 0 && targetLocale != null) {
            if (customSources.containsKey(targetLocale)) {
                theSource = customSources.get(targetLocale);
            }
        }
        return ((theSource != null) ? theSource : defaultSource);
    }

    //source for active locale
    @Override
    public TextContainer setSource(TextContainer textContainer) {
        if (activeTargetLocale == null) {
            defaultSource = textContainer;
            return defaultSource;
        }
        return setCustomSource(activeTargetLocale, textContainer);
    }

    @Override
    public TextContainer setCustomSource(LocaleId targetLocale, TextContainer textContainer) {
        if (targetLocale == null) return null;
        else putSource(targetLocale, textContainer);
        return textContainer;
    }

    /* Adds or replaces a custom source for the given locale, creating a hash of
     * sources if required and incrementing the count if it is changed.
     *
     * @param targetLocale the target locale that uses the source
     * @param theSource the source to be put in the hash
     * @return the source that was put in the list
     */
    private TextContainer putSource(LocaleId targetLocale, TextContainer theSource) {
        if (customSources == null) customSources = new ConcurrentHashMap<LocaleId, TextContainer>(SOURCES_INITCAP);
        //can rely on a null meaning that there was no custom source because
        // ConcurrentHashMap cannot contain null values
        if (customSources.put(targetLocale, theSource) == null) customSourceCount++;
        return customSources.get(targetLocale);
    }


    @Override
    public void removeCustomSource(LocaleId targetLocale) {
        if (targetLocale == null) return;
        if (customSources == null) return;
        if (customSources.remove(targetLocale) != null) customSourceCount--;
    }

    @Override
    public boolean hasCustomSource() {
        return (customSourceCount > 0);
    }

    @Override
    public boolean hasCustomSource(LocaleId targetLocale) {
        return (customSources == null) ? false : customSources.containsKey(targetLocale);
    }


    //Sets for active locale
    @Override
    public TextFragment setSourceContent(TextFragment content) {
        LocaleId theLocale = null; //null for default source
        //only use non-null locale if there is already a custom source for it
        if (customSources != null && activeTargetLocale != null)
            if (customSources.containsKey(activeTargetLocale)) theLocale = activeTargetLocale;

        if (theLocale == null) {
            defaultSource.setContent(content);
            return defaultSource.getSegments().getFirstContent();
        } else {
            return setCustomSourceContent(theLocale, content);
        }
    }


    @Override
    public TextFragment setCustomSourceContent(LocaleId targetLocale, TextFragment content) throws NullPointerException {
        TextContainer theSource;
        if (targetLocale == null) throw new NullPointerException("targetLocale should not be null");

        if (customSources == null) customSources = new ConcurrentHashMap<LocaleId, TextContainer>(SOURCES_INITCAP);
        if (customSources.containsKey(targetLocale)) {
            theSource = getSource(targetLocale);
            theSource.setContent(content);
        } else {
            theSource = putSource(targetLocale, new TextContainer(content));
        }
        
        // We can use this because the setContent() removed any segmentation
        return theSource.getSegments().getFirstContent();
    }


    //Uses custom source if available for this target locale, otherwise uses
    // the default source
    @Override
    public TextContainer createTarget(LocaleId targetLocale, boolean overwriteExisting, int creationOptions) {
        TextContainer trgCont = targets.get(targetLocale);
        if (( trgCont == null ) || overwriteExisting ) {
            trgCont = getSource(targetLocale).clone((creationOptions & COPY_PROPERTIES) == COPY_PROPERTIES);
            if ( (creationOptions & COPY_SEGMENTS) != COPY_SEGMENTS ) {
                trgCont.joinAll();
            }
            if ( (creationOptions & COPY_CONTENT) != COPY_CONTENT ) {
                for ( Segment seg : trgCont.getSegments() ) {
                    seg.text.clear();
                }
            }
            targets.put(targetLocale, trgCont);
        }
        return trgCont;
    }


    @Override
    public TextContainer getTarget_DIFF(LocaleId locId) {
        return createTarget(locId, false, IResource.COPY_SEGMENTS);
    }

    @Override
    public TextContainer setTarget(LocaleId locId, TextContainer text) {
        targets.put(locId, text);
	return text;
    }

    //Does not remove associated custom source
    @Override
    public void removeTarget(LocaleId locId) {
        if ( hasTarget(locId) ) {
	    targets.remove(locId);
	}
    }

    @Override
    public boolean hasTarget(LocaleId locId) {
        //ConcurrentHashMap doesn't allow nulls so no need to check for null
        return targets.containsKey(locId);
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
        //Disabled until predictable behaviour can be ensured under IMultiSourceTextUnit
        //return segments
        throw new UnsupportedOperationException("Not supported yet.");
    }

    //TODO look at making IAlignedSegments use an action listener model so that
    // if associated source/target are removed it can respond appropriately
    @Override
    public IAlignedSegments getSegments(LocaleId loc) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    //for source of active locale
    @Override
    public ISegments getSourceSegments() {
        return getSource().getSegments();
    }

    @Override
    public ISegments getSourceSegments(LocaleId targetLocale) {
        return getSource(targetLocale).getSegments();
    }

    //for source of active locale
    @Override
    public Segment getSourceSegment(String segId, boolean createIfNeeded) {
        return getSourceSegment(activeTargetLocale, segId, createIfNeeded);
    }

    @Override
    public Segment getSourceSegment(LocaleId loc, String segId, boolean createIfNeeded) {
        Segment seg = getSourceSegments(loc).get(segId);
        if (( seg == null ) && createIfNeeded ) {
            // If the segment does not exists: create a new one if requested
            seg = new Segment(segId);
            getSource().getSegments().append(seg);
        }
        return seg;
    }

    @Override
    public ISegments getTargetSegments(LocaleId trgLoc) {
        return getTarget_DIFF(trgLoc).getSegments();
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
    public Set<LocaleId> getTargetLocales () {
        return targets.keySet();
    }

    @Override
    public Set<LocaleId> getTargetLocalesWithCustomSource() {
        return (customSources == null) ? new HashSet<LocaleId>() : customSources.keySet();
    }


    @Override
    public String getName () {return name;}
    @Override
    public void setName (String name) {this.name = name;}

    @Override
    public String getType () {return type;}
    @Override
    public void setType (String value) {type = value;}

    @Override
    public String getMimeType () {return mimeType;}
    @Override
    public void setMimeType (String mimeType) {this.mimeType = mimeType;}

    @Override
    public boolean isTranslatable () {return isTranslatable;}
    @Override
    public void setIsTranslatable (boolean value) {isTranslatable = value;}

    @Override
    public boolean preserveWhitespaces () {return preserveWS;}
    @Override
    public void setPreserveWhitespaces (boolean value) {preserveWS = value;}

    @Override
    public String getId () {return id;}
    @Override
    public void setId (String id) {this.id = id;}

    @Override
    public ISkeleton getSkeleton () {return skeleton;}
    @Override
    public void setSkeleton (ISkeleton skeleton) {this.skeleton = skeleton;}

    @Override
    public boolean isReferent () {return (refCount > 0);}
    @Override
    public void setIsReferent (boolean value) {refCount = (value ? 1 : 0 );}

    @Override
    public int getReferenceCount () {return refCount;}
    @Override
    public void setReferenceCount (int value) {refCount = value;}



    @Override
    public Set<String> getPropertyNames () {
        if ( properties == null ) properties = new LinkedHashMap<String, Property>();
        return properties.keySet();
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
    public boolean hasProperty (String name) {
        if ( properties == null ) return false;
        return properties.containsKey(name);
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



    //for source of active locale
    @Override
    public Property getSourceProperty(String name) {
        return getSourceProperty(activeTargetLocale, name);
    }

    @Override
    public Property getSourceProperty(LocaleId targetLocale, String name) {
        return getSource(targetLocale).getProperty(name);
    }

    //for source of active locale
    @Override
    public Property setSourceProperty (Property property) {
        return setSourceProperty(activeTargetLocale, property);
    }

    @Override
    public Property setSourceProperty(LocaleId targetLocale, Property property) {
        return getSource(targetLocale).setProperty(property);
    }

    //for source of active locale
    @Override
    public void removeSourceProperty (String name) {
        removeSourceProperty(activeTargetLocale, name);
    }

    @Override
    public void removeSourceProperty(LocaleId targetLocale, String name) {
        getSource(targetLocale).removeProperty(name);
    }

    //for source of active locale
    @Override
    public Set<String> getSourcePropertyNames () {
        return getSourcePropertyNames(activeTargetLocale);
    }

    @Override
    public Set<String> getSourcePropertyNames(LocaleId targetLocale) {
        return getSource(targetLocale).getPropertyNames();
    }

    //for source of active locale
    @Override
    public boolean hasSourceProperty (String name) {
        return hasSourceProperty(activeTargetLocale, name);
    }

    @Override
    public boolean hasSourceProperty(LocaleId targetLocale, String name) {
        return getSource(targetLocale).hasProperty(name);
    }


    @Override
    public Property getTargetProperty (LocaleId locId, String name) {
        if ( !hasTarget(locId) ) return null;
        return getTarget_DIFF(locId).getProperty(name);
    }

    @Override
    public Property setTargetProperty (LocaleId locId, Property property) {
        return createTarget(locId, false, IResource.COPY_SEGMENTS).setProperty(property);
    }

    @Override
    public void removeTargetProperty (LocaleId locId, String name) {
        if ( hasTarget(locId) )  getTarget_DIFF(locId).removeProperty(name);
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
    public boolean hasTargetProperty (LocaleId locId, String name) {
        TextContainer tc = getTarget_DIFF(locId);
        if ( tc == null ) return false;
        return (tc.getProperty(name) != null);
    }

    @Override
    public Property createTargetProperty (LocaleId locId,
                                          String name,
                                          boolean overwriteExisting,
                                          int creationOptions) {
        // Get the target or create an empty one
        TextContainer tc = createTarget(locId, false, CREATE_EMPTY);
        // Get the property if it exists
        Property prop = tc.getProperty(name);
        // If it does not exists or if we overwrite: create a new one
        if (( prop == null ) || overwriteExisting ) {
            // Get the source property
            prop = getSource(locId).getProperty(name);
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

    /**
     * Gets the string representation of the source container.
     * If there are multiple sources, the source for the active target locale is used.
     * If the container is segmented, the representation shows the merged
     * segments. Inline codes are also included.
     * @return the string representation of the source container.
     */
    @Override
    public String toString () {
        return getSource().toString();
    }



    
    @Override
    public void propagateSourceProperty(LocaleId from, String propertyName, boolean overwriteExisting) {
        if (!hasCustomSource()) return;

        //Could be made more efficient by first checking that the property to copy exists
        //Not doing that for now in order to keep code less complicated

        //the following will try to copy the property to the origin source, but
        // this is ok as it is prevented in the other propagateSourceProperty method

        //propagate to default source
        propagateSourceProperty(from, null, propertyName, overwriteExisting);

        //propagate to all custom sources
        for (LocaleId loc : getTargetLocalesWithCustomSource()) {
            propagateSourceProperty(from, loc, propertyName, overwriteExisting);
        }
    }

    @Override
    public void propagateSourceProperty(LocaleId from, LocaleId to, String propertyName, boolean overwriteExisting) {
        if (!hasCustomSource()) return;
        if (from == null ? from == to : from.equals(to)) return; //compare from & to without NullPointerException
        Property p = getSourceProperty(from, propertyName);
        if (p != null)
            if ( overwriteExisting || !hasSourceProperty(to, propertyName) )
                setSourceProperty(to, p);
    }


    @Override
    public void propagateAllSourceProperties(LocaleId from, boolean overwriteExisting) {
        if (!hasCustomSource()) return;
        //Could be made more efficient as property names are retrieved on each
        // function call, but leaving it for the moment to keep code simple
        
        //propagate to default source
        propagateAllSourceProperties(from, null, overwriteExisting);

        //propagate to all custom sources
        for (LocaleId loc : getTargetLocalesWithCustomSource()) {
            propagateAllSourceProperties(from, loc, overwriteExisting);
        }
    }
    
    @Override
    public void propagateAllSourceProperties(LocaleId from, LocaleId to, boolean overwriteExisting) {
        if (!hasCustomSource()) return;
        if (from == null ? from == to : from.equals(to)) return;
        //get a list of all properties from from
        for (String propName : getSourcePropertyNames(from)) {
            //for each, propagate it to to
            propagateSourceProperty(from, to, propName, overwriteExisting);
        }
    }


    
    @Override
    public <A extends IAnnotation> void propagateSourceAnnotation(LocaleId from, Class<A> type, boolean overwriteExisting) {
        if (!hasCustomSource()) return;

        //propagate to default source
        propagateSourceAnnotation(from, null, type, overwriteExisting);

        //propagate to all custom sources
        for (LocaleId loc : getTargetLocalesWithCustomSource()) {
            propagateSourceAnnotation(from, loc, type, overwriteExisting);
        }
    }
    
    @Override
    public <A extends IAnnotation> void propagateSourceAnnotation(LocaleId from, LocaleId to, Class<A> type, boolean overwriteExisting) {
        if (!hasCustomSource()) return;
        if (from == null ? from == to : from.equals(to)) return;

        IAnnotation fromA = getSource(from).getAnnotation(type);
        TextContainer toSource = getSource(to);
        if (fromA != null) {
            if (overwriteExisting || ( toSource.getAnnotation(type) == null ) ) {
                toSource.setAnnotation(fromA);
            }
        }
    }

    @Override
    public void propagateAllSourceAnnotations(LocaleId from, boolean overwriteExisting) {
        if (!hasCustomSource()) return;

        //propagate to default source
        propagateAllSourceAnnotations(from, null, overwriteExisting);

        //propagate to all custom sources
        for (LocaleId loc : getTargetLocalesWithCustomSource()) {
            propagateAllSourceAnnotations(from, loc, overwriteExisting);
        }
    }

    @Override
    public void propagateAllSourceAnnotations(LocaleId from, LocaleId to, boolean overwriteExisting) {
        if (!hasCustomSource()) return;
        if (from == null ? from == to : from.equals(to)) return;

        for (IAnnotation annot : getSource(from).getAnnotations()) {
            //TODO check that getClass() will always return the correct class for looking up the annotation
            propagateSourceAnnotation(from, to, annot.getClass(), overwriteExisting);
        }
    }



    //TODO check this
    /**
     * Clones this TextUnit.
     * @return A new TextUnit object that is a copy of this one.
     */
    @Override
    public TextUnit3 clone () {
        TextUnit3 tu = new TextUnit3(getId());
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

        // Set all custom sources
        if (hasCustomSource()) {
            tu.customSources = new ConcurrentHashMap<LocaleId, TextContainer>(SOURCES_INITCAP);
            tu.customSources.putAll(customSources);
        }

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



}
