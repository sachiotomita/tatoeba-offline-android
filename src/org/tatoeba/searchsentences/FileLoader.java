/**
 * 
 */
package org.tatoeba.searchsentences;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import android.os.Handler;
import android.os.Message;

/**
 * Creates two threads. The first reads the file and outputs buffers of data and
 * the second parses the buffers into sentences
 * 
 * @author qdii
 * 
 */
public class FileLoader
{
    FileLoader( Handler uiHandler ) throws FileNotFoundException
    {
        assert (uiHandler != null);

        this.uiHandler = uiHandler;
        sentences = new Vector<Sentence>();
    }

    public void start( String path ) throws FileNotFoundException
    {
        List<char[]> buffers = new ArrayList<char[]>();

        parserThread = new ParserThread( buffers, sentences, uiHandler );
        ioThread = new IOThread( buffers, path );

        new Thread( parserThread ).start();
        new Thread( ioThread ).start();
    }

    public void terminate()
    {
        // TODO: let the user cancel
    }

    class IOThread implements Runnable
    {
        public IOThread( List<char[]> buffers, String path ) throws FileNotFoundException
        {
            fileReader = new FileToBufferReader( buffers, path );
        }

        @Override
        public void run()
        {
            assert (fileReader != null);

            fileReader.readAllFile();
        }

        private FileToBufferReader fileReader;
    };

    class ParserThread implements Runnable
    {
        public ParserThread( List<char[]> buffers, Vector<Sentence> sentences, Handler uiHandler )
        {
            assert (buffers != null);
            assert (sentences != null);

            this.uiHandler = uiHandler;
            bufferParser = new BufferToSentenceParser( buffers, sentences );
        }

        @Override
        public void run()
        {
            assert (bufferParser != null);
            assert (uiHandler != null);

            bufferParser.waitForBufferThenParse();
            uiHandler.sendMessage( Message.obtain( uiHandler, Search.ACTIVATE_BUTTON ) );
        }

        private BufferToSentenceParser bufferParser;
        private Handler uiHandler;
    };

    private Vector<Sentence> sentences;
    private Handler uiHandler;

    private ParserThread parserThread;
    private IOThread ioThread;

}
