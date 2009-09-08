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
 *
 * @author HaslamJD
 */
public enum MetaDataType {
    ID("tuid", StoreType.YES, IndexType.NOT_ANALYZED),
    NAME("Name", StoreType.YES, IndexType.NOT_ANALYZED),
    TYPE("Type", StoreType.YES, IndexType.NOT_ANALYZED),
    GROUP_NAME("GroupName", StoreType.YES, IndexType.NOT_ANALYZED),
    FILE_NAME("FileName", StoreType.YES, IndexType.NOT_ANALYZED);

    private String fieldName;
    private StoreType store;
    private IndexType indexType;

    private MetaDataType(String fieldName, StoreType store, IndexType indexType){
        this.fieldName = fieldName;
        this.store = store;
        this.indexType = indexType;
    }

    public String fieldName(){
        return fieldName;
    }

    public StoreType store(){
        return store;
    }

    public IndexType indexType(){
        return indexType;
    }
}


