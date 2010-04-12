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

package net.sf.okapi.steps.xliffkit.common.persistence;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonParser.Feature;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;

public class JSONPersistenceSession implements IPersistenceSession {

	private static final String VERSION = "1.0";
	private static final Class<?> rootClass = net.sf.okapi.common.Event.class;
	private static final String MIME_TYPE = "application/json";
	// RFC http://www.ietf.org/rfc/rfc4627.txt
	
	private ObjectMapper mapper;
	private JsonFactory jsonFactory;
	private JsonParser parser;	
	private OutputStream outStream;
	private InputStream inStream;
	private boolean isActive;	
	private String description;
	//private Class<? extends IPersistenceBean> beanClass;

	public JSONPersistenceSession() {
		super();
		//this.beanClass = BeanMapper.getBeanClass(rootClass);
		
		mapper = new ObjectMapper();		
		mapper.configure(SerializationConfig.Feature.INDENT_OUTPUT, true); 
		mapper.configure(SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS, false);
		mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		mapper.configure(DeserializationConfig.Feature.USE_ANNOTATIONS, true);
		mapper.configure(Feature.AUTO_CLOSE_SOURCE, false);
		mapper.configure(JsonGenerator.Feature.AUTO_CLOSE_TARGET, false);
		
		jsonFactory = mapper.getJsonFactory();
	}
	
	public JSONPersistenceSession(OutputStream outStream) {
		start(outStream);
	}
	
	public JSONPersistenceSession(InputStream inStream) {
		start(inStream);
	}
	
	@Override
	public <T extends IPersistenceBean> T convert(Object object, Class<T> expectedClass) {		
		return mapper.convertValue(object, expectedClass);
	}

	@Override
	public <T> T deserialize(Class<T> classRef) {
		if (!isActive) return null;
		
		IPersistenceBean bean = null;
		try {
			Class<? extends IPersistenceBean> beanClass = BeanMapper.getBeanClass(classRef);
				//this.beanClass = BeanMapper.getBeanClass(rootClass);
			bean = mapper.readValue(parser, beanClass);
			//bean = mapper.readValue(parser, IPersistenceBean.class);
						
		} catch (JsonParseException e) {
			// TODO Handle exception
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Handle exception
			e.printStackTrace();
		} catch (EOFException e) {
			// Normal situation, reached EOF, close session, return null
			end();
			return null;
		} catch (IOException e) {
			// TODO Handle exception
			e.printStackTrace();
		}
		
		if (bean == null) return null;
		return bean.get(classRef);
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
		SessionMapper.endSession();
	}

	@Override
	public void serialize(Object obj) {
		if (!isActive) return;
//		if (!rootClass.isInstance(obj))
//			throw new IllegalArgumentException(String.format("JSONPersistenceSession: " +
//					"unable to serialize %s, this session handles only %s", 
//					ClassUtil.getQualifiedClassName(obj),
//					ClassUtil.getQualifiedClassName(rootClass)));
		
		IPersistenceBean bean = BeanMapper.getBean(obj.getClass());
		
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
		if (outStream == null)
			throw(new IllegalArgumentException("JSONPersistenceSession: output stream cannot be null"));
		end();
		
		this.outStream = outStream;
		startSession();
		serialize(this); // Write out the header
	}

	private void startSession() {		
		SessionMapper.startSession(this);
		isActive = true;		
	}

	@Override
	public void start(InputStream inStream) {
		if (inStream == null)
			throw(new IllegalArgumentException("JSONPersistenceSession: input stream cannot be null"));
		end();
		
		this.inStream = inStream;				
		try {
			parser = jsonFactory.createJsonParser(inStream);
		} catch (JsonParseException e) {
			// TODO Handle exception
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Handle exception
			e.printStackTrace();
		}
		startSession();
		SessionInfo inputSessionInfo = deserialize(SessionInfo.class);
		SessionMapper.configureSession(this, inputSessionInfo);
	}

	@Override
	public boolean isActive() {
		return isActive;
	}

	@Override
	public String getMimeType() {
		return MIME_TYPE;
	}

	@Override
	public String getVersion() {
		return VERSION;
	}

	@Override
	public String getRootClass() {
		return (rootClass == null) ? "" : rootClass.getName();
	}

	@Override
	public String getDescription() {		
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
