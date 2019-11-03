package edu.newhaven.krikorherlopian.android.vproperty.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import edu.newhaven.krikorherlopian.android.vproperty.R
import edu.newhaven.krikorherlopian.android.vproperty.adapter.PropertiesTabViewPagerAdapter
import kotlinx.android.synthetic.main.list_properties_fragment.view.*

class ViewPagerPropertiesFragment : Fragment() {
    var root: View? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.list_properties_fragment, container, false)
        val propertiesTabViewPagerAdapter =
            PropertiesTabViewPagerAdapter(activity!!, childFragmentManager)
        root?.view_pager?.adapter = propertiesTabViewPagerAdapter
        root?.tabs?.setupWithViewPager(root?.view_pager!!)
        return root
    }
}