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

/**
 * User: Christian Hargraves
 * Date: Aug 19, 2009
 * Time: 6:53:34 AM
 */
public class TranslationUnit {
    private TranslationUnitVariant source;
    private TranslationUnitVariant target;
    private Metadata metadata;

    public TranslationUnit(){
        metadata = new Metadata();
    }

    public TranslationUnit(TranslationUnitVariant source, TranslationUnitVariant target) {
        this();
        this.source = source;
        this.target = target;
    }

    public Metadata getMetadata() {
        return metadata;
    }

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

    public boolean isSourceEmpty() {
        return isFragmentEmpty(source);
    }

    public boolean isTargetEmpty() {
        return isFragmentEmpty(target);
    }

    private static boolean isFragmentEmpty(TranslationUnitVariant frag){
        return frag == null || frag.getContent().isEmpty();
    }
}
