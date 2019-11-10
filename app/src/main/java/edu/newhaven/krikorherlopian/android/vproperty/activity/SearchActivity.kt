package edu.newhaven.krikorherlopian.android.vproperty.activity

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.anupcowkur.statelin.Machine
import com.anupcowkur.statelin.State
import edu.newhaven.krikorherlopian.android.vproperty.LocaleHelper
import edu.newhaven.krikorherlopian.android.vproperty.R
import edu.newhaven.krikorherlopian.android.vproperty.fragment.SearchListFragment
import edu.newhaven.krikorherlopian.android.vproperty.fragment.SearchMapFragment
import edu.newhaven.krikorherlopian.android.vproperty.model.Property
import kotlinx.android.synthetic.main.search_layout.*


class SearchActivity : AppCompatActivity() {
    var title: String? = ""
    lateinit var prop: Property
    val mapState = State("map")
    val listState = State("state")
    val machine = Machine(mapState)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            setContentView(R.layout.search_layout)
            var bundle: Bundle? = intent.extras
            prop = intent.getSerializableExtra("argPojo") as Property
            var min = bundle?.getString("min")
            var max = bundle?.getString("max")

            machine.state = mapState
            menu.setImageResource(R.drawable.ic_view_list_black_24dp)
            val fragment = SearchMapFragment.newInstance(0, prop, min!!, max!!)
            val fragmentManager = supportFragmentManager
            fragmentManager.beginTransaction()
                .replace(R.id.frame_container, fragment).commit()
            setupToolBar()

            menu_layout.setOnClickListener {
                when (machine.state) {
                    mapState -> {
                        machine.state = listState
                        menu.setImageResource(R.drawable.mapmenu)
                        val fragment = SearchListFragment.newInstance(0, prop, min, max)
                        val fragmentManager = supportFragmentManager
                        fragmentManager.beginTransaction()
                            .replace(R.id.frame_container, fragment).commit()
                    }
                    listState -> {
                        machine.state = mapState
                        menu.setImageResource(R.drawable.ic_view_list_black_24dp)
                        val fragment = SearchMapFragment.newInstance(0, prop, min, max)
                        val fragmentManager = supportFragmentManager
                        fragmentManager.beginTransaction()
                            .replace(R.id.frame_container, fragment).commit()
                    }
                }
            }

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
