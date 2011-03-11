package net.sf.okapi.steps.rainbowkit.ontram;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.resource.TextUnit;
import net.sf.okapi.filters.rainbowkit.Manifest;
import net.sf.okapi.steps.rainbowkit.common.BasePackageWriter;
import net.sf.okapi.steps.rainbowkit.ontram.xini.XiniWriter;

public class OntramPackageWriter extends BasePackageWriter {

	private XiniWriter writer;

	public OntramPackageWriter() {
		super(Manifest.EXTRACTIONTYPE_ONRTAM);
		writer = new XiniWriter();
	}

	@Override
	protected void processStartBatch() {
		manifest.setSubDirectories("original", "work", "work", "done", null, true);
		setTMXInfo(false, null, null, null, null);
		super.processStartBatch();

		writer = new XiniWriter();
		String xiniPath = manifest.getSourceDirectory() + "contents.xini";
		writer.setXiniPath(xiniPath);
	}

	@Override
	protected void processEndBatch() {
		super.processEndBatch();
		writer.writeXINI();
		close();
	}

	@Override
	protected void processStartDocument(Event event) {
		writer.handleEvent(event);
	}

	@Override
	protected void processEndDocument(Event event) {
		writer.handleEvent(event);
	}

	@Override
	protected void processTextUnit (Event event) {
		// Skip non-translatable
		TextUnit tu = event.getTextUnit();
		if ( !tu.isTranslatable() ) return;
		
		writer.handleEvent(event);
	}
	
	@Override
	public void close () {
		if ( writer != null ) {
			writer.close();
			writer = null;
		}
	}

	@Override
	public String getName() {
		return getClass().getName();
	}
}
