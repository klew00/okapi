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
import java.util.List;

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
import org.codehaus.jackson.JsonToken;
import org.codehaus.jackson.JsonParser.Feature;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;

public class JSONPersistenceSession implements IPersistenceSession {

	private static final String JSON_HEADER = "header"; //$NON-NLS-1$
	private static final String JSON_BODY = "body"; //$NON-NLS-1$
	private static final String JSON_ITEM = "item"; //$NON-NLS-1$
	private static final String JSON_VER = "version"; //$NON-NLS-1$
	private static final String JSON_DESCR = "description"; //$NON-NLS-1$
	private static final String JSON_CLASS = "itemClass"; //$NON-NLS-1$
	private static final String JSON_MIME = "mimeType"; //$NON-NLS-1$
	//private static final String JSON_REFS = "references"; //$NON-NLS-1$
	private static final String JSON_FRAMES = "frames"; //$NON-NLS-1$
		
	private static final String VERSION = "1.0"; //$NON-NLS-1$
	private static final Class<?> rootClass = net.sf.okapi.common.Event.class;
	// JSON RFC http://www.ietf.org/rfc/rfc4627.txt
	private static final String MIME_TYPE = "application/json";  //$NON-NLS-1$ 	
	
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
	//private boolean isActive;	
	private String description;
	private ReferenceResolver refResolver = new ReferenceResolver();
	private String itemLabel = JSON_ITEM;
	private int itemCounter = 0;
	private Class<?> prevClass;
	private Class<? extends IPersistenceBean> beanClass;
	private SessionState state = SessionState.IDLE;

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

	private <T> T deserialize(Class<T> classRef, String name) {
		if (state != SessionState.READING) return null;
		
		// Update bean class if core class has changed
		if (classRef != prevClass) { 
			beanClass = BeanMapper.getBeanClass(classRef);
			prevClass = classRef;
		}
		
		IPersistenceBean bean = null;
		try {
			String fieldName = parser.getCurrentName();
			if (fieldName != null && name != null && !fieldName.startsWith(name))
				throw(new OkapiIOException("JSONPersistenceSession: input stream is broken"));			
			parser.nextToken();
			
			bean = mapper.readValue(parser, beanClass);
			JsonToken token = parser.nextToken();
			if (token == JsonToken.END_OBJECT)
				end();
			//bean = mapper.readValue(parser, IPersistenceBean.class);
						
		} catch (JsonParseException e) {
			// TODO Handle exception
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Handle exception
			e.printStackTrace();
		} catch (EOFException e) {
//			// Normal situation, reached EOF, close session, return null
//			end();
			// TODO Handle exception
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			throw new OkapiIOException(e);
		}
		
		if (bean == null) return null;
		return bean.get(classRef, this);
	}
	
	@Override
	public <T> T deserialize(Class<T> classRef) {
		return deserialize(classRef, itemLabel);
	}

	@Override
	public void end() {
		if (state == SessionState.IDLE) return;		
		
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
				//serialize(headerGen, this, JSON_HEADER);
				headerGen.writeFieldName(JSON_HEADER);
				headerGen.writeStartObject();
				headerGen.writeStringField(JSON_VER, this.getVersion());
				headerGen.writeStringField(JSON_DESCR, this.getDescription());
				headerGen.writeStringField(JSON_CLASS, this.getItemClass());
				headerGen.writeStringField(JSON_MIME, this.getMimeType());
				
				refResolver.updateFrames();
				headerGen.writeObjectField(JSON_FRAMES, refResolver.getFrames());
				
				headerGen.writeEndObject();
				
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
		inStream = null;
		outStream = null;
		prevClass = null;
		beanClass = null;
		refResolver.reset();	
		state = SessionState.IDLE;
	}

	private void serialize(JsonGenerator generator, Object obj) {
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
		if (state != SessionState.WRITING) return;
		try {
			generator.writeFieldName(name);
		} catch (JsonGenerationException e) {
			// TODO Handle exception
			e.printStackTrace();
		} catch (IOException e) {
			throw new OkapiIOException(e);
		}
		IPersistenceBean bean = refResolver.createBean(obj.getClass());
		if (bean == null) return;
		
		refResolver.setRootId(bean.getRefId());
		bean.set(obj, this);
		
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
		
		state = SessionState.WRITING;
		refResolver.reset();
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
	}

	private String readFieldValue(String fieldName) {
		String res = ""; 
		try {
			parser.nextToken();
			if (!Util.isEmpty(fieldName))
				if (!fieldName.equalsIgnoreCase(parser.getCurrentName()))
					throw(new OkapiIOException("JSONPersistenceSession: input stream is broken"));
			
			parser.nextToken();
			res = parser.getText();
		} catch (JsonParseException e) {
			// TODO Handle exception
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Handle exception
			e.printStackTrace();
		}
		return res;
	}
	
	@Override
	@SuppressWarnings({ "unused", "unchecked" })
	public void start(InputStream inStream) {
		if (inStream == null)
			throw(new IllegalArgumentException("JSONPersistenceSession: input stream cannot be null"));
		end();
		
		this.inStream = inStream;				
		try {			
			state = SessionState.READING;
			refResolver.reset();
			parser = jsonFactory.createJsonParser(inStream);
			
			JsonToken token = parser.nextToken(); 
			if (token != JsonToken.START_OBJECT)
				throw(new OkapiIOException("JSONPersistenceSession: input stream is broken"));
			
			parser.nextToken();						
			if (!JSON_HEADER.equalsIgnoreCase(parser.getCurrentName()))
				throw(new OkapiIOException("JSONPersistenceSession: input stream is broken"));
			
			token = parser.nextToken(); 
			if (token != JsonToken.START_OBJECT)
				throw(new OkapiIOException("JSONPersistenceSession: input stream is broken"));
			
			// Header
			String version = readFieldValue(JSON_VER);
			if (!VERSION.equalsIgnoreCase(version)) {
				// Version control
			}									
			String description = readFieldValue(JSON_DESCR);
			String itemClass = readFieldValue(JSON_CLASS);
			String mimeType = readFieldValue(JSON_MIME);
			
			// Frames
			parser.nextToken();
			if (!JSON_FRAMES.equalsIgnoreCase(parser.getCurrentName()))
				throw(new OkapiIOException("JSONPersistenceSession: input stream is broken"));
			
			parser.nextToken();
						
			refResolver.setFrames(mapper.readValue(parser, List.class));
			parser.nextToken();
			parser.nextToken();
			
			// Prepare the stream for items deserialization
			if (!JSON_BODY.equalsIgnoreCase(parser.getCurrentName()))
				throw(new OkapiIOException("JSONPersistenceSession: input stream is broken"));
			parser.nextToken();
			if (token != JsonToken.START_OBJECT)
				throw(new OkapiIOException("JSONPersistenceSession: input stream is broken"));
			parser.nextToken();
			
		} catch (JsonParseException e) {
			throw(new OkapiIOException("JSONPersistenceSession: input stream is broken. ", e));
		} catch (IOException e) {
			throw new OkapiIOException(e);
		}		
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
	public String getItemClass() {
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
	public IPersistenceBean createBean(Class<?> classRef) {
		return refResolver.createBean(classRef);
	}

	@Override
	public void cacheBean(Object obj, IPersistenceBean bean) {
		refResolver.cacheBean(obj, bean);
	}

	@Override
	public IPersistenceBean uncacheBean(Object obj) {
		return refResolver.uncacheBean(obj);
	}

	@Override
	public SessionState getState() {		
		return state;
	}
}
