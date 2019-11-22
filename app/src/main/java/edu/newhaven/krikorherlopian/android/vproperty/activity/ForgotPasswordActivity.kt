package edu.newhaven.krikorherlopian.android.vproperty.activity


import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import edu.newhaven.krikorherlopian.android.vproperty.LocaleHelper
import edu.newhaven.krikorherlopian.android.vproperty.R
import edu.newhaven.krikorherlopian.android.vproperty.font
import edu.newhaven.krikorherlopian.android.vproperty.isEmailValid
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_forgot_password.*

/*
    This page has email and forgot password button.So that user reset his password if he forgot it.
    Validation done if email is valid and entered once submit link is clicked.
 */

class ForgotPasswordActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)
        setUpToolbar()
        setUpFonts()
        auth = FirebaseAuth.getInstance()
        forgotPasswordButton.setOnClickListener {
            forgotPasswordButtonClicked()
        }
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(LocaleHelper.onAttach(base))
    }

    private fun forgotPasswordButtonClicked() {
        if (email.text.isNullOrBlank()) {
            setError(null, true)
            emailAddressInputLayout.error = resources.getString(R.string.enter_email)
        } else if (!isEmailValid(email.text.toString())) {
            setError(null, true)
            emailAddressInputLayout.error = resources.getString(R.string.enter_valid_email)
        } else {
            setError(null, false)
            //show progress bar with firebase called
            progressbar.visibility = View.VISIBLE
            //disable forgotpasswordbutton so that user doesnt reset link again while one call to firebase is running.
            forgotPasswordButton.isEnabled = false
            reset(email.text.toString())
        }
    }

    private fun setUpToolbar() {
        setSupportActionBar(toolbar)
        val actionBar = supportActionBar
        actionBar!!.title = resources.getString(R.string.forgot_password)
        actionBar.elevation = 4.0F
        actionBar.setDisplayShowHomeEnabled(true)
        actionBar.setDisplayUseLogoEnabled(true)
        actionBar.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener(View.OnClickListener {
            super.onBackPressed()
        })
    }

    private fun setUpFonts() {
        var tf = Typeface.createFromAsset(assets, "" + font)
        emailAddressInputLayout.typeface = tf
        email.typeface = tf
    }

    private fun setError(emailError: CharSequence?, emailErrorEnabled: Boolean) {
        emailAddressInputLayout.error = emailError
        emailAddressInputLayout.isErrorEnabled = emailErrorEnabled
    }

    //call firebase to reset password.
    fun reset(em: String) {
        FirebaseAuth.getInstance().sendPasswordResetEmail(em).addOnCompleteListener { task ->
            //hide progress bar when done with firebase call
            progressbar.visibility = View.GONE
            // re-enable forgotpasswordbutton, incase firebase called failed user can click it again.
            forgotPasswordButton.isEnabled = true
            if (task.isSuccessful) {
                // successful!
                Toasty.success(
                    this@ForgotPasswordActivity,
                    R.string.reset_successful,
                    Toast.LENGTH_SHORT,
                    true
                ).show()
                val intent = Intent(this@ForgotPasswordActivity, LoginActivity::class.java)
                //this flag to close all activities and start the application back with loginscreen on top.
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(intent)
            } else {
                // failed!
                if (task.exception is FirebaseAuthException) {
                    val e = task.exception as FirebaseAuthException
                    Toasty.error(
                        this@ForgotPasswordActivity,
                        e.localizedMessage,
                        Toast.LENGTH_SHORT,
                        true
                    ).show()
                } else {
                    Toasty.error(
                        this@ForgotPasswordActivity,
                        R.string.connection_failed,
                        Toast.LENGTH_SHORT,
                        true
                    ).show()
                }
            }
        }
    }
}