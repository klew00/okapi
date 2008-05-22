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

import net.sf.okapi.common.resource.IResourceBuilder;

/**
 * A simple error-handler that logs all exceptions.
 *
 * @version $Revision: 83 $
 */
public class LoggingPipelineExceptionListener implements PipelineExceptionListener {
   public void onException(PipelineException ex, IResourceBuilder resourceBuilder) {
	   // TODO: Implement logging
   }
}
