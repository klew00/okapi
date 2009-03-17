package net.sf.okapi.filters.openoffice;

import net.sf.okapi.common.resource.TextFragment;
import net.sf.okapi.common.resource.TextUnit;
import net.sf.okapi.common.skeleton.GenericSkeleton;

class Context {

	public String name;
	public boolean extract;
	public GenericSkeleton skel;
	public TextFragment tf;
	public TextUnit tu;

	public Context (String name,
		boolean extract)
	{
		this.name = name;
		this.extract = extract;
	}

	public void setVariables (TextFragment tf,
		GenericSkeleton skel,
		TextUnit tu)
	{
		this.tf = tf;
		this.skel = skel;
		this.tu = tu;
	}
		
}
