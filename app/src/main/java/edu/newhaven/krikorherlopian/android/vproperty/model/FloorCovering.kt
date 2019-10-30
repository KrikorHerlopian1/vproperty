package edu.newhaven.krikorherlopian.android.vproperty.model


data class FloorCovering(
    var carpet: Boolean = false, var concrete: Boolean = false,
    var hardwood: Boolean = false, var laminate: Boolean = false,
    var slate: Boolean = false, var softwood: Boolean = false,
    var linoleum: Boolean = false, var tile: Boolean = false,
    var other: Boolean = false
)