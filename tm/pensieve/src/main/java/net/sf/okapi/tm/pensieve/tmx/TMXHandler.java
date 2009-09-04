/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sf.okapi.tm.pensieve.tmx;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import net.sf.okapi.common.Event;
import net.sf.okapi.common.resource.RawDocument;
import net.sf.okapi.common.resource.TextUnit;
import net.sf.okapi.tm.pensieve.common.TranslationUnit;
import net.sf.okapi.common.EventType;
import net.sf.okapi.common.filters.IFilter;
import net.sf.okapi.filters.tmx.TmxFilter;
import net.sf.okapi.tm.pensieve.common.MetaDataTypes;

public final class TMXHandler {


    private TMXHandler() {
    //this should never be instantiated
    }

    public static List<TranslationUnit> getTranslationUnitsFromTMX(String filename, String sourceLang, String targetLang) {

        List<TranslationUnit> tus = new ArrayList<TranslationUnit>();

        List<TextUnit> textunits = getTextUnit(
                getEventsFromTMX(filename, sourceLang, targetLang));

        for (TextUnit textunit : textunits) {
            TranslationUnit tu = new TranslationUnit();
            tu.setSource(textunit.getSourceContent());
            tu.getMetadata().put(MetaDataTypes.SOURCE_LANG, sourceLang);
            tu.setTarget(textunit.getTargetContent(targetLang));
            tu.getMetadata().put(MetaDataTypes.TARGET_LANG, targetLang);
            tus.add(tu);
        }

        return tus;
    }

    private static List<Event> getEventsFromTMX(String filename, String sourceLang, String targetLang) {
        URI fileURI;
        try {            
            fileURI = TMXHandler.class.getResource(filename).toURI();
        } catch (URISyntaxException use) {
            throw new IllegalArgumentException(use);
        }

        IFilter filter = new TmxFilter();
        ArrayList<Event> list = new ArrayList<Event>();
        filter.open(new RawDocument(fileURI, null, sourceLang, targetLang));
        while (filter.hasNext()) {
            Event event = filter.next();
            list.add(event);
        }
        filter.close();
        return list;
    }

    private static List<TextUnit> getTextUnit(List<Event> list) {
        List<TextUnit> tus = new ArrayList<TextUnit>();
        for (Event event : list) {
            if (event.getEventType() == EventType.TEXT_UNIT) {
                tus.add((TextUnit) event.getResource());
            }
        }
        return tus;
    }
}
