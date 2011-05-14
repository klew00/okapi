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

package net.sf.okapi.filters.transifex;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.sf.okapi.common.Base64;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.exceptions.OkapiIOException;
import net.sf.okapi.lib.transifex.ResourceInfo;
import net.sf.okapi.lib.transifex.TransifexClient;

public class Project {

	public static final String HOST = "host";
	public static final String USER = "user";
	public static final String PASSWORD = "password";
	public static final String PROJECTID = "projectId";
	public static final String SOURCELOCALE = "sourceLocale";
	public static final String TARGETLOCALE = "targetLocale";
	public static final String PROTECTAPPROVED = "protectApproved";
	
	public static final String PROJECT_EXTENSION = ".txp";

	private static final String PO_TYPE = "PO";

	private String path;
	private String host;
	private String user;
	private String password;
	private String projectId;
	private LocaleId sourceLocale;
	private LocaleId targetLocale;
	private List<ResourceInfo> resources;
	private TransifexClient cli;
	private boolean protectApproved;
	
	public Project () {
		reset(LocaleId.ENGLISH, LocaleId.FRENCH);
	}
	
	private void reset (LocaleId srcLoc,
		LocaleId trgLoc)
	{
		path = null;
		resources = new ArrayList<ResourceInfo>();
		setHost("http://www.transifex.net");
		setUser("");
		setPassword("");
		setProjectId("");
		cli = null;
		sourceLocale = srcLoc;
		targetLocale = trgLoc;
		protectApproved = false;
	}
	
	public String getHost () {
		return host;
	}

	public void setHost (String host) {
		if ( host.endsWith("\\") ) {
			host = host.substring(0, host.length()-1) + "/";
		}
		else if ( !host.endsWith("/") ) {
			host += "/";
		}
		this.host = host;
	}

	public LocaleId getSourceLocale () {
		return sourceLocale;
	}
	
	public void setSourceLocale (LocaleId srcLoc) {
		this.sourceLocale = srcLoc;
	}
	
	public LocaleId getTargetLocale () {
		return targetLocale;
	}
	
	public void setTargetLocale (LocaleId trgLoc) {
		this.targetLocale = trgLoc;
	}
	
	public String getProjectId () {
		return projectId;
	}

	public void setProjectId (String projectId) {
		this.projectId = projectId;
		cli = null;
	}

	public String getUser () {
		return user;
	}

	public void setUser (String user) {
		this.user = user;
		cli = null;
	}

	public String getPassword () {
		return password;
	}

	public void setPassword (String password) {
		this.password = Base64.decodePassword(password);
		cli = null;
	}
	
	public boolean getProtectApproved () {
		return protectApproved;
	}

	public void setProtectApproved (boolean protectApproved) {
		this.protectApproved = protectApproved;
	}

	public List<ResourceInfo> getResources () {
		return resources;
	}
	
	public void setPath (String path) {
		this.path = path;
	}
	
	public void save () {
		PrintWriter pw = null;
		try {
			pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(path), "UTF-8"));
			pw.println(HOST + "=" + host);
			pw.println(USER + "=" + user);
			pw.println(PASSWORD + "=" + Base64.encodePassword(password));
			pw.println(PROJECTID + "=" + projectId);
			pw.println(SOURCELOCALE + "=" + sourceLocale.toString());
			pw.println(TARGETLOCALE + "=" + targetLocale.toString());
			pw.println(PROTECTAPPROVED+ "=" + (protectApproved ? "yes" : "no"));
			// Resources
			for ( ResourceInfo info : resources ) {
				pw.println(info.getId() + "\t" + (info.getSelected() ? "yes" : "no" ));
			}
		}
		catch ( FileNotFoundException e ) {
			throw new OkapiIOException("Error saving project file.\n"+e.getMessage(), e);
		}
		catch ( UnsupportedEncodingException e ) {
			throw new OkapiIOException("Error saving project file.\n"+e.getMessage(), e);
		}
		finally {
			if ( pw != null ) {
				pw.close();
			}
		}
	}
	
	public void read (BufferedReader br,
		LocaleId srcLoc,
		LocaleId trgLoc)
		throws IOException
	{
		reset(srcLoc, trgLoc);
		String line = br.readLine();
		while ( line != null ) {
			line = line.trim();
			if ( !line.isEmpty() && !line.startsWith("#") ) {
				int n = line.indexOf('=');
				if ( n > 0 ) {
					String value = line.substring(n+1).trim();
					if ( line.startsWith(HOST) ) {
						setHost(value);
					}
					else if ( line.startsWith(USER) ) {
						setUser(value);
					}
					else if ( line.startsWith(PASSWORD) ) {
						setPassword(value);
					}
					else if ( line.startsWith(PROJECTID) ) {
						setProjectId(value);
					}
					// Source and target from the file are used only as fall-back
					else if ( line.startsWith(SOURCELOCALE) ) {
						if ( sourceLocale == null ) {
							setSourceLocale(LocaleId.fromString(value));
						}
					}
					else if ( line.startsWith(TARGETLOCALE) ) {
						if ( targetLocale == null ) {
							setTargetLocale(LocaleId.fromString(value));
						}
					}
					else if ( line.startsWith(PROTECTAPPROVED) ) {
						setProtectApproved(value.equals("yes"));
					}
				}
				else {
					// Else: add the resource to the list
					n = line.indexOf('\t');
					boolean selected = true;
					if ( n > 0 ) { // The selected flag is present
						String tmp = line.substring(n+1).trim();
						selected = tmp.equals("yes");
						line = line.substring(0, n);
					}
					ResourceInfo info = new ResourceInfo(line, "", PO_TYPE, selected);
					resources.add(info);
				}
			}
			// Next line
			line = br.readLine();
		}
	}

	@SuppressWarnings("unchecked")
	public void refreshResources (boolean onlyExistingResources) {
		// Reset the connection info if needed
		if ( cli == null ) {
			cli = new TransifexClient(getHost());
			cli.setCredentials(getUser(), getPassword());
			cli.setProject(getProjectId());
		}
		
		// Get the list of resources in the given project
		Object[] res = cli.getResourceList(sourceLocale);
		if ( res[0] == null ) {
			throw new OkapiIOException((String)res[1]);
		}
		Map<String, ResourceInfo> map = (Map<String, ResourceInfo>)res[2];
		if ( map.isEmpty() ) {
			resources.clear();
//			lastMessage = String.format("The project '%s' has no resources for '%s'.",
//				projectId, sourceLocale.toString());
			return;
		}

		// Make a temporary copy of the existing list
		List<ResourceInfo> oldList = new ArrayList<ResourceInfo>();
		oldList.addAll(resources);
		resources.clear();
		
		// Fill the new list
		for ( String resId : map.keySet() ) {
			ResourceInfo info = map.get(resId);
			if ( PO_TYPE.equals(info.getI18nType()) ) {
				// Try to preserve the existing selection
				boolean found = false;
				for ( int i=0; i<oldList.size(); i++ ) {
					if ( oldList.get(i).getId().equals(resId) ) {
						info.setSelected(oldList.get(i).getSelected());
						found = true;
						break;
					}
				}
				// Update the list as needed
				if ( found || !onlyExistingResources ) {
					resources.add(info);
				}
			}
		}
	}

}
