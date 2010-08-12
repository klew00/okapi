package net.sf.okapi.lib.terminology.dummy;

import java.util.List;

import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.resource.TextFragment;
import net.sf.okapi.lib.terminology.GlossaryEntry;
import net.sf.okapi.lib.terminology.ITermAccess;
import net.sf.okapi.lib.terminology.TermHit;

public class SimpleTBConnector implements ITermAccess {

	private SimpleTB tb;
	
	@Override
	public IParameters getParameters () {
		// Nothing to do
		return null;
	}

	@Override
	public void setParameters (IParameters params) {
		// Nothing to do
	}

	@Override
	public void open () {
		tb = new SimpleTB(null);
	}

	@Override
	public void close() {
		// Nothing to do
	}

	@Override
	public List<TermHit> getExistingTerms (TextFragment fragment,
		LocaleId sourceLocId,
		LocaleId targetLocId)
	{
		return tb.getExistingTerms(fragment, sourceLocId, targetLocId);
	}
	
	public GlossaryEntry addEntry (LocaleId locId,
		String term)
	{
		return tb.addEntry(locId, term);
	}

//	public List<TermHit> getMissingTerms (TextFragment fragment,
//		List<TermHit> termsToCheck)
//	{
//
//		String text = fragment.getCodedText();
//		List<String> parts = Arrays.asList(text.split("\\s"));
//		List<TermHit> res = new ArrayList<TermHit>();
//	
//		for ( TermHit th : termsToCheck ) {
//			String term = th.targetTerm.getText();
//			if ( !parts.contains(term) ) {
//				res.add(th);
//			}
//		}
//		return res;
//	}

}
