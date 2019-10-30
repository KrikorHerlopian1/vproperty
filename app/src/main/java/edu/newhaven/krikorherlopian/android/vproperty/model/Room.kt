package edu.newhaven.krikorherlopian.android.vproperty.model

import java.io.Serializable

data class Room(
    var breakfast: Boolean = false, var dinning: Boolean = false,
    var family: Boolean = false, var laundry: Boolean = false,
    var library: Boolean = false, var masterBath: Boolean = false,
    var mud: Boolean = false, var office: Boolean = false,
    var pantry: Boolean = false, var recreation: Boolean = false,
    var workshop: Boolean = false, var solarium: Boolean = false,
    var sun: Boolean = false, var walkInCloset: Boolean = false
) : Serializable