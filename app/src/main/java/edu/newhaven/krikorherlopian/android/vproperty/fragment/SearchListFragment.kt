package edu.newhaven.krikorherlopian.android.vproperty.fragment

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.android.material.tabs.TabLayout
import edu.newhaven.krikorherlopian.android.vproperty.R
import edu.newhaven.krikorherlopian.android.vproperty.adapter.SearchTabViewPagerAdapter
import edu.newhaven.krikorherlopian.android.vproperty.model.Property
import kotlinx.android.synthetic.main.list_properties_fragment.view.*


class SearchListFragment : Fragment() {
    var root: View? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.list_properties_fragment, container, false)
        var param = arguments?.getSerializable(ARG_PARAM) as Property
        val searchTabViewPagerAdapter =
            SearchTabViewPagerAdapter(
                activity!!,
                childFragmentManager,
                param,
                arguments?.getString(ARG_MIN)!!,
                arguments?.getString(
                    ARG_MAX
                )!!
            )
        root?.view_pager?.adapter = searchTabViewPagerAdapter
        root?.tabs?.setupWithViewPager(root?.view_pager!!)
        root?.tabs?.tabMode = TabLayout.MODE_FIXED
        try {
            val handler = Handler()
            handler.postDelayed(object : Runnable {
                override fun run() {
                    try {
                        MobileAds.initialize(activity!!) {
                        }
                        //.addTestDevice("A70D704E7B6A63E4EC37DC500C5F87F2")
                        val adRequest =
                            AdRequest.Builder().addTestDevice("A70D704E7B6A63E4EC37DC500C5F87F2")
                                .build()
                        root?.adView?.loadAd(adRequest)
                        root?.adView?.adListener = object : AdListener() {
                            override fun onAdLoaded() {
                                root?.adView?.visibility = View.VISIBLE
                                // Code to be executed when an ad finishes loading.
                            }

                            override fun onAdFailedToLoad(errorCode: Int) {
                                // Code to be executed when an ad request fails
                            }

                            override fun onAdOpened() {
                                // Code to be executed when an ad opens an overlay that
                                // covers the screen.
                            }

                            override fun onAdClicked() {
                                // Code to be executed when the user clicks on an ad.
                            }

                            override fun onAdLeftApplication() {
                                // Code to be executed when the user has left the app.
                            }

                            override fun onAdClosed() {
                                // Code to be executed when the user is about to return
                                // to the app after tapping on an ad.
                            }
                        }
                    } catch (e: Exception) {
                    }
                }
            }, 1000)
        } catch (e: Exception) {
        }
        return root
    }

    companion object {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private const val ARG_PARAM = "param"
        private const val ARG_MIN = "0"
        private const val ARG_MAX = "1"
        /**   private const val ARG_SECTION_NUMBER = "section_number"
         * Returns a new instance of this fragment for the given section
         * number.
         */
        @JvmStatic
        fun newInstance(
            sectionNumber: Int,
            param: Property,
            min: String,
            max: String
        ): SearchListFragment {
            return SearchListFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_MAX, max)
                    putString(ARG_MIN, min)
                    putSerializable(ARG_PARAM, param)
                }
            }
        }
    }

}