/*===========================================================================*/
/* Copyright (C) 2008 Asgeir Frimannsson, Jim Hargrave, Yves Savourel        */
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

package net.sf.okapi.common.filters;

import java.io.InputStream;
import java.net.URL;

import net.sf.okapi.common.resource.Code;
import net.sf.okapi.common.resource.Group;
import net.sf.okapi.common.resource.IContainable;
import net.sf.okapi.common.resource.SkeletonUnit;
import net.sf.okapi.common.resource.TextUnit;

/**
 * @author HargraveJE
 * 
 */
public abstract class BaseParser implements IParser {
	protected int groupId = 0;
	protected int textUnitId = 0;
	protected int skeleltonUnitId = 0;

	public BaseParser() {
	}

	public SkeletonUnit appendToSkeletonUnit(SkeletonUnit skeletonUnit, int offset, int length) {
		if (skeletonUnit == null) {
			skeletonUnit = createSkeletonUnit(offset, length);
		} else {			
			skeletonUnit.addToLength(length);
		}
		return skeletonUnit;
	}

	public SkeletonUnit appendToSkeletonUnit(SkeletonUnit skeletonUnit, String skeleton) {
		if (skeletonUnit == null) {
			skeletonUnit = createSkeletonUnit(skeleton);
		} else {
			skeletonUnit.appendData(skeleton);
		}
		return skeletonUnit;
	}

	public SkeletonUnit appendToSkeletonUnit(SkeletonUnit skeletonUnit, String skeleton, int offset, int length) {
		if (skeletonUnit == null) {
			skeletonUnit = createSkeletonUnit(skeleton, offset, length);
		} else {
			skeletonUnit.appendData(skeleton);
			skeletonUnit.addToLength(length);
		}
		return skeletonUnit;
	}

	public TextUnit appendToTextUnit(TextUnit textUnit, Code code) {
		if (textUnit == null) {
			textUnit = createTextUnit(code);
		} else {
			textUnit.getSourceContent().append(code.getTagType(), code.getType(), code.getData());
		}
		return textUnit;
	}

	public TextUnit appendToTextUnit(TextUnit textUnit, String text) {
		if (textUnit == null) {
			textUnit = createTextUnit(text);
		} else {
			textUnit.getSourceContent().append(text);
		}
		return textUnit;
	}

	public TextUnit appendToTextUnit(TextUnit textUnit, TextUnit child) {
		if (textUnit == null) {
			textUnit = createTextUnit(child);
		} else {
			textUnit.addChild(child);
		}

		return textUnit;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.okapi.common.filters.IParser#close()
	 */
	abstract public void close();

	private SkeletonUnit createSkeletonUnit(int offset, int length) {
		SkeletonUnit skeletonUnit = new SkeletonUnit(String.format("s%d", ++skeleltonUnitId), offset, length);
		return skeletonUnit;
	}

	private SkeletonUnit createSkeletonUnit(String skeleton) {
		SkeletonUnit skeletonUnit = new SkeletonUnit(String.format("s%d", ++skeleltonUnitId), skeleton);
		return skeletonUnit;
	}

	private SkeletonUnit createSkeletonUnit(String skeleton, int offset, int length) {
		SkeletonUnit skeletonUnit = new SkeletonUnit(String.format("s%d", ++skeleltonUnitId), offset, length);
		skeletonUnit.setData(skeleton);
		return skeletonUnit;
	}

	private TextUnit createTextUnit(Code code) {
		TextUnit textUnit = new TextUnit();
		textUnit.setID(String.format("s%d", ++textUnitId));
		textUnit.getSource().getContent().append(code.getTagType(), code.getType(), code.getData());
		return textUnit;
	}

	private TextUnit createTextUnit(String text) {
		TextUnit textUnit = new TextUnit(String.format("s%d", ++textUnitId), text);
		return textUnit;
	}

	private TextUnit createTextUnit(TextUnit child) {
		TextUnit textUnit = new TextUnit();
		textUnit.setID(String.format("s%d", ++textUnitId));
		child.setParent(textUnit);
		textUnit.addChild(child);
		return textUnit;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.okapi.common.filters.IParser#getResource()
	 */
	abstract public IContainable getResource();

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.okapi.common.filters.IParser#open(java.lang.CharSequence)
	 */
	abstract public void open(CharSequence input);

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.okapi.common.filters.IParser#open(java.io.InputStream)
	 */
	abstract public void open(InputStream input);
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.okapi.common.filters.IParser#open(java.net.URL)
	 */
	abstract public void open(URL input);

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.okapi.common.filters.IParser#parseNext()
	 */
	abstract public ParserTokenType parseNext();
}
