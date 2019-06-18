package com.tipbox.app.util

import com.tipbox.app.R

class ExpandableListData {
   companion object {
       open fun getData(type:String): LinkedHashMap<String, List<String>> {
           val expandableListDetail = LinkedHashMap<String, List<String>>()
           val one = ArrayList<String>()


           val basketball = ArrayList<String>()
           basketball.add("Privacy Policy")
           basketball.add("Terms & Condition")
           basketball.add("Help")

           val football = ArrayList<String>()
           football.add("Payment Methods")
           if (type.equals("1")) {
               football.add("Bank Details")
           } else {
               football.add("[Start Receiving Tips]")
           }
           football.add("Sign Out")


         /*  expandableListDetail["Dashboard"]=one
           expandableListDetail["Notification"]=one
           expandableListDetail["Settings"] = football
           expandableListDetail["AboutUs"] = basketball*/


           //expandableListDetail["Dashboard"]=one
           expandableListDetail["Notifications"]=one
           //expandableListDetail["Profile"]=one
           expandableListDetail["Settings"] = football
           expandableListDetail["About Us"] = basketball
           return expandableListDetail
       }
   }
}