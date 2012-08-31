/*===========================================================================
  Copyright (C) 2012 by the Okapi Framework contributors
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

package org.w3c.its;

import static org.junit.Assert.*;

import java.io.File;

import net.sf.okapi.common.FileCompare;
import net.sf.okapi.common.TestUtil;
import net.sf.okapi.common.Util;

import org.junit.Test;

public class ITSTest {

	private String root = TestUtil.getParentDir(this.getClass(), "/input.xml") + "/ITS2/input";
	private FileCompare fc = new FileCompare();
	
	@Test
	public void testTranslate () {
		String base = root+"/translate/xml";
		removeOutput(base);
		process(base+"/Translate1.xml", Main.DC_TRANSLATE);
		process(base+"/Translate3.xml", Main.DC_TRANSLATE);
		process(base+"/Translate4.xml", Main.DC_TRANSLATE);
		process(base+"/Translate5.xml", Main.DC_TRANSLATE);
		process(base+"/Translate6.xml", Main.DC_TRANSLATE);
		process(base+"/Translate7.xml", Main.DC_TRANSLATE);
		process(base+"/TranslateGlobal.xml", Main.DC_TRANSLATE);
	}

	@Test
	public void testIdValue () {
		String base = root+"/idvalue/xml";
		removeOutput(base);
		process(base+"/idvalue1xml.xml", Main.DC_IDVALUE);
		process(base+"/idvalue2xml.xml", Main.DC_IDVALUE);
		process(base+"/idvalue3xml.xml", Main.DC_IDVALUE);
	}

	private void removeOutput (String baseDir) {
		String outDir = baseDir.replace("/input/", "/output/");
		Util.deleteDirectory(outDir, true);
	}
	
	private void process (String baseName,
		String dataCategory)
	{
		String input = baseName;
		String output = input.replace("/input/", "/output/");
		int n = output.lastIndexOf('.');
		if ( n > -1 ) output = output.substring(0, n);
		output += "output";
		output += ".txt"; //Util.getExtension(input);
		
		Main.main(new String[]{input, output, "-dc", dataCategory});
		assertTrue(new File(output).exists());
		
		String gold = output.replace("/output/", "/expected/");
		assertTrue(fc.compareFilesPerLines(output, gold, "UTF-8"));
	}
	
}
