package net.sf.okapi.common.filters.myformat2;

import net.sf.okapi.common.resource.Resource;

public class MyFormat2Resource extends Resource {
	public StringBuilder buffer;
	
	public MyFormat2Resource() {
		buffer = new StringBuilder();
	}

	public StringBuilder getBuffer() {
		return buffer;
	}

	public void setBuffer(StringBuilder buffer) {
		this.buffer = buffer;
	}		
}
