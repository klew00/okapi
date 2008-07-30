package net.sf.okapi.common.resource3;

import java.util.List;

import net.sf.okapi.common.resource3.string.StringRootContainer;
import net.sf.okapi.common.resource3.tree.TreeRootContainer;

public interface IRootContainer {
	
	public String getCodedText ();
	
	public List<Code> getCodes ();

	public void setCodedText (String codedText);

	public void setCodedText (String codedText, List<Code> codes);

	public TreeRootContainer getTreeView ();
	
	public StringRootContainer getStringView ();
}
