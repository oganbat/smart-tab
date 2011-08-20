package com.bhavnadevani.android.smarttab;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import static com.bhavnadevani.android.smarttab.Constants.LOG_TAG;

public class AddEventActivity extends Activity {

	EditText eventNameEditText, eventAmountEditText, eventDescriptionEditText;
	DatePicker eventDatePicker;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.addevent);

		// set contents of the date field as today's date
		eventNameEditText = (EditText) findViewById(R.id.event_name);
		eventAmountEditText = (EditText) findViewById(R.id.event_amount);
		eventDescriptionEditText = (EditText) findViewById(R.id.event_description);
		eventDatePicker = (DatePicker) findViewById(R.id.event_date);

		// get the next button
		Button nextButton = (Button) findViewById(R.id.next_button);
		// set the onClick handler to launch the SelectPeopleActivity, while
		// passing the values in the bundle
		nextButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Log.d(LOG_TAG, "Next button clicked on AddEventActivity");

				try {
					double amount = Double.parseDouble(eventAmountEditText
							.getText().toString());
					Log.d(LOG_TAG, "amount is " + amount);

					String name = eventNameEditText.getText().toString();
					Log.d(LOG_TAG, "name is " + name);

					String description = eventDescriptionEditText.getText()
							.toString();
					Log.d(LOG_TAG, "description is " + description);

					String errorMessage = isValid(name, amount, description);

					//if there is an error message, then show it and do nothing else.
					if (errorMessage != null) {
						Log.e(LOG_TAG, "Error in Validation: " + errorMessage);
						Toast.makeText(AddEventActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
						return;
					}

					// package the name, amount, description, and date into a
					// bundle, and pack it into the intent
					Bundle eventInfoBundle = new Bundle(4);

					// adding name
					eventInfoBundle.putString(Constants.TITLE_BUNDLE_KEY, name);

					// adding amount
					eventInfoBundle.putDouble(Constants.AMOUNT_BUNDLE_KEY,
							amount);

					// adding description
					eventInfoBundle.putString(Constants.DESCRIPTION_BUNDLE_KEY,
							description);

					// adding description
					// get the month, year and date, record it as yyyymmdd
					int year = eventDatePicker.getYear();
					int month = eventDatePicker.getMonth() + 1; // adding 1
																// since months
																// start from 0
					int day = eventDatePicker.getDayOfMonth();
					// The tri-condition statements below append 0s to months
					// and days which are less than 9
					String date = year + (month > 9 ? "" : "0") + month
							+ (day > 9 ? "" : "0") + day;
					Log.d(LOG_TAG, "date is " + date);
					eventInfoBundle.putString(Constants.DATE_BUNDLE_KEY, date);

					// now start the new activity
					Intent newIntent = new Intent(getApplicationContext(),
							AddPeopleToEventActivity.class);
					newIntent.putExtra(Constants.EVENT_INFO_BUNDLE_NAME,
							eventInfoBundle);
					startActivity(newIntent);

				} catch (Exception e) {
					// could not parse the double! WTF!
					Toast.makeText(AddEventActivity.this,
							"Amount is ill-formatted", Toast.LENGTH_SHORT)
							.show();
					return;
				}

			}
		});

	}

	/**
	 * Checks validity of the event name, amount and description
	 * @param name The event name
	 * @param amount The amount involved in the event
	 * @param description The description of the event
	 * @return null if all is well. A string with the message if something is wrong.
	 */
	private String isValid(String name, double amount, String description) {
		
		if("".equals(name))
			return "Event name cannot be empty.";
		
		if("".equals(description))
			return("Event description cannot be empty.");
		
		if(amount <= 0.0)
			return ("Event amount has to be positive.");
		
		return null;
	}

}
