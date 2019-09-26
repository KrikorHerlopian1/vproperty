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
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_register.*

/*
    Registration page.Email, Password to enter. There is validation on username and email, in case user enters invalid email
    or leaves them empty when clicking register. In case successfully registered, username and email is stored on phone
    and user is navigated back to login screen.
 */

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
                setError(null, null, true, false)
                emailAddressInputLayout.error = resources.getString(R.string.enter_email)
            } else if (!isEmailValid(email.text.toString())) {
                setError(null, null, true, false)
                emailAddressInputLayout.error = resources.getString(R.string.enter_valid_email)
            } else if (password.text.isNullOrBlank()) {
                setError(null, null, false, true)
                passwordInputLayout.error = resources.getString(R.string.enter_password)
            } else {
                setError(null, null, false, false)
                //show progress bar with firebase called
                progressbar.visibility = View.VISIBLE
                //disable registerbutton so that user doesnt register again while one call to firebase is running.
                registerButton.isEnabled = false
                register(email.text.toString(), password.text.toString())
            }
        }
    }


    private fun setError(
        emailError: CharSequence?,
        passwordError: CharSequence?,
        emailErrorEnabled: Boolean,
        passwordErrorEnabled: Boolean
    ) {
        emailAddressInputLayout.error = emailError
        passwordInputLayout.error = passwordError
        emailAddressInputLayout.isErrorEnabled = emailErrorEnabled
        passwordInputLayout.isErrorEnabled = passwordErrorEnabled
    }
    //call firebase to register user.
    fun register(em: String, pass: String) {
        auth.createUserWithEmailAndPassword(em, pass)
            .addOnCompleteListener(OnCompleteListener<AuthResult> { task ->
                //hide progressbar when done with firebase call
                progressbar.visibility = View.GONE
                //re-enable register button when done with the firebase call.
                registerButton.isEnabled = true
                if (task.isSuccessful) {
                    Toasty.success(
                        this@RegisterActivity,
                        R.string.registration_success,
                        Toast.LENGTH_SHORT,
                        true
                    ).show()
                    //on successfull registration store the email and username on device.
                    val editor = sharedPref?.edit()
                    editor?.putString(PREF_EMAIL, em)
                    editor?.putString(PREF_PASS, pass)
                    editor?.apply()
                    val intent = Intent(this@RegisterActivity, MainActivity::class.java)
                    //this flag to close all activities and start the application back with loginscreen on top.
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    startActivity(intent)
                } else {
                    if (task.exception is FirebaseAuthException) {
                        val e = task.exception as FirebaseAuthException
                        Toasty.error(
                            this@RegisterActivity,
                            e.localizedMessage,
                            Toast.LENGTH_SHORT,
                            true
                        ).show()
                    } else {
                        Toasty.error(
                            this@RegisterActivity,
                            R.string.connection_failed,
                            Toast.LENGTH_SHORT,
                            true
                        ).show()
                    }
                }
            })
    }
}