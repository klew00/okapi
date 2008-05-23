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

/**
 * A convenient base-implementation of a {@link PipelineStep}.
 *
 * @version $Revision: 106 $
 */
public abstract class BasePipelineStep implements PipelineStep {
   private String name;

   /**
    * Creates a step with the given name.
    *
    * @param name the name of step.
    *
    * @see PipelineStep#getName()
    * @see PipelineStep#setName(String)
    */
   public BasePipelineStep(String name) {
      this.name = name;
   }

   /**
    * Calls {@link BeanValidator#validate(Object) BeanValidator.validate(this)}.
    *
    * @throws PipelineException see {@link BeanValidator#validate(Object)}.
    */
   public void prepare() throws PipelineException {

   }

   /**
    * Does nothing. Override to implement.
    */
   public void finish(boolean success) throws PipelineException {
   }

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }
}
