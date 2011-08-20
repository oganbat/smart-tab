package com.bhavnadevani.android.smarttab;

import static com.bhavnadevani.android.smarttab.Constants.LOG_TAG;

import java.security.acl.Owner;

import com.bhavnadevani.android.smarttab.db.SmartTabDBHelper;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
/**
 * This activity shows the current info about the event (title and amount), and allows the user to change the amount each person owes
 * @author animesh
 *
 */
public class EditEventAmountsActivity extends Activity {
	
	//shared variables
	private String eventName;
	private double amount;
	private String description;
	private String rawdate;
	private String date;

	private long iowetoID;
	private String iowetoName;
	private String[] whoowesmeIDs;
	private String[] whoowesmeNames;
	private int numberOfPeople;
	/** the amount owed by each person*/
	private double[] amounts;
	
	private TextView console;
	
	/**
	 * unpacks the bundle received in the intent, and unpacks into local variables
	 */
	private void unpackBundle(){
		
		//TODO first check if the extras meant "show details of an existing activity"
		
		Bundle b = getIntent().getExtras().getBundle(Constants.EVENT_WITH_PAYEE_INFO_BUNDLE_NAME);
		eventName = b.getString(Constants.TITLE_BUNDLE_KEY);
		amount = b.getDouble(Constants.AMOUNT_BUNDLE_KEY);
		description = b.getString(Constants.DESCRIPTION_BUNDLE_KEY);
		rawdate = b.getString(Constants.DATE_BUNDLE_KEY);
		date = Utils.convertRawDateToDisplayDate(rawdate);
		iowetoID = b.getLong(Constants.IOWEWHO_ID_BUNDLE_KEY);
		iowetoName = b.getString(Constants.IOWEWHO_NAME_BUNDLE_KEY);
		whoowesmeIDs = b.getStringArray(Constants.WHOOWESME_IDS_BUNDLE_KEY);
		whoowesmeNames = b.getStringArray(Constants.WHOOWESME_NAMES_BUNDLE_KEY);
		
		numberOfPeople = whoowesmeIDs.length;
		
		amounts = new double[numberOfPeople];
		
		Log.d(LOG_TAG, "EditEventAmountsActivity: Event name is " + eventName);
		Log.d(LOG_TAG, "EditEventAmountsActivity: Amount is " + amount);
		Log.d(LOG_TAG, "EditEventAmountsActivity: Description is " + description);
		Log.d(LOG_TAG, "EditEventAmountsActivity: Date is " + rawdate);
		
		console.setText("Here are the event details I have:");
		console.append("\nTitle: " + eventName);
		console.append(", Description: " + description);
		console.append(", Amount: " + amount);
		console.append(", Date: " + date);
		
		//now print the owing info
		Log.d(LOG_TAG, "\nEditEventAmountsActivity: I owe (" + iowetoID + ", " + iowetoName +")");
		console.append("\nI owe (" + iowetoID + ", " + iowetoName +")");
		Log.d(LOG_TAG, "\nEditEventAmountsActivity: These folks owe me");
		console.append("\nThese folks owe me");
		for(int i = 0; i< numberOfPeople; i++){
			//initializing so everyone owes equally
			amounts[i] = amount/numberOfPeople;
			
			Log.d(LOG_TAG, "\nEditEventAmountsActivity: " + whoowesmeIDs[i] + ", " + whoowesmeNames[i]);
			console.append("\n" + whoowesmeIDs[i] + ", " + whoowesmeNames[i] + ", EUR " + amounts[i]);
			
		}
		
		
	}	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//load View
		setContentView(R.layout.editeventamounts);
		
		//get console
		console = (TextView)findViewById(R.id.edit_event_amount_console);
		
		//unpack and write on console
		unpackBundle();
		
		//add handler to submit button
		Button commitButton = (Button) findViewById(R.id.edit_event_amounts_next_button);
		commitButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				
				//TODO validate.
				
