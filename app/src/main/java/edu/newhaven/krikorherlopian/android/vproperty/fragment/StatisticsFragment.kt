package edu.newhaven.krikorherlopian.android.vproperty.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayout
import edu.newhaven.krikorherlopian.android.vproperty.R
import edu.newhaven.krikorherlopian.android.vproperty.adapter.StatisticsAdapter
import kotlinx.android.synthetic.main.statistics_fragment.view.*

class StatisticsFragment : Fragment() {
    var root: View? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.statistics_fragment, container, false)
        val statisticsAdapter =
            StatisticsAdapter(activity!!, childFragmentManager)
        root?.view_pager?.adapter = statisticsAdapter
        root?.tabs?.setupWithViewPager(root?.view_pager!!)
        root!!.tabs?.tabMode = TabLayout.MODE_FIXED
        return root
    }
}