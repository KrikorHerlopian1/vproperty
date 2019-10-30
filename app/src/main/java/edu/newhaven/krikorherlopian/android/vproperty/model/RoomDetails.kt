package edu.newhaven.krikorherlopian.android.vproperty.model

import java.io.Serializable

data class RoomDetails(
    var appliances: Appliances = Appliances(),
    var floorCovering: FloorCovering = FloorCovering(),
    var rooms: Room = Room()
) : Serializable