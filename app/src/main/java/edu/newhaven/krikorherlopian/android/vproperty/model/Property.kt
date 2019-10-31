package edu.newhaven.krikorherlopian.android.vproperty.model

import java.io.Serializable

data class Property(
    var houseName: String = "",
    var address: Address = Address(),
    var photoUrl: String = "",
    var email: String? = "",
    var homeFacts: HomeFacts = HomeFacts(),
    var roomDetails: RoomDetails = RoomDetails(),
    var buildingDetails: BuildingDetails = BuildingDetails(),
    var utilityDetails: UtilityDetails = UtilityDetails(),
    var relatedWebsite: String = "",
    var virtualTour: String = "",
    var contactPhone: String = "",
    var id: String = ""
) : Serializable