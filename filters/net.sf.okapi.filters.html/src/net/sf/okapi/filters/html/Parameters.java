package net.sf.okapi.filters.html;

import net.sf.okapi.common.BaseParameters;
import net.sf.okapi.common.yaml.TaggedFilterConfiguration;
import net.sf.okapi.common.yaml.YamlConfigurationReader;

public class Parameters extends BaseParameters {
	private TaggedFilterConfiguration taggedConfig;
	
	public Parameters () {		
		reset();
	}

	/* (non-Javadoc)
	 * @see net.sf.okapi.common.IParameters#fromString(java.lang.String)
	 */
	public void fromString(String data) {
		taggedConfig = new TaggedFilterConfiguration(data);
	}
	
	@Override
	public String toString() {
		return taggedConfig.toString();
		
	}

	/* (non-Javadoc)
	 * @see net.sf.okapi.common.IParameters#reset()
	 */
	public void reset() {
		taggedConfig = null;
	}
	
	public TaggedFilterConfiguration getTaggedConfig() {
		return taggedConfig;
	}

	/**
	 * @param groovyFilterConfiguration
	 */
	public void setTaggedConfig(TaggedFilterConfiguration taggedConfig) {		
		this.taggedConfig = taggedConfig;
	}
}
