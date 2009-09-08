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

import net.sf.okapi.common.resource.TextFragment;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 * User: Christian Hargraves
 * Date: Aug 19, 2009
 * Time: 6:49:59 AM
 */
public class TranslationUnitTest {

    TranslationUnit tu;
    final static TranslationUnitVariant SOURCE = new TranslationUnitVariant("EN", new TextFragment("Joe McMac"));
    final static TranslationUnitVariant CONTENT = new TranslationUnitVariant("EN", new TextFragment("Some content that isn't very long"));

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
        MetaData md = new MetaData();
        tu.setMetadata(md);
        assertSame("metadata should be the same", md, tu.getMetadata());
    }

    @Test
    public void isSourceEmptyNull(){
        tu = new TranslationUnit();
        assertTrue("source should be empty", tu.isSourceEmpty());
    }

    @Test
    public void isSourceEmptyEmpty(){
        tu = new TranslationUnit();
        tu.setSource(new TranslationUnitVariant("EN", new TextFragment("")));
        assertTrue("source should be empty", tu.isSourceEmpty());
    }

    @Test
    public void isSourceEmptyNotEmpty(){
        tu = new TranslationUnit();
        tu.setSource(new TranslationUnitVariant("EN", new TextFragment("this is not empty")));
        assertFalse("source should not be empty", tu.isSourceEmpty());
    }

    @Test
    public void isTargetEmptyNull(){
        tu = new TranslationUnit();
        assertTrue("target should be empty", tu.isTargetEmpty());
    }

    @Test
    public void isTargetEmptyEmpty(){
        tu = new TranslationUnit();
        tu.setTarget(new TranslationUnitVariant("EN", new TextFragment("")));
        assertTrue("target should be empty", tu.isTargetEmpty());
    }

    @Test
    public void isTargetEmptyNotEmpty(){
        tu = new TranslationUnit();
        tu.setTarget(new TranslationUnitVariant("EN", new TextFragment("this is not empty")));
        assertFalse("target should not be empty", tu.isTargetEmpty());
    }
}
