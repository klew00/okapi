package net.sf.okapi.common;

import java.io.FileNotFoundException;
import java.io.File;
import java.net.URL;

/**
 * User: Christian Hargraves
 * Date: Aug 13, 2009
 * Time: 8:11:05 AM
 */
public class TestUtil {


    public static String getParentDir(Class clazz, String filepath) throws FileNotFoundException {
        URL url = clazz.getResource(filepath);
        if (url == null) {
            throw new FileNotFoundException(filepath + " not found!");
        }
        return Util.getDirectoryName(url.getPath()) + File.separator;
    }
}
