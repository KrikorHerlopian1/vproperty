package edu.newhaven.krikorherlopian.android.vproperty


import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import kotlinx.android.synthetic.main.activity_forgot_password.*


class ForgotPasswordActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)
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
        auth = FirebaseAuth.getInstance()
        forgotPasswordButton.setOnClickListener {
            if (email.text.isNullOrBlank()) {
                emailAddressInputLayout.isErrorEnabled = true
                emailAddressInputLayout.error = resources.getString(R.string.enter_email)
            } else if (!isEmailValid(email.text.toString())) {
                emailAddressInputLayout.isErrorEnabled = true
                emailAddressInputLayout.error = resources.getString(R.string.enter_valid_email)
            } else {
                emailAddressInputLayout.error = null
                emailAddressInputLayout.isErrorEnabled = false
                progressbar.visibility = View.VISIBLE
                forgotPasswordButton.isEnabled = false
                reset(email.text.toString())
            }
        }
    }

    fun reset(em: String) {
        FirebaseAuth.getInstance().sendPasswordResetEmail(em).addOnCompleteListener { task ->
            progressbar.visibility = View.GONE
            forgotPasswordButton.isEnabled = true
            if (task.isSuccessful) {
                // successful!
                Toast.makeText(
                    applicationContext,
                    resources.getString(R.string.reset_successful),
                    Toast.LENGTH_LONG
                ).show()
                val intent = Intent(this@ForgotPasswordActivity, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(intent)
            } else {
                // failed!
                val e = task.exception as FirebaseAuthException
                Toast.makeText(
                    applicationContext,
                    e.localizedMessage,
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}