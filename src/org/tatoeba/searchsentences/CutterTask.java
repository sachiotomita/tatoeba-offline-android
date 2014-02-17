/**
 * 
 */
package org.tatoeba.searchsentences;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Vector;
import java.util.concurrent.ConcurrentLinkedQueue;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.ProgressBar;

/**
 * Cutter takes a sentences.csv file and generates optimized language files. A
 * language file is of type fra.sentences.csv and contains information to ease
 * parsing.
 * 
 * @author qdii
 * 
 */
public class CutterTask extends AsyncTask<Void, Integer, Boolean>
{
    /**
     * To avoid updating the progress bar to often, we publish progress every X
     * sentences.
     */
    private static final int UPDATE_EVERY_X_SENTENCES = 5000;

    /**
     * Constructs a CutterTask
     * 
     * @param path
     *            The path to the sentences.csv file to cut
     * 
     * @param storage
     *            The database where to store the generated file
     * 
     * @param progressBar
     *            Where to show progress
     */
    CutterTask( String path, ProgressBar progressBar, DBAdapter storage )
            throws FileNotFoundException
    {
        assert path != null;
        assert !path.isEmpty();
        assert progressBar != null;

        File sentencesCsvFile = new File( path );
        fileSize = sentencesCsvFile.length();
        progress = 0;

        reader = new BufferedReader( new FileReader( sentencesCsvFile ) );

        this.progressBar = progressBar;
        progressBar.setProgress( 0 );

        this.database = storage;
    }

    /**
     * Cuts sentences.csv in the background.
     */
    @Override
    protected Boolean doInBackground( Void... params )
    {
        assert progress == 0;
        assert fileSize > 0;

        workMaterial = new ConcurrentLinkedQueue<Vector<Sentence>>();
        new Thread( new SqlThread(), "SQL Thread" ).start();

        finished = false;

        try
        {
            int lineNumber = 0;
            // used to issue many insertions at the same time in the
            // SQL database
            Vector<Sentence> temporaryContainer = new Vector<Sentence>( UPDATE_EVERY_X_SENTENCES );

            for ( String line = null; (line = reader.readLine()) != null; ++lineNumber )
            {
                assert line != null;

                Sentence sentenceToInsert = cutLine( line );
                if ( sentenceToInsert == null )
                    continue;

                temporaryContainer.add( sentenceToInsert );
                synchronized (temporaryContainer) {
                    temporaryContainer.add( sentenceToInsert );
				 }

                progress += line.length();

                if ( lineNumber % UPDATE_EVERY_X_SENTENCES == 0 )
                {
                	synchronized (workMaterial) {
                        workMaterial.offer( temporaryContainer );
                        workMaterial.notify();
                	}

 
                    Log.v( "CutterTask", "parsed: " + lineNumber + " sentences." );
                    temporaryContainer.clear();

                    publishProgress( (int) (((float) progress / (float) fileSize) * 100) );
                }
            }
            reader.close();
        }
        catch ( IOException e )
        {
            return Boolean.FALSE;
        }
        finally
        {
            finished = true;
        }

        return Boolean.TRUE;
    }

    /**
     * Periodically update the progress bar;
     */
    @Override
    protected void onProgressUpdate( Integer... progress )
    {
        assert progressBar != null;
        assert progress.length == 1;
        progressBar.setProgress( progress[0] );
    }

    /**
     * Writes a line from sentences.csv into a language file.
     * 
     * @param line
     *            Current line to treat
     * @throws IOException
     */
    private Sentence cutLine( String line ) throws IOException
    {
        assert line != null;
        assert line.length() > 0;

        String[] sections = line.split( "\t" );

        // a line consists of an ID, a language, and data
        if ( sections.length > 3 )
            return null;

        // language should have between 1 and 3 characters
        char[] parsedLanguage = sections[1].toCharArray();
        if ( parsedLanguage.length < 1 || parsedLanguage.length > 3 )
            return null;
        // language should be made of characters only
        for ( int i = 0; i < parsedLanguage.length; ++i )
        {
            if ( !Character.isLetter( parsedLanguage[i] ) )
                return null;
        }

        // constructs the sentence object
        int id = Integer.valueOf( sections[0] );
        Language language = new Language(sections[1]);
        String data = new String( sections[2] );
        Sentence sentence = new Sentence( id, language, data );

        // writes the sentence to the new file
        return sentence;
    }

    private class SqlThread implements Runnable
    {
        @Override
        public void run()
        {
            Vector<Sentence> sentencesToAdd = null;
            while ( !finished )
            {
	                try
	                {
	
	                	synchronized (workMaterial) {

	                        workMaterial.wait();	
	                	}
					
	                }
	                catch ( InterruptedException e )
	                {
	                    finished = true;
	                }
	
	                do
	                {
	                	synchronized (workMaterial) {
	                		sentencesToAdd = workMaterial.poll();
	                	}
	
	                    if ( sentencesToAdd != null && !sentencesToAdd.isEmpty() ) {
	                    	synchronized (database) {
		                        database.insertMany( sentencesToAdd );
							}
	                    }
	
	                } while ( sentencesToAdd == null || sentencesToAdd.isEmpty() );
            	
            }
        }
    }

    private volatile ConcurrentLinkedQueue<Vector<Sentence>> workMaterial;
    private volatile boolean finished;

    private ProgressBar progressBar;
    private long progress, fileSize;
    private BufferedReader reader;
    private volatile DBAdapter database;
}
