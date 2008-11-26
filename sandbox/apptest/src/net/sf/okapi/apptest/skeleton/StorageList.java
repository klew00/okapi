package net.sf.okapi.apptest.skeleton;

import java.util.ArrayList;
import java.util.Iterator;

import net.sf.okapi.apptest.common.IAnnotation;
import net.sf.okapi.apptest.common.INameable;
import net.sf.okapi.apptest.common.IReferenceable;
import net.sf.okapi.apptest.common.IResource;
import net.sf.okapi.apptest.common.ISkeleton;
import net.sf.okapi.apptest.resource.Property;
import net.sf.okapi.apptest.resource.StartGroup;

public class StorageList extends ArrayList<IResource>
	implements IResource, INameable, IReferenceable {

	private static final long serialVersionUID = 1L;
	
	private StartGroup startGroup;

	public StorageList (StartGroup startGroup) {
		this.startGroup = startGroup;
	}
	
	public String getId () {
		return startGroup.getId();
	}

	public void setId (String id) {
		// Not implemented: read-only info
	}

	public boolean isReferent () {
		return startGroup.isReferent();
	}

	public void setIsReferent (boolean value) {
		// Not implemented: read-only info
	}

	public ISkeleton getSkeleton () {
		return startGroup.getSkeleton();
	}

	public void setSkeleton (ISkeleton skeleton) {
		// Not implemented: read-only info
	}

	public String getName () {
		return startGroup.getName();
	}

	public Property getProperty (String name) {
		return startGroup.getProperty(name);
	}

	public void setName (String name) {
		// Not implemented: read-only info
	}

	public void setProperty (Property property) {
		// Not implemented: read-only info
	}

	public <A> A getAnnotation (Class<? extends IAnnotation> type) {
		return startGroup.getAnnotation(type);
	}

	public void setAnnotation (IAnnotation annotation) {
		startGroup.setAnnotation(annotation);
	}

	public Property getTargetProperty(String language,
		String name,
		int creationOptions)
	{
		return startGroup.getTargetProperty(language, name, creationOptions);
	}

	public void setTargetProperty (String language, Property property) {
		// Not implemented: read-only info
	}

	public Property getTargetProperty (String language, String name) {
		return startGroup.getTargetProperty(language, name);
	}

	public boolean hasTargetProperty(String language, String name) {
		return startGroup.hasTargetProperty(language, name);
	}

	public Iterator<String> propertyNames () {
		return startGroup.propertyNames();
	}

	public Iterator<String> targetPropertyNames (String language) {
		return startGroup.targetPropertyNames(language);
	}

}
