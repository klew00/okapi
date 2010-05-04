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
import java.util.LinkedHashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import net.sf.okapi.common.ClassUtil;
import net.sf.okapi.common.Util;

public class BeanMapper {
	
	private static final String MAPPER_NOT_INIT = "BeanMapper: bean mapping is not initialized";
	private static final String MAPPER_UNK_CLASS = "BeanMapper: unknown class: %s";		
	private static final String MAPPER_EMPTY_REF = "BeanMapper: class reference cannot be empty";
	
	private static final String OBJ_MAPPER_NOT_INIT = "BeanMapper: obhect mapping is not initialized";
	private static final String OBJ_MAPPER_EMPTY_REF = "BeanMapper: bean class reference cannot be empty";
	
	private static final String PROXIES_CANT_INST = "BeanMapper: cannot instantiate a proxy for %s";
	private static final String PROXIES_NOT_INIT = "BeanMapper: proxy mapping is not initialized";
	
	// !!! LinkedHashMap to preserve registration order
	private static LinkedHashMap<Class<?>, Class<? extends IPersistenceBean<?>>> beanClassMapping;
	private static LinkedHashMap<Class<? extends IPersistenceBean<?>>, Class<?>> objectClassMapping;
	private static ArrayList<Class<?>> loggedClasses; 
	private static ConcurrentHashMap<String, IPersistenceBean<?>> proxies; // used in ref resolution
	private static final Logger LOGGER = Logger.getLogger(BeanMapper.class.getName());
	
	static {
		beanClassMapping = new LinkedHashMap<Class<?>, Class<? extends IPersistenceBean<?>>> ();
		objectClassMapping = new LinkedHashMap<Class<? extends IPersistenceBean<?>>, Class<?>> ();
		proxies = new ConcurrentHashMap<String, IPersistenceBean<?>>();
		loggedClasses = new ArrayList<Class<?>>();
	}

	public static void registerBean(
			Class<?> classRef, 
			Class<? extends IPersistenceBean<?>> beanClassRef) {
		if (classRef == null || beanClassRef == null)
			throw(new IllegalArgumentException());
		
		if (beanClassMapping == null)
			throw(new RuntimeException(MAPPER_NOT_INIT));
		
		// TODO Make sure if a bean for already registered class was registered later, the later bean takes precedence
		// HashMap.put(): "If the map previously contained a mapping for the key, the old value is replaced". Test it.
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
	public static <T> Class<IPersistenceBean<T>> getBeanClass(Class<T> classRef) {
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
					if (!loggedClasses.contains(classRef)) {
						loggedClasses.add(classRef);
						LOGGER.warning(String.format("No bean class registered for %s, using %s for %s instead.", 
								ClassUtil.getQualifiedClassName(classRef),
								ClassUtil.getQualifiedClassName(beanClass),
								ClassUtil.getQualifiedClassName(cls)));
					}					
					break;
				}	
			if (beanClass == null && !loggedClasses.contains(classRef)) {
				loggedClasses.add(classRef);
				LOGGER.warning(String.format("No bean class registered for %s", ClassUtil.getQualifiedClassName(classRef)));
			}				
		}
		return beanClass;		
	}
	
	public static Class<?> getObjectClass(Class<? extends IPersistenceBean<?>> beanClassRef) {
		if (beanClassRef == null)
			throw(new IllegalArgumentException(OBJ_MAPPER_EMPTY_REF));
	
		if (objectClassMapping == null)
			throw(new RuntimeException(OBJ_MAPPER_NOT_INIT));
		
		return objectClassMapping.get(beanClassRef);		
	}
	
	public static IPersistenceBean<?> getProxy(String objClassName) {
		if (Util.isEmpty(objClassName)) return null;
		
		return proxies.get(objClassName);
	}
	
	public static IPersistenceBean<?> getProxy(Class<?> objClassRef) {
		if (objClassRef == null) return null;
				
//		Class<? extends IPersistenceBean> beanClassRef = getBeanClass(objClassRef);
//		if (beanClassRef == null) return null;
//		return getProxy(beanClassRef.getName());
		return getProxy(objClassRef.getName());
	}
	
	public static Class<? extends IPersistenceBean<?>> getBeanClass(String className) {
			
		Class<? extends IPersistenceBean<?>> res = null;
		try {
			res = getBeanClass(Class.forName(className));
		} catch (ClassNotFoundException e) {
			throw(new RuntimeException(String.format(MAPPER_UNK_CLASS, className)));
		}
		return res;		
	}			
}
