package net.sf.okapi.tm.pensieve.server;

import java.io.File;
import java.io.IOException;
import java.net.URI;
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
		PensieveWriter writer = getWriter();
		
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
		}
		catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
	}
	
}
