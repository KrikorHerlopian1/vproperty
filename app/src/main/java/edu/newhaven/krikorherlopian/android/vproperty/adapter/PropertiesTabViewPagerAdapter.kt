package edu.newhaven.krikorherlopian.android.vproperty.adapter

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import edu.newhaven.krikorherlopian.android.vproperty.R
import edu.newhaven.krikorherlopian.android.vproperty.fragment.ListPropertiesFragment


private val TAB_TITLES = arrayOf(
    R.string.all,
    R.string.for_sale,
    R.string.for_rent,
    R.string.apartment,
    R.string.single_family,
    R.string.multi_family,
    R.string.condo,
    R.string.town_house,
    R.string.mobile_manufactured,
    R.string.coop_unit,
    R.string.vacant_land,
    R.string.other
)
private val TAB_VALUES = arrayOf(
    "ALL",
    "homeFacts.sale",
    "homeFacts.rent",
    "APT",
    "SIF",
    "MUF",
    "CON",
    "TOW",
    "MOB",
    "COU",
    "VAL",
    "OTH"
)

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
class PropertiesTabViewPagerAdapter(private val context: Context, fm: FragmentManager) :
    FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).
        return ListPropertiesFragment.newInstance(position + 1, TAB_VALUES[position])
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return context.resources.getString(TAB_TITLES[position])
    }

    override fun getCount(): Int {
        return 12
    }
}