package edu.newhaven.krikorherlopian.android.vproperty

import android.Manifest
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricPrompt
import androidx.core.app.ActivityCompat
import androidx.core.hardware.fingerprint.FingerprintManagerCompat
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.android.synthetic.main.activity_main.*
import java.util.concurrent.Executors


class MainActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    var sharedPref: SharedPreferences? = null


    val progressDialog by lazy {
        ProgressDialog(this)
    }

    fun showProgressDialog() {
        progressDialog.setMessage(getString(R.string.loading))
        progressDialog.isIndeterminate = true
        progressDialog.show()
    }

    fun hideProgressDialog() {
        if (progressDialog.isShowing) {
            progressDialog.dismiss()
        }
    }

    fun isPermissionGranted(context: Context): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            return ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.USE_BIOMETRIC
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            return ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.USE_FINGERPRINT
            ) == PackageManager.PERMISSION_GRANTED
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        sharedPref = getSharedPreferences(PREFS_FILENAME, PRIVATE_MODE)
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        auth = FirebaseAuth.getInstance()
        fingerPrintSetup()
        sign_in_button.setOnClickListener {
            val signInIntent = googleSignInClient.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }
        loginButton.setOnClickListener {
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
                passwordInputLayout.error = null
                emailAddressInputLayout.isErrorEnabled = false
                passwordInputLayout.isErrorEnabled = false
                progressbar.visibility = View.VISIBLE
                loginButton.isEnabled = false
                login(email.text.toString(), password.text.toString())
            }
        }
        registerLayout.setOnClickListener {
            val intent = Intent(this@MainActivity, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    fun login(em: String, pass: String, showDialog: Boolean = false) {
        if (showDialog)
            showProgressDialog()
        auth.signInWithEmailAndPassword(em, pass)
            .addOnCompleteListener(this) { task ->
                progressbar.visibility = View.GONE
                loginButton.isEnabled = true
                if (showDialog)
                    hideProgressDialog()
                if (task.isSuccessful) {
                    val editor = sharedPref?.edit()
                    editor?.putString(PREF_EMAIL, em)
                    editor?.putString(PREF_PASS, pass)
                    editor?.apply()
                    Toast.makeText(
                        baseContext, R.string.auth_succeeded,
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    val e = task.exception as FirebaseAuthException
                    loginButton.isEnabled = true
                    Toast.makeText(
                        baseContext, e.localizedMessage,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account!!)
            } catch (e: ApiException) {
                Toast.makeText(
                    baseContext, R.string.google_failed,
                    Toast.LENGTH_SHORT
                ).show()

            }
        }
    }

    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {

        showProgressDialog()
        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Toast.makeText(
                        baseContext, R.string.google_succeeded,
                        Toast.LENGTH_SHORT
                    ).show()
                    val user = auth.currentUser
                } else {
                    val e = task.exception as FirebaseAuthException
                    Toast.makeText(
                        baseContext, e.localizedMessage,
                        Toast.LENGTH_SHORT
                    ).show()
                }
                hideProgressDialog()
            }
    }

    fun fingerPrintSetup() {
        if (sharedPref?.getString(PREF_EMAIL, "").isNullOrEmpty()
            || sharedPref?.getString(PREF_PASS, "").isNullOrEmpty()
            || Build.VERSION.SDK_INT < Build.VERSION_CODES.M
        ) {
            Log.w("tag", "got here e")
            lfingerprint.visibility = View.GONE
        } else {
            val fingerprintManager = FingerprintManagerCompat.from(this)
            if (fingerprintManager.isHardwareDetected &&
                fingerprintManager.hasEnrolledFingerprints() && isPermissionGranted(this)
            ) {
                lfingerprint.visibility = View.VISIBLE
            } else
                lfingerprint.visibility = View.GONE


            val executor = Executors.newSingleThreadExecutor()
            val biometricPrompt =
                BiometricPrompt(this, executor, object : BiometricPrompt.AuthenticationCallback() {

                    override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                        super.onAuthenticationSucceeded(result)
                        this@MainActivity.runOnUiThread(java.lang.Runnable {
                            login(
                                sharedPref?.getString(PREF_EMAIL, "").toString(),
                                sharedPref?.getString(PREF_PASS, "").toString(), true
                            )
                        })
                    }

                    override fun onAuthenticationFailed() {
                        super.onAuthenticationFailed()
                        this@MainActivity.runOnUiThread(java.lang.Runnable {
                            Toast.makeText(
                                this@MainActivity, R.string.biometric_unrecognized,
                                Toast.LENGTH_SHORT
                            ).show()
                        })
                    }
                })
            lfingerprint.setOnClickListener {
                val promptInfo = BiometricPrompt.PromptInfo.Builder()
                    .setTitle(resources.getString(R.string.fingerprint))
                    .setSubtitle(resources.getString(R.string.fingerprint_login))
                    .setDescription("")
                    .setNegativeButtonText(resources.getString(R.string.cancel))
                    .build()
                biometricPrompt.authenticate(promptInfo)
            }
        }
    }

    public override fun onStart() {
        super.onStart()
    }

    companion object {
        private const val RC_SIGN_IN = 9001
    }
}
