package net.sf.okapi.apptest.dummyfilter;

import java.io.OutputStream;

import net.sf.okapi.apptest.common.IParameters;
import net.sf.okapi.apptest.filters.FilterEvent;
import net.sf.okapi.apptest.filters.IFilterWriter;
import net.sf.okapi.apptest.resource.SkeletonUnit;
import net.sf.okapi.apptest.resource.TextUnit;

public class DummyFilterWriter implements IFilterWriter {

	private String outputPath;
	private String language;
	private String defaultEncoding;

	public void close() {
		System.out.println("DummyFilterWriter: close() called");
		reset();
	}

	public String getName() {
		return "DummyFilterWriter";
	}

	public IParameters getParameters() {
		return null;
	}

	public void handleEvent(FilterEvent event) {
		switch ( event.getEventType() ) {
		case START_DOCUMENT:
			System.out.println("DummyFilterWriter: start-of-document");
			System.out.println(" -open output: " + outputPath);
			System.out.println(" -output language = " + language);
			System.out.println(" -output default encoding = " + defaultEncoding);
			break;
		case END_DOCUMENT:
			System.out.println("DummyFilterWriter: end-of-document");
			close();
			break;
		case START_SUBDOCUMENT:
			System.out.println("DummyFilterWriter: start-of-sub-document");
			break;
		case END_SUBDOCUMENT:
			System.out.println("DummyFilterWriter: end-of-sub-document");
			break;
		case START_GROUP:
			System.out.println("DummyFilterWriter: start-of-group");
			break;
		case END_GROUP:
			System.out.println("DummyFilterWriter: end-of-group");
			break;
		case TEXT_GROUP:
			System.out.print("DummyFilterWriter: text-group: ");
			System.out.println("TODO");
			break;
		case SKELETON_UNIT:
			System.out.print("DummyFilterWriter: skeleton-unit: [");
			System.out.println(((SkeletonUnit)event.getResource()).toString()+"]");
			break;
		case TEXT_UNIT:
			System.out.print("DummyFilterWriter: text-unit: [");
			System.out.println(((TextUnit)event.getResource()).toString()+"]");
			break;
		}
	}

	public void setOptions(String language,
		String defaultEncoding)
	{
		this.language = language;
		this.defaultEncoding = defaultEncoding;
		System.out.println("DummyFilterWriter: setOptions() called");
		System.out.println(" -output language = " + language);
		System.out.println(" -output default encoding = " + defaultEncoding);
	}

	public void setOutput(String path) {
		outputPath = path;
		System.out.println("DummyFilterWriter: setOutput(path) called");
		System.out.println(" -output path = " + outputPath);
	}

	public void setOutput(OutputStream output) {
		System.out.println("DummyFilterWriter: setOutput(OutputStream) called");
	}

	public void setParameters (IParameters params) {
	}

	private void reset () {
		outputPath = null;
		language = null;
		defaultEncoding = null;
	}

}
