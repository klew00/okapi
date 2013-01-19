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

package net.sf.okapi.lib.persistence;

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
//	private final String MSG3 = "ReferenceResolver.createAntiBean: anti-bean class mismatch (actual: %s, expected: %s)";
	private final String MSG3 = "ReferenceResolver: object references are broken, reference to a non-existing object";
	private final String MSG4 = "ReferenceResolver.createAntiBean: objClassRef cannot be null";
	private final String MSG5 = "ReferenceResolver.createAntiBean: refId cannot be 0";
	private final static String MSG6 = "ReferenceResolver: idCounter overflow";
	
	private static long idCounter = 0;
	private long rootId = 0;
	private IPersistenceSession session;

	private static Map<Object, Long> refIdLookup = new ConcurrentHashMap<Object, Long>();
	private static Map<Long, Object> objectLookup = new ConcurrentHashMap<Long, Object>();
	private static Map<Long, Long> rootLookup = new ConcurrentHashMap<Long, Long>();
	private static Map<Object, IPersistenceBean<?>> beanCache = new ConcurrentHashMap<Object, IPersistenceBean<?>>();
	private static Map<Long, IPersistenceBean<?>> beanCache2 = new ConcurrentHashMap<Long, IPersistenceBean<?>>();
	private Map<Long, Set<Long>> references = new LinkedHashMap<Long, Set<Long>>();
	private Set<Set<Long>> frames = new TreeSet<Set<Long>>(new Comparator<Set<Long>>() {
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
	private Map<Long, Set<Long>> frameLookup = new ConcurrentHashMap<Long, Set<Long>>();
	private List<Object> serialized = new ArrayList<Object>();

	public ReferenceResolver(IPersistenceSession session) {
		super();
		this.session = session;
	}
	
	public void reset() {
		//idCounter = 0; //!!! Sessions are not allowed to reset the counter
		rootId = 0;
		refIdLookup.clear();
		objectLookup.clear();
		rootLookup.clear();
		beanCache.clear();
		beanCache2.clear();
		references.clear();
		frames.clear();
		frameLookup.clear();
		serialized.clear();
	}
	
	public void releaseObject(Object obj) {
		long refId = refIdLookup.remove(obj);
		beanCache.remove(obj);
		if (refId != 0) {
			objectLookup.remove(refId);
			rootLookup.remove(refId);
			beanCache2.remove(refId);
		}
	}
	
	public void removeFrame(Set<Long> frame) {
		frames.remove(frame);
		for (Long rid : frame)
			frameLookup.remove(rid);
	}
	
	public static long generateRefId() {
		if (idCounter == Long.MAX_VALUE)
			throw new RuntimeException(MSG6);
		return ++idCounter;
	}

	public long getRefIdForObject(Object obj) {
		return (refIdLookup.containsKey(obj)) ? refIdLookup.get(obj) : 0;
	}
	
	public Object getObject(long refId) {
		return objectLookup.get(refId);
	}
	
	public long getRootId(long refId) {
		if (refId < 0) return refId; // anti-bean is the root for itself 
		return (rootLookup.containsKey(refId)) ? rootLookup.get(refId) : 0;
	}

	public void setRefIdForObject(Object obj, long refId) {
		if (obj == null) return;
		if (refId == 0)
			throw new RuntimeException(MSG3);
		
		refIdLookup.put(obj, refId);  // refIdLookup.get(obj)
		rootLookup.put(refId, rootId);
		objectLookup.put(refId, obj);
	}
	
	public void setReference(long parentRefId, long childRefId) {
		if (parentRefId == 0 || childRefId == 0)
			throw new RuntimeException(MSG3);
			
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
	
	public Set<Long> getFrame(long refId) {
		return frameLookup.get(refId);
	}
	
	public void updateFrames() {
		
//		Set<Set<Long>> frameSet = new TreeSet<Set<Long>>(new Comparator<Set<Long>>() {
//			@Override
//			public int compare(Set<Long> frame1, Set<Long> frame2) {
//				if (frame1 == null || frame2 == null) return 0;
//				if (frame1.size() < 1 || frame2.size() < 1) return 0;
//				
//				// Frames are sorted by the first element
//				long e1 = frame1.iterator().next();
//				long e2 = frame2.iterator().next();
//				
//				if (e1 < e2) 
//					return -1;
//				else 
//					if (e1 > e2) return 1;
//				else
//					return 0;
//			}
//		});
		
		frames.clear();
		frameLookup.clear();
		
		for (Long parentRefId : references.keySet()) {
			Set<Long> childRefs = references.get(parentRefId);
			if (childRefs == null) continue;
			
			for (Long childRefId : childRefs) {
				
				long parentRoot = getRootId(parentRefId);
				long childRoot = getRootId(childRefId);

				if (parentRoot == 0 || childRoot == 0)
					throw new RuntimeException(MSG3);
				
				if (parentRoot == childRoot) continue; // refs within same bean
				
				Set<Long> parentFrame = getFrame(parentRoot);
				Set<Long> childFrame = getFrame(childRoot);
				
				if (parentFrame == childFrame && parentFrame != null) continue; // already both in the same frame 
			
				if (parentFrame == null && childFrame == null) { // 00
					Set<Long> frame = new TreeSet<Long>(); // default Long comparator is used
					frame.add(parentRoot);
					frame.add(childRoot);
					frames.add(frame);
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
					frames.remove(childFrame);
					frameLookup.remove(childRoot);
				}
			}
		}					
	}

	public void setRootId(long rootId) {
		this.rootId = rootId;		
	}
	
	public <T> IPersistenceBean<T> createBean(Class<T> classRef) {
		Class<IPersistenceBean<T>> beanClass = 
			session.getBeanClass(classRef);
		
		if (beanClass == null)
			throw(new RuntimeException(String.format(MSG1, classRef.getName())));
		
		IPersistenceBean<T> bean = null; 
		try {
			bean = ClassUtil.instantiateClass(beanClass);
		} catch (Exception e) {
			throw new RuntimeException(String.format(MSG2, beanClass.getName()), e);
		}
		
		return bean;
	}

	public void cacheBean(Object obj, IPersistenceBean<?> bean) {		
		beanCache.put(obj, bean);
	}
	
	public void cacheBean(IPersistenceBean<?> bean) {
		if (bean == null) return;
		beanCache2.put(bean.getRefId(), bean);
	}

	public IPersistenceBean<?> uncacheBean(Object obj) {
		IPersistenceBean<?> bean = beanCache.get(obj);
		beanCache.remove(obj); // The caller takes ownership of the object ref
		return bean;
	}
	
	public IPersistenceBean<?> uncacheBean(Long refId) {
		IPersistenceBean<?> bean = beanCache2.get(refId);
		beanCache2.remove(refId);
		return bean;
	}

	public List<List<Long>> getFrames() {
		List<List<Long>> frames = new ArrayList<List<Long>>();
		
		for (Set<Long> frame : this.frames) {
			List<Long> frameList = new ArrayList<Long>(frame);
			frames.add(frameList);
		}
		return (List<List<Long>>) frames;
	}

	public void setFrames(List<List<?>> frames) {
		frameLookup.clear();
		
		for (List<?> frame : frames) {
			Set<Long> newFrame = new TreeSet<Long>();
			this.frames.add(newFrame);
			Long rid = null;
			
			// The actual framework can read the frames as list of int of long 
			for (Object refId : frame) {
				if (refId instanceof Long) 
					rid = (Long) refId;
				else if (refId instanceof Integer)
					rid = new Long((Integer) refId);
				newFrame.add(rid);
				frameLookup.put(rid, newFrame);
			}				
		}
				
//		this.frames = frames;
//		if (frames == null) return;		
//		if (frames.size() == 0) return;
//		
//		// Check the actual element type
//		List<?> frame = frames.get(0);
//		if (frame.size() == 0) return;
//		Object refId = frame.get(0);
//		if (refId instanceof Integer)
//			System.out.println("int");
//		else
//			if (refId instanceof Long)
//				System.out.println("long");
//			else
//				System.out.println("something else");
		
//		if (frames instanceof List<List<Integer>>)
//		Class<List<List<Integer>>> intListClass;
//		if (intListClass.isInstance(frames));
		
//		for (List<?> frame : frames) {
//			for (Object refId : frame)
//				frameLookup.put(refId, new HashSet<Long>(frame));
//		}
	}

	/**
	 * Checks if all beans in a given frame have been processed and their core objects are found in the cache. 
	 * @param frame the given frame
	 * @return true if all beans of the frame are processed
	 */
	public boolean isFrameAvailable(Set<Long> frame) {
		for (Long refId : frame)
			if (!beanCache2.containsKey(refId)) return false;
		
		return true;
	}

	/**
	 * Anti-beans are used to serialize a reference to a root object that has already been serialized as part of another bean.
	 * The anti-beans are instances of the right bean class corresponding to the given object, which refId is the inverted value
	 * of the given bean's refId. Fields or the resulting anti-bean contain default values. 
	 * @param objClassRef
	 * @param refId
	 * @return
	 */
	public <T> IPersistenceBean<T> createAntiBean(Class<T> objClassRef, long refId) {
		if (objClassRef == null)
			throw new IllegalArgumentException(MSG4);
		if (refId == 0)
			throw new IllegalArgumentException(MSG5);
		
		IPersistenceBean<T> res = createBean(objClassRef);
//		if (!res.getClass().equals(bean.getClass()))
//			throw new RuntimeException(String.format(MSG3, ClassUtil.getQualifiedClassName(res.getClass()),
//				ClassUtil.getQualifiedClassName(bean.getClass())));		
		if (refId > 0) refId = -refId;
		
		res.setRefId(refId);
		setReference(refId, -refId);
		return res;
	}

	public boolean isSerialized(Object obj) {
		return serialized.contains(obj);
	}

	public void setSerialized(Object obj) {
		serialized.add(obj);
	}
}
