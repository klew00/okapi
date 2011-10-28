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

import java.util.ArrayList;
import java.util.List;

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
    
    @Test
    public void testClone () {
    	Code code = new Code(TagType.OPENING, "ctype", "data");
    	code.setOuterData("out1");
    	Code c2 = code.clone();
    	assertNotSame(c2, code);
    	assertEquals(c2.getId(), code.getId());
    	assertEquals(c2.getData(), code.getData());
    	assertNotSame(c2.data, code.data);
    	assertEquals(c2.getTagType(), code.getTagType());
    	assertEquals(c2.getType(), code.getType());
    	assertEquals(c2.getOuterData(), code.getOuterData());
    	assertNotSame(c2.outerData, code.outerData);
    }

    @Test
    public void testStrings () {
    	ArrayList<Code> codes = new ArrayList<Code>();
    	codes.add(new Code(TagType.OPENING, "bold", "<b>"));
    	codes.add(new Code(TagType.PLACEHOLDER, "break", "<br/>"));
    	codes.add(new Code(TagType.CLOSING, "bold", "</b>"));
    	String tmp = Code.codesToString(codes);
    	
    	assertNotNull(tmp);
    	List<Code> codesAfter = Code.stringToCodes(tmp);
    	assertEquals(3, codesAfter.size());
    	
    	Code code = codesAfter.get(0);
    	assertEquals("<b>", code.getData());
    	assertEquals(TagType.OPENING, code.getTagType());
    	assertEquals("bold", code.getType());
    	
    	code = codesAfter.get(1);
    	assertEquals("<br/>", code.getData());
    	assertEquals(TagType.PLACEHOLDER, code.getTagType());
    	assertEquals("break", code.getType());
    	
    	code = codesAfter.get(2);
    	assertEquals("</b>", code.getData());
    	assertEquals(TagType.CLOSING, code.getTagType());
    	assertEquals("bold", code.getType());
    }

    @Test
    public void testCodeData () {
    	Code code = new Code(TagType.PLACEHOLDER, "type", null);
    	assertEquals("", code.toString());
    	
    	code = new Code(TagType.PLACEHOLDER, "type", null);
    	code.setOuterData("<x id=\"1\">");
    	assertEquals("", code.toString());
    	code.setOuterData(null);
    	assertEquals("", code.toString());
    }

}
