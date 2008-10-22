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

package net.sf.okapi.common.pipeline2.tests;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import net.sf.okapi.common.threadedpipeline.ILinearPipeline;
import net.sf.okapi.common.threadedpipeline.LinearPipeline;
import net.sf.okapi.common.threadedpipeline.PipelineReturnValue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class LinearPipelineTest {

	@Before
	public void setUp() {

	}

	@Test
	public void runPipelineAsThread() {
		final ILinearPipeline pipeline = new LinearPipeline();

		Runnable runnable = new Runnable() {
			public void run() {
				pipeline.addPipleLineStep(new Producer());
				pipeline.addPipleLineStep(new ConsumerProducer());
				pipeline.addPipleLineStep(new Consumer());
				pipeline.execute();
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
		}
	}

	//@Test
	public void runPipeline() {
		ILinearPipeline pipeline = new LinearPipeline();
		pipeline.addPipleLineStep(new Producer());
		pipeline.addPipleLineStep(new ConsumerProducer());
		pipeline.addPipleLineStep(new Consumer());

		System.out.println("START PIPELINE");
		pipeline.execute();
		while (pipeline.getState() == PipelineReturnValue.RUNNING || pipeline.getState() == PipelineReturnValue.PAUSED) {
			pipeline.pause();
			if (pipeline.getState() == PipelineReturnValue.PAUSED) {
				System.out.println("Paused");
			} else {
				System.out.println("Running");
			}
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			pipeline.resume();
			if (pipeline.getState() == PipelineReturnValue.PAUSED) {
				System.out.println("Paused");
			} else {
				System.out.println("Running");
			}
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@After
	public void cleanUp() {
		System.out.println("CLEANUP PIPELINE");
	}

}
