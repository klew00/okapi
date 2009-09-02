/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.sf.okapi.tm.pensieve.common;

import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author HaslamJD
 */
public class TMHitTest {

    @Test
    public void noArgConstructor() {
        TMHit tmh = new TMHit();
        assertNull(tmh.getTu());
        assertNull(tmh.getScore());
    }
}
