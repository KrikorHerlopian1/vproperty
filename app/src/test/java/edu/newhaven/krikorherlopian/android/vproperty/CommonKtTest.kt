package edu.newhaven.krikorherlopian.android.vproperty

import edu.newhaven.krikorherlopian.android.vproperty.model.Address
import edu.newhaven.krikorherlopian.android.vproperty.model.Property
import org.junit.Assert.assertEquals
import org.junit.Test

class CommonKtTest {

    @Test
    fun sortByDistance_test_success() {
        var newYork = Property(
            "NY House",
            address = Address(longitude = "-73.986572", latitude = "40.742748")
        )
        var newHaven = Property(
            "NH House",
            address = Address(longitude = "-72.928080", latitude = "41.304670")
        )
        var losAngeles =
            Property("LA House", address = Address(longitude = "-118.496475", latitude = "34.0522"))
        var boston = Property(
            "Boston House",
            address = Address(longitude = "-71.057083", latitude = "42.361145")
        )
        val arizona =
            Property("AZ House", address = Address(longitude = "-111.0937", latitude = "34.0489"))
        val florida =
            Property("FL House", address = Address(longitude = "-81.5158", latitude = "27.6648"))

        val propertyListActual: MutableList<Any> = mutableListOf(
            newYork,
            newHaven,
            losAngeles,
            boston,
            arizona,
            florida
        )
        val propertyListExpected: MutableList<Any> = mutableListOf(
            newHaven,
            newYork,
            boston,
            florida,
            arizona,
            losAngeles
        )
        //my current location 300 boston post road new haven connecticut
        //41.287160 latitude, -72.657680 longitude
        val result = sortByDistance(propertyListActual, "-72.657680", "41.287160 ")
        assertEquals(propertyListExpected.toList(), propertyListActual.toList())
    }

    @Test
    fun sortByDistance_test_fail() {
        var newYork = Property(
            "NY House",
            address = Address(longitude = "-73.986572", latitude = "40.742748")
        )
        var newHaven = Property(
            "NH House",
            address = Address(longitude = "-72.928080", latitude = "41.304670")
        )
        var losAngeles =
            Property("LA House", address = Address(longitude = "-118.496475", latitude = "34.0522"))
        var boston = Property(
            "Boston House",
            address = Address(longitude = "-71.057083", latitude = "42.361145")
        )
        val arizona =
            Property("AZ House", address = Address(longitude = "-111.0937", latitude = "34.0489"))
        val florida =
            Property("FL House", address = Address(longitude = "-81.5158", latitude = "27.6648"))

        val propertyListActual: MutableList<Any> = mutableListOf(
            newYork,
            newHaven,
            losAngeles,
            boston,
            arizona,
            florida
        )
        val propertyListExpected: MutableList<Any> = mutableListOf(
            newYork,
            newHaven,
            losAngeles,
            boston,
            arizona,
            florida
        )
        //my current location 300 boston post road new haven connecticut
        //41.287160 latitude, -72.657680 longitude
        val result = sortByDistance(propertyListActual, "-72.657680", "41.287160 ")
        assertEquals(propertyListExpected.toList(), propertyListActual.toList())
    }
}