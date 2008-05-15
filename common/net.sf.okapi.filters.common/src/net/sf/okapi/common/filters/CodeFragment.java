package net.sf.okapi.common.filters;

public class CodeFragment implements IFragment {
	
	public String       data;
	public String       label;
	public int          type;
	public int          id;

	public CodeFragment (int codeType,
		String codeLabel,
		String codeData)
	{
		type = codeType;
		label = codeLabel;
		data = codeData;
	}
	
	public String toString () {
		return data;
	}
	
	public boolean isText () {
		return false;
	}
}
