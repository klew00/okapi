/*===========================================================================
  Copyright (C) 2011 by the Okapi Framework contributors
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

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.okapi.common.annotation.IAnnotation;
import net.sf.okapi.tm.pensieve.common.TmHit;
import net.sf.okapi.tm.pensieve.common.TranslationUnit;

public class RepetitiveSegmentAnnotation implements IAnnotation {
	
	private SegmentInfo info;
	private Map<SegmentInfo, Float> map; // TranslationUnit Id + score

	public RepetitiveSegmentAnnotation(SegmentInfo info, Map<SegmentInfo, Float> map) {
		super();
		this.info = info;
		this.map = map;
	}
	
	public RepetitiveSegmentAnnotation(SegmentInfo info, List<TmHit> hits) {
		super();
		this.info = info;
		map = new HashMap<SegmentInfo, Float> ();
		if (hits == null) return;
		
		for (TmHit hit : hits) {
			TranslationUnit hitTu = hit.getTu();
			//map.put(hitTu.getMetadataValue(MetadataType.ID), hit.getScore());
			map.put(new SegmentInfo(hitTu.getMetadata()), hit.getScore());
		}
	}
	
	public Map<SegmentInfo, Float> getMap() {
		return Collections.unmodifiableMap(map);
	}

	public void setMap(Map<SegmentInfo, Float> map) {
		this.map = map;
	}

	public SegmentInfo getInfo() {
		return info;
	}

	public void setInfo(SegmentInfo info) {
		this.info = info;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (SegmentInfo refInfo : map.keySet()) {
			sb.append(String.format(", %s - %3.2f", refInfo.getTuid(), map.get(refInfo)));
		}
		return String.format("(tuid: %s groupId: %s segId: %s)%s", 
				info.getTuid(), info.getGroupId(), info.getSegId(), sb.toString());
	}

}
