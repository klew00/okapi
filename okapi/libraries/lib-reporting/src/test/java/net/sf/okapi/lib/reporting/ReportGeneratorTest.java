/*===========================================================================
  Copyright (C) 2008-2010 by the Okapi Framework contributors
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

package net.sf.okapi.lib.reporting;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import net.sf.okapi.lib.reporting.ReportGenerator;

import org.junit.Test;
public class ReportGeneratorTest {

	@Test
	public void reportTest() {
		ReportGenerator gen = new ReportGenerator(this.getClass().getResourceAsStream("test_report.txt"));
		
		gen.setField("A1", "<a1>");
		gen.setField("A2", "<a2>");
		gen.setField("A3", "<a3>");
		gen.setField("A4", "<a4>");
		gen.setField("A5", "<a5>");
		gen.setField("A6", "<a6>");
		
		String report = gen.generate();
		assertEquals("Test report\n\n#Creation Date: <a1>\n#Project Name: <a2>\n#Target Locale: <a3>\n\n\n" +
				"Total,<a4>,<a5>,<a6>\n\nThe report was created on <a1>\n", report);
	}
	
	@Test
	public void tableReportTest() {
		ReportGenerator gen = new ReportGenerator(this.getClass().getResourceAsStream("test_table_report.txt"));
		
		gen.setField("A1", "<a1>");
		gen.setField("A2", "<a2>");
		gen.setField("A3", "<a3>");
		gen.setField("A4", "<a4>");
		gen.setField("A5", "<a5>");
		gen.setField("A6", "<a6>");
		
		gen.setField("A7", "<a7.1>");
		gen.setField("A8", "<a8.1>");
		gen.setField("A9", "<a9.1>");
		gen.setField("A10", "<a10.1>");
		
		gen.setField("A7", "<a7.2>");
		gen.setField("A8", "<a8.2>");
		gen.setField("A9", "<a9.2>");
		gen.setField("A10", "<a10.2>");
		
		gen.setField("A7", "<a7.3>");
		gen.setField("A8", "<a8.3>");
		gen.setField("A9", "<a9.3>");
		gen.setField("A10", "<a10.3>");
		
		gen.setField("A7", "<a7.4>");
		gen.setField("A8", "<a8.4>");
		gen.setField("A9", "<a9.4>");
		gen.setField("A10", "<a10.4>");
		
		gen.setField("A7", "<a7.5>");
		gen.setField("A8", "<a8.5>");
		gen.setField("A9", "<a9.5>");
		gen.setField("A10", "<a10.5>");
		
		String report = gen.generate();
		assertEquals("Test report\n\n#Creation Date: <a1>\n#Project Name: <a2>\n#Target Locale: <a3>\n\n" +
				"<a7.1>,<a8.1>,<a9.1>,<a10.1>,\n<a7.2>,<a8.2>,<a9.2>,<a10.2>,\n<a7.3>,<a8.3>,<a9.3>,<a10.3>,\n" +
				"<a7.4>,<a8.4>,<a9.4>,<a10.4>,\n<a7.5>,<a8.5>,<a9.5>,<a10.5>,\n\n" +
				"Total,<a4>,<a5>,<a6>\n\nThe report was created on <a1>\n", report);
	}
	
	@Test
	public void tableReportTest2() {
		ReportGenerator gen = new ReportGenerator(this.getClass().getResourceAsStream("test_table_report2.txt"));
		
		gen.setField("A1", "<a1>");
		gen.setField("A2", "<a2>");
		gen.setField("A3", "<a3>");
		gen.setField("A4", "<a4>");
		gen.setField("A5", "<a5>");
		gen.setField("A6", "<a6>");
		
		gen.setField("A7", "<a7.1>");
		gen.setField("A8", "<a8.1>");
		gen.setField("A9", "<a9.1>");
		gen.setField("A10", "<a10.1>");
		
		gen.setField("A7", "<a7.2>");
		gen.setField("A8", "<a8.2>");
		gen.setField("A9", "<a9.2>");
		gen.setField("A10", "<a10.2>");
		
		gen.setField("A7", "<a7.3>");
		gen.setField("A8", "<a8.3>");
		gen.setField("A9", "<a9.3>");
		gen.setField("A10", "<a10.3>");
		
		gen.setField("A7", "<a7.4>");
		gen.setField("A8", "<a8.4>");
		gen.setField("A9", "<a9.4>");
		gen.setField("A10", "<a10.4>");
		
		gen.setField("A7", "<a7.5>");
		gen.setField("A8", "<a8.5>");
		gen.setField("A9", "<a9.5>");
		gen.setField("A10", "<a10.5>");
		
		String report = gen.generate();
		assertEquals("Test report\n\n#Creation Date: <a1>\n#Project Name: <a2>\n#Target Locale: <a3>\n\n" +
				"<a7.1>\n<a7.2>\n<a7.3>\n" +
				"<a7.4>\n<a7.5>\n\n" +
				"Total,<a4>,<a5>,<a6>\n\nThe report was created on <a1>\n", report);
	}
	
	@Test
	public void tableReportTest3() {
		ReportGenerator gen = new ReportGenerator(this.getClass().getResourceAsStream("test_table_report2.txt"));
		
		gen.setField("A1", "<a1>");
		gen.setField("A2", "<a2>");
		gen.setField("A3", "<a3>");
		gen.setField("A4", "<a4>");
		gen.setField("A5", "<a5>");
		//gen.setField("A6", "<a6>");
		
		gen.setField("A7", "<a7.1>");
		gen.setField("A8", "<a8.1>");
		gen.setField("A9", "<a9.1>");
		gen.setField("A10", "<a10.1>");
		
		gen.setField("A7", "<a7.2>");
		gen.setField("A8", "<a8.2>");
		gen.setField("A9", "<a9.2>");
		gen.setField("A10", "<a10.2>");
		
		gen.setField("A7", "<a7.3>");
		gen.setField("A8", "<a8.3>");
		gen.setField("A9", "<a9.3>");
		gen.setField("A10", "<a10.3>");
		
		gen.setField("A7", "<a7.4>");
		gen.setField("A8", "<a8.4>");
		gen.setField("A9", "<a9.4>");
		gen.setField("A10", "<a10.4>");
		
		gen.setField("A7", "<a7.5>");
		gen.setField("A8", "<a8.5>");
		gen.setField("A9", "<a9.5>");
		gen.setField("A10", "<a10.5>");
		
		String report = gen.generate();
		assertEquals("Test report\n\n#Creation Date: <a1>\n#Project Name: <a2>\n#Target Locale: <a3>\n\n" +
				"<a7.1>\n<a7.2>\n<a7.3>\n" +
				"<a7.4>\n<a7.5>\n\n" +
				"Total,<a4>,<a5>,[?A6]\n\nThe report was created on <a1>\n", report);
	}
	
	@Test
	public void tableReportTest4() {
		ReportGenerator gen = new ReportGenerator(this.getClass().getResourceAsStream("test_table_report2.txt"));
		
		gen.setField("A1", "<a1>");
		gen.setField("A2", "<a2>");
		gen.setField("A3", "<a3>");
		gen.setField("A4", "<a4>");
		gen.setField("A5", "<a5>");
		gen.setField("A6", "<a6>");
		
//		gen.setField("A7", "<a7.1>");
//		gen.setField("A8", "<a8.1>");
//		gen.setField("A9", "<a9.1>");
//		gen.setField("A10", "<a10.1>");
//		
//		gen.setField("A7", "<a7.2>");
//		gen.setField("A8", "<a8.2>");
//		gen.setField("A9", "<a9.2>");
//		gen.setField("A10", "<a10.2>");
//		
//		gen.setField("A7", "<a7.3>");
//		gen.setField("A8", "<a8.3>");
//		gen.setField("A9", "<a9.3>");
//		gen.setField("A10", "<a10.3>");
//		
//		gen.setField("A7", "<a7.4>");
//		gen.setField("A8", "<a8.4>");
//		gen.setField("A9", "<a9.4>");
//		gen.setField("A10", "<a10.4>");
//		
//		gen.setField("A7", "<a7.5>");
//		gen.setField("A8", "<a8.5>");
//		gen.setField("A9", "<a9.5>");
//		gen.setField("A10", "<a10.5>");
		
		String report = gen.generate();
		assertEquals("Test report\n\n#Creation Date: <a1>\n#Project Name: <a2>\n#Target Locale: <a3>\n\n" +
				"[?[?A7]]\n\n" +
				"Total,<a4>,<a5>,<a6>\n\nThe report was created on <a1>\n", report);
	}
	
	@Test
	public void tableReportTest5() {
		ReportGenerator gen = new ReportGenerator(this.getClass().getResourceAsStream("scoping_report.html"));
		assertTrue(gen.isHtmlReport());
		assertTrue(gen.isMultiItemReport());
	}
	
	@Test
	public void tableReportTest6() {
		ReportGenerator gen = new ReportGenerator(this.getClass().getResourceAsStream("scoping_report2.html"));
		assertTrue(gen.isHtmlReport());
		assertFalse(gen.isMultiItemReport());
	}
	
	@Test
	public void tableReportTest7() {
		ReportGenerator gen = new ReportGenerator(this.getClass().getResourceAsStream("test_table_report3.txt"));
		assertFalse(gen.isHtmlReport());
		assertTrue(gen.isMultiItemReport());
	}
	
	@Test
	public void tableReportTest8() {
		ReportGenerator gen = new ReportGenerator(this.getClass().getResourceAsStream("test_table_report4.txt"));
		assertFalse(gen.isHtmlReport());
		assertFalse(gen.isMultiItemReport());
	}
}
