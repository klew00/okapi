package net.sf.okapi.steps.rainbowkit.ontram;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.resource.TextUnit;
import net.sf.okapi.filters.rainbowkit.Manifest;
import net.sf.okapi.filters.rainbowkit.MergingInfo;
import net.sf.okapi.filters.xini.XINIWriter;
import net.sf.okapi.steps.rainbowkit.common.BasePackageWriter;

public class OntramPackageWriter extends BasePackageWriter {

	private XINIWriter writer;

	public OntramPackageWriter() {
		super(Manifest.EXTRACTIONTYPE_ONTRAM);
		writer = new XINIWriter();
	}

	@Override
	protected void processStartBatch() {
		manifest.setSubDirectories("original", "xini", "xini", "translated", null, false);
		setTMXInfo(false, null, null, null, null);
		String path = manifest.getSourceDirectory() + "contents.xini";

		writer = new XINIWriter();
		writer.setOutputPath(path);
		
		writer.init();
		
		super.processStartBatch();
	}

	@Override
	protected void processStartDocument(Event event) {
		super.processStartDocument(event);
		
		MergingInfo info = manifest.getItem(docId);
		String inputPath = info.getRelativeInputPath();
		writer.setNextPageName(inputPath);
		
		writer.handleEvent(event);
	}

	@Override
	protected void processEndDocument(Event event) {
		writer.writeXINI();
		close();

		super.processEndDocument(event);
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
