package com.bhavnadevani.android.smarttab;

import static com.bhavnadevani.android.smarttab.Constants.LOG_TAG;

import com.bhavnadevani.android.smarttab.db.SmartTabDBHelper;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

public class PersonListActivity extends ListActivity {

	Cursor personCursor;
	SQLiteDatabase myDB;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		SmartTabDBHelper myOpenHelper = new SmartTabDBHelper(
				getApplicationContext());

		// uncomment the line below if you are having the
		// "cannot upgrade read-only db..." error
		// myDB = myOpenHelper.getWritableDatabase();

		myDB = myOpenHelper.getReadableDatabase();

		// pick person
		personCursor = myDB
				.query(Constants.PERSON_TABLE_NAME,
						Constants.PERSON_TABLE_PROJECTION, null, null, null,
						null, null);
		startManagingCursor(personCursor);

		// ListView personListView =
		// (ListView)findViewById(R.id.persons_who_owe_me_list);

		// TODO This is deprecated. Replace with something else later
		ListAdapter personListAdapter = new SimpleCursorAdapter(this,
				R.layout.person_list_item, personCursor,
				Constants.PERSON_TABLE_COLUMNS, new int[] { R.id.person_name,
						R.id.person_email, R.id.person_phone });

		setListAdapter(personListAdapter);

	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		Log.d(Constants.LOG_TAG, "item " + id + " clicked");
		// Toast.makeText(getApplicationContext(), "ID " + id + " clicked!",
		// Toast.LENGTH_SHORT).show();
		// get ID, if it is already toggled, untoggle it, else toggle it.
		// go to the cursor, get that item
		personCursor.moveToPosition(position);

		String pid = personCursor.getString(0); // index of the ID field
		String name = personCursor.getString(1); // index of the name field
		String email = personCursor.getString(2); // index of the email field
		String phone = personCursor.getString(3); // index of the phone field
		Log.i(Constants.LOG_TAG, pid + ": " + name + ", " + email + ", "
				+ phone);

		// Toast.makeText(this, "Person ("+pid + ": " + name + ", " + email +
		// ", " + phone+") clicked", Toast.LENGTH_SHORT).show();

		// package the name, amount, description, and date into a
		// bundle, and pack it into the intent
		Bundle personInfoBundle = new Bundle(2);

		// adding name
		personInfoBundle.putString(Constants.PERSON_NAME_BUNDLE_KEY, name);

		personInfoBundle.putLong(Constants.PERSON_ID_BUNDLE_KEY, id);

		// now start the new activity
		Intent newIntent = new Intent(PersonListActivity.this,
				PersonTransactionListActivity.class);
		newIntent.putExtra(Constants.PERSON_INFO_BUNDLE_NAME, personInfoBundle);
		startActivity(newIntent);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.personlist_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.add_person_menu_item:
			startActivity(new Intent(this, AddPersonActivity.class));
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

}
