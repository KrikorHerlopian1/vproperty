package edu.newhaven.krikorherlopian.android.vproperty.model

data class Parking(
    var carport: Boolean = false, var onStreet: Boolean = false,
    var offStreet: Boolean = false, var garageAttached: Boolean = false,
    var garageDetached: Boolean = false, var none: Boolean = false
)