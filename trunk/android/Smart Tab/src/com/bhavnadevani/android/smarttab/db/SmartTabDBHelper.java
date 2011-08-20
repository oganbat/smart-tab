package com.bhavnadevani.android.smarttab.db;

import com.bhavnadevani.android.smarttab.Constants;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * A DB helper to better access the SQLite database in the backend of the application
 * @author animesh
 *
 */
public class SmartTabDBHelper extends SQLiteOpenHelper {

	/** The name of the DB*/
	private static final String DATABASE_NAME = "smarttab.db";
	
	/** version of the DB. Hopefully it will stay at 1 for some time. When changing it, be sure to update the onUpgrade method*/
	private static final int DATABASE_VERSION = 3;

	/** SQLite query for creating the event table*/
	private static final String EVENT_TABLE_CREATE = 
			"CREATE TABLE " + Constants.EVENT_TABLE_NAME + "(" +
					Constants.KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
					Constants.COLUMN_DATE + " TEXT NOT NULL, " +
					Constants.COLUMN_TITLE + " TEXT NOT NULL, " +
					Constants.COLUMN_DESCRIPTION + " TEXT NOT NULL);";


	/** SQLite query for creating the person table*/
	private static final String PERSON_TABLE_CREATE =   
			"CREATE TABLE " + Constants.PERSON_TABLE_NAME + " (" +
					Constants.KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
					Constants.COLUMN_NAME + " TEXT NOT NULL, " +
					Constants.COLUMN_EMAIL + " TEXT, " +
					Constants.COLUMN_PHONE + " TEXT);";
	
	/** SQLite query for creating the transactions table*/
	private static final String TRANSACTIONS_TABLE_CREATE =
			"CREATE TABLE " + Constants.TRANSACTIONS_TABLE_NAME + " (" +
					Constants.KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
					Constants.COLUMN_AMOUNT + " REAL NOT NULL, " +
					Constants.COLUMN_PAYMENT_STATUS + " INTEGER NOT NULL, " +
					Constants.COLUMN_DIRECTION + " INTEGER NOT NULL, " +
					Constants.FOREIGN_KEY_EVENT_ID + " INTEGER NOT NULL, " +
					Constants.FOREIGN_KEY_PERSON_ID + " INTEGER NOT NULL, " +
					"FOREIGN KEY(" + Constants.FOREIGN_KEY_EVENT_ID +") REFERENCES " + Constants.EVENT_TABLE_NAME+"("+Constants.KEY_ID +")," +
					"FOREIGN KEY(" + Constants.FOREIGN_KEY_PERSON_ID +") REFERENCES " + Constants.PERSON_TABLE_NAME+"("+Constants.KEY_ID +"));";

	/**
	 * Constructor. Not used. Used onCreate() instead.
	 * @param context
	 * @param name
	 * @param factory
	 * @param version
	 */
	public SmartTabDBHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	/** other constructor*/
	public SmartTabDBHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// create event table
		db.execSQL(EVENT_TABLE_CREATE);
		
		// create person table
		db.execSQL(PERSON_TABLE_CREATE);
		
		// create transactions table
		db.execSQL(TRANSACTIONS_TABLE_CREATE);
		
		//preparing to add a person. Name, Email, and Phone #
		ContentValues personContentValues = new ContentValues(3);
		
		//add people to the persons table
		personContentValues.put(Constants.COLUMN_NAME, "Bhavna Devani");
		personContentValues.put(Constants.COLUMN_EMAIL, "bhavna.devani@gmail.com");
		personContentValues.put(Constants.COLUMN_PHONE, "");
		db.insert(Constants.PERSON_TABLE_NAME, null, personContentValues);
		Log.d(Constants.LOG_TAG, "added Bhavna");
		
		personContentValues.put(Constants.COLUMN_NAME, "Sheldon Cooper");
		personContentValues.put(Constants.COLUMN_EMAIL, "scooper@gmail.com");
		personContentValues.put(Constants.COLUMN_PHONE, "");
		db.insert(Constants.PERSON_TABLE_NAME, null, personContentValues);
		Log.d(Constants.LOG_TAG, "added Sheldon");

		personContentValues.put(Constants.COLUMN_NAME, "Penny");
		personContentValues.put(Constants.COLUMN_EMAIL, "");
		personContentValues.put(Constants.COLUMN_PHONE, "+12135508322");
		db.insert(Constants.PERSON_TABLE_NAME, null, personContentValues);
		Log.d(Constants.LOG_TAG, "added Penny");

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.e(Constants.LOG_TAG,"Upgrading to version 1 :)");
	    db.execSQL("DROP TABLE IF EXISTS " + Constants.EVENT_TABLE_NAME);
	    db.execSQL("DROP TABLE IF EXISTS " + Constants.PERSON_TABLE_NAME);
	    db.execSQL("DROP TABLE IF EXISTS " + Constants.TRANSACTIONS_TABLE_NAME);
	    onCreate(db);
	}

}
