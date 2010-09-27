package net.sf.okapi.steps.xliffsplitter;

import net.sf.okapi.common.BaseParameters;

public class Parameters extends BaseParameters {

	/*
	 * Hidden option - only used for special internal projects. False by default.
	 */
	public boolean updateSDLTranslationStatus;

	public Parameters() {
		reset();
	}

	public void reset() {
		updateSDLTranslationStatus = false;
	}

	public void fromString(String data) {
		reset();
		buffer.fromString(data);
		updateSDLTranslationStatus = buffer.getBoolean("updateSDLTranslationStatus",
				updateSDLTranslationStatus);
	}

	public String toString() {
		buffer.reset();
		buffer.setBoolean("updateSDLTranslationStatus", updateSDLTranslationStatus);
		return buffer.toString();
	}

	public boolean isUpdateSDLTranslationStatus() {
		return updateSDLTranslationStatus;
	}

	public void setUpdateSDLTranslationStatus(boolean updateSDLTranslationStatus) {
		this.updateSDLTranslationStatus = updateSDLTranslationStatus;
	}
}
