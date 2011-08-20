package com.bhavnadevani.android.smarttab;

/** A class to store all constants */
public class Constants {
	/** Our log tag. Giving it package scope for now. */
	public static final String LOG_TAG = "Smart Tab";

	// the keys used in bundles
	protected static final String TITLE_BUNDLE_KEY = "EVENT_NAME";
	protected static final String DESCRIPTION_BUNDLE_KEY = "EVENT_DESCRIPTION";
	protected static final String AMOUNT_BUNDLE_KEY = "EVENT_AMOUNT";
	protected static final String DATE_BUNDLE_KEY = "EVENT_DATE";
	protected static final String WHOOWESME_IDS_BUNDLE_KEY = "WHOOWESME_IDS";
	protected static final String WHOOWESME_NAMES_BUNDLE_KEY = "WHOOWESME_NAMES";
	protected static final String IOWEWHO_ID_BUNDLE_KEY = "IOWEWHO_ID";
	protected static final String IOWEWHO_NAME_BUNDLE_KEY = "IOWEWHO_NAME";
	protected static final String PERSON_ID_BUNDLE_KEY = "PERSON_ID";
	protected static final String PERSON_NAME_BUNDLE_KEY = "PERSON_NAME";
	protected static final String EVENT_ID_BUNDLE_KEY = "EVENT_ID";
	protected static final String EVENT_TITLE_BUNDLE_KEY = "EVENT_TITLE";

	/**
	 * the tag used for the bundle that contains the event info, sent to the
	 * AddPeopleToEventActivity activity
	 */
	protected static final String EVENT_INFO_BUNDLE_NAME = "EVENT_INFO";
	/**
	 * the tag used for the bundle that has the event info, and also the list of
	 * owers and owees
	 */
	protected static final String EVENT_WITH_PAYEE_INFO_BUNDLE_NAME = "EVENT_INFO_WITH_PERSONS";
	/** the tag used for the bundle that has the person info */
	protected static final String PERSON_INFO_BUNDLE_NAME = "PERSON_INFO";

	public static final String EVENT_TABLE_NAME = "SMARTTAB_EVENT";
	public static final String TRANSACTIONS_TABLE_NAME = "SMARTTAB_TRANSACTION";
	public static final String PERSON_TABLE_NAME = "SMARTTAB_PERSON";

	/** make sure this is lowercase */
	public static final String KEY_ID = "_id";
	public static final String COLUMN_DATE = "DATE";
	public static final String COLUMN_TITLE = "TITLE";
	public static final String COLUMN_DESCRIPTION = "DESCRIPTION";
	public static final String COLUMN_PHONE = "PHONE";
	public static final String COLUMN_NAME = "NAME";
	public static final String COLUMN_EMAIL = "EMAIL";
	public static final String COLUMN_AMOUNT = "AMOUNT";
	public static final String COLUMN_DIRECTION = "DIRECTION";
	public static final String COLUMN_PAYMENT_STATUS = "PAYMENT_STATUS";
	public static final String FOREIGN_KEY_EVENT_ID = "EVENT_ID";
	public static final String FOREIGN_KEY_PERSON_ID = "PERSON_ID";
	/** int code for direction "I owe this to other" */
	public static final int DIRECTION_IOWETO = 0;
	/** int code for direction "Other owes me" */
	public static final int DIRECTION_OWESME = 1;

	/** int code for payment status "unpaid" */
	public static final int PAYMENT_STATUS_UNPAID = 0;
	/** int code for payment status "paid" */
	public static final int PAYMENT_STATUS_PAID = 1;

	/**
	 * The projection which we make when making the query to the person table in
	 * the KB
	 */
	public static final String[] PERSON_TABLE_PROJECTION = { KEY_ID, // 0
			COLUMN_NAME, // 1
			COLUMN_EMAIL, // 2
			COLUMN_PHONE // 3
	};

	/** The columns chosen to display in the list */
	public static final String[] PERSON_TABLE_COLUMNS = { COLUMN_NAME, // 0
			COLUMN_EMAIL, // 1
			COLUMN_PHONE // 2
	};

	/** ID used in the program to mean "invalid person" */
	public static final long INVALID_PERSON_ID = -1;

	/**
	 * The projection which we make when making the query to the person table in
	 * the KB
	 */
	public static final String[] EVENT_TABLE_PROJECTION = { KEY_ID, // 0
			COLUMN_TITLE, // 1
			COLUMN_DATE, // 2
	};

	/** The columns chosen to display in the list */
	public static final String[] EVENT_TABLE_COLUMNS = { COLUMN_TITLE, // 0
			COLUMN_DATE, // 1
	};

	public static final String[] EVENT_LIST_TABLE_NAMES = {
			EVENT_TABLE_NAME+"."+KEY_ID +" as _id",
			EVENT_TABLE_NAME + "." + COLUMN_TITLE + " as title",
			EVENT_TABLE_NAME + "." + COLUMN_DATE + " as date",
			"sum("+TRANSACTIONS_TABLE_NAME+"."+COLUMN_AMOUNT+") as amount",
			"min("+TRANSACTIONS_TABLE_NAME+"."+COLUMN_DIRECTION+") as direction" };

	public static final String[] PERSON_TRANSACTION_TABLE_NAMES = {
			TRANSACTIONS_TABLE_NAME + "." + KEY_ID + " as _id",
			TRANSACTIONS_TABLE_NAME + "." + COLUMN_AMOUNT + " as amount",
			TRANSACTIONS_TABLE_NAME + "." + COLUMN_PAYMENT_STATUS
					+ " as payment_status",
			TRANSACTIONS_TABLE_NAME + "." + COLUMN_DIRECTION + " as direction",
			EVENT_TABLE_NAME + "." + COLUMN_TITLE + " as title",
			EVENT_TABLE_NAME + "." + COLUMN_DATE + " as date" };

	public static final String[] EVENT_TRANSACTION_TABLE_NAMES = {
			TRANSACTIONS_TABLE_NAME + "." + KEY_ID + " as _id",
			TRANSACTIONS_TABLE_NAME + "." + COLUMN_AMOUNT + " as amount",
			TRANSACTIONS_TABLE_NAME + "." + COLUMN_PAYMENT_STATUS
					+ " as payment_status",
			TRANSACTIONS_TABLE_NAME + "." + COLUMN_DIRECTION + " as direction",
			PERSON_TABLE_NAME + "." + COLUMN_NAME + " as name",
			TRANSACTIONS_TABLE_NAME + "." + FOREIGN_KEY_PERSON_ID
					+ " as person_id" };

	/**
	 * the key used in the bundle passed to {@link EventListActivity}
	 */
	protected static final String DIRECTION_KEYNAME = "DIRECTION";
}
