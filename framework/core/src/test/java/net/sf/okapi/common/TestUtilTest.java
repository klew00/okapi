package net.sf.okapi.common;

import org.junit.Test;
import static org.junit.Assert.assertTrue;

import java.io.FileNotFoundException;
import java.io.File;

/**
 * User: Christian Hargraves
 * Date: Aug 13, 2009
 * Time: 8:08:16 AM
 */
public class TestUtilTest {

    @Test(expected = FileNotFoundException.class)
    public void getParentDir_FileNotFound() throws FileNotFoundException {
        TestUtil.getParentDir(this.getClass(), "some/nonexistent/file/that/could/no/way/exist.txt");
    }

    @Test
    public void getParentDir_ValidFile() throws FileNotFoundException {
        assertTrue("Incorrect path returned",
                TestUtil.getParentDir(this.getClass(), "/TestUtilTestTestFile.txt").endsWith("test-classes"+ File.separator));
    }
}
