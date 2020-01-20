package org.tatoeba.searchsentences;

import java.util.Vector;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBAdapter
{
    public static final String KEY_ID = "sentence_id"; // sentence id
    public static final String KEY_LANGUAGE = "language";
    public static final String KEY_DATA = "data";
    private static final String TAG = "DBAdapter";

    private static final String DATABASE_NAME = "sentences";
    private static final String TABLE_NAME = "tatoeba";
    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_CREATE = "create table " + TABLE_NAME + " (" + KEY_ID
            + " integer primary key, " + KEY_LANGUAGE + " text not null, " + KEY_DATA
            + " text not null);";
    private static final String DATABASE_COLUMNS = "select count(*) from INFORMATION_SCHEMA.COLUMNS where table_catalog = \""
            + TABLE_NAME + "\" and table_name = \"" + DATABASE_NAME + "\"";

    private final Context context;

    private DatabaseHelper dbHelper;
    private SQLiteDatabase db;

    public DBAdapter( Context ctx )
    {
        this.context = ctx;
        dbHelper = new DatabaseHelper( context );
    }

    public DBAdapter open() throws SQLException
    {
        db = dbHelper.getWritableDatabase();
        return this;
    }

    // check if the database exists AND contains sentences
    public boolean isCreatedAndPopulated()
    {
        return getNbSentences() > 0;
    }

    public void destroyTable()
    {
        assert db != null && db.isOpen();

        db.execSQL( "DROP TABLE IF EXISTS " + TABLE_NAME );
    }

    public void createTable()
    {
        assert db != null && db.isOpen();
        dbHelper.onCreate( db );
    }

    public int getNbSentences()
    {
        assert db != null && db.isOpen();

        String[] args = { new String() };
        Cursor cursor = db.rawQuery( DATABASE_COLUMNS, args );
        assert cursor != null;

        int columnIndex = cursor.getColumnIndex( "Rows" );
        cursor.close();

        return cursor.getInt( columnIndex );
    }

    // ---insert a title into the database---
    public long insert( Sentence sentence )
    {
        assert db != null;
        assert db.isOpen();

        ContentValues initialValues = prepareSentenceForInsertion( sentence );

        return db.insert( TABLE_NAME, null, initialValues );
    }

    public long insertMany( Vector<Sentence> sentences )
    {
        assert db != null && db.isOpen();
        assert sentences.size() > 0;


        db.beginTransaction();
        for ( int i = 0; i < sentences.size(); ++i )
        {
            db.insert( TABLE_NAME, null, prepareSentenceForInsertion( sentences.get( i ) ) );
        }
        db.endTransaction();

        return sentences.size();
    }

    private ContentValues prepareSentenceForInsertion( Sentence sentence )
    {
    	int sentenceId = sentence.getId();
    	String lang = new String(sentence.language().name());
    	String data = sentence.data();
    	
        ContentValues initialValues = new ContentValues();
        initialValues.put( KEY_ID, sentenceId );
        initialValues.put( KEY_LANGUAGE, lang );
        initialValues.put( KEY_DATA, data );
        Log.d("prepare", lang + " " + data + " " + sentenceId);
        Log.d("prepare", initialValues.toString());

        return initialValues;
    }

    // ---retrieves a particular title---
    public Sentence fetchSentence( int sentenceId ) throws SQLException
    {
        Cursor cursor = db.query( true, TABLE_NAME,
                new String[] { KEY_ID, KEY_LANGUAGE, KEY_DATA }, KEY_ID + "=" + sentenceId, null,
                null, null, null, null );

        if ( cursor == null )
            return null;

        cursor.moveToFirst();
        assert sentenceId == cursor.getInt( 0 );
        Language language = new Language( cursor.getString( 1 ));
        Sentence sentence = new Sentence( sentenceId, language, cursor.getString( 2 ) );
        cursor.close();

        return sentence;
    }

    private static class DatabaseHelper extends SQLiteOpenHelper
    {
        DatabaseHelper( Context context )
        {
            super( context, DATABASE_NAME, null, DATABASE_VERSION );
        }

        @Override
        public void onCreate( SQLiteDatabase db )
        {
            db.execSQL( DATABASE_CREATE );
        }

        @Override
        public void onUpgrade( SQLiteDatabase db, int oldVersion, int newVersion )
        {
            Log.w( TAG, "Upgrading database from version " + oldVersion + " to " + newVersion
                    + ", which will destroy all old data" );
            db.execSQL( "DROP TABLE IF EXISTS " + TABLE_NAME );
            onCreate( db );
        }
    }
}