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

package net.sf.okapi.lib.beans.v1;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import net.sf.okapi.common.ISkeleton;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.annotation.IAnnotation;
import net.sf.okapi.common.resource.ITextUnit;
import net.sf.okapi.common.resource.Property;
import net.sf.okapi.common.resource.TextContainer;
import net.sf.okapi.common.resource.TextUnit;
import net.sf.okapi.lib.persistence.IPersistenceSession;
import net.sf.okapi.lib.persistence.PersistenceBean;
import net.sf.okapi.lib.persistence.beans.FactoryBean;

public class TextUnitBean extends PersistenceBean<ITextUnit> {
	private String id;
	private int refCount;
	private String name;
	private String type;
	private boolean isTranslatable;
	private boolean preserveWS;	
	private FactoryBean skeleton = new FactoryBean();
	private List<PropertyBean> properties = new ArrayList<PropertyBean>();
	private AnnotationsBean annotations = new AnnotationsBean();
	private TextContainerBean source = new TextContainerBean();
	private String mimeType;
	private Map<String, TextContainerBean> targets = new ConcurrentHashMap<String, TextContainerBean>();
	
//	private List<RangeBean> srcSegRanges = new ArrayList<RangeBean>();
//	private ConcurrentHashMap<String, List<RangeBean>> trgSegRanges = new ConcurrentHashMap<String, List<RangeBean>>();

	@Override
	protected ITextUnit createObject(IPersistenceSession session) {
		return new TextUnit(getId());
	}

	@Override
	protected void fromObject(ITextUnit obj, IPersistenceSession session) {
		id = obj.getId();
		refCount = obj.getReferenceCount();
		name = obj.getName();
		type = obj.getType();
		isTranslatable = obj.isTranslatable();
		preserveWS = obj.preserveWhitespaces();
		skeleton.set(obj.getSkeleton(), session);

		for (String propName : obj.getPropertyNames()) {
			PropertyBean propBean = new PropertyBean();
			propBean.set(obj.getProperty(propName), session);
			properties.add(propBean);
		}
		
		annotations.set(obj.getAnnotations(), session);
								
		source.set(obj.getSource(), session);
		mimeType = obj.getMimeType();
		
		for (LocaleId locId : obj.getTargetLocales()) {
			TextContainerBean targetBean = new TextContainerBean();
			targets.put(locId.toString(), targetBean);
			targetBean.set(obj.getTarget(locId), session);
		}
		
//			// srcSegRanges
//			List<Range> ranges = obj.saveCurrentSourceSegmentation();
//			for (Range range : ranges) {
//				RangeBean rangeBean = new RangeBean(); 
//				rangeBean.set(range);
//				srcSegRanges.add(rangeBean);
//			}
//			
//			// trgSegRanges
	}

	@Override
	protected void setObject(ITextUnit obj, IPersistenceSession session) {
		obj.setId(id);
		obj.setReferenceCount(refCount);
		obj.setName(name);
		obj.setType(type);
		obj.setIsTranslatable(isTranslatable);
		obj.setPreserveWhitespaces(preserveWS);
		obj.setSkeleton(skeleton.get(ISkeleton.class, session));
		
		for (PropertyBean propBean : properties)
			obj.setProperty(propBean.get(Property.class, session));
		
		for (FactoryBean annotationBean : annotations.getItems())
			obj.setAnnotation(annotationBean.get(IAnnotation.class, session));
		
		obj.setSource(source.get(TextContainer.class, session));
		obj.setMimeType(mimeType);		
					
		for (String locTag : targets.keySet())
			obj.setTarget(new LocaleId(locTag), targets.get(locTag).get(TextContainer.class, session));						

//			// srcSegRanges
//			List<Range> ranges = new ArrayList<Range>();
//			for (RangeBean rangeBean : srcSegRanges)
//				ranges.add(rangeBean.get(Range.class));
//			
//			obj.getSource().createSegments(ranges);
//			
//			// trgSegRanges
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public boolean isTranslatable() {
		return isTranslatable;
	}
	public void setTranslatable(boolean isTranslatable) {
		this.isTranslatable = isTranslatable;
	}
	public boolean isPreserveWS() {
		return preserveWS;
	}
	public void setPreserveWS(boolean preserveWS) {
		this.preserveWS = preserveWS;
	}
	public String getMimeType() {
		return mimeType;
	}
	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}
	public TextContainerBean getSource() {
		return source;
	}
	public void setSource(TextContainerBean source) {
		this.source = source;
	}

	public void setSkeleton(FactoryBean skeleton) {
		this.skeleton = skeleton;
	}

	public FactoryBean getSkeleton() {
		return skeleton;
	}

	public Map<String, TextContainerBean> getTargets() {
		return targets;
	}

	public void setTargets(Map<String, TextContainerBean> targets) {
		this.targets = targets;
	}

	public AnnotationsBean getAnnotations() {
		return annotations;
	}

	public void setAnnotations(AnnotationsBean annotations) {
		this.annotations = annotations;
	}

	public List<PropertyBean> getProperties() {
		return properties;
	}

	public void setProperties(List<PropertyBean> properties) {
		this.properties = properties;
	}

	public void setRefCount(int refCount) {
		this.refCount = refCount;
	}

	public int getRefCount() {
		return refCount;
	}

//	public List<RangeBean> getSrcSegRanges() {
//		return srcSegRanges;
//	}
//
//	public void setSrcSegRanges(List<RangeBean> srcSegRanges) {
//		this.srcSegRanges = srcSegRanges;
//	}
//
//	public ConcurrentHashMap<String, List<RangeBean>> getTrgSegRanges() {
//		return trgSegRanges;
//	}
//
//	public void setTrgSegRanges(
//			ConcurrentHashMap<String, List<RangeBean>> trgSegRanges) {
//		this.trgSegRanges = trgSegRanges;
//	}

}
