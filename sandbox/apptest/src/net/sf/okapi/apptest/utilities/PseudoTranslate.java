package net.sf.okapi.apptest.utilities;

import net.sf.okapi.common.pipeline2.IPipelineEvent;
import net.sf.okapi.common.pipeline2.PipelineEvent.PipelineEventType;
import net.sf.okapi.common.resource.TextUnit;

public class PseudoTranslate implements IUtility2 {

	public void process (IPipelineEvent event) {
		if ( event.getEventType() != PipelineEventType.TEXTUNIT ) return;
		TextUnit tu1 = (TextUnit)event.getData();
		processTU(tu1);
		if ( tu1.hasChild() ) {
			for ( TextUnit tu : tu1.childTextUnitIterator() ) {
				processTU(tu);
			}
		}
	}
	
	private void processTU (TextUnit tu) {
		tu.getSourceContent().setCodedText(
			tu.getSourceContent().getCodedText().replace("e", "X"));
		
	}

}
