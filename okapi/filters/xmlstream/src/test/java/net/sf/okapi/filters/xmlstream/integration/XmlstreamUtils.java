package net.sf.okapi.filters.xmlstream.integration;

import java.io.File;
import java.io.FilenameFilter;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.resource.DocumentPart;
import net.sf.okapi.common.resource.Ending;
import net.sf.okapi.common.resource.StartDocument;
import net.sf.okapi.common.resource.StartGroup;
import net.sf.okapi.common.resource.TextUnit;
import net.sf.okapi.common.skeleton.GenericSkeletonWriter;

public final class XmlstreamUtils {
	public static String[] getTestFiles(final String aFile, final String suffix) throws URISyntaxException {
		// read all files in the test xmlstream directory
		URL url = DitaExtractionComparisionTest.class.getResource(aFile);
		File dir = new File(url.toURI()).getParentFile();

		FilenameFilter filter = new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.endsWith(suffix);
			}
		};
		return dir.list(filter);
	}
}
