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

package net.sf.okapi.common.skeleton;

import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Extension of the {@link GenericSkeleton} skeleton implementation that allow
 * ZipFile and ZipEntry objects to be passed along with skeleton parts. 
 */
public class ZipSkeleton extends GenericSkeleton {

	private ZipFile original;
	private ZipEntry entry;
	
	public ZipSkeleton (ZipFile original, ZipEntry entry) {
		this.original = original;
		this.entry = entry;
	}
	
	public ZipSkeleton (GenericSkeleton skel, ZipFile original, ZipEntry entry) {
		this(original, entry);
		add(skel);		
	}
	
	public ZipFile getOriginal () {
		return original;
	}
	
	public ZipEntry getEntry () {
		return entry;
	}

	@Override
	public ZipSkeleton clone() {
		ZipSkeleton newSkel = new ZipSkeleton(original, entry);
		super.copyFields(newSkel);
    	return newSkel;
	}
}
