/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.sf.okapi.tm.pensieve.common;

/**
 *
 * @author HaslamJD
 */
public class TMHit {

    public TMHit() {

    }

    private TranslationUnit tu;
    private Float score;

    public Float getScore() {
        return score;
    }

    public void setScore(Float score) {
        this.score = score;
    }

    public TranslationUnit getTu() {
        return tu;
    }

    public void setTu(TranslationUnit tu) {
        this.tu = tu;
    }
}
