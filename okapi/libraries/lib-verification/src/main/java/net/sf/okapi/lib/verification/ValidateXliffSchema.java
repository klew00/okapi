/*===========================================================================
  Copyright (C) 2012 by the Okapi Framework contributors
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

package net.sf.okapi.lib.verification;

import java.io.File;
import java.io.InputStream;
import java.net.URI;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.w3c.dom.DOMImplementation;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSResourceResolver;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import net.sf.okapi.common.exceptions.OkapiIOException;

class JarLSResourceResolver implements LSResourceResolver {

    @Override
    public LSInput resolveResource(String type, String namespaceURI, String publicId, String systemId, String baseURI) {
        LSInput result = null;
        try {
            String featureName = "XML 1.0";
            DOMImplementationRegistry registry = DOMImplementationRegistry.newInstance();
            DOMImplementation impl = registry.getDOMImplementation(featureName);
            DOMImplementationLS ls = (DOMImplementationLS) impl;

            result = ls.createLSInput();
            InputStream is = JarLSResourceResolver.class.getResourceAsStream("xml.xsd");
            result.setByteStream(is);
        } catch (ClassNotFoundException ex) {
        } catch (InstantiationException ex) {
        } catch (IllegalAccessException ex) {
        }
        if (result == null)
            throw new OkapiIOException("XLIFF Validation : internal DOM error, cannot load xml.xsd");

        return result;
    }
}

public class ValidateXliffSchema {
    private final static String xliffSchema = "xliff-core-1.2-transitional.xsd";
    private static Validator    validator;

    static {
        try {
            InputStream is = ValidateXliffSchema.class.getResourceAsStream(xliffSchema);

            SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            // my own resolver to load xml.xsd from a jar
            LSResourceResolver jarResolver = new JarLSResourceResolver();
            factory.setResourceResolver(jarResolver);

            Schema schema = factory.newSchema(new StreamSource(is));
            validator = schema.newValidator();
        } catch (SAXParseException ex) {
        } catch (SAXException ex) {
        }
		if (validator == null)
            throw new OkapiIOException("XLIFF Schame Validation : internal error, invalid " + xliffSchema);
    }

    public static boolean validateXliffSchema(URI fileURI) {
        if (validator == null)
            return true;
        try {
            validator.validate(new StreamSource(new File(fileURI)));
            return true;
        } catch (SAXParseException ex) {
            String message = ex.getMessage();
            if (!message.startsWith("Duplicate key value [")
                    || !message.endsWith("] declared for identity constraint of element \"file\".")) {
                String errorString = String.format("XLIFF Validation Error [%d, %d]:\n  %s\n  %s", ex.getLineNumber(),
                        ex.getColumnNumber(), ex.getSystemId(), ex.getMessage());
                throw new OkapiIOException(errorString);
            }
        } catch (Exception ex) {
            throw new OkapiIOException("XLIFF Validation Error: " + ex.getMessage());
        }
        return false;
    }
}
