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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

import net.sf.okapi.common.ClassUtil;

public class ReferenceResolver {
	private final String MSG1 = "ReferenceResolver: class %s is not registered";
	private final String MSG2 = "ReferenceResolver: cannot instantiate %s";
	
	private static long idCounter = 0;
	private long rootId = 0;
	private static Map<Object, Long> refIdLookup = new ConcurrentHashMap<Object, Long>();
	private static Map<Long, Long> rootLookup = new ConcurrentHashMap<Long, Long>();
	private static Map<Object, IPersistenceBean> beanCache = new ConcurrentHashMap<Object, IPersistenceBean>();
	private Map<Long, Set<Long>> references = new LinkedHashMap<Long, Set<Long>>();
	private List<List<Long>> frames = new ArrayList<List<Long>>();	
	private Map<Long, Set<Long>> frameLookup = new ConcurrentHashMap<Long, Set<Long>>();
	
	public void reset() {
		//idCounter = 0; //!!! Sessions are not allowed to reset the counter
		rootId = 0;
		refIdLookup.clear();
		rootLookup.clear();
		references.clear();
		frames.clear();
		frameLookup.clear();
	}
	
	public static long generateRefId() {
		return ++idCounter; // Long.MAX_VALUE
	}

	public long getRefIdForObject(Object obj) {
		return (refIdLookup.containsKey(obj)) ? refIdLookup.get(obj) : 0;
	}
	
	public long getRootId(long refId) {
		return (rootLookup.containsKey(refId)) ? rootLookup.get(refId) : 0;
	}

	public void setRefIdForObject(Object obj, long refId) {
		refIdLookup.put(obj, refId);  // refIdLookup.get(obj)
		rootLookup.put(refId, rootId);
	}
	
	public void addReference(long parentRefId, long childRefId) {
		Set<Long> list = references.get(parentRefId);
		if (list == null) {
			list = new HashSet<Long>();
			references.put(parentRefId, list);
		}		
		list.add(childRefId);
	}

	public Map<Long, Set<Long>> getReferences() {
		return references;
	}
	
	private Set<Long> getFrame(long refId) {
		return (frameLookup.containsKey(refId)) ? frameLookup.get(refId) : null;
	}
	
	public void updateFrames() {
		
		// TODO Handle idCounter overflow, when it wraps to negatives
		
		Set<Set<Long>> frameSet = new TreeSet<Set<Long>>(new Comparator<Set<Long>>() {
			@Override
			public int compare(Set<Long> frame1, Set<Long> frame2) {
				if (frame1 == null || frame2 == null) return 0;
				if (frame1.size() < 1 || frame2.size() < 1) return 0;
				
				// Frames are sorted by the first element
				long e1 = frame1.iterator().next();
				long e2 = frame2.iterator().next();
				
				if (e1 < e2) 
					return -1;
				else 
					if (e1 > e2) return 1;
				else
					return 0;
			}
		});
		
		for (Long parentRefId : references.keySet()) {
			Set<Long> childRefs = references.get(parentRefId);
			if (childRefs == null) continue;
			
			for (Long childRefId : childRefs) {
				
				long parentRoot = getRootId(parentRefId);
				long childRoot = getRootId(childRefId);

				if (parentRoot == childRoot) continue; // refs within same bean
				
				Set<Long> parentFrame = getFrame(parentRoot);
				Set<Long> childFrame = getFrame(childRoot);
				
				if (parentFrame == childFrame && parentFrame != null) continue; // already both in the same frame 
			
				if (parentFrame == null && childFrame == null) { // 00
					Set<Long> frame = new TreeSet<Long>(); // default Long comparator is used
					frame.add(parentRoot);
					frame.add(childRoot);
					frameSet.add(frame);
					frameLookup.put(parentRoot, frame);
					frameLookup.put(childRoot, frame);
				} else 
				if (parentFrame == null && childFrame != null) { // 01
					childFrame.add(parentRoot);
					frameLookup.put(parentRoot, childFrame);
				} 
				else 
				if (parentFrame != null && childFrame == null) { // 10
					parentFrame.add(childRoot);
					frameLookup.put(childRoot, parentFrame);
				} 
				else 
				if (parentFrame != null && childFrame != null) { // 11
					// Merge frames
					parentFrame.addAll(childFrame);
					frameSet.remove(childFrame);
					frameLookup.remove(childRoot);
				}
			}
		}			
		for (Set<Long> frame : frameSet) {
			List<Long> frameList = new ArrayList<Long>();
			((ArrayList<List<Long>>)frames).add(frameList);
			frameList.addAll(frame);
		}
	}

	public void setRootId(long rootId) {
		this.rootId = rootId;		
	}
	
	public IPersistenceBean createBean(Class<?> classRef) {
		Class<? extends IPersistenceBean> beanClass = 
			BeanMapper.getBeanClass(classRef);
		
		if (beanClass == null)
			throw(new RuntimeException(String.format(MSG1, classRef.getName())));
		
		IPersistenceBean bean = null; 
		try {
			bean = ClassUtil.instantiateClass(beanClass);
		} catch (Exception e) {
			throw new RuntimeException(String.format(MSG2, beanClass.getName()), e);
		}
		
		return bean;
	}

	public void cacheBean(Object obj, IPersistenceBean bean) {		
		beanCache.put(obj, bean);
	}

	public IPersistenceBean uncacheBean(Object obj) {
		IPersistenceBean bean = beanCache.get(obj);
		beanCache.remove(obj); // The caller takes ownership of the object ref
		return bean;
	}

	public List<List<Long>> getFrames() {
		return frames;
	}

	public void setFrames(List<List<Long>> frames) {
		frameLookup.clear();
		this.frames = frames;
	}
}
