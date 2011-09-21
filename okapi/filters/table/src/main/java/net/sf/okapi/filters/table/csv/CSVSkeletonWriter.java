package net.sf.okapi.filters.table.csv;

import net.sf.okapi.common.resource.ITextUnit;
import net.sf.okapi.common.resource.TextContainer;
import net.sf.okapi.common.resource.TextFragment;
import net.sf.okapi.common.resource.TextPart;
import net.sf.okapi.common.skeleton.GenericSkeletonWriter;

public class CSVSkeletonWriter extends GenericSkeletonWriter {

	@Override
	public String processTextUnit(ITextUnit tu) {
		if (tu.hasProperty(CommaSeparatedValuesFilter.PROP_QUALIFIED) && 
			"yes".equals(tu.getProperty(CommaSeparatedValuesFilter.PROP_QUALIFIED).getValue())) {
				return super.processTextUnit(tu);
		}

		TextContainer tc;
		boolean isTarget = tu.hasTarget(outputLoc);
		if (isTarget) {
			tc = tu.getTarget(outputLoc);
		}
		else {
			tc = tu.getSource();
		}
		
		if (tc == null)
			return super.processTextUnit(tu);
		
		TextFragment tf = tc.getUnSegmentedContentCopy();
		String text = tf.toText(); // Just to detect "bad" characters
		if (text.contains(",") || text.contains("\n")) {
			if (tc.hasBeenSegmented()) {
				tc.insert(0, new TextPart("\""));
				tc.append(new TextPart("\""));
			}
			else {
				tf.insert(0, new TextFragment("\""));
				tf.append("\"");
				tc.setContent(tf);
			}
		}		
		return super.processTextUnit(tu);
	}

}
