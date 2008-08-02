package net.sf.okapi.common.resource2;

public class Group extends ResourceContainer implements ITranslatable {

	private static final long serialVersionUID = 1L;
	
	ITranslatable     parent;


	public ITranslatable getParent () {
		return parent;
	}

	public void setParent (ITranslatable value) {
		parent = value;
	}

	public boolean isTranslatable() {
		// TODO Auto-generated method stub
		return false;
	}

	public void setIsTranslatable(boolean value) {
		// TODO Auto-generated method stub
		
	}

	public String getID() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setID(String value) {
		// TODO Auto-generated method stub
		
	}
}
