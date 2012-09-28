package net.sf.okapi.steps.enrycher;

import net.sf.okapi.common.LocaleId;

import org.junit.Test;
import static org.junit.Assert.*;

public class EnrycherClientTest {

	@Test
	public void parametersTest () {
		EnrycherClient ec = new EnrycherClient();
		assertNotNull(ec.getParameters());
	}

	// Comment out the test annotation before committing
//	@Test
	public void manual () {
		EnrycherClient ec = new EnrycherClient();
		ec.setLocale(LocaleId.ENGLISH);
		
		// Detection (use the s element to avoid the change in the p element)
		String res = ec.processContent("<p id=\"1\"><s>CMS</s></p>");
		assertEquals("<p id=\"1\"><s translate=\"no\" itsx-lexicalizes=\"dbr:CMS\">CMS<span hidden=\"hidden\" itsx-alternative-label=\"CMS\"></span></s></p>", res);
		
		// Nothing to annotate
		res = ec.processContent("<p id=\"1\"><s>something</s></p>");
		assertEquals("<p id=\"1\"><s>something</s></p>", res);
	}
}
