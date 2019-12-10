package edu.newhaven.krikorherlopian.android.vproperty.model

import com.google.gson.annotations.SerializedName


class RequestNotificaton {

    @SerializedName("token")
    var token: String? = null

    @SerializedName("notification")
    var sendNotificationModel: SendNotificationModel? = null
}