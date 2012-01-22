package com.bhavnadevani.android.smarttab;

import static com.bhavnadevani.android.smarttab.Constants.LOG_TAG;

import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

import com.bhavnadevani.android.smarttab.db.SmartTabDBHelper;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class AddPeopleToEventActivity extends Activity {
	
	protected static final int PICK_PERSON_I_OWE_DIALOG = 0;
	protected static final int PICK_PERSON_WHO_OWES_ME_DIALOG = 1;
	CursorAdapter personListAdapter;
	Cursor personCursor;
	TextView console;
	TextView addEventPersonConsole;
	
	long personId  = Constants.INVALID_PERSON_ID;
	String personName = ""; //blank means noone
	
	Button pickPersonIOweButton;
	Button pickPersonWhoOwesMeButton;
	
	/** list of selected people. Stored as a hashmap to be able to store names also*/
	HashMap<String, String> selectedPeopleMap = new HashMap<String, String>();
	
	
	SQLiteDatabase myDB;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.addpeopletoevent);
		
		//get the extra which has the info about the event
		Bundle eventInfoBundle = getIntent().getExtras().getBundle(Constants.EVENT_INFO_BUNDLE_NAME);
		
		String eventName = eventInfoBundle.getString(Constants.TITLE_BUNDLE_KEY);
		double amount = eventInfoBundle.getDouble(Constants.AMOUNT_BUNDLE_KEY);
		String description = eventInfoBundle.getString(Constants.DESCRIPTION_BUNDLE_KEY);
		String rawdate = eventInfoBundle.getString(Constants.DATE_BUNDLE_KEY);
		String date = Utils.convertRawDateToDisplayDate(rawdate);
		
		Log.d(LOG_TAG, "AddPeopleToEventActivity: Event name is " + eventName);
		Log.d(LOG_TAG, "AddPeopleToEventActivity: Amount is " + amount);
		Log.d(LOG_TAG, "AddPeopleToEventActivity: Description is " + description);
		Log.d(LOG_TAG, "AddPeopleToEventActivity: Date is " + rawdate);
		
		console = (TextView) findViewById(R.id.console_content_addpeopletoevent);
		console.setText("Here are the event details I have:");
		console.append("\nTitle: " + eventName);
		console.append(", Description: " + description);
		console.append(", Amount: " + amount);
		console.append(", Date: " + date);
		
		//append list of people to the console
		SmartTabDBHelper myOpenHelper = new SmartTabDBHelper(getApplicationContext());
		
		
		//uncomment the line below if you are having the "cannot upgrade read-only db..." error
		//myDB = myOpenHelper.getWritableDatabase();
		
		myDB = myOpenHelper.getReadableDatabase();
		
		//pick person
		personCursor = myDB.query(Constants.PERSON_TABLE_NAME, Constants.PERSON_TABLE_PROJECTION, 
				null, null, null, null, null);
		
		//pick people who will pay
		Log.i(Constants.LOG_TAG, "Total # of persons: " + personCursor.getCount());
		for(personCursor.moveToFirst(); !personCursor.isAfterLast(); personCursor.moveToNext()){
			String id = personCursor.getString(0); //index of the ID field
			String name = personCursor.getString(1); //index of the name field
			String email = personCursor.getString(2); //index of the email field
			String phone = personCursor.getString(3); //index of the phone field
			Log.i(Constants.LOG_TAG, id + ": " + name + ", " + email + ", " + phone);
		}
		
		//TODO This is deprecated. Replace with something else later
		personListAdapter = new SimpleCursorAdapter(
				getApplicationContext(), 
				R.layout.person_list_item, 
				personCursor, 
				Constants.PERSON_TABLE_COLUMNS, 
				new int[] {R.id.person_name, R.id.person_email, R.id.person_phone}
		);
		
		startManagingCursor(personCursor);
		
		//set handler on buttons to show dialog with people
		pickPersonIOweButton = (Button) findViewById(R.id.who_do_i_owe_button);
		pickPersonIOweButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.d(Constants.LOG_TAG,"Pick Person I Owe Button Clicked!");
				showDialog(PICK_PERSON_I_OWE_DIALOG);
			}
		});
		pickPersonWhoOwesMeButton = (Button) findViewById(R.id.who_owes_me_button);
		pickPersonWhoOwesMeButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.d(Constants.LOG_TAG,"Pick Person Who Owes Me Button Clicked!");
				showDialog(PICK_PERSON_WHO_OWES_ME_DIALOG);
			}
		});
		
	}
	
	
	@Override
	protected Dialog onCreateDialog(int id) {
		final int dialogId = id;
		Log.d(Constants.LOG_TAG,"Now creating dialog with ID " + id);
		String title = "Pick Person Who I Owe";
		
		if (id == PICK_PERSON_WHO_OWES_ME_DIALOG) title = "Pick Person Who Owes Me";
		
		AlertDialog.Builder builder = new AlertDialog.Builder(AddPeopleToEventActivity.this);
		builder.setTitle(title);
		
		ListView personsList = new ListView(this);
		
		personsList.setAdapter(personListAdapter);
		
		personsList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				Log.d(Constants.LOG_TAG, "item "+id +" clicked");
				//Toast.makeText(getApplicationContext(), "ID " + id + " clicked!", Toast.LENGTH_SHORT).show();
				//get ID, if it is already toggled, untoggle it, else toggle it.
				//go to the cursor, get that item
				personCursor.moveToPosition(position);
				
				String pid = personCursor.getString(0); //index of the ID field
				String name = personCursor.getString(1); //index of the name field
				String email = personCursor.getString(2); //index of the email field
				String phone = personCursor.getString(3); //index of the phone field
				Log.i(Constants.LOG_TAG, "I owe to " + pid + ": " + name + ", " + email + ", " + phone);
				
				if(personId == id){
					//already selected, so deselect it
					personId  = Constants.INVALID_PERSON_ID;
					personName = ""; //blank means noone
				} else {
					personId = id;
					personName = name;
				}
				
				Button button = pickPersonIOweButton;
				if (dialogId == PICK_PERSON_WHO_OWES_ME_DIALOG) {
					button = pickPersonWhoOwesMeButton;
				}
				button.setText(personName + " (Tap to Change/Toggle)");
				
				dismissDialog(dialogId);
			}
		});

		builder.setView(personsList);
		Dialog dialog = builder.create();
		
		//Dialog dialog = new ProgressDialog(getApplicationContext()); 
		
		//dialog.setTitle("Woo!!");
		
		return dialog;
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
