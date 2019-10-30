package edu.newhaven.krikorherlopian.android.vproperty.model

import java.io.Serializable

data class OutdoorAminities(
    var balconyPatio: Boolean = false, var lawn: Boolean = false,
    var barbecueArea: Boolean = false, var pond: Boolean = false,
    var deck: Boolean = false, var pool: Boolean = false,
    var dock: Boolean = false, var porch: Boolean = false,
    var fencedYard: Boolean = false, var rvParking: Boolean = false,
    var garden: Boolean = false, var sauna: Boolean = false,
    var greenHouse: Boolean = false, var sprinkerlSystem: Boolean = false,
    var hotTubSpa: Boolean = false, var waterfront: Boolean = false
) : Serializable