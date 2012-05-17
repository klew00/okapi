/*===========================================================================
  Copyright (C) 2011 by the Okapi Framework contributors
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

package net.sf.okapi.common.resource;

import java.net.URI;
import java.util.Collections;

import net.sf.okapi.common.IResource;
import net.sf.okapi.common.ISkeleton;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.annotation.Annotations;
import net.sf.okapi.common.annotation.IAnnotation;
import net.sf.okapi.common.exceptions.OkapiNotImplementedException;
import net.sf.okapi.common.filters.IFilterConfigurationMapper;

/**
 * Special resource used to carry runtime parameters.
 */
public class PipelineParameters implements IResource {

	private Annotations annotations;
	private String id;
	private URI outputURI;
	private LocaleId targetLocale;
	private LocaleId sourceLocale;
	private String outputEncoding;
	private URI inputURI;
	private String filterConfigId;
	private IFilterConfigurationMapper fcMapper;
	private RawDocument inputRawDocument;
	private RawDocument secondInputRawDocument;
	private RawDocument thirdInputRawDocument;
	private String rootDirectory;
	private String inputRootDirectory;
	private Object uiParent;
	private int batchInputCount = -1;

	/**
	 * Creates a new empty ParametersEvent object.
	 */
	public PipelineParameters () {
	}
	
	@Override
	public <A extends IAnnotation> A getAnnotation(Class<A> annotationType) {
		if ( annotations == null ) {
			return null;
		}
		return annotationType.cast(annotations.get(annotationType));
	}

	@Override
	public String getId () {
		return id;
	}

	@Override
	public ISkeleton getSkeleton () {
		throw new OkapiNotImplementedException("This resource does not have a skeketon");
	}

	@Override
	public void setAnnotation (IAnnotation annotation) {
		if ( annotations == null ) {
			annotations = new Annotations();
		}
		annotations.set(annotation);
	}

	@Override
	public void setId (String id) {
		this.id = id;
	}

	@Override
	public void setSkeleton(ISkeleton skeleton) {
		throw new OkapiNotImplementedException("This resource does not have a skeketon");
	}

	public Iterable<IAnnotation> getAnnotations () {
		if ( annotations == null ) {
			return Collections.emptyList();
		}
		return annotations;
	}

	public void setOutputURI (URI outputURI) {
		this.outputURI = outputURI;
	}
	
	public URI getOutputURI () {
		return outputURI;
	}

	public void setTargetLocale (LocaleId targetLocale) {
		this.targetLocale = targetLocale;
	}
	
	public LocaleId getTargetLocale () {
		return targetLocale;
	}

	public void setSourceLocale (LocaleId sourceLocale) {
		this.sourceLocale = sourceLocale;
	}
	
	public LocaleId getSourceLocale () {
		return sourceLocale;
	}

	public void setOutputEncoding (String outputEncoding) {
		this.outputEncoding = outputEncoding;
	}
	
	public String getOutputEncoding () {
		return outputEncoding;
	}

	public void setInputURI (URI inputURI) {
		this.inputURI = inputURI;
	}
	
	public URI getInputURI () {
		return inputURI;
	}

	public void setFilterConfigurationId (String filterConfigId) {
		this.filterConfigId = filterConfigId;
	}
	
	public String getFilterConfigurationId () {
		return filterConfigId;
	}

	public void setFilterConfigurationMapper (IFilterConfigurationMapper fcMapper) {
		this.fcMapper = fcMapper;
	}
	
	public IFilterConfigurationMapper getFilterConfigurationMapper () {
		return fcMapper;
	}

	public void setInputRawDocument (RawDocument inputRawDocument) {
		this.inputRawDocument = inputRawDocument;
	}
	
	public RawDocument getInputRawDocument () {
		return inputRawDocument;
	}

	public void setSecondInputRawDocument (RawDocument secondInputRawDocument) {
		this.secondInputRawDocument = secondInputRawDocument;
	}
	
	public RawDocument getSecondInputRawDocument () {
		return secondInputRawDocument;
	}

	public void setThirdInputRawDocument (RawDocument thirdInputRawDocument) {
		this.thirdInputRawDocument = thirdInputRawDocument;
	}
	
	public RawDocument getThirdInputRawDocument () {
		return thirdInputRawDocument;
	}

	public void setRootDirectory (String rootDirectory) {
		this.rootDirectory = rootDirectory;
	}
	
	public String getRootDirectory () {
		return rootDirectory;
	}

	public void setInputRootDirectory (String inputRootDirectory) {
		this.inputRootDirectory = inputRootDirectory;
	}
	
	public String getInputRootDirectory () {
		return inputRootDirectory;
	}

	public void setUIParent (Object uiParent) {
		this.uiParent = uiParent;
	}
	
	public Object getUIParent () {
		return uiParent;
	}

	public void setBatchInputCount (int batchInputCount) {
		this.batchInputCount = batchInputCount;
	}
	
	public int getBatchInputCount () {
		return batchInputCount;
	}

}