				//create entry for event, get ID
				long eventID = createEventEntryInDB(eventName,description,rawdate);
				if(eventID >= 0){
					for(int j = 0; j<numberOfPeople;j++){
						//for each person
						//create entry for transaction, with appropriate amount,
						if(createTransactionEntryInDB(Long.decode(whoowesmeIDs[j]),Constants.DIRECTION_OWESME,amounts[j],eventID) < 0){
							console.append("Entering transaction for " + whoowesmeIDs[j] +"," + whoowesmeNames[j] + "failed");
							Log.e(LOG_TAG, "Entering transaction for " + whoowesmeIDs[j] +"," + whoowesmeNames[j] + "failed");
							//TODO rollback transaction addition?
						}
					}
					
					//add the owes to also, if not blank
					if(iowetoID != Constants.INVALID_PERSON_ID){
						if(createTransactionEntryInDB(iowetoID,Constants.DIRECTION_IOWETO,amount,eventID) < 0){
							console.append("Entering transaction for " + iowetoID +"," + iowetoName + "failed");
							Log.e(LOG_TAG, "Entering transaction for " + iowetoID +"," + iowetoName + "failed");
							//TODO rollback transaction addition?
						}
					}
					
					
				}else{
					console.append("EventID " + eventID + "returned");
					Log.e(LOG_TAG, "OOPS! Negative EventID " + eventID + "returned");
				}
				
				//make toast saying all is well :)
				Toast.makeText(EditEventAmountsActivity.this, 
						"All seems to have gone well in inserting new event details :).", 
						Toast.LENGTH_SHORT
						).show();
				
				Intent newIntent = new Intent(EditEventAmountsActivity.this, HomeActivity.class);
				newIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(newIntent);
				
			}	

			/**
			 * Add a new transaction in the DB
			 * @param personID_insert other person's ID
			 * @param direction_insert Direction of payment. Either Constants.DIRECTION_OWESME or Constants.DIRECTION_IOWESTO
			 * @param amount_insert The amount owed
			 * @param eventID_insert The relevant Event ID
			 * @return the ID of the transaction created in the DB, negative if something went wrong.
			 */
			private long createTransactionEntryInDB(long personID_insert,
					int direction_insert, double amount_insert, long eventID_insert) {
				
				SmartTabDBHelper myDBHelper = new SmartTabDBHelper(EditEventAmountsActivity.this);
				SQLiteDatabase db = myDBHelper.getWritableDatabase();
				
				//preparing to add a person. Name, Email, and Phone #
				ContentValues transactionContentValues = new ContentValues(6);
				
				//add event to the events table
				transactionContentValues.put(Constants.FOREIGN_KEY_PERSON_ID,personID_insert);
				transactionContentValues.put(Constants.COLUMN_DIRECTION, direction_insert);
				transactionContentValues.put(Constants.COLUMN_AMOUNT, amount_insert);
				transactionContentValues.put(Constants.FOREIGN_KEY_EVENT_ID, eventID_insert);
				
				//initially unpaid
				transactionContentValues.put(Constants.COLUMN_PAYMENT_STATUS,Constants.PAYMENT_STATUS_UNPAID);
				long newTransactionID = db.insert(Constants.TRANSACTIONS_TABLE_NAME, null, transactionContentValues);
				Log.d(Constants.LOG_TAG, "added Transaction with person ID" + personID_insert + " for " + amount_insert);
				
				db.close();				
				
				return newTransactionID;
			}

			/**
			 * Add an entry for this event in the database.
			 * @param eventName_insert The name or title of this event
			 * @param description_insert the description of this event
			 * @param rawdate_insert the date when the event happenned. In the format YYYYMMDD
			 * @return the ID of the event if successful, or a negative number if not.
			 */
			private long createEventEntryInDB(String eventName_insert, String description_insert, String rawdate_insert) {

				SmartTabDBHelper myDBHelper = new SmartTabDBHelper(EditEventAmountsActivity.this);
				SQLiteDatabase db = myDBHelper.getWritableDatabase();
				
				//preparing to add a person. Name, Email, and Phone #
				ContentValues eventContentValues = new ContentValues(3);
				
				//add event to the events table
				eventContentValues.put(Constants.COLUMN_TITLE, eventName_insert);
				eventContentValues.put(Constants.COLUMN_DESCRIPTION, description_insert);
				eventContentValues.put(Constants.COLUMN_DATE, rawdate_insert);
				long newEventID = db.insert(Constants.EVENT_TABLE_NAME, null, eventContentValues);
				Log.d(Constants.LOG_TAG, "added Event " + eventName_insert);
				
				db.close();
				
				return newEventID;
			}
		});
	}

}
