package net.sf.okapi.common.resource;

import java.util.ArrayList;
import java.util.List;

public class GroupResource extends CommonResource implements IGroupResource {

	private ArrayList<IBaseResource>   resList;
	
	
	public int getKind() {
		return KIND_GROUP;
	}

	public void add (IBaseResource resource) {
		if ( resList == null ) {
			resList = new ArrayList<IBaseResource>();
		}
		resList.add(resource);
	}

	public List<IBaseResource> getContent() {
		if ( resList == null ) {
			resList = new ArrayList<IBaseResource>();
		}
		return resList;
	}

	public void remove (IBaseResource resource) {
		if ( resList == null ) return;
		resList.remove(resource);
	}

	public String startToXML () {
		StringBuilder tmp = new StringBuilder ();
		tmp.append("<grp ");
		commonAttributesToXML(tmp);
		tmp.append(">");
		return tmp.toString();
	}
	
	public String endToXML () {
		return "</grp>";
	}
	
	public String toXML () {
		StringBuilder tmp = new StringBuilder ();
		tmp.append(startToXML());
		propertiesToXML(tmp);
		extensionsToXML(tmp);
		for ( IBaseResource res : resList ) {
			tmp.append(res.toXML());
		}
		tmp.append(endToXML());
		return tmp.toString();
	}
}
