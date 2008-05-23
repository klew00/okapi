package net.sf.okapi.common.resource;

public class CodeFragment implements IFragment {
	
	/**
	 * Original code string.
	 */
	public String       code;
	/**
	 * Type of code.
	 */
	public int          type;
	/**
	 * ID of the code in the content.
	 */
	public int          id;
	/**
	 * Extra format-specific data that is needed to re-build the original code.
	 */
	public String       extraData;

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
		String extraData)
	{
		this.type = type;
		this.id = id;
		this.code = code;
		this.extraData = extraData;
	}
		
	public String toString () {
		return code;
	}
	
	public boolean isText () {
		return false;
	}
}
