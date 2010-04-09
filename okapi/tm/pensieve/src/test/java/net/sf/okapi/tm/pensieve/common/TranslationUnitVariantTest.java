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
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author HaslamJD
 */
public class TranslationUnitVariantTest {

    private final TextFragment content = new TextFragment();
    private final LocaleId locale = LocaleId.fromString("fb");

    @Test
    public void constructorNoArg() {
        TranslationUnitVariant tuv = new TranslationUnitVariant();
        assertNull("text fragment should be null", tuv.getContent());
        assertNull("lang should be null", tuv.getLanguage());
    }

    @Test
    public void constructorTwoArgs() {
        TranslationUnitVariant tuv = new TranslationUnitVariant(locale, content);
        assertSame("content", content, tuv.getContent());
        assertSame("lang", locale, tuv.getLanguage());
    }

    @Test
    public void setContent() {
        TranslationUnitVariant tuv = new TranslationUnitVariant();
        tuv.setContent(content);
        assertSame("content", content, tuv.getContent());
    }

    @Test
    public void setLang() {
        TranslationUnitVariant tuv = new TranslationUnitVariant();
        tuv.setLocale(locale);
        assertSame("content", locale, tuv.getLanguage());
    }
}
