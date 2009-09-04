package net.sf.okapi.tm.pensieve.tmx;

import net.sf.okapi.tm.pensieve.common.TranslationUnit;
import net.sf.okapi.tm.pensieve.writer.TMWriter;

import java.io.IOException;
import java.util.List;

/**
 * User: Christian Hargraves
 * Date: Sep 4, 2009
 * Time: 1:54:27 PM
 */
public interface TMXHandler {

    List<TranslationUnit> getTranslationUnitsFromTMX(String targetLang);

    void importTMX(String targetLang, TMWriter tmWriter) throws IOException;
}
