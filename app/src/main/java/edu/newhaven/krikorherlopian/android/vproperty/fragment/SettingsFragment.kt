package edu.newhaven.krikorherlopian.android.vproperty.fragment

import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.messaging.FirebaseMessaging
import edu.newhaven.krikorherlopian.android.vproperty.*
import edu.newhaven.krikorherlopian.android.vproperty.activity.CustomHomeMenuActivity
import edu.newhaven.krikorherlopian.android.vproperty.activity.HomeMenuActivity
import edu.newhaven.krikorherlopian.android.vproperty.activity.LoginActivity
import edu.newhaven.krikorherlopian.android.vproperty.adapter.RecylerViewAdapter
import edu.newhaven.krikorherlopian.android.vproperty.interfaces.ListClick
import edu.newhaven.krikorherlopian.android.vproperty.model.SettingsItem
import kotlinx.android.synthetic.main.listview_fragment.view.*

/*
    Settings Fragment.Options to change in app.
 */
class SettingsFragment : Fragment(),
    ListClick {
    var drawerSettingsItem: SettingsItem? = null
    var autoLoginItem: SettingsItem? = null
    var signOutItem: SettingsItem? = null
    var versionItem: SettingsItem? = null
    var mapTypeItem: SettingsItem? = null
    var notifications: SettingsItem? = null
    var localeItem: SettingsItem? = null
    var list: MutableList<Any> = mutableListOf<Any>()
    var sharedPref: SharedPreferences? = null
    var root: View? = null
    var adapter: RecylerViewAdapter? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.listview_fragment, container, false)
        sharedPref = root?.context?.getSharedPreferences(
            PREFS_FILENAME,
            PRIVATE_MODE
        )
        // define the first setting item, the style of the navigation drawer.
        // in normal cases its default, user can change it from this page into custom(arc).

        var mapType = sharedPref?.getString(PREF_MAP, "normal").toString()
        var auto = sharedPref?.getBoolean(PREF_AUTO, true).toString()
        var not = sharedPref?.getBoolean(PREF_NOT, true).toString()
        var drawer = sharedPref?.getString(PREF_DRAWER, "default").toString()
        var locale = sharedPref?.getString(PREF_LOCALE, "").toString()

        val manager = root?.context?.packageManager
        val info = manager?.getPackageInfo(
            root?.context?.packageName, 0
        )
        val version = info?.versionName
        drawerSettingsItem = SettingsItem(
            root!!.resources.getString(R.string.navigation_drawer),
            "",
            R.drawable.ic_menu_black_24dp
        )
        localeItem = SettingsItem(
            root!!.resources.getString(R.string.language),
            "",
            R.drawable.ic_language_black_24dp
        )
        autoLoginItem = SettingsItem(
            root!!.resources.getString(R.string.auto_login),
            "" + auto,
            R.drawable.ic_login, 1
        )

        signOutItem = SettingsItem(
            root!!.resources.getString(R.string.sign_out),
            "",
            R.drawable.ic_sign_out, 2
        )
        notifications = SettingsItem(
            root!!.resources.getString(R.string.push_notifications),
            "" + not,
            R.drawable.ic_notifications, 1
        )
        versionItem = SettingsItem(
            root!!.resources.getString(R.string.version),
            "" + version,
            R.drawable.ic_info
        )
        mapTypeItem = SettingsItem(
            root!!.resources.getString(R.string.map_types),
            "",
            R.drawable.ic_placeholder_location
        )


        when (mapType) {
            "normal" -> mapTypeItem?.subtitle =
                root!!.resources.getString(R.string.normal)
            "hybrid" -> mapTypeItem?.subtitle = root!!.resources.getString(R.string.hybdrid)
            "terrain" -> mapTypeItem?.subtitle = root!!.resources.getString(R.string.terrain)
            "satellite" -> mapTypeItem?.subtitle = root!!.resources.getString(R.string.satellite)
        }

        if (locale.contains("es")) {
            localeItem?.subtitle = root!!.resources.getString(R.string.spanish)
        } else {
            localeItem?.subtitle = root!!.resources.getString(R.string.english)
        }


        when (drawer) {
            "default" -> {
                drawerSettingsItem?.subtitle =
                    root!!.resources.getString(R.string.default_drawer)
            }
            "custom" -> {
                drawerSettingsItem?.subtitle = root!!.resources.getString(R.string.custom)
            }
        }
        list.add(localeItem!!)
        list.add(drawerSettingsItem!!)
        list.add(mapTypeItem!!)
        list.add(notifications!!)
        list.add(autoLoginItem!!)
        list.add(signOutItem!!)
        list.add(versionItem!!)
        adapter = RecylerViewAdapter(
            list, this, root?.context!!
        )
        root?.recyclerView?.layoutManager = LinearLayoutManager(root?.context)
        root?.recyclerView?.itemAnimator = DefaultItemAnimator()
        root?.recyclerView?.adapter = adapter
        return root
    }

    override fun rowClicked(position: Int, position2: Int, imageLayout: ImageView?) {
        if (position == 0) {
            showLangOptions()
        } else if (position == 1) {
            showDrawerOptions()
        } else if (position == 2) {
            showMapTypeOptions()
        } else if (position == 4) {
            val editor = sharedPref?.edit()
            editor?.putBoolean(PREF_AUTO, (list.get(position) as SettingsItem).subtitle.toBoolean())
            editor?.apply()
        } else if (position == 5) {
            val intent = Intent(context, LoginActivity::class.java)
            //this flag to close all activities and start the application back with loginscreen on top.
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
            activity?.finish()
        } else if (position == 3) {
            val editor = sharedPref?.edit()
            editor?.putBoolean(PREF_NOT, (list.get(position) as SettingsItem).subtitle.toBoolean())
            editor?.apply()


            if ((list.get(position) as SettingsItem).subtitle.toBoolean()) {
                FirebaseMessaging.getInstance().subscribeToTopic("vproperty")
                    .addOnCompleteListener { task ->
                        var msg = getString(R.string.msg_subscribed)
                        if (!task.isSuccessful) {
                            msg = getString(R.string.msg_subscribe_failed)
                        }
                    }
            } else {
                FirebaseMessaging.getInstance().unsubscribeFromTopic("vproperty")
                    .addOnCompleteListener { task ->
                        var msg = getString(R.string.msg_subscribed)
                        if (!task.isSuccessful) {
                            msg = getString(R.string.msg_subscribe_failed)
                        }
                    }
            }
        }
    }

    private fun showMapTypeOptions() {
        val sel = "" + resources.getString(R.string.navigation_drawer)
        val alerBuilder = AlertDialog.Builder(root!!.context)
        val settingsOptArr = arrayOfNulls<String>(4)

        var selectedChoice = 0
        var previousChoice = 0
        var mapType = sharedPref?.getString(PREF_MAP, "normal").toString()
        when (mapType) {
            "normal" -> previousChoice = 0
            "hybrid" -> previousChoice = 1
            "terrain" -> previousChoice = 2
            "satellite" -> previousChoice = 3
        }

        selectedChoice = previousChoice
        val ok = "" + resources.getString(android.R.string.ok)
        settingsOptArr[0] = resources.getString(R.string.normal)
        settingsOptArr[1] = resources.getString(R.string.hybdrid)
        settingsOptArr[2] = resources.getString(R.string.terrain)
        settingsOptArr[3] = resources.getString(R.string.satellite)

        var alert = alerBuilder.setSingleChoiceItems(
            settingsOptArr,
            previousChoice,
            DialogInterface.OnClickListener { dialog, item ->
                when (item) {
                    0 -> selectedChoice = 0
                    1 -> selectedChoice = 1
                    2 -> selectedChoice = 2
                    3 -> selectedChoice = 3
                }
            }
        ).setPositiveButton(ok, DialogInterface.OnClickListener { dialogInterface, ii ->
            try {
                //user selected an option save it to shared preferences for next login situations and open the new style menu.
                var param = ""
                if (selectedChoice != previousChoice) {
                    if (selectedChoice == 0) {
                        param = "normal"
                        mapTypeItem?.subtitle =
                            root!!.resources.getString(R.string.normal)
                    } else if (selectedChoice == 1) {
                        param = "hybrid"
                        mapTypeItem?.subtitle = root!!.resources.getString(R.string.hybdrid)
                    } else if (selectedChoice == 2) {
                        param = "terrain"
                        mapTypeItem?.subtitle = root!!.resources.getString(R.string.terrain)
                    } else if (selectedChoice == 3) {
                        param = "satellite"
                        mapTypeItem?.subtitle = root!!.resources.getString(R.string.satellite)
                    }
                    adapter?.notifyDataSetChanged()
                    val editor = sharedPref?.edit()
                    editor?.putString(PREF_MAP, param)
                    editor?.apply()
                }
            } catch (e: Exception) {
            }
        }).setCancelable(true).setTitle(sel).create()
        alert.window?.attributes?.windowAnimations = R.style.DialogAnimation
        alert.show()
    }

    // Show popupbox for user to select between default drawer or custom drawer.
    private fun showDrawerOptions() {
        val sel = "" + resources.getString(R.string.navigation_drawer)
        val alerBuilder = AlertDialog.Builder(root!!.context)
        val settingsOptArr = arrayOfNulls<String>(2)

        var selectedChoice = 0
        var previousChoice = 0
        var drawer = sharedPref?.getString(PREF_DRAWER, "default").toString()
        when (drawer) {
            "default" -> {
                previousChoice = 0
            }
            "custom" -> {
                previousChoice = 1
            }
        }
        selectedChoice = previousChoice
        val ok = "" + resources.getString(android.R.string.ok)
        settingsOptArr[0] = resources.getString(R.string.default_drawer)
        settingsOptArr[1] = resources.getString(R.string.custom)

        var alert = alerBuilder.setSingleChoiceItems(
            settingsOptArr,
            previousChoice,
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
                if (selectedChoice != previousChoice) {
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
                }


            } catch (e: Exception) {
            }
        }).setCancelable(true).setTitle(sel).create()
        alert.window?.attributes?.windowAnimations = R.style.DialogAnimation
        alert.show()
    }

    private fun showLangOptions() {
        val sel = "" + resources.getString(R.string.language)
        val alerBuilder = AlertDialog.Builder(root!!.context)
        val settingsOptArr = arrayOfNulls<String>(2)

        var selectedChoice = 0
        var previousChoice = 0
        var locale = sharedPref?.getString(PREF_LOCALE, "").toString()
        if (locale.contains("es")) {
            previousChoice = 1
        } else {
            previousChoice = 0
        }

        selectedChoice = previousChoice
        val ok = "" + resources.getString(android.R.string.ok)
        settingsOptArr[0] = resources.getString(R.string.english)
        settingsOptArr[1] = resources.getString(R.string.spanish)

        var alert = alerBuilder.setSingleChoiceItems(
            settingsOptArr,
            previousChoice,
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
                if (selectedChoice != previousChoice) {
                    if (selectedChoice == 0) {
                        param = "en"
                        localeItem?.subtitle =
                            root!!.resources.getString(R.string.english)
                    } else {
                        param = "es"
                        localeItem?.subtitle = root!!.resources.getString(R.string.spanish)
                    }
                    val editor = sharedPref?.edit()
                    editor?.putString(PREF_LOCALE, param)
                    editor?.apply()
                    startHomeMenuActivity(param)
                }


            } catch (e: Exception) {
            }
        }).setCancelable(true).setTitle(sel).create()
        alert.window?.attributes?.windowAnimations = R.style.DialogAnimation
        alert.show()
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

    override fun deleteRow(position: Int) {
    }
}