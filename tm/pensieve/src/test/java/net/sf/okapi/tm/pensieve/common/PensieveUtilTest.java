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
import net.sf.okapi.common.resource.TextUnit;
import net.sf.okapi.tm.pensieve.Helper;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 * User: Christian Hargraves
 * Date: Sep 4, 2009
 * Time: 4:12:34 PM
 */
public class PensieveUtilTest {

    @Test
    public void stupidTestOnlyForCoverage() throws Exception {
        Helper.genericTestConstructor(PensieveUtil.class);
    }

    @Test
    public void convertTranslationUnit(){
        TextUnit textUnit = new TextUnit("someId", "some great text");
        textUnit.setTargetContent("kr", new TextFragment("some great text in Korean"));
        TranslationUnit tu = PensieveUtil.convertTranslationUnit("en", "kr", textUnit);
        assertEquals("sourceLang", "en", tu.getSource().getLang());
        assertEquals("source content", "some great text", tu.getSource().getContent().toString());
        assertEquals("targetLang", "kr", tu.getTarget().getLang());
        assertEquals("target content", "some great text in Korean", tu.getTarget().getContent().toString());
    }

}
