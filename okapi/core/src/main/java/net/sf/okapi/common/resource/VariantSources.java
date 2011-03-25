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

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import static net.sf.okapi.common.IResource.*;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.annotation.IAnnotation;

/**
 * EXPERIMENTAL implementation, do not use yet.
 *
 * Provides a standard implementation of the IVariantSources interface
 * 
 * @author David Mason, dr.d.mason@gmail.com
 */
public class VariantSources implements IVariantSources {

    private static final int SOURCES_INITCAP = 2;
    private ConcurrentHashMap<LocaleId, TextContainer> sources;
    TextContainer defaultSource; //points to source in parent ITextUnit
    private int sourceCount;

    public VariantSources(TextContainer sourceContainer) {
        createVariantSources(sourceContainer, null);
    }

    public VariantSources(TextContainer sourceContainer, VariantSources original) {
        createVariantSources(sourceContainer, original);
    }

    public void setDefaultSource(TextContainer sourceContainer) {
        this.defaultSource = sourceContainer;
    }

    private void createVariantSources(TextContainer sourceContainer, VariantSources original) {
        this.defaultSource = sourceContainer;
        sourceCount = 0;
        sources = new ConcurrentHashMap<LocaleId, TextContainer>();
        if (original != null) {
            this.sources.putAll(original.sources);
            this.sourceCount = original.sourceCount;
        }
    }

    @Override
    public VariantSources clone() {
        //this links to the parent, which may not be appropriate where clone() is to be used
        //TODO consider whether this is appropriate
        VariantSources cs = new VariantSources(defaultSource);
        cs.sources = new ConcurrentHashMap<LocaleId, TextContainer>();
        cs.sources.putAll(this.sources);

        return cs;
    }

    @Override
    public boolean isEmpty(LocaleId targetLocale) {
        return get(targetLocale).isEmpty();
    }

    @Override
    public TextContainer create(LocaleId targetLocale, boolean overwriteExisting, int creationOptions) {
        TextContainer newSource = defaultSource.clone((creationOptions & COPY_PROPERTIES) == COPY_PROPERTIES);
        if ( (creationOptions & COPY_SEGMENTS) != COPY_SEGMENTS )
            newSource.joinAll();
        if ( (creationOptions & COPY_CONTENT) != COPY_CONTENT )
            for (Segment seg : newSource.getSegments())
                seg.text.clear();
        return create(defaultSource.clone(), targetLocale, overwriteExisting);
    }

    @Override
    public TextContainer create(TextContainer sourceText, LocaleId targetLocale, boolean overwriteExisting) {
        if (targetLocale == null) {
            return null;
        } else {
            if (sources == null)
                sources = new ConcurrentHashMap<LocaleId, TextContainer>(SOURCES_INITCAP);
            if ( (!sources.containsKey(targetLocale)) || overwriteExisting) {
                sourceCount += (sources.put(targetLocale, sourceText) == null) ? 1 : 0 ; //put returns null if there was no mapping
            }
        }
        return sources.get(targetLocale);
    }

    @Override
    public TextContainer get(LocaleId targetLocale) {
        TextContainer theSource = null;
        if (sourceCount > 0 && targetLocale != null)
            if (sources.containsKey(targetLocale))
                theSource = sources.get(targetLocale);
        return ((theSource != null) ? theSource : defaultSource);
    }

    @Override
    public TextContainer set(LocaleId targetLocale, TextContainer textContainer) throws IllegalArgumentException {
        if (targetLocale == null) throw new IllegalArgumentException("The target locale must not be null");
        return put(targetLocale, textContainer);
    }

    /* Adds or replaces a custom source for the given locale, creating a hash of
     * sources if required and incrementing the count if it is changed.
     *
     * @param targetLocale the target locale that uses the source
     * @param theSource the source to be put in the hash
     * @return the source that was put in the list
     */
    private TextContainer put(LocaleId targetLocale, TextContainer theSource) {
        if (sources == null) sources = new ConcurrentHashMap<LocaleId, TextContainer>(SOURCES_INITCAP);
        //can rely on a null meaning that there was no custom source because
        // ConcurrentHashMap cannot contain null values
        if (sources.put(targetLocale, theSource) == null) sourceCount++;
        return sources.get(targetLocale);
    }

    @Override
    public void remove(LocaleId targetLocale) throws IllegalArgumentException {
        if (targetLocale == null) throw new IllegalArgumentException("The target locale must not be null");
        if (sources == null) return;
        if (sources.remove(targetLocale) != null) sourceCount--;
    }

    @Override
    public boolean hasVariant(LocaleId targetLocale) throws IllegalArgumentException {
        if (targetLocale == null) throw new IllegalArgumentException("The target locale must not be null");
        return (sources == null) ? false : sources.containsKey(targetLocale);
    }

    @Override
    public int count() {
        return sourceCount;
    }

    @Override
    public boolean isEmpty() {
        return (sourceCount == 0);
    }

    @Override
    public TextFragment setContent(LocaleId targetLocale, TextFragment content) throws IllegalArgumentException {
        if (targetLocale == null) throw new IllegalArgumentException("targetLocale should not be null");

        TextContainer theSource;
        if (sources == null) sources = new ConcurrentHashMap<LocaleId, TextContainer>(SOURCES_INITCAP);
        if (sources.containsKey(targetLocale)) {
            theSource = get(targetLocale);
            theSource.setContent(content);
        } else {
            theSource = put(targetLocale, new TextContainer(content));
        }
        // We can use this because the setContent() removed any segmentation
        return theSource.getSegments().getFirstContent();
    }

