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
package org.eclipse.swt.program;

import net.sf.okapi.common.ui.rwt.AbstractWebApp;
import net.sf.okapi.common.ui.rwt.RwtNotImplementedException;

import org.eclipse.swt.graphics.ImageData;

/**
 * Instances of this class represent programs and
 * their associated file extensions in the operating
 * system.
 *
 * @see <a href="http://www.eclipse.org/swt/snippets/#program">Program snippets</a>
 * @see <a href="http://www.eclipse.org/swt/">Sample code and further information</a>
 */
public final class Program {
	String name;
	String command;
	String iconName;
	String extension;
	static final String [] ARGUMENTS = new String [] {"%1", "%l", "%L"}; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

/**
 * Prevents uninitialized instances from being created outside the package.
 */
Program () {
	throw new RwtNotImplementedException(this, "()");
}

static String assocQueryString (int assocStr, char key, boolean expand) {
	throw new RwtNotImplementedException("org.eclipse.swt.program.Program.assocQueryString(int assocStr, char key, boolean expand)");
}

/**
 * Finds the program that is associated with an extension.
 * The extension may or may not begin with a '.'.  Note that
 * a <code>Display</code> must already exist to guarantee that
 * this method returns an appropriate result.
 *
 * @param extension the program extension
 * @return the program or <code>null</code>
 *
 * @exception IllegalArgumentException <ul>
 *		<li>ERROR_NULL_ARGUMENT when extension is null</li>
 *	</ul>
 */
public static Program findProgram (String extension) {
	throw new RwtNotImplementedException("org.eclipse.swt.program.Program.findProgram(String extension)");
}

/**
 * Answer all program extensions in the operating system.  Note
 * that a <code>Display</code> must already exist to guarantee
 * that this method returns an appropriate result.
 *
 * @return an array of extensions
 */
public static String [] getExtensions () {
	throw new RwtNotImplementedException("org.eclipse.swt.program.Program.getExtensions()");
}

static String getKeyValue (String string, boolean expand) {
	throw new RwtNotImplementedException("org.eclipse.swt.program.Program.getKeyValue (String string, boolean expand)");
}

static Program getProgram (String key, String extension) {
	throw new RwtNotImplementedException("org.eclipse.swt.program.Program.getProgram (String key, String extension)");
}

/**
 * Answers all available programs in the operating system.  Note
 * that a <code>Display</code> must already exist to guarantee
 * that this method returns an appropriate result.
 *
 * @return an array of programs
 */
public static Program [] getPrograms () {
	throw new RwtNotImplementedException("org.eclipse.swt.program.Program.getPrograms()");
}

/**
 * Launches the operating system executable associated with the file or
 * URL (http:// or https://).  If the file is an executable then the
 * executable is launched.  Note that a <code>Display</code> must already
 * exist to guarantee that this method returns an appropriate result.
 *
 * @param fileName the file or program name or URL (http:// or https://)
 * @return <code>true</code> if the file is launched, otherwise <code>false</code>
 * 
 * @exception IllegalArgumentException <ul>
 *    <li>ERROR_NULL_ARGUMENT when fileName is null</li>
 * </ul>
 */
public static boolean launch (String fileName) {
	//throw new RwtNotImplementedException("org.eclipse.swt.program.Program.launch (String fileName)");
	//Util.openURL(fileName);
	AbstractWebApp.getApp().openURL(fileName);	
	return true;
}

/**
 * Launches the operating system executable associated with the file or
 * URL (http:// or https://).  If the file is an executable then the
 * executable is launched. The program is launched with the specified
 * working directory only when the <code>workingDir</code> exists and
 * <code>fileName</code> is an executable.
 * Note that a <code>Display</code> must already exist to guarantee
 * that this method returns an appropriate result.
 *
 * @param fileName the file name or program name or URL (http:// or https://)
 * @param workingDir the name of the working directory or null
 * @return <code>true</code> if the file is launched, otherwise <code>false</code>
 * 
 * @exception IllegalArgumentException <ul>
 *    <li>ERROR_NULL_ARGUMENT when fileName is null</li>
 * </ul>
 * 
 * @since 3.6
 */
public static boolean launch (String fileName, String workingDir) {
	throw new RwtNotImplementedException("org.eclipse.swt.program.Program.launch (String fileName, String workingDir)");	
}

/**
 * Executes the program with the file as the single argument
 * in the operating system.  It is the responsibility of the
 * programmer to ensure that the file contains valid data for 
 * this program.
 *
 * @param fileName the file or program name
 * @return <code>true</code> if the file is launched, otherwise <code>false</code>
 * 
 * @exception IllegalArgumentException <ul>
 *    <li>ERROR_NULL_ARGUMENT when fileName is null</li>
 * </ul>
 */
public boolean execute (String fileName) {
	throw new RwtNotImplementedException(this, ".execute (String fileName)");
}

/**
 * Returns the receiver's image data.  This is the icon
 * that is associated with the receiver in the operating
 * system.
 *
 * @return the image data for the program, may be null
 */
public ImageData getImageData () {
	throw new RwtNotImplementedException(this, ".getImageData()");
}

/**
 * Returns the receiver's name.  This is as short and
 * descriptive a name as possible for the program.  If
 * the program has no descriptive name, this string may
 * be the executable name, path or empty.
 *
 * @return the name of the program
 */
public String getName () {
	throw new RwtNotImplementedException(this, ".getName()");
}

/**
 * Compares the argument to the receiver, and returns true
 * if they represent the <em>same</em> object using a class
 * specific comparison.
 *
 * @param other the object to compare with this object
 * @return <code>true</code> if the object is the same as this object and <code>false</code> otherwise
 *
 * @see #hashCode()
 */
public boolean equals(Object other) {
	throw new RwtNotImplementedException(this, ".equals(Object other)");
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

/**
 * Returns a string containing a concise, human-readable
 * description of the receiver.
 *
 * @return a string representation of the program
 */
public String toString () {
	throw new RwtNotImplementedException(this, ".toString()");
}

}
