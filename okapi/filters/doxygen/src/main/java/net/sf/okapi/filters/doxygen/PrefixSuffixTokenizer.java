package net.sf.okapi.filters.doxygen;

import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PrefixSuffixTokenizer extends RegexTokenizer implements Iterable<PrefixSuffixTokenizer.Token> {
	
	private Matcher currentSuffixMatcher;
	private boolean switcher = false;
	
	public PrefixSuffixTokenizer(Map<Pattern, Pattern> delimiters, String string)
	{
		s = string;
		
		if (string == null) return;
		
		for (Map.Entry<Pattern, Pattern> e : delimiters.entrySet()) {
			
			Matcher prefixMatcher = e.getKey().matcher(string);
			Matcher suffixMatcher = e.getValue().matcher(string);
			
			matchers.put(prefixMatcher, suffixMatcher);
		}
	}
	
	private Matcher getSuffixMatcher()
	{
		if (currentSuffixMatcher != null)
			return currentSuffixMatcher;
		
		Matcher newMatcher = null;
		
		Matcher p = getPrefixMatcher();
		
		if (p != null && !firstRun) {
			Matcher s = (Matcher) matchers.get(p);
			newMatcher = s.find(p.end()) ? s : null;
		}
		
		currentSuffixMatcher = newMatcher;
		
		return currentSuffixMatcher;
	}
	
	
	@Override
	public Iterator<Token> iterator() {
		return new Iterator<Token>()
		{
			
			@Override
			public boolean hasNext()
			{
				return s != null && s.length() > 0 && (
						getPrefixMatcher() != null 
						|| getSuffixMatcher() != null 
						|| firstRun
						);
			}
			
			@Override
			public Token next()
			{
				Matcher front;
				Matcher back;
				
				if (switcher) {
					front = getPrefixMatcher();
					back = getSuffixMatcher();
					currentPrefixMatcher = null;
				} else {
					front = getSuffixMatcher();
					back = getPrefixMatcher();
					currentSuffixMatcher = null;
				}
				switcher = !switcher;
				
				i = back != null ? back.end() : s.length();
				
				if (firstRun) firstRun = false;
				
				return new Token(front, back);
			}
			
			@Override
			public void remove()
			{
				throw new UnsupportedOperationException();
			}
		};
	}
	
	public static void main(String[] argv)
	{
		
		IdentityHashMap<Pattern, Pattern> set = new IdentityHashMap<Pattern, Pattern>();
		set.put(Pattern.compile("(?=1)"), Pattern.compile("(?=2)"));
		set.put(Pattern.compile("(?=2)"), Pattern.compile("(?=4)"));
		
		PrefixSuffixTokenizer t = new PrefixSuffixTokenizer(set, "1foo2bar3baz4");
		
		for (Token u : t) {
			System.out.println("Prefix: " + u.prefix());
			System.out.println("Token: " + u.toString());
			System.out.println("Suffix: " + u.suffix() + "\n");
		}
	}
	
}