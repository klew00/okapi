package net.sf.okapi.filters.html;

import net.sf.okapi.common.BaseParameters;
import net.sf.okapi.common.groovy.GroovyFilterConfiguration;

public class Parameters extends BaseParameters {
	private GroovyFilterConfiguration groovyConfig;
	
	public Parameters () {		
		reset();
	}

	/* (non-Javadoc)
	 * @see net.sf.okapi.common.IParameters#fromString(java.lang.String)
	 */
	public void fromString(String data) {
		groovyConfig = new GroovyFilterConfiguration(data);
	}
	
	@Override
	public String toString() {
		return groovyConfig.toString();
		
	}

	/* (non-Javadoc)
	 * @see net.sf.okapi.common.IParameters#reset()
	 */
	public void reset() {
		groovyConfig = null;
	}
	
	public GroovyFilterConfiguration getGroovyConfig() {
		return groovyConfig;
	}

	/**
	 * @param groovyFilterConfiguration
	 */
	public void setGroovyConfig(GroovyFilterConfiguration groovyConfig) {		
		this.groovyConfig = groovyConfig;
	}
}
