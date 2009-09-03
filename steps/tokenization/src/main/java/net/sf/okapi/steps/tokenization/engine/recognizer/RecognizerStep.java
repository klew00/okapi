package net.sf.okapi.steps.tokenization.engine.recognizer;

import net.sf.okapi.steps.tokenization.common.AbstractTokenizationStep;
import net.sf.okapi.steps.tokenization.common.TokenizationStepParameters;
import net.sf.okapi.steps.tokenization.engine.rbbi.Parameters;
import net.sf.okapi.steps.tokenization.tokens.Tokens;

public class RecognizerStep extends AbstractTokenizationStep {

	@Override
	public void tokenize(String text, Tokens tokens, String language,
			String... tokenTypes) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected TokenizationStepParameters createParameters() {
		
		return new Parameters();
	}

}
