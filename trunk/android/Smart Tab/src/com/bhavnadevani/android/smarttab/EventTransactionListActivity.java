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
 * Shows the list of transactions corresponding to an event
 * 
 * @author animesh
 * 
 */
public class EventTransactionListActivity extends ListActivity {

	private static final int DIALOG_TRANSACTION_TOGGLE = 0;
	private String mEventTitle;
	private long mEventId;

	Cursor transactionCursor;
	SQLiteDatabase myDB;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// get the bundle
		// adding name

		Bundle eventInfoBundle = getIntent().getBundleExtra(
				Constants.EVENT_INFO_BUNDLE_NAME);

		mEventTitle = eventInfoBundle
				.getString(Constants.EVENT_TITLE_BUNDLE_KEY);

		mEventId = eventInfoBundle.getLong(Constants.EVENT_ID_BUNDLE_KEY);

		Log.d(LOG_TAG, "Getting data for event with Title: " + mEventTitle
				+ " and ID " + mEventId);

		setTitle("Transactions for " + mEventTitle + ", ID:" + mEventId);

		// do the query
		/*
		 * select fasttab_transaction._id as _id, fasttab_transaction.amount as
		 * amount, fasttab_transaction.payment_status as payment_status,
		 * fasttab_transaction.direction as direction, fasttab_person.name as
		 * name, fasttab_transaction.person_id as person_id from
		 * fasttab_transaction, fasttab_person where
		 * fasttab_transaction.event_id = 2 and fasttab_transaction.person_id =
		 * fasttab_person._id
		 */

		SmartTabDBHelper myOpenHelper = new SmartTabDBHelper(
				getApplicationContext());

		myDB = myOpenHelper.getReadableDatabase();

		SQLiteQueryBuilder qBuilder = new SQLiteQueryBuilder();

		qBuilder.setTables(Constants.PERSON_TABLE_NAME + ", "
				+ Constants.TRANSACTIONS_TABLE_NAME);

		transactionCursor = qBuilder.query(myDB,
				Constants.EVENT_TRANSACTION_TABLE_NAMES,
				Constants.TRANSACTIONS_TABLE_NAME + "."
						+ Constants.FOREIGN_KEY_EVENT_ID + " = " + mEventId
						+ " and " + Constants.PERSON_TABLE_NAME + "."
						+ Constants.KEY_ID + " = "
						+ Constants.TRANSACTIONS_TABLE_NAME + "."
						+ Constants.FOREIGN_KEY_PERSON_ID, null, null, null,
				null);

		// TODO This is deprecated. Replace with something else later
		setListAdapter(new SimpleCursorAdapter(
				EventTransactionListActivity.this,
				R.layout.event_transaction_list_item,
				transactionCursor,
				new String[] { "name", "amount", "direction", "payment_status" },
				new int[] { R.id.person_name, R.id.transaction_amount,
						R.id.transaction_direction, R.id.transaction_status }));

		startManagingCursor(transactionCursor);

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
										EventTransactionListActivity.this,
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
										EventTransactionListActivity.this);
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
