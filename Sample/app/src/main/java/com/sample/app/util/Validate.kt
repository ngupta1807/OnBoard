package com.sample.app.util

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
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.sample.app.R

import java.util.regex.Matcher
import java.util.regex.Pattern

// TODO: Auto-generated Javadoc

/**
 * The Class Validate.
 */
class Validate(var context : Context) {
    /**
     * Checks if is valid name.
     *
     * @param strName the textvalue
     * @param name the edittext
     * @return true, if is valid email
     */
    fun isValidName(strName: String, name: EditText, type : String): Boolean {
        if (strName.length <= 0) {
            if(type.equals("HashTag"))
                name.error = "Please enter hashtag before submit"
            else
                name.error = "Please enter badword before submit"
            name.isFocusable = true
            name.requestFocus()
            return false
        }
        return true
    }
}
