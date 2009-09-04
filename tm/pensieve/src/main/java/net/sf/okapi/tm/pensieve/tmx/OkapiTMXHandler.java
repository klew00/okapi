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
import net.sf.okapi.tm.pensieve.common.TranslationUnit;
import net.sf.okapi.tm.pensieve.common.TranslationUnitValue;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class OkapiTMXHandler implements TMXHandler{

    private URI filename;
    private String sourceLang;

    public OkapiTMXHandler(String filename, String sourceLang) {
        if (Util.isEmpty(filename) || Util.isEmpty(sourceLang)){
            throw new IllegalArgumentException("both filename and sourceLang must be set");
        }
        URL tmpFile = this.getClass().getResource(filename);
        if (tmpFile == null){
            throw new IllegalArgumentException(filename + " was not found!");
        }
        try{
            this.filename = tmpFile.toURI();
        }catch(URISyntaxException urise){
            //How could this happen? It found the file, but couldn't convert it to a URI?
            //:'( no coverage for this one.
            throw new IllegalArgumentException(filename + " does not represent a valid URI.", urise);
        }
        if (this.filename == null){
        }
        this.sourceLang = sourceLang;
    }

    public List<TranslationUnit> getTranslationUnitsFromTMX(String targetLang) {
        List<TextUnit> textUnits = getTextUnits(getEventsFromTMX(targetLang));
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

    private List<Event> getEventsFromTMX(String targetLang) {

        IFilter filter = new TmxFilter();
        ArrayList<Event> list = new ArrayList<Event>();
        filter.open(new RawDocument(filename, null, sourceLang, targetLang));
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
