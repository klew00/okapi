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
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.okapi.common.ClassUtil;
import net.sf.okapi.common.Util;

public class BeanMapper {
	
	private static final String MAPPER_NOT_INIT = "BeanMapper: bean mapping is not initialized";
	private static final String MAPPER_UNK_CLASS = "BeanMapper: unknown class: %s";		
	private static final String MAPPER_EMPTY_REF = "BeanMapper: class reference cannot be empty";
	private static final String MAPPER_NOT_REG = "No bean class registered for %s, using %s for %s instead.";
	
	private static final String OBJ_MAPPER_NOT_INIT = "BeanMapper: object mapping is not initialized";
	private static final String OBJ_MAPPER_EMPTY_REF = "BeanMapper: bean class reference cannot be empty";
	
	private static final String PROXIES_CANT_INST = "BeanMapper: cannot instantiate a proxy for %s";
	private static final String PROXIES_NOT_INIT = "BeanMapper: proxy mapping is not initialized";	
	
	// !!! LinkedHashMap to preserve registration order
	private LinkedHashMap<Class<?>, Class<? extends IPersistenceBean<?>>> beanClassMapping;
	private HashMap<Class<? extends IPersistenceBean<?>>, Class<?>> objectClassMapping;
	private ArrayList<Class<?>> loggedClasses; 
	private ArrayList<String> loggedClassNames;
	private ConcurrentHashMap<String, IPersistenceBean<?>> proxies; // used in ref resolution
	private IPersistenceSession session;
	private final Logger LOGGER = LoggerFactory.getLogger(getClass());
	
	public BeanMapper(IPersistenceSession session) {
		this.session = session;
		beanClassMapping = new LinkedHashMap<Class<?>, Class<? extends IPersistenceBean<?>>> ();
		objectClassMapping = new HashMap<Class<? extends IPersistenceBean<?>>, Class<?>> ();
		proxies = new ConcurrentHashMap<String, IPersistenceBean<?>>();
		loggedClasses = new ArrayList<Class<?>>();
		loggedClassNames = new ArrayList<String>();
	}

	public void reset() {
		beanClassMapping.clear();
		objectClassMapping.clear();
		proxies.clear();
		loggedClasses.clear(); 
		loggedClassNames.clear();		
	}
	
	public void registerBean(
			Class<?> classRef, 
			Class<? extends IPersistenceBean<?>> beanClassRef) {
		if (classRef == null || beanClassRef == null)
			throw(new IllegalArgumentException());
		
		if (beanClassMapping == null)
			throw(new RuntimeException(MAPPER_NOT_INIT));
		
		beanClassMapping.put(classRef, beanClassRef);
				
		if (objectClassMapping == null)
			throw(new RuntimeException(OBJ_MAPPER_NOT_INIT));
		
		objectClassMapping.put(beanClassRef, classRef);
		
		if (proxies == null)
			throw(new RuntimeException(PROXIES_NOT_INIT));
		
//		String beanClassName = ClassUtil.getQualifiedClassName(beanClassRef);
//		try {
//			proxies.put(beanClassName, ClassUtil.instantiateClass(beanClassRef));
//		} catch (Exception e) {
//			throw new RuntimeException(String.format(PROXIES_CANT_INST, beanClassName), e);
//		}
		
		String objClassName = ClassUtil.getQualifiedClassName(classRef);
		String beanClassName = ClassUtil.getQualifiedClassName(classRef);
		try {
			IPersistenceBean<?> proxy = ClassUtil.instantiateClass(beanClassRef);
			if (proxy != null)
				proxy.setRefId(0); // to distinguish from regular beans 
			
			proxies.put(objClassName, proxy);
		} catch (Exception e) {
			throw new RuntimeException(String.format(PROXIES_CANT_INST, beanClassName), e);
		}		
	}
	
	@SuppressWarnings("unchecked")
	public <T> Class<IPersistenceBean<T>> getBeanClass(Class<T> classRef) {
		if (classRef == null)
			throw(new IllegalArgumentException(MAPPER_EMPTY_REF));
	
		if (beanClassMapping == null)
			throw(new RuntimeException(MAPPER_NOT_INIT));
		
		Class<IPersistenceBean<T>> beanClass = (Class<IPersistenceBean<T>>) beanClassMapping.get(classRef);
				
		// If not found explicitly, try to find a matching bean
		if (beanClass == null) {
			if (IPersistenceBean.class.isAssignableFrom(classRef)) // A bean is a bean for itself 
				beanClass = (Class<IPersistenceBean<T>>) classRef;
			else
			for (Class<?> cls : beanClassMapping.keySet())
				if (cls.isAssignableFrom(classRef)) {
					beanClass = (Class<IPersistenceBean<T>>) beanClassMapping.get(cls);
					if (session.getState() == SessionState.WRITING && !loggedClasses.contains(classRef)) {
						loggedClasses.add(classRef);
						LOGGER.warn(String.format(MAPPER_NOT_REG,
								ClassUtil.getQualifiedClassName(classRef),
								ClassUtil.getQualifiedClassName(beanClass),
								ClassUtil.getQualifiedClassName(cls)));
					}					
					break;
				}	
			if (beanClass == null && !loggedClasses.contains(classRef)) {
				loggedClasses.add(classRef);
				LOGGER.warn(String.format("No bean class registered for %s", ClassUtil.getQualifiedClassName(classRef)));
			}				
		}
		return beanClass;		
	}
	
	public Class<?> getObjectClass(Class<? extends IPersistenceBean<?>> beanClassRef) {
		if (beanClassRef == null)
			throw(new IllegalArgumentException(OBJ_MAPPER_EMPTY_REF));
	
		if (objectClassMapping == null)
			throw(new RuntimeException(OBJ_MAPPER_NOT_INIT));
		
		return objectClassMapping.get(beanClassRef);		
	}
	
	public Class<?> getClass(String objClassName) {
		return ClassUtil.getClass(NamespaceMapper.getMapping(objClassName));
	}
	
	public IPersistenceBean<?> getProxy(String objClassName) {
		if (Util.isEmpty(objClassName)) return null;
		
		IPersistenceBean<?> proxy = proxies.get(NamespaceMapper.getMapping(objClassName)); 
		if (proxy == null && !loggedClassNames.contains(objClassName)) {
			loggedClassNames.add(objClassName);
			LOGGER.warn(String.format("No proxy found for %s", objClassName));
		}
		
		return proxy;
	}
	
	public IPersistenceBean<?> getProxy(Class<?> objClassRef) {
		if (objClassRef == null) return null;
				
//		Class<? extends IPersistenceBean> beanClassRef = getBeanClass(objClassRef);
//		if (beanClassRef == null) return null;
//		return getProxy(beanClassRef.getName());
		return getProxy(objClassRef.getName());
	}
	
	public Class<? extends IPersistenceBean<?>> getBeanClass(String className) {
			
		Class<? extends IPersistenceBean<?>> res = null;
		try {
			res = getBeanClass(Class.forName(NamespaceMapper.getMapping(className)));
		} catch (ClassNotFoundException e) {
			throw(new RuntimeException(String.format(MAPPER_UNK_CLASS, className)));
		}
		return res;		
	}
}
