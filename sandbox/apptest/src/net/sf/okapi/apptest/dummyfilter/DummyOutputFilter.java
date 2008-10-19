package net.sf.okapi.apptest.dummyfilter;

import java.io.OutputStream;

import net.sf.okapi.apptest.common.IParameters;
import net.sf.okapi.apptest.common.IResource;
import net.sf.okapi.apptest.filters.IInputFilter;
import net.sf.okapi.apptest.filters.IOutputFilter;
import net.sf.okapi.apptest.resource.SkeletonUnit;
import net.sf.okapi.apptest.resource.TextUnit;

public class DummyOutputFilter implements IOutputFilter {
	
	private String outputPath;
	private String language;
	private String defaultEncoding;
	
	public String getName () {
		return "DummyOutputFilter";
	}
	
	public void handleEvent (int eventType,
		IResource resource)
	{
		switch ( eventType ) {
		case IInputFilter.START_DOCUMENT:
			System.out.println("DummyOutputFilter: start-of-document");
			System.out.println(" -open output: " + outputPath);
			System.out.println(" -output language = " + language);
			System.out.println(" -output default encoding = " + defaultEncoding);
			break;
		case IInputFilter.END_DOCUMENT:
			System.out.println("DummyOutputFilter: end-of-document");
			close();
			break;
		case IInputFilter.START_SUBDOCUMENT:
			System.out.println("DummyOutputFilter: start-of-sub-document");
			break;
		case IInputFilter.END_SUBDOCUMENT:
			System.out.println("DummyOutputFilter: end-of-sub-document");
			break;
		case IInputFilter.START_GROUP:
			System.out.println("DummyOutputFilter: start-of-group");
			break;
		case IInputFilter.END_GROUP:
			System.out.println("DummyOutputFilter: end-of-group");
			break;
		case IInputFilter.TEXT_GROUP:
			System.out.print("DummyOutputFilter: text-group: ");
			System.out.println("TODO");
			break;
		case IInputFilter.SKELETON_UNIT:
			System.out.print("DummyOutputFilter: skeleton-unit: [");
			System.out.println(((SkeletonUnit)resource).toString()+"]");
			break;
		case IInputFilter.TEXT_UNIT:
			System.out.print("DummyOutputFilter: text-unit: [");
			System.out.println(((TextUnit)resource).toString()+"]");
			break;
		}
	}

	public void close() {
		System.out.println("DummyOutputFilter: close() called");
		reset();
	}

	public void setOptions (String language,
		String defaultEncoding)
	{
		this.language = language;
		this.defaultEncoding = defaultEncoding;
		System.out.println("DummyOutputFilter: setOptions() called");
		System.out.println(" -output language = " + language);
		System.out.println(" -output default encoding = " + defaultEncoding);
	}

	public void setOutput (String outputPath) {
		this.outputPath = outputPath;
		System.out.println("DummyOutputFilter: setOutput(path) called");
		System.out.println(" -output path = " + outputPath);
	}

	public void setOutput (OutputStream outputStream) {
		System.out.println("DummyOutputFilter: setOutput(stream) called");
	}

	private void reset () {
		outputPath = null;
		language = null;
		defaultEncoding = null;
	}

	public IParameters getParameters () {
		return null;
	}

	public void setParameters (IParameters params) {
		// Not parameters in this output
	}

}
