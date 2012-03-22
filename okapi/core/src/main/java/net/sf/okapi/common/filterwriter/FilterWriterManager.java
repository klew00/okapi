package net.sf.okapi.common.filterwriter;

import java.io.IOException;
import java.util.Stack;

import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.encoder.EncoderManager;
import net.sf.okapi.common.resource.EndSubfilter;
import net.sf.okapi.common.resource.StartSubfilter;
import net.sf.okapi.common.skeleton.ISkeletonWriter;

public class FilterWriterManager extends GenericFilterWriter {
	private class FilterWriterInfo {
		IFilterWriter filterWriter;
		IFilterWriter subDocWriter;
		ISkeletonWriter skelWriter;
		EncoderManager encoderManager;
		int subDocLevel;
		String entryName;
	}
	
	Stack<FilterWriterInfo> filterWriterStack;

	public FilterWriterManager(ISkeletonWriter skelWriter,
			EncoderManager encoderManager) {
		super(skelWriter, encoderManager);
		filterWriterStack = new Stack<FilterWriterInfo>();
		// add outself to the stack
		FilterWriterInfo fwi = new FilterWriterInfo();
		filterWriterStack.add(fwi);
	}

	@Override
	protected void processStartSubfilter(StartSubfilter resource)
			throws IOException {
		super.processStartSubfilter(resource);
	}

	@Override
	protected void processEndSubfilter(EndSubfilter resource)
			throws IOException {	
		super.processEndSubfilter(resource);
	}
}
