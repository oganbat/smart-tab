package com.bhavnadevani.android.smarttab;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import static com.bhavnadevani.android.smarttab.Constants.LOG_TAG;

public class HomeActivity extends Activity {

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// call my parent's onCreate method
		super.onCreate(savedInstanceState);

		// set my view
		setContentView(R.layout.main);

		// set a handler for the add event button
		Button addEventButton = (Button) findViewById(R.id.add_event_button);

		addEventButton.setOnClickListener(new OnClickListener() {

//			@Override
						public void onClick(View v) {
			//TextView consoleContent = (TextView) findViewById(R.id.console_content);
			//consoleContent.append("\nAdd Event Pressed.");

				// log that the button was pressed
			//Log.d(LOG_TAG, "Add Event Pressed, Launching new activity");

				// launch the add event activity
				// set up an intent to launch the AddEventActivity specifically
				Intent newIntent = new Intent(getApplicationContext(),
						AddEventActivity.class);

				// start that activity
				startActivity(newIntent);

			}
		});

		((Button) findViewById(R.id.see_recent_history_button))
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						startActivity(new Intent(HomeActivity.this,
								EventListActivity.class));
					}
				});

		((Button) findViewById(R.id.see_recent_credits_button))
		.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent tempIntent = new Intent(HomeActivity.this,
						EventListActivity.class);
				tempIntent.putExtra(Constants.DIRECTION_KEYNAME, Constants.DIRECTION_IOWETO);
				startActivity(tempIntent);
			}
		});

		((Button) findViewById(R.id.see_recent_debits_button))
		.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent tempIntent = new Intent(HomeActivity.this,
						EventListActivity.class);
				tempIntent.putExtra(Constants.DIRECTION_KEYNAME, Constants.DIRECTION_OWESME);
				startActivity(tempIntent);
			}
		});

		((Button) findViewById(R.id.list_persons))
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						startActivity(new Intent(HomeActivity.this,
								PersonListActivity.class));
					}
				});

	}
}