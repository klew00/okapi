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

package net.sf.okapi.persistence.json.jackson;

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
import net.sf.okapi.common.annotation.Annotations;
import net.sf.okapi.common.exceptions.OkapiFileNotFoundException;
import net.sf.okapi.common.exceptions.OkapiIOException;
import net.sf.okapi.lib.persistence.IPersistenceBean;
import net.sf.okapi.lib.persistence.PersistenceSession;

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

@SuppressWarnings("unchecked")
public abstract class JSONPersistenceSession extends PersistenceSession {
	public static final String MSG_JSON_READ_EX = "JSONPersistenceSession: error reading.";
	public static final String MSG_JSON_WRITE_EX = "JSONPersistenceSession: error writing.";
	
	private static final String JSON_HEADER = "header"; //$NON-NLS-1$
	private static final String JSON_BODY = "body"; //$NON-NLS-1$	
	private static final String JSON_VER = "version"; //$NON-NLS-1$
	private static final String JSON_DESCR = "description"; //$NON-NLS-1$
	private static final String JSON_CLASS = "itemClass"; //$NON-NLS-1$
	private static final String JSON_MIME = "mimeType"; //$NON-NLS-1$
	private static final String JSON_FRAMES = "frames"; //$NON-NLS-1$
	private static final String JSON_ANNOTATIONS = "annotations"; //$NON-NLS-1$

	//private static final String VERSION = "1.0"; //$NON-NLS-1$	
	// JSON RFC http://www.ietf.org/rfc/rfc4627.txt
	private static final String MIME_TYPE = "application/json";  //$NON-NLS-1$ 	
	
	private ObjectMapper mapper;
	private JsonFactory jsonFactory;
	private JsonParser parser;
	private JsonGenerator headerGen;
	private JsonGenerator bodyGen;	
	private OutputStream bodyOut;
	private File headerTemp;
	private File bodyTemp;	

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
	
	@Override
	public <T extends IPersistenceBean<?>> T convert(Object object, Class<T> expectedClass) {		
		return mapper.convertValue(object, expectedClass);
	}

	@Override
	protected <T extends IPersistenceBean<?>> T readBean(Class<T> beanClass, String name) {
		
		T bean = null;
		try {
			JsonToken token = parser.nextToken();
			if (token == JsonToken.END_OBJECT)
				return null;

			String fieldName = parser.getCurrentName();
			if (fieldName != null && name != null && !fieldName.startsWith(name))
				throw(new OkapiIOException(String.format("JSONPersistenceSession: input stream " +
						"is broken. Item label should start with \"%s\", but was \"%s\"", name, fieldName)));			
			parser.nextToken();
			
			bean = mapper.readValue(parser, beanClass);
//			JsonToken token = parser.nextToken();
//			if (token == JsonToken.END_OBJECT)
//				end();
						
		} catch (JsonParseException e) {			
			throw new RuntimeException(MSG_JSON_READ_EX, e);
		} catch (JsonMappingException e) {
			throw new RuntimeException(MSG_JSON_READ_EX, e);
		} catch (EOFException e) {
			throw new OkapiIOException("JSONPersistenceSession: input stream is broken -- unexpected EOF.", e);
		} catch (IOException e) {
			throw new OkapiIOException(e);
		}
		
		return bean;
	}
	
	@Override
	protected void writeBean(IPersistenceBean<?> bean, String name) {
		try {
			bodyGen.writeFieldName(name);
			mapper.writeValue(bodyGen, bean);
		} catch (JsonGenerationException e) {
			throw new RuntimeException(MSG_JSON_WRITE_EX, e);
		} catch (IOException e) {
			throw new OkapiIOException(e);
		}
	}
	
