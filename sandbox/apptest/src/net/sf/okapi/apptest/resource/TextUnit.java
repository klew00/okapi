/*===========================================================================*/
/* Copyright (C) 2008 Asgeir Frimannsson, Jim Hargrave, Yves Savourel        */
/*---------------------------------------------------------------------------*/
/* This library is free software; you can redistribute it and/or modify it   */
/* under the terms of the GNU Lesser General Public License as published by  */
/* the Free Software Foundation; either version 2.1 of the License, or (at   */
/* your option) any later version.                                           */
/*                                                                           */
/* This library is distributed in the hope that it will be useful, but       */
/* WITHOUT ANY WARRANTY; without even the implied warranty of                */
/* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser   */
/* General Public License for more details.                                  */
/*                                                                           */
/* You should have received a copy of the GNU Lesser General Public License  */
/* along with this library; if not, write to the Free Software Foundation,   */
/* Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA               */
/*                                                                           */
/* See also the full LGPL text here: http://www.gnu.org/copyleft/lesser.html */
/*===========================================================================*/

package net.sf.okapi.apptest.resource;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import net.sf.okapi.apptest.common.IResource;

/**
 * This class implements the methods to manipulate a unit of extracted text.
 * <p>The TextUnit object includes a source content ({@link #getSourceContent()})
 * and one or more target content ({@link #getTargets()}). You can access
 * the first target with {@link #getTarget()}.
 */
public class TextUnit extends BaseResource implements IReferenceable {

	protected String                        name;
	protected String                        type;
	protected boolean                       isTranslatable;
	protected boolean                       preserveWS;
	protected IResource                     parent;
	protected LocaleData                    source;
	protected ArrayList<LocaleData>         targets;
	protected Hashtable<String, String>     propList;
	protected Hashtable<String, IExtension> extList;
	protected boolean isReference = false;

	/**
	 * Creates a TextUnit object with an empty source content and no target content.
	 */
	public TextUnit () {
		isTranslatable = true;
		source = new LocaleData(this);
		targets = new ArrayList<LocaleData>();
		targets.add(null);
	}

	/**
	 * Creates a TextUnit object with given ID and source text.
	 * @param id the ID to use.
	 * @param sourceText The source text to use.
	 */
	public TextUnit (String id,
		String sourceText)
	{
		create(id, sourceText, false);
	}

	/**
	 * Creates a TextUnit object with given ID and source text, and a flag to indicate if
	 * it is a reference or not.
	 * @param id the ID to use.
	 * @param sourceText The source text to use.
	 * @param isReference Indicates if this text unit is a reference or not.
	 */
	public TextUnit (String id,
		String sourceText,
		boolean isReference)
	{
		create(id, sourceText, isReference);
	}
	
	private void create (String id,
		String sourceText,
		boolean isReference)
	{
		isTranslatable = true;
		this.id = id;
		this.isReference = isReference;
		source = new LocaleData(this);
		source.container = new TextContainer(this);
		if ( sourceText != null ) source.container.append(sourceText);
		source.container.id = id;
		targets = new ArrayList<LocaleData>();
		targets.add(null);
	}

	/** 
	 * Get the text representation of the source of this text unit.
	 * @return The text representation of the source of this text unit.
	 */
	@Override
	public String toString () {
		return source.container.toString();
	}
	
	public void setIsReference (boolean value) {
		isReference = value;
	}
	
	public boolean isReference () {
		return isReference;
	}
	
	public boolean hasReference () {
		// If source has references, target does too
		return source.container.hasReference();
	}
	
	/**
	 * Indicates if the source content of this resource is empty.
	 * @return True if the source content is empty, false otherwise.
	 */
	public boolean isEmpty () {
		return source.container.isEmpty();
	}

	public String getName () {
		if ( name == null ) return "";
		return name;
	}

	public void setName (String value) {
		name = value;
	}

