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
    TextContainer parentSource; //points to source in parent ITextUnit
    private int sourceCount;
    private ITextUnit myParent;



    public VariantSources(ITextUnit parent) {
        create(parent, null);
    }

    public VariantSources(ITextUnit parent, VariantSources original) {
        create(parent, original);
    }

    public void setDefaultSource(TextContainer sourceContainer) {
        this.parentSource = sourceContainer;
    }

    private void create(ITextUnit parent, VariantSources original) {
        myParent = parent;
        this.parentSource = parent.getSource();
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
        VariantSources cs = new VariantSources(myParent);
        cs.sources = new ConcurrentHashMap<LocaleId, TextContainer>();
        cs.sources.putAll(this.sources);

        return cs;
    }

    @Override
    public boolean isSourceEmpty(LocaleId targetLocale) {
        return getSource(targetLocale).isEmpty();
    }

    @Override
    public TextContainer createSource(LocaleId targetLocale, boolean overwriteExisting, int creationOptions) {
        TextContainer newSource = parentSource.clone((creationOptions & COPY_PROPERTIES) == COPY_PROPERTIES);
        if ( (creationOptions & COPY_SEGMENTS) != COPY_SEGMENTS )
            newSource.joinAll();
        if ( (creationOptions & COPY_CONTENT) != COPY_CONTENT )
            for (Segment seg : newSource.getSegments())
                seg.text.clear();
        return createSource(parentSource.clone(), targetLocale, overwriteExisting);
    }

    @Override
    public TextContainer createSource(TextContainer sourceText, LocaleId targetLocale, boolean overwriteExisting) {
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
    public TextContainer getSource(LocaleId targetLocale) {
        TextContainer theSource = null;
        if (sourceCount > 0 && targetLocale != null)
            if (sources.containsKey(targetLocale))
                theSource = sources.get(targetLocale);
        return ((theSource != null) ? theSource : parentSource);
    }

    @Override
    public TextContainer setSource(LocaleId targetLocale, TextContainer textContainer) throws IllegalArgumentException {
        if (targetLocale == null) throw new IllegalArgumentException("The target locale must not be null");
        return putSource(targetLocale, textContainer);
    }

    /* Adds or replaces a custom source for the given locale, creating a hash of
     * sources if required and incrementing the count if it is changed.
     *
     * @param targetLocale the target locale that uses the source
     * @param theSource the source to be put in the hash
     * @return the source that was put in the list
     */
    private TextContainer putSource(LocaleId targetLocale, TextContainer theSource) {
        if (sources == null) sources = new ConcurrentHashMap<LocaleId, TextContainer>(SOURCES_INITCAP);
        //can rely on a null meaning that there was no custom source because
        // ConcurrentHashMap cannot contain null values
        if (sources.put(targetLocale, theSource) == null) sourceCount++;
        return sources.get(targetLocale);
    }

    @Override
    public void removeSource(LocaleId targetLocale) throws IllegalArgumentException {
        if (targetLocale == null) throw new IllegalArgumentException("The target locale must not be null");
        if (sources == null) return;
        if (sources.remove(targetLocale) != null) sourceCount--;
    }

    @Override
    public boolean hasCustomSource(LocaleId targetLocale) throws IllegalArgumentException {
        if (targetLocale == null) throw new IllegalArgumentException("The target locale must not be null");
        return (sources == null) ? false : sources.containsKey(targetLocale);
    }

    @Override
    public int count() {
        return sourceCount;
    }

    @Override
    public boolean empty() {
        return (sourceCount == 0);
    }

    @Override
    public TextFragment setSourceContent(LocaleId targetLocale, TextFragment content) throws IllegalArgumentException {
        if (targetLocale == null) throw new IllegalArgumentException("targetLocale should not be null");

        TextContainer theSource;
        if (sources == null) sources = new ConcurrentHashMap<LocaleId, TextContainer>(SOURCES_INITCAP);
        if (sources.containsKey(targetLocale)) {
            theSource = getSource(targetLocale);
            theSource.setContent(content);
        } else {
            theSource = putSource(targetLocale, new TextContainer(content));
        }
        // We can use this because the setContent() removed any segmentation
        return theSource.getSegments().getFirstContent();
    }

    //TODO look at making IAlignedSegments use an action listener model so that
    // if associated source/target are removed it can respond appropriately
    @Override
    public IAlignedSegments getSegments(LocaleId loc) {
        return new AlignedSegments(myParent, loc);
    }

    @Override
    public ISegments getSourceSegments(LocaleId targetLocale) {
        return getSource(targetLocale).getSegments();
    }

    @Override
    public Segment getSourceSegment(LocaleId targetLocale, String segId, boolean createIfNeeded) {
        Segment seg = getSourceSegments(targetLocale).get(segId);
        if (( seg == null ) && createIfNeeded ) {
            seg = new Segment(segId);
            getSource(targetLocale).getSegments().append(seg);
        }
        return seg;
    }

    @Override
    public Set<LocaleId> getTargetLocalesWithCustomSource() {
        return (sources == null) ? new HashSet<LocaleId>() : sources.keySet();
    }

    @Override
    public Property getSourceProperty(LocaleId targetLocale, String name) {
        return getSource(targetLocale).getProperty(name);
    }

    @Override
    public Property setSourceProperty(LocaleId targetLocale, Property property) {
        return getSource(targetLocale).setProperty(property);
    }

    @Override
    public void removeSourceProperty(LocaleId targetLocale, String name) {
        getSource(targetLocale).removeProperty(name);
    }

    @Override
    public Set<String> getSourcePropertyNames(LocaleId targetLocale) {
        return getSource(targetLocale).getPropertyNames();
    }

    @Override
    public boolean hasSourceProperty(LocaleId targetLocale, String name) {
        return getSource(targetLocale).hasProperty(name);
    }


    @Override
    public void propagateSourceProperty(LocaleId from, String propertyName, boolean overwriteExisting) {
        if (sources == null) return;

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
        if (sources == null) return;
        if (from == null ? from == to : from.equals(to)) return; //compare from & to without NullPointerException
        Property p = getSourceProperty(from, propertyName);
        if (p != null)
            if ( overwriteExisting || !hasSourceProperty(to, propertyName) )
                setSourceProperty(to, p);
    }

    @Override
    public void propagateAllSourceProperties(LocaleId from, boolean overwriteExisting) {
        if (sources == null) return;

        //Could be made more efficient as property names for the 'from' locale
        // are retrieved on each call of the overloaded propagateAllSourceProperties()
        // function, but leaving it for the moment to keep code simple

        //propagate to default source
        propagateAllSourceProperties(from, null, overwriteExisting);

        //propagate to all custom sources
        for (LocaleId loc : getTargetLocalesWithCustomSource()) {
            propagateAllSourceProperties(from, loc, overwriteExisting);
        }
    }

    @Override
    public void propagateAllSourceProperties(LocaleId from, LocaleId to, boolean overwriteExisting) {
        if (sources == null) return;
        if (from == null ? from == to : from.equals(to)) return;
        //get a list of all properties from from
        for (String propName : getSourcePropertyNames(from)) {
            //for each, propagate it to to
            propagateSourceProperty(from, to, propName, overwriteExisting);
        }
    }


    @Override
    public <A extends IAnnotation> void propagateSourceAnnotation(LocaleId from, Class<A> type, boolean overwriteExisting) {
        if (sources == null) return;

        //propagate to default source
        propagateSourceAnnotation(from, null, type, overwriteExisting);

        //propagate to all custom sources
        for (LocaleId loc : getTargetLocalesWithCustomSource()) {
            propagateSourceAnnotation(from, loc, type, overwriteExisting);
        }
    }

    @Override
    public <A extends IAnnotation> void propagateSourceAnnotation(LocaleId from, LocaleId to, Class<A> type, boolean overwriteExisting) {
        if (sources == null) return;
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
        if (sources == null) return;

        //propagate to default source
        propagateAllSourceAnnotations(from, null, overwriteExisting);

        //propagate to all custom sources
        for (LocaleId loc : getTargetLocalesWithCustomSource()) {
            propagateAllSourceAnnotations(from, loc, overwriteExisting);
        }
    }

    @Override
    public void propagateAllSourceAnnotations(LocaleId from, LocaleId to, boolean overwriteExisting) {
        if (sources == null) return;
        if (from == null ? from == to : from.equals(to)) return;

        for (IAnnotation annot : getSource(from).getAnnotations()) {
            //TODO check that getClass() will always return the correct class for looking up the annotation
            propagateSourceAnnotation(from, to, annot.getClass(), overwriteExisting);
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
                return (removedParentContainer) ? iter.next() : parentSource;
            }

            @Override
            public void remove() {
                if (!removedParentContainer) removedParentContainer = true;
                else iter.remove();
            }
        };
    }

}
