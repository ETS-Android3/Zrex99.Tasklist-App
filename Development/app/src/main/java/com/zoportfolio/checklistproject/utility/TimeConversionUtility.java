package com.zoportfolio.checklistproject.utility;

import android.util.Log;

public class TimeConversionUtility {

    private static final String TAG = "TimeConversionUtility.TAG";

    public static int convertStandardHourFormatToMilitaryHourFormat(String _hour, String _meridies) {
        int convertedHour = 0;
        if(_meridies.equals("AM") && _hour.equals("12")) {//If the meridies is AM and the hour is 12, then set the converted hour to 24.
            convertedHour = 24;
        }else if(_meridies.equals("PM") && _hour.equals("12")) {//If the meridies is PM and the hour is 12, set the converted hour to 12.
            convertedHour = 12;
        }else if(_meridies.equals("AM")) {//If the meridies is AM, there is nothing to do.
            convertedHour = Integer.parseInt(_hour);
        }else {//If the meridies is PM, add 12 to the hour.
            convertedHour = 12 + Integer.parseInt(_hour);
        }
        Log.i(TAG, "convertStandardHourFormatToMilitaryHourFormat: converted hour: " + convertedHour);
        return convertedHour;
    }
}
