/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.sf.okapi.tm.pensieve.common;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author HaslamJD
 */
public class MetaDataTest {

    @Test
    public void noArgConstructor() {
        MetaData md = new MetaData();
        assertEquals("metadata entries", 0, md.size());
    }

    @Test
    public void keysAndValues() {
        MetaData md = new MetaData();
        String id = "MetaData - pronounced may-da-day-da";
        md.put(MetaDataTypes.ID, id);
        assertEquals("metadata entries", id, md.get(MetaDataTypes.ID));
    }
}
