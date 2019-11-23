package edu.newhaven.krikorherlopian.android.vproperty

import edu.newhaven.krikorherlopian.android.vproperty.model.Address
import edu.newhaven.krikorherlopian.android.vproperty.model.Property
import org.junit.Assert.assertEquals
import org.junit.Test

class CommonKtTest {

    @Test
    fun sortByDistance_test() {


        var newYork = Address("", "", "-73.986572", "40.742748", "")
        var newHaven = Address("", "", "-72.928080", "41.304670")
        var losAngeles = Address("", "", "-118.496475", "34.0522")
        var boston = Address("", "", "-71.057083", "42.361145", "")
        val propertyListActual: MutableList<Any> = mutableListOf(
            Property("house 1", newYork, distance = 78.83144221662072),
            Property("house 2", newHaven, distance = 14.088658705148184),
            Property("house 3", losAngeles, distance = 110.89273864552389),
            Property("house 4", boston, distance = 2526.2674284586433)
        )
        val propertyListExpected: MutableList<Any> = mutableListOf(
            Property("house 2", newHaven, distance = 14.088658705148184),
            Property("house 1", newYork, distance = 78.83144221662072),
            Property("house 4", boston, distance = 110.89273864552389),
            Property("house 3", losAngeles, distance = 2526.2674284586433)
        )
        //my current location 300 boston post road new haven connecticut
        //41.287160 latitude, -72.657680 longitude
        val result = sortByDistance(propertyListActual, "-72.657680", "41.287160 ")
        assertEquals(propertyListExpected.toList(), propertyListActual.toList())
    }
}