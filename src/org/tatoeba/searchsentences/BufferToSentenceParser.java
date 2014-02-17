package org.tatoeba.searchsentences;

import java.util.List;
import java.util.Vector;

/**
 * This class takes a buffer and extracts sentences out of it.
 * 
 * @author qdii
 * 
 */
public class BufferToSentenceParser
{
    private static final int WAIT_TIME_MS = 1;

    BufferToSentenceParser( List<char[]> allBuffers, Vector<Sentence> allSentences )
    {
        assert allBuffers != null;
        assert allSentences != null;

        this.allBuffers = allBuffers;
        this.allSentences = allSentences;
    }

    /**
     * Wait for the buffer to be ready, then extracts it. Repeat.
     */
    public void waitForBufferThenParse()
    {
        try
        {
            for ( char[] buffer = null;; buffer = null )
            {
                synchronized ( allBuffers )
                {
                    allBuffers.wait();
                    if ( allBuffers.size() > 0 )
                        buffer = allBuffers.remove( 0 );
                }

                if ( buffer == null )
                    break;

                treatBuffer( buffer );
            }
        }
        catch ( InterruptedException e )
        {
            assert false;
        }
    }

    public void treatBuffer( char[] str )
    {
        assert (str != null);
        char languageName[] = new char[Language.NB_CHARS];

        int length = str.length;

        // skip garbage at start of buffer
        int startOffset = 0;
        while ( startOffset < length && str[startOffset] != '\n' )
            ++startOffset;

        ++startOffset; // skip '\n';

        for ( int i = startOffset; i < length; ++i )
        {
            int id = 0;
            do
            {
                id = (str[i] - '0') + 10 * id;
                i++;
            } while ( i < length && str[i] != '\t' );

            if ( i == length )
                return;

            i++; // skip '\t'

            // if the line is ill-formed, skip it
            if ( i == length )
                return;

            // handle special case where there is NO language name
            // the line looks like 12345 <TAB><TAB> blablalbla
            if ( str[i] == '\t' )
                return;

            int nbCharInLang = 0;
            do
            {
                languageName[nbCharInLang++] = str[i++];
            } while ( i < length && nbCharInLang < Language.NB_CHARS && str[i] != '\t' );

            languageName[nbCharInLang] = '\0';
            Language lang = new Language( languageName );

            i++;
            if ( i == length )
                return;

            int sentenceEnd = i;
            while ( sentenceEnd < length && str[sentenceEnd] != '\n' )
                ++sentenceEnd;

            // prune other languages
            // TODO : let the user decide which languages to keep
            if ( languageName[0] == 'f' && languageName[1] == 'r' && languageName[2] == 'a'
                    && languageName[4] == '\0' )
            {
                String data = new String( str, i, sentenceEnd - i );
                allSentences.add( new Sentence( id, lang, data ) );
            }
            i = sentenceEnd;
        }
    }

    private List<char[]> allBuffers;
    private Vector<Sentence> allSentences;
}
