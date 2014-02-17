package org.tatoeba.searchsentences;

import java.io.FileNotFoundException;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.widget.ProgressBar;
import android.widget.TextView;

public class CutterActivity extends Activity
{
    static private String SENTENCES_FILENAME = "/storage/extSdCard/Download/sentences.csv";

    private Activity cutter;

    @Override
    protected void onCreate( Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_cutter );

        cutter = this;

        progressBar = (ProgressBar) findViewById( R.id.cutterProgress );
        assert progressBar != null;

        editText = (TextView) findViewById( R.id.cutterEdit );
        assert editText != null;

        editText.setKeyListener( null ); // disable user selection

        dbAdapter = new DBAdapter( this.getApplicationContext() );

        // check if the database is present and contains data, otherwise
        // populate it
        dbAdapter.open();

        // if ( !dbAdapter.isCreatedAndPopulated() )
        dbAdapter.destroyTable();
        dbAdapter.createTable();
        try
        {
            loadSentencesInBackground( dbAdapter );
        }
        catch ( FileNotFoundException e )
        {
            assert false;
        }
    }

    @Override
    public boolean onCreateOptionsMenu( Menu menu )
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate( R.menu.cutter, menu );
        return true;
    }

    private void loadSentencesInBackground( DBAdapter dbAdapter ) throws FileNotFoundException
    {
        assert dbAdapter != null;
        dbAdapter.open();

        cutterTask = new PrivateCutterTask( SENTENCES_FILENAME, progressBar, dbAdapter );
        cutterTask.execute( new Void[1] );
    }

    private class PrivateCutterTask extends CutterTask {
    	PrivateCutterTask(String path, ProgressBar progressBar,
				DBAdapter storage) throws FileNotFoundException {
			super(path, progressBar, storage);
			// TODO Auto-generated constructor stub
		}

		@Override
    	protected void onPostExecute(Boolean result) {
    		// TODO Auto-generated method stub
    		super.onPostExecute(result);
    		Intent searchIntent = new Intent(cutter, Search.class);
    		cutter.startActivity(searchIntent);
    	}
    }

    private PrivateCutterTask cutterTask;
    private ProgressBar progressBar;
    private TextView editText;
    private DBAdapter dbAdapter;
}
