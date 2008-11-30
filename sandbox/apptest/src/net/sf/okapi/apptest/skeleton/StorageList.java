package net.sf.okapi.apptest.skeleton;

import java.util.ArrayList;
import java.util.Set;

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

	public ISkeleton getSkeleton () {
		return startGroup.getSkeleton();
	}

	public void setSkeleton (ISkeleton skeleton) {
		// Not implemented: read-only info
	}

	public boolean isReferent () {
		return startGroup.isReferent();
	}

	public void setIsReferent (boolean value) {
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

	public Property setProperty (Property property) {
		// Not implemented: read-only info
		return null;
	}

	public <A> A getAnnotation (Class<? extends IAnnotation> type) {
		return startGroup.getAnnotation(type);
	}

	public void setAnnotation (IAnnotation annotation) {
		startGroup.setAnnotation(annotation);
	}

	public Property createTargetProperty(String language,
		String name,
		boolean overwrite,
		int creationOptions)
	{
		return startGroup.createTargetProperty(language, name, overwrite, creationOptions);
	}

	public Property setTargetProperty (String language, Property property) {
		// Not implemented: read-only info
		return null;
	}

	public Property getTargetProperty (String language, String name) {
		return startGroup.getTargetProperty(language, name);
	}

	public boolean hasTargetProperty(String language, String name) {
		return startGroup.hasTargetProperty(language, name);
	}

	public Set<String> getPropertyNames () {
		return startGroup.getPropertyNames();
	}

	public Set<String> getTargetPropertyNames (String language) {
		return startGroup.getTargetPropertyNames(language);
	}

	public Property getSourceProperty (String name) {
		return startGroup.getSourceProperty(name);
	}

	public Set<String> getSourcePropertyNames () {
		return startGroup.getSourcePropertyNames();
	}

	public Set<String> getTargetLanguages () {
		return startGroup.getTargetLanguages();
	}

	public Property setSourceProperty (Property property) {
		// Not implemented: read-only info
		return null;
	}

}
