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

package net.sf.okapi.tm.pensieve;

import net.sf.okapi.common.resource.TextFragment;
import net.sf.okapi.tm.pensieve.common.MetadataType;
import net.sf.okapi.tm.pensieve.common.TranslationUnit;
import net.sf.okapi.tm.pensieve.common.TranslationUnitVariant;
import org.junit.Assert;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

/**
 * User: Christian Hargraves
 * Date: Sep 4, 2009
 * Time: 4:25:11 PM
 */
public class Helper {

    /*
     * Invoke private constructor by reflection purely for code-coverage 
     */
    public static Object genericTestConstructor(final Class<?> cls) throws Exception {
        //This is going to be the only constructor since it is for testing private constructors ... why have
        //more than one private constructor?
        final Constructor<?> c = cls.getDeclaredConstructors()[0];
        c.setAccessible(true);
        final Object n = c.newInstance((Object[])null);
        Assert.assertNotNull(n);
        return n;
    }

    public static TranslationUnit createTU(String srcLang, String targetLang, String source, String target, String id){
        TranslationUnitVariant tuvS = new TranslationUnitVariant(srcLang, new TextFragment(source));
        TranslationUnitVariant tuvT = new TranslationUnitVariant(targetLang, new TextFragment(target));
        TranslationUnit tu = new TranslationUnit(tuvS, tuvT);
        tu.getMetadata().put(MetadataType.ID, id);
        return tu;
    }

    public static void setPrivateMember(Object instance, String memberName, Object value) throws Exception{
        Field field = instance.getClass().getDeclaredField(memberName);
        field.setAccessible(true);
        field.set(instance, value);
    }
}
