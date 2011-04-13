package net.sf.okapi.steps.common.codesimplifier;

import net.sf.okapi.common.BaseParameters;
import net.sf.okapi.common.EditorFor;
import net.sf.okapi.common.ParametersDescription;
import net.sf.okapi.common.uidescription.EditorDescription;
import net.sf.okapi.common.uidescription.IEditorDescriptionProvider;

@EditorFor(Parameters.class)
public class Parameters extends BaseParameters implements IEditorDescriptionProvider {

	private static final String REMOVE_LEADING_TRAILING_CODES = "removeLeadingTrailingCodes";
	
	private boolean removeLeadingTrailingCodes;
	
	public Parameters() {
		reset();
	}
	
	@Override
	public void reset() {
		removeLeadingTrailingCodes = true;
	}

	@Override
	public void fromString(String data) {
		reset();
		buffer.fromString(data);
		removeLeadingTrailingCodes = buffer.getBoolean(REMOVE_LEADING_TRAILING_CODES, removeLeadingTrailingCodes);
	}

	@Override
	public String toString() {
		buffer.reset();
		buffer.setBoolean(REMOVE_LEADING_TRAILING_CODES, removeLeadingTrailingCodes);
		return buffer.toString();
	}

	public void setRemoveLeadingTrailingCodes(boolean removeLeadingTrailingCodes) {
		this.removeLeadingTrailingCodes = removeLeadingTrailingCodes;
	}

	public boolean getRemoveLeadingTrailingCodes() {
		return removeLeadingTrailingCodes;
	}

	@Override
	public ParametersDescription getParametersDescription() {
		ParametersDescription desc = new ParametersDescription(this);
		desc.add(REMOVE_LEADING_TRAILING_CODES, "Remove from the source and place in the skeleton leading and " +
				"trailing inline codes", null);
		return desc;
	}

	@Override
	public EditorDescription createEditorDescription(ParametersDescription paramsDesc) {
		EditorDescription desc = new EditorDescription("Remove leading and trailing codes", true, false);		
		desc.addCheckboxPart(paramsDesc.get(REMOVE_LEADING_TRAILING_CODES));
		return desc;
	}
}
