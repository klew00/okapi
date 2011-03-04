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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sf.okapi.common.DefaultFilenameFilter;

/**
 * Utilities for the web-service's project and file handling.
 */
public final class WorkspaceUtils {
	private static final Logger LOGGER = Logger.getLogger(WorkspaceUtils.class.getName());
	private static final String PLUGINS = "plugins";
	private static final String BATCH_CONF = "settings.bconf";
	private static final String EXTENSIONS_MAPPING = "extensions-mapping.txt";
	private static final String INPUT = "input";
	private static final String CONFIG = "config";
	private static final String OUTPUT = "output";
	private static final FilenameFilter DIRECTORY_FILTER = new FilenameFilter() {
		@Override
		public boolean accept(File dir, String name) {
			File file = new File(dir.getAbsolutePath() + File.separator + name);
			return file.isDirectory();
		}
	};

	public static final String BATCH_CONF_PARAM = "batchConfiguration";
	public static final String INPUT_FILE_PARAM = "inputFile";

	/**
	 * @return The directory where the local projects will be created and saved temporarily
	 */
	public static String getWorkingDirectory() {
		//TODO Don't re-load that every time!
		Configuration conf = loadConfig();
		return conf.getWorkingDirectory();
	}
	
	/**
	 * @return The user's configuration (if <code>System.getProperty("user.home") + "/okapi-longhorn-configuration.xml"</code>
	 * 		was found) or the default configuration
	 */
	private static Configuration loadConfig() {
		File userConfig = new File( System.getProperty("user.home") + "/okapi-longhorn-configuration.xml");
		
		try {
			if(userConfig.exists())
				return new Configuration(new FileInputStream(userConfig));
			else
				LOGGER.log(Level.INFO, "No system specific configuration was found at " + userConfig.getAbsolutePath() + ". " +
						"The default configuration will be loaded instead.");
		}
		catch (FileNotFoundException e) {
			// This should be impossible, because we checked for the existence of the file
			throw new RuntimeException(e);
		}
		catch (IllegalArgumentException e) {
			LOGGER.log(Level.WARNING, "An error occurred while loading the system specific configuration. " +
					"The default configuration will be loaded instead.", e);
		}
		
		return new Configuration();
	}

	/**
	 * @param projId The id of a local project
	 * @return The project's absolute path on the file system
	 */
	public static String getProjectPath(int projId) {
		
		return getWorkingDirectory() + File.separator + projId;
	}

	/**
	 * @param projId The id of a local project
	 * @return The project's batch configuration file
	 */
	public static File getBatchConfigurationFile(int projId) {
		
		return new File(getProjectPath(projId) + File.separator + BATCH_CONF);
	}
	
	/**
	 * @param projId The id of a local project
	 * @return The absolute path of the project's input files directory on the file system
	 */
	public static String getInputDirPath(int projId) {
		
		return getProjectPath(projId) + File.separator + INPUT;
	}
	
	/**
	 * @param projId The id of a local project
	 * @param filename The name of the file to return
	 * @return The input file with the specified file name that belongs to the project
	 */
	public static File getInputFile(int projId, String filename) {
		
		return new File(getInputDirPath(projId) + File.separator + filename);
	}
	
	/**
	 * @param projId The id of a local project
	 * @return All input files belonging to the project
	 */
	public static Collection<File> getInputFiles(int projId) {
		
		return Arrays.asList(getFilteredFiles(getInputDirPath(projId), null));
	}
	
	/**
	 * @param projId The id of a local project
	 * @return A (string) list of the file names of the project's input files
	 */
	public static ArrayList<String> getInputFileNames(int projId) {
		
		Collection<File> files = getInputFiles(projId);
		return getFileNames(files);
	}
	
	/**
	 * @param projId The id of a local project
	 * @return The absolute path of the project's output files directory on the file system
	 */
	public static String getOutputDirPath(int projId) {
		
		return getProjectPath(projId) + File.separator + OUTPUT;
	}
	
