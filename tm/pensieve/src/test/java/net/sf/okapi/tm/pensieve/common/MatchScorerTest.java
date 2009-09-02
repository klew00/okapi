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
public class MatchScorerTest {


    @Test
    public void scoreExactMatch()
    {
        assertEquals("exact match score", 0.0, MatchScorer.scoreMatch("DOG", "DOG"), 0.0);
    }

    @Test
    public void scoreSameLengthEachDifferent()
    {
        assertEquals("exact match score", 3.0, MatchScorer.scoreMatch("DOG", "CAT"), 0.0);
    }
}
