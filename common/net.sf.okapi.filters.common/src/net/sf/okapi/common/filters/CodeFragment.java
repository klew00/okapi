package net.sf.okapi.common.filters;

public class CodeFragment implements IFragment {
	
	public String       data;
	public int          type;
	public int          id;

	public CodeFragment (int codeType,
		int codeId,
		String codeData)
	{
		type = codeType;
		id = codeId;
		data = codeData;
	}
	
	public String toString () {
		return data;
	}
	
	public boolean isText () {
		return false;
	}
}
