package edu.newhaven.krikorherlopian.android.vproperty.fragment

import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import edu.newhaven.krikorherlopian.android.vproperty.*
import edu.newhaven.krikorherlopian.android.vproperty.activity.CustomHomeMenuActivity
import edu.newhaven.krikorherlopian.android.vproperty.activity.HomeMenuActivity
import edu.newhaven.krikorherlopian.android.vproperty.adapter.TitleSubtitleAdapter
import edu.newhaven.krikorherlopian.android.vproperty.model.SettingsItem
import kotlinx.android.synthetic.main.settings.view.*

/*
    Settings Fragment.Options to change in app.
 */
class SettingsFragment : Fragment(), ListClick {
    var drawerSettingsItem: SettingsItem? = null
    var list: MutableList<SettingsItem> = mutableListOf<SettingsItem>()
    var sharedPref: SharedPreferences? = null
    var root: View? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.settings, container, false)
        sharedPref = root?.context?.getSharedPreferences(
            PREFS_FILENAME,
            PRIVATE_MODE
        )
        sharedPref = root?.context?.getSharedPreferences(
            PREFS_FILENAME,
            PRIVATE_MODE
        )

        // define the first setting item, the style of the navigation drawer.
        // in normal cases its default, user can change it from this page into custom(arc).
        var drawer = sharedPref?.getString(PREF_DRAWER, "default").toString()
        drawerSettingsItem = SettingsItem(
            root!!.resources.getString(R.string.navigation_drawer),
            "",
            R.drawable.ic_menu_black_24dp
        )
        when (drawer) {
            "default" -> drawerSettingsItem?.subtitle =
                root!!.resources.getString(R.string.default_drawer)
            "custom" -> drawerSettingsItem?.subtitle = root!!.resources.getString(R.string.custom)
        }
        list.add(drawerSettingsItem!!)


        val adapter = TitleSubtitleAdapter(
            list, this
        )
        root?.recyclerView?.layoutManager = LinearLayoutManager(root?.context)
        root?.recyclerView?.itemAnimator = DefaultItemAnimator()
        root?.recyclerView?.adapter = adapter
        return root
    }

    override fun rowClicked(position: Int) {
        if (position == 0) {
            showDrawerOptions()
        }
    }

    // Show popupbox for user to select between default drawer or custom drawer.
    private fun showDrawerOptions() {
        val sel = "" + resources.getString(R.string.navigation_drawer)
        val alerBuilder = AlertDialog.Builder(root!!.context)
        val settingsOptArr = arrayOfNulls<String>(2)

        var selectedChoice = 0
        if (drawerSettingsItem?.subtitle?.toUpperCase() == "CUSTOM")
            selectedChoice = 1
        else
            selectedChoice = 0
        val ok = "" + resources.getString(android.R.string.ok)
        settingsOptArr[0] = resources.getString(R.string.default_drawer)
        settingsOptArr[1] = resources.getString(R.string.custom)
        alerBuilder.setSingleChoiceItems(
            settingsOptArr,
            selectedChoice,
            DialogInterface.OnClickListener { dialog, item ->
                when (item) {
                    0 -> selectedChoice = 0
                    1 -> selectedChoice = 1
                }
            }
        ).setPositiveButton(ok, DialogInterface.OnClickListener { dialogInterface, ii ->
            try {
                //user selected an option save it to shared preferences for next login situations and open the new style menu.
                var param = ""
                if (selectedChoice == 0) {
                    param = "default"
                    drawerSettingsItem?.subtitle =
                        root!!.resources.getString(R.string.default_drawer)
                } else {
                    param = "custom"
                    drawerSettingsItem?.subtitle = root!!.resources.getString(R.string.custom)
                }
                val editor = sharedPref?.edit()
                editor?.putString(PREF_DRAWER, param)
                editor?.apply()
                startHomeMenuActivity(param)

            } catch (e: Exception) {
            }
        }).setCancelable(false).setTitle(sel).create().show()
    }

    //open the home or custom menu when navigation drawer settings changed in the app.
    //FLAG_ACTIVITY_CLEAR_TOP to close all activities in stack, and start new activity on top of stack.
    private fun startHomeMenuActivity(type: String) {
        var intent: Intent? = null
        if (type.equals("default")) {
            intent = Intent(root?.context, HomeMenuActivity::class.java)

        } else {
            intent = Intent(root?.context, CustomHomeMenuActivity::class.java)
        }
        intent.putExtra("email", loggedInUser?.email?.toString())
        intent.putExtra("displayName", loggedInUser?.displayName?.toString())
        intent.putExtra("phoneNumber", loggedInUser?.phoneNumber?.toString())
        intent.putExtra("photoUrl", loggedInUser?.photoUrl?.toString())
        intent.putExtra("page", 1)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
        activity?.finish()

    }
}