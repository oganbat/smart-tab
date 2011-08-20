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
	
	protected static final int PICK_PERSON_DIALOG = 0;
	CursorAdapter personListAdapter;
	Cursor personCursor;
	TextView console;
	TextView addEventPersonConsole;
	
	long iOweToID  = Constants.INVALID_PERSON_ID;
	String iOweToName = ""; //blank means noone
	
	Button pickPersonButton;
	
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
		
		addEventPersonConsole = (TextView)findViewById(R.id.add_event_person_console);
		
		ListView personListView = (ListView)findViewById(R.id.persons_who_owe_me_list);
		
		//TODO This is deprecated. Replace with something else later
		personListAdapter = new SimpleCursorAdapter(
				getApplicationContext(), 
				R.layout.person_list_item, 
				personCursor, 
				Constants.PERSON_TABLE_COLUMNS, 
				new int[] {R.id.person_name, R.id.person_email, R.id.person_phone}
		);
		
		personListView.setAdapter(personListAdapter);
		
		personListView.setOnItemClickListener(new OnItemClickListener() {

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
				Log.i(Constants.LOG_TAG, pid + ": " + name + ", " + email + ", " + phone);
				
				//store this person in a list, to be used in next activity
				
				String id_string = String.valueOf(id);
				String selectedPersonName = selectedPeopleMap.get(id_string); 
				if(selectedPersonName == null){
					//not currently added, add this person
					selectedPeopleMap.put(id_string, name);
				} else {
					//remove it
					selectedPeopleMap.remove(id_string);
				}
				
				//redraw console
				//TODO replace it with a nicer UI
				updateConsole();
				
			}
		});
		
		startManagingCursor(personCursor);
		
		//set handler on button to show dialog with people
		pickPersonButton = (Button)findViewById(R.id.who_do_i_owe_button);
		pickPersonButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.d(Constants.LOG_TAG,"Pick Person Button Clicked!");
				showDialog(PICK_PERSON_DIALOG);
			}
		});
		
		Button nextButton = (Button)findViewById(R.id.add_event_persons_next_button);
		nextButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				//now start the new activity
				//get the old bundle
				Bundle eventInfoWithPersonsBundle = getIntent().getExtras().getBundle(Constants.EVENT_INFO_BUNDLE_NAME);
				//add stuff to it
				
				if(iOweToID == Constants.INVALID_PERSON_ID){
					//I owe no one
					if(selectedPeopleMap.size() == 0){
						//No one owes me
						Toast.makeText(AddPeopleToEventActivity.this, "No people selected", Toast.LENGTH_SHORT).show();
						return;
					} else {
						//some folks owe me
						Log.d(LOG_TAG, "All is well in validation, proceeding to pack bundle");
					}
				} else {
					//I owe someone
					if(selectedPeopleMap.size() == 0){
						//No one owes me
						Log.d(LOG_TAG, "All is well in validation, proceeding to pack bundle");
					} else {
						//some folks owe me
						Toast.makeText(AddPeopleToEventActivity.this, "You can either owe or be owed. Pick one.", Toast.LENGTH_SHORT).show();
						return;
					}
				}
				
				
				//add ioweto ID and name
				eventInfoWithPersonsBundle.putLong(Constants.IOWEWHO_ID_BUNDLE_KEY, iOweToID);
				eventInfoWithPersonsBundle.putString(Constants.IOWEWHO_NAME_BUNDLE_KEY, iOweToName);
				
				//add owestome IDs and names
				Set<String> tempKeySet = selectedPeopleMap.keySet();
				String[] tempKeyStringArray = new String[tempKeySet.size()];
				int i = 0;
				for(String tempKeyString : tempKeySet){
					tempKeyStringArray[i++] = tempKeyString;
				}
				eventInfoWithPersonsBundle.putStringArray(Constants.WHOOWESME_IDS_BUNDLE_KEY, tempKeyStringArray);
				Collection<String> tempNamesSet = selectedPeopleMap.values();
				String[] tempNamesStringArray = new String[tempNamesSet.size()];
				int j = 0;
				for(String tempNameString : tempNamesSet){
					tempNamesStringArray[j++] = tempNameString;
				}
				eventInfoWithPersonsBundle.putStringArray(Constants.WHOOWESME_NAMES_BUNDLE_KEY, tempNamesStringArray);
				
				//create new intent for next activity
				Intent newIntent = new Intent(getApplicationContext(), EditEventAmountsActivity.class);
				
				//launch it
				newIntent.putExtra(Constants.EVENT_WITH_PAYEE_INFO_BUNDLE_NAME, eventInfoWithPersonsBundle);
				startActivity(newIntent);

			}
		});
		
		//update the console
		updateConsole();
		
	}
	
	/** updates the consoleView with currently selected people*/
	void updateConsole(){
		StringBuilder consoleString = new StringBuilder();
		consoleString.append("Currently: ");
		
		if (selectedPeopleMap.size() == 0){
			consoleString.append("nobody");
		} else{
			for(String name : selectedPeopleMap.values()){
				consoleString.append(name + ", ");
			}
			//remove the last comma :)
			consoleString.deleteCharAt(consoleString.length() - 2);
		}

		Log.d(Constants.LOG_TAG,"Updating console with " + consoleString);
		addEventPersonConsole.setText(consoleString);
		
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		Log.d(Constants.LOG_TAG,"Now creating dialog with ID " + id);

		AlertDialog.Builder builder = new AlertDialog.Builder(AddPeopleToEventActivity.this);
		builder.setTitle("Pick Person Who I Owe");
		
		ListView iOweList = new ListView(this);
		
		iOweList.setAdapter(personListAdapter);
		
		iOweList.setOnItemClickListener(new OnItemClickListener() {

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
				
				if(iOweToID == id){
					//already selected, so deselect it
					iOweToID  = Constants.INVALID_PERSON_ID;
					iOweToName = ""; //blank means noone
				} else {
					iOweToID = id;
					iOweToName = name;
				}
				
				
				pickPersonButton.setText(iOweToName + " (Tap to Change/Toggle)");
				
				dismissDialog(PICK_PERSON_DIALOG);
			}
		});

		builder.setView(iOweList);
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
