package net.sf.okapi.apptest.resource;

import java.util.ArrayList;

public class BuilderData {

	public ArrayList<IReferenceable> references;
	public boolean outputTarget;
	
	public BuilderData () {
		references = new ArrayList<IReferenceable>();
	}
}
