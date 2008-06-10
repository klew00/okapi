package net.sf.okapi.common.resource;

public class CodeFragment implements IFragment {
	
	protected String       code;
	protected int          type;
	protected int          id;
	protected Object       data;


	public CodeFragment (int type,
		int id,
		String code)
	{
		this.type = type;
		this.id = id;
		this.code = code;
	}
		
	public CodeFragment (int type,
		int id,
		String code,
		Object data)
	{
		this.type = type;
		this.id = id;
		this.code = code;
		this.data = data;
	}
		
	public String toString () {
		return code;
	}
	
	public boolean isText () {
		return false;
	}
	
	public Object getData () {
		return data;
	}
	
	public int getID () {
		return id;
	}
	
	public int getType () {
		return type;
	}
}
