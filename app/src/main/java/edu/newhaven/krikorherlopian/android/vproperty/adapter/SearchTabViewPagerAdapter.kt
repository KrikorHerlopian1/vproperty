package edu.newhaven.krikorherlopian.android.vproperty.adapter

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import edu.newhaven.krikorherlopian.android.vproperty.R
import edu.newhaven.krikorherlopian.android.vproperty.fragment.SearchListPropertyFragment
import edu.newhaven.krikorherlopian.android.vproperty.model.Property


private val TAB_TITLES = arrayOf(
    R.string.all,
    R.string.by_distance,
    R.string.by_price
)
private val TAB_VALUES = arrayOf(
    "ALL",
    "distance",
    "price"
)

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
class SearchTabViewPagerAdapter(
    private val context: Context,
    fm: FragmentManager,
    var param: Property,
    var min: String,
    var max: String
) :
    FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).
        return SearchListPropertyFragment.newInstance(
            position + 1,
            TAB_VALUES[position],
            param,
            min,
            max
        )
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return context.resources.getString(TAB_TITLES[position])
    }

    override fun getCount(): Int {
        return 3
    }
}