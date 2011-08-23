package com.bhavnadevani.android.smarttab;

import com.bhavnadevani.android.smarttab.db.SmartTabDBHelper;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.AdapterView.OnItemClickListener;

public class EventListActivity extends Activity {

	CursorAdapter eventListAdapter;
	Cursor eventCursor;
	SQLiteDatabase myDB;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.eventlist);

		// figure out what my purpose is

		String whereClauseExtraCondition = "";

		switch (getIntent().getIntExtra(Constants.DIRECTION_KEYNAME, -1)) {
		case Constants.DIRECTION_IOWETO:
			setTitle("Events where I owe");
			whereClauseExtraCondition = " and direction = '"
					+ Constants.DIRECTION_IOWETO + "'";
			break;
		case Constants.DIRECTION_OWESME:
			setTitle("Events where I am owed");
			whereClauseExtraCondition = " and direction = '"
					+ Constants.DIRECTION_OWESME + "'";
			break;
		case -1:
			// nothing
			break;
		}

		// set the proper list adapter etc.
		// append list of people to the console
		SmartTabDBHelper myOpenHelper = new SmartTabDBHelper(
				getApplicationContext());

		// uncomment the line below if you are having the
		// "cannot upgrade read-only db..." error
		// myDB = myOpenHelper.getWritableDatabase();

		myDB = myOpenHelper.getReadableDatabase();

		// pick person
		// eventCursor = myDB.query(Constants.EVENT_TABLE_NAME,
		// Constants.EVENT_TABLE_PROJECTION,
		// null, null, null, null, null);

		SQLiteQueryBuilder qBuilder = new SQLiteQueryBuilder();

		qBuilder.setTables(Constants.EVENT_TABLE_NAME + ", "
				+ Constants.TRANSACTIONS_TABLE_NAME);

		// String queryString = SQLiteQueryBuilder.buildQueryString(false,
		// "fasttab_event, fasttab_transaction",
		// new String[] {"fasttab_event._id", "fasttab_event.title",
		// "fasttab_event.date", "sum(fasttab_transaction.amount)"},
		// "fasttab_event._id = fasttab_transaction.event_id",
		// "fasttab_event._id", null, null, null);
		//

		eventCursor = qBuilder
				.query(myDB, Constants.EVENT_LIST_TABLE_NAMES,
						Constants.EVENT_TABLE_NAME + "." + Constants.KEY_ID
								+ " = " + Constants.TRANSACTIONS_TABLE_NAME
								+ "." + Constants.FOREIGN_KEY_EVENT_ID
								+ whereClauseExtraCondition, null,
						Constants.EVENT_TABLE_NAME + "." + Constants.KEY_ID,
						null, null);

		/*
		 * select fasttab_event._id, fasttab_event.title, fasttab_event.date,
		 * sum(fasttab_transaction.amount) from fasttab_event,
		 * fasttab_transaction where fasttab_event._id =
		 * fasttab_transaction.event_id group by fasttab_event._id;
		 */

		ListView eventListView = (ListView) findViewById(R.id.event_list);

		// TODO This is deprecated. Replace with something else later. Below
		// code altered to remove event_direction from view
		/*
		 * eventListAdapter = new SimpleCursorAdapter(getApplicationContext(),
		 * R.layout.event_list_item, eventCursor, new String[] { "title",
		 * "amount", "direction" }, new int[] { R.id.event_name,
		 * R.id.event_amount, R.id.event_direction });
		 */

		eventListAdapter = new SimpleCursorAdapter(getApplicationContext(),
				R.layout.event_list_item, eventCursor, new String[] { "title",
						"amount" }, new int[] { R.id.event_name,
						R.id.event_amount });

		eventListView.setAdapter(eventListAdapter);

		eventListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Log.d(Constants.LOG_TAG, "item " + id + " clicked");

				eventCursor.moveToPosition(position);

				// the position of the event title
				String eventTitle = eventCursor.getString(1);

				// launch the eventtransactionlist activity
				// Toast.makeText(
				// EventListActivity.this,
				// "Want to launch activity with ID: " + id
				// + " and Title " + eventTitle,
				// Toast.LENGTH_SHORT).show();

				Bundle eventInfoBundle = new Bundle(2);

				// adding name
				eventInfoBundle.putString(Constants.EVENT_TITLE_BUNDLE_KEY,
						eventTitle);

				eventInfoBundle.putLong(Constants.EVENT_ID_BUNDLE_KEY, id);

				// now start the new activity
				Intent newIntent = new Intent(EventListActivity.this,
						EventTransactionListActivity.class);
				newIntent.putExtra(Constants.EVENT_INFO_BUNDLE_NAME,
						eventInfoBundle);
				startActivity(newIntent);

			}
		});

		startManagingCursor(eventCursor);

	}
}
