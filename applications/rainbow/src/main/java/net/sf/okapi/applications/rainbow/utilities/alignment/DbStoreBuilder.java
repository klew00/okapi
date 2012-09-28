/*===========================================================================
  Copyright (C) 2008-2011 by the Okapi Framework contributors
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

package net.sf.okapi.applications.rainbow.utilities.alignment;

import java.io.File;
import java.io.OutputStream;
import java.util.Stack;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.ISegmenter;
import net.sf.okapi.common.Util;
import net.sf.okapi.common.encoder.EncoderManager;
import net.sf.okapi.common.filterwriter.IFilterWriter;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.resource.ITextUnit;
import net.sf.okapi.common.resource.StartDocument;
import net.sf.okapi.common.resource.TextContainer;
import net.sf.okapi.common.skeleton.ISkeletonWriter;

class DbStoreBuilder implements IFilterWriter {
	
	private Stack<Integer> groupStack;
	private int lastGroupKey;
	private DbStore dbs;
	private ISegmenter segmenter;
	private LocaleId language;
	private boolean useSource;

	public DbStoreBuilder () {
		dbs = new DbStore();
	}
	
	public DbStore getDbStore () {
		return dbs;
	}
	
	public void setSegmenter (ISegmenter segmenter) {
		this.segmenter = segmenter;
	}
	
	public void cancel () {
		//TODO: implement cancel()
	}
	
	public void close () {
		// Nothing to do
	}

	public String getName () {
		return getClass().getName();
	}

	public EncoderManager getEncoderManager () {
		return null;
	}
	
	public IParameters getParameters () {
		return null;
	}

	public Event handleEvent (Event event) {
		switch ( event.getEventType() ) {
		case START_DOCUMENT:
			processStartDocument((StartDocument)event.getResource());
			break;
		case START_GROUP:
		case START_SUBFILTER:
			processStartGroup();
			break;
		case END_GROUP:
		case END_SUBFILTER:
			processEndGroup();
			break;
		case TEXT_UNIT:
			processTextUnit(event.getTextUnit());
			break;
		}
		return event;
	}

	public void setOptions (LocaleId language,
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

	private void processTextUnit (ITextUnit tu) {
		TextContainer tc;
		if ( useSource ) tc = tu.getSource();
		else { // Use the target
			tc = tu.getTarget(language);
			if ( tc == null ) return; // No target to set
		}
		// Segment if requested
		if ( segmenter != null ) {
			segmenter.computeSegments(tc);
			tc.getSegments().create(segmenter.getRanges());
		}
		// Add the tu to the db store
		dbs.addSourceEntry(tc, groupStack.peek(), tu.getId(), tu.getName(), tu.getType());
	}

	private void processStartGroup () {
		groupStack.push(++lastGroupKey);
	}

	private void processStartDocument (StartDocument resource) {
		groupStack = new Stack<Integer>();
		lastGroupKey = 0;
		groupStack.push(0);

		// Fill with the source or the target of this document
		// If monolingual, use the source
		// If multilingual we assume to use the target
		useSource = !resource.isMultilingual();
		
		//TODO: Better temp filename (rely on createTmpFile() or something like this)
		String path = Util.getTempDirectory() + File.separatorChar + "tmpDB";
		dbs.create(path, "tmpDB", true);
	}

	@Override
	public ISkeletonWriter getSkeletonWriter () {
		return null;
	}

}
