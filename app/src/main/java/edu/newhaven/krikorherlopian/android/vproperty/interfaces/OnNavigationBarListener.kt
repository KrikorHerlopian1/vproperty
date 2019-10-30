package edu.newhaven.krikorherlopian.android.vproperty.interfaces

import android.graphics.Bitmap
import edu.newhaven.krikorherlopian.android.vproperty.model.BuildingDetails
import edu.newhaven.krikorherlopian.android.vproperty.model.RoomDetails
import edu.newhaven.krikorherlopian.android.vproperty.model.UtilityDetails

interface OnNavigationBarListener {
    fun addHomeType(typeCode: String)
    fun addAddress(
        displayName: String, address: String, zipCode: String,
        longitude: String, latitude: String, descriptionAddress: String
    )

    fun addHomeFacts(
        price: String, isRent: Boolean, isSale: Boolean,
        bedrooms: String, bathrooms: String, totalRooms: String,
        parkingSpaces: String, yearBuilt: String, hoadues: String,
        structuralModalYear: String, floorNumber: String, finishedSqFt: String,
        lotSizeFqFt: String, basementSqFt: String, garageSqFt: String
    )

    fun addRoomDetails(room: RoomDetails)
    fun addBuildingDetails(buildingDetails: BuildingDetails)
    fun addUtilityDetails(utilityDetails: UtilityDetails)
    fun addPictureClicked()
    fun finishStep(
        relatedWebsite: String,
        virtualTour: String,
        contactinput: String,
        bitmap: Bitmap
    )
}