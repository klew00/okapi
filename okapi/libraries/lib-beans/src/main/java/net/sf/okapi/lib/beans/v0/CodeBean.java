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

import java.util.LinkedHashMap;

import net.sf.okapi.common.resource.Code;
import net.sf.okapi.common.resource.TextFragment.TagType;
import net.sf.okapi.lib.beans.v0.IPersistenceBean;
import net.sf.okapi.lib.beans.v0.IPersistenceSession;

public class CodeBean implements IPersistenceBean {

	private TagType tagType;
	private int id;
	private String type;
	private String data;
	private String outerData;
	private int flag;
	private LinkedHashMap<String, InlineAnnotationBean> annotations = new LinkedHashMap<String, InlineAnnotationBean>();
	
	@Override
	public void init(IPersistenceSession session) {
	}

	@Override
	public <T> T get(Class<T> classRef) {
		Code code = new Code(tagType, type, data);
		code.setId(id);
		code.setOuterData(outerData);
		// TODO flag handling in Code
		// code.setFlag
		
		return classRef.cast(code);
	}

	@Override
	public IPersistenceBean set(Object obj) {
		if (obj instanceof Code) {
			Code code = (Code) obj;
			tagType = code.getTagType();
			id = code.getId();
			type = code.getType();
			data = code.getData();
			outerData = code.getOuterData();
			// TODO flag handling in Code
			//flag = code.get;
		}
		return this;
	}

	public TagType getTagType() {
		return tagType;
	}

	public void setTagType(TagType tagType) {
		this.tagType = tagType;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public String getOuterData() {
		return outerData;
	}

	public void setOuterData(String outerData) {
		this.outerData = outerData;
	}

	public int getFlag() {
		return flag;
	}

	public void setFlag(int flag) {
		this.flag = flag;
	}

	public LinkedHashMap<String, InlineAnnotationBean> getAnnotations() {
		return annotations;
	}

	public void setAnnotations(
			LinkedHashMap<String, InlineAnnotationBean> annotations) {
		this.annotations = annotations;
	}

}
