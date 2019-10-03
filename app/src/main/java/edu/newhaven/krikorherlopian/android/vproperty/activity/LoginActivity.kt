package edu.newhaven.krikorherlopian.android.vproperty.activity

import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.hardware.fingerprint.FingerprintManagerCompat
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.GoogleAuthProvider
import edu.newhaven.krikorherlopian.android.vproperty.*
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_main.*
import java.util.concurrent.Executors

/*
    Login Page implemented here. Email, Password to enter. Or login via google account. Or if user previously logged in with email username
    his credentials are stored on phone and next time is given fingerprint login option ( provided he has fingerprints enrolled, and hardware).
    Two Buttons on the page as well to take you to registration page, and forgot password page.
    There is validation on username and email, in case user enters invalid email or leaves them empty when clicking login button for email/username.
 */

class LoginActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    var sharedPref: SharedPreferences? = null
    private fun showProgressDialog() {
        //hide google login and fingerprint buttons and show curve loader. also disable login button.
        curveLoader.visibility = View.VISIBLE
        lfingerprint.visibility = View.GONE
        googleLoginButton.visibility = View.GONE
        loginButton.isEnabled = false
    }

    private fun hideProgressDialog() {
        curveLoader.visibility = View.GONE
        fingerPrintSetup()
        googleLoginButton.visibility = View.VISIBLE
        loginButton.isEnabled = true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        sharedPref = getSharedPreferences(
            PREFS_FILENAME,
            PRIVATE_MODE
        )
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        auth = FirebaseAuth.getInstance()
        auth.signOut()
        fingerPrintSetup()
        sign_in_button.setOnClickListener {
            val signInIntent = googleSignInClient.signInIntent
            startActivityForResult(
                signInIntent,
                RC_SIGN_IN
            )
        }
        loginButton.setOnClickListener {
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
                //show progressbar before making firebase call.
                progressbar.visibility = View.VISIBLE
                //disable loginButton so that user doesnt click login again while one call to firebase is running.
                loginButton.isEnabled = false
                login(email.text.toString(), password.text.toString(), true)
            }
        }
        registerLayout.setOnClickListener {
            val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
            startActivityForResult(
                intent,
                RC_REGISTER
            )
        }
        forgotLayout.setOnClickListener {
            val intent = Intent(this@LoginActivity, ForgotPasswordActivity::class.java)
            startActivity(intent)
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

    fun login(em: String, pass: String, showDialog: Boolean = false) {
        //show  dialog in case fingerprint authentication is called.
        if (showDialog)
            showProgressDialog()
        auth.signInWithEmailAndPassword(em, pass)
            .addOnCompleteListener(this) { task ->
                //hide progressbar once firebase call is done
                progressbar.visibility = View.GONE
                //re-enable button again, for user to be able to call login again if attempt failed.
                loginButton.isEnabled = true
                //hide progress dialog in case of fingerprint authentication

                if (task.isSuccessful) {
                    //successful login save username and password on device.
                    val editor = sharedPref?.edit()
                    editor?.putString(PREF_EMAIL, em)
                    editor?.putString(PREF_PASS, pass)
                    editor?.apply()
                    Toasty.success(
                        this@LoginActivity,
                        R.string.auth_succeeded,
                        Toast.LENGTH_SHORT,
                        true
                    ).show()
                    loggedInUser = auth.currentUser
                    startHomeMenuActivity()
                } else {
                    if (task.exception is FirebaseAuthException) {
                        val e = task.exception as FirebaseAuthException
                        loginButton.isEnabled = true
                        Toasty.error(
                            this@LoginActivity,
                            e.localizedMessage,
                            Toast.LENGTH_SHORT,
                            true
                        ).show()
                    } else {
                        Toasty.error(
                            this@LoginActivity,
                            R.string.auth_failed,
                            Toast.LENGTH_SHORT,
                            true
                        ).show()
                    }

                }
                if (showDialog)
                    hideProgressDialog()
            }
    }

    private fun startHomeMenuActivity() {
        var drawer = sharedPref?.getString(PREF_DRAWER, "default").toString()
        var intent: Intent? = null
        if (drawer.equals("default")) {
            intent = Intent(this@LoginActivity, HomeMenuActivity::class.java)

        } else {
            intent = Intent(this@LoginActivity, CustomHomeMenuActivity::class.java)
        }
        intent.putExtra("email", loggedInUser?.email?.toString())
        intent.putExtra("displayName", loggedInUser?.displayName?.toString())
        intent.putExtra("phoneNumber", loggedInUser?.phoneNumber?.toString())
        intent.putExtra("photoUrl", loggedInUser?.photoUrl?.toString())
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
        finish()

    }

    //login result by google, returned here.RC_SIGN_IN will indicate what returned is from the google login.
    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account!!)
            } catch (e: ApiException) {
                Toasty.error(
                    this@LoginActivity,
                    R.string.google_failed, Toast.LENGTH_SHORT, true
                )
                    .show()
            }
        } else if (requestCode == RC_REGISTER) {
            setError(null, null, false, false)
            email.setText(data!!.getStringExtra("email"))
            password.setText(data.getStringExtra("pass"))
            fingerPrintSetup()
            // login(email.text.toString(), password.text.toString(), true)
        }
    }

    //login with google account .
    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {

        showProgressDialog()
        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, process to next step.
                    Toasty.success(
                        this@LoginActivity,
                        R.string.google_succeeded,
                        Toast.LENGTH_SHORT,
                        true
                    ).show()
                    loggedInUser = auth.currentUser
                    startHomeMenuActivity()
                } else {
                    if (task.exception is FirebaseAuthException) {
                        val e = task.exception as FirebaseAuthException
                        Toasty.error(
                            this@LoginActivity,
                            e.localizedMessage,
                            Toast.LENGTH_SHORT,
                            true
                        ).show()
                    } else {
                        Toasty.error(
                            this@LoginActivity,
                            R.string.google_failed,
                            Toast.LENGTH_SHORT,
                            true
                        ).show()
                    }
                }
                hideProgressDialog()
            }
    }

    private fun fingerPrintSetup() {
        //if no email or password stored on phone, or in case the device running the app is older then api level 23  Dont show fingerprint icon.
        if (fingerPrintAvailabilityCheck()) {
            lfingerprint.visibility = View.VISIBLE
            val executor = Executors.newSingleThreadExecutor()
            val biometricPrompt =
                BiometricPrompt(this, executor, object : BiometricPrompt.AuthenticationCallback() {

                    override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                        super.onAuthenticationSucceeded(result)
                        this@LoginActivity.runOnUiThread(java.lang.Runnable {
                            login(
                                sharedPref?.getString(PREF_EMAIL, "").toString(),
                                sharedPref?.getString(PREF_PASS, "").toString(), true
                            )
                        })
                    }

                    override fun onAuthenticationFailed() {
                        super.onAuthenticationFailed()
                        this@LoginActivity.runOnUiThread(java.lang.Runnable {
                            Toasty.error(
                                this@LoginActivity,
                                R.string.biometric_unrecognized,
                                Toast.LENGTH_SHORT,
                                true
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
        } else
            lfingerprint.visibility = View.GONE
    }

    private fun fingerPrintAvailabilityCheck(): Boolean {

        if (sharedPref?.getString(
                PREF_EMAIL,
                ""
            ).isNullOrEmpty() || sharedPref?.getString(PREF_PASS, "").isNullOrEmpty()
        ) {
            return false
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            var hardwareDetected = false
            var hasEnrolledFingerprints = false

            val biometricManager = this.getSystemService(BiometricManager::class.java)
            if (biometricManager != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                // canAuthenticate ()
                // Determine if biometrics can be used. In other words, determine if BiometricPrompt can be expected to be shown (hardware available, templates enrolled, user-enabled).
                hardwareDetected =
                    (biometricManager.canAuthenticate() === BiometricManager.BIOMETRIC_SUCCESS)
                hasEnrolledFingerprints = hardwareDetected
            } else {

                // FOR api level 28 and below. check if fingerprint hardware detected and fingerprints enrolled.
                val fingerprintManager = FingerprintManagerCompat.from(this)
                hardwareDetected = fingerprintManager.isHardwareDetected
                hasEnrolledFingerprints = fingerprintManager.hasEnrolledFingerprints()
            }
            return hardwareDetected && hasEnrolledFingerprints && isPermissionGranted(
                this
            )
        }
        return false
    }

    companion object {
        private const val RC_SIGN_IN = 9001
        private const val RC_REGISTER = 9002
    }
}
