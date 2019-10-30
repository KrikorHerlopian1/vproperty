package edu.newhaven.krikorherlopian.android.vproperty.model

import java.io.Serializable


data class HomeTypes(
    var typeCode: String,
    var type: String,
    var selected: Int = 0
) : Serializable