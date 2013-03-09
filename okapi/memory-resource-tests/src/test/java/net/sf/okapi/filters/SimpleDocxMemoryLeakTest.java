package net.sf.okapi.filters;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.filters.FilterConfigurationMapper;
import net.sf.okapi.common.resource.RawDocument;
import net.sf.okapi.filters.openxml.OpenXMLFilter;

public class SimpleDocxMemoryLeakTest {

	private static FilterConfigurationMapper fcMapper;	
	private static LocaleId locEN = LocaleId.fromString("EN");

	public static void setUp() throws Exception {
		// Create the mapper
		fcMapper = new FilterConfigurationMapper();
		// Fill it with the default configurations of several filters
		fcMapper.addConfigurations("net.sf.okapi.filters.openxml.OpenXMLFilter");
		fcMapper.addConfigurations("net.sf.okapi.filters.xml.XMLFilter");
	}
	
	private static URI getUri(String fileName) throws URISyntaxException {
		URL url = SimpleDocxMemoryLeakTest.class.getResource(fileName);
		return url.toURI();
	}
		
	public static void main(String[] args) throws Exception {
		setUp();
		
		for (int i = 0; i <= 10000000L; i++) {
			RawDocument rd = new RawDocument(getUri("/docx/OpenXML_text_reference_v1_2.docx"), "UTF-8", locEN);
			rd.setFilterConfigId("okf_openxml");
			OpenXMLFilter f = new OpenXMLFilter();
			f.open(rd);
			while(f.hasNext()) {
				f.next();
			}
			f.close();
			System.out.println(i);
		}				
	}
}
