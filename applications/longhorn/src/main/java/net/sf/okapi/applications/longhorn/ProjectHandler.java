/*===========================================================================
  Copyright (C) 2011 by the Okapi Framework contributors
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

package net.sf.okapi.applications.longhorn;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import net.sf.okapi.applications.longhorn.transport.XMLStringList;
import net.sf.okapi.applications.rainbow.Project;
import net.sf.okapi.applications.rainbow.batchconfig.BatchConfiguration;
import net.sf.okapi.applications.rainbow.lib.LanguageManager;
import net.sf.okapi.applications.rainbow.pipeline.PipelineWrapper;
import net.sf.okapi.common.Util;
import net.sf.okapi.common.filters.DefaultFilters;
import net.sf.okapi.common.filters.FilterConfigurationMapper;
import net.sf.okapi.common.plugins.PluginsManager;

import org.apache.commons.httpclient.HttpStatus;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;

/**
 * Handles Web-Service requests and delegates them to Rainbow/Okapi.
 * Also does the handling of the input and output files.
 * 
 *
 * Basic workflow for processing files with the web-service:
 * 
 * <ol>
 *	<li> POST	/projects/new
 *	<li> POST	/projects/1/batchConfiguration
 *	<li> PUT	/projects/1/inputFiles/a.html
 *	<li> PUT	/projects/1/inputFiles/b.html
 *	<li> PUT	/projects/1/inputFiles/c.html
 *	<li> POST	/projects/1/tasks/execute
 *	<li> GET	/projects/1/outputFiles
 *	<li> GET	/projects/1/outputFiles/a.out.html
 *	<li> GET	/projects/1/outputFiles/b.out.html
 *	<li> GET	/projects/1/outputFiles/c.out.html
 *	<li> DEL	/projects/1
 * </ol>
 */
@Path("/projects")
public class ProjectHandler {
	private static final Logger LOGGER = Logger.getLogger(ProjectHandler.class.getName());
	private static final String CURRENT_PROJECT_PIPELINE = "currentProjectPipeline";
	
	//TODO DEL for input file
	//TODO DEL for output file
	//TODO DEL /projects/outputFiles to clear output directory
	//TODO GET for batch conf
	//TODO POST to add input files as archive
	//TODO POST to get output files as archive

	/**
	 * Create a new project to work with.
	 * 
	 * @return The new project's URI
	 */
	@POST
	@Path("/new")
	public Response createProject(@Context UriInfo uriInfo) {
		
		int projId = WorkspaceUtils.determineNewProjectId();
		
		File workingDir = new File(WorkspaceUtils.getWorkingDirectory());
		if (!workingDir.exists())
			LOGGER.log(Level.INFO, "The working directory " + workingDir.getAbsolutePath() + " doesn't exist. " +
					"It will be created.");
		
		Util.createDirectories(WorkspaceUtils.getInputDirPath(projId) + File.separator);
		Util.createDirectories(WorkspaceUtils.getConfigDirPath(projId) + File.separator);
		Util.createDirectories(WorkspaceUtils.getOutputDirPath(projId) + File.separator);
		
		URI projectUri = uriInfo.getAbsolutePath().resolve(projId + "");
		return Response.created(projectUri).build();
	}

	/**
	 * @return A list of all existing project folders (Integers in numerical order)
	 */
	@GET
	@Path("/")
	@Produces(MediaType.TEXT_XML)
	public XMLStringList getProjects() {
		
		ArrayList<Integer> projIds = WorkspaceUtils.getProjectIds();
		return new XMLStringList(projIds);
	}

	/**
	 * Deletes a project directory. Should be used to clean up after all processing is done.
	 * 
	 * @param projId The id of the project to delete
	 * @return
	 */
	@DELETE
	@Path("/{projId}")
	public Response deleteProject(@PathParam("projId") int projId) {
		
		//TODO Check why some files remain on the FS
		//TODO The last input file processed in the pipeline seems to remain opened
		Util.deleteDirectory(WorkspaceUtils.getProjectPath(projId), false);

		int status = HttpStatus.SC_OK;
		return Response.status(status).build();
	}

	/**
	 * Installs the posted batch configuration file in the project with the given id.
	 * This batch configuration will be used to process the input files.
	 * 
	 * @param projId The id of the project the batch configuration shall be added to
	 * @param input The batch configuration file as part of a multi-part form. The parameter must have the name from <code>WorkspaceUtils.BATCH_CONF_PARAM</code>
	 * @return
	 */
	@POST
	@Path("/{projId}/batchConfiguration")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public Response addBatchConfigurationFile(@PathParam("projId") int projId, MultipartFormDataInput input) {

		try {
			File tmpFile = input.getFormDataPart(WorkspaceUtils.BATCH_CONF_PARAM, File.class, null);
			File targetFile = WorkspaceUtils.getBatchConfigurationFile(projId);
			Util.copyFile(tmpFile, targetFile);
			
			PipelineWrapper pipelineWrapper = preparePipelineWrapper(projId);
			
			// install batch configuration to config directory
			BatchConfiguration bconf = new BatchConfiguration();
			bconf.installConfiguration(targetFile.getAbsolutePath(),
					WorkspaceUtils.getConfigDirPath(projId), pipelineWrapper.getAvailableSteps());
		}
		catch (IOException e) {
			int status = HttpStatus.SC_INTERNAL_SERVER_ERROR;
			return Response.status(status).build();
		}

		int status = HttpStatus.SC_OK;
		return Response.status(status).build();
	}

