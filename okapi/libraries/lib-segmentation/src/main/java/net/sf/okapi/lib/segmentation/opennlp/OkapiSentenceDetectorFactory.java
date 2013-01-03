package net.sf.okapi.lib.segmentation.opennlp;

import java.util.Collections;
import java.util.Set;

import net.sf.okapi.common.LocaleId;
import opennlp.tools.sentdetect.DefaultEndOfSentenceScanner;
import opennlp.tools.sentdetect.DefaultSDContextGenerator;
import opennlp.tools.sentdetect.EndOfSentenceScanner;
import opennlp.tools.sentdetect.SDContextGenerator;
import opennlp.tools.sentdetect.lang.Factory;
import opennlp.tools.sentdetect.lang.th.SentenceContextGenerator;

public class OkapiSentenceDetectorFactory extends Factory {
	private LocaleId locale;

	public OkapiSentenceDetectorFactory(LocaleId locale) {
		this.locale = locale;
	}

	@Override
	public EndOfSentenceScanner createEndOfSentenceScanner(String languageCode) {
		if ("th".equals(languageCode)) {
			return new DefaultEndOfSentenceScanner(new char[] { ' ', '\n' });
		}

		return new DefaultEndOfSentenceScanner(new char[] { '.', '!', '?' });
	}

	@Override
	public SDContextGenerator createSentenceContextGenerator(String languageCode, Set<String> abbreviations) {
		if ("th".equals(languageCode)) {
			return new SentenceContextGenerator();
		}

		return new DefaultSDContextGenerator(abbreviations, new char[] { '.',	'!', '?' });
	}

	@Override
	public SDContextGenerator createSentenceContextGenerator(String languageCode) {
		return createSentenceContextGenerator(languageCode, Collections.<String> emptySet());
	}
}
