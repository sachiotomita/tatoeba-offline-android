package org.tatoeba.searchsentences;

/**
 * Selects sentences which are in a given language
 * 
 * @author qdii
 * 
 */
public class LanguageFilter implements Filter
{

    /**
     * Constructs a LanguageFilter
     * 
     * @param language
     *            the language to select sentences upon
     */
    public LanguageFilter( String language )
    {
        lang = new Language( language);
    }

    /**
     * Selects sentences which language are the appropriate
     * 
     * @param sentence
     *            The sentence to check
     */
    @Override
    public boolean select( Sentence sentence )
    {
        return lang == sentence.language();
    }

    Language lang;

}
