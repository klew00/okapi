/*===========================================================================
  Copyright (C) 2008-2010 by the Okapi Framework contributors
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

package net.sf.okapi.lib.persistence;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import net.sf.okapi.common.ClassUtil;
import net.sf.okapi.common.Util;

public class VersionMapper {

	private static Map<String, IVersionDriver> versionMap = new ConcurrentHashMap<String, IVersionDriver>();
	private static Map<String, String> versionIdMap = new ConcurrentHashMap<String, String>(); 
	
	public static void registerVersion(Class<? extends IVersionDriver> versionDriverClass) {
		IVersionDriver versionDriver = null;
		try {
			versionDriver = ClassUtil.instantiateClass(versionDriverClass);
		} catch (InstantiationException e) {
			throw(new RuntimeException(String.format("VersionMapper: cannot instantiate version driver %s", 
					ClassUtil.getQualifiedClassName(versionDriverClass))));
		} catch (IllegalAccessException e) {
			throw(new RuntimeException(String.format("VersionMapper: cannot instantiate version driver %s", 
					ClassUtil.getQualifiedClassName(versionDriverClass))));
		}
		versionMap.put(versionDriver.getVersionId(), versionDriver);
	}
	
	public static IVersionDriver getDriver(String versionId) {
		return versionMap.get(getMapping(versionId));
	}
	
	/**
	 * Maps a VersionId to another VersionId. Used for backwards compatibility with older formats if the VersionId has changed.   
	 * @param previousVersionId old VersionId
	 * @param versionId new VersionId
	 */
	public static void mapVersionId(String previousVersionId, String versionId) {
		versionIdMap.put(previousVersionId, versionId);
	}
	
	public static String getMapping(String versionId) {
		String vid = versionIdMap.get(versionId); 
		return Util.isEmpty(vid) ? versionId : vid; // if no mapping is set, return the original version Id
	}
}