	/**
	 * @param projId The id of a local project
	 * @return The absolute path of the project's configuration files directory on the file system
	 */
	public static String getConfigDirPath(int projId) {
		
		return getProjectPath(projId) + File.separator + CONFIG;
	}

	/**
	 * @param projId The id of a local project
	 * @return The first file in the project's configuration directory with the extension ".pln" or null if none exists
	 */
	public static File getPipelineFile(int projId) {
		File[] pipelineFiles = getFilteredFiles(getConfigDirPath(projId), ".pln");
		if (pipelineFiles.length == 0)
			return null;
		return pipelineFiles[0];
	}

	/**
	 * @param projId The id of a local project
	 * @return The project's file extension-to-filter-configuration mapping file
	 */
	public static File getFilterMappingFile(int projId) {
		
		return new File(getConfigDirPath(projId) + File.separator + EXTENSIONS_MAPPING);
	}
	
	/**
	 * @param projId The id of a local project
	 * @param filename The name of the file to return
	 * @return The output file with the given file name that belongs to the project
	 */
	public static File getOutputFile(int projId, String filename) {
		
		return new File(getOutputDirPath(projId) + File.separator + filename);
	}
	
	/**
	 * @param projId The id of a local project
	 * @return All output files belonging to the project
	 */
	public static Collection<File> getOutputFiles(int projId) {
		
		return Arrays.asList(getFilteredFiles(getOutputDirPath(projId), null));
	}
	
	/**
	 * @param projId The id of a local project
	 * @return A (string) list of the file names of the project's output files
	 */
	public static ArrayList<String> getOutputFileNames(int projId) {
		
		Collection<File> files = getOutputFiles(projId);
		return getFileNames(files);
	}
	
	/**
	 * @param files Any collection of files
	 * @return The names of the given files (including their extension)
	 */
	public static ArrayList<String> getFileNames(Collection<File> files) {
		
		ArrayList<String> fileNames = new ArrayList<String>();
		for (File file : files) {
			fileNames.add(file.getName());
		}
		return fileNames;
	}

	/**
	 * @return A list of all project ids that are currently in use (in numerical order)
	 */
	public static ArrayList<Integer> getProjectIds() {

		ArrayList<Integer> projectIds = new ArrayList<Integer>();
		File[] subDirs = getSubdirectories(getWorkingDirectory());
		
		if(subDirs == null)
			// The Directory has not yet been created
			return projectIds;
		
		Collection<File> directories = Arrays.asList(subDirs);
		
		for (File dir : directories) {
			if (PLUGINS.equals(dir.getName()))
				continue;
			projectIds.add(Integer.parseInt(dir.getName()));
		}
		
		// Sort list, so new project folders are at the end.
		// The OS may put 10 in between 1 and 2.
		Collections.sort(projectIds);
		
		return projectIds;
	}
	
	/**
	 * @return The highest project id currently in use, increased by 1
	 */
	public static int determineNewProjectId() {
		
		ArrayList<Integer> takenProjectIds = getProjectIds();
		
		if (takenProjectIds.isEmpty())
			return 1;
		
		// List is in numerical order, so we can simply increase the last value by 1
		Integer highestId = takenProjectIds.get(takenProjectIds.size() - 1);
		return highestId + 1;
	}
	
	
	/**
	 * Filter files in a directory by their extension.
	 * 
	 * @param directory - directory where files are located
	 * @param extension - the extension used to filter files
	 * @return - list of files matching the suffix, all files in the directory if suffix is null
	 */
	private static File[] getFilteredFiles(String directory, String extension) {
		File dir = new File(directory);
		return dir.listFiles(new DefaultFilenameFilter(extension));
	}
	
	/**
	 * @param directory - directory where sub-directories are located
	 * @return - list of the directory's sub-directories or null if the directory does not exist
	 */
	private static File[] getSubdirectories(String directory) {
		File dir = new File(directory);
		File[] directories = dir.listFiles(DIRECTORY_FILTER);
		return directories;
	}
}
