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
import java.io.OutputStream;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import net.sf.okapi.common.Util;

public abstract class PersistenceSession implements IPersistenceSession {

	private static final String ITEM_LABEL = "item"; //$NON-NLS-1$
	
	protected abstract void writeBean(IPersistenceBean bean, String name);	
	protected abstract IPersistenceBean readBean(Class<? extends IPersistenceBean> beanClass, String name);
	
	protected abstract void startWriting(OutputStream outStream);
	protected abstract void endWriting(OutputStream outStream);
	
	protected abstract void startReading(InputStream inStream);
	protected abstract void endReading(InputStream inStream);

	private String itemLabel = ITEM_LABEL;
	private SessionState state = SessionState.IDLE;
	private ReferenceResolver refResolver = new ReferenceResolver();	
	private int itemCounter = 0;
	private Class<?> prevClass;
	private Class<? extends IPersistenceBean> beanClass;
	private OutputStream outStream;
	private InputStream inStream;
	private String description;
	private Class<?> itemClass;
	private LinkedList<Object> queue = new LinkedList<Object>(); 
	private LinkedList<IPersistenceBean> beanQueue = new LinkedList<IPersistenceBean>();
	
	@Override
	public void cacheBean(Object obj, IPersistenceBean bean) {
		refResolver.cacheBean(obj, bean);
	}

	@Override
	public IPersistenceBean createBean(Class<?> classRef) {
		return refResolver.createBean(classRef);
	}

	@Override
	public <T> T deserialize(Class<T> classRef) {
		return deserialize(classRef, itemLabel);
	}

	private <T> T deserialize(Class<T> classRef, String name) {
		if (state != SessionState.READING) return null;
		
		if (queue.size() > 0) 
			return classRef.cast(queue.poll());
		
		Object[] objects = new Object[beanQueue.size()];
		queue.addAll(Arrays.asList(objects));
//		if (beanQueue.size() == 0)
//			queue.a
		
		// Update bean class if core class has changed
		if (classRef != prevClass) { 
			beanClass = BeanMapper.getBeanClass(classRef);
			prevClass = classRef;
		}
		IPersistenceBean bean = readBean(beanClass, name);
		
		if (bean == null) {
			end();
			return null;
		}
		return bean.get(classRef, this);
	}
	
	@Override
	public void end() {
		switch (state) {
		case IDLE:
			return;
			
		case READING:
			if (inStream != null)
				endReading(inStream);
			// !!! Do not close external inStream
			break;
			
		case WRITING:
			if (outStream != null) {
				refResolver.updateFrames();
				endWriting(outStream);
			}
			// !!! Do not close external outStream
		}		
		inStream = null;
		outStream = null;
		prevClass = null;
		beanClass = null;
		refResolver.reset();	
		state = SessionState.IDLE;
	}
	
	@Override
	public String getDescription() {		
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public long getRefIdForObject(Object obj) {
		return refResolver.getRefIdForObject(obj);
	}

	@Override
	public void setRefIdForObject(Object obj, long refId) {
		refResolver.setRefIdForObject(obj, refId);
	}

	@Override
	public void setReference(long parentRefId, long childRefId) {
		refResolver.addReference(parentRefId, childRefId);
	}

	@Override
	public String getItemClass() {
		return (itemClass == null) ? "" : itemClass.getName();
	}

	@Override
	public void serialize(Object obj) {
		serialize(obj, String.format("%s%d", itemLabel, ++itemCounter));
	}

	@Override
	public void serialize(Object obj, String name) {
		if (state != SessionState.WRITING) return;

		IPersistenceBean bean = refResolver.createBean(obj.getClass());
		if (bean == null) return;
		
		refResolver.setRootId(bean.getRefId());
		bean.set(obj, this);
		
		writeBean(bean, name);
	}

	@Override
	public void start(OutputStream outStream) {
		if (outStream == null)
			throw(new IllegalArgumentException("PersistenceSession: output stream cannot be null"));
		
		end();
		refResolver.reset();
		this.outStream = outStream;		
		itemCounter = 0;
		
		if (Util.isEmpty(itemLabel))
			this.itemLabel = ITEM_LABEL;
		
		state = SessionState.WRITING;
		startWriting(outStream);
	}

	@Override
	public void start(InputStream inStream) {
		if (inStream == null)
			throw(new IllegalArgumentException("PersistenceSession: input stream cannot be null"));
		
		end();
		refResolver.reset();
		this.inStream = inStream;
				
		state = SessionState.READING;
		startReading(inStream);
	}

	@Override
	public IPersistenceBean uncacheBean(Object obj) {
		return refResolver.uncacheBean(obj);
	}

	@Override
	public SessionState getState() {
		return state;
	}
	public String getItemLabel() {
		return itemLabel;
	}
	public void setItemLabel(String itemLabel) {
		this.itemLabel = itemLabel;
	}

	protected List<List<Long>> getFrames() {
		return refResolver.getFrames();
	}
	
	protected void setFrames(List<List<Long>> frames) {
		refResolver.setFrames(frames);
	}
	public void setItemClass(Class<?> itemClass) {
		this.itemClass = itemClass;
	}	
}
