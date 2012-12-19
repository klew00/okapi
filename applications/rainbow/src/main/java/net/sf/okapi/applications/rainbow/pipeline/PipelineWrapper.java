/*===========================================================================
  Copyright (C) 2009-2012 by the Okapi Framework contributors
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

package net.sf.okapi.applications.rainbow.pipeline;

import java.io.File;
import java.net.URI;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.okapi.applications.rainbow.Input;
import net.sf.okapi.applications.rainbow.Project;
import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.IParametersEditorMapper;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.ParametersEditorMapper;
import net.sf.okapi.common.Util;
import net.sf.okapi.common.filters.FilterConfigurationMapper;
import net.sf.okapi.common.filters.IFilterConfigurationMapper;
import net.sf.okapi.common.pipeline.IPipeline;
import net.sf.okapi.common.pipeline.IPipelineStep;
import net.sf.okapi.common.pipeline.Pipeline;
import net.sf.okapi.common.pipelinedriver.BatchItemContext;
import net.sf.okapi.common.pipelinedriver.IPipelineDriver;
import net.sf.okapi.common.pipelinedriver.PipelineDriver;
import net.sf.okapi.common.plugins.PluginItem;
import net.sf.okapi.common.plugins.PluginsManager;
import net.sf.okapi.common.resource.RawDocument;
import net.sf.okapi.steps.leveraging.LeveragingStep;

public class PipelineWrapper {
	
	private final Logger logger = LoggerFactory.getLogger(getClass());

	private Map<String, StepInfo> availableSteps;
	private Map<String, ClassLoader> pluginConnectors;
	private String path;
	private ArrayList<StepInfo> steps;
	private IPipelineDriver driver;
	private IFilterConfigurationMapper fcMapper;
	private IParametersEditorMapper peMapper;
	private PluginsManager pm;

	public PipelineWrapper (IFilterConfigurationMapper fcMapper,
		String appFolder,
		PluginsManager pm,
		String rootDir,
		String inputRootDir,
		Object uiParent)
	{
		this.fcMapper = fcMapper;
		this.pm = pm;
		steps = new ArrayList<StepInfo>();
		driver = new PipelineDriver();
		driver.setFilterConfigurationMapper(this.fcMapper);
		driver.setRootDirectories(rootDir, inputRootDir);
		driver.setUIParent(uiParent);
		
		refreshAvailableStepsList();
	}
	
	public void refreshAvailableStepsList () {
		// Hard-wired steps
		buildStepList();
		// Discover and add plug-ins
		addFromPlugins(pm);
	}
	
	public void addFromPlugins (PluginsManager pm) {
		try {
			pluginConnectors = new LinkedHashMap<String, ClassLoader>(); 
			List<PluginItem> plugins = pm.getList();
			URLClassLoader classLoader = pm.getClassLoader();
			for ( PluginItem item : plugins ) {
				if ( item.getType() == PluginItem.TYPE_IQUERY ) {
					pluginConnectors.put(item.getClassName(), classLoader);
					continue;
				}
				
				// Skip plug-ins that are not steps
				if ( item.getType() != PluginItem.TYPE_IPIPELINESTEP ) continue;
				try {
					// Instantiate the step and get its info
					IPipelineStep ps = (IPipelineStep)Class.forName(item.getClassName(), true, classLoader).newInstance();
					IParameters params = ps.getParameters();
					StepInfo stepInfo = new StepInfo(ps.getName(), ps.getDescription(), ps.getClass().getName(), classLoader,
						(params==null) ? null : params.getClass().getName());
					
					// Try to get the editor info if needed
					if ( params != null ) {
						stepInfo.paramsData = params.toString();
						if ( item.getEditorDescriptionProvider() != null ) {
							peMapper.addDescriptionProvider(item.getEditorDescriptionProvider(), stepInfo.paramsClass);
						}
						if ( item.getParamsEditor() != null ) {
							peMapper.addEditor(item.getParamsEditor(), stepInfo.paramsClass);
						}
					}
					
					// Add the step
					availableSteps.put(stepInfo.stepClass, stepInfo);
				}
				catch ( Throwable e ) {
					logger.warn("Could not instantiate step '{}' because of error.\n"+e.getMessage(),
						item.getClassName());
				}
			}
			if (fcMapper instanceof FilterConfigurationMapper) {
				((FilterConfigurationMapper) fcMapper).addFromPlugins(pm);
			}	
		}
		catch ( Throwable e ) {
			throw new RuntimeException("Error when creating the plug-ins lists.\n"+e.getMessage(), e);
		}
	}

	public void setRootDirectories (String rootDir,
		String inputRootDir)
	{
		driver.setRootDirectories(rootDir, inputRootDir);
	}
	
	/**
	 * Populate the hard-wired steps.
	 */
	private void buildStepList () {
		availableSteps = new LinkedHashMap<String, StepInfo>();		
		peMapper = new ParametersEditorMapper();
		try {
			IPipelineStep ps = (IPipelineStep)Class.forName(
				"net.sf.okapi.steps.common.RawDocumentToFilterEventsStep").newInstance();
			StepInfo step = new StepInfo(ps.getName(), ps.getDescription(), ps.getClass().getName(), null, null);
			IParameters params = ps.getParameters();
			if ( params != null ) {
				step.paramsData = params.toString();
			}
			availableSteps.put(step.stepClass, step);
				
			ps = (IPipelineStep)Class.forName(
					"net.sf.okapi.steps.common.FilterEventsToRawDocumentStep").newInstance();
			step = new StepInfo(ps.getName(), ps.getDescription(), ps.getClass().getName(), null, null);
			params = ps.getParameters();
			if ( params != null ) {
				step.paramsData = params.toString();
			}
			availableSteps.put(step.stepClass, step);

// Usable only via script			
//			ps = (IPipelineStep)Class.forName(
//					"net.sf.okapi.steps.common.FilterEventsWriterStep").newInstance();
//			params = ps.getParameters();
//			step = new StepInfo(ps.getClass().getSimpleName(),
//				ps.getName(), ps.getDescription(), ps.getClass().getName(), null, null);
//			if ( params != null ) {
//				step.paramsData = params.toString();
//			}
//			availableSteps.put(step.stepClass, step);

// Usable only via script			
//			ps = (IPipelineStep)Class.forName(
//				"net.sf.okapi.steps.common.RawDocumentWriterStep").newInstance();
//			params = ps.getParameters();
//			step = new StepInfo(ps.getClass().getSimpleName(),
//				ps.getName(), ps.getDescription(), ps.getClass().getName(), null, null);
//			if ( params != null ) {
//				step.paramsData = params.toString();
//			}
//			availableSteps.put(step.stepClass, step);

			ps = (IPipelineStep)Class.forName(
				"net.sf.okapi.steps.leveraging.BatchTmLeveragingStep").newInstance();
			params = ps.getParameters();
			step = new StepInfo(ps.getName(), ps.getDescription(), ps.getClass().getName(), null,
				params.getClass().getName());
			if ( params != null ) {
				step.paramsData = params.toString();
				peMapper.addDescriptionProvider("net.sf.okapi.steps.leveraging.Parameters", step.paramsClass);
			}
			availableSteps.put(step.stepClass, step);

			ps = (IPipelineStep)Class.forName(
				"net.sf.okapi.steps.batchtranslation.BatchTranslationStep").newInstance();
			params = ps.getParameters();
			step = new StepInfo(ps.getName(), ps.getDescription(), ps.getClass().getName(), null,
				params.getClass().getName());
			if ( params != null ) {
				step.paramsData = params.toString();
				peMapper.addDescriptionProvider("net.sf.okapi.steps.batchtranslation.Parameters", step.paramsClass);
			}
			availableSteps.put(step.stepClass, step);

			ps = (IPipelineStep)Class.forName(
				"net.sf.okapi.steps.bomconversion.BOMConversionStep").newInstance();
			params = ps.getParameters();
			step = new StepInfo(ps.getName(), ps.getDescription(), ps.getClass().getName(), null,
				params.getClass().getName());
			if ( params != null ) {
				step.paramsData = params.toString();
				peMapper.addEditor("net.sf.okapi.steps.bomconversion.ui.ParametersEditor", step.paramsClass);
			}
			availableSteps.put(step.stepClass, step);

			ps = (IPipelineStep)Class.forName(
				"net.sf.okapi.steps.charlisting.CharListingStep").newInstance();
			params = ps.getParameters();
			step = new StepInfo(ps.getName(), ps.getDescription(), ps.getClass().getName(), null,
				params.getClass().getName());
			if ( params != null ) {
				step.paramsData = params.toString();
				peMapper.addDescriptionProvider("net.sf.okapi.steps.charlisting.Parameters", step.paramsClass);
			}
			availableSteps.put(step.stepClass, step);

			ps = (IPipelineStep)Class.forName(
				"net.sf.okapi.steps.codesremoval.CodesRemovalStep").newInstance();
			params = ps.getParameters();
			step = new StepInfo(ps.getName(), ps.getDescription(), ps.getClass().getName(), null,
				params.getClass().getName());
			if ( params != null ) {
				step.paramsData = params.toString();
				peMapper.addDescriptionProvider("net.sf.okapi.steps.codesremoval.Parameters", step.paramsClass);
			}
			availableSteps.put(step.stepClass, step);
			
			ps = (IPipelineStep)Class.forName(
				"net.sf.okapi.steps.common.codesimplifier.CodeSimplifierStep").newInstance();
			params = ps.getParameters();
			step = new StepInfo(ps.getName(), ps.getDescription(), ps.getClass().getName(), null,
				params.getClass().getName());
			if ( params != null ) {
				step.paramsData = params.toString();
				peMapper.addDescriptionProvider("net.sf.okapi.steps.common.codesimplifier.Parameters", step.paramsClass);
			}
			availableSteps.put(step.stepClass, step);

			ps = (IPipelineStep)Class.forName(
				"net.sf.okapi.steps.common.ConvertSegmentsToTextUnitsStep").newInstance();
			step = new StepInfo(ps.getName(), ps.getDescription(), ps.getClass().getName(), null, null);
			// No parameters
			availableSteps.put(step.stepClass, step);

			ps = (IPipelineStep)Class.forName(
				"net.sf.okapi.steps.common.createtarget.CreateTargetStep").newInstance();
			params = ps.getParameters();
			step = new StepInfo(ps.getName(), ps.getDescription(), ps.getClass().getName(), null,
				params.getClass().getName());
			if ( params != null ) {
				step.paramsData = params.toString();
				peMapper.addDescriptionProvider("net.sf.okapi.steps.common.createtarget.Parameters", step.paramsClass);
			}
			availableSteps.put(step.stepClass, step);

			ps = (IPipelineStep)Class.forName(
				"net.sf.okapi.steps.desegmentation.DesegmentationStep").newInstance();
			params = ps.getParameters();
			step = new StepInfo(ps.getName(), ps.getDescription(), ps.getClass().getName(), null,
				params.getClass().getName());
			if ( params != null ) {
				step.paramsData = params.toString();
				peMapper.addDescriptionProvider("net.sf.okapi.steps.desegmentation.Parameters", step.paramsClass);
			}
			availableSteps.put(step.stepClass, step);

			ps = (IPipelineStep)Class.forName(
				"net.sf.okapi.steps.diffleverage.DiffLeverageStep").newInstance();
			params = ps.getParameters();
			step = new StepInfo(ps.getName(), ps.getDescription(), ps.getClass().getName(), null,
				params.getClass().getName());
			if ( params != null ) {
				step.paramsData = params.toString();
				peMapper.addDescriptionProvider("net.sf.okapi.steps.diffleverage.Parameters", step.paramsClass);
			}
			availableSteps.put(step.stepClass, step);

			ps = (IPipelineStep)Class.forName(
				"net.sf.okapi.steps.encodingconversion.EncodingConversionStep").newInstance();
			params = ps.getParameters();
			step = new StepInfo(ps.getName(), ps.getDescription(), ps.getClass().getName(), null,
				params.getClass().getName());
			if ( params != null ) {
				step.paramsData = params.toString();
				peMapper.addEditor("net.sf.okapi.steps.encodingconversion.ui.ParametersEditor", step.paramsClass);
			}
			availableSteps.put(step.stepClass, step);

			ps = (IPipelineStep)Class.forName(
				"net.sf.okapi.steps.enrycher.EnrycherStep").newInstance();
			params = ps.getParameters();
			step = new StepInfo(ps.getName(), ps.getDescription(), ps.getClass().getName(), null,
				params.getClass().getName());
			if ( params != null ) {
				step.paramsData = params.toString();
				peMapper.addDescriptionProvider("net.sf.okapi.steps.enrycher.Parameters", step.paramsClass);
			}
			availableSteps.put(step.stepClass, step);
			
			ps = (IPipelineStep)Class.forName(
				"net.sf.okapi.steps.externalcommand.ExternalCommandStep").newInstance();
			params = ps.getParameters();
			step = new StepInfo(ps.getName(), ps.getDescription(), ps.getClass().getName(), null,
				params.getClass().getName());
			if ( params != null ) {
				step.paramsData = params.toString();
				peMapper.addDescriptionProvider("net.sf.okapi.steps.externalcommand.Parameters", step.paramsClass);
			}
			availableSteps.put(step.stepClass, step);

			ps = (IPipelineStep)Class.forName(
				"net.sf.okapi.steps.common.ExtractionVerificationStep").newInstance();
			params = ps.getParameters();
			step = new StepInfo(ps.getName(), ps.getDescription(), ps.getClass().getName(), null,
				params.getClass().getName());
			if ( params != null ) {
				step.paramsData = params.toString();
				peMapper.addDescriptionProvider("net.sf.okapi.steps.common.ExtractionVerificationStepParameters", step.paramsClass);
			}
			availableSteps.put(step.stepClass, step);
			
			ps = (IPipelineStep)Class.forName(
			 	"net.sf.okapi.steps.formatconversion.FormatConversionStep").newInstance();
			params = ps.getParameters();
			step = new StepInfo(ps.getName(), ps.getDescription(), ps.getClass().getName(), null,
				params.getClass().getName());
			if ( params != null ) {
				step.paramsData = params.toString();
				peMapper.addDescriptionProvider("net.sf.okapi.steps.formatconversion.Parameters", step.paramsClass);
			}
			availableSteps.put(step.stepClass, step);

			ps = (IPipelineStep)Class.forName(
				"net.sf.okapi.steps.fullwidthconversion.FullWidthConversionStep").newInstance();
			params = ps.getParameters();
			step = new StepInfo(ps.getName(), ps.getDescription(), ps.getClass().getName(), null,
				params.getClass().getName());
			if ( params != null ) {
				step.paramsData = params.toString();
				peMapper.addEditor("net.sf.okapi.steps.fullwidthconversion.ui.ParametersEditor", step.paramsClass);
			}
			availableSteps.put(step.stepClass, step);

			ps = (IPipelineStep)Class.forName(
			 	"net.sf.okapi.steps.generatesimpletm.GenerateSimpleTmStep").newInstance();
			params = ps.getParameters();
			step = new StepInfo(ps.getName(), ps.getDescription(), ps.getClass().getName(), null,
				params.getClass().getName());
			if ( params != null ) {
				step.paramsData = params.toString();
				peMapper.addDescriptionProvider("net.sf.okapi.steps.generatesimpletm.ParametersUI", step.paramsClass);
			}
			availableSteps.put(step.stepClass, step);

			ps = (IPipelineStep)Class.forName(
				"net.sf.okapi.steps.gttbatchtranslation.GTTBatchTranslationStep").newInstance();
			params = ps.getParameters();
			step = new StepInfo(ps.getName(), ps.getDescription(), ps.getClass().getName(), null,
				params.getClass().getName());
			if ( params != null ) {
				step.paramsData = params.toString();
				peMapper.addDescriptionProvider("net.sf.okapi.steps.gttbatchtranslation.Parameters", step.paramsClass);
			}
			availableSteps.put(step.stepClass, step);

			ps = (IPipelineStep)Class.forName(
				"net.sf.okapi.steps.idaligner.IdBasedAlignerStep").newInstance();
			params = ps.getParameters();
			step = new StepInfo(ps.getName(), ps.getDescription(), ps.getClass().getName(), null,
				params.getClass().getName());
			if ( params != null ) {
				step.paramsData = params.toString();
				peMapper.addDescriptionProvider("net.sf.okapi.steps.idaligner.Parameters", step.paramsClass);
			}
			availableSteps.put(step.stepClass, step);
			
			ps = (IPipelineStep)Class.forName(
				"net.sf.okapi.steps.idbasedcopy.IdBasedCopyStep").newInstance();
			params = ps.getParameters();
			step = new StepInfo(ps.getName(), ps.getDescription(), ps.getClass().getName(), null,
				params.getClass().getName());
			if ( params != null ) {
				step.paramsData = params.toString();
				peMapper.addDescriptionProvider("net.sf.okapi.steps.idbasedcopy.Parameters", step.paramsClass);
			}
			availableSteps.put(step.stepClass, step);
			
			ps = (IPipelineStep)Class.forName(
				"net.sf.okapi.steps.imagemodification.ImageModificationStep").newInstance();
			params = ps.getParameters();
			step = new StepInfo(ps.getName(), ps.getDescription(), ps.getClass().getName(), null,
				params.getClass().getName());
			if ( params != null ) {
				step.paramsData = params.toString();
				peMapper.addDescriptionProvider("net.sf.okapi.steps.imagemodification.Parameters", step.paramsClass);
			}
			availableSteps.put(step.stepClass, step);

			ps = (IPipelineStep)Class.forName(
			 	"net.sf.okapi.steps.leveraging.LeveragingStep").newInstance();
			params = ps.getParameters();
			step = new StepInfo(ps.getName(), ps.getDescription(), ps.getClass().getName(), null,
				params.getClass().getName());
			if ( params != null ) {
				step.paramsData = params.toString();
				peMapper.addEditor("net.sf.okapi.steps.leveraging.ui.ParametersEditor", step.paramsClass);
			}
			availableSteps.put(step.stepClass, step);

			ps = (IPipelineStep)Class.forName(
				"net.sf.okapi.steps.linebreakconversion.LineBreakConversionStep").newInstance();
			params = ps.getParameters();
			step = new StepInfo(ps.getName(), ps.getDescription(), ps.getClass().getName(), null,
				params.getClass().getName());
			if ( params != null ) {
				step.paramsData = params.toString();
				peMapper.addDescriptionProvider("net.sf.okapi.steps.linebreakconversion.Parameters", step.paramsClass);
			}
			availableSteps.put(step.stepClass, step);

			ps = (IPipelineStep)Class.forName(
				"net.sf.okapi.steps.moses.ExtractionStep").newInstance();
			params = ps.getParameters();
			step = new StepInfo(ps.getName(), ps.getDescription(), ps.getClass().getName(), null, params.getClass().getName());
			if ( params != null ) {
				step.paramsData = params.toString();
				peMapper.addDescriptionProvider("net.sf.okapi.filters.mosestext.FilterWriterParameters", step.paramsClass);
			}
			availableSteps.put(step.stepClass, step);

			ps = (IPipelineStep)Class.forName(
				"net.sf.okapi.steps.moses.MergingStep").newInstance();
			params = ps.getParameters();
			step = new StepInfo(ps.getName(), ps.getDescription(), ps.getClass().getName(), null, params.getClass().getName());
			if ( params != null ) {
				step.paramsData = params.toString();
				peMapper.addDescriptionProvider("net.sf.okapi.steps.moses.MergingParameters", step.paramsClass);
			}
			availableSteps.put(step.stepClass, step);

			ps = (IPipelineStep)Class.forName(
				"net.sf.okapi.steps.msbatchtranslation.MSBatchTranslationStep").newInstance();
			params = ps.getParameters();
			step = new StepInfo(ps.getName(), ps.getDescription(), ps.getClass().getName(), null,
				params.getClass().getName());
			if ( params != null ) {
				step.paramsData = params.toString();
				peMapper.addDescriptionProvider("net.sf.okapi.steps.msbatchtranslation.Parameters", step.paramsClass);
			}
			availableSteps.put(step.stepClass, step);

			ps = (IPipelineStep)Class.forName(
				"net.sf.okapi.steps.msbatchtranslation.MSBatchSubmissionStep").newInstance();
			params = ps.getParameters();
			step = new StepInfo(ps.getName(), ps.getDescription(), ps.getClass().getName(), null,
				params.getClass().getName());
			if ( params != null ) {
				step.paramsData = params.toString();
				peMapper.addDescriptionProvider("net.sf.okapi.steps.msbatchtranslation.SubmissionParameters", step.paramsClass);
			}
			availableSteps.put(step.stepClass, step);

//TODO: un-comment when ready			
//			ps = (IPipelineStep)Class.forName(
//				"net.sf.okapi.steps.paraaligner.ParagraphAlignerStep").newInstance();
//			params = ps.getParameters();
//			step = new StepInfo(ps.getName(), ps.getDescription(), ps.getClass().getName(), null,
//				params.getClass().getName());
//			if ( params != null ) {
//				step.paramsData = params.toString();
//				peMapper.addDescriptionProvider("net.sf.okapi.steps.paraaligner.Parameters", step.paramsClass);
//			}
//			availableSteps.put(step.stepClass, step);
			
			ps = (IPipelineStep)Class.forName(
				"net.sf.okapi.steps.qualitycheck.QualityCheckStep").newInstance();
			params = ps.getParameters();
			step = new StepInfo(ps.getName(), ps.getDescription(), ps.getClass().getName(), null,
				params.getClass().getName());
			if ( params != null ) {
				step.paramsData = params.toString();
				peMapper.addEditor("net.sf.okapi.lib.ui.verification.ParametersEditor", step.paramsClass);
			}
			availableSteps.put(step.stepClass, step);

			ps = (IPipelineStep)Class.forName(
				"net.sf.okapi.steps.rainbowkit.creation.ExtractionStep").newInstance();
			params = ps.getParameters();
			step = new StepInfo(ps.getName(), ps.getDescription(), ps.getClass().getName(), null,
				params.getClass().getName());
			if ( params != null ) {
				step.paramsData = params.toString();
				peMapper.addEditor("net.sf.okapi.steps.rainbowkit.ui.CreationParametersEditor", step.paramsClass);
			}
			availableSteps.put(step.stepClass, step);

			ps = (IPipelineStep)Class.forName(
				"net.sf.okapi.steps.rainbowkit.postprocess.MergingStep").newInstance();
			params = ps.getParameters();
			step = new StepInfo(ps.getName(), ps.getDescription(), ps.getClass().getName(), null,
				params.getClass().getName());
			if ( params != null ) {
				step.paramsData = params.toString();
				peMapper.addDescriptionProvider("net.sf.okapi.steps.rainbowkit.postprocess.Parameters", step.paramsClass);
			}
			availableSteps.put(step.stepClass, step);

			ps = (IPipelineStep)Class.forName(
				"net.sf.okapi.steps.common.removetarget.RemoveTargetStep").newInstance();
			params = ps.getParameters();
			step = new StepInfo(ps.getName(), ps.getDescription(), ps.getClass().getName(), null,
				params.getClass().getName());
			if ( params != null ) {
				step.paramsData = params.toString();
				peMapper.addDescriptionProvider("net.sf.okapi.steps.common.removetarget.Parameters", step.paramsClass);
			}
			availableSteps.put(step.stepClass, step);

			ps = (IPipelineStep)Class.forName(
				"net.sf.okapi.steps.repetitionanalysis.RepetitionAnalysisStep").newInstance();
			params = ps.getParameters();
			step = new StepInfo(ps.getName(), ps.getDescription(), ps.getClass().getName(), null,
				params.getClass().getName());
			if ( params != null ) {
				step.paramsData = params.toString();
				peMapper.addDescriptionProvider("net.sf.okapi.steps.repetitionanalysis.Parameters", step.paramsClass);
			}
			availableSteps.put(step.stepClass, step);

			ps = (IPipelineStep)Class.forName(
				"net.sf.okapi.steps.common.ResourceSimplifierStep").newInstance();
			step = new StepInfo(ps.getName(), ps.getDescription(), ps.getClass().getName(), null, null);
			params = ps.getParameters();
			if ( params != null ) {
				step.paramsData = params.toString();
			}
			availableSteps.put(step.stepClass, step);
			
			ps = (IPipelineStep)Class.forName(
				"net.sf.okapi.steps.rtfconversion.RTFConversionStep").newInstance();
			params = ps.getParameters();
			step = new StepInfo(ps.getName(), ps.getDescription(), ps.getClass().getName(), null,
				params.getClass().getName());
			if ( params != null ) {
				step.paramsData = params.toString();
				peMapper.addDescriptionProvider("net.sf.okapi.steps.rtfconversion.Parameters", step.paramsClass);
			}
			availableSteps.put(step.stepClass, step);

			ps = (IPipelineStep)Class.forName(
				"net.sf.okapi.steps.scopingreport.ScopingReportStep").newInstance();
			params = ps.getParameters();
			step = new StepInfo(ps.getName(), ps.getDescription(), ps.getClass().getName(), null,
				params.getClass().getName());
			if ( params != null ) {
				step.paramsData = params.toString();
				peMapper.addDescriptionProvider("net.sf.okapi.steps.scopingreport.Parameters", step.paramsClass);
			}
			availableSteps.put(step.stepClass, step);

			ps = (IPipelineStep)Class.forName(
				"net.sf.okapi.steps.searchandreplace.SearchAndReplaceStep").newInstance();
			params = ps.getParameters();
			step = new StepInfo(ps.getName(), ps.getDescription(), ps.getClass().getName(), null,
				params.getClass().getName());
			if ( params != null ) {
				step.paramsData = params.toString();
				peMapper.addEditor("net.sf.okapi.steps.searchandreplace.ui.ParametersEditor", step.paramsClass);
			}
			availableSteps.put(step.stepClass, step);

			ps = (IPipelineStep)Class.forName(
				"net.sf.okapi.steps.segmentation.SegmentationStep").newInstance();
			params = ps.getParameters();
			step = new StepInfo(ps.getName(), ps.getDescription(), ps.getClass().getName(), null,
				params.getClass().getName());
			if ( params != null ) {
				step.paramsData = params.toString();
				peMapper.addEditor("net.sf.okapi.steps.segmentation.ui.ParametersEditor", step.paramsClass);
			}
			availableSteps.put(step.stepClass, step);

			ps = (IPipelineStep)Class.forName(
				"net.sf.okapi.steps.sentencealigner.SentenceAlignerStep").newInstance();
			params = ps.getParameters();
			step = new StepInfo(ps.getName(), ps.getDescription(), ps.getClass().getName(), null,
				params.getClass().getName());
			if ( params != null ) {
				step.paramsData = params.toString();
				peMapper.addDescriptionProvider("net.sf.okapi.steps.sentencealigner.Parameters", step.paramsClass);
			}
			availableSteps.put(step.stepClass, step);

			ps = (IPipelineStep)Class.forName(
			 	"net.sf.okapi.steps.simpletm2tmx.SimpleTM2TMXStep").newInstance();
			params = ps.getParameters();
			step = new StepInfo(ps.getName(), ps.getDescription(), ps.getClass().getName(), null,
				null);
			availableSteps.put(step.stepClass, step);

			ps = (IPipelineStep)Class.forName(
				"net.sf.okapi.steps.spacecheck.SpaceCheckStep").newInstance();
			params = ps.getParameters();
			step = new StepInfo(ps.getName(), ps.getDescription(), ps.getClass().getName(), null,
				null);
			availableSteps.put(step.stepClass, step);

			ps = (IPipelineStep)Class.forName(
				"net.sf.okapi.steps.termextraction.TermExtractionStep").newInstance();
			params = ps.getParameters();
			step = new StepInfo(ps.getName(), ps.getDescription(), ps.getClass().getName(), null,
				params.getClass().getName());
			if ( params != null ) {
				step.paramsData = params.toString();
				peMapper.addDescriptionProvider("net.sf.okapi.steps.termextraction.Parameters", step.paramsClass);
			}
			availableSteps.put(step.stepClass, step);

			ps = (IPipelineStep)Class.forName(
				"net.sf.okapi.steps.textmodification.TextModificationStep").newInstance();
			params = ps.getParameters();
			step = new StepInfo(ps.getName(), ps.getDescription(), ps.getClass().getName(), null,
				params.getClass().getName());
			if ( params != null ) {
				step.paramsData = params.toString();
				peMapper.addEditor("net.sf.okapi.steps.textmodification.ui.ParametersEditor", step.paramsClass);
			}
			availableSteps.put(step.stepClass, step);

			ps = (IPipelineStep)Class.forName(
		 		"net.sf.okapi.steps.tmimport.TMImportStep").newInstance();
			params = ps.getParameters();
			step = new StepInfo(ps.getName(), ps.getDescription(), ps.getClass().getName(), null,
				params.getClass().getName());
			if ( params != null ) {
				step.paramsData = params.toString();
				peMapper.addDescriptionProvider("net.sf.okapi.steps.tmimport.Parameters", step.paramsClass);
			}
			availableSteps.put(step.stepClass, step);

			ps = (IPipelineStep)Class.forName(
				"net.sf.okapi.steps.tokenization.TokenizationStep").newInstance();
			params = ps.getParameters();
			step = new StepInfo(ps.getName(), ps.getDescription(), ps.getClass().getName(), null,
				params.getClass().getName());
			if ( params != null ) {
				step.paramsData = params.toString();
				peMapper.addEditor("net.sf.okapi.steps.tokenization.ui.ParametersEditor", step.paramsClass);
			}
			availableSteps.put(step.stepClass, step);
			
			ps = (IPipelineStep)Class.forName(
				"net.sf.okapi.steps.translationcomparison.TranslationComparisonStep").newInstance();
			params = ps.getParameters();
			step = new StepInfo(ps.getName(), ps.getDescription(), ps.getClass().getName(), null,
				params.getClass().getName());
			if ( params != null ) {
				step.paramsData = params.toString();
				peMapper.addDescriptionProvider("net.sf.okapi.steps.translationcomparison.Parameters", step.paramsClass);
			}
			availableSteps.put(step.stepClass, step);

			ps = (IPipelineStep)Class.forName(
				"net.sf.okapi.steps.uriconversion.UriConversionStep").newInstance();
			params = ps.getParameters();
			step = new StepInfo(ps.getName(), ps.getDescription(), ps.getClass().getName(), null,
				params.getClass().getName());
			if ( params != null ) {
				step.paramsData = params.toString();
				peMapper.addEditor("net.sf.okapi.steps.uriconversion.ui.ParametersEditor", step.paramsClass);
			}
			availableSteps.put(step.stepClass, step);

			ps = (IPipelineStep)Class.forName(
				"net.sf.okapi.steps.wordcount.WordCountStep").newInstance();
			params = ps.getParameters();
			step = new StepInfo(ps.getName(), ps.getDescription(), ps.getClass().getName(), null,
				params.getClass().getName());
			if ( params != null ) {
				step.paramsData = params.toString();
				peMapper.addDescriptionProvider("net.sf.okapi.steps.wordcount.common.Parameters", step.paramsClass);
			}
			availableSteps.put(step.stepClass, step);

			ps = (IPipelineStep)Class.forName(
				"net.sf.okapi.steps.wordcount.SimpleWordCountStep").newInstance();
			params = ps.getParameters();
			step = new StepInfo(ps.getName(), ps.getDescription(), ps.getClass().getName(), null, null);
			if ( params != null ) {
				step.paramsData = params.toString();
			}
			availableSteps.put(step.stepClass, step);
			
			ps = (IPipelineStep)Class.forName(
				"net.sf.okapi.steps.xmlcharfixing.XMLCharFixingStep").newInstance();
			params = ps.getParameters();
			step = new StepInfo(ps.getName(), ps.getDescription(), ps.getClass().getName(), null,
				params.getClass().getName());
			if ( params != null ) {
				step.paramsData = params.toString();
				peMapper.addDescriptionProvider("net.sf.okapi.steps.xmlcharfixing.Parameters", step.paramsClass);
			}
			availableSteps.put(step.stepClass, step);
			
			ps = (IPipelineStep)Class.forName(
			"net.sf.okapi.steps.xliffsplitter.XliffJoinerStep").newInstance();
			params = ps.getParameters();
			step = new StepInfo(ps.getName(), ps.getDescription(), ps.getClass().getName(), null,
					params.getClass().getName());
			if ( params != null ) {
				step.paramsData = params.toString();
				peMapper.addDescriptionProvider("net.sf.okapi.steps.xliffsplitter.XliffJoinerParameters", step.paramsClass);
			}
			availableSteps.put(step.stepClass, step);

			ps = (IPipelineStep)Class.forName(
				"net.sf.okapi.steps.xliffsplitter.XliffSplitterStep").newInstance();
			params = ps.getParameters();
			step = new StepInfo(ps.getName(), ps.getDescription(), ps.getClass().getName(), null,
				params.getClass().getName());
			if ( params != null ) {
				step.paramsData = params.toString();
				peMapper.addDescriptionProvider("net.sf.okapi.steps.xliffsplitter.XliffSplitterParameters", step.paramsClass);
			}
			availableSteps.put(step.stepClass, step);
			
			ps = (IPipelineStep)Class.forName(
				"net.sf.okapi.steps.xmlanalysis.XMLAnalysisStep").newInstance();
			params = ps.getParameters();
			step = new StepInfo(ps.getName(), ps.getDescription(), ps.getClass().getName(), null,
				params.getClass().getName());
			if ( params != null ) {
				step.paramsData = params.toString();
				peMapper.addDescriptionProvider("net.sf.okapi.steps.xmlanalysis.Parameters", step.paramsClass);
			}
			availableSteps.put(step.stepClass, step);
			
			ps = (IPipelineStep)Class.forName(
				"net.sf.okapi.steps.xmlvalidation.XMLValidationStep").newInstance();
			params = ps.getParameters();
			step = new StepInfo(ps.getName(), ps.getDescription(), ps.getClass().getName(), null,
				params.getClass().getName());
			if ( params != null ) {
				step.paramsData = params.toString();
				peMapper.addDescriptionProvider("net.sf.okapi.steps.xmlvalidation.Parameters", step.paramsClass);
			}
			availableSteps.put(step.stepClass, step);
			
			ps = (IPipelineStep)Class.forName(
				"net.sf.okapi.steps.xsltransform.XSLTransformStep").newInstance();
			params = ps.getParameters();
			step = new StepInfo(ps.getName(), ps.getDescription(), ps.getClass().getName(), null,
				params.getClass().getName());
			if ( params != null ) {
				step.paramsData = params.toString();
				peMapper.addEditor("net.sf.okapi.steps.xsltransform.ui.ParametersEditor", step.paramsClass);
			}
			availableSteps.put(step.stepClass, step);

		}
		catch ( InstantiationException e ) {
			logger.warn("Could not instantiate a step.\n" + e.getMessage());
		}
		catch ( IllegalAccessException e ) {
			logger.warn("Illegal access for a step.\n" + e.getMessage());
		}
		catch ( ClassNotFoundException e ) {
			logger.warn("Step class not found.\n" + e.getMessage());
		}
		catch ( Throwable e ) {
			logger.warn("Error creating one of the step.\n" + e.getMessage());
		}
	}
	
	public void clear () {
		steps.clear();		
	}
	
	public String getPath () {
		return path;
	}
	
	public void setPath (String path) {
		this.path = path;
	}
	
	public IParametersEditorMapper getEditorMapper () {
		return peMapper;
	}
	
	public Map<String, StepInfo> getAvailableSteps () {
		return availableSteps;
	}
	
	public String getStringStorage () {
		copyInfoStepsToPipeline();
		PipelineStorage store = new PipelineStorage(availableSteps);
		store.write(driver.getPipeline());
		return store.getStringOutput();
	}
	
	public void reset () {
		clear();
		path = null;
		driver.setPipeline(new Pipeline());
	}
	
	public void loadFromStringStorageOrReset (String data) {
		if ( Util.isEmpty(data) ) {
			reset();
			return;
		}
		PipelineStorage store = new PipelineStorage(availableSteps, (CharSequence)data);
		loadPipeline(store.read(), null);
	}
	
	public void loadPipeline (IPipeline newPipeline,
		String path)
	{
		driver.setPipeline(newPipeline);
		// Set the info-steps
		StepInfo infoStep;
		IParameters params;
		steps.clear();
		for ( IPipelineStep step : driver.getPipeline().getSteps() ) {
			infoStep = new StepInfo(step.getName(), step.getDescription(),
				step.getClass().getName(), step.getClass().getClassLoader(), null);
			params = step.getParameters();
			if ( params != null ) {
				infoStep.paramsData = params.toString();
				infoStep.paramsClass = params.getClass().getName();
			}
			steps.add(infoStep);
		}
		this.path = path;
	}
	
	public void load (String path) {
		PipelineStorage store = new PipelineStorage(availableSteps, path);
		loadPipeline(store.read(), path);
	}
	
	public void save (String path) {
		PipelineStorage store = new PipelineStorage(availableSteps, path);
		copyInfoStepsToPipeline();
		store.write(driver.getPipeline());
		this.path = path;
	}
	
	public IPipeline getPipeline () {
		copyInfoStepsToPipeline();
		return driver.getPipeline();
	}
	
	public PluginsManager getPluginsManager() {
		return pm;		
	}
	
	private void copyInfoStepsToPipeline () {
		try {
			// Build the pipeline
			driver.setPipeline(new Pipeline());
			for ( StepInfo stepInfo : steps ) {
				IPipelineStep step;
				if ( stepInfo.loader == null ) {
					step = (IPipelineStep)Class.forName(stepInfo.stepClass).newInstance();
				}
				else {
					step = (IPipelineStep)Class.forName(stepInfo.stepClass,
						true, stepInfo.loader).newInstance();
				}
				// Update the parameters with the one in the pipeline storage
				IParameters params = step.getParameters();
				if (( params != null ) && ( stepInfo.paramsData != null )) {
					params.fromString(stepInfo.paramsData);
				}
				
				// Enable connectors from plug-ins
				if (step instanceof LeveragingStep) {
					LeveragingStep ls = (LeveragingStep) step;
					net.sf.okapi.steps.leveraging.Parameters lsParams = 
						(net.sf.okapi.steps.leveraging.Parameters) ls.getParameters();
					String connectorClassName = lsParams.getResourceClassName();
					ClassLoader connectorLoader = pluginConnectors.get(connectorClassName);
					
					ls.setConnectorContext(connectorLoader);
				}
				driver.addStep(step);
			}
		}
		catch ( InstantiationException e ) {
			throw new RuntimeException(e);
		}
		catch ( IllegalAccessException e ) {
			throw new RuntimeException(e);
		}
		catch ( ClassNotFoundException e ) {
			throw new RuntimeException(e);
		}
	}

	public void copyParametersToPipeline (IPipeline pipeline) {
		List<IPipelineStep> destSteps = pipeline.getSteps();
		if ( destSteps.size() != steps.size() ) {
			throw new RuntimeException("Parameters and destination do not match.");
		}
		StepInfo stepInfo;
		for ( int i=0; i<destSteps.size(); i++ ) {
			stepInfo = steps.get(i);
			IParameters params = destSteps.get(i).getParameters();
			if ( params != null ) {
				params.fromString(stepInfo.paramsData);
			}
		}
	}

	public void execute (Project prj) {
		execute(prj, null);
	}
	
	public void execute (Project prj, List<LocaleId> targetLocales) {
		copyInfoStepsToPipeline();
		// Set the batch items
		driver.clearItems();
		//TODO: Replace this: driver.getPipeline().getContext().removeProperty("outputFile");
		int f = -1;
		URI outURI;
		URI inpURI;
		RawDocument rawDoc;
		BatchItemContext bic;
		int inputRequested = driver.getRequestedInputCount();
		
		for ( Input item : prj.getList(0) ) {
			f++;
			// Set the data for the first input of the batch item
			outURI = (new File(prj.buildTargetPath(0, item.relativePath))).toURI();
			inpURI = (new File(prj.getInputRoot(0) + File.separator + item.relativePath)).toURI();
			rawDoc = new RawDocument(inpURI, prj.buildSourceEncoding(item),
				prj.getSourceLanguage(), prj.getTargetLanguage());
			rawDoc.setFilterConfigId(item.filterConfigId);
			rawDoc.setId(Util.makeId(item.relativePath)); // Set the document ID based on its relative path
			if(targetLocales != null)
				rawDoc.setTargetLocales(targetLocales);
			bic = new BatchItemContext(rawDoc, outURI, prj.buildTargetEncoding(item));
			
			// Add input/output data from other input lists if requested
			for ( int j=1; j<3; j++ ) {
				// Does the utility requests this list?
				if ( j >= inputRequested ) break; // No need to loop more
				// Do we have a corresponding input?
				if ( 3 > j ) {
					// Data is available
					List<Input> list = prj.getList(j);
					// Make sure we have an entry for that list
					if ( list.size() > f ) {
						Input item2 = list.get(f);
						// Input
						outURI = (new File(prj.buildTargetPath(j, item2.relativePath))).toURI();
						inpURI = (new File(prj.getInputRoot(j) + File.separator + item2.relativePath)).toURI();
						rawDoc = new RawDocument(inpURI, prj.buildSourceEncoding(item),
							prj.getSourceLanguage(), prj.getTargetLanguage());
						rawDoc.setFilterConfigId(item2.filterConfigId);
						rawDoc.setId(Util.makeId(item2.relativePath)); // Set the document ID based on its relative path
						if(targetLocales != null)
							rawDoc.setTargetLocales(targetLocales);
						bic.add(rawDoc, outURI, prj.buildTargetEncoding(item2));
					}
					// If no entry for that list: it'll be null
				}
				// Else: don't add anything
				// The lists will return null and that is up to the utility to check.
			}
			
			// Add the constructed batch item to the driver's list
			driver.addBatchItem(bic);
		}

		// Execute
		driver.processBatch();
	}

	public void addStep (StepInfo step) {
		steps.add(step);
	}
	
	public void insertStep (int index,
		StepInfo step)
	{
		if ( index == -1 ) {
			steps.add(step);
		}
		else {
			steps.add(index, step);
		}
	}
	
	public void removeStep (int index) {
		steps.remove(index);
	}
	
	public List<StepInfo> getSteps () {
		return steps;
	}

}
