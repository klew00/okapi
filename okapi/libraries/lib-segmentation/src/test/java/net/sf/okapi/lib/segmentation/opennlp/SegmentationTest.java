/*===========================================================================
  Copyright (C) 2009-2011 by the Okapi Framework contributors
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

package net.sf.okapi.lib.segmentation.opennlp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import net.sf.okapi.common.IResource;
import net.sf.okapi.common.ISegmenter;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.Range;
import net.sf.okapi.common.TestUtil;
import net.sf.okapi.common.Util;
import net.sf.okapi.common.filterwriter.GenericContent;
import net.sf.okapi.common.resource.ISegments;
import net.sf.okapi.common.resource.ITextUnit;
import net.sf.okapi.common.resource.Segment;
import net.sf.okapi.common.resource.TextContainer;
import net.sf.okapi.common.resource.TextFragment;
import net.sf.okapi.common.resource.TextFragment.TagType;
import net.sf.okapi.common.resource.TextPart;
import net.sf.okapi.common.resource.TextUnit;
import net.sf.okapi.lib.segmentation.SRXDocument;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class SegmentationTest {

	private ISegmenter segmenter;
	private GenericContent fmt = new GenericContent();
			
	@Before
	public void setUp() {
		segmenter = new OkapiMaxEntSegmenter(null, LocaleId.ENGLISH);
	}

	@Test
	public void testGetSegmentCount () {
		TextContainer tc = createSegmentedContainer();
		assertEquals(2, tc.getSegments().count());
	}
	
	@Test
	public void testGetSegments () {
		TextContainer tc = createSegmentedContainer();
		ISegments segments = tc.getSegments();
		assertEquals("Part 1.", segments.get(0).toString());
		assertEquals(" Part 2.", segments.get(1).toString());
		assertEquals("[Part 1.] Outside[ Part 2.]", fmt.printSegmentedContent(tc, true));
	}
	


	private TextContainer createSegmentedContainer () {
		TextFragment tf = new TextFragment();
		tf.append("Part 1.");
		tf.append(" Part 2.");
		TextContainer tc = new TextContainer(tf);
		segmenter.computeSegments(tc);
		tc.getSegments().create(segmenter.getRanges());
		// Insert in holder between the two segments
		tc.insert(1, new TextPart(new TextFragment(" Outside")));
		return tc;
	}
}
