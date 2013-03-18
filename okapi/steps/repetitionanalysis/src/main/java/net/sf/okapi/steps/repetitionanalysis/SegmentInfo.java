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

package net.sf.okapi.steps.repetitionanalysis;

import net.sf.okapi.tm.pensieve.common.Metadata;
import net.sf.okapi.tm.pensieve.common.MetadataType;

public class SegmentInfo {

	private String tuid;
	private String groupId;
	private String segId;
	
	public SegmentInfo() {
		super();
	}
			
	public SegmentInfo(String tuid, String groupId, String segId) {
		super();
		this.tuid = tuid;
		this.groupId = groupId;
		this.segId = segId;
	}

	public SegmentInfo(Metadata metadata) {
		this(metadata.get(MetadataType.ID), 
			 metadata.get(MetadataType.GROUP_NAME),				
			 metadata.get(MetadataType.FILE_NAME));
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof SegmentInfo) {
			SegmentInfo si = (SegmentInfo) obj;
			if (!si.tuid.equals(this.tuid)) return false;
			if (!si.groupId.equals(this.groupId)) return false;
			if (!si.segId.equals(this.segId)) return false;
			return true;
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return (String.format("%s %s %s", tuid, groupId, segId)).hashCode();
	}
	
	public String getTuid() {
		return tuid;
	}
	
	public void setTuid(String tuid) {
		this.tuid = tuid;
	}
	
	public String getGroupId() {
		return groupId;
	}
	
	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}
	
	public String getSegId() {
		return segId;
	}
	
	public void setSegId(String segId) {
		this.segId = segId;
	}
}
