package com.bhavnadevani.android.smarttab;

/** a utility class, for static funcions of all sorts*/
public class Utils {

	/**
	 * converts raw dates [YYYYMMDD] to displayable dates
	 * @param rawdate The raw date, in YYYYMMDD format
	 * @return the displayable date, or "invalid date" if there is an error
	 */
	public static String convertRawDateToDisplayDate(String rawdate) {
		try{
		
			String year = rawdate.substring(0, 4);
			String month = rawdate.substring(4, 6);
			String date = rawdate.substring(6, 8);

			//TODO change this to depend on locale, or maybe on an argument. currently it is just US format MM/DD/YYYY
			return month + "/" + date + "/" + year;
			
		} catch (Exception e){
			return "Invalid date. Raw date was " + rawdate + ", exception code " + e.getLocalizedMessage();
		}
	}
	
	

}
