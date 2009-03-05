/*===========================================================================*/
/* Copyright (C) 2008 Jim Hargrave                                           */
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
/* Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307 USA              */
/*                                                                           */
/* See also the full LGPL text here: http://www.gnu.org/copyleft/lesser.html */
/*===========================================================================*/

package net.sf.okapi.filters.html.tests;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class HtmlFilterThreadedRoundtripTest {

	@Before
	public void setUp() {

	}

	@Test
	public void runPipeline() {
/*		final IPipeline pipeline = new ThreadedPipeline();
		
		final HtmlFilter htmlFilter = new HtmlFilter();
		InputStream htmlStream = HtmlEventTest.class.getResourceAsStream("/simpleTest.html");
		htmlFilter.setOptions("en", "UTF-8", true);
		htmlFilter.open(htmlStream);
		
		GenericSkeletonWriter genericSkeletonWriter = new GenericSkeletonWriter();
		final GenericFilterWriter genericFilterWriter = new GenericFilterWriter(genericSkeletonWriter);
		genericFilterWriter.setOptions("es", "windows-1252");
		genericFilterWriter.setOutput("genericOutput.txt");				
		
		Runnable runnable = new Runnable() {
			public void run() {
				pipeline.addStep(new FilterPipelineStepAdaptor(htmlFilter));
				pipeline.addStep(new FilterWriterPipelineStepAdaptor(genericFilterWriter));
				pipeline.process("<p>Before <input type=\"radio\" name=\"FavouriteFare\" value=\"spam\" checked=\"checked\"/> after.</p>");				
				pipeline.close();
			}
		};

		ExecutorService e = Executors.newSingleThreadExecutor();
		e.execute(runnable);

		boolean stop = false;
		while (!stop) {
			switch (pipeline.getState()) {
			case CANCELLED:
			case SUCCEDED:
			case FAILED:
			case INTERRUPTED:			
				stop = true;
				e.shutdownNow();
				break;
			default:
				// still running
				break;
			}
		}*/
	}

	@After
	public void cleanUp() {
	}

}