	public String getType () {
		if ( type == null ) return "";
		return type;
	}

	public void setType (String value) {
		type = value;
	}

	public String getProperty (String name) {
		if ( propList == null ) return null;
		return propList.get(name);
	}

	public void setProperty (String name,
		String value)
	{
		if ( propList == null ) propList = new Hashtable<String, String>();
		propList.put(name, value);
	}

	public Hashtable<String, String> getProperties () {
		if ( propList == null ) propList = new Hashtable<String, String>();
		return propList;
	}

	public IExtension getExtension (String name) {
		if ( extList == null ) return null;
		return extList.get(name);
	}

	public void setExtension (String name,
		IExtension value)
	{
		if ( extList == null ) extList = new Hashtable<String, IExtension>();
		extList.put(name, value);
	}

	public Hashtable<String, IExtension> getExtensions () {
		if ( extList == null ) extList = new Hashtable<String, IExtension>();
		return extList;
	}

	public boolean preserveWhitespaces () {
		return preserveWS;
	}

	public void setPreserveWhitespaces (boolean value) {
		preserveWS = value;
	}

	public boolean isTranslatable () {
		return isTranslatable;
	}

	public void setIsTranslatable (boolean value) {
		isTranslatable = value;
	}

	public IResource getParent () {
		return parent;
	}

	public void setParent (IResource value) {
		parent = value;
	}

	/**
	 * Gets the source object of the resource.
	 * @return The source object of the resource, never null.
	 */
	public LocaleData getSource () {
		return source;
	}

	/**
	 * Sets the source object of the resource.
	 * @param value The new source object. Must not be null.
	 */
	public void setSource (LocaleData value) {
		if ( value == null ) throw new InvalidParameterException();
		source = value;
		if ( source.container == null ) throw new InvalidParameterException();
		source.container.parent = this;
	}
	
	/**
	 * Gets the source content of the resource.
	 * @return The source content of the resource.
	 */
	public TextContainer getSourceContent () {
		return source.container;
	}
	
	/**
	 * Sets the source content of the resource.
	 * @param value The source content. Must not be null.
	 */
	public void setSourceContent (TextContainer value) {
		if ( value == null ) throw new InvalidParameterException();
		value.parent = this;
		source.container = value;
	}
	
	/**
	 * Indicates if the text unit has a (first) target.
	 * @return True if the unit has a (first) target, false otherwise.
	 */
	public boolean hasTarget () {
		if (( targets.get(0) == null )
			|| ( !(targets.get(0).container != null) )) return false;
		return !targets.get(0).container.isEmpty();
	}

	/**
	 * Gets the (first) target object of the resource. You can use
	 * {@link #hasTarget()} to know if there is a target available.
	 * @return The (first) target object, or null.
	 */
	public LocaleData getTarget () {
		return targets.get(0); 
	}
	
	/**
	 * Sets the (first) target object of the resource.
	 * @param value The object to assign (can be null).
	 */
	public void setTarget (LocaleData value) {
		if ( value == null ) throw new InvalidParameterException();
		targets.set(0, value);
	}
	
	/**
	 * Gets the (first) target content of the resource. You can use
	 * {@link #hasTarget()} to know if there is a target available.
	 * @return The (first) target content, or null.
	 */
	public TextContainer getTargetContent () {
		if ( targets.get(0) == null ) return null;
		return targets.get(0).container;
	}

	/**
	 * Sets the (first) target content of the resource. If there is no target
	 * object yet, it is created automatically.
	 * @param value The new content (can be null).
	 */
	public void setTargetContent (TextContainer value) {
		if ( targets.get(0) == null ) {
			targets.set(0, new LocaleData(this));
		}
		targets.get(0).container = value;
	}

	/**
	 * Gets the list of the target objects of the resource.
	 * @return The list of the targets.
	 */
	//For now the only way to access all targets
	public List<LocaleData> getTargets () {
		return targets;
	}
	
}
