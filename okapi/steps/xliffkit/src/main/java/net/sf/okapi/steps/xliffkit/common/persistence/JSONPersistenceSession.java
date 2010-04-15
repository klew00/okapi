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
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;
import java.util.Set;

import net.sf.okapi.common.StreamUtil;
import net.sf.okapi.common.Util;
import net.sf.okapi.common.exceptions.OkapiFileNotFoundException;
import net.sf.okapi.common.exceptions.OkapiIOException;

import org.codehaus.jackson.JsonEncoding;
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

	private static final String JSON_HEADER = "header";
	private static final String JSON_BODY = "body";
	private static final String JSON_ITEM = "item";
	private static final String VERSION = "1.0";
	private static final Class<?> rootClass = net.sf.okapi.common.Event.class;
	private static final String MIME_TYPE = "application/json"; // RFC http://www.ietf.org/rfc/rfc4627.txt	
	
	private ObjectMapper mapper;
	private JsonFactory jsonFactory;
	private JsonParser parser;
	private JsonGenerator headerGen;
	private JsonGenerator bodyGen;
	private OutputStream outStream;
	private OutputStream bodyOut;
	private File headerTemp;
	private File bodyTemp;
	private InputStream inStream;
	private boolean isActive;	
	private String description;
	private ReferenceResolver refResolver = new ReferenceResolver();
	private String itemLabel = JSON_ITEM;
	private int itemCounter = 0;

	public JSONPersistenceSession() {
		super();
		
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
	
	public JSONPersistenceSession(OutputStream outStream, String itemLabel) {
		start(outStream, itemLabel);
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
			throw new OkapiIOException(e);
		}
		
		if (bean == null) return null;
		return bean.get(classRef);
	}

	@Override
	public void end() {
		if (!isActive) return;		
		
		if (inStream != null)
			try {
				parser.close();
				inStream.close();
			} catch (IOException e) {
				throw new OkapiIOException(e);
			}
		if (outStream != null) {			
			// Finalize body
			try {
				//generator.writeEndObject();				
				bodyGen.writeRaw('}'); // writeEndObject() counts levels and throws exception instead
				
				bodyGen.close();
				bodyOut.close();
			} catch (JsonGenerationException e) {
				// TODO Handle exception
				e.printStackTrace();
			} catch (IOException e) {
				throw new OkapiIOException(e);
			}
			
			// Write header and references
			OutputStream headerOut;
			try {
				headerTemp = File.createTempFile("~temp", null);
				headerTemp.deleteOnExit();
				headerOut = new FileOutputStream(headerTemp);
				
				headerGen = jsonFactory.createJsonGenerator(headerOut, JsonEncoding.UTF8);
				headerGen.useDefaultPrettyPrinter();

				headerGen.writeStartObject();
				// All this, because references are built during body serialization, but need to be in header
				serialize(headerGen, this, JSON_HEADER);
				headerGen.writeFieldName(JSON_BODY);
				headerGen.writeRaw(" : ");
				
				headerGen.flush(); // !!!
				headerOut.close();
			} catch (JsonGenerationException e) {
				// TODO Handle exception
				e.printStackTrace();
			} catch (IOException e) {
				throw new OkapiIOException(e);
			}
			
			// Write out stream
			try {
				InputStream headerIn = new FileInputStream(headerTemp);
				InputStream bodyIn = new FileInputStream(bodyTemp);
				
				StreamUtil.copy(headerIn, outStream);
				StreamUtil.copy(bodyIn, outStream);
			
				// !!! Do not close external outStream
				
			} catch (FileNotFoundException e) {
				throw new OkapiFileNotFoundException(e);
			} 			
		}
		isActive = false;
		inStream = null;
		outStream = null;
		refResolver.reset();		
	}

	private void serialize(JsonGenerator generator, Object obj) {
		if (!isActive) return;
////		if (!rootClass.isInstance(obj))
////			throw new IllegalArgumentException(String.format("JSONPersistenceSession: " +
////					"unable to serialize %s, this session handles only %s", 
////					ClassUtil.getQualifiedClassName(obj),
////					ClassUtil.getQualifiedClassName(rootClass)));
//		
//		IPersistenceBean bean = BeanMapper.getBean(obj.getClass(), this);
//		
//		bean.set(obj);
//		
////		Event ev = bean.read(Event.class);
////		if (ev != null) {
////			IResource r = ev.getResource();
////			if (r != null) {
////				System.out.println(r.getClass().getName());
////			}
////		}
//		
//		try {
//			mapper.writeValue(generator, bean);
//			
//		} catch (JsonGenerationException e) {
//			// TODO Handle exception
//			e.printStackTrace();
//		} catch (JsonMappingException e) {
//			// TODO Handle exception
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Handle exception
//			e.printStackTrace();
//		}
		serialize(obj, String.format("%s%d", itemLabel, ++itemCounter));
	}
	
	@Override
	public void serialize(Object obj) {
		serialize(bodyGen, obj);
	}
	
	@Override
	public void serialize(Object obj, String name) {
		serialize(bodyGen, obj, name);
	}
	
	private void serialize(JsonGenerator generator, Object obj, String name) {
		if (!isActive) return;
		try {
			generator.writeFieldName(name);
		} catch (JsonGenerationException e) {
			// TODO Handle exception
			e.printStackTrace();
		} catch (IOException e) {
			throw new OkapiIOException(e);
		}
		IPersistenceBean bean = BeanMapper.getBean(obj.getClass(), this);
		if (bean == null) return;
		
		refResolver.setRootId(bean.getRefId());
		bean.set(obj);
		
		try {
			mapper.writeValue(generator, bean);
			
		} catch (JsonGenerationException e) {
			// TODO Handle exception
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Handle exception
			e.printStackTrace();
		} catch (IOException e) {
			throw new OkapiIOException(e);
		}
	}

	@Override
	public void start(OutputStream outStream) {
		start(outStream, null);
	}
	
	public void start(OutputStream outStream, String itemLabel) {
		if (outStream == null)
			throw(new IllegalArgumentException("JSONPersistenceSession: output stream cannot be null"));
		end();
		
		if (Util.isEmpty(itemLabel))
			this.itemLabel = JSON_ITEM;
		else
			this.itemLabel = itemLabel;
				
		itemCounter = 0;
		this.outStream = outStream;
		try {
			bodyTemp = File.createTempFile("~temp", null);
			bodyTemp.deleteOnExit();
			bodyOut = new FileOutputStream(bodyTemp);
			
			bodyGen = jsonFactory.createJsonGenerator(bodyOut, JsonEncoding.UTF8);
			bodyGen.useDefaultPrettyPrinter();
			bodyGen.writeStartObject();
		} catch (IOException e) {
			throw new OkapiIOException(e);
		}
		startSession();
	}

	private void startSession() {		
		refResolver.reset();
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
			throw new OkapiIOException(e);
		}
		startSession();
		//HeaderBean headerBean = deserialize(HeaderBean.class);
		deserialize(HeaderBean.class);
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

	@Override
	public int generateRefId() {
		return refResolver.generateRefId();
	}

	@Override
	public int getRefIdForObject(Object obj) {
		return refResolver.getRefIdForObject(obj);
	}

	@Override
	public void setRefIdForObject(Object obj, int refId) {
		refResolver.setRefIdForObject(obj, refId);
	}

	@Override
	public void setReference(int parentRefId, int childRefId) {
		refResolver.addReference(parentRefId, childRefId);
	}

	@Override
	public Map<Integer, Set<Integer>> getReferences() {		
		return refResolver.getReferences();
	}
}
