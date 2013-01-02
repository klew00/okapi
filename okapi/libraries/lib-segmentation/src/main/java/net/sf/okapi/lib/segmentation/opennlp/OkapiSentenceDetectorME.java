package net.sf.okapi.lib.segmentation.opennlp;

import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.sentdetect.lang.Factory;

public class OkapiSentenceDetectorME extends SentenceDetectorME {

	public OkapiSentenceDetectorME(SentenceModel model, Factory factory) {
		super(model, factory);		
	}
}
