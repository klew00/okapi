package net.sf.okapi.common.filters;

import java.io.OutputStream;

import net.sf.okapi.common.pipeline.IResourceBuilder;

public interface IOutputFilter extends IResourceBuilder {

	/**
	 * Initializes the output.
	 * @param output Stream where to do the output.
	 * @param encoding Encoding for the output.
	 */
	void initialize (OutputStream output,
		String encoding);
}
