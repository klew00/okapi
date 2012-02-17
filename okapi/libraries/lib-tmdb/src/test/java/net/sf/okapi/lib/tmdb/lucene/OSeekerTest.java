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
package net.sf.okapi.lib.tmdb.lucene;

import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.resource.TextFragment;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
//import static org.mockito.Mockito.*;

import java.io.IOException;
import java.util.List;

/**
 * User: Christian Hargraves
 * Date: Aug 17, 2009
 * Time: 1:04:24 PM
 * 
 * @author HARGRAVEJE
 */
public class OSeekerTest  {

	
    //static final Directory DIR = new RAMDirectory();
	Directory DIR;
	
    static final OTranslationUnitVariant TARGET = new OTranslationUnitVariant(LocaleId.fromString("FR"), new TextFragment("target text"));
    static final String STR = "watch out for the killer rabbit";
    OSeeker seeker;
    List<OTmHit> tmhits;

    @Before
    public void setUp() throws IOException {
    	DIR = new RAMDirectory();
    	//DIR = FSDirectory.open(new File("C:\\PENSIEVE"));
        seeker = new OSeeker(DIR);
        
    }

    @After
    public void tearDown() {
        seeker.close();
    }

    @Test
    public void shortEntries () throws Exception {
        OWriter writer = getWriter();
        OTranslationUnitInput inputTu = new OTranslationUnitInput("1");
        inputTu.add(new OTranslationUnitVariant(LocaleId.fromString("EN"), new TextFragment("abcd")));
        inputTu.add(new OTranslationUnitVariant(LocaleId.fromString("FR"), new TextFragment("efgh")));
        inputTu.add(new OTranslationUnitVariant(LocaleId.fromString("ES"), new TextFragment("ijkl")));
        
        inputTu.setField(new OField("category", "first"));
        
        writer.index(inputTu);
        
        inputTu = new OTranslationUnitInput("2");
        inputTu.add(new OTranslationUnitVariant(LocaleId.fromString("EN"), new TextFragment("abc")));
        inputTu.add(new OTranslationUnitVariant(LocaleId.fromString("FR"), new TextFragment("def")));
        inputTu.add(new OTranslationUnitVariant(LocaleId.fromString("ES"), new TextFragment("ghi")));
        writer.index(inputTu);
        
        inputTu = new OTranslationUnitInput("3");
        inputTu.add(new OTranslationUnitVariant(LocaleId.fromString("EN"), new TextFragment("am")));
        inputTu.add(new OTranslationUnitVariant(LocaleId.fromString("FR"), new TextFragment("bm")));
        inputTu.add(new OTranslationUnitVariant(LocaleId.fromString("ES"), new TextFragment("cm")));
        writer.index(inputTu);
        
        inputTu = new OTranslationUnitInput("4");
        inputTu.add(new OTranslationUnitVariant(LocaleId.fromString("EN"), new TextFragment("zq")));
        inputTu.add(new OTranslationUnitVariant(LocaleId.fromString("FR"), new TextFragment("zr")));
        inputTu.add(new OTranslationUnitVariant(LocaleId.fromString("ES"), new TextFragment("zs")));
        writer.index(inputTu);
        
        inputTu = new OTranslationUnitInput("5");
        inputTu.add(new OTranslationUnitVariant(LocaleId.fromString("EN"), new TextFragment("zqq")));
        inputTu.add(new OTranslationUnitVariant(LocaleId.fromString("FR"), new TextFragment("zrr")));
        inputTu.add(new OTranslationUnitVariant(LocaleId.fromString("ES"), new TextFragment("zss")));
        writer.index(inputTu);
        
        writer.close();
        
        List<OTmHit> list;
        
        //include existing category
        OFields searchfields = new OFields();
        searchfields.put("category", new OField("category", "first"));
        list = seeker.searchFuzzy(new TextFragment("abcd"), 100, 1, searchfields, LocaleId.fromString("EN"));
        assertEquals("number of docs found", 1, list.size());        

        OTmHit hit = list.get(0);
        assertEquals("id name", "segKey", hit.getTu().getIdName());
        assertEquals("id value", "1", hit.getTu().getIdValue());
        assertEquals("number of additional fields", 1, hit.getTu().getFields().size());

        //include missing category
        searchfields = new OFields();
        searchfields.put("category", new OField("category", "second"));
        list = seeker.searchFuzzy(new TextFragment("abcd"), 100, 1, searchfields, LocaleId.fromString("EN"));
        assertEquals("number of docs found", 0, list.size());
        
        list = seeker.searchFuzzy(new TextFragment("abcd"), 100, 1, null, LocaleId.fromString("EN"));
        assertEquals("number of docs found", 1, list.size());
        list = seeker.searchFuzzy(new TextFragment("efgh"), 100, 1, null, LocaleId.fromString("FR"));
        assertEquals("number of docs found", 1, list.size());
        list = seeker.searchFuzzy(new TextFragment("ijkl"), 100, 1, null, LocaleId.fromString("ES"));
        assertEquals("number of docs found", 1, list.size());
        
        list = seeker.searchFuzzy(new TextFragment("abc"), 100, 1, null, LocaleId.fromString("EN"));
        assertEquals("number of docs found", 1, list.size());
        list = seeker.searchFuzzy(new TextFragment("def"), 100, 1, null, LocaleId.fromString("FR"));
        assertEquals("number of docs found", 1, list.size());
        list = seeker.searchFuzzy(new TextFragment("ghi"), 100, 1, null, LocaleId.fromString("ES"));
        assertEquals("number of docs found", 1, list.size());
        
        list = seeker.searchFuzzy(new TextFragment("zqq"), 100, 1, null, LocaleId.fromString("EN"));
        assertEquals("number of docs found", 1, list.size());
        list = seeker.searchFuzzy(new TextFragment("zrr"), 100, 1, null, LocaleId.fromString("FR"));
        assertEquals("number of docs found", 1, list.size());
        list = seeker.searchFuzzy(new TextFragment("zss"), 100, 1, null, LocaleId.fromString("ES"));
        assertEquals("number of docs found", 1, list.size());

        list = seeker.searchFuzzy(new TextFragment("am"), 100, 1, null, LocaleId.fromString("EN"));
        assertEquals("number of docs found", 1, list.size());
        list = seeker.searchFuzzy(new TextFragment("bm"), 100, 1, null, LocaleId.fromString("FR"));
        assertEquals("number of docs found", 1, list.size());
        list = seeker.searchFuzzy(new TextFragment("cm"), 100, 1, null, LocaleId.fromString("ES"));
        assertEquals("number of docs found", 1, list.size());
        
        list = seeker.searchFuzzy(new TextFragment("zq"), 100, 1, null, LocaleId.fromString("EN"));
        assertEquals("number of docs found", 1, list.size());
        list = seeker.searchFuzzy(new TextFragment("zr"), 100, 1, null, LocaleId.fromString("FR"));
        assertEquals("number of docs found", 1, list.size());
        list = seeker.searchFuzzy(new TextFragment("zs"), 100, 1, null, LocaleId.fromString("ES"));
        assertEquals("number of docs found", 1, list.size());
    }

