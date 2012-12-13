package net.sf.okapi.common;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import static org.junit.Assert.assertEquals;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Class to hold test utility methods
 * @author Christian Hargraves
 *
 */
public class TestUtil {
	private final static Pattern STARTING_TAG = Pattern.compile("<(\\w+ [^>]+?)(/?)>");
	private final static Pattern ATTRIBUTES = Pattern.compile("\\w+?=\".*?\"");

    /**
     * Takes a class and a file path and returns you the parent directory name of that file. This is used
     * for getting the directory from a file which is in the classpath.
     * @param clazz - The class to use for classpath loading. Don't forget the resource might be a jar file where this
     * class exists.
     * @param filepath - the location of the file. For example &quot;/testFile.txt&quot; would be loaded from the root
     * of the classpath.
     * @return The path of directory which contains the file
     */
	@SuppressWarnings("rawtypes")
	public static String getParentDir(Class clazz, String filepath) {
        URL url = clazz.getResource(filepath);
        String parentDir = null;
        if (url != null) {
			try {
				parentDir = Util.getDirectoryName(url.toURI().getPath()) + "/";
			} catch (URISyntaxException e) {
				return null;
			}
        }
        return parentDir;
    }
	
    public static String getFileAsString(final File file) throws IOException {
        final BufferedInputStream bis = new BufferedInputStream(
            new FileInputStream(file));
        final byte [] bytes = new byte[(int) file.length()];
        bis.read(bytes);
        bis.close();
        return new String(bytes);
    }

	public static void assertEquivalentXml(String expected, String actual) {
		expected = normalize(expected);
		actual = normalize(actual);
		assertEquals(expected, actual);
	}

	private static String normalize(String str) {
		String result = str;
		Matcher startTagMatcher = STARTING_TAG.matcher(str);

		while (startTagMatcher.find()) {
			String tagContent = startTagMatcher.group(1);
			String suffix = startTagMatcher.group(2);
			String newTagContent = orderAttributes(tagContent);
			newTagContent += suffix;

			String before = result.substring(0, startTagMatcher.start());
			String after = result.substring(startTagMatcher.end(), result.length());
			result = before + "<" + newTagContent + ">" + after;
		}
		return result;
	}

	private static String orderAttributes(String tagContent) {
		String tagContentWithOrderedAttributes = "";
		Matcher attributeMatcher = ATTRIBUTES.matcher(tagContent);
		List<String> attributeDeclarations = new ArrayList<String>();
		boolean firstMatch = true;
		while (attributeMatcher.find()) {
			if (firstMatch) {
				tagContentWithOrderedAttributes += tagContent.substring(0, attributeMatcher.start());
				tagContentWithOrderedAttributes = tagContentWithOrderedAttributes.trim();
				firstMatch = false;
			}
			String attDeclaration = attributeMatcher.group();
			attributeDeclarations.add(attDeclaration);
		}
		Collections.sort(attributeDeclarations);
		for (String attributeDeclaration : attributeDeclarations) {
			tagContentWithOrderedAttributes += " " + attributeDeclaration;
		}
		return tagContentWithOrderedAttributes;
	}
}
