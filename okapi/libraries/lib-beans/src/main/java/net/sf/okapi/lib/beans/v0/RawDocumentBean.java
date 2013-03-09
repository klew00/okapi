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

package net.sf.okapi.lib.beans.v0;

import java.io.InputStream;
import java.io.Reader;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import net.sf.okapi.common.LocaleId;
import net.sf.okapi.lib.beans.v0.FactoryBean;
import net.sf.okapi.lib.beans.v0.IPersistenceBean;
import net.sf.okapi.lib.beans.v0.IPersistenceSession;

public class RawDocumentBean implements IPersistenceBean {

	private List<FactoryBean> annotations = new ArrayList<FactoryBean>();
	private String filterConfigId;
	private String id;
	private String encoding;
	private String srcLoc;
	private LocaleId trgLoc;
	private URI inputURI;
	private CharSequence inputCharSequence;
	private boolean hasReaderBeenCalled;
	private Reader reader;
	private InputStream inputStream;
	private FactoryBean skeleton;
	
	@Override
	public void init(IPersistenceSession session) {
		// TODO Auto-generated method stub

	}

	@Override
	public <T> T get(Class<T> classRef) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IPersistenceBean set(Object obj) {
		// TODO Auto-generated method stub
		return null;
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

	public LocaleId getTrgLoc() {
		return trgLoc;
	}

	public void setTrgLoc(LocaleId trgLoc) {
		this.trgLoc = trgLoc;
	}

	public URI getInputURI() {
		return inputURI;
	}

	public void setInputURI(URI inputURI) {
		this.inputURI = inputURI;
	}

	public CharSequence getInputCharSequence() {
		return inputCharSequence;
	}

	public void setInputCharSequence(CharSequence inputCharSequence) {
		this.inputCharSequence = inputCharSequence;
	}

	public boolean isHasReaderBeenCalled() {
		return hasReaderBeenCalled;
	}

	public void setHasReaderBeenCalled(boolean hasReaderBeenCalled) {
		this.hasReaderBeenCalled = hasReaderBeenCalled;
	}

	public Reader getReader() {
		return reader;
	}

	public void setReader(Reader reader) {
		this.reader = reader;
	}

	public InputStream getInputStream() {
		return inputStream;
	}

	public void setInputStream(InputStream inputStream) {
		this.inputStream = inputStream;
	}

	public FactoryBean getSkeleton() {
		return skeleton;
	}

	public void setSkeleton(FactoryBean skeleton) {
		this.skeleton = skeleton;
	}

	public void setAnnotations(List<FactoryBean> annotations) {
		this.annotations = annotations;
	}

	public List<FactoryBean> getAnnotations() {
		return annotations;
	}

}
