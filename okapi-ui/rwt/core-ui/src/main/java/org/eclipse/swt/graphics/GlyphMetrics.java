// Modified content of swt-4.2.1-win32-win32-x86.zip

/*******************************************************************************
 * Copyright (c) 2000, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.swt.graphics;

import net.sf.okapi.common.ui.rwt.RwtNotImplementedException;

import org.eclipse.swt.*;

/**
 * Instances of this class represent glyph metrics.
 * <p>
 * The hashCode() method in this class uses the values of the public
 * fields to compute the hash value. When storing instances of the
 * class in hashed collections, do not modify these fields after the
 * object has been inserted.  
 * </p>
 * <p>
 * Application code does <em>not</em> need to explicitly release the
 * resources managed by each instance when those instances are no longer
 * required, and thus no <code>dispose()</code> method is provided.
 * </p>
 * 
 * @see TextStyle
 * @see TextLayout
 * @see <a href="http://www.eclipse.org/swt/">Sample code and further information</a>
 * 
 * @since 3.2
 */
public final class GlyphMetrics {
	
	/**
	 * the ascent of the GlyphMetrics
	 */
	public int ascent;
	
	/**
	 * the descent of the GlyphMetrics
	 */
	public int descent;
	
	/**
	 * the width of the GlyphMetrics
	 */
	public int width;
	
/**
 * Constructs an instance of this class with the given
 * ascent, descent and width values.
 *
 * @param ascent the GlyphMetrics ascent
 * @param descent the GlyphMetrics descent
 * @param width the GlyphMetrics width
 *
 * @exception IllegalArgumentException <ul>
 *    <li>ERROR_INVALID_ARGUMENT - if the ascent, descent or width argument is negative</li>
 * </ul>
 */
public GlyphMetrics(int ascent, int descent, int width) {
	throw new RwtNotImplementedException(this, "int ascent, int descent, int width");
}

/**
 * Compares the argument to the receiver, and returns true
 * if they represent the <em>same</em> object using a class
 * specific comparison.
 *
 * @param object the object to compare with this object
 * @return <code>true</code> if the object is the same as this object and <code>false</code> otherwise
 *
 * @see #hashCode()
 */
public boolean equals (Object object) {
	throw new RwtNotImplementedException(this, ".equals(Object object)");
}

/**
 * Returns an integer hash code for the receiver. Any two 
 * objects that return <code>true</code> when passed to 
 * <code>equals</code> must return the same value for this
 * method.
 *
 * @return the receiver's hash
 *
 * @see #equals(Object)
 */
public int hashCode () {
	throw new RwtNotImplementedException(this, ".hashCode()");
}

/**
 * Returns a string containing a concise, human-readable
 * description of the receiver.
 *
 * @return a string representation of the <code>GlyphMetrics</code>
 */
public String toString () {
	throw new RwtNotImplementedException(this, ".toString()");
}

}
