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

package net.sf.okapi.lib.xliff;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Segment {

	private String id;
	private ArrayList<Fragment> before;
	private Fragment source;
	private Fragment target;
	private ArrayList<Fragment> after;
	private ArrayList<Alternate> candidates;
	
	public Segment () {
		source = new Fragment();
	}
	
	public Segment (Fragment source) {
		this.source = source;
	}
	
	public Segment (String sourceContent) {
		source = new Fragment(sourceContent);
	}
	
	public String getId () {
		return id;
	}
	
	public void setId (String id) {
		this.id = id;
	}
	
	public Fragment getSource () {
		return source;
	}
	
	public Fragment setSource (Fragment fragment) {
		source = fragment;
		return source;
	}
	
	public boolean hasTarget () {
		return (target != null);
	}

	public Fragment getTarget () {
		return target;
	}
	
	public Fragment setTarget (Fragment fragment) {
		target = fragment;
		return target;
	}
	
	public Fragment addBefore (Fragment fragment) {
		if ( before == null ) {
			before = new ArrayList<Fragment>();
		}
		before.add(fragment);
		return fragment;
	}
	
	public Fragment addAfter (Fragment fragment) {
		if ( after == null ) {
			after = new ArrayList<Fragment>();
		}
		after.add(fragment);
		return fragment;
	}
	
	List<Fragment> getLeadingParts () {
		if ( before == null ) return Collections.emptyList();
		else return before;
	}

	List<Fragment> getTrailingParts () {
		if ( after == null ) return Collections.emptyList();
		else return after;
	}

	public void addCandidate (Alternate candidate) {
		if ( candidates == null ) candidates = new ArrayList<Alternate>();
		candidates.add(candidate);
	}
	
	public List<Alternate> getCandidates () {
		if ( candidates == null ) return Collections.emptyList();
		else return candidates;
	}
	
}
