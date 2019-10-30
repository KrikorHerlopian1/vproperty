package edu.newhaven.krikorherlopian.android.vproperty.model

data class RoomDetails(
    var appliances: Appliances = Appliances(),
    var floorCovering: FloorCovering = FloorCovering(),
    var rooms: Room = Room()
)