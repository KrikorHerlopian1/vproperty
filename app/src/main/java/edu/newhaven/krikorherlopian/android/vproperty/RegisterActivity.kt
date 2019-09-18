package edu.newhaven.krikorherlopian.android.vproperty

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import kotlinx.android.synthetic.main.activity_register.*


class RegisterActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    var sharedPref: SharedPreferences? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        setSupportActionBar(toolbar)
        val actionBar = supportActionBar
        actionBar!!.title = resources.getString(R.string.register_new_account)
        actionBar.elevation = 4.0F
        actionBar.setDisplayShowHomeEnabled(true)
        actionBar.setDisplayUseLogoEnabled(true)
        actionBar.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener(View.OnClickListener {
            super.onBackPressed()
        })
        sharedPref = getSharedPreferences(PREFS_FILENAME, PRIVATE_MODE)
        auth = FirebaseAuth.getInstance()
        registerButton.setOnClickListener {
            if (email.text.isNullOrBlank()) {
                passwordInputLayout.error = null
                passwordInputLayout.isErrorEnabled = false
                emailAddressInputLayout.isErrorEnabled = true
                emailAddressInputLayout.error = resources.getString(R.string.enter_email)
            } else if (!isEmailValid(email.text.toString())) {
                passwordInputLayout.error = null
                passwordInputLayout.isErrorEnabled = false
                emailAddressInputLayout.isErrorEnabled = true
                emailAddressInputLayout.error = resources.getString(R.string.enter_valid_email)
            } else if (password.text.isNullOrBlank()) {
                emailAddressInputLayout.error = null
                emailAddressInputLayout.isErrorEnabled = false
                passwordInputLayout.isErrorEnabled = true
                passwordInputLayout.error = resources.getString(R.string.enter_password)
            } else {
                emailAddressInputLayout.error = null
                emailAddressInputLayout.isErrorEnabled = false
                passwordInputLayout.error = null
                passwordInputLayout.isErrorEnabled = false
                progressbar.visibility = View.VISIBLE
                registerButton.isEnabled = false
                register(email.text.toString(), password.text.toString())
            }
        }
    }

    fun register(em: String, pass: String) {
        auth.createUserWithEmailAndPassword(em, pass)
            .addOnCompleteListener(OnCompleteListener<AuthResult> { task ->
                progressbar.visibility = View.GONE
                registerButton.isEnabled = true
                if (task.isSuccessful) {
                    Toast.makeText(
                        applicationContext,
                        resources.getString(R.string.registration_success),
                        Toast.LENGTH_LONG
                    ).show()
                    val editor = sharedPref?.edit()
                    editor?.putString(PREF_EMAIL, em)
                    editor?.putString(PREF_PASS, pass)
                    editor?.apply()
                    val intent = Intent(this@RegisterActivity, MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    startActivity(intent)
                } else {
                    val e = task.exception as FirebaseAuthException
                    Toast.makeText(
                        applicationContext,
                        e.localizedMessage,
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
    }
}