/*===========================================================================
  Copyright (C) 2008-2013 by the Okapi Framework contributors
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

package java.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;

import org.eclipse.rwt.RWT;

import net.sf.okapi.common.ui.rwt.RwtNotImplementedException;

/**
 * Stores user settings in a RWT session settings store (persisted in browser cookies).
 */
public class Properties extends Hashtable<Object,Object> {

	private static final long serialVersionUID = 1188319440709317260L;

    public Properties() {
    	this(null);
    }

    public Properties(Properties defaults) {
    }

    public synchronized Object setProperty(String key, String value) {
    	Object prevValue = RWT.getSettingStore().getAttribute(key);
    	System.out.println(key + "=" + value);
		try {
			if ( value == null )
				RWT.getSettingStore().removeAttribute(key);
			else			 
				RWT.getSettingStore().setAttribute(key, value);
		} catch (Exception e) {
			// Silently ignore
		}
		return prevValue;
    }

    public synchronized void load(Reader reader) throws IOException {
    	throw new RwtNotImplementedException(this, ".load(Reader reader)");
    }

    public synchronized void load(InputStream inStream) throws IOException {
    	throw new RwtNotImplementedException(this, ".load(InputStream inStream)");
    }

    @Deprecated
    public synchronized void save(OutputStream out, String comments)  {
    	throw new RwtNotImplementedException(this, ".save(OutputStream out, String comments)");
    }

    public void store(Writer writer, String comments)
        throws IOException
    {
    	throw new RwtNotImplementedException(this, ".store(Writer writer, String comments)");
    }

    public void store(OutputStream out, String comments)
        throws IOException
    {
    	throw new RwtNotImplementedException(this, ".store(OutputStream out, String comments)");
    }

    public synchronized void loadFromXML(InputStream in)
        throws IOException, InvalidPropertiesFormatException 
    {
    	throw new RwtNotImplementedException(this, ".loadFromXML(InputStream in)");
    }
    
    public synchronized void storeToXML(OutputStream os, String comment)
        throws IOException
    {
    	throw new RwtNotImplementedException(this, ".storeToXML(OutputStream os, String comment)");
    }

    public synchronized void storeToXML(OutputStream os, String comment, 
                                       String encoding)
        throws IOException
    {
    	throw new RwtNotImplementedException(this, ".storeToXML(OutputStream os, String comment, String encoding)");
    }

    public String getProperty(String key) {
    	return (String) RWT.getSettingStore().getAttribute(key);
    }

    public String getProperty(String key, String defaultValue) {
    	Object value = RWT.getSettingStore().getAttribute(key);
		return (value instanceof String) ? (String) value : defaultValue;
    }

    public Enumeration<?> propertyNames() {
    	return RWT.getSettingStore().getAttributeNames();
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
	public Set<String> stringPropertyNames() {    	
    	return new HashSet(Collections.list(propertyNames()));
    }

    public void list(PrintStream out) {
    	throw new RwtNotImplementedException(this, ".list(PrintStream out)");
    }

    public void list(PrintWriter out) {
    	throw new RwtNotImplementedException(this, ".list(PrintWriter out)");
    }

}
