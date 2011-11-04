/*===========================================================================
  Copyright (C) 2010-2011 by the Okapi Framework contributors
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

package net.sf.okapi.filters.idml;

import java.util.HashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import net.sf.okapi.common.IResource;
import net.sf.okapi.common.ISkeleton;

public class IDMLSkeleton implements ISkeleton {

	public final static String NODEREMARKER = "SKLREF";

	private ZipFile original; // Used for startDocument
	private ZipEntry entry; // Used for Startgroup of story
	private Document doc; // Used for Startgroup of story
	private Node topNode; // Used for TextUnit
	private Node scopeNode; // Used for TextUnit
	private HashMap<String, NodeReference> refs; // Used for TextUnit
	private String[] movedParts; // Temporary moved outside the content
	
	public IDMLSkeleton (ZipFile original) {
		this.original = original;
	}
	
	public IDMLSkeleton (ZipEntry entry,
		Document doc)
	{
		this.entry = entry;
		this.doc = doc;
	}
	
	public IDMLSkeleton (Node topNode,
		Node scopeNode)
	{
		this.topNode = topNode;
		this.scopeNode = scopeNode;
	}
	
	public void addReferenceNode (String id,
		NodeReference ref)
	{
		if ( refs == null ) {
			refs = new HashMap<String, NodeReference>();
		}
		refs.put(id, ref);
	}
	
	public void addMovedParts (String[] movedParts) {
		this.movedParts = movedParts;
	}
	
	public String[] getMovedParts () {
		return movedParts;
	}
	
	public boolean hasReferences () {
		return (( refs != null ) && ( refs.size() > 0 ));
	}
	
	public HashMap<String, NodeReference> getReferences () {
		return refs;
	}
	
	public ZipFile getOriginal () {
		return original;
	}
	
	public ZipEntry getEntry () {
		return entry;
	}

	public Document getDocument () {
		return doc;
	}

	public Node getTopNode () {
		return topNode;
	}

	public Node getScopeNode () {
		return scopeNode;
	}

	/**
	 * Returns a shallow copy of this object.
	 */
	@Override
	public ISkeleton clone(IResource parent) {
		IDMLSkeleton newSkel = new IDMLSkeleton(original);
		newSkel.entry = entry;
		newSkel.doc = doc;
		newSkel.topNode = topNode;
		newSkel.scopeNode = scopeNode;
		newSkel.refs = refs;
		newSkel.movedParts = movedParts;
		return newSkel;
	}
}
 