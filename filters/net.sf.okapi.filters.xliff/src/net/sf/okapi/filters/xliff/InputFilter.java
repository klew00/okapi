package net.sf.okapi.filters.xliff;

import java.io.InputStream;

import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.filters.IInputFilter;
import net.sf.okapi.common.pipeline.IResourceBuilder;

public class InputFilter implements IInputFilter {

	private InputStream      input;
	private IResourceBuilder output;
	private XLIFFReader      reader;
	

	public InputFilter () {
		reader = new XLIFFReader();
	}
	
	public void close ()
		throws Exception 
	{
	}

	public IParameters getParameters () {
		return null;
	}

	public void initialize (InputStream input,
		String name,
		String encoding,
		String sourceLanguage,
		String targetLanguage)
		throws Exception
	{
		close();
		this.input = input;
		reader.resource.setName(name);
		// Not used: encoding;
		// Not used: sourceLanguage
		// Not used: targetLanguage
	}

	public void setParameters (IParameters params) {
	}

	public boolean supports (int feature) {
		switch ( feature ) {
		case FEATURE_TEXTBASED:
		case FEATURE_BILINGUAL:
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
				//TODO: groups
				switch ( (n = reader.readItem()) ) {
				case XLIFFReader.RESULT_STARTTRANSUNIT:
					output.startExtractionItem(reader.sourceItem, reader.targetItem);
					break;
				case XLIFFReader.RESULT_ENDTRANSUNIT:
					output.endExtractionItem(reader.sourceItem, reader.targetItem);
					break;
				case XLIFFReader.RESULT_STARTFILE:
					output.startContainer(reader.fileRes);
					break;
				case XLIFFReader.RESULT_ENDFILE:
					output.endContainer(reader.fileRes);
					break;
				}
			}
			while ( n > XLIFFReader.RESULT_ENDINPUT );
			
			output.endResource(reader.resource);
		}
		catch ( Exception e ) {
			System.err.println(e.getLocalizedMessage());
		}
		finally {
			try {
				close();
			}
			catch ( Exception e ) {
				System.err.println(e.getLocalizedMessage());
			}
		}
	}

	public void setOutput (IResourceBuilder builder) {
		this.output = builder;
	}

}
