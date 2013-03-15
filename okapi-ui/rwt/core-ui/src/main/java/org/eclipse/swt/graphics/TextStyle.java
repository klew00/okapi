// Modified content of swt-4.2.1-win32-win32-x86.zip

/*******************************************************************************
 * Copyright (c) 2000, 2010 IBM Corporation and others.
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

import org.eclipse.swt.SWT;

/**
 * <code>TextStyle</code> defines a set of styles that can be applied
 * to a range of text.
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
 * @see TextLayout
 * @see Font
 * @see Color
 * @see <a href="http://www.eclipse.org/swt/snippets/#textlayout">TextLayout, TextStyle snippets</a>
 * @see <a href="http://www.eclipse.org/swt/">Sample code and further information</a>
 *  
 * @since 3.0
 */
public class TextStyle {

	/**
	 * the font of the style
	 */
	public Font font;

	/**
	 * the foreground of the style
	 */
	public Color foreground;

	/**
	 * the background of the style
	 */
	public Color background;

	/**
	 * the underline flag of the style. The default underline
	 * style is <code>SWT.UNDERLINE_SINGLE</code>.
	 * 
	 * 
	 * @since 3.1
	 */	
	public boolean underline;
	
	/**
	 * the underline color of the style
	 * 
	 * @since 3.4
	 */	
	public Color underlineColor;

	/**
	 * the underline style. This style is ignored when
	 * <code>underline</code> is false.
	 * <p> 
	 * This value should be one of <code>SWT.UNDERLINE_SINGLE</code>,
	 * <code>SWT.UNDERLINE_DOUBLE</code>, <code>SWT.UNDERLINE_ERROR</code>,
	 * <code>SWT.UNDERLINE_SQUIGGLE</code>, or <code>SWT.UNDERLINE_LINK</code>.
	 * </p>
	 * 
	 * @see SWT#UNDERLINE_SINGLE
	 * @see SWT#UNDERLINE_DOUBLE 
	 * @see SWT#UNDERLINE_ERROR
	 * @see SWT#UNDERLINE_SQUIGGLE
	 * @see SWT#UNDERLINE_LINK
	 * 
	 * @since 3.4
	 */	
	public int underlineStyle;

	/**
	 * the strikeout flag of the style
	 * 
	 * @since 3.1
	 */	
	public boolean strikeout;
	
	/**
	 * the strikeout color of the style
	 * 
	 * @since 3.4
	 */	
	public Color strikeoutColor;
	
	/**
	 * the border style. The default border style is <code>SWT.NONE</code>.
	 * <p> 
	 * This value should be one of <code>SWT.BORDER_SOLID</code>,
	 * <code>SWT.BORDER_DASH</code>,<code>SWT.BORDER_DOT</code> or
	 * <code>SWT.NONE</code>.
	 * </p>
	 * 
	 * @see SWT#BORDER_SOLID
	 * @see SWT#BORDER_DASH 
	 * @see SWT#BORDER_DOT
	 * @see SWT#NONE
	 * 
	 * @since 3.4
	 */	
	public int borderStyle;
	
	/**
	 * the border color of the style
	 * 
	 * @since 3.4
	 */	
	public Color borderColor;
	
	/**
	 * the GlyphMetrics of the style
	 * 
	 * @since 3.2
	 */	
	public GlyphMetrics metrics;
	
	/**
	 * the baseline rise of the style. 
	 * 
	 * @since 3.2
	 */	
	public int rise;
	
	
	/**
	 * the data. An user data field. It can be used to hold the HREF when the range 
	 * is used as a link or the embed object when the range is used with <code>GlyphMetrics</code>.
	 * <p>
	 * 
	 * @since 3.5
	 */
	public Object data;


/** 
 * Create an empty text style.
 *
 * @since 3.4
 */
public TextStyle () {
	throw new RwtNotImplementedException(this, "()");
}
	
/** 
 * Create a new text style with the specified font, foreground
 * and background.
 *
 * @param font the font of the style, <code>null</code> if none 
 * @param foreground the foreground color of the style, <code>null</code> if none 
 * @param background the background color of the style, <code>null</code> if none
 */
public TextStyle (Font font, Color foreground, Color background) {
	throw new RwtNotImplementedException(this, "(Font font, Color foreground, Color background)");
}


/** 
 * Create a new text style from an existing text style.
 * 
 * @param style the style to copy 
 *
 * @since 3.4
 */
public TextStyle (TextStyle style) {
	throw new RwtNotImplementedException(this, "(TextStyle style)");
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
public boolean equals(Object object) {
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
public int hashCode() {
	throw new RwtNotImplementedException(this, ".hashCode()");
}

boolean isAdherentBorder(TextStyle style) {
	throw new RwtNotImplementedException(this, ".isAdherentBorder(TextStyle style)");
}

boolean isAdherentUnderline(TextStyle style) {
	throw new RwtNotImplementedException(this, ".isAdherentUnderline(TextStyle style)");
}

boolean isAdherentStrikeout(TextStyle style) {
	throw new RwtNotImplementedException(this, ".isAdherentStrikeout(TextStyle style)");
}

/**
 * Returns a string containing a concise, human-readable
 * description of the receiver.
 *
 * @return a string representation of the <code>TextStyle</code>
 */
public String toString () {
	throw new RwtNotImplementedException(this, ".toString()");
}

}
