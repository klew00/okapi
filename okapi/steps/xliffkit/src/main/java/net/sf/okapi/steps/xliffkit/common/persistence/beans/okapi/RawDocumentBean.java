/*===========================================================================
  Copyright (C) 2010 by the Okapi Framework contributors
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

package net.sf.okapi.steps.xliffkit.common.persistence.beans.okapi;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.Util;
import net.sf.okapi.common.annotation.IAnnotation;
import net.sf.okapi.common.resource.RawDocument;
import net.sf.okapi.steps.xliffkit.common.persistence.IPersistenceSession;
import net.sf.okapi.steps.xliffkit.common.persistence.PersistenceBean;
import net.sf.okapi.steps.xliffkit.common.persistence.beans.FactoryBean;

public class RawDocumentBean extends PersistenceBean<RawDocument> {

	private List<FactoryBean> annotations = new ArrayList<FactoryBean>();
	private String filterConfigId;
	private String id;
	private String encoding;
	private String srcLoc;
	private String trgLoc;
	private String inputURI;
	private String inputCharSequence;
	// TODO Handle inputStream if possible at all
	// private InputStreamBean inputStream; 

	@Override
	protected RawDocument createObject(IPersistenceSession session) {
		RawDocument obj = null;
		
		if (!Util.isEmpty(inputURI))
			try {
				obj = new RawDocument(new URI(inputURI), encoding, new LocaleId(srcLoc), new LocaleId(trgLoc));
			} catch (URISyntaxException e) {
				// TODO Handle exception
				e.printStackTrace();
			}
		else if (!Util.isEmpty(inputCharSequence))
			obj = new RawDocument(inputCharSequence, new LocaleId(srcLoc), new LocaleId(trgLoc));		
		else
			// TODO Handle inputStream or empty input params (Okapi-B 19)
			obj = new RawDocument("", new LocaleId(srcLoc), new LocaleId(trgLoc));
		
		return obj;
	}

	@Override
	protected void fromObject(RawDocument obj, IPersistenceSession session) {
		for (IAnnotation annotation : obj.getAnnotations()) {
			FactoryBean annotationBean = new FactoryBean();
			annotations.add(annotationBean);
			annotationBean.set(annotation, session);
		}
		
		filterConfigId = obj.getFilterConfigId();
		id = obj.getId();
		encoding = obj.getEncoding();
		
		if (obj.getSourceLocale() != null)
			srcLoc = obj.getSourceLocale().toString();
		
		if (obj.getTargetLocale() != null)
			trgLoc = obj.getTargetLocale().toString();

		if (obj.getInputURI() != null)
			inputURI = obj.getInputURI().toString();
		
		if (obj.getInputCharSequence() != null)
			inputCharSequence = obj.getInputCharSequence().toString();
	}

	@Override
	protected void setObject(RawDocument obj, IPersistenceSession session) {
		for (FactoryBean annotationBean : annotations)
			obj.setAnnotation(annotationBean.get(IAnnotation.class, session));
						
		obj.setFilterConfigId(filterConfigId);
		obj.setId(id);
		obj.setEncoding(encoding);			
	}
	
	public List<FactoryBean> getAnnotations() {
		return annotations;
	}

	public void setAnnotations(List<FactoryBean> annotations) {
		this.annotations = annotations;
	}

	public String getFilterConfigId() {
		return filterConfigId;
	}

	public void setFilterConfigId(String filterConfigId) {
		this.filterConfigId = filterConfigId;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getEncoding() {
		return encoding;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	public String getSrcLoc() {
		return srcLoc;
	}

	public void setSrcLoc(String srcLoc) {
		this.srcLoc = srcLoc;
	}

	public String getTrgLoc() {
		return trgLoc;
	}

	public void setTrgLoc(String trgLoc) {
		this.trgLoc = trgLoc;
	}

	public String getInputURI() {
		return inputURI;
	}

	public void setInputURI(String inputURI) {
		this.inputURI = inputURI;
	}

	public String getInputCharSequence() {
		return inputCharSequence;
	}

	public void setInputCharSequence(String inputCharSequence) {
		this.inputCharSequence = inputCharSequence;
	}
}
