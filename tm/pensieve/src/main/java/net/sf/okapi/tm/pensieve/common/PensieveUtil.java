package net.sf.okapi.tm.pensieve.common;

import net.sf.okapi.common.resource.TextUnit;

/**
 * User: Christian Hargraves
 * Date: Sep 4, 2009
 * Time: 4:12:24 PM
 */
public final class PensieveUtil {

    private PensieveUtil(){}

    public static TranslationUnit convertTranslationUnit(String sourceLang, String targetLang, TextUnit textUnit) {
        TranslationUnitVariant source = new TranslationUnitVariant(sourceLang, textUnit.getSourceContent());
        TranslationUnitVariant target = new TranslationUnitVariant(targetLang, textUnit.getTargetContent(targetLang));
        return new TranslationUnit(source, target);
    }
    
}
