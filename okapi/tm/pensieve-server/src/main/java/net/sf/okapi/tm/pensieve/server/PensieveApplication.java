package net.sf.okapi.tm.pensieve.server;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Application;

public class PensieveApplication extends Application {
	
	@Override
	public Set<Class<?>> getClasses() {
		HashSet<Class<?>> classes = new HashSet<Class<?>>();
		classes.add(QueryResource.class);
		return classes;
		
	}

}
