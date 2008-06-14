package net.sf.okapi.filters.properties;

import net.sf.okapi.common.Util;
import net.sf.okapi.common.resource.IExtension;

public class TestExtension implements IExtension {

	private String      data;
	
	
	public TestExtension (String data) {
		this.data = data;
	}
	
	public String toXML () {
		//TODO: is the x: prefix going to be an issue?
		StringBuilder tmp = new StringBuilder();
		tmp.append(String.format("<x:TestExtension xmlns:x=\"%s\">",
			Util.escapeToXML(getClass().getCanonicalName(), 3, false)));
		if ( data != null ) {
			tmp.append("<x:data>");
			tmp.append(Util.escapeToXML(data, 0, false));
			tmp.append("</x:data>");
		}
		tmp.append("</x:TestExtension>");
		return tmp.toString();
	}
}
