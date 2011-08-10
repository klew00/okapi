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

import org.oasisopen.xliff.v2.ICandidate;
import org.oasisopen.xliff.v2.INote;
import org.oasisopen.xliff.v2.IWithCandidates;
import org.oasisopen.xliff.v2.IWithNotes;

public class Segment extends Part implements IWithCandidates, IWithNotes {

	private static final long serialVersionUID = 0100L;

	private String id;
	private boolean translatable = true;
	private ArrayList<ICandidate> candidates;
	private ArrayList<INote> notes;
	
	public Segment (DataStore store) {
		super(store);
	}

	public String getId () {
		return id;
	}
	
	public void setId (String id) {
		this.id = id;
	}
	
	public boolean isTranslatable () {
		return translatable;
	}
	
	public void setTranslatable (boolean translatable) {
		this.translatable = translatable;
	}
	
	@Override
	public void addCandidate (ICandidate candidate) {
		if ( candidates == null ) candidates = new ArrayList<ICandidate>();
		candidates.add(candidate);
	}
	
	@Override
	public List<ICandidate> getCandidates () {
		if ( candidates == null ) return Collections.emptyList();
		else return candidates;
	}
	
	@Override
	public int getCandidateCount () {
		if ( candidates == null ) return 0;
		return candidates.size();
	}
	
	@Override
	public void addNote (INote note) {
		if ( notes == null ) notes = new ArrayList<INote>();
		notes.add(note);
	}

	@Override
	public List<INote> getNotes () {
		if ( notes == null ) return Collections.emptyList();
		else return notes;
	}
	
	@Override
	public int getNoteCount () {
		if ( notes == null ) return 0;
		return notes.size();
	}

}
