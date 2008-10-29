package net.sf.okapi.common.filters;

import java.io.OutputStream;

import net.sf.okapi.common.IParameters;

public abstract class BaseFilterWriter implements IFilterWriter {

	/* (non-Javadoc)
	 * @see net.sf.okapi.common.filters.IFilterWriter#close()
	 */
	public void close() {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see net.sf.okapi.common.filters.IFilterWriter#getName()
	 */
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see net.sf.okapi.common.filters.IFilterWriter#getParameters()
	 */
	public IParameters getParameters() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see net.sf.okapi.common.filters.IFilterWriter#handleEvent(net.sf.okapi.common.filters.FilterEvent)
	 */
	public FilterEvent handleEvent(FilterEvent event) {
		// TODO Auto-generated method stub
		return event;
	}

	/* (non-Javadoc)
	 * @see net.sf.okapi.common.filters.IFilterWriter#setOptions(java.lang.String, java.lang.String)
	 */
	public void setOptions(String language, String defaultEncoding) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see net.sf.okapi.common.filters.IFilterWriter#setOutput(java.lang.String)
	 */
	public void setOutput(String path) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see net.sf.okapi.common.filters.IFilterWriter#setOutput(java.io.OutputStream)
	 */
	public void setOutput(OutputStream output) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see net.sf.okapi.common.filters.IFilterWriter#setParameters(net.sf.okapi.common.IParameters)
	 */
	public void setParameters(IParameters params) {
		// TODO Auto-generated method stub

	}
}
