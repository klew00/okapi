/*===========================================================================
  Copyright (C) 2010 by the Okapi Framework contributors
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

package net.sf.okapi.tm.pensieve.server;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import net.sf.okapi.common.LocaleId;
import net.sf.okapi.filters.tmx.TmxFilter;
import net.sf.okapi.tm.pensieve.seeker.PensieveSeeker;
import net.sf.okapi.tm.pensieve.tmx.OkapiTmxImporter;
import net.sf.okapi.tm.pensieve.writer.PensieveWriter;

import org.apache.lucene.store.Directory;
import org.apache.lucene.store.NIOFSDirectory;
import org.slf4j.Logger;

@ApplicationScoped
public class PensieveStartup {

	private Logger log;
	
	private Directory directory;
	
	private PensieveWriter writer;
	private PensieveSeeker seeker;
	
	protected PensieveStartup() {}
	
	@Inject
	public PensieveStartup(Logger log) throws IOException {
		this.log = log;
		this.directory = new NIOFSDirectory(new File("pensieve-index"));

		// create index if it doesn't exist
		getWriter();
		importTestData();
	}	
	
	@Produces PensieveWriter getWriter() throws IOException {
		if( writer == null ) {
			log.debug("creating PensieveWriter");
			writer = new PensieveWriter(directory, true);
		}
		return writer; 
	}
	
	@Produces PensieveSeeker getSeeker() throws IOException {
		if( seeker == null) {
			log.debug("creating PensieveSeeker");
			seeker = new PensieveSeeker(directory);
		}
		return seeker;
	}
	
	private void importTestData() throws IOException {
		OkapiTmxImporter importer = new OkapiTmxImporter(new LocaleId("EN-US"), new TmxFilter());
		try{
			importer.importTmx(PensieveStartup.class.getResource("mytm.tmx").toURI(), new LocaleId("FR-FR"), writer);
			writer.commit();
		}
		catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
	}
	
}
