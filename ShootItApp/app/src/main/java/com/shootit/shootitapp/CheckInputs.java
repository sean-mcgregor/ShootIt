package com.shootit.shootitapp;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

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

        Pattern pattern = Pattern.compile("[\\s\\S]{5,30}$");
        Matcher matcher = pattern.matcher(input);

        if (matcher.find()){

            Log.d("Description", "isValid");
            return true;
        } else {

            Log.d("Description", "invalid");
            return false;
        }
    }

    public static boolean isValidDescription(String input) {

        Pattern pattern = Pattern.compile("[\\s\\S]{5,400}$");
        Matcher matcher = pattern.matcher(input);

        if (matcher.find()){

            Log.d("Description", "isValid");
            return true;
        } else {

            Log.d("Description", "invalid");
            return false;
        }
    }

    public static boolean markerHasMoved(LatLng startPosition, LatLng locationCoords) {

        if (startPosition.latitude == locationCoords.latitude && startPosition.longitude == locationCoords.longitude) {

            return false;
        } else {

            return true;
        }
    }
}
