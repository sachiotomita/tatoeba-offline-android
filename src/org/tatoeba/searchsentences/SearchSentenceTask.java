/**
 * 
 */
package org.tatoeba.searchsentences;

import java.util.Collection;
import java.util.Iterator;

import android.os.AsyncTask;
import android.widget.Button;
import android.widget.EditText;

/**
 * @author qdii
 * 
 *         This class runs a series of filters on the sentences and selects the
 *         ones that pass all the filters.
 */
public class SearchSentenceTask extends AsyncTask<Collection<Sentence>, Sentence, Void>
{
    private static final int NB_SENTENCES_TO_DISPLAY = 10;

    /**
     * Constructs the class
     * 
     * @param userFilters
     *            Sentences will be selected upon these filters
     * @param whereToPublish
     *            When a sentence is selected, it will be displayed here
     * @param inProgress
     *            Set to true when the search is going on
     */
    SearchSentenceTask( Collection<Filter> userFilters, EditText whereToPublish, Button searchButton )
    {
        allFilters = userFilters;
        publishWidget = whereToPublish;
        nbSentencesDisplayed = 0;
        searchInProgress = false;
        this.searchButton = searchButton;
        canceled = false;
    }

    @Override
    /**
     * Treats a list of sentence, and display them if they match 
     * a list of user-provided filters.
     * @param sentenceList The list of sentences.
     */
    protected Void doInBackground( Collection<Sentence>... sentenceList )
    {
        canceled = false;
        searchInProgress = true;

        nbSentencesDisplayed = 0;
        Collection<Sentence> sentences = sentenceList[0];
        Iterator<Sentence> iter = sentences.iterator();

        boolean keepSentence = false;

        while ( iter.hasNext() && !canceled )
        {
            Sentence candidate = iter.next();
            keepSentence = runFiltersOnSentence( candidate );

            // skip the sentences that are not interesting
            if ( !keepSentence )
                continue;

            // only display the 10 first sentences
            if ( nbSentencesDisplayed++ == NB_SENTENCES_TO_DISPLAY )
                break;

            publishProgress( candidate );
        }

        return null;
    }

    /**
     * Run a sentence against every filters. If it passes all the filters then
     * return true
     * 
     * @param candidate
     *            The sentence to check against the filters
     * @return true if all filters match the sentence
     */
    private boolean runFiltersOnSentence( Sentence candidate )
    {
        boolean complyAllFilters = true;

        for ( Filter filter : allFilters )
        {
            complyAllFilters = filter.select( candidate );

            if ( canceled )
                break;

            if ( !complyAllFilters )
                break;
        }

        return complyAllFilters;
    }

    @Override
    /**
     * @param candidateSentences An array containing ONE sentence that will be
     * appended to the edittext field. 
     */
    protected void onProgressUpdate( Sentence... candidateSentences )
    {
        Sentence sentence = candidateSentences[0];
        publishWidget.append( sentence.data() + "\n" );
    }

    @Override
    protected void onPostExecute( Void v )
    {
        // tell the Activity that parsing is finished!
        searchInProgress = false;
        searchButton.setText( "Search" );
    }

    public boolean inProgress()
    {
        return searchInProgress;
    }

    public void cancel()
    {
        canceled = true;
    }

    private Collection<Filter> allFilters;
    private EditText publishWidget;
    private int nbSentencesDisplayed;
    private volatile boolean searchInProgress;
    private Button searchButton;
    private boolean canceled;
}
