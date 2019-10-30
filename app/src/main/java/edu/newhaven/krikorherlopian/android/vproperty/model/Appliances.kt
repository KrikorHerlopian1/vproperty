package edu.newhaven.krikorherlopian.android.vproperty.model


data class Appliances(
    var dishWasher: Boolean = false, var dryer: Boolean = false,
    var freezer: Boolean = false, var disposal: Boolean = false,
    var microwave: Boolean = false, var rangeoven: Boolean = false,
    var refrigerator: Boolean = false, var washer: Boolean = false,
    var trashCompactor: Boolean = false
)