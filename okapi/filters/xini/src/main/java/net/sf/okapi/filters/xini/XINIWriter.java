package net.sf.okapi.filters.xini;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.encoder.EncoderManager;
import net.sf.okapi.common.filterwriter.IFilterWriter;
import net.sf.okapi.common.skeleton.ISkeletonWriter;

public class XINIWriter extends FilterEventToXiniTransformer implements IFilterWriter {

	private EncoderManager encodingManager;
	private IParameters params;
	private String xiniPath;
	private String nextPageName;

	public XINIWriter() {
		
	}

	@Override
	public void cancel() {
	}

	@Override
	public void close() {
	}

	@Override
	public String getName() {
		return "XINIWriter";
	}

	@Override
	public void setOptions(LocaleId locale, String defaultEncoding) {
	}

	@Override
	public void setOutput(String path) {
	}

	@Override
	public void setOutput(OutputStream output) {
	}

	@Override
	public Event handleEvent(Event event) {

		switch (event.getEventType()) {
		case START_DOCUMENT:
			startPage(nextPageName);
			break;
		case TEXT_UNIT:
			transformTextUnit(event.getTextUnit());
			break;
		}
		return event;
	}

	public void writeXINI() {
		
		try {
			File outputPath = new File(xiniPath);
			
			if ( !outputPath.getParentFile().exists() ) {
				outputPath.getParentFile().mkdirs();
			}
			
			if ( !outputPath.exists() )
				outputPath.createNewFile();
			
			marshall(new FileOutputStream(outputPath));
		}
		catch (FileNotFoundException e) {
			// Should be impossible. We created the file.
			throw new RuntimeException(e);
		}
		catch (IOException e) {
			// TODO add Logging
			throw new RuntimeException(e);
		}
	}

	@Override
	public IParameters getParameters() {
		return params;
	}

	@Override
	public void setParameters(IParameters params) {
		this.params = params;

	}

	@Override
	public EncoderManager getEncoderManager() {
		return encodingManager;
	}

	public void setOutputPath(String path) {
		this.xiniPath = path;
	}

	@Override
	public ISkeletonWriter getSkeletonWriter() {
		return null;
	}

	public void setNextPageName(String nextPageName) {
		this.nextPageName = nextPageName;
	}

}
