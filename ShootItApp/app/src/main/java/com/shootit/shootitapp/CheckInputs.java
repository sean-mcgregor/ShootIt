package com.shootit.shootitapp;

import android.util.Log;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CheckInputs {

    public static boolean isValidEmail (String input) {

        return false;
    }

    public static boolean isValidUsername (String input) {

        Pattern pattern = Pattern.compile("^(?=[a-zA-Z0-9._]{8,20}$)(?!.*[_.]{2})[^_.].*[^_.]$");
        Matcher matcher = pattern.matcher(input);

        if (matcher.find()){

            Log.d("Username", "isValid");
            return true;
        } else {

            Log.d("Username", "invalid");
            return false;
        }
    }

    public static boolean isValidPlaceTitle (String input) {

        return false;
    }

    public static boolean isValidDescription(String input) {

        return false;
    }
}
