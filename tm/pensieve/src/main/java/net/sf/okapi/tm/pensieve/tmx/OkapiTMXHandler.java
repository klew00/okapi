/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sf.okapi.tm.pensieve.tmx;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.EventType;
import net.sf.okapi.common.Util;
import net.sf.okapi.common.filters.IFilter;
import net.sf.okapi.common.resource.RawDocument;
import net.sf.okapi.common.resource.TextUnit;
import net.sf.okapi.filters.tmx.TmxFilter;
import net.sf.okapi.tm.pensieve.common.PensieveUtil;
import net.sf.okapi.tm.pensieve.common.TranslationUnit;
import net.sf.okapi.tm.pensieve.writer.TMWriter;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class OkapiTMXHandler implements TMXHandler{

    private URI uri;
    private String sourceLang;

    public OkapiTMXHandler(URI uri, String sourceLang) {
        if (uri == null || Util.isEmpty(sourceLang)){
            throw new IllegalArgumentException("both uri and sourceLang must be set");
        }
        this.uri = uri;
        this.sourceLang = sourceLang;
    }

    public List<TranslationUnit> getTranslationUnitsFromTMX(String targetLang) {
        if (Util.isEmpty(targetLang)){
            throw new IllegalArgumentException("targetLang was not set");
        }
        List<TextUnit> textUnits = getTextUnits(getEventsFromTMX(targetLang));
        List<TranslationUnit> tus = new ArrayList<TranslationUnit>(textUnits.size());
        for (TextUnit textUnit : textUnits) {
            tus.add(PensieveUtil.convertTranslationUnit(sourceLang, targetLang, textUnit));
        }
        return tus;
    }

    public void importTMX(String targetLang, TMWriter tmWriter) throws IOException {
        List<TranslationUnit> tus = getTranslationUnitsFromTMX(targetLang);
        for(TranslationUnit tu : tus) {
            tmWriter.indexTranslationUnit(tu);
        }
    }

    private List<Event> getEventsFromTMX(String targetLang) {
        IFilter filter = new TmxFilter();
        ArrayList<Event> list = new ArrayList<Event>();
        filter.open(new RawDocument(uri, null, sourceLang, targetLang));
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
