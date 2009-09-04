/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.sf.okapi.tm.pensieve.common;

import net.sf.okapi.common.resource.TextFragment;

/**
 *
 * @author HaslamJD
 */
class TranslationUnitValue {
    private String lang;
    private TextFragment content;

    public TranslationUnitValue() {}

    public TranslationUnitValue(String lang, TextFragment content) {
        this.lang = lang;
        this.content = content;
    }

    public TextFragment getContent() {
        return content;
    }

    public void setContent(TextFragment content) {
        this.content = content;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

}
