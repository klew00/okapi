/*
 * Copyright 2007  T-Rank AS
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.sf.okapi.common.pipeline;

import java.util.ArrayList;
import java.util.List;

import net.sf.okapi.common.resource.IResourceBuilder;

/**
 * @version $Revision: 148 $
 */
public class BaseSubPipeline implements SubPipeline {
   private final List<PipelineStep> preparedSteps = new ArrayList<PipelineStep>();
   private List<? extends PipelineStep> pipelineSteps;

   public BaseSubPipeline() {
   }

   public BaseSubPipeline(List<? extends PipelineStep> pipelineSteps) {
      setPipelineSteps(pipelineSteps);
   }

   public List<? extends PipelineStep> getPipelineSteps() {
      return pipelineSteps;
   }

   public void setPipelineSteps(List<? extends PipelineStep> pipelineSteps) {
      this.pipelineSteps = pipelineSteps;
   }

   public boolean prepare() throws PipelineException {
      preparedSteps.clear();
      for (PipelineStep step : getPipelineSteps()) {
         try {
            step.prepare();
            preparedSteps.add(step);
         } catch (PipelineException e) {
            e.setPipelineStepNameIfNull(step.getName());
            throw e;
         } catch (RuntimeException e) {
            throw new PipelineException(e, step.getName());
         }
      }
      return true;
   }

   public void finish(boolean success) throws PipelineException {
      MultiPipelineException pipelineException = null;
      for (PipelineStep step : preparedSteps) {
         try {
            step.finish(success);
         } catch (PipelineException e) {
            e.setPipelineStepNameIfNull(step.getName());
            if (pipelineException == null) {
               pipelineException = new MultiPipelineException();
            }
            pipelineException.add(e);
         } catch (RuntimeException e) {
            if (pipelineException == null) {
               pipelineException = new MultiPipelineException();
            }
            pipelineException.add(new PipelineException(e, step.getName()));
         }
      }
      preparedSteps.clear();
      if (pipelineException != null) {
         throw pipelineException;
      }
   }


   public PipelineStatusCode executeSteps(IResourceBuilder resourceBuilder) throws PipelineException {
      PipelineStatusCode pipelineStatusCode = PipelineStatusCode.CONTINUE;
      for (PipelineStep pipelineStep : preparedSteps) {
         final PipelineStepStatusCode stepStatusCode;
         try {
            PipelineStepStatus status = executeStep(resourceBuilder, pipelineStep);
            if (status == null) {
               throw new PipelineException("null status received", pipelineStep.getName());
            }
            stepStatusCode = status.getStatusCode();
            if (stepStatusCode.hasSubPipeline()) {
               pipelineStatusCode = status.getSubPipeline().executeSteps(resourceBuilder);
            } else {
               pipelineStatusCode = stepStatusCode.toPipelineStatusCode();
            }
            if (stepStatusCode.isDone()) {
               return stepStatusCode.toPipelineStatusCode();
            }
         } catch (PipelineException e) {
            e.setPipelineStepNameIfNull(pipelineStep.getName());
            throw e;
         } catch (RuntimeException e) {
            throw new PipelineException(e, pipelineStep.getName());
         }
      }
      return pipelineStatusCode;
   }

   protected PipelineStepStatus executeStep(IResourceBuilder resourceBuilder, PipelineStep pipelineStep) throws PipelineException {
      final PipelineStepStatus status = pipelineStep.execute(resourceBuilder);
      return status;
   }
}
