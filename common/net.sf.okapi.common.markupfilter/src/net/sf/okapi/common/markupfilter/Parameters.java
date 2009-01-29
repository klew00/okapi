package net.sf.okapi.common.markupfilter;

import java.net.URL;

import net.sf.okapi.common.BaseParameters;
import net.sf.okapi.common.yaml.TaggedFilterConfiguration;

public class Parameters extends BaseParameters {
	private TaggedFilterConfiguration taggedConfig;

	public Parameters(String configClassPath) {		
		reset();
		URL url = BaseMarkupFilter.class.getResource(configClassPath);
		setTaggedConfig(new TaggedFilterConfiguration(url));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.okapi.common.IParameters#fromString(java.lang.String)
	 */
	public void fromString(String data) {
		taggedConfig = new TaggedFilterConfiguration(data);
	}

	@Override
	public String toString() {
		return taggedConfig.toString();

	}

	/*
	 * (non-Javadoc)
	 * 
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
