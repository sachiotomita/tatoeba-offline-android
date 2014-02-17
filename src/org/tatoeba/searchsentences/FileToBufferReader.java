package org.tatoeba.searchsentences;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

/**
 * Reads data into buffers and notify when a buffer is available.
 * 
 * @author qdii
 * 
 */
public class FileToBufferReader
{
    private static final int BUFFER_SIZE = 30000; // number of bytes per buffer
    private static final int NB_BUFFER_MAX = 10; // after filling up these many
                                                 // buffers, wait

    /**
     * Constructs a FileToBufferReader
     * 
     * @param allBuffers
     *            A shared object
     * @param pathToFile
     * @throws FileNotFoundException
     *             If the path is incorrect
     */
    public FileToBufferReader( List<char[]> allBuffers, String pathToFile )
            throws FileNotFoundException
    {
        assert (allBuffers != null);

        this.fileReader = new FileReader( new File( pathToFile ) );
        this.allBuffers = allBuffers;
        this.currentOffset = 0;
    }

    /**
     * Read file to a buffer and add this buffer to the list.
     * 
     * @param buffer
     *            An empty buffer that will be filled up with data from the file
     * @return -1 on error, 0 if the file is closed, the nb of bytes read
     *         otherwise
     */
    private int readToBuffer( char[] buffer )
    {
        assert (buffer != null);
        assert (buffer.length >= BUFFER_SIZE);
        assert (fileReader != null);

        int nbReadBytes = 0;
        try
        {
            nbReadBytes = fileReader.read( buffer, 0, BUFFER_SIZE );
        }
        catch ( IOException e )
        {
            return -1;
        }

        if ( nbReadBytes >= 0 )
            currentOffset += nbReadBytes;

        return nbReadBytes;
    }

    /**
     * Read all the file into buffers.
     */
    public void readAllFile()
    {
        int nbBuffers = 0;
        for ( int nbOfBytesRead = 0;; )
        {
            boolean skipReading = false;
            char[] buffer = null;

            // only update "nbBuffers" if truly necessary
            if ( nbBuffers > NB_BUFFER_MAX )
            {
                synchronized ( allBuffers )
                {
                    nbBuffers = allBuffers.size();
                }
                skipReading = nbBuffers >= NB_BUFFER_MAX;
            }

            if ( !skipReading )
            {

                buffer = new char[BUFFER_SIZE + 1];
                nbOfBytesRead = readToBuffer( buffer );
                nbBuffers++;

                if ( nbOfBytesRead <= 0 )
                    break;

                synchronized ( allBuffers )
                {
                    allBuffers.add( buffer );
                }
            }

            synchronized ( allBuffers )
            {
                allBuffers.notify();
            }
        }
    }

    private List<char[]> allBuffers;
    private FileReader fileReader;
    private int currentOffset;
}
