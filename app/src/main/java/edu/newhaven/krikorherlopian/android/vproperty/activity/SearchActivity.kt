package edu.newhaven.krikorherlopian.android.vproperty.activity

import android.os.Bundle
import com.anupcowkur.statelin.Machine
import com.anupcowkur.statelin.State
import edu.newhaven.krikorherlopian.android.vproperty.R
import edu.newhaven.krikorherlopian.android.vproperty.fragment.SearchListFragment
import edu.newhaven.krikorherlopian.android.vproperty.fragment.SearchMapFragment
import edu.newhaven.krikorherlopian.android.vproperty.model.Property
import kotlinx.android.synthetic.main.search_layout.*

class SearchActivity : CustomAppCompatActivity() {
    var title: String? = ""
    lateinit var prop: Property
    //state implemented to show either properties on map or lists.
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
            setUpToolbar(toolbar, resources.getString(R.string.search))

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
}

