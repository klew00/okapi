package net.sf.okapi.steps.rainbowkit.ontram.xini;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.XMLWriter;
import net.sf.okapi.common.encoder.EncoderManager;
import net.sf.okapi.common.filterwriter.IFilterWriter;
import net.sf.okapi.common.resource.StartDocument;
import net.sf.okapi.common.resource.TextUnit;
import net.sf.okapi.common.skeleton.ISkeletonWriter;

public class XiniWriter implements IFilterWriter {

	private XMLWriter writer;
	private EncoderManager encodingManager;
	private IParameters params;
	FilterEventToXiniTransformer transformer;
	private String xiniPath;

	public XiniWriter() {
		super();
		transformer = new FilterEventToXiniTransformer();
	}

	@Override
	public void cancel() {
	}

	@Override
	public void close() {
		if ( writer != null ) {
			writer.close();
			writer = null;
		}
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
			processStartDocument(event.getStartDocument());
			break;
		case TEXT_UNIT:
			processTextUnit(event.getTextUnit());
			break;
		}
		return event;
	}

	private void processTextUnit(TextUnit textUnit) {

		transformer.transformTextUnit(textUnit);

	}

	public void writeXINI() {
		
		try {
			File outPutPath = new File(xiniPath);
			
			if ( !outPutPath.getParentFile().exists() ) {
				outPutPath.getParentFile().mkdirs();
			}
			
			if ( !outPutPath.exists() )
				outPutPath.createNewFile();
			
			transformer.marshall(new FileOutputStream(outPutPath));
		}
		catch (FileNotFoundException e) {
			// We created the file
			throw new RuntimeException(e);
		}
		catch (IOException e) {
			// TODO add Logging
			throw new RuntimeException(e);
		}
	}

	private void processStartDocument(StartDocument startDocument) {
		transformer.transformStartDoc(startDocument);
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

	public void setXiniPath(String xiniPath) {
		this.xiniPath = xiniPath;
	}

	@Override
	public ISkeletonWriter getSkeletonWriter() {
		return null;
	}

}
