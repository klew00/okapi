package net.sf.okapi.lib.longhornapi;

import java.util.ArrayList;

/**
 * Provides abstract methods for communicating with an Longhorn web-service instance.
 */
public interface LonghornService {

	/**
	 * @return All projects currently available/in use on the service
	 */
	ArrayList<LonghornProject> getProjects();
	
	/**
	 * Creates a new temporary project that can be used for processing files with the Okapi framework.
	 * 
	 * @return The created project
	 */
	LonghornProject createProject();
}
