/*===========================================================================
  Copyright (C) 2008-2011 by the Okapi Framework contributors
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
import net.sf.okapi.common.resource.ITextUnit;
import net.sf.okapi.common.resource.Property;
import net.sf.okapi.common.resource.TextFragment;
import net.sf.okapi.common.resource.TextUnit;
import net.sf.okapi.tm.pensieve.Helper;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class PensieveUtilTest {

    @Test
    public void stupidTestOnlyForCoverage() throws Exception {
        Helper.genericTestConstructor(PensieveUtil.class);
    }

    @Test
    public void convertToTranslationUnitMetadata(){
        ITextUnit textUnit = new TextUnit("someId", "some great text");
        textUnit.setTargetContent(LocaleId.fromString("kr"), new TextFragment("some great text in Korean"));
        textUnit.setProperty(new Property(MetadataType.FILE_NAME.fieldName(), "sumdumfilename"));
        TranslationUnit tu = PensieveUtil.convertToTranslationUnit(LocaleId.fromString("en"), LocaleId.fromString("kr"), textUnit);
        assertEquals("# of meetadaatas", 1, tu.getMetadata().size());
        assertEquals("file name meta data", "sumdumfilename", tu.getMetadataValue(MetadataType.FILE_NAME));
    }

    @Test
    public void convertToTranslationUnitNonMatchingProperty(){
        ITextUnit textUnit = new TextUnit("someId", "some great text");
        textUnit.setTargetContent(LocaleId.fromString("kr"), new TextFragment("some great text in Korean"));
        textUnit.setProperty(new Property(MetadataType.FILE_NAME.fieldName(), "sumdumfilename"));
        textUnit.setProperty(new Property("somedumbkey", "sumdumvalue"));
        TranslationUnit tu = PensieveUtil.convertToTranslationUnit(LocaleId.fromString("en"), LocaleId.fromString("kr"), textUnit);
        assertEquals("# of meetadaatas", 1, tu.getMetadata().size());
    }

    @Test
    public void convertToTranslationUnit(){
        ITextUnit textUnit = new TextUnit("someId", "some great text");
        textUnit.setTargetContent(LocaleId.fromString("kr"), new TextFragment("some great text in Korean"));
        TranslationUnit tu = PensieveUtil.convertToTranslationUnit(LocaleId.fromString("en"), LocaleId.fromString("kr"), textUnit);
        assertEquals("sourceLang", "en", tu.getSource().getLanguage().toString());
        assertEquals("source content", "some great text", tu.getSource().getContent().toText());
        assertEquals("targetLang", "kr", tu.getTarget().getLanguage().toString());
        assertEquals("target content", "some great text in Korean", tu.getTarget().getContent().toText());
    }

    @Test
    public void convertToTextUnitNullId(){
        TranslationUnit tu = Helper.createTU(LocaleId.fromString("EN"), LocaleId.fromString("KR"), "bipity bopity boo", "something in korean", null);
        tu.setMetadataValue(MetadataType.GROUP_NAME, "groupie");
        ITextUnit textUnit = PensieveUtil.convertToTextUnit(tu);
        assertEquals("source content", "bipity bopity boo", textUnit.getSource().getFirstContent().toText());
        assertEquals("target content", "something in korean",
        	textUnit.getTarget(LocaleId.fromString("KR")).getFirstContent().toText());
        assertEquals("tuid", null, textUnit.getId());
        assertEquals("name", null, textUnit.getName());
        assertEquals("group attribute", "groupie", textUnit.getProperty(MetadataType.GROUP_NAME.fieldName()).getValue());
    }

    @Test
    public void convertToTextUnit(){
        TranslationUnit tu = Helper.createTU(LocaleId.fromString("EN"), LocaleId.fromString("KR"), "bipity bopity boo", "something in korean", "1");
        tu.setMetadataValue(MetadataType.GROUP_NAME, "groupie");
        ITextUnit textUnit = PensieveUtil.convertToTextUnit(tu);
        assertEquals("source content", "bipity bopity boo", textUnit.getSource().getFirstContent().toText());
        assertEquals("target content", "something in korean",
        	textUnit.getTarget(LocaleId.fromString("KR")).getFirstContent().toText());
        assertEquals("tuid", "1", textUnit.getId());
        assertEquals("name", "1", textUnit.getName());
        assertEquals("group attribute", "groupie", textUnit.getProperty(MetadataType.GROUP_NAME.fieldName()).getValue());
    }

}
