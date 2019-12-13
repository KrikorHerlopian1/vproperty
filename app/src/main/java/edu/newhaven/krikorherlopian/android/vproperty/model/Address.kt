package edu.newhaven.krikorherlopian.android.vproperty.model

import java.io.Serializable

data class Address(
    var addressName: String = "",
    var zipCode: String = "", var longitude: String = "",
    var latitude: String = "", var descriptionAddress: String = ""
) : Serializable

