package net.sf.okapi.common;

import java.io.File;
import java.net.URL;

/**
 * Class to hold test utility methods
 * @author Christian Hargraves
 *
 */
public class TestUtil {

    /**
     * Takes a class and a file path and returns you the parent directory name of that file. This is used
     * for getting the directory from a file which is in the classpath.
     * @param clazz - The class to use for classpath loading. Don't forget the resource might be a jar file where this
     * class exists.
     * @param filepath - the location of the file. For example &quot;/testFile.txt&quot; would be loaded from the root
     * of the classpath.
     * @return The path of directory which contains the file
     */
    public static String getParentDir(Class clazz, String filepath) {
        URL url = clazz.getResource(filepath);
        String parentDir = null;
        if (url != null) {
            parentDir = Util.getDirectoryName(url.getPath()) + File.separator;
        }
        return parentDir;
    }
}
