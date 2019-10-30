package edu.newhaven.krikorherlopian.android.vproperty.interfaces

import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface ApiInterface {
    @Headers(
        "Authorization: key=AIzaSyCzlg3ksM5x3l4GoyR3f7TKQlHEqirlmtw\n",
        "Content-Type:application/json"
    )
    @POST("fcm/send")
    fun sendChatNotification(@Body requestBody: RequestBody): Call<com.squareup.okhttp.ResponseBody>
}