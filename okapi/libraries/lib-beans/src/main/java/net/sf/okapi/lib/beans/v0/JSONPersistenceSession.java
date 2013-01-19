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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import net.sf.okapi.common.ClassUtil;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;

public class JSONPersistenceSession implements IPersistenceSession {

	public static final String MIME_TYPE = "application/json";
	
	private ObjectMapper mapper;
	private JsonFactory jsonFactory;
	private JsonParser parser;
	private OutputStream outStream;
	private InputStream inStream;
	private boolean isActive;
	private Class<?> rootClass;
	private Class<? extends IPersistenceBean> beanClass;

	public JSONPersistenceSession(Class<?> rootClass) {
		super();
		this.rootClass = rootClass;
		this.beanClass = PersistenceMapper.getBeanClass(rootClass);
	}
	
	public JSONPersistenceSession(Class<?> rootClass, OutputStream outStream) {
		this(rootClass);
		start(outStream);
	}
	
	public JSONPersistenceSession(Class<?> rootClass, InputStream inStream) {
		this(rootClass);
		start(inStream);
	}
	
	@Override
	public <T> T convert(Object object, Class<T> expectedClass) {		
		return mapper.convertValue(object, expectedClass);
	}

	@Override
	public Object deserialize() {
		if (!isActive) return null;
		
		IPersistenceBean bean = null;
		try {
			//bean = mapper.readValue(inStream, beanClass);
			bean = mapper.readValue(parser, beanClass);
			bean.init(this);
						
		} catch (JsonParseException e) {
			// TODO Handle exception
		} catch (JsonMappingException e) {
			// TODO Handle exception
		} catch (IOException e) {
			// TODO Handle exception
		}
		
		return bean.get(rootClass);
	}

	@Override
	public void end() {
		if (!isActive) return;
		isActive = false;
		
		if (inStream != null)
			try {
				inStream.close();
			} catch (IOException e) {
				// TODO Handle exception
			}
		if (outStream != null)
			try {
				outStream.close();
			} catch (IOException e) {
				// TODO Handle exception
			}
		inStream = null;
		outStream = null;
		mapper = null;		
	}

	@Override
	public void serialize(Object obj) {
		if (!isActive) return;
		if (!rootClass.isInstance(obj))
			throw new IllegalArgumentException(String.format("JSONPersistenceSession: " +
					"unable to serialize %s, this session handles only %s", 
					ClassUtil.getQualifiedClassName(obj),
					ClassUtil.getQualifiedClassName(rootClass)));
		
		IPersistenceBean bean = PersistenceMapper.getBean(rootClass);
		
		bean.init(this);
		bean.set(obj);
		
//		Event ev = bean.read(Event.class);
//		if (ev != null) {
//			IResource r = ev.getResource();
//			if (r != null) {
//				System.out.println(r.getClass().getName());
//			}
//		}
		
		try {
			mapper.writeValue(outStream, bean);
			
		} catch (JsonGenerationException e) {
			// TODO Handle exception
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Handle exception
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Handle exception
			e.printStackTrace();
		}
	}

	@Override
	public void start(OutputStream outStream) {
		end();
		
		this.outStream = outStream;
		startSession();
	}

	private void startSession() {		
		mapper = new ObjectMapper();
		
		mapper.configure(SerializationConfig.Feature.INDENT_OUTPUT, true); 
		mapper.configure(SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS, false);
		mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		mapper.configure(JsonGenerator.Feature.AUTO_CLOSE_TARGET, false);
		
		jsonFactory = mapper.getJsonFactory();
		isActive = true;
	}

	@Override
	public void start(InputStream inStream) {
		end();
		
		this.inStream = inStream;		
		startSession();
		try {
			parser = jsonFactory.createJsonParser(inStream);
		} catch (JsonParseException e) {
			// TODO Handle exception
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Handle exception
			e.printStackTrace();
		}
	}

	@Override
	public boolean isActive() {
		return isActive;
	}

}
