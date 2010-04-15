/*===========================================================================
  Copyright (C) 2009 by the Okapi Framework contributors
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
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import net.sf.okapi.common.ClassUtil;
import net.sf.okapi.common.Event;
import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.Range;
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
import net.sf.okapi.filters.openxml.OpenXMLZipFilterWriter;
import net.sf.okapi.filters.pensieve.PensieveFilterWriter;
import net.sf.okapi.filters.po.POFilterWriter;
import net.sf.okapi.steps.formatconversion.TableFilterWriter;
import net.sf.okapi.steps.xliffkit.common.persistence.beans.BaseNameableBean;
import net.sf.okapi.steps.xliffkit.common.persistence.beans.BaseReferenceableBean;
import net.sf.okapi.steps.xliffkit.common.persistence.beans.CodeBean;
import net.sf.okapi.steps.xliffkit.common.persistence.beans.DocumentBean;
import net.sf.okapi.steps.xliffkit.common.persistence.beans.DocumentPartBean;
import net.sf.okapi.steps.xliffkit.common.persistence.beans.EndingBean;
import net.sf.okapi.steps.xliffkit.common.persistence.beans.EventBean;
import net.sf.okapi.steps.xliffkit.common.persistence.beans.GenericFilterWriterBean;
import net.sf.okapi.steps.xliffkit.common.persistence.beans.GenericSkeletonBean;
import net.sf.okapi.steps.xliffkit.common.persistence.beans.GenericSkeletonPartBean;
import net.sf.okapi.steps.xliffkit.common.persistence.beans.InlineAnnotationBean;
import net.sf.okapi.steps.xliffkit.common.persistence.beans.InputStreamBean;
import net.sf.okapi.steps.xliffkit.common.persistence.beans.MultiEventBean;
import net.sf.okapi.steps.xliffkit.common.persistence.beans.ParametersBean;
import net.sf.okapi.steps.xliffkit.common.persistence.beans.PropertyBean;
import net.sf.okapi.steps.xliffkit.common.persistence.beans.RangeBean;
import net.sf.okapi.steps.xliffkit.common.persistence.beans.RawDocumentBean;
import net.sf.okapi.steps.xliffkit.common.persistence.beans.SegmentBean;
import net.sf.okapi.steps.xliffkit.common.persistence.beans.StartDocumentBean;
import net.sf.okapi.steps.xliffkit.common.persistence.beans.StartGroupBean;
import net.sf.okapi.steps.xliffkit.common.persistence.beans.StartSubDocumentBean;
import net.sf.okapi.steps.xliffkit.common.persistence.beans.TMXFilterWriterBean;
import net.sf.okapi.steps.xliffkit.common.persistence.beans.TargetPropertiesAnnotationBean;
import net.sf.okapi.steps.xliffkit.common.persistence.beans.TextContainerBean;
import net.sf.okapi.steps.xliffkit.common.persistence.beans.TextFragmentBean;
import net.sf.okapi.steps.xliffkit.common.persistence.beans.TextPartBean;
import net.sf.okapi.steps.xliffkit.common.persistence.beans.TextUnitBean;
import net.sf.okapi.steps.xliffkit.common.persistence.beans.ZipEntryBean;
import net.sf.okapi.steps.xliffkit.common.persistence.beans.ZipFileBean;
import net.sf.okapi.steps.xliffkit.common.persistence.beans.ZipFilterWriterBean;
import net.sf.okapi.steps.xliffkit.common.persistence.beans.ZipSkeletonBean;

public class BeanMapper {
	
	private static final String MSG1 = "PersistenceFactory: bean mapping is not initialized";
	private static final String MSG2 = "PersistenceFactory: unknown class: %s";
	private static final String MSG3 = "PersistenceFactory: class %s is not registered";
	private static final String MSG4 = "PersistenceFactory: cannot instantiate %s";	
	private static final String MSG5 = "PersistenceFactory: Class reference cannot be empty";
	
	// !!! LinkedHashMap to preserve registration order
	private static LinkedHashMap<Class<?>, Class<? extends IPersistenceBean>> beanMapping;
	//private static ConcurrentHashMap<Class<? extends IPersistenceBean>, IPersistenceBean> persistenceCache;
	
	static {
		beanMapping = new LinkedHashMap<Class<?>, Class<? extends IPersistenceBean>> ();
		//persistenceCache = new ConcurrentHashMap<Class<? extends IPersistenceBean>, IPersistenceBean> ();
		
		registerBeans();
	}

	public static void registerBean(
			Class<?> classRef, 
			Class<? extends IPersistenceBean> beanClassRef) {
		if (classRef == null || beanClassRef == null)
			throw(new IllegalArgumentException());
		
		if (beanMapping == null)
			throw(new RuntimeException(MSG1));
		
		// TODO Make sure if a bean for already registered class was registered later, the later bean takes precedence
		// HashMap.put(): "If the map previously contained a mapping for the key, the old value is replaced". Test it.
		beanMapping.put(classRef, beanClassRef);		
	}
	
	@SuppressWarnings("unchecked")
	public static Class<? extends IPersistenceBean> getBeanClass(Class<?> classRef) {
		if (classRef == null)
			throw(new IllegalArgumentException(MSG5));
		//if (classRef == null) return null;
	
		if (beanMapping == null)
			throw(new RuntimeException(MSG1));
		
		Class<? extends IPersistenceBean> beanClass = beanMapping.get(classRef);
		
		// If not found explicitly, try to find a matching bean
		if (beanClass == null)
			if (IPersistenceBean.class.isAssignableFrom(classRef))
				beanClass = (Class<? extends IPersistenceBean>) classRef;
			else
			for (Class<?> cls : beanMapping.keySet())
				if (cls.isAssignableFrom(classRef)) {
					beanClass = beanMapping.get(cls);
					break;
				}		
		return beanClass;		
	}
	
	public static Class<? extends IPersistenceBean> getBeanClass(String className) {
			
		Class<? extends IPersistenceBean> res = null;
		try {
			res = getBeanClass(Class.forName(className));
		} catch (ClassNotFoundException e) {
			throw(new RuntimeException(String.format(MSG2, className)));
		}
		return res;		
	}
	
	public static IPersistenceBean getBean(Class<?> classRef, IPersistenceSession session) {
		Class<? extends IPersistenceBean> beanClass = 
			getBeanClass(classRef); // Checks for skelClass == null, beanMapping == null
		
		if (beanClass == null)
			throw(new RuntimeException(String.format(MSG3, classRef.getName())));
		
//		if (persistenceCache == null)
//			throw(new RuntimeException(MSG2));
		
		IPersistenceBean bean = null; //persistenceCache.get(beanClass); 
		//if (bean == null) {
			try {
				bean = ClassUtil.instantiateClass(beanClass, session);
				//persistenceCache.put(beanClass, bean);
			} catch (Exception e) {
				throw new RuntimeException(String.format(MSG4, beanClass.getName()), e);
			}
		//}		
		return bean;		
	}
	
	public static <T> T getObject(Class<T> classRef, IPersistenceSession session) {
		T res = null;
		try {
			res = ClassUtil.instantiateClass(classRef, session);
		} catch (Exception e) {
			throw new RuntimeException(String.format(MSG4, ClassUtil.getClassName(classRef)), e);
		}
		return res;
	}
	
	private static void registerBeans() {
		// General purpose beans
		registerBean(List.class, ListBean.class);
		registerBean(IParameters.class, ParametersBean.class);
		//registerBean(IFilterWriter.class, FilterWriterBean.class);
		registerBean(IPersistenceSession.class, HeaderBean.class);
		registerBean(Object.class, TypeInfoBean.class); // If no bean was found, use just this one to store class info
		
		// Specific class beans				
		registerBean(Event.class, EventBean.class);		
		registerBean(TextUnit.class, TextUnitBean.class);
		registerBean(RawDocument.class, RawDocumentBean.class);
		registerBean(Property.class, PropertyBean.class);
		registerBean(TextFragment.class, TextFragmentBean.class);
		registerBean(TextContainer.class, TextContainerBean.class);
		registerBean(Code.class, CodeBean.class);
		registerBean(Document.class, DocumentBean.class);
		registerBean(DocumentPart.class, DocumentPartBean.class);
		registerBean(Ending.class, EndingBean.class);
		registerBean(MultiEvent.class, MultiEventBean.class);
		registerBean(TextPart.class, TextPartBean.class);
		registerBean(Segment.class, SegmentBean.class);
		registerBean(Range.class, RangeBean.class);
		registerBean(BaseNameable.class, BaseNameableBean.class);
		registerBean(BaseReferenceable.class, BaseReferenceableBean.class);
		registerBean(StartDocument.class, StartDocumentBean.class);
		registerBean(StartGroup.class, StartGroupBean.class);
		registerBean(StartSubDocument.class, StartSubDocumentBean.class);
		registerBean(TargetPropertiesAnnotation.class, TargetPropertiesAnnotationBean.class);
		registerBean(GenericSkeleton.class, GenericSkeletonBean.class);
		registerBean(GenericSkeletonPart.class, GenericSkeletonPartBean.class);
		registerBean(ZipSkeleton.class, ZipSkeletonBean.class);
		registerBean(ZipFile.class, ZipFileBean.class);
		registerBean(ZipEntry.class, ZipEntryBean.class);
		registerBean(InputStream.class, InputStreamBean.class);
		registerBean(InlineAnnotation.class, InlineAnnotationBean.class);
		registerBean(GenericFilterWriter.class, GenericFilterWriterBean.class);
		registerBean(TMXFilterWriter.class, TMXFilterWriterBean.class);
		registerBean(ZipFilterWriter.class, ZipFilterWriterBean.class);
		// Registered here to require dependencies at development-time
		registerBean(OpenXMLZipFilterWriter.class, TypeInfoBean.class); 		
		registerBean(PensieveFilterWriter.class, TypeInfoBean.class);
		registerBean(POFilterWriter.class, TypeInfoBean.class);
		registerBean(TableFilterWriter.class, TypeInfoBean.class);		
		//registerBean(.class, Bean.class);
	}
}
