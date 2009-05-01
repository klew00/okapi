package net.sf.okapi.filters.rtf.tests;



import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.encoder.EncoderManager;
import net.sf.okapi.common.resource.Code;
import net.sf.okapi.common.resource.DocumentPart;
import net.sf.okapi.common.resource.Ending;
import net.sf.okapi.common.resource.RawDocument;
import net.sf.okapi.common.resource.StartDocument;
import net.sf.okapi.common.resource.StartGroup;
import net.sf.okapi.common.resource.TextUnit;
import net.sf.okapi.common.resource.TextFragment.TagType;
import net.sf.okapi.common.skeleton.GenericSkeletonWriter;
import net.sf.okapi.filters.rtf.RTFFilter;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class RtfSnippetsTest {
	private RTFFilter filter;
	private URL parameters;

	@Before
	public void setUp() {
		filter = new RTFFilter();
		//parameters = RtfSnippetsTest.class.getResource("testConfiguration1.yml");
	}

	@After
	public void tearDown() {
	}
	
	private ArrayList<Event> getEvents(String snippet) {
		ArrayList<Event> list = new ArrayList<Event>();
		//filter.setParametersFromURL(parameters);
		filter.open(new RawDocument(snippet, "en"));
		while (filter.hasNext()) {
			Event event = filter.next();
			list.add(event);
		}
		filter.close();
		return list;
	}

	private String generateOutput(ArrayList<Event> list, String original, String trgLang) {
		GenericSkeletonWriter writer = new GenericSkeletonWriter();
		StringBuilder tmp = new StringBuilder();
		for (Event event : list) {
			switch (event.getEventType()) {
			case START_DOCUMENT:
				writer.processStartDocument(trgLang, "utf-8", null, new EncoderManager(), (StartDocument) event
						.getResource());
				break;
			case TEXT_UNIT:
				TextUnit tu = (TextUnit) event.getResource();
				tmp.append(writer.processTextUnit(tu));
				break;
			case DOCUMENT_PART:
				DocumentPart dp = (DocumentPart) event.getResource();
				tmp.append(writer.processDocumentPart(dp));
				break;
			case START_GROUP:
				StartGroup startGroup = (StartGroup) event.getResource();
				tmp.append(writer.processStartGroup(startGroup));
				break;
			case END_GROUP:
				Ending ending = (Ending) event.getResource();
				tmp.append(writer.processEndGroup(ending));
				break;
			}
		}
		writer.close();
		return tmp.toString();
	}
}
