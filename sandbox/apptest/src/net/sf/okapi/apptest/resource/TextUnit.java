package net.sf.okapi.apptest.resource;

import net.sf.okapi.apptest.annotation.TargetsAnnotation;

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

	/**
	 * Indicates if a TextUnit has a target for a given language.
	 * @param language The language to check for.
	 * @return True if a target object exists (even empty), false if a target
	 * object does not exist.
	 */
	public boolean hasTarget (String language) {
		TargetsAnnotation ta = annotations.get(TargetsAnnotation.class);
		if ( ta == null ) return false;
		return (ta.get(language) != null);
	}
	
	/**
	 * Gets the target TextUnit of a given TextUnit, for a given language.
	 * This method <b>assumes the target exists</b> and does no check at all.
	 * @return The target TextUnit.
	 */
	public TextUnit getTarget (String language) {
		return ((TargetsAnnotation)annotations.get(
			TargetsAnnotation.class)).get(language);
	}

	/**
	 * Gets the target TextUnit of a given source TextUnit, for a given language.
	 * If the target does not exists a null is returned, except if the option to create
	 * the target is set.
	 * @param language The language to look for. 
	 * @param creationOptions The creation option:
	 * <ul><li>DO_NOTHING: Returns null if there is no target.</li>
	 * <li>CREATE_EMPTY: Creates a target if it does not exist, and leave it empty.</li>
	 * <li>CREATE_COPY: Creates a target if it does not exist, and copy the text and
	 * in-line codes of the source into it.</li></ul> 
	 * @return The target TextUnit, or null if none if available for the given
	 * language.
	 */
	public TextUnit getTarget (String language,
		int creationOptions)
	{
		TargetsAnnotation ta = annotations.get(TargetsAnnotation.class);
		if ( ta == null ) {
			if ( creationOptions > DO_NOTHING ) {
				ta = new TargetsAnnotation();
				annotations.set(ta);
			}
			else return null;
		}
		TextUnit trgTu = ta.get(language);
		if ( trgTu == null ) {
			if ( creationOptions > DO_NOTHING ) {
				trgTu = new TextUnit(id, "");
				if ( creationOptions > CREATE_EMPTY ) {
					TextFragment tf = getContent().clone();
					trgTu.setContent(tf);
				}
				ta.set(language, trgTu);
			}
		}
		return trgTu;
	}

	public void setTarget (String language,
		TextUnit tu)
	{
		TargetsAnnotation ta = annotations.get(TargetsAnnotation.class);
		if ( ta == null ) {
			ta = new TargetsAnnotation();
			annotations.set(ta);
		}
		ta.set(language, tu);
	}

	@Override
	public boolean hasTargetProperty (String language,
		String name)
	{
		// The target property use the target TU, not the source TU's annotations
		if ( !hasTarget(language) ) return false;
		return (getTarget(language).getProperty(name) != null);
	}

	@Override
	public Property getTargetProperty (String language,
		String name)
	{
		// The target property use the target TU, not the source TU's annotations
		// Assumes the target exists
		return getTarget(language).getProperty(name);
	}

	@Override
	public Property getTargetProperty (String language,
		String name,
		int creationOptions)
	{
		// The target property use the target TU, not the source TU's annotations
		return getTarget(language, creationOptions).getProperty(name);
	}

	@Override
	public void setTargetProperty (String language,
		Property property)
	{
		// The target property use the target TU, not the source TU's annotations
		getTarget(language, CREATE_COPY).setProperty(property);
	}

}
