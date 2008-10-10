package net.sf.okapi.apptest.filters;

import net.sf.okapi.common.filters.IParser.ParserTokenType;
import net.sf.okapi.common.pipeline2.PipelineEvent;
import net.sf.okapi.common.pipeline2.PipelineReturnValue;
import net.sf.okapi.common.pipeline2.PipelineEvent.PipelineEventType;

public class DummyInputFilter extends BaseInputFilter {
	
	private DummyParser parser;
	
	public DummyInputFilter () {
		parser = new DummyParser();
	}

	@Override
	public String getName () {
		return "DummyInputFilter";
	}
	
	@Override
	public PipelineReturnValue process () throws InterruptedException {
		if ( !parser.hasNext() ) {
			return PipelineReturnValue.SUCCEDED;
		}

		ParserTokenType tokType = parser.next();
		switch ( tokType ) {
		case ENDINPUT:
			producerQueue.put(new PipelineEvent(PipelineEventType.FINISHED, null, ++order));
			break;
		case TRANSUNIT:
			producerQueue.put(new PipelineEvent(PipelineEventType.TEXTUNIT, parser.getResource(), ++order));
			break;
		case SKELETON:
			producerQueue.put(new PipelineEvent(PipelineEventType.SKELETON, parser.getResource(), ++order));
			break;
		case STARTGROUP:
			producerQueue.put(new PipelineEvent(PipelineEventType.START_GROUP, parser.getResource(), ++order));
			break;
		case ENDGROUP:
			producerQueue.put(new PipelineEvent(PipelineEventType.END_GROUP, parser.getResource(), ++order));
			break;
		case NONE:
			break;
		}
		
		Thread.sleep(100); // Slow things down
		return PipelineReturnValue.RUNNING;
	}
	
	@Override
	public void initialize () throws InterruptedException {
		parser.open(null);
		super.initialize();
	}

	@Override
	public void finish () throws InterruptedException {
		parser.close();
		super.finish();
	}
}
