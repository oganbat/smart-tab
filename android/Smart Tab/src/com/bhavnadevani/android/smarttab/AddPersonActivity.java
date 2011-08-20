package com.bhavnadevani.android.smarttab;

import com.bhavnadevani.android.smarttab.db.SmartTabDBHelper;

import android.app.Activity;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Activity for adding a person to the DB
 * 
 * @author animesh
 * 
 */
public class AddPersonActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.addperson);

		((Button) findViewById(R.id.addperson_button))
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// see the inputs.
						String name = ((EditText) findViewById(R.id.person_name))
								.getEditableText().toString();
						String email = ((EditText) findViewById(R.id.person_email))
								.getEditableText().toString();
						String phone = ((EditText) findViewById(R.id.person_phone))
								.getEditableText().toString();

						String errorString = validate(name, email, phone);

						if (errorString == null) {
							// all is well. insert entry
							long person_id = addPerson(name, email, phone);
							// kill this activity and return to calling activity
							Toast.makeText(AddPersonActivity.this, "Added person with ID " + person_id, Toast.LENGTH_SHORT).show();
							finish();

						} else {
							// something went wrong. Make a text
							Toast.makeText(AddPersonActivity.this, errorString,
									Toast.LENGTH_SHORT).show();
						}

					}

				});

	}

	private long addPerson(String name, String email, String phone) {
		SmartTabDBHelper myDBHelper = new SmartTabDBHelper(AddPersonActivity.this);
		SQLiteDatabase db = myDBHelper.getWritableDatabase();

		// preparing to add a person. Name, Email, and Phone #
		ContentValues personContentValues = new ContentValues(3);

		// add people to the persons table
		personContentValues.put(Constants.COLUMN_NAME, name);
		personContentValues.put(Constants.COLUMN_EMAIL,
				email);
		personContentValues.put(Constants.COLUMN_PHONE, phone);
		long retValue = db.insert(Constants.PERSON_TABLE_NAME, null,
				personContentValues);
		
		db.close();
		
		Log.d(Constants.LOG_TAG, "added Person (" + retValue + ":" + name + ","
				+ email + "," + phone + "," + ")");
		return retValue;
	}

	/**
	 * Checks the name, email and phone and returns an appropriate error string
	 * 
	 * @param name
	 *            The name
	 * @param email
	 *            The email
	 * @param phone
	 *            The phone number
	 * @return null if all is well, otherwise an error string.
	 */
	private String validate(String name, String email, String phone) {
		if (isNonBlankValidName(name)) {
			// name is valid and non blank
			if ("".equals(phone)) {
				// phone is blank
				if ("".equals(email)) {
					// email also blank
					return "Sorry, at least one of Email and Phone should be non blank";
				} else {
					// phone is blank but email is not
					if (isValidEmail(email)) {
						return null;
					} else {
						// invalid email
						return "Sorry, the email address is invalid";
					}
				}
			} else {
				// phone is not blank
				if (isValidPhone(phone)) {
					// valid phone
					return null;
				} else {
					// invalid phone
					return "Sorry, the phone number is invalid";
				}
			}
		} else {
			// name is invalid/blank
			return "Sorry, the name is invalid";
		}
	}

	/**
	 * Checks if this string is a valid name. For now, just checks if this is
	 * blank
	 * 
	 * @param name
	 *            The string to be checked
	 * @return true if the name if valid, false otherwise
	 */
	private boolean isNonBlankValidName(String name) {
		return !("".equals(name));
	}

	/**
	 * Checks if an email is valid
	 * 
	 * @param email
	 * @return false if the email is not valid. True if it is. Currently not
	 *         very good :)
	 */
	private boolean isValidEmail(String email) {
		// TODO improve the validation
		return true;
	}

	/**
	 * Checks if a phone number is valid
	 * 
	 * @param phone
	 * @return false if the phone is not valid. True if it is. Currently not
	 *         very good :)
	 */
	private boolean isValidPhone(String phone) {
		// TODO improve the validation
		return true;
	}

}
