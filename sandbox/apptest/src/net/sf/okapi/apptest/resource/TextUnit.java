package net.sf.okapi.apptest.resource;

public class TextUnit extends BaseReferenceable {

	private TextFragment source;
	
	public TextUnit () {
		super();
		source = new TextFragment(this);
	}

	public TextUnit (String id,
		String sourceText)
	{
		super();
		create(id, sourceText, false);
	}

	public TextUnit (String id,
		String sourceText,
		boolean isReferent)
	{
		super();
		create(id, sourceText, isReferent);
	}

	private void create (String id,
		String sourceText,
		boolean isReferent)
	{
		this.id = id;
		this.isReferent = isReferent;
		source = new TextFragment(this);
		if ( sourceText != null ) source.append(sourceText);
	}

	@Override
	public String toString () {
		return source.toString();
	}
	
	public TextFragment getContent () {
		return source;
	}
	
	public void setContent (TextFragment content) {
		source = content;
		// We don't change the current annotations
	}

}
