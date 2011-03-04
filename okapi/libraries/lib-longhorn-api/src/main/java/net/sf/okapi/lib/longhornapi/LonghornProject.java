package net.sf.okapi.lib.longhornapi;

import java.io.File;
import java.util.ArrayList;

/**
 * Provides abstract methods for interacting with one of the temporary projects
 * available on a Longhorn web-service instance.
 */
public interface LonghornProject {

	/**
	 * Pushes a batch configuration file that was exported from Rainbow to the project.
	 * It will be used to process the input files when {@link #executePipeline()} is called.
	 * 
	 * @param bconf A batch configuration file that was exported from Rainbow
	 */
	void addBatchConfiguration(File bconf);
	
	/**
	 * Adds an input file to the project. It will be processed when {@link #executePipeline()} is called.
	 * 
	 * @param inputFile The file to be pushed to the service
	 * @param relativePath The relative path of the file that shall be used to store it in the project
	 */
	void addInputFile(File inputFile, String relativePath);
	
	/**
	 * Returns all input files that have been added to this project so far.
	 * 
	 * @return A list of all input files in this project
	 */
	ArrayList<LonghornFile> getInputFiles();
	
	/**
	 * Executes the pipeline from the previously added batch configuration
	 * on all input files in this project.
	 */
	void executePipeline();
	
	/**
	 * Returns all output files that were generated when the project's pipeline was executed.
	 * 
	 * @return A list of all output files in this project
	 */
	ArrayList<LonghornFile> getOutputFiles();
	
	/**
	 * Deletes this project from the web-service.
	 */
	void delete();
}
