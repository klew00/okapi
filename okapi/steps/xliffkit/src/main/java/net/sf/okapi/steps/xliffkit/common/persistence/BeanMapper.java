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

import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import net.sf.okapi.common.ClassUtil;
import net.sf.okapi.common.Event;
import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.Range;
import net.sf.okapi.common.Util;
import net.sf.okapi.common.filterwriter.GenericFilterWriter;
import net.sf.okapi.common.filterwriter.TMXFilterWriter;
import net.sf.okapi.common.filterwriter.ZipFilterWriter;
import net.sf.okapi.common.resource.BaseNameable;
import net.sf.okapi.common.resource.BaseReferenceable;
import net.sf.okapi.common.resource.Code;
import net.sf.okapi.common.resource.Document;
import net.sf.okapi.common.resource.DocumentPart;
import net.sf.okapi.common.resource.Ending;
import net.sf.okapi.common.resource.InlineAnnotation;
import net.sf.okapi.common.resource.MultiEvent;
import net.sf.okapi.common.resource.Property;
import net.sf.okapi.common.resource.RawDocument;
import net.sf.okapi.common.resource.Segment;
import net.sf.okapi.common.resource.StartDocument;
import net.sf.okapi.common.resource.StartGroup;
import net.sf.okapi.common.resource.StartSubDocument;
import net.sf.okapi.common.resource.TargetPropertiesAnnotation;
import net.sf.okapi.common.resource.TextContainer;
import net.sf.okapi.common.resource.TextFragment;
import net.sf.okapi.common.resource.TextPart;
import net.sf.okapi.common.resource.TextUnit;
import net.sf.okapi.common.skeleton.GenericSkeleton;
import net.sf.okapi.common.skeleton.GenericSkeletonPart;
import net.sf.okapi.common.skeleton.ZipSkeleton;
import net.sf.okapi.filters.openxml.ConditionalParameters;
import net.sf.okapi.filters.openxml.OpenXMLZipFilterWriter;
import net.sf.okapi.filters.pensieve.PensieveFilterWriter;
import net.sf.okapi.filters.po.POFilterWriter;
import net.sf.okapi.steps.formatconversion.TableFilterWriter;
import net.sf.okapi.steps.xliffkit.common.persistence.beans.ListBean;
import net.sf.okapi.steps.xliffkit.common.persistence.beans.TypeInfoBean;
import net.sf.okapi.steps.xliffkit.common.persistence.beans.okapi.BaseNameableBean;
import net.sf.okapi.steps.xliffkit.common.persistence.beans.okapi.BaseReferenceableBean;
import net.sf.okapi.steps.xliffkit.common.persistence.beans.okapi.CodeBean;
import net.sf.okapi.steps.xliffkit.common.persistence.beans.okapi.ConditionalParametersBean;
import net.sf.okapi.steps.xliffkit.common.persistence.beans.okapi.DocumentBean;
import net.sf.okapi.steps.xliffkit.common.persistence.beans.okapi.DocumentPartBean;
import net.sf.okapi.steps.xliffkit.common.persistence.beans.okapi.EndingBean;
import net.sf.okapi.steps.xliffkit.common.persistence.beans.okapi.EventBean;
import net.sf.okapi.steps.xliffkit.common.persistence.beans.okapi.GenericFilterWriterBean;
import net.sf.okapi.steps.xliffkit.common.persistence.beans.okapi.GenericSkeletonBean;
import net.sf.okapi.steps.xliffkit.common.persistence.beans.okapi.GenericSkeletonPartBean;
import net.sf.okapi.steps.xliffkit.common.persistence.beans.okapi.InlineAnnotationBean;
import net.sf.okapi.steps.xliffkit.common.persistence.beans.okapi.InputStreamBean;
import net.sf.okapi.steps.xliffkit.common.persistence.beans.okapi.MultiEventBean;
import net.sf.okapi.steps.xliffkit.common.persistence.beans.okapi.ParametersBean;
import net.sf.okapi.steps.xliffkit.common.persistence.beans.okapi.PropertyBean;
import net.sf.okapi.steps.xliffkit.common.persistence.beans.okapi.RangeBean;
import net.sf.okapi.steps.xliffkit.common.persistence.beans.okapi.RawDocumentBean;
import net.sf.okapi.steps.xliffkit.common.persistence.beans.okapi.SegmentBean;
import net.sf.okapi.steps.xliffkit.common.persistence.beans.okapi.StartDocumentBean;
import net.sf.okapi.steps.xliffkit.common.persistence.beans.okapi.StartGroupBean;
import net.sf.okapi.steps.xliffkit.common.persistence.beans.okapi.StartSubDocumentBean;
import net.sf.okapi.steps.xliffkit.common.persistence.beans.okapi.TMXFilterWriterBean;
import net.sf.okapi.steps.xliffkit.common.persistence.beans.okapi.TargetPropertiesAnnotationBean;
import net.sf.okapi.steps.xliffkit.common.persistence.beans.okapi.TextContainerBean;
import net.sf.okapi.steps.xliffkit.common.persistence.beans.okapi.TextFragmentBean;
import net.sf.okapi.steps.xliffkit.common.persistence.beans.okapi.TextPartBean;
import net.sf.okapi.steps.xliffkit.common.persistence.beans.okapi.TextUnitBean;
import net.sf.okapi.steps.xliffkit.common.persistence.beans.okapi.ZipEntryBean;
import net.sf.okapi.steps.xliffkit.common.persistence.beans.okapi.ZipFileBean;
import net.sf.okapi.steps.xliffkit.common.persistence.beans.okapi.ZipFilterWriterBean;
import net.sf.okapi.steps.xliffkit.common.persistence.beans.okapi.ZipSkeletonBean;