	/**
	 * Stores the posted file in the input file's directory of the project with the given id.
	 * 
	 * @param projId The id of the project the file shall be added to
	 * @param filename The file's original filename
	 * @param input The input file as part of a multi-part form. The parameter must have the name from
	 *            <code>WorkspaceUtils.BATCH_CONF_PARAM</code>
	 * @return
	 */
	@PUT
	@Path("/{projId}/inputFiles/{filename}")
	public Response addProjectInputFile(@PathParam("projId") int projId, @PathParam("filename") String filename,
			MultipartFormDataInput input) {
		
		try {
			File tmpFile = input.getFormDataPart(WorkspaceUtils.INPUT_FILE_PARAM, File.class, null);
			File targetFile = WorkspaceUtils.getInputFile(projId, filename);
			Util.copyFile(tmpFile, targetFile);
		}
		catch (IOException e) {
			int status = HttpStatus.SC_INTERNAL_SERVER_ERROR;
			return Response.status(status).build();
		}

		int status = HttpStatus.SC_OK;
		return Response.status(status).build();
	}

	/**
	 * Stores the posted file in the input file's directory of the project with the given id.
	 * 
	 * @param projId The id of the project the file shall be added to
	 * @param filename The file's original filename
	 * @param input The input file as part of a multi-part form. The parameter must have the name from
	 *            <code>WorkspaceUtils.BATCH_CONF_PARAM</code>
	 * @return
	 */
	@POST
	@Path("/{projId}/inputFiles/{filename}")
	public Response addProjectInputFilePost(@PathParam("projId") int projId, @PathParam("filename") String filename,
			MultipartFormDataInput input) {
		
		// TODO How to handle sub-directories?
		
		try {
			File tmpFile = input.getFormDataPart(WorkspaceUtils.INPUT_FILE_PARAM, File.class, null);
			File targetFile = WorkspaceUtils.getInputFile(projId, filename);
			Util.copyFile(tmpFile, targetFile);
		}
		catch (IOException e) {
			int status = HttpStatus.SC_INTERNAL_SERVER_ERROR;
			return Response.status(status).build();
		}

		int status = HttpStatus.SC_OK;
		return Response.status(status).build();
	}
	
	/**
	 * @param projId The id of a project
	 * @return A list of the names of all input files uploaded yet
	 */
	@GET
	@Path("/{projId}/inputFiles")
	@Produces(MediaType.TEXT_XML)
	public XMLStringList getProjectInputFiles(@PathParam("projId") int projId) {
		
		ArrayList<String> inputFiles = WorkspaceUtils.getInputFileNames(projId);
		return new XMLStringList(inputFiles);
	}

	/**
	 * Retrieve one of the input files that were added to the project before.
	 * 
	 * @param projId The id of a project
	 * @param filename The name of the input file to fetch
	 * @return The specified file from the project
	 */
	@GET
	@Path("/{projId}/inputFiles/{filename}")
	@Produces(MediaType.WILDCARD)
	public File getProjectInputFile(
			@PathParam("projId") int projId, @PathParam("filename") String filename) {
		
		return WorkspaceUtils.getInputFile(projId, filename);
	}

	/**
	 * Executes the uploaded batch configuration on the input files that have been added.
	 * 
	 * @param projId The id of the project to be executed
	 * @return
	 */
	@POST
	@Path("/{projId}/tasks/execute")
	public Response executeProject(@PathParam("projId") int projId) {

		try {
			// Create a new, empty rainbow project
			Project rainbowProject = new Project(new LanguageManager());
			rainbowProject.setCustomParametersFolder(WorkspaceUtils.getConfigDirPath(projId));
			rainbowProject.setUseCustomParametersFolder(true);
			
			// Create a pipeline wrapper
			PipelineWrapper pipelineWrapper = preparePipelineWrapper(projId);
			
			// Load pipeline into the rainbow project
			File pipelineFile = WorkspaceUtils.getPipelineFile(projId);
			pipelineWrapper.load(pipelineFile.getAbsolutePath());
			rainbowProject.setUtilityParameters(CURRENT_PROJECT_PIPELINE, pipelineWrapper.getStringStorage());

			// Set new input and output root
			rainbowProject.setInputRoot(0, WorkspaceUtils.getInputDirPath(projId), true);
			rainbowProject.setOutputRoot(WorkspaceUtils.getOutputDirPath(projId));
			rainbowProject.setUseOutputRoot(true);
			
			// Load mapping of filter configs to file extensions
			HashMap<String, String> filterConfigByExtension = loadFilterConfigurationMapping(projId);

			// Add files to project input list
			addDocumentsToProject(projId, rainbowProject, filterConfigByExtension);

			// Execute pipeline
			pipelineWrapper.execute(rainbowProject);
		}
		catch (Exception e) {
			int status = HttpStatus.SC_INTERNAL_SERVER_ERROR;
			String type = MediaType.TEXT_PLAIN;
			String body = e.toString();
			return Response.status(status).type(type).entity(body).build();
		}

		int status = HttpStatus.SC_OK;
		return Response.status(status).build();
	}

