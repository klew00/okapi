package net.sf.okapi.lib.segmentation.opennlp;

import java.io.InputStream;
import java.net.URI;

import net.sf.okapi.common.FileUtil;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.exceptions.OkapiIOException;
import net.sf.okapi.common.resource.ITextUnit;
import opennlp.tools.sentdetect.SentenceModel;

final class MaxEntSegmenter {
	private static final String DEFAULT_MODEL = "models/opennlp.model";
	private OkapiSentenceDetectorME sentenceDetector;	
	private SentenceModel model;
	private LocaleId locale;
		
	public MaxEntSegmenter(URI modelPath, LocaleId locale) {
		this.locale = locale;
		String mp = DEFAULT_MODEL;
		if (modelPath == null) {
			// find a locale based default model
			mp = FileUtil.getLocaleBasedFile("models/opennlp", "model", locale);
			if (mp == null) {
				mp = DEFAULT_MODEL;
			}
		} else {
			mp = modelPath.getPath();
		}
		
		try {			
			InputStream modelIn = MaxEntSegmenter.class.getResourceAsStream(mp);
			model = new SentenceModel(modelIn);
			this.sentenceDetector = new OkapiSentenceDetectorME(model, new OkapiSentenceDetectorFactory());
		} catch (Exception e) {
			throw new OkapiIOException("Error loading openNLP setence breaking model", e);
		}
	}
	
	public void segment(ITextUnit tu) {
		//this.sentenceDetector.sentDetect(text);
	}
}
