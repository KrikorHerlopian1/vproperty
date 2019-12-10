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
import edu.newhaven.krikorherlopian.android.vproperty.R
import edu.newhaven.krikorherlopian.android.vproperty.adapter.PropertiesTabViewPagerAdapter
import edu.newhaven.krikorherlopian.android.vproperty.fragmentActivityCommunication
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
        fragmentActivityCommunication!!.hideShowMenuItems(true)
        root?.tabs?.setupWithViewPager(root?.view_pager!!)
        try {
            val handler = Handler()
            handler.postDelayed(object : Runnable {
                override fun run() {
                    try {
                        MobileAds.initialize(activity!!) {
                        }
                        val adRequest =
                            AdRequest.Builder()
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
}