	/**
	 * Adds all input files from the local project directory with the specified projId to the Rainbow project.
	 * The HashMap will be used to assign filter configurations to the files (by the file's extension).
	 * 
	 * @param projId The id of the local project in which the input files are located
	 * @param rainbowProject The Rainbow project to which the input files shall be added
	 * @param filterConfigByExtension The mapping from file extensions (including dot, ".html" for example)
	 * 			to filter configurations (e.g. "okf_html@Customized")
	 */
	private void addDocumentsToProject(int projId, Project rainbowProject,
			HashMap<String, String> filterConfigByExtension) {
		
		for (File inputFile : WorkspaceUtils.getInputFiles(projId)) {
			
			String extension = Util.getExtension(inputFile.getName());
			String filterConfigurationId = filterConfigByExtension.get(extension);

			int status = rainbowProject.addDocument(
					0, inputFile.getAbsolutePath(), null, null, filterConfigurationId, false);
			
			if (status == 1)
				throw new RuntimeException("Adding document " + inputFile.getName() + " to list of input files failed");
		}
	}

	/**
	 * Loads project's file extension to filter configuration mapping.
	 * 
	 * @param projId The id of the project that has the mapping file in it's configuration sub-folder
	 * @return A HashMap with the file extension (keys) to filter configuration (values) mapping
	 * @throws IOException If the file could not be read or it doesn't exist
	 */
	private HashMap<String, String> loadFilterConfigurationMapping(int projId)
			throws IOException {
		
		BufferedReader fh = new BufferedReader(new FileReader(WorkspaceUtils.getFilterMappingFile(projId)));
		HashMap<String, String> filterConfigByExtension = new HashMap<String, String>();
		
		String s;
		
		while ((s = fh.readLine()) != null) {
			String fields[] = s.split("\t");
			String ext = fields[0];
			String fc = fields[1];
			
			filterConfigByExtension.put(ext, fc);
		}
		fh.close();

		return filterConfigByExtension;
	}

	/**
	 * Loads the default filter configurations from Okapi and also the custom filter
	 * configurations and plug-ins from the project's configuration directory
	 * (where the batch configuration should have been installed to).
	 * 
	 * @param projId The id of a local project
	 * @return A PipelineWrapper using all available filter configurations and plug-ins
	 */
	private PipelineWrapper preparePipelineWrapper(int projId) {
		
		// Load local plug-ins
		// TODO Check if this works
		PluginsManager plManager = new PluginsManager();
		plManager.discover(new File(WorkspaceUtils.getConfigDirPath(projId)), true);

		// Initialize filter configurations
		FilterConfigurationMapper fcMapper = new FilterConfigurationMapper();
		DefaultFilters.setMappings(fcMapper, false, true);
		fcMapper.setCustomConfigurationsDirectory(WorkspaceUtils.getConfigDirPath(projId));
		fcMapper.updateCustomConfigurations();

		// Load pipeline
		PipelineWrapper pipelineWrapper = new PipelineWrapper(fcMapper, WorkspaceUtils.getConfigDirPath(projId),
				plManager, WorkspaceUtils.getInputDirPath(projId), null);
		pipelineWrapper.addFromPlugins(plManager);
		return pipelineWrapper;
	}

	/**
	 * @param projId The id of a local project
	 * @return A list of the names of all output files that have been generated in that project
	 */
	@GET
	@Path("/{projId}/outputFiles")
	@Produces(MediaType.TEXT_XML)
	public XMLStringList getProjectOutputFiles(@PathParam("projId") int projId) {
		
		ArrayList<String> outputFiles = WorkspaceUtils.getOutputFileNames(projId);
		return new XMLStringList(outputFiles);
	}

	/**
	 * Retrieve one of the output files generated by Okapi/Rainbow.
	 * 
	 * @param projId The id of a project
	 * @param filename The name of the input file to fetch
	 * @return The specified file from the project
	 */
	@GET
	@Path("/{projId}/outputFiles/{filename}")
	@Produces(MediaType.WILDCARD)
	public File getProjectOutputFile(
			@PathParam("projId") int projId, @PathParam("filename") String filename) {
		
		return WorkspaceUtils.getOutputFile(projId, filename);
	}
}
