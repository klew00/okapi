package net.sf.okapi.tm.pensieve;

import org.junit.Assert;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * User: Christian Hargraves
 * Date: Sep 4, 2009
 * Time: 4:25:11 PM
 */
public class Helper {

    /*
     * Invoke private constructor by reflection purely for code-coverage 
     */
    public static Object genericTestConstructor(final Class<?> cls) throws InstantiationException,
            IllegalAccessException, InvocationTargetException {
        
        final Constructor<?> c = cls.getDeclaredConstructors()[0];
        c.setAccessible(true);
        final Object n = c.newInstance((Object[])null);
        Assert.assertNotNull(n);
        return n;
    }
}
