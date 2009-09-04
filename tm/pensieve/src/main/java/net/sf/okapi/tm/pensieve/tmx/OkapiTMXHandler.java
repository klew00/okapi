/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sf.okapi.tm.pensieve.tmx;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.EventType;
import net.sf.okapi.common.filters.IFilter;
import net.sf.okapi.common.resource.RawDocument;
import net.sf.okapi.common.resource.TextUnit;
import net.sf.okapi.filters.tmx.TmxFilter;
import net.sf.okapi.tm.pensieve.common.TranslationUnit;
import net.sf.okapi.tm.pensieve.common.TranslationUnitValue;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public final class OkapiTMXHandler {


    private OkapiTMXHandler() {
    }

    public static List<TranslationUnit> getTranslationUnitsFromTMX(String filename, String sourceLang, String targetLang) {
        List<TextUnit> textUnits = getTextUnits(getEventsFromTMX(filename, sourceLang, targetLang));
        List<TranslationUnit> tus = new ArrayList<TranslationUnit>(textUnits.size());
        for (TextUnit textUnit : textUnits) {
            tus.add(convertTranslationUnit(sourceLang, targetLang, textUnit));
        }
        return tus;
    }

    public static TranslationUnit convertTranslationUnit(String sourceLang, String targetLang, TextUnit textUnit) {
        TranslationUnitValue source = new TranslationUnitValue(sourceLang, textUnit.getSourceContent());
        TranslationUnitValue target = new TranslationUnitValue(targetLang, textUnit.getTargetContent(targetLang));
        return new TranslationUnit(source, target);
    }

    private static List<Event> getEventsFromTMX(String filename, String sourceLang, String targetLang) {
        URI fileURI;
        try {
            fileURI = OkapiTMXHandler.class.getResource(filename).toURI();
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

    private static List<TextUnit> getTextUnits(List<Event> list) {
        List<TextUnit> tus = new ArrayList<TextUnit>();
        for (Event event : list) {
            if (event.getEventType() == EventType.TEXT_UNIT) {
                tus.add((TextUnit) event.getResource());
            }
        }
        return tus;
    }
}
