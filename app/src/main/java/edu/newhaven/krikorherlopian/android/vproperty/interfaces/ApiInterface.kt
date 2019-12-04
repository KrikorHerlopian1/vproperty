package edu.newhaven.krikorherlopian.android.vproperty.interfaces

import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface ApiInterface {
    @Headers(
        "Authorization: key=AIzaSyCzlg3ksM5x3l4GoyR3f7TKQlHEqirlmtw\n",
        "Content-Type:application/json"
    )
    @POST("fcm/send")
    fun sendChatNotification(@Body requestBody: RequestBody): Call<com.squareup.okhttp.ResponseBody>

    @GET("GetSearchResults.htm")
    fun zestimate(
        @Query(encoded = true, value = "zws-id") zwsid: String,
        @Query(encoded = true, value = "address") address: String,
        @Query(encoded = true, value = "citystatezip") citystatezip: String,
        @Query(encoded = true, value = "rentzestimate") rentzestimate: String
    ): Call<okhttp3.ResponseBody>
}

