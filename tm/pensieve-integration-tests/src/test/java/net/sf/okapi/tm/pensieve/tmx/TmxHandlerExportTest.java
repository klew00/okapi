package net.sf.okapi.tm.pensieve.tmx;

import java.io.StringWriter;
import java.net.URI;
import net.sf.okapi.common.XMLWriter;
import net.sf.okapi.filters.tmx.TmxFilter;
import net.sf.okapi.tm.pensieve.seeker.PensieveSeeker;
import net.sf.okapi.tm.pensieve.seeker.TmSeeker;
import net.sf.okapi.tm.pensieve.writer.PensieveWriter;
import net.sf.okapi.tm.pensieve.writer.TmWriter;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.junit.Test;

import net.sf.okapi.common.filterwriter.TMXWriter;

public class TmxHandlerExportTest {

    @Test
    public void exportTmx_sample_metadata() throws Exception {
        TmxFilter tmxFilter = new TmxFilter();
        Directory ramDir = new RAMDirectory();
        TmWriter tmWriter = new PensieveWriter(ramDir);
        OkapiTmxImporter tmxImporter = new OkapiTmxImporter("EN", new TmxFilter());
        OkapiTmxExporter tmxExporter = new OkapiTmxExporter();
        tmxImporter.importTmx(this.getClass().getResource("/sample_tmx.xml").toURI(), "IT", tmWriter);

        TmSeeker seeker = new PensieveSeeker(ramDir);
        StringWriter sWriter = new StringWriter();
        URI uri = new URI("target/test-classes/output_tmx.xml");
        tmxExporter.exportTmx("EN", "IT", seeker, new TMXWriter(new XMLWriter(sWriter)));
        
        //TODO: verify content of file
    }

}
