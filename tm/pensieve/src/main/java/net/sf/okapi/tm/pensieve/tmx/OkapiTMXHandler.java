/*===========================================================================
  Copyright (C) 2008-2009 by the Okapi Framework contributors
-----------------------------------------------------------------------------
  This library is free software; you can redistribute it and/or modify it
  under the terms of the GNU Lesser General Public License as published by
  the Free Software Foundation; either version 2.1 of the License, or (at
  your option) any later version.

  This library is distributed in the hope that it will be useful, but
  WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser
  General Public License for more details.

  You should have received a copy of the GNU Lesser General Public License
  along with this library; if not, write to the Free Software Foundation,
  Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA

  See also the full LGPL text here: http://www.gnu.org/copyleft/lesser.html
===========================================================================*/

package net.sf.okapi.tm.pensieve.tmx;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.EventType;
import net.sf.okapi.common.Util;
import net.sf.okapi.common.filters.IFilter;
import net.sf.okapi.common.resource.RawDocument;
import net.sf.okapi.common.resource.TextUnit;
import net.sf.okapi.tm.pensieve.common.PensieveUtil;
import net.sf.okapi.tm.pensieve.common.TranslationUnit;
import net.sf.okapi.tm.pensieve.writer.TMWriter;

import java.io.IOException;
import java.net.URI;

/**
 * Used to interact with the Okapi Standards for TMX. For example, the property names and default fields stored.
 */
public class OkapiTMXHandler implements TMXHandler{

    private String sourceLang;
    IFilter tmxFilter;

    /**
     * Creates an instance of OkapiTMXHandler
     * @param sourceLang The language to import as the source language
     * @param tmxFilter The IFilter to use to parse the TMX
     */
    public OkapiTMXHandler(String sourceLang, IFilter tmxFilter) {
        this.tmxFilter = tmxFilter;
        this.sourceLang = sourceLang;
        if (Util.isEmpty(sourceLang)){
            throw new IllegalArgumentException("sourceLang must be set");
        }
        if (tmxFilter == null){
            throw new IllegalArgumentException("filter must be set");
        }
    }

    /**
     * Imports TMX to Pensieve
     * @param tmxUri The location of the TMX
     * @param targetLang The target language to index
     * @param tmWriter The TMWriter to use when writing to the TM
     * @throws IOException if there was a problem with the TMX import
     */
    public void importTmx(URI tmxUri, String targetLang, TMWriter tmWriter) throws IOException {
        checkImportTmxParams(tmxUri, targetLang, tmWriter);
        try{
            tmxFilter.open(new RawDocument(tmxUri, null, sourceLang, targetLang));
            while (tmxFilter.hasNext()) {
                Event event = tmxFilter.next();
                indexEvent(targetLang, tmWriter, event);
            }
        }finally{
            tmxFilter.close();
        }
    }

    private void checkImportTmxParams(URI tmxUri, String targetLang, TMWriter tmWriter) {
        if (Util.isEmpty(targetLang)){
            throw new IllegalArgumentException("targetLang was not set");
        }
        if (tmxUri == null){
            throw new IllegalArgumentException("tmxUri was not set");
        }
        if (tmWriter == null){
            throw new IllegalArgumentException("tmWriter was not set");
        }
    }

    private void indexEvent(String targetLang, TMWriter tmWriter, Event event) throws IOException {
        TranslationUnit tu;
        if (event.getEventType() == EventType.TEXT_UNIT) {
            tu = PensieveUtil.convertTranslationUnit(sourceLang, targetLang, (TextUnit) event.getResource());
            tmWriter.indexTranslationUnit(tu);
        }
    }

}
