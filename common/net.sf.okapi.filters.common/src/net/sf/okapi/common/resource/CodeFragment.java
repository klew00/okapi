package net.sf.okapi.common.resource;

import net.sf.okapi.common.Util;

public class CodeFragment implements IFragment {
	
	protected String       code;
	protected String       anchor;
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

	@Override
	public String toString () {
		return code;
	}

	@Override
	public CodeFragment clone () {
		return new CodeFragment(type, id, code);
	}
	
	public String toXML (IPart parent) {
		switch ( type ) {
		case IContainer.CODE_ISOLATED:
			return String.format("<ic id=\"%d\" c=\"%s\"/>",
				id, Util.escapeToXML(toString(), 3, false));
		case IContainer.CODE_OPENING:
			return String.format("<pc id=\"%d\" oc=\"%s\" cc=\"%s\">",
				id, Util.escapeToXML(toString(), 3, false),
				Util.escapeToXML(parent.getCodeForID(id, Container.CODE_CLOSING), 3, false));
		case IContainer.CODE_CLOSING:
			return "</pc>";
		}
		return null;
	}
	
	public boolean isText () {
		return false;
	}
	
	public int getID () {
		return id;
	}
	
	public String getAnchor () {
		return anchor;
	}

	public int getType () {
		return type;
	}
	
	public CodeFragment append (String codeToAppend) {
		if ( code == null ) code = codeToAppend;
		else code = code + codeToAppend;
		return this;
	}
}
