package net.sf.okapi.applications.rainbow.utilities;

import net.sf.okapi.common.pipeline.IOutputPipe;
import net.sf.okapi.common.pipeline.IResourceBuilder;

public interface IFilterDrivenUtility extends  IUtility, IResourceBuilder, IOutputPipe{

	/**
	 * Indicates if the utility has to use the output filter
	 * pipe for its last output.
	 * @return True if the output filter is required, false otherwise.
	 */
	boolean needsOutputFilter ();
	
}
