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

package net.sf.okapi.common.resource;

import net.sf.okapi.common.resource.TextFragment.TagType;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

public class CodeTest {

    @Before
    public void setUp(){
    }

    @Test
    public void testAccess () {
    	Code code = new Code(TagType.OPENING, "ctype", "data");
    	assertEquals("data", code.getData());
    	assertEquals("ctype", code.getType());
    	assertEquals(TagType.OPENING, code.getTagType());
    	assertEquals("data", code.getOuterData()); // default
    	code.setOuterData("outerData");
    	assertEquals("outerData", code.getOuterData());
    	assertEquals("data", code.getData());
    }

    @Test
    public void testFlags () {
    	Code code = new Code(TagType.OPENING, "ctype", "data");
    	assertFalse(code.isCloneable());
    	assertFalse(code.isDeleteable());
    	assertFalse(code.hasReference());
    	code.setDeleteable(true);
    	code.setCloneable(true);
    	code.setReferenceFlag(true);
    	assertTrue(code.isCloneable());
    	assertTrue(code.isDeleteable());
    	assertTrue(code.hasReference());
    }


}
