package com.sample.app.util


object ConstantParam {
        var CONSUMER_KEY = "yUCvSfrPJ3tvEhD4fmiGmOQzq"
        var CONSUMER_SECRET = "twlslucKMledEmbESYcrBv69FZpVUT1jv1eV07P4Brzj2V13Jx"
        var timeOut = 30000
        var POST = "POST"
        var AUTHPARAMKEY = "grant_type"
        var AUTHPARAMVALUE = "client_credentials"
        var REQUESTKEY = "Content-Type"
        var REQUESTVALUEPOST = "application/x-www-form-urlencoded;charset=UTF-8"
        var REQUESTVALUEGET ="application/json"

        val TwitterTokenURL = "https://api.twitter.com/oauth2/token"
        val TwitterStreamURL = "https://api.twitter.com/1.1/statuses/user_timeline.json?count=10&screen_name="
        val delay = 60000L
}