    @Test
    public void penaltyDifferentSpaces () throws Exception {
        OWriter writer = getWriter();
        OTranslationUnitInput inputTu = new OTranslationUnitInput("1");
        
        inputTu.add(new OTranslationUnitVariant(LocaleId.fromString("EN"), new TextFragment("abcdef")));
        inputTu.add(new OTranslationUnitVariant(LocaleId.fromString("FR"), new TextFragment("ghijkl")));
        inputTu.add(new OTranslationUnitVariant(LocaleId.fromString("ES"), new TextFragment("mnopqr")));
        writer.index(inputTu);
        writer.close();

        List<OTmHit> list;
        list = seeker.searchFuzzy(new TextFragment("abCdef"), 100, 1, null, LocaleId.fromString("EN"));
        assertEquals("number of docs found", 0, list.size());
        list = seeker.searchFuzzy(new TextFragment("ghIjkl"), 100, 1, null, LocaleId.fromString("FR"));
        assertEquals("number of docs found", 0, list.size());
        list = seeker.searchFuzzy(new TextFragment("mnOpqr"), 100, 1, null, LocaleId.fromString("ES"));
        assertEquals("number of docs found", 0, list.size());
    }


    OWriter getWriter() throws Exception {
        return new OWriter(DIR, true);
    }
}
