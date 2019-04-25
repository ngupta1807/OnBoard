package com.sample.app.util

import android.util.Base64
import com.google.gson.Gson
import com.sample.app.param.Authenticated
import org.apache.http.NameValuePair
import org.apache.http.message.BasicNameValuePair
import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

class APICall{
     fun postRequest( requestURL: String): String {
         lateinit var response: String
         lateinit var urlConnection:HttpURLConnection
         var params = ArrayList<NameValuePair>();
         try {
            val urlApiKey = URLEncoder.encode(ConstantParam.CONSUMER_KEY, "UTF-8")
            val urlApiSecret = URLEncoder.encode(ConstantParam.CONSUMER_SECRET, "UTF-8")
            params.add( BasicNameValuePair(ConstantParam.AUTHPARAMKEY, ConstantParam.AUTHPARAMVALUE));
            val combined = urlApiKey+":"+urlApiSecret
            val base64Encoded = Base64.encodeToString(combined.toByteArray(), Base64.NO_WRAP) // Base64 encode the stringconnection = URL(requestURL).openConnection() as HttpURLConnection
             urlConnection = URL(requestURL).openConnection() as HttpURLConnection
             urlConnection.addRequestProperty("Authorization", "Basic "+ base64Encoded)
             urlConnection.addRequestProperty(ConstantParam.REQUESTKEY, ConstantParam.REQUESTVALUEPOST)
             urlConnection!!.setConnectTimeout(ConstantParam.timeOut)
             urlConnection!!.setRequestMethod(ConstantParam.POST)
             urlConnection!!.setDoInput(true)
             urlConnection!!.setDoOutput(true)
            val os = urlConnection!!.getOutputStream()
            val writer = BufferedWriter(OutputStreamWriter(os, "UTF-8"))
            writer.write(getQuery(params))
            writer.flush()
            writer.close()
            os.close()
            response = readInputStreamToString(urlConnection)
         } catch (e: Exception) {
             e.printStackTrace()
         } finally {
             urlConnection?.disconnect()
         }
        return response
    }

    fun getData(requestURL: String,auth:String):String{
        lateinit var response: String
        lateinit var urlConnection:HttpURLConnection
        try {
            urlConnection = URL(requestURL).openConnection() as HttpURLConnection
            urlConnection.addRequestProperty("Authorization", "Bearer " + auth);
            urlConnection.addRequestProperty(ConstantParam.REQUESTKEY, ConstantParam.REQUESTVALUEGET);
            response = readInputStreamToString(urlConnection)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            urlConnection?.disconnect()
        }
        return response
    }


    fun getQuery( params : List<NameValuePair>) : String {
        var result = StringBuilder()
        var first = true
        for (pairs : NameValuePair in params) {
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(pairs.getName(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(pairs.getValue(), "UTF-8"));
        }
        return result.toString();
    }


    private fun readInputStreamToString(connection: HttpURLConnection): String {
        val sb = StringBuffer()
        var `is`: InputStream? = null
        lateinit var result: String
        try {
            val statusCode = connection.responseCode
            if (statusCode >= 200 && statusCode < 400) {
                `is` = connection.inputStream
            } else {
                `is` = connection.errorStream
            }
            val br = BufferedReader(InputStreamReader(`is`!!))
            for (inputLine in br.readLine()) {
                sb.append(inputLine)
            }
            result = sb.toString()
        } catch (e: Exception) {
            result = ""
        } finally {
            if (`is` != null) {
                try {
                    `is`.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
        return result
    }

    // convert a JSON authentication object into an Authenticated object
    fun jsonToAuthenticated(rawAuthorization: String?): Authenticated? {
        var auth: Authenticated? = null
        if (rawAuthorization != null && rawAuthorization.length > 0) {
            try {
                val gson = Gson()
                auth = gson.fromJson<Authenticated>(rawAuthorization, Authenticated::class.java)
            } catch (ex: IllegalStateException) {
            }

        }
        return auth
    }



}