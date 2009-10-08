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

package net.sf.okapi.common;

public class ClassUtil {
	
	/**
	 * Gets non-qualified (w/o package name prefix) class name for the given object.
	 * @param obj The object
	 * @return The object's class name (w/o package name prefix)
	 */
	public static String getClassName(Object obj) {
		
		if (obj == null) return "";
	
		return getClassName(obj.getClass());
	}
	
	/**
	 * Gets non-qualified (w/o package name prefix) name for the given class.
	 * @param classRef Class reference
	 * @return The name of the class (w/o package name prefix)
	 */
	public static String getClassName(Class<?> classRef) {
		
		if (classRef == null) return "";
	
		return classRef.getSimpleName();
	}

	/**
	 * Gets a qualified name of the given object.
	 * @param obj The object
	 * @return Qualified class name
	 */
	public static String getQualifiedName(Object obj) {
		
		if (obj == null) return "";
		
		return getQualifiedName(obj.getClass());
	}
	
	/**
	 * Gets a qualified class name.
	 * @param classRef Class reference
	 * @return Qualified class name
	 */
	public static String getQualifiedName(Class<?> classRef) {
		
		if (classRef == null) return "";
		
		return classRef.getName();
	}
		
	/**
	 * Gets the name of the package containing the given object's class.
	 * @param obj The object
	 * @return Package name of the object's class (without the trailing dot), or an empty string
	 */
	public static String getPackageName(Object obj) {

		if (obj == null) return "";
		
		return getPackageName(obj.getClass());
	}
	
	/**
	 * Gets a package name for the given class. 
	 * @param classRef Class reference
	 * @return Package name of the class (without the trailing dot), or an empty string 
	 */
	public static String getPackageName(Class<?> classRef) {
		
		if (classRef == null) return "";
		
		Package pkg = classRef.getPackage();
		
		if (pkg == null) {
			
			String className = classRef.getName();
			String shortClassName = classRef.getSimpleName();
			
			int index = className.lastIndexOf(shortClassName);
            if (index != -1) {
            	
            	String res = className.substring(0, index);
            	return res.endsWith(".") ? res.substring(0, res.length() - 1) : res; 
            }
            else
            	return "";
		}
		
		return pkg.getName();
	}
	
	/**
	 * Extracts the package name part of a qualified class name.
	 * @param className Qualified class name
	 * @return Package name (without the trailing dot)
	 */
	public static String extractPackageName(String className) {
		
		if (Util.isEmpty(className)) return "";
		
		int index = className.lastIndexOf(".");
		if (index > -1)
			return className.substring(0, index);
		
		return "";
	}

	/**
	 * Gets a qualified class name.  
	 * @param packageName Package name
	 * @param shortClassName Class name
	 * @return Qualified class name
	 */
	public static String qualifyName(String packageName, String shortClassName) {
		
		if (Util.isEmpty(packageName)) return "";
		if (Util.isEmpty(shortClassName)) return "";
		
		// Already qualified
		if (shortClassName.indexOf(".") != -1) return shortClassName;
			
		if (!packageName.endsWith("."))
			packageName += ".";
		
		return packageName + shortClassName;
	}

	/**
	 * Gets a qualified class name for the given class name. Package name is determined 
	 * from a reference to another class in the same package.
	 * @param siblingClassRef Reference to another class in the same package
	 * @param shortClassName Non-qualified name of the class to get a qualified name for
	 * @return Qualified class name
	 */
	public static String qualifyName(Class<?> siblingClassRef, String shortClassName) {
		
		return qualifyName(getPackageName(siblingClassRef), shortClassName);
	}
	
	/**
	 * Gets a qualified class name for the given class name. Package name is determined 
	 * from an instance of another class in the same package.
	 * @param sibling Existing object, an instance of another class in the same package
	 * @param shortClassName Non-qualified name of the class to get a qualified name for
	 * @return Qualified class name
	 */
	public static String qualifyName(Object sibling, String shortClassName) {
		
		if (sibling == null) return ""; 
		
		return qualifyName(sibling.getClass(), shortClassName);
	}
}
