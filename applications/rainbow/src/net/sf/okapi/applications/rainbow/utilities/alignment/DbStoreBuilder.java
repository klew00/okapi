/*===========================================================================
  Copyright (C) 2008 by the Okapi Framework contributors
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
============================================================================*/

package net.sf.okapi.applications.rainbow.utilities.alignment;

import java.io.File;
import java.io.OutputStream;
import java.util.Stack;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.Util;
import net.sf.okapi.common.filterwriter.IFilterWriter;
import net.sf.okapi.common.resource.TextUnit;
import net.sf.okapi.lib.segmentation.Segmenter;

class DbStoreBuilder implements IFilterWriter {
	
	private Stack<Integer> groupStack;
	private int lastGroupKey;
	private DbStore dbs;
	private Segmenter srcSeg;
	private Segmenter trgSeg;
	private String language;

	public DbStoreBuilder () {
		dbs = new DbStore();
	}
	
	public DbStore getDbStore () {
		return dbs;
	}
	
	public void setSegmenters (Segmenter srcSeg,
		Segmenter trgSeg)
	{
		this.srcSeg = srcSeg;
		this.trgSeg = trgSeg;
	}
	
	public void cancel () {
		//TODO: implement cancel()
	}
	
	public void close () {
		// Nothing to do
	}

	public String getName () {
		// No name
		return null;
	}

	public IParameters getParameters () {
		return null;
	}

	public Event handleEvent (Event event) {
		switch ( event.getEventType() ) {
		case START_DOCUMENT:
			processStartDocument();
			break;
		case START_GROUP:
			processStartGroup();
			break;
		case END_GROUP:
			processEndGroup();
			break;
		case TEXT_UNIT:
			processTextUnit((TextUnit)event.getResource());
			break;
		}
		return event;
	}

	public void setOptions (String language,
		String defaultEncoding)
	{
		this.language = language;
	}

	public void setOutput (String path) {
		// Not used
	}

	public void setOutput (OutputStream output) {
		// Not used
	}

	public void setParameters (IParameters params) {
		// Nothing to do
	}

	private void processEndGroup () {
		groupStack.pop();
	}

	private void processTextUnit (TextUnit tu) {
		// Segment if requested
		if ( srcSeg != null ) {
			srcSeg.computeSegments(tu.getSource());
			tu.getSource().createSegments(srcSeg.getSegmentRanges());
		}
		if ( trgSeg != null ) {
			if ( tu.hasTarget(language) ) {
				trgSeg.computeSegments(tu.getTarget(language));
				tu.getTarget(language).createSegments(trgSeg.getSegmentRanges());
			}
		}
		// Add the tu to the db store
		dbs.addSourceTextUnit(tu, groupStack.peek());
	}

	private void processStartGroup () {
		groupStack.push(++lastGroupKey);
	}

	private void processStartDocument () {
		groupStack = new Stack<Integer>();
		lastGroupKey = 0;
		groupStack.push(0);
		//TODO: Better temp filename (rely on createTmpFile() or something like this)
		String path = Util.getTempDirectory() + File.separatorChar + "tmpDB";
		dbs.create(path, "tmpDB", true);
	}

}
