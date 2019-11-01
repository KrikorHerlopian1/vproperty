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
import com.google.firebase.messaging.FirebaseMessaging
import edu.newhaven.krikorherlopian.android.vproperty.*
import edu.newhaven.krikorherlopian.android.vproperty.activity.CustomHomeMenuActivity
import edu.newhaven.krikorherlopian.android.vproperty.activity.HomeMenuActivity
import edu.newhaven.krikorherlopian.android.vproperty.activity.LoginActivity
import edu.newhaven.krikorherlopian.android.vproperty.adapter.TitleSubtitleAdapter
import edu.newhaven.krikorherlopian.android.vproperty.interfaces.ListClick
import edu.newhaven.krikorherlopian.android.vproperty.model.SettingsItem
import kotlinx.android.synthetic.main.settings.view.*

/*
    Settings Fragment.Options to change in app.
 */
class SettingsFragment : Fragment(),
    ListClick {
    var drawerSettingsItem: SettingsItem? = null
    var autoLoginItem: SettingsItem? = null
    var signOutItem: SettingsItem? = null
    var versionItem: SettingsItem? = null
    var notifications: SettingsItem? = null
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
        // define the first setting item, the style of the navigation drawer.
        // in normal cases its default, user can change it from this page into custom(arc).
        var drawer = sharedPref?.getString(PREF_DRAWER, "default").toString()
        var auto = sharedPref?.getBoolean(PREF_AUTO, true).toString()
        var not = sharedPref?.getBoolean(PREF_NOT, true).toString()
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

        when (drawer) {
            "default" -> drawerSettingsItem?.subtitle =
                root!!.resources.getString(R.string.default_drawer)
            "custom" -> drawerSettingsItem?.subtitle = root!!.resources.getString(R.string.custom)
        }
        list.add(drawerSettingsItem!!)
        list.add(notifications!!)
        list.add(autoLoginItem!!)
        list.add(signOutItem!!)
        list.add(versionItem!!)
        val adapter = TitleSubtitleAdapter(
            list, this, root?.context!!
        )
        root?.recyclerView?.layoutManager = LinearLayoutManager(root?.context)
        root?.recyclerView?.itemAnimator = DefaultItemAnimator()
        root?.recyclerView?.adapter = adapter
        return root
    }

    override fun rowClicked(position: Int, position2: Int) {
        if (position == 0) {
            showDrawerOptions()
        } else if (position == 2) {
            val editor = sharedPref?.edit()
            editor?.putBoolean(PREF_AUTO, list.get(position).subtitle.toBoolean())
            editor?.apply()
        } else if (position == 3) {
            val intent = Intent(context, LoginActivity::class.java)
            //this flag to close all activities and start the application back with loginscreen on top.
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
            activity?.finish()
        } else if (position == 1) {
            val editor = sharedPref?.edit()
            editor?.putBoolean(PREF_NOT, list.get(position).subtitle.toBoolean())
            editor?.apply()


            if (list.get(position).subtitle.toBoolean()) {
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

    // Show popupbox for user to select between default drawer or custom drawer.
    private fun showDrawerOptions() {
        val sel = "" + resources.getString(R.string.navigation_drawer)
        val alerBuilder = AlertDialog.Builder(root!!.context)
        val settingsOptArr = arrayOfNulls<String>(2)

        var selectedChoice = 0
        var previousChoice = 0
        if (drawerSettingsItem?.subtitle?.toUpperCase() == "CUSTOM")
            previousChoice = 1
        else
            previousChoice = 0
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
        }).setCancelable(false).setTitle(sel).create()
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
}