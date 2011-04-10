/*===========================================================================
  Copyright (C) 2008-2011 by the Okapi Framework contributors
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

import net.sf.okapi.common.filters.IFilter;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.Util;
import net.sf.okapi.common.Event;
import net.sf.okapi.common.EventType;
import net.sf.okapi.common.resource.RawDocument;
import net.sf.okapi.tm.pensieve.writer.ITmWriter;
import net.sf.okapi.tm.pensieve.common.TranslationUnit;
import net.sf.okapi.tm.pensieve.common.PensieveUtil;

import java.net.URI;
import java.io.IOException;

public class OkapiTmxImporter implements ITmxImporter {

    private IFilter tmxFilter;
    private LocaleId sourceLang;

    /**
     * Creates an instance of OkapiTMXHandler
     * @param sourceLang the language to import as the source language.
     * @param tmxFilter the IFilter to use to parse the TMX
     */
    public OkapiTmxImporter(LocaleId sourceLang, IFilter tmxFilter) {
        this.tmxFilter = tmxFilter;
        this.sourceLang = sourceLang;
        if (Util.isNullOrEmpty(sourceLang)) {
            throw new IllegalArgumentException("'sourceLang' must be set");
        }
        if (tmxFilter == null) {
            throw new IllegalArgumentException("'filter' must be set");
        }
    }

    /**
     * Imports TMX to Pensieve
     * @param tmxUri The location of the TMX
     * @param targetLang The target language to index
     * @param tmWriter The TMWriter to use when writing to the TM
     * @throws java.io.IOException if there was a problem with the TMX import
     */
    public void importTmx(URI tmxUri, LocaleId targetLang, ITmWriter tmWriter) throws IOException {
        checkImportTmxParams(tmxUri, targetLang, tmWriter);
        try {
            tmxFilter.open(new RawDocument(tmxUri, null, sourceLang, targetLang));
            while (tmxFilter.hasNext()) {
                Event event = tmxFilter.next();
                indexEvent(targetLang, tmWriter, event);
            }
        } finally {
            tmxFilter.close();
        }
    }

    private void checkImportTmxParams(URI tmxUri,
    	LocaleId targetLang,
    	ITmWriter tmWriter)
    {
        if (Util.isNullOrEmpty(targetLang)) {
            throw new IllegalArgumentException("'targetLang' was not set");
        }
        if (tmxUri == null) {
            throw new IllegalArgumentException("'tmxUri' was not set");
        }
        if (tmWriter == null) {
            throw new IllegalArgumentException("'tmWriter' was not set");
        }
    }

    private void indexEvent(LocaleId targetLang, ITmWriter tmWriter, Event event) throws IOException {
        TranslationUnit tu;
        if (event.getEventType() == EventType.TEXT_UNIT) {
            tu = PensieveUtil.convertToTranslationUnit(sourceLang, targetLang, event.getTextUnit());
            tmWriter.indexTranslationUnit(tu);
        }
    }
    

}