public class BeanMapper {
	
	private static final String MAPPER_NOT_INIT = "BeanMapper: bean mapping is not initialized";
	private static final String MAPPER_UNK_CLASS = "BeanMapper: unknown class: %s";		
	private static final String MAPPER_EMPTY_REF = "BeanMapper: class reference cannot be empty";
	
	private static final String OBJ_MAPPER_NOT_INIT = "BeanMapper: obhect mapping is not initialized";
	private static final String OBJ_MAPPER_EMPTY_REF = "BeanMapper: bean class reference cannot be empty";
	
	private static final String PROXIES_CANT_INST = "BeanMapper: cannot instantiate a proxy for %s";
	private static final String PROXIES_NOT_INIT = "BeanMapper: proxy mapping is not initialized";
	
	// !!! LinkedHashMap to preserve registration order
	private static LinkedHashMap<Class<?>, Class<? extends IPersistenceBean>> beanClassMapping;
	private static LinkedHashMap<Class<? extends IPersistenceBean>, Class<?>> objectClassMapping;
	private static ConcurrentHashMap<String, IPersistenceBean> proxies; // used in ref resolution
	private static final Logger LOGGER = Logger.getLogger(BeanMapper.class.getName());
	
	static {
		beanClassMapping = new LinkedHashMap<Class<?>, Class<? extends IPersistenceBean>> ();
		objectClassMapping = new LinkedHashMap<Class<? extends IPersistenceBean>, Class<?>> ();
		proxies = new ConcurrentHashMap<String, IPersistenceBean>();
	}

	public static void registerBean(
			Class<?> classRef, 
			Class<? extends IPersistenceBean> beanClassRef) {
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
			IPersistenceBean proxy = ClassUtil.instantiateClass(beanClassRef);
			if (proxy != null)
				proxy.setRefId(0); // to distinguish from regular beans 
			
			proxies.put(objClassName, proxy);
		} catch (Exception e) {
			throw new RuntimeException(String.format(PROXIES_CANT_INST, beanClassName), e);
		}		
	}
	
	@SuppressWarnings("unchecked")
	public static Class<? extends IPersistenceBean> getBeanClass(Class<?> classRef) {
		if (classRef == null)
			throw(new IllegalArgumentException(MAPPER_EMPTY_REF));
	
		if (beanClassMapping == null)
			throw(new RuntimeException(MAPPER_NOT_INIT));
		
		Class<? extends IPersistenceBean> beanClass = beanClassMapping.get(classRef);
				
		// If not found explicitly, try to find a matching bean
		if (beanClass == null) {
			if (IPersistenceBean.class.isAssignableFrom(classRef)) // A bean is a bean for itself 
				beanClass = (Class<? extends IPersistenceBean>) classRef;
			else
			for (Class<?> cls : beanClassMapping.keySet())
				if (cls.isAssignableFrom(classRef)) {
					beanClass = beanClassMapping.get(cls);
					LOGGER.warning(String.format("No bean class registered for %s, using %s for %s instead.", 
							ClassUtil.getQualifiedClassName(classRef),
							ClassUtil.getQualifiedClassName(beanClass),
							ClassUtil.getQualifiedClassName(cls)));
					break;
				}	
			if (beanClass == null)
				LOGGER.warning(String.format("No bean class registered for %s", ClassUtil.getQualifiedClassName(classRef)));
		}
		return beanClass;		
	}
	
	public static Class<?> getObjectClass(Class<? extends IPersistenceBean> beanClassRef) {
		if (beanClassRef == null)
			throw(new IllegalArgumentException(OBJ_MAPPER_EMPTY_REF));
	
		if (objectClassMapping == null)
			throw(new RuntimeException(OBJ_MAPPER_NOT_INIT));
		
		return objectClassMapping.get(beanClassRef);		
	}
	
	public static IPersistenceBean getProxy(String objClassName) {
		if (Util.isEmpty(objClassName)) return null;
		
		return proxies.get(objClassName);
	}
	
	public static IPersistenceBean getProxy(Class<?> objClassRef) {
		if (objClassRef == null) return null;
				
//		Class<? extends IPersistenceBean> beanClassRef = getBeanClass(objClassRef);
//		if (beanClassRef == null) return null;
//		return getProxy(beanClassRef.getName());
		return getProxy(objClassRef.getName());
	}
	
	public static Class<? extends IPersistenceBean> getBeanClass(String className) {
			
		Class<? extends IPersistenceBean> res = null;
		try {
			res = getBeanClass(Class.forName(className));
		} catch (ClassNotFoundException e) {
			throw(new RuntimeException(String.format(MAPPER_UNK_CLASS, className)));
		}
		return res;		
	}			
}
