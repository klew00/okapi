package net.sf.okapi.apptest.resource;

import java.util.ArrayList;

import net.sf.okapi.apptest.filters.IEncoder;

public class BuilderData {

	public ArrayList<IReferenceable> references;
	public boolean outputTarget;
	public IEncoder encoder;
	
	public BuilderData () {
		references = new ArrayList<IReferenceable>();
	}
	
	public String encode (String text) {
		return encoder.encode(text);
	}
	
	public String encode (char value) {
		return encoder.encode(value);
	}
}
