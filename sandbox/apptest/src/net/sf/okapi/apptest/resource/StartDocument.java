package net.sf.okapi.apptest.resource;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

public class StartDocument extends BaseContainer {

	private LocaleProperties srcProp;
	private ArrayList<LocaleProperties> trgPropList;
	protected String language;
	protected String encoding;
	
	public String getLanguage () {
		return language;
	}
	
	public void setLanguage (String language) {
		this.language = language;
	}

	public String getEncoding () {
		return encoding;
	}
	
	public void setEncoding (String encoding) {
		this.encoding = encoding;
	}

	public LocaleProperties getSourceProperties () {
		if ( srcProp == null ) srcProp = new LocaleProperties();
		return srcProp;
	}
	
	public void setSourceProperties (LocaleProperties value) {
		if ( value == null ) throw new InvalidParameterException();
		srcProp = value;
	}

	public boolean hasTargetProperties () {
		if ( trgPropList == null ) return false;
		return (trgPropList.get(0) != null);
	}

	public LocaleProperties getTargetProperties () {
		if ( trgPropList == null ) trgPropList = new ArrayList<LocaleProperties>();
		if ( trgPropList.get(0) == null ) trgPropList.add(new LocaleProperties());
		return trgPropList.get(0);
	}
	
	public void setTarget (LocaleProperties value) {
		if ( value == null ) throw new InvalidParameterException();
		if ( trgPropList == null ) trgPropList = new ArrayList<LocaleProperties>();
		if ( trgPropList.size() == 0 ) trgPropList.add(value);
		else trgPropList.set(0, value);
	}

	public List<LocaleProperties> getTargetsProperties () {
		if ( trgPropList == null ) trgPropList = new ArrayList<LocaleProperties>();
		if ( trgPropList.get(0) == null ) trgPropList.add(new LocaleProperties());
		return trgPropList;
	}

}
