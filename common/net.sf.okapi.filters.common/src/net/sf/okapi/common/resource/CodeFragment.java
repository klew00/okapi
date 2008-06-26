package net.sf.okapi.common.resource;

public class CodeFragment implements IFragment {
	
	protected String       code;
	protected int          type;
	protected int          id;


	public CodeFragment (int type,
		int id,
		String code)
	{
		this.type = type;
		this.id = id;
		this.code = code;
	}
			
	public String toString () {
		return code;
	}
	
	public boolean isText () {
		return false;
	}
	
	public int getID () {
		return id;
	}
	
	public int getType () {
		return type;
	}
	
}
