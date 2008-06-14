package net.sf.okapi.common.resource;

public abstract class DocumentResource extends GroupResource implements IDocumentResource {
	
	private String      srcEnc;
	private String      srcLang;
	private String      trgEnc;
	private String      trgLang;
	private String      root;
	private String      trgName;
	private String      filterSettings;
	

	public int getKind () {
		return KIND_DOCUMENT;
	}

	public String getSourceEncoding () {
		return srcEnc;
	}

	public String getSourceLanguage () {
		return srcLang;
	}

	public String getSourceRoot () {
		return root;
	}

	public String getTargetEncoding () {
		return trgEnc;
	}

	public String getTargetLanguage () {
		return trgLang;
	}

	public String getTargetName () {
		return trgName;
	}

	public void setSourceEncoding (String encoding) {
		srcEnc = encoding;
	}

	public void setSourceLanguage (String languageCode) {
		srcLang = languageCode;
	}

	public void setSourceRoot (String rootFolder) {
		root = rootFolder;
	}

	public void setTargetEncoding (String encoding) {
		trgEnc = encoding;
	}

	public void setTargetLanguage (String languageCode) {
		trgLang = languageCode;
	}

	public void setTargetName (String name) {
		trgName = name;
	}

	public String startToXML () {
		StringBuilder tmp = new StringBuilder();
		tmp.append("<document version=\"1\"");
		commonAttributesToXML(tmp);
		tmp.append(" srcEncoding=\"");
		tmp.append(getSourceEncoding());
		tmp.append("\" trgEncoding=\"");
		tmp.append(getTargetEncoding());
		tmp.append("\" srcLanguage=\"");
		tmp.append(getSourceLanguage());
		tmp.append("\" trgLanguage=\"");
		tmp.append(getTargetLanguage());
		tmp.append("\">");
		return tmp.toString();
	}
	
	public String endToXML () {
		StringBuilder tmp = new StringBuilder();
		propertiesToXML(tmp);
		extensionsToXML(tmp);
		tmp.append("</document>");
		return tmp.toString();
	}
	
	public String getFilterSettings () {
		return filterSettings;
	}

	public void setFilterSettings(String filterSettings) {
		this.filterSettings = filterSettings;
	}
}
