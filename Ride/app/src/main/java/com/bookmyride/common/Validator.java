package com.bookmyride.common;

import android.util.Patterns;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

/**
 * Created by vinod on 1/5/2017.
 */
public class Validator {
    public static boolean isEmailValid(String str_newEmail) {
        /*return str_newEmail.matches("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");*/
        Pattern pattern = Patterns.EMAIL_ADDRESS;
        return pattern.matcher(str_newEmail).matches();
    }
    public static boolean isValidDate(String pDateString)  {
        Date date = null;
        try {
            date = new SimpleDateFormat("yyyy-MM-dd").parse(pDateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return new Date().before(date);
    }
}
