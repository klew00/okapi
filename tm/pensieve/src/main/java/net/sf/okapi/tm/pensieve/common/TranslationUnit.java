/*===========================================================================
  Copyright (C) 2008-2009 by the Okapi Framework contributors
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

package net.sf.okapi.tm.pensieve.common;

import net.sf.okapi.common.Util;

/**
 * Represents a Unit of Translation.
 */
public class TranslationUnit {
    private TranslationUnitVariant source;
    private TranslationUnitVariant target;
    private Metadata metadata;

    /**
     * Creates a TU w/o an source or target defined
     */
    public TranslationUnit(){
        metadata = new Metadata();
    }

    /**
     * Creates a TU with the provided source and targets
     * @param source The source of the TU
     * @param target The target of the TU
     */
    public TranslationUnit(TranslationUnitVariant source, TranslationUnitVariant target) {
        this();
        this.source = source;
        this.target = target;
    }

    /**
     * Gets the metadata or attributes for this TU
     * @return The Metadata of this TU
     */
    public Metadata getMetadata() {
        return metadata;
    }
    //TODO: get rid of me
    public void setMetadata(Metadata metadata) {
        this.metadata = metadata;
    }

    public TranslationUnitVariant getSource() {
        return source;
    }

    public TranslationUnitVariant getTarget() {
        return target;
    }

    public void setSource(TranslationUnitVariant source) {
        this.source = source;
    }

    public void setTarget(TranslationUnitVariant target) {
        this.target = target;
    }

    /**
     * Checks to see if the the source is empty
     * @return true if the source is empty
     */
    public boolean isSourceEmpty() {
        return isFragmentEmpty(source);
    }

    /**
     * Sets the value for a give metadata value field
     * @param key the key for the data we want set
     * @param value the vlaue to set the metadata to
     */
    public void setMetadataValue(MetadataType key, String value) {
        if (Util.isEmpty(value)){
            metadata.remove(key);
        }else{
            metadata.put(key, value);
        }
    }

    /**
     * Checks to see if the the target is empty
     * @return true if the target is empty
     */
    public boolean isTargetEmpty() {
        return isFragmentEmpty(target);
    }

    /**
     * Gets the value for a give metadata value field
     * @param key the key for the data we want
     * @return the value for a give metadata value field
     */
    public String getMetadataValue(MetadataType key) {
        return metadata.get(key);
    }

    private static boolean isFragmentEmpty (TranslationUnitVariant frag){
        return (( frag == null ) || frag.getContent().isEmpty() );
    }
}
