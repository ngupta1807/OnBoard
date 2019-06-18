package com.tipbox.app.util

/**
 * Validate.java
 *
 *
 * This class is used to validate  the fields that user enters in the fields
 *
 * @category Contus
 * @package com.nuno.com.utils
 * @version 1.0
 * @author Contus Team <developers></developers>@contus.in>
 * @copyright Copyright (C) 2015 Contus. All rights reserved.
 * @license http://www.apache.org/licenses/LICENSE-2.0
 */

import android.content.Context
import android.graphics.drawable.Drawable
import android.widget.EditText
import android.widget.Toast
import com.tipbox.app.R
import java.util.regex.Matcher
import java.util.regex.Pattern



// TODO: Auto-generated Javadoc

/**
 * The Class Validate.
 */
class Validate(val context : Context) {

    /** The pattern.  */
    private var pattern: Pattern? = null

    /** The matcher.  */
    private var matcher: Matcher? = null

    var icon = context.getResources().getDrawable(R.drawable.zd_date);

     companion object {

         /** The regex ddmmyyyy.  */
         private val regexDDMMYYYY = "^(0[1-9]|[12][0-9]|3[01])[- /.](0[1-9]|1[012])[- /.](19|20)\\d\\d$"

         /** The Constant NUMBER_ONLY.  */
         private val NUMBER_ONLY = "^[0-9]{0,11}$"

         /** The Constant ALPHA_NUM_PATTERN.  */
         private val ALPHA_NUM_PATTERN = "[\\p{Alnum} \\. \\,']*"

         /** The Constant PHONE_NO_PATTERN.  */
         private val PHONE_NO_PATTERN = "^[0-9]{10}$"

         /** The Constant EMAIL_PATTERN.  */
         private val EMAIL_PATTERN =
             "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$"

         /** The Constant PASWD_PATTERN.  */
         private val PASWD_PATTERN = "((?=.*[a-z])(?=.*\\d)(?=.*[A-Z])(?=.*[@#$%!]).{8,40})"

         /** The Constant CANADA_POSTALCODE.  */
         private val CANADA_POSTALCODE = "^([A-Za-z]\\d[A-Za-z]\\d[A-Za-z]\\d)$"

     }

    /**
     * Validate field.
     *
     * @param key the key
     * @param str the str
     * @return the boolean
     */
    fun validateField(key: Int, str: String): Boolean {
        when (key) {
            0 // Validate character
            -> pattern = Pattern.compile(ALPHA_NUM_PATTERN)
            1 // Validate Phone Number Formats: 1234567890
            -> pattern = Pattern.compile(PHONE_NO_PATTERN)
            2 // Validate Email Formats: xxx@xx.com
            -> pattern = Pattern.compile(EMAIL_PATTERN)
            3 // Validate only number
            -> pattern = Pattern.compile(NUMBER_ONLY)
            4 // Validate zd_date
            -> pattern = Pattern.compile(regexDDMMYYYY)
            5 // Validate password
            -> pattern = Pattern.compile(PASWD_PATTERN)
            6 // Validate postal code
            -> pattern = Pattern.compile(CANADA_POSTALCODE)
            else -> pattern = Pattern.compile(ALPHA_NUM_PATTERN)
        }

        matcher = pattern!!.matcher(str)
        return matcher!!.matches()
    }


    /**
     * Checks if is valid first name.
     *
     * @param strFirstName the str first name
     * @param first_name   the first name
     * @return true, if is valid first name
     */
    fun isValidName(strFirstName: String, first_name: EditText,tag: String): Boolean {

        if (strFirstName.length <= 0) {
            first_name.setError("Please enter " +tag,setIcon())
            first_name.isFocusable = true
            first_name.requestFocus()
            return false
        }
        return true
    }



    /**
     * Checks if isValidBankName.
     * @return true, if is valid  name
     */
    fun isValidBankName(strFirstName: String, first_name: EditText,tag: String): Boolean {

        if (strFirstName.length <= 0) {
            first_name.setError("Please enter " +tag,setIcon())
            first_name.isFocusable = true
            first_name.requestFocus()
            return false
        } else if (strFirstName.length < 10) {
            first_name.setError("Please enter a valid "+tag,setIcon())
            first_name.isFocusable = true
            first_name.requestFocus()
            return false
        }
        return true
    }
    /**
     * Checks if isValidBankName.
     * @return true, if is valid  name
     */
    fun isValidSwiftCode(strFirstName: String, first_name: EditText,tag: String): Boolean {

        if (strFirstName.length <= 0) {
            first_name.setError("Please enter " +tag,setIcon())
            first_name.isFocusable = true
            first_name.requestFocus()
            return false
        } else if (strFirstName.length < 8) {
            first_name.setError("Please enter a valid swift code",setIcon())
            first_name.isFocusable = true
            first_name.requestFocus()
            return false
        }
        return true
    }


