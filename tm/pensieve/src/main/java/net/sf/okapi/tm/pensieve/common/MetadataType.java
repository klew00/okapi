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

import java.util.HashMap;
import java.util.Map;
import org.apache.lucene.document.Field;

/**
 * The types of metadata that is supported. Currently all properties use the same store and indexTypes
 * @author HaslamJD
 */
public enum MetadataType {
    //TODO move ID which should be required to the TranslationUnitField enum
    ID("tuid"),
    TYPE("datatype"),
    GROUP_NAME("Txt::GroupName"),
    FILE_NAME("Txt::FileName");

    private String fieldName;

    private static Map<String, MetadataType> mapping = new HashMap<String, MetadataType>() {
        {
            put("Txt::GroupName", GROUP_NAME);
            put("Txt::FileName", FILE_NAME);
            put("datatype", TYPE);
            put("tuid", ID);
        }
    };

    private MetadataType(String fieldName) {
        this.fieldName = fieldName;
    }

    public String fieldName() {
        return fieldName;
    }

    public Field.Store store() {
        return Field.Store.YES;
    }

    public Field.Index indexType() {
        return Field.Index.NOT_ANALYZED;
    }

    public static MetadataType findMetadataType(String keyword) {
        return mapping.get(keyword);
    }
}


