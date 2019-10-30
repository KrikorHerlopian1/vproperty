package edu.newhaven.krikorherlopian.android.vproperty.model

data class HeatingType(
    var baseboard: Boolean = false, var forcedAir: Boolean = false,
    var geoThermal: Boolean = false, var heatPump: Boolean = false,
    var radiant: Boolean = false, var stove: Boolean = false,
    var wall: Boolean = false, var other: Boolean = false
)