    /**
     * Checks if isValidMobile.
     *
     * @param mobileno the mobileno
     * @param mobile   the mobile
     * @return true, if is valid first name
     */
    fun isValidMobile(mobileno: String, mobile: EditText): Boolean {
        if (mobileno.length <= 0) {
            mobile.setError("Please enter your phone number",setIcon())
            mobile.isFocusable = true
            mobile.requestFocus()
            return false
        } else if (mobileno.length < 13) {
            mobile.setError("Please enter a valid phone number",setIcon())
            mobile.isFocusable = true
            mobile.requestFocus()
            return false
        }
        return true
    }


    fun isValidImage(savedImage: String,mcon:Context?): Boolean {
        if(savedImage.equals("")) {
            Toast.makeText(mcon, "Please choose profile image", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    /**
     * Is valid password boolean.
     *
     * @param strPassword the str password
     * @param password    the password
     * @return the boolean
     */
    fun isValidPassword(strPassword: String, password: EditText): Boolean {
        if (strPassword.length <= 0) {
            password.setError("Please enter your password",setIcon())
            password.setText("")
            password.isFocusable = true
            password.requestFocus()
            return false
        } else if (strPassword.length < 4) {
            password.setError("Password should be minimum 4 characters long",setIcon())
            password.isFocusable = true
            password.requestFocus()
            return false
        }
        return true
    }


    /**
     * Is valid password boolean.
     *
     * @param strPassword the str password
     * @param password    the password
     * @return the boolean
     */
    fun isValidNewPassword(strPassword: String, password: EditText): Boolean {
        if (strPassword.length <= 0) {
            password.setError("Please enter new password",setIcon())
            password.setText("")
            password.isFocusable = true
            password.requestFocus()
            return false
        } else if (strPassword.length < 4) {
            password.setError("Password should be minimum 4 characters long",setIcon())
            password.isFocusable = true
            password.requestFocus()
            return false
        }
        return true
    }


    fun isValidNewOldPassword(strPassword: String, password: EditText,stroldPassword: String): Boolean {
        if (strPassword.equals(stroldPassword)) {
            password.setError("New password and old password cannot be same",setIcon())
            password.setText("")
            password.isFocusable = true
            password.requestFocus()
            return false
        }
        return true
    }


    /**
     * Is valid old password boolean.
     *
     * @param strPassword the str password
     * @param password    the password
     * @return the boolean
     */
    fun isValidOldPassword(strPassword: String, password: EditText): Boolean {
        if (strPassword.length <= 0) {
            password.setError("Please enter old password",setIcon())
            password.setText("")
            password.isFocusable = true
            password.requestFocus()
            return false
        }else if (strPassword.length < 4) {
            password.setError("Password should be minimum 4 characters long",setIcon())
            password.isFocusable = true
            password.requestFocus()
            return false
        }
        return true
    }

    /**
     * Is valid confirm password boolean.
     *
     * @param strConfirmPassword the str confirm password
     * @param confirm_password   the confirm password
     * @return the boolean
     */
    fun isValidConfirmPassword(strConfirmPassword: String, confirm_password: EditText,strPassword: String): Boolean {
        if (strConfirmPassword.length <= 0) {
            confirm_password.setError("Please re-enter your password",setIcon())
            confirm_password.setText("")
            confirm_password.isFocusable = true
            confirm_password.requestFocus()
            return false
        } else if (strConfirmPassword.length < 4) {
            confirm_password.setError("Password should be minimum 4 characters long",setIcon())
            confirm_password.isFocusable = true
            confirm_password.requestFocus()
            return false
        }else if(!strConfirmPassword.equals(strPassword)){
            confirm_password.setError("Passwords doesn't match",setIcon())
            confirm_password.isFocusable = true
            confirm_password.requestFocus()
            return false
        }
        return true
    }


    /**
     * Checks if is valid email.
     *
     * @param strEmail the str email
     * @param username the username
     * @return true, if is valid email
     */
    fun isValidEmail(strEmail: String, username: EditText): Boolean {
        if (strEmail.length <= 0) {
            username.setError("Please enter an email address",setIcon())
            username.isFocusable = true
            username.requestFocus()
            return false
        } else if (!(validateField(2, strEmail))) {
            username.setError("Please enter a valid email address",setIcon())
            username.isFocusable = true
            username.requestFocus()
            return false
        }
        return true
    }
    fun setIcon(): Drawable? {
        icon.setBounds(0, 0,
            30,
            30)
        return icon
    }

    /**
     * Checks if isValidBankName.
     * @return true, if is valid  name
     */
    fun isValidAmount(strFirstName: String, first_name: EditText,tag: String): Boolean {
        if (Integer.valueOf(strFirstName) >100) {
            first_name.setError("Please enter amount less than 100$ ",setIcon())
            first_name.isFocusable = true
            first_name.requestFocus()
            return false
        }
        return true
    }


}
