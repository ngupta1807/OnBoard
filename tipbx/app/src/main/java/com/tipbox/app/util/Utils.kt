package com.tipbox.app.util

/**
 * Utils.java
 *
 * @category Contus
 * @package com.nuno.com.utils
 * @version 1.0
 * @author Contus Team <developers></developers>@contus.in>
 * @copyright Copyright (C) 2015 Contus. All rights reserved.
 * @license http://www.apache.org/licenses/LICENSE-2.0
 */

import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import android.util.Log


// TODO: Auto-generated Javadoc

/**
 * The Class Utils.
 */
@TargetApi(Build.VERSION_CODES.KITKAT)
class Utils {
    init {

        Log.e("Utils:::", "")
    }

    companion object {

        /**
         * Checking internet state.
         *
         * @param context the context
         * @return true if internet is enabled else false
         */
        fun networkStatus(context: Context): Boolean {
            return NetworkStatusCheker.checkNetConnection(context)
        }
    }


}