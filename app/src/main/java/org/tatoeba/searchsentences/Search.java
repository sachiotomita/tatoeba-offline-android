package org.tatoeba.searchsentences;

import java.util.Collection;
import java.util.Vector;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class Search extends Activity
{

    @Override
    protected void onCreate( Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_search );
        allSentences = new Vector<Sentence>();

        uiHandler = new UIHandler();

        searchButton = (Button) findViewById( R.id.buttonSearch );
        editResults = (EditText) findViewById( R.id.editResults );
        editSearch = (EditText) findViewById( R.id.editSearch );

        // until the file is loaded, the button is disabled
        searchButton.setEnabled(true);
    }

    @Override
    public void onResume()
    {
        super.onResume();
    }

    @Override
    public void onPause()
    {
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu( Menu menu )
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate( R.menu.search, menu );
        return true;
    }

    // check if the button is pressed and launch the search

    @SuppressWarnings("unchecked")
    private void launchSearch()
    {
        if ( allSentences.size() == 0 )
            return;

        // launch a thread for the search, as the GUI needs to stay interactive.
        searchButton.setText( "Cancel" );
        searchTask = new SearchSentenceTask( getUserFilters(), editResults, searchButton );
        searchTask.execute( allSentences );
    }

    private Collection<Filter> getUserFilters()
    {
        Collection<Filter> ret = new Vector<Filter>();
        ret.add( new RegexFilter( editSearch.getText().toString() ) );

        return ret;
    }

    public void onSearchButtonClicked( View v )
    {
        if ( searchTask == null || !searchTask.inProgress() )
        {
            launchSearch();
        }
        else
        {
            searchTask.cancel();
        }
    }

    class UIHandler extends Handler
    {
        @Override
        public void handleMessage( Message msg )
        {
        	/*
            switch ( msg.what )
            {
            case DEACTIVATE_BUTTON:
                searchButton.setActivated( false );
                break;
            case ACTIVATE_BUTTON:
                searchButton.setActivated( true );
                break;
            }
            */
        }
    };

    public static final int ACTIVATE_BUTTON = 0;
    public static final int DEACTIVATE_BUTTON = 1;

    // let the user add filters
    Collection<Sentence> allSentences;
    Collection<Filter> allFilters;
    SearchSentenceTask searchTask;
    EditText editResults;
    EditText editSearch;
    Button searchButton;
    UIHandler uiHandler;
}
