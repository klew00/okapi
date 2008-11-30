package net.sf.okapi.apptest.resource;

import java.util.Hashtable;
import java.util.Set;

import net.sf.okapi.apptest.annotation.Annotations;
import net.sf.okapi.apptest.annotation.TargetsAnnotation;
import net.sf.okapi.apptest.common.IAnnotation;
import net.sf.okapi.apptest.common.INameable;
import net.sf.okapi.apptest.common.IReferenceable;
import net.sf.okapi.apptest.common.IResource;
import net.sf.okapi.apptest.common.ISkeleton;

public class TextUnit implements IResource, INameable, IReferenceable {

	private String id;
	private boolean isReferent;
	private String name;
	private ISkeleton skeleton;
	protected Hashtable<String, Property> properties;
	private Annotations annotations;
	private TextContainer source;
	
	public TextUnit (String id) {
		create(id, null, false);
	}

	public TextUnit (String id,
		String sourceText)
	{
		create(id, sourceText, false);
	}

	public TextUnit (String id,
		String sourceText,
		boolean isReferent)
	{
		create(id, sourceText, isReferent);
	}

	private void create (String id,
		String sourceText,
		boolean isReferent)
	{
		annotations = new Annotations();
		this.id = id;
		this.isReferent = isReferent;
		source = new TextContainer();
		if ( sourceText != null ) {
			source.text.append(sourceText);
		}
	}

	@Override
	public String toString () {
		return source.toString();
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

	public String getName () {
		return name;
	}

	public void setName (String name) {
		this.name = name;
	}

	@SuppressWarnings("unchecked")
	public <A> A getAnnotation (Class<? extends IAnnotation> type) {
		return (A)annotations.get(type);
	}

	public void setAnnotation (IAnnotation annotation) {
		annotations.set(annotation);
	}

	public Property getProperty (String name) {
		if ( properties == null ) return null;
		return properties.get(name);
	}

	public Property setProperty (Property property) {
		if ( properties == null ) properties = new Hashtable<String, Property>();
		properties.put(property.getName(), property);
		return property;
	}
	
	public Set<String> getPropertyNames () {
		if ( properties == null ) properties = new Hashtable<String, Property>();
		return properties.keySet();
	}

	public Property getSourceProperty (String name) {
		if ( source.properties == null ) return null;
		return source.properties.get(name);
	}

	public Property setSourceProperty (Property property) {
		return source.setProperty(property);
	}
	
	public Set<String> getSourcePropertyNames () {
		return source.getPropertyNames();
	}
	
	public Property getTargetProperty (String language,
		String name)
	{
		TextContainer tc = getTarget(language);
		if ( tc == null ) return null;
		return tc.getProperty(name);
	}

	public Property setTargetProperty (String language,
		Property property)
	{
		return createTarget(language, false, CREATE_EMPTY).setProperty(property);
	}

	public Set<String> getTargetPropertyNames (String language) {
		TextContainer tc = createTarget(language, false, CREATE_EMPTY);
		if ( tc.properties == null ) {
			tc.properties = new Hashtable<String, Property>(); 
		}
		return tc.properties.keySet();
	}

	public boolean hasTargetProperty (String language,
		String name)
	{
		TextContainer tc = getTarget(language);
		if ( tc == null ) return false;
		return (tc.getProperty(name) != null);
	}

	public Set<String> getTargetLanguages () {
		TargetsAnnotation ta = annotations.get(TargetsAnnotation.class);
		if ( ta == null ) {
			ta = new TargetsAnnotation();
			annotations.set(ta);
		}
		return ta.getLanguages();
	}

	public Property createTargetProperty (String language,
		String name,
		boolean overwriteExisting,
		int creationOptions)
	{
		// Get the target or create an empty one
		TextContainer tc = createTarget(language, false, CREATE_EMPTY);
		// Get the property if it exists
		Property prop = tc.getProperty(name);
		// If it does not exists or if we overwrite: create a new one
		if (( prop == null ) || overwriteExisting ) {
			// Get the source property
			prop = source.getProperty(name);
			if ( prop == null ) {
				// If there is no source, create an empty property
				return tc.setProperty(new Property(name, "", false));
			}
			else { // If there is a source property
				// Create a copy, empty or not depending on the options
				if ( creationOptions == CREATE_EMPTY ) {
					return tc.setProperty(new Property(name, "", prop.isReadOnly()));
				}
				else {
					return tc.setProperty(new Property(name, prop.getValue(), prop.isReadOnly()));
				}
			}
		}
		return prop;
	}

	public boolean isReferent () {
		return isReferent;
	}

	public void setIsReferent (boolean value) {
		isReferent = value;
	}

	public TextContainer getSource () {
		return source;
	}
	
	public void setSource (TextContainer textContainer) {
		source = textContainer;
	}
	
	public TextContainer getTarget (String language) {
		TargetsAnnotation ta = annotations.get(TargetsAnnotation.class);
		if ( ta == null ) return null;
		return ta.get(language);
	}

	public TextContainer setTarget (String language,
		TextContainer text)
	{
		TargetsAnnotation ta = annotations.get(TargetsAnnotation.class);
		if ( ta == null ) {
			ta = new TargetsAnnotation();
			annotations.set(ta);
		}
		ta.set(language, text);
		return text;
	}
		
	public boolean hasTarget (String language) {
		TargetsAnnotation ta = annotations.get(TargetsAnnotation.class);
		if ( ta == null ) return false;
		return (ta.get(language) != null);
	}
	
	public TextContainer createTarget (String language,
		boolean overwriteExisting,
		int creationOptions)
	{
		TargetsAnnotation ta = annotations.get(TargetsAnnotation.class);
		if ( ta == null ) {
			ta = new TargetsAnnotation();
			annotations.set(ta);
		}
		TextContainer tc = ta.get(language);
		if (( tc == null ) || overwriteExisting ) {
			tc = new TextContainer();
			if ( (creationOptions & COPY_CONTENT) == COPY_CONTENT ) {
				TextFragment tf = getSourceContent().clone();
				tc.setContent(tf);
			}
			if ( (creationOptions & COPY_PROPERTIES) == COPY_PROPERTIES ) {
				//TODO
			}
			ta.set(language, tc);
		}
		return tc;
	}
	
	public TextFragment getSourceContent () {
		return source.text;
	}
	
	public TextFragment setSourceContent (TextFragment content) {
		source.text = content;
		return source.text;
	}

	public TextFragment getTargetContent (String language) {
		TextContainer tc = getTarget(language);
		if ( tc == null ) return null;
		return tc.getContent();
	}
	
	public TextFragment setTargetContent (String language,
		TextFragment content)
	{
		TextContainer tc = createTarget(language, false, CREATE_EMPTY);
		tc.text = content;
		return tc.text;
	}

}
