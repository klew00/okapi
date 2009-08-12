/*===========================================================================
  Copyright (C) 2008-2009 by the Okapi Framework contributors
-----------------------------------------------------------------------------
  This library is free software; you can redistribute it and/or modify it 
  under the terms of the GNU Lesser General Public License as published by 
  the Free Software Foundation; either version 2.1 of the License, or (at 
  your option) any later version.

  This library is distributed in the hope that it will be useful, but 
  WITHOUT ANY WARRANTY; without even the implied warranty of 
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser 
  General Public License for more details.

  You should have received a copy of the GNU Lesser General Public License 
  along with this library; if not, write to the Free Software Foundation, 
  Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA

  See also the full LGPL text here: http://www.gnu.org/copyleft/lesser.html
===========================================================================*/

package net.sf.okapi.common.skeleton;

import java.util.ArrayList;
import java.util.Set;

import net.sf.okapi.common.IResource;
import net.sf.okapi.common.ISkeleton;
import net.sf.okapi.common.annotation.IAnnotation;
import net.sf.okapi.common.resource.INameable;
import net.sf.okapi.common.resource.IReferenceable;
import net.sf.okapi.common.resource.Property;
import net.sf.okapi.common.resource.StartGroup;

class StorageList extends ArrayList<IResource>
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
	
	public int getReferenceCount () {
		return startGroup.getReferenceCount();
	}
	
	public void setReferenceCount (int value) {
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

	public <A extends IAnnotation> A getAnnotation (Class<A> annotationType) {
		return null; //TODO: Fix this (doesn't compile on command-line) startGroup.getAnnotation(type);
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

	public boolean isTranslatable () {
		return startGroup.isTranslatable();
	}

	public void setIsTranslatable (boolean value) {
		// Not implemented: read-only info
	}

	public String getType () {
		return startGroup.getType();
	}

	public void setType (String value) {
		// Not implemented: read-only info
	}

	public boolean preserveWhitespaces () {
		return startGroup.preserveWhitespaces();
	}

	public void setPreserveWhitespaces (boolean value) {
		// Not implemented: read-only info
	}

	public String getMimeType() {
		return startGroup.getMimeType();
	}

	public void setMimeType(String value) {
		// Not implemented: read-only info		
	}

	public boolean hasProperty (String name) {
		return startGroup.hasProperty(name);
	}

	public boolean hasSourceProperty(String name) {
		return startGroup.hasSourceProperty(name);
	}

	public void removeProperty (String name) {
		startGroup.removeProperty(name);
	}

	public void removeSourceProperty(String name) {
		startGroup.removeSourceProperty(name);
	}

	public void removeTargetProperty(String language, String name) {
		startGroup.removeTargetProperty(language, name);
	}

}
