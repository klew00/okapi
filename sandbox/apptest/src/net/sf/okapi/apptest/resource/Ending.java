package net.sf.okapi.apptest.resource;

import java.util.Hashtable;

import net.sf.okapi.apptest.common.IAnnotation;
import net.sf.okapi.apptest.common.IResource;
import net.sf.okapi.apptest.common.ISkeleton;

public class Ending implements IResource {

	protected String id;
	protected ISkeleton skeleton;
	protected Hashtable<String, IAnnotation> annotations;
	
	public Ending (String id) {
		annotations = new Hashtable<String, IAnnotation>();
		this.id = id;
	}

	public String getId () {
		return id;
	}
	
	public void setId (String id) {
		this.id = id;
	}

	public ISkeleton getSkeleton () {
		return skeleton;
	}
	
	public void setSkeleton (ISkeleton skeleton) {
		this.skeleton = skeleton;
	}

	public IAnnotation getAnnotation (String name) {
		return annotations.get(name);
	}

	public void setAnnotation (String name,
		IAnnotation object)
	{
		annotations.put(name, object);
	}

}
