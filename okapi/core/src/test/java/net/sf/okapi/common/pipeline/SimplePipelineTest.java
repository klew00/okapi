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

package net.sf.okapi.common.pipeline;

import static org.junit.Assert.assertEquals;

import java.net.URISyntaxException;

import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.pipeline.Pipeline;
import net.sf.okapi.common.pipeline.IPipeline;
import net.sf.okapi.common.pipeline.PipelineReturnValue;
import net.sf.okapi.common.resource.RawDocument;

import org.junit.Test;

public class SimplePipelineTest {
	
	@Test
	public void runPipeline() throws URISyntaxException {
		IPipeline pipeline = new Pipeline();
		pipeline.addStep(new Producer());
		pipeline.addStep(new ConsumerProducer());
		pipeline.addStep(new Consumer());

		pipeline.startBatch();
		pipeline.process(new RawDocument("DUMMY", LocaleId.fromString("en")));
		pipeline.endBatch();
		
		assertEquals(PipelineReturnValue.SUCCEDED, pipeline.getState());
		pipeline.destroy();
		assertEquals(PipelineReturnValue.DESTROYED, pipeline.getState());
	}
}
