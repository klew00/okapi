package net.sf.okapi.common.resource;

public class Group extends ResourceContainer implements ITranslatable {

	private static final long serialVersionUID = 1L;
	
	private String           id;
	private ITranslatable    parent;
	private boolean          isTranslatable;

	
	public String getID () {
		return id;
	}

	public void setID (String value) {
		id = value;
	}

	public ITranslatable getParent () {
		return parent;
	}

	public void setParent (ITranslatable value) {
		parent = value;
	}

	public boolean isTranslatable () {
		return isTranslatable;
	}

	public void setIsTranslatable (boolean value) {
		isTranslatable = value;
	}

	public boolean hasChild () {
		return !isEmpty();
	}

}