	@Override
	public String getMimeType() {
		return MIME_TYPE;
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
			throw new RuntimeException(MSG_JSON_READ_EX, e);
		} catch (IOException e) {
			throw new OkapiIOException(e);
		}
		return res;
	}
	
	@Override
	@SuppressWarnings("unused")
	protected void startReading(InputStream inStream) {
		try {
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
			setVersion(version);
															
			String description = readFieldValue(JSON_DESCR);
			String itemClass = readFieldValue(JSON_CLASS);
			String mimeType = readFieldValue(JSON_MIME);
			
			// Frames
			parser.nextToken();
			if (!JSON_FRAMES.equalsIgnoreCase(parser.getCurrentName()))
				throw(new OkapiIOException("JSONPersistenceSession: input stream is broken"));
			
			parser.nextToken();
						
			this.setFrames(mapper.readValue(parser, List.class));
			parser.nextToken();
			
			// Optional annotations entry
			if (JSON_ANNOTATIONS.equalsIgnoreCase(parser.getCurrentName())) {
				parser.nextToken();
				Class<IPersistenceBean<Annotations>> beanClass = 
						this.getBeanClass(Annotations.class);
				IPersistenceBean<Annotations> bean = mapper.readValue(parser, beanClass);
				this.setAnnotations(bean);
				parser.nextToken();
			}
						
			parser.nextToken();
			
			// Prepare the stream for items deserialization
			if (!JSON_BODY.equalsIgnoreCase(parser.getCurrentName()))
				throw(new OkapiIOException("JSONPersistenceSession: input stream is broken"));
			parser.nextToken();
			if (token != JsonToken.START_OBJECT)
				throw(new OkapiIOException("JSONPersistenceSession: input stream is broken"));
			//parser.nextToken();
			
		} catch (JsonParseException e) {
			throw(new OkapiIOException("JSONPersistenceSession: input stream is broken. ", e));
		} catch (IOException e) {
			throw new OkapiIOException(e);
		}
	}
	
	@Override
	protected void endReading(InputStream inStream) {
		try {
			parser.close();
		} catch (IOException e) {
			throw new OkapiIOException(e);
		}
	}

	@Override
	protected void startWriting(OutputStream outStream) {
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
	
	@Override
	protected void endWriting(OutputStream outStream) {
		// Finalize body
		try {
			bodyGen.writeRaw('}'); // writeEndObject() counts levels and throws exception instead
			
			bodyGen.close();
			bodyOut.close();
		} catch (JsonGenerationException e) {
			throw new RuntimeException(MSG_JSON_WRITE_EX, e);
		} catch (IOException e) {
			throw new OkapiIOException(e);
		}
		
		// Write header and frames
		OutputStream headerOut;
		try {
			headerTemp = File.createTempFile("~temp", null);
			headerTemp.deleteOnExit();
			headerOut = new FileOutputStream(headerTemp);
			
			headerGen = jsonFactory.createJsonGenerator(headerOut, JsonEncoding.UTF8);
			headerGen.useDefaultPrettyPrinter();

			headerGen.writeStartObject(); // The file root
			
			// All this, because references are built during body serialization, but need to be in header
			headerGen.writeFieldName(JSON_HEADER);			
			headerGen.writeStartObject();
			headerGen.writeStringField(JSON_VER, this.getVersion());
			headerGen.writeStringField(JSON_DESCR, this.getDescription());			
			headerGen.writeStringField(JSON_CLASS, this.getItemClass());
			headerGen.writeStringField(JSON_MIME, this.getMimeType());			
			headerGen.writeObjectField(JSON_FRAMES, this.getFrames());
			headerGen.writeObjectField(JSON_ANNOTATIONS, this.getAnnotationsBean());			
			headerGen.writeEndObject();
			
			headerGen.writeFieldName(JSON_BODY);
			headerGen.writeRaw(" : ");
			
			headerGen.flush(); // !!!
			headerOut.close();
		} catch (JsonGenerationException e) {
			throw new RuntimeException(MSG_JSON_WRITE_EX, e);
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

	@Override
	protected String writeBeanToString(IPersistenceBean<?> bean) {
		try {
			return mapper.writeValueAsString(bean);
		} catch (JsonGenerationException e) {
			throw new RuntimeException(MSG_JSON_WRITE_EX, e);
		} catch (IOException e) {
			throw new OkapiIOException(e);
		}
	}

	@Override
	protected <T extends IPersistenceBean<?>> T readBeanFromString(
			String content, Class<T> beanClass) {
		try {
			return mapper.readValue(content, beanClass);
		} catch (JsonParseException e) {			
			throw new RuntimeException(MSG_JSON_READ_EX, e);
		} catch (JsonMappingException e) {
			throw new RuntimeException(MSG_JSON_READ_EX, e);
		} catch (IOException e) {
			throw new OkapiIOException(e);
		}
	}
}
