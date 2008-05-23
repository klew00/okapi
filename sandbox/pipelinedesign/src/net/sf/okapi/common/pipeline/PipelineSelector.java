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

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.okapi.common.pipeline.BasePipelineStep;
import net.sf.okapi.common.pipeline.BaseSubPipeline;
import net.sf.okapi.common.pipeline.MultiPipelineException;
import net.sf.okapi.common.pipeline.PipelineException;
import net.sf.okapi.common.pipeline.PipelineStep;
import net.sf.okapi.common.pipeline.PipelineStepStatus;
import net.sf.okapi.common.pipeline.PipelineStepStatusCode;
import static net.sf.okapi.common.pipeline.PipelineStepStatusCode.CONTINUE;
import static net.sf.okapi.common.pipeline.PipelineStepStatusCode.DIVERT_PIPELINE;
import net.sf.okapi.common.pipeline.SubPipeline;

import net.sf.okapi.common.resource.IResourceBuilder;

/**
 * An abstract {@link PipelineStep} that selects a sub-pipeline based on the
 * switch value implemented by a subclass
 *
 * @version $Revision: 128 $
 */
public abstract class PipelineSelector extends BasePipelineStep {
	private Map<String, List<PipelineStep>> switchMap = Collections.emptyMap();
	private Map<String, PipelineStepStatusCode> statusCodeMap = Collections
			.emptyMap();
	private Map<String, SubPipeline> swMap = new HashMap<String, SubPipeline>();

	public PipelineSelector(String name) {
		super(name);
	}

	public PipelineStepStatus execute(IResourceBuilder resourceBuilder) throws PipelineException {
		final SubPipeline pipeline = swMap.get(getSwitchValue(resourceBuilder));
		final PipelineStepStatus status;
		if (pipeline != null) {
			status = handleSubPipeline(resourceBuilder, pipeline);
		} else {
			final PipelineStepStatusCode statusCode = getStatusCode(
					getSwitchValue(resourceBuilder), CONTINUE);
			if (statusCode.hasSubPipeline()) {
				throw new PipelineException(
						"No sub-pipeline configured for operation '"
								+ getSwitchValue(resourceBuilder) + "' but code "
								+ statusCode + " found");
			}
			status = new PipelineStepStatus(statusCode);
		}
		return status;
	}

	protected abstract String getSwitchValue(IResourceBuilder resourceBuilder);

	@Override
	public void prepare() throws PipelineException {
		super.prepare();

		swMap.clear();
		for (Map.Entry<String, List<PipelineStep>> entry : switchMap.entrySet()) {
			final BaseSubPipeline pipeline = new BaseSubPipeline(entry
					.getValue());
			pipeline.prepare();
			swMap.put(entry.getKey(), pipeline);
		}
	}

	@Override
	public void finish(boolean success) throws PipelineException {
		// Rethrow all exceptions
		MultiPipelineException pipelineException = null;
		for (SubPipeline pipeline : swMap.values()) {
			try {
				pipeline.finish(success);
			} catch (PipelineException e) {
				if (pipelineException == null) {
					pipelineException = new MultiPipelineException(getName());
				}
				pipelineException.add(e);
			} catch (RuntimeException e) {
				if (pipelineException == null) {
					pipelineException = new MultiPipelineException(getName());
				}
				pipelineException.add(new PipelineException(e));
			}
		}
		swMap.clear();
		if (pipelineException != null) {
			throw pipelineException;
		}
	}

	private PipelineStepStatus handleSubPipeline(IResourceBuilder resourceBuilder,
			SubPipeline pipeline) {
		final PipelineStepStatusCode statusCode = getStatusCode(
				getSwitchValue(resourceBuilder), DIVERT_PIPELINE);
		final PipelineStepStatus status = new PipelineStepStatus(statusCode,
				pipeline);
		if (!statusCode.hasSubPipeline()) {
			// TODO: Log warning or throw exception
		}
		return status;
	}

	private PipelineStepStatusCode getStatusCode(String operation,
			PipelineStepStatusCode defaultCode) {
		final PipelineStepStatusCode code = statusCodeMap.get(operation);
		return code != null ? code : defaultCode;
	}

	public Map<String, List<PipelineStep>> getSwitchMap() {
		return switchMap;
	}

	public void setSwitchMap(Map<String, List<PipelineStep>> switchMap) {
		this.switchMap = switchMap;
	}

	public Map<String, PipelineStepStatusCode> getStatusCodeMap() {
		return statusCodeMap;
	}

	public void setStatusCodeMap(
			Map<String, PipelineStepStatusCode> statusCodeMap) {
		this.statusCodeMap = statusCodeMap;
	}
}
