/*===========================================================================
  Copyright (C) 2008-2010 by the Okapi Framework contributors
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

package net.sf.okapi.steps.xliffkit.common.persistence;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ReferenceResolver {
	private int idCounter = 0;
	private int rootId = 0;
	private static ConcurrentHashMap<Object, Integer> refIdLookup = new ConcurrentHashMap<Object, Integer>();
	private static ConcurrentHashMap<Integer, Integer> rootLookup = new ConcurrentHashMap<Integer, Integer>();
	private Map<Integer, Set<Integer>> references = new LinkedHashMap<Integer, Set<Integer>>();
	
	public void reset() {
		idCounter = 0;
		rootId = 0;
		refIdLookup.clear();
		rootLookup.clear();
		references.clear();
	}
	
	public int generateRefId() {
		return ++idCounter;
	}

	public int getRefIdForObject(Object obj) {
		return (refIdLookup.containsKey(obj)) ? refIdLookup.get(obj) : 0;
	}
	
	public int getRootId(int refId) {
		return (rootLookup.containsKey(refId)) ? rootLookup.get(refId) : 0;
	}

	public void setRefIdForObject(Object obj, int refId) {
		refIdLookup.put(obj, refId);
		rootLookup.put(refId, rootId);
	}
	
	public void addReference(int parentRefId, int childRefId) {
		int parentRoot = getRootId(parentRefId);
		int childRoot = getRootId(childRefId);
		
		if (parentRoot == childRoot && parentRoot != 0) return;
		
		Set<Integer> list = references.get(parentRefId);
		if (list == null) {
			list = new HashSet<Integer>();
			references.put(parentRefId, list);
		}		
		list.add(childRefId);
	}

	public Map<Integer, Set<Integer>> getReferences() {
		return references;
	}

	public void setRootId(int rootId) {
		this.rootId = rootId;		
	}
}
