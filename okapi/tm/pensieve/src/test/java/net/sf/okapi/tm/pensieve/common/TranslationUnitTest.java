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

import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.resource.TextFragment;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

/**
 * User: Christian Hargraves
 * Date: Aug 19, 2009
 * Time: 6:49:59 AM
 */
public class TranslationUnitTest {

    TranslationUnit tu;
    final static TranslationUnitVariant SOURCE = new TranslationUnitVariant(LocaleId.fromString("EN"), new TextFragment("Joe McMac"));
    final static TranslationUnitVariant CONTENT = new TranslationUnitVariant(LocaleId.fromString("EN"), new TextFragment("Some content that isn't very long"));

    @Before
    public void setUp(){
        tu = new TranslationUnit();
    }


    @Test
    public void getMetadataValueNullKey(){
        tu.setMetadataValue(MetadataType.ID, "test");
        assertNull("The ID metadata should not exist", tu.getMetadataValue(null));
    }

    @Test
    public void getMetadataValueKey(){
        tu.setMetadataValue(MetadataType.ID, "test");
        assertEquals("The ID metadata", "test", tu.getMetadataValue(MetadataType.ID));
    }

    @Test
    public void setMetadataValueNull(){
        tu.setMetadataValue(MetadataType.ID, null);
        assertFalse("The ID metadata should not exist", tu.getMetadata().containsKey(MetadataType.ID));
    }

    @Test
    public void setMetadataValueEmpty(){
        tu.setMetadataValue(MetadataType.ID, "");
        assertFalse("The ID metadata should not exist", tu.getMetadata().containsKey(MetadataType.ID));
    }

    @Test
    public void setMetadataValue(){
        tu.setMetadataValue(MetadataType.ID, "yipee");
        assertEquals("The ID metadata", "yipee", tu.getMetadata().get(MetadataType.ID));
    }

    @Test
    public void noArgConstructor(){
        tu = new TranslationUnit();
        assertNull("source", tu.getSource());
        assertNull("content", tu.getTarget());
        assertEquals("metadata entries", 0, tu.getMetadata().size());
    }

    @Test
    public void constructor_allParamsPassed(){
        tu = new TranslationUnit(SOURCE, CONTENT);
        assertEquals("source", SOURCE, tu.getSource());
        assertEquals("content", CONTENT, tu.getTarget());
        assertEquals("metadata entries", 0, tu.getMetadata().size());
    }

    @Test
    public void metaDataSetter(){
        tu = new TranslationUnit(SOURCE, CONTENT);
        Metadata md = new Metadata();
        tu.setMetadata(md);
        assertSame("metadata should be the same", md, tu.getMetadata());
    }

    @Test
    public void isSourceEmptyNull(){
        assertTrue("source should be empty", tu.isSourceEmpty());
    }

    @Test
    public void isSourceEmptyEmpty(){
        tu.setSource(new TranslationUnitVariant(LocaleId.fromString("EN"), new TextFragment("")));
        assertTrue("source should be empty", tu.isSourceEmpty());
    }

    @Test
    public void isSourceEmptyNotEmpty(){
        tu.setSource(new TranslationUnitVariant(LocaleId.fromString("EN"), new TextFragment("this is not empty")));
        assertFalse("source should not be empty", tu.isSourceEmpty());
    }

    @Test
    public void isTargetEmptyNull(){
        assertTrue("target should be empty", tu.isTargetEmpty());
    }

    @Test
    public void isTargetEmptyEmpty(){
        tu.setTarget(new TranslationUnitVariant(LocaleId.fromString("EN"), new TextFragment("")));
        assertTrue("target should be empty", tu.isTargetEmpty());
    }

    @Test
    public void isTargetEmptyNotEmpty(){
        tu.setTarget(new TranslationUnitVariant(LocaleId.fromString("EN"), new TextFragment("this is not empty")));
        assertFalse("target should not be empty", tu.isTargetEmpty());
    }
}
