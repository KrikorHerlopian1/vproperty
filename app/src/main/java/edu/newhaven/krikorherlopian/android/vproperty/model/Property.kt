package edu.newhaven.krikorherlopian.android.vproperty.model

import com.google.firebase.firestore.Exclude
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
    var id: String = "",
    var isDisabled: String = "N",
    @Exclude @set:Exclude @get:Exclude var distance: Double = 0.0
) : Serializable