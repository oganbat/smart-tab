/**
 * 
 */
package com.bhavnadevani.android.smarttab;

import com.bhavnadevani.android.smarttab.db.SmartTabDBHelper;

import static com.bhavnadevani.android.smarttab.Constants.LOG_TAG;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

/**
 * The activity showing the transactions of a single person
 * 
 * @author animesh
 */
public class PersonTransactionListActivity extends ListActivity {

	private static final int DIALOG_TRANSACTION_TOGGLE = 0;
	private String mPersonName;
	private long mPersonId;

	Cursor transactionCursor;
	SQLiteDatabase myDB;



	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// setContentView(R.layout.person_transaction_list);

		// get the bundle
		// adding name

		Bundle personInfoBundle = getIntent().getBundleExtra(
				Constants.PERSON_INFO_BUNDLE_NAME);

		mPersonName = personInfoBundle
				.getString(Constants.PERSON_NAME_BUNDLE_KEY);

		mPersonId = personInfoBundle.getLong(Constants.PERSON_ID_BUNDLE_KEY);

		Log.d(LOG_TAG, "Getting data for person with Name: " + mPersonName
				+ " and ID " + mPersonId);

		setTitle("Transactions with " + mPersonName + ", ID:" + mPersonId);

		// do the query
		/*
		 * select fasttab_transaction._id as _id, fasttab_transaction.amount as
		 * amount, fasttab_transaction.payment_status as payment_status,
		 * fasttab_transaction.direction as direction, fasttab_event.name as
		 * title, fasttab_event.date as date from fasttab_transaction,
		 * fasttab_event where fasttab_transaction.person_id = 2 and
		 * fasttab_transaction.event_id = fasttab_event._id order by
		 * fasttab_event.date desc
		 */

		SmartTabDBHelper myOpenHelper = new SmartTabDBHelper(
				getApplicationContext());

		myDB = myOpenHelper.getReadableDatabase();

		SQLiteQueryBuilder qBuilder = new SQLiteQueryBuilder();

		qBuilder.setTables(Constants.EVENT_TABLE_NAME + ", "
				+ Constants.TRANSACTIONS_TABLE_NAME);

		transactionCursor = qBuilder.query(myDB,
				Constants.PERSON_TRANSACTION_TABLE_NAMES,
				Constants.TRANSACTIONS_TABLE_NAME + "."
						+ Constants.FOREIGN_KEY_PERSON_ID + " = " + mPersonId
						+ " and " + Constants.EVENT_TABLE_NAME + "."
						+ Constants.KEY_ID + " = "
						+ Constants.TRANSACTIONS_TABLE_NAME + "."
						+ Constants.FOREIGN_KEY_EVENT_ID, null, null, null,
				Constants.EVENT_TABLE_NAME + "." + Constants.COLUMN_DATE
						+ " desc");

		/*
		 * select fasttab_event._id, fasttab_event.title, fasttab_event.date,
		 * sum(fasttab_transaction.amount) from fasttab_event,
		 * fasttab_transaction where fasttab_event._id =
		 * fasttab_transaction.event_id group by fasttab_event._id;
		 */

		// TODO This is deprecated. Replace with something else later
		setListAdapter(new SimpleCursorAdapter(
				PersonTransactionListActivity.this,
				R.layout.person_transaction_list_item,
				transactionCursor,
				new String[] { "title", "amount", "direction", "payment_status" },
				new int[] { R.id.event_name, R.id.transaction_amount,
						R.id.transaction_direction, R.id.transaction_status }));

		startManagingCursor(transactionCursor);
	}
	
	@Override
	protected void onDestroy() {
		myDB.close();
		super.onDestroy();
	}
	

	// These will be stored when the dialog is shown.
	long mTransactionId;
	int mDirection;
	int mPaymentStatus;

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);

		// Store the values form the cursor into the local var
		transactionCursor.moveToPosition(position);
		mTransactionId = transactionCursor.getInt(transactionCursor
				.getColumnIndex("_id"));
		mDirection = transactionCursor.getInt(transactionCursor
				.getColumnIndex("direction"));
		mPaymentStatus = transactionCursor.getInt(transactionCursor
				.getColumnIndex("payment_status"));

		showDialog(DIALOG_TRANSACTION_TOGGLE);

	}

	@Override
	protected Dialog onCreateDialog(int id) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		String newStatus = (mPaymentStatus == Constants.PAYMENT_STATUS_PAID) ? "unpaid"
				: "paid";
		builder.setMessage(
				"Are you sure you want to toggle the status of this transaction?")
				.setPositiveButton("Yes",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								// TODO what happens when yes is clicked
								Toast.makeText(
										PersonTransactionListActivity.this,
										"Setting transaction ID "
												+ mTransactionId
												+ " to "
												+ ((mPaymentStatus == Constants.PAYMENT_STATUS_PAID) ? "unpaid"
														: "paid") + " for "
												+ mTransactionId + " "
												+ mDirection + " "
												+ mPaymentStatus,
										Toast.LENGTH_SHORT).show();
								// do the query
								int newStatusCode = (mPaymentStatus == Constants.PAYMENT_STATUS_PAID) ? Constants.PAYMENT_STATUS_UNPAID
										: Constants.PAYMENT_STATUS_PAID;
								/*
								 * update fasttab_transaction set
								 * fasttab_transaction.payment_status =
								 * newStatusCode where fasttab_transaction._id =
								 * mTransactionId
								 */
								SmartTabDBHelper myOpenHelper = new SmartTabDBHelper(
										PersonTransactionListActivity.this);
								SQLiteDatabase myUpdateDB;

								myUpdateDB = myOpenHelper.getWritableDatabase();
								ContentValues transactionContentValues = new ContentValues(
										1);

								// add people to the persons table
								transactionContentValues.put(
										Constants.COLUMN_PAYMENT_STATUS,
										newStatusCode);

								int numRows = myUpdateDB.update(
										Constants.TRANSACTIONS_TABLE_NAME,
										transactionContentValues,
										Constants.TRANSACTIONS_TABLE_NAME+"."+Constants.KEY_ID + " = "
												+ mTransactionId, null);

								
								Log.d(LOG_TAG, "number of rows affected = "
										+ numRows);
								myUpdateDB.close();

								transactionCursor.requery();

							}
						})
				.setNegativeButton("No", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// Do nothing, just dismiss the dialog
						dialog.dismiss();
					}
				});
		AlertDialog alert = builder.create();

		return alert;
	}

}
