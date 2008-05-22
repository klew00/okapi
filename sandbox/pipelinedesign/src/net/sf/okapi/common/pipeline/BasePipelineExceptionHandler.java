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

import java.util.Set;

import net.sf.okapi.common.resource.IResource;
import net.sf.okapi.utilities.IdentityHashSet;

/**
 * A base implementation for the PipelineExceptionHandler to simplify handling of
 * ExceptionListeners.
 *
 * @version $Revision: 154 $
 */
public abstract class BasePipelineExceptionHandler implements PipelineExceptionHandler {
   protected IdentityHashSet<PipelineExceptionListener> exceptionListeners = new IdentityHashSet<PipelineExceptionListener>();

   public void addExceptionListener(PipelineExceptionListener exceptionListener) {
      exceptionListeners.add(exceptionListener);
   }

   public void removeExceptionListener(PipelineExceptionListener exceptionListener) {
      exceptionListeners.remove(exceptionListener);
   }

   public Set<PipelineExceptionListener> getExceptionListeners() {
      return exceptionListeners;
   }

   public void setExceptionListeners(Set<PipelineExceptionListener> exceptionListeners) {
      this.exceptionListeners = new IdentityHashSet<PipelineExceptionListener>(exceptionListeners);
   }

   /**
    * This method should be called on all exceptions.
    *
    * @param ex the exception that was handled
    */
   protected void notifyExceptionListeners(PipelineException ex) {
      notifyExceptionListeners(ex, null);
   }

   /**
    * This method should be called on all exceptions.
    *
    * @param ex the exception that was handled
    * @param document the document(if any) this exception was thrown on
    */
   protected void notifyExceptionListeners(PipelineException ex, IResource resource) {
      for (PipelineExceptionListener exceptionListener : exceptionListeners) {
         exceptionListener.onException(ex, resource);
      }
   }

}