    @Override
    public ISegments getSegments(LocaleId targetLocale) {
        return get(targetLocale).getSegments();
    }

    @Override
    public Segment getSegment(LocaleId targetLocale, String segId, boolean createIfNeeded) {
        Segment seg = getSegments(targetLocale).get(segId);
        if (( seg == null ) && createIfNeeded ) {
            seg = new Segment(segId);
            get(targetLocale).getSegments().append(seg);
        }
        return seg;
    }

    @Override
    public Set<LocaleId> getLocales() {
        return (sources == null) ? new HashSet<LocaleId>() : sources.keySet();
    }

    @Override
    public Property getProperty(LocaleId targetLocale, String name) {
        return get(targetLocale).getProperty(name);
    }

    @Override
    public Property setProperty(LocaleId targetLocale, Property property) {
        return get(targetLocale).setProperty(property);
    }

    @Override
    public void removeProperty(LocaleId targetLocale, String name) {
        get(targetLocale).removeProperty(name);
    }

    @Override
    public Set<String> getPropertyNames(LocaleId targetLocale) {
        return get(targetLocale).getPropertyNames();
    }

    @Override
    public boolean hasProperty(LocaleId targetLocale, String name) {
        return get(targetLocale).hasProperty(name);
    }


    @Override
    public void propagateProperty(LocaleId from, String propertyName, boolean overwriteExisting) {
        if (sources == null) return;

        //Could be made more efficient by first checking that the property to copy exists
        //Not doing that for now in order to keep code less complicated

        //the following will try to copy the property to the origin source, but
        // this is ok as it is prevented in the other propagateProperty method

        //propagate to default source
        propagateProperty(from, null, propertyName, overwriteExisting);

        //propagate to all custom sources
        for (LocaleId loc : getLocales()) {
            propagateProperty(from, loc, propertyName, overwriteExisting);
        }
    }

    @Override
    public void propagateProperty(LocaleId from, LocaleId to, String propertyName, boolean overwriteExisting) {
        if (sources == null) return;
        if (from == null ? from == to : from.equals(to)) return; //compare from & to without NullPointerException
        Property p = getProperty(from, propertyName);
        if (p != null)
            if ( overwriteExisting || !hasProperty(to, propertyName) )
                setProperty(to, p);
    }

    @Override
    public void propagateAllProperties(LocaleId from, boolean overwriteExisting) {
        if (sources == null) return;

        //Could be made more efficient as property names for the 'from' locale
        // are retrieved on each call of the overloaded propagateAllProperties()
        // function, but leaving it for the moment to keep code simple

        //propagate to default source
        propagateAllProperties(from, null, overwriteExisting);

        //propagate to all custom sources
        for (LocaleId loc : getLocales()) {
            propagateAllProperties(from, loc, overwriteExisting);
        }
    }

    @Override
    public void propagateAllProperties(LocaleId from, LocaleId to, boolean overwriteExisting) {
        if (sources == null) return;
        if (from == null ? from == to : from.equals(to)) return;
        //get a list of all properties from from
        for (String propName : getPropertyNames(from)) {
            //for each, propagate it to to
            propagateProperty(from, to, propName, overwriteExisting);
        }
    }


    @Override
    public <A extends IAnnotation> void propagateAnnotation(LocaleId from, Class<A> type, boolean overwriteExisting) {
        if (sources == null) return;

        //propagate to default source
        propagateAnnotation(from, null, type, overwriteExisting);

        //propagate to all custom sources
        for (LocaleId loc : getLocales()) {
            propagateAnnotation(from, loc, type, overwriteExisting);
        }
    }

    @Override
    public <A extends IAnnotation> void propagateAnnotation(LocaleId from, LocaleId to, Class<A> type, boolean overwriteExisting) {
        if (sources == null) return;
        if (from == null ? from == to : from.equals(to)) return;

        IAnnotation fromA = get(from).getAnnotation(type);
        TextContainer toSource = get(to);
        if (fromA != null) {
            if (overwriteExisting || ( toSource.getAnnotation(type) == null ) ) {
                toSource.setAnnotation(fromA);
            }
        }
    }

    @Override
    public void propagateAllAnnotations(LocaleId from, boolean overwriteExisting) {
        if (sources == null) return;

        //propagate to default source
        propagateAllAnnotations(from, null, overwriteExisting);

        //propagate to all custom sources
        for (LocaleId loc : getLocales()) {
            propagateAllAnnotations(from, loc, overwriteExisting);
        }
    }

    @Override
    public void propagateAllAnnotations(LocaleId from, LocaleId to, boolean overwriteExisting) {
        if (sources == null) return;
        if (from == null ? from == to : from.equals(to)) return;

        for (IAnnotation annot : get(from).getAnnotations()) {
            //TODO check that getClass() will always return the correct class for looking up the annotation
            propagateAnnotation(from, to, annot.getClass(), overwriteExisting);
        }
    }

    @Override
    public Iterator<TextContainer> iterator() {
        //Anonymous class adds the parent source as the first source in the iterator
        return new Iterator<TextContainer>() {
            private boolean removedParentContainer = false;
            private Iterator<TextContainer> iter = sources.values().iterator();

            @Override
            public boolean hasNext() {
                return (removedParentContainer) ? iter.hasNext() : true;
            }

            @Override
            public TextContainer next() {
                return (removedParentContainer) ? iter.next() : defaultSource;
            }

            @Override
            public void remove() {
                if (!removedParentContainer) removedParentContainer = true;
                else iter.remove();
            }
        };
    }

}
