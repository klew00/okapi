package net.sf.okapi.lib.beans.v1;

import net.sf.okapi.lib.persistence.IPersistenceSession;
import net.sf.okapi.lib.persistence.PersistenceBean;
import net.sf.okapi.steps.repetitionanalysis.RepetitiveSegmentAnnotation;

public class RepetitiveSegmentAnnotationBean extends
		PersistenceBean<RepetitiveSegmentAnnotation> {

	private String tuid;
	private String headTuid;
	private float score;
	
	@Override
	protected RepetitiveSegmentAnnotation createObject(
			IPersistenceSession session) {
		return new RepetitiveSegmentAnnotation(tuid, headTuid, score);
	}

	@Override
	protected void setObject(RepetitiveSegmentAnnotation obj,
			IPersistenceSession session) {
	}

	@Override
	protected void fromObject(RepetitiveSegmentAnnotation obj,
			IPersistenceSession session) {
		tuid = obj.getTuid();
		headTuid = obj.getHeadTuid();
		score = obj.getScore();
	}

	public String getTuid() {
		return tuid;
	}

	public void setTuid(String tuid) {
		this.tuid = tuid;
	}

	public String getHeadTuid() {
		return headTuid;
	}

	public void setHeadTuid(String headTuid) {
		this.headTuid = headTuid;
	}

	public float getScore() {
		return score;
	}

	public void setScore(float score) {
		this.score = score;
	}

}
