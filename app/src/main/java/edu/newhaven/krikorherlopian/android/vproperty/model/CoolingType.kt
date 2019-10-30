package edu.newhaven.krikorherlopian.android.vproperty.model

import java.io.Serializable

data class CoolingType(
    var central: Boolean = false, var evaporative: Boolean = false,
    var geoThermal: Boolean = false, var refrigeration: Boolean = false,
    var solar: Boolean = false, var wall: Boolean = false,
    var other: Boolean = false, var none: Boolean = false
) : Serializable