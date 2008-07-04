package net.sf.okapi.filters.regex;

import java.io.InputStream;

import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.filters.IInputFilter;
import net.sf.okapi.common.pipeline.IResourceBuilder;

public class InputFilter implements IInputFilter {

	private InputStream      input;
	private IResourceBuilder output;
	private RegexReader      reader;
	

	public InputFilter () {
		reader = new RegexReader();
	}
	
	public void close () {
		if ( reader != null ) {
			reader.close();
		}
	}

	public IParameters getParameters () {
		return reader.resource.getParameters();
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
		reader.resource.setName(name);
		reader.resource.setFilterSettings(filterSettings);
		reader.resource.setSourceEncoding(encoding);
		reader.resource.setSourceLanguage(sourceLanguage);
		reader.resource.setTargetLanguage(targetLanguage);
		//TODO: Get the real target/output encoding from parameters
		reader.resource.setTargetEncoding(encoding);
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
			reader.open(input);
			
			// Get started
			output.startResource(reader.resource);
			
			// Process
			int n;
			do {
				switch ( (n = reader.read()) ) {
				case RegexReader.RESULT_TRANSUNIT:
					output.startExtractionItem(reader.item);
					output.endExtractionItem(reader.item);
					break;
				case RegexReader.RESULT_SKELETON:
					output.skeletonContainer(reader.getSkeleton());
					break;
				case RegexReader.RESULT_STARTGROUP:
					output.startContainer(reader.groupResStack.peek());
					break;
				case RegexReader.RESULT_ENDGROUP:
					output.endContainer(reader.groupResStack.peek());
					break;
				}
			}
			while ( n > RegexReader.RESULT_ENDINPUT );
			
			output.endResource(reader.resource);
		}
		finally {
			close();
		}
	}

	public void setOutput (IResourceBuilder builder) {
		output = builder;
	}
}
