package net.sf.okapi.common;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 * User: Christian Hargraves
 * Date: Aug 13, 2009
 * Time: 8:08:16 AM
 */
public class TestUtilTest {

    @Test
    public void getParentDir_FileNotFound() {
        assertNull("A parent directory for a nonexistent file should be null",
                TestUtil.getParentDir(this.getClass(), "some/nonexistent/file/that/could/no/way/exist.txt"));
    }

    @Test
    public void getParentDir_ValidFile() {
        assertTrue("Incorrect path returned",
                TestUtil.getParentDir(this.getClass(), "/TestUtilTestTestFile.txt").endsWith("test-classes/"));
    }

    @Test
    public void checkXmlIgnoreAttributeOrder() {
		TestUtil.assertEquivalentXml("<ph type=\"deleted\" ID=\"1\">test content</ph>", "<ph ID=\"1\" type=\"deleted\">test content</ph>");
	}
}
