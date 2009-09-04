/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.sf.okapi.tm.pensieve.common;

import org.junit.Test;
import static org.junit.Assert.*;
import net.sf.okapi.tm.pensieve.Helper;

/**
 *
 * @author HaslamJD
 */
public class MatchScorerTest {

    @Test
    public void stupidTestOnlyForCoverage() throws Exception {
        Helper.genericTestConstructor(MatchScorer.class);
    }

    @Test
    public void scoreExactMatch() {
        assertEquals("exact match score", 100.0, MatchScorer.scoreMatch("DOG", "DOG"), 0.0);
    }

    @Test
    public void scoreSameLengthEachDifferent() {
        assertEquals("exact match score", 3.0, MatchScorer.scoreMatch("DOG", "CAT"), 0.0);
    }

    @Test
    public void scoreOneEmpty() {
        assertEquals("exact match score", 3.0, MatchScorer.scoreMatch("", "CAT"), 0.0);
    }

    @Test
    public void scoreOtherEmpty() {
        assertEquals("exact match score", 3.0, MatchScorer.scoreMatch("DOG", ""), 0.0);
    }
}
