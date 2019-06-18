package com.tipbox.app.restclient;

/**
 * Poaservice.java
 * This class is used to load the contact user's wall data.
 *
 * @author Contus Team <developers@contus.in>
 * @version 1.0
 * @category Contus
 * @package com.poaap.chatpageview
 * @copyright Copyright (C) 2015 Contus. All rights reserved.
 * @license http ://www.apache.org/licenses/LICENSE-2.0
 */




import com.tipbox.app.util.ServerUrls;
import retrofit2.Call;
import retrofit2.http.*;

import java.util.HashMap;

/**
 * Created by user on 9/29/2015.
 * */
public interface Poaservice {



    @POST(ServerUrls.REGISTOR)
    Call<Object> createUser(@Body HashMap<String, String> data);

    @POST(ServerUrls.LOGIN)
    Call<Object> ac_login(@Body HashMap<String, String> logindata);

    @POST(ServerUrls.FORGOT)
    Call<Object> ac_forgot(@Body HashMap<String, String> data);

    @POST(ServerUrls.CHANGEPASSWORD)
    Call<Object> changePassword(@Body HashMap<String, String> data,@Query(value = "userId", encoded = true) String userId);

    @GET(ServerUrls.USERPROFILE)
    Call<Object> viewProfile(@Query(value = "userId", encoded = true) String userId);

    @POST(ServerUrls.USERPROFILE)
    Call<Object> updateUser(@Body HashMap<String, String> data,@Query(value = "userId", encoded = true) String userId);

    @POST(ServerUrls.LOGOUT)
    Call<Object> logout(@Body HashMap<String, String> data,@Query(value = "userId", encoded = true) String userId);

    @POST(ServerUrls.EDITPROFILE)
    Call<Object> updateProfileImage(@Body HashMap<String, String> data,@Query(value = "userId", encoded = true) String userId);

    @GET(ServerUrls.DASHBOARD)
    Call<Object> viewDashboard(@Query(value = "userId", encoded = true) String userId);

    @GET(ServerUrls.NOTIFICATION)
    Call<Object> viewNotification(@Query(value = "userId", encoded = true) String userId);

    @POST(ServerUrls.BANKDETAIL)
    Call<Object> saveBankDetail(@Body HashMap<String, String> data,@Query(value = "userId", encoded = true) String userId);

    @PUT(ServerUrls.BANKDETAIL)
    Call<Object> putBankDetail(@Body HashMap<String, String> data,@Query(value = "userId", encoded = true) String userId);

    @GET(ServerUrls.BANKDETAIL)
    Call<Object> getBankDetail(@Query(value = "userId", encoded = true) String userId);


    @GET(ServerUrls.SWIPESETTING)
    Call<Object> viewSwipeSetting(@Query(value = "userId", encoded = true) String userId);

    @GET(ServerUrls.PERFORMERPROFILE)
    Call<Object> viewPerformerDetail(@Query(value = "userId", encoded = true) String userId);

    @POST(ServerUrls.TRANSFERAMOUNT)
    Call<Object> transferAmount(@Body HashMap<String, String> data,@Query(value = "userId", encoded = true) String userId);


    @POST(ServerUrls.SWIPESETTING)
    Call<Object> updateSwipeSetting(@Body HashMap<String, String> data,@Query(value = "userId", encoded = true) String userId);

    @GET(ServerUrls.PAYHISTORY)
    Call<Object> viewPayHistory(@Query(value = "userId", encoded = true) String userId);

    /*@FormUrlEncodedTRANSFERAMOUNT
    @POST(ServerUrls.UPDATE)
    Call<Object> updateUser(@FieldMap HashMap<String, String> user);

    @POST(ServerUrls.LOGOUT)
    Call<Object> logout();


    @GET(ServerUrls.VIEWLOC)
    Call<Object> viewLoc();

    @FormUrlEncoded
    @POST(ServerUrls.FINDRIDE)
    Call<Object> findRide(@Field("current_latitude") String latitude, @Field("current_longitude") String longitude);

    @FormUrlEncoded
    @POST(ServerUrls.SAVELOC)
    Call<Object> saveLocation(@Field("location_name") String location_name, @Field("address") String address,
                              @Field("latitude") String latitude, @Field("longitude") String longitude);

    @GET(ServerUrls.ALLRIDE)
    Call<Object> viewRides();*/

}
