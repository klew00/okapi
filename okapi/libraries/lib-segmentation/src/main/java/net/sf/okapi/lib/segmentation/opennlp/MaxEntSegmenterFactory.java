package net.sf.okapi.lib.segmentation.opennlp;

import java.net.URI;

import net.sf.okapi.common.LocaleId;

final class MaxEntSegmenterFactory {
	MaxEntSegmenter create(URI modelPath, LocaleId locale) {
		return new MaxEntSegmenter(modelPath, locale);
	}
}
