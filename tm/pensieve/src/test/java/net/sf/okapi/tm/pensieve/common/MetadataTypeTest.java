package net.sf.okapi.tm.pensieve.common;

import org.junit.Test;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertEquals;

/**
 * User: Christian Hargraves
 * Date: Sep 10, 2009
 * Time: 11:05:12 AM
 */
public class MetadataTypeTest {

    @Test
    public void findMetadataTypeNullName(){
        assertNull("Null value for a null key should be returned", MetadataType.findMetadataType(null));
    }

    @Test
    public void findMetadataTypeNonexistentName(){
        assertNull("Null value for a key that doesn't exist should be returned", MetadataType.findMetadataType("mdkjfdlskdf"));
    }

    @Test
    public void findMetadataType(){
        assertEquals("NAME", MetadataType.GROUP_NAME, MetadataType.findMetadataType("Txt::GroupName"));
    }
}
