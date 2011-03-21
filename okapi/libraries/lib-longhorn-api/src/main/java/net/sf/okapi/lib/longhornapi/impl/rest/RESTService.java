package net.sf.okapi.lib.longhornapi.impl.rest;

import java.net.URI;
import java.util.ArrayList;

import net.sf.okapi.lib.longhornapi.LonghornProject;
import net.sf.okapi.lib.longhornapi.LonghornService;

/**
 * Implementation of {@link LonghornService} for the communication with Longhorn's RESTful interface.
 */
public class RESTService implements LonghornService {
	private URI baseUri;
	
	protected RESTService() {
	}
	
	public RESTService(URI baseUri) {
		this.baseUri = baseUri;
		// Check if service is reachable
		getProjects();
	}
	
	public URI getBaseUri() {
		return baseUri;
	}

	@Override
	public LonghornProject createProject() {

		try {
			URI projUri = Util.createProject(baseUri);
			return new RESTProject(projUri);
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public ArrayList<LonghornProject> getProjects() {
		
		try {
			ArrayList<String> projIds = Util.getList(baseUri.toString() + "/projects");
			ArrayList<LonghornProject> projects = new ArrayList<LonghornProject>();
			
			for (String projId : projIds) {
				projects.add(new RESTProject(baseUri, projId));
			}
			
			return projects;
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
