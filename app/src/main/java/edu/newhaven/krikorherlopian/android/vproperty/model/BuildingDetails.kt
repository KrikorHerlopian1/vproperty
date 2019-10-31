package edu.newhaven.krikorherlopian.android.vproperty.model


import java.io.Serializable

data class BuildingDetails(
    var exterior: Exterior = Exterior(),
    var buildingAminities: BuildingAminities = BuildingAminities(),
    var outdoorAminities: OutdoorAminities = OutdoorAminities(),
    var parking: Parking = Parking()
) : Serializable