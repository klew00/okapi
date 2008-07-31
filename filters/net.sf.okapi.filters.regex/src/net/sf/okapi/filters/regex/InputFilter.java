package net.sf.okapi.filters.regex;

import java.io.InputStream;

import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.filters.IInputFilter;
import net.sf.okapi.common.filters.IParser.ParserTokenType;
import net.sf.okapi.common.pipeline.IResourceBuilder;
import net.sf.okapi.common.resource.IExtractionItem;
import net.sf.okapi.common.resource.IGroupResource;
import net.sf.okapi.common.resource.ISkeletonResource;

public class InputFilter implements IInputFilter {

	private InputStream      input;
	private IResourceBuilder output;
	private Parser           parser;
	

	public InputFilter () {
		parser = new Parser();
	}
	
	public void close () {
		if ( parser != null ) {
			parser.close();
		}
	}

	public IParameters getParameters () {
		return parser.resource.getParameters();
	}

	public void initialize (InputStream input,
		String name,
		String filterSettings,
		String encoding,
		String sourceLanguage,
		String targetLanguage)
	{
		close();
		this.input = input;
		parser.resource.setName(name);
		parser.resource.setFilterSettings(filterSettings);
		parser.resource.setSourceEncoding(encoding);
		parser.resource.setSourceLanguage(sourceLanguage);
		parser.resource.setTargetLanguage(targetLanguage);
		//TODO: Get the real target/output encoding from parameters
		parser.resource.setTargetEncoding(encoding);
	}

	public boolean supports (int feature) {
		switch ( feature ) {
		case FEATURE_TEXTBASED:
			return true;
		default:
			return false;
		}
	}

	public void process () {
		try {
			close();
			parser.open(input);
			
			// Get started
			output.startResource(parser.resource);
			
			// Process
			ParserTokenType tok;
			do {
				switch ( (tok = parser.parseNext()) ) {
				case TRANSUNIT:
					output.startExtractionItem((IExtractionItem)parser.getResource());
					output.endExtractionItem((IExtractionItem)parser.getResource());
					break;
				case SKELETON:
					output.skeletonContainer((ISkeletonResource)parser.getResource());
					break;
				case STARTGROUP:
					output.startContainer((IGroupResource)parser.getResource());
					break;
				case ENDGROUP:
					output.endContainer((IGroupResource)parser.getResource());
					break;
				}
			}
			while ( tok != ParserTokenType.ENDINPUT );

			output.endResource(parser.resource);
		}
		finally {
			close();
		}
	}

	public void setOutput (IResourceBuilder builder) {
		output = builder;
	}
}
