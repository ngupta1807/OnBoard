package com.wfi.beaconscanner.API

import com.wfi.beaconscanner.models.LoggingRequest
import io.reactivex.Completable
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Url


/**
 * Created by nisha on 24/08/2017.
 */

interface LoggingService {

    @POST
    fun postLogs(@Url url: String, @Body beacons: LoggingRequest) : Completable
}
