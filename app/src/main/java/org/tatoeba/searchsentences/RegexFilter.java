package org.tatoeba.searchsentences;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexFilter implements Filter {

	/**
	 * Constructs the regex filter according to the given regex
	 * @param expression The regex to select sentence upon
	 */
	RegexFilter( String expression )
	{
		pattern = Pattern.compile( expression );
	}
	
	/**
	 * @param sentence The sentence to check 
	 */
	@Override
	public boolean select(Sentence sentence)
	{
		Matcher matcher = pattern.matcher( sentence.data() );
		return matcher.matches();
	}
	
	Pattern pattern;
}
