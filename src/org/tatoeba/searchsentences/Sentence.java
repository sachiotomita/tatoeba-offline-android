package org.tatoeba.searchsentences;

/**
 * @author qdii This class represents a tatoeba sentence, which actually
 *         contains more information than a sentence: namely a language a
 *         creation date and other things.
 */
public class Sentence
{
    public Sentence( int id, Language lang, String data )
    {
        this.id = id;
        this.lang = lang;
        this.data = data;
    }

    public String data()
    {
        return data;
    }

    public Language language()
    {
        return lang;
    }

    public int getId()
    {
        return id;
    }

    private int id;
    private Language lang;
    private String data;
}
