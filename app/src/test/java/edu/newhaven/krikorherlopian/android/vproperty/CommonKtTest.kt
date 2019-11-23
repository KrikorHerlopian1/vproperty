package edu.newhaven.krikorherlopian.android.vproperty

import edu.newhaven.krikorherlopian.android.vproperty.model.Address
import edu.newhaven.krikorherlopian.android.vproperty.model.Property
import org.junit.Assert.assertEquals
import org.junit.Test

class CommonKtTest {

    @Test
    fun sortByDistance_test() {
        var newYork = Address(longitude = "-73.986572", latitude = "40.742748")
        var newHaven = Address(longitude = "-72.928080", latitude = "41.304670")
        var losAngeles = Address(longitude = "-118.496475", latitude = "34.0522")
        var boston = Address(longitude = "-71.057083", latitude = "42.361145")
        val arizona = Address(longitude = "-111.0937", latitude = "34.0489")
        val florida = Address(longitude = "-81.5158", latitude = "27.6648")

        val propertyListActual: MutableList<Any> = mutableListOf(
            Property("NY House", newYork),
            Property("NH House", newHaven),
            Property("Florida House", florida),
            Property("LA House", losAngeles),
            Property("BOSTON House", boston),
            Property("AZ House", arizona)
        )
        val propertyListExpected: MutableList<Any> = mutableListOf(
            Property("NH House", newHaven, distance = 14.088658705148184),
            Property("NY House", newYork, distance = 78.83144221662072),
            Property("BOSTON House", boston, distance = 110.89273864552389),
            Property("Florida House", florida, distance = 1066.4791337229924),
            Property("AZ House", arizona, distance = 2142.277482099129),
            Property("LA House", losAngeles, distance = 2526.2674284586433)
        )
        //my current location 300 boston post road new haven connecticut
        //41.287160 latitude, -72.657680 longitude
        val result = sortByDistance(propertyListActual, "-72.657680", "41.287160 ")
        assertEquals(propertyListExpected.toList(), propertyListActual.toList())
    }
}