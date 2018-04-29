package com.example.kotak.shaban_new_1;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateClass {

	public static String changeDateFormat(String d)
	{
		SimpleDateFormat format = new SimpleDateFormat(
				"yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
		Date date=null;
		try {
				date = format.parse(d);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();

			return (DateFormat.getDateTimeInstance(DateFormat.MEDIUM
					,DateFormat.SHORT).format(new Date()));
		}
		return (DateFormat.getDateTimeInstance(DateFormat.MEDIUM
				,DateFormat.SHORT).format(date));

	}
}
