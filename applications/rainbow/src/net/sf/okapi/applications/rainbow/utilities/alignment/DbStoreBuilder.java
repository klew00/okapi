/*===========================================================================*/
/* Copyright (C) 2008 by the Okapi Framework contributors                    */
/*---------------------------------------------------------------------------*/
/* This library is free software; you can redistribute it and/or modify it   */
/* under the terms of the GNU Lesser General Public License as published by  */
/* the Free Software Foundation; either version 2.1 of the License, or (at   */
/* your option) any later version.                                           */
/*                                                                           */
/* This library is distributed in the hope that it will be useful, but       */
/* WITHOUT ANY WARRANTY; without even the implied warranty of                */
/* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser   */
/* General Public License for more details.                                  */
/*                                                                           */
/* You should have received a copy of the GNU Lesser General Public License  */
/* along with this library; if not, write to the Free Software Foundation,   */
/* Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA               */
/*                                                                           */
/* See also the full LGPL text here: http://www.gnu.org/copyleft/lesser.html */
/*===========================================================================*/

package net.sf.okapi.applications.rainbow.utilities.alignment;

import java.io.File;
import java.util.Stack;

import net.sf.okapi.common.Util;
import net.sf.okapi.common.resource.TextUnit;
import net.sf.okapi.lib.segmentation.Segmenter;

public class DbStoreBuilder implements IResourceBuilder {
	
	private Stack<Integer>   groupStack;
	private int              lastGroupKey;
	private DbStore          dbs;
	private Segmenter        srcSeg;
	private Segmenter        trgSeg;


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
	
	public void endContainer (Group resource) {
		groupStack.pop();
	}

	public void endExtractionItem (TextUnit tu) {
		// Segment if requested
		if ( srcSeg != null ) {
			srcSeg.computeSegments(tu.getSourceContent());
			tu.getSourceContent().createSegments(srcSeg.getSegmentRanges());
		}
		if ( trgSeg != null ) {
			if ( tu.hasTarget() ) {
				trgSeg.computeSegments(tu.getTargetContent());
				tu.getTargetContent().createSegments(trgSeg.getSegmentRanges());
			}
		}
		// Add the tu to the db store
		dbs.addSourceTextUnit(tu, groupStack.peek());
	}

	public void endResource (Document resource) {
	}

	public void skeletonContainer (SkeletonUnit resource) {
		// Store nothing of the skeleton
	}

	public void startContainer (Group resource) {
		groupStack.push(++lastGroupKey);
	}

	public void startExtractionItem (TextUnit item) {
		// Nothing to do, wait for end
	}

	public void startResource (Document resource) {
		groupStack = new Stack<Integer>();
		lastGroupKey = 0;
		groupStack.push(0);
		//TODO: Better temp filename (rely on createTmpFile() or something like this)
		String path = Util.getTempDirectory() + File.separatorChar + "tmpDB";
		dbs.create(path, "tmpDB", true);
	}
}
