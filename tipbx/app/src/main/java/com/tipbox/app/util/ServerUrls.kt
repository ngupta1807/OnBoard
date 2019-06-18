package com.tipbox.app.util

object ServerUrls {
   // var BASE_URL = "https://uzl915hnkh.execute-api.ap-south-1.amazonaws.com/dev/"
    var BASE_URL = "http://ec2-13-235-67-170.ap-south-1.compute.amazonaws.com:8080/"

    const val REGISTOR: String = "signup"
    const val LOGIN = "login"
    const val USERPROFILE = "profile"
    //const val CHANGEPASSWORD = "changepassword"
    const val CHANGEPASSWORD = "changePassword"
    //const val FORGOT = "forgetpassword"
    const val FORGOT = "recoverPassword"
    const val DASHBOARD = "dashboard"
    const val NOTIFICATION = "notifications"
    //const val PAYHISTORY = "paymenthistory"
    const val PAYHISTORY = "getPaymentHistory"
    //const val BANKDETAIL = "bankaccount"
    const val BANKDETAIL = "accountDetails"
    const val SWIPESETTING = "swipesetting"
    const val PERFORMERPROFILE = "performer"
    //const val EDITPROFILE = "editprofileimage"
    const val EDITPROFILE = "editProfileImage"
    const val TRANSFERAMOUNT = "transferamount"
    const val APPLOGO = "applogo"
    const val LOGOUT = "logout"

    const val SET_TOKEN = "setToken"
    var AUTH = "authCode"
    var USERID = "userId"
    var USER = "user"
    var SWIPEAMT = "swipe_amt"
    var SESSION = "session"
    var INFO = "info_session"
    var ACCOUNTNUMBER = "accountNumber"
    //var ACCOUNTNUMBER = "bankAccountNumber"

   var IS_LOGGED_IN = ""
   var AUTHORIZATION_HEADER = ""
   var AUTHORIZATION_USERID = ""
   var USER_ID = ""

   var NOTIFICATIONDATA = "NotificationsList"
   var PAYMENTHISTORYDATA = "PaymentHistory"

   var STATUS="status"

}
