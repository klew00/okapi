package net.sf.okapi.filters.html.ui.model;

import java.util.List;

public class AttributeRules extends AbstractModelObject {
	private String globalAttributeName;
	private List<String> allTagsExcept;
	private String tagName;
	private String attribute;
	private String attributeType;
	private String conditionalRules;

	public String getGlobalAttributeName() {
		return globalAttributeName;
	}

	public void setGlobalAttributeName(String globalAttributeName) {
		this.globalAttributeName = globalAttributeName;
	}

	public List<String> getAllTagsExcept() {
		return allTagsExcept;
	}

	public void setAllTagsExcept(List<String> allTagsExcept) {
		this.allTagsExcept = allTagsExcept;
	}

	public String getTagName() {
		return tagName;
	}

	public void setTagName(String tagName) {
		this.tagName = tagName;
	}

	public String getAttribute() {
		return attribute;
	}

	public void setAttribute(String attribute) {
		this.attribute = attribute;
	}

	public String getAttributeType() {
		return attributeType;
	}

	public void setAttributeType(String attributeType) {
		this.attributeType = attributeType;
	}

	public String getConditionalRules() {
		return conditionalRules;
	}

	public void setConditionalRules(String conditionalRules) {
		this.conditionalRules = conditionalRules;
	}
}
