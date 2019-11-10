package edu.newhaven.krikorherlopian.android.vproperty.activity

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import edu.newhaven.krikorherlopian.android.vproperty.LocaleHelper
import edu.newhaven.krikorherlopian.android.vproperty.R
import edu.newhaven.krikorherlopian.android.vproperty.fragment.SearchListFragment
import edu.newhaven.krikorherlopian.android.vproperty.model.Property
import kotlinx.android.synthetic.main.search_layout.*


class SearchActivity : AppCompatActivity() {
    var title: String? = ""
    lateinit var prop: Property
    var list: MutableList<Any> = mutableListOf<Any>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            setContentView(R.layout.search_layout)
            var bundle: Bundle? = intent.extras
            prop = intent.getSerializableExtra("argPojo") as Property
            var min = bundle?.getString("min")
            var max = bundle?.getString("max")


            val fragment = SearchListFragment.newInstance(0, prop, min!!, max!!)
            val fragmentManager = supportFragmentManager
            fragmentManager.beginTransaction()
                .replace(R.id.frame_container, fragment).commit()
            setupToolBar()

        } catch (e: Exception) {
        }
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(LocaleHelper.onAttach(base))
    }

    private fun setupToolBar() {
        setSupportActionBar(toolbar)
        val actionBar = supportActionBar
        actionBar!!.title = resources.getString(R.string.search)
        actionBar.elevation = 4.0F
        actionBar.setDisplayShowHomeEnabled(true)
        actionBar.setDisplayUseLogoEnabled(true)
        actionBar.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener(View.OnClickListener {
            super.onBackPressed()
        })
    }
}
