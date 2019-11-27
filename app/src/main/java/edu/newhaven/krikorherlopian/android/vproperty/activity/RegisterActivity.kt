package edu.newhaven.krikorherlopian.android.vproperty.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.Typeface
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import com.mikelau.croperino.Croperino
import com.mikelau.croperino.CroperinoConfig
import com.mikelau.croperino.CroperinoFileUtil
import edu.newhaven.krikorherlopian.android.vproperty.*
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.custom_croperino_dialog.view.*
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


/*
    Registration page.Email, Password to enter. There is validation on username and email, in case user enters invalid email
    or leaves them empty when clicking register. In case successfully registered, username and email is stored on phone
    and user is navigated back to login screen.
 */

class RegisterActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    lateinit var storage: FirebaseStorage
    var sharedPref: SharedPreferences? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        setUpToolbar()
        storage = FirebaseStorage.getInstance()
        sharedPref = getSharedPreferences(
            PREFS_FILENAME,
            PRIVATE_MODE
        )
        setUpPermissions(this)
        auth = FirebaseAuth.getInstance()
        prepareCroperino()
        setUpFonts()
        registerButton.setOnClickListener {
            registerButtonClicked()
        }
        profile_image.setOnClickListener {
            addProfileButtonClicked()
        }
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(LocaleHelper.onAttach(base))
    }

    private fun addProfileButtonClicked() {
        val alerBuilder = AlertDialog.Builder(this@RegisterActivity)
        val dialogView = layoutInflater.inflate(R.layout.custom_croperino_dialog, null)

        alerBuilder.setView(dialogView)
        val alert = alerBuilder.setCancelable(true).setTitle("").create()
        alert.window?.attributes?.windowAnimations = R.style.DialogAnimation
        alert.show()
        dialogView.close.setOnClickListener {
            alert.dismiss()
        }
        dialogView.camera.setOnClickListener {
            // Croperino.prepareCamera(this@RegisterActivity)
            dispatchTakePictureIntent()
            alert.dismiss()
        }
        dialogView.gallery.setOnClickListener {
            Croperino.prepareGallery(this@RegisterActivity)
            alert.dismiss()
        }
    }

    private fun registerButtonClicked() {
        if (displayName.text.isNullOrBlank()) {
            setError(null, null, null, true, false, false, null, false)
            displayNamenputLayout.error = resources.getString(R.string.enter_display_name)
        } else if (email.text.isNullOrBlank()) {
            setError(null, null, null, false, true, false, null, false)
            emailAddressInputLayout.error = resources.getString(R.string.enter_email)
        } else if (!isEmailValid(email.text.toString())) {
            setError(null, null, null, false, true, false, null, false)
            emailAddressInputLayout.error = resources.getString(R.string.enter_valid_email)
        } else if (password.text.isNullOrBlank()) {
            setError(null, null, null, false, false, true, null, false)
            passwordInputLayout.error = resources.getString(R.string.enter_password)
        } else if (confirmPassword.text.isNullOrBlank()) {
            setError(null, null, null, false, false, false, null, true)
            confirmPasswordInputLayout.error = resources.getString(R.string.enter_confirm_password)
        } else if (!confirmPassword.text.toString().equals(password.text.toString())) {
            setError(null, null, null, false, false, true, null, true)
            passwordInputLayout.error = resources.getString(R.string.passwords_dont_match)
            confirmPasswordInputLayout.error = resources.getString(R.string.passwords_dont_match)
        } else {
            setError(null, null, null, false, false, false, null, false)
            //show progress bar with firebase called
            progressbar.visibility = View.VISIBLE
            //disable registerbutton so that user doesnt register again while one call to firebase is running.
            registerButton.isEnabled = false
            register(
                email.text.toString(),
                password.text.toString(),
                displayName.text.toString()
            )
        }
    }

    private fun setUpFonts() {
        var tf = Typeface.createFromAsset(assets, "" + font)
        emailAddressInputLayout.typeface = tf
        passwordInputLayout.typeface = tf
        confirmPasswordInputLayout.typeface = tf
        displayNamenputLayout.typeface = tf
        email.typeface = tf
        confirmPassword.typeface = tf
        password.typeface = tf
        displayName.typeface = tf

    }

    var currentPhotoPath: String? = ""
    var photoFile: File? = null

    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File = getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = absolutePath
        }
    }

    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            // Ensure that there's a camera activity to handle the intent
            takePictureIntent.resolveActivity(packageManager)?.also {
                // Create the File where the photo should go
                photoFile = try {
                    createImageFile()
                } catch (ex: IOException) {
                    null
                }
                // Continue only if the File was successfully created
                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                        this,
                        "edu.newhaven.krikorherlopian.android.vproperty.fileprovider",
                        it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, CroperinoConfig.REQUEST_TAKE_PHOTO)
                }
            }
        }
    }

    private fun setUpToolbar() {
        setSupportActionBar(toolbar)
        val actionBar = supportActionBar
        actionBar!!.title = resources.getString(R.string.register_new_account)
        actionBar.elevation = 4.0F
        actionBar.setDisplayShowHomeEnabled(true)
        actionBar.setDisplayUseLogoEnabled(true)
        actionBar.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener(View.OnClickListener {
            setResult(0, Intent())
            finish()
        })
    }

    private fun prepareCroperino() {
        //prepare camera, gallery and ask for storage permissions.
        CroperinoConfig(
            "IMG_" + System.currentTimeMillis() + ".jpg",
            "/VProperty/Pictures",
            "/sdcard/VProperty/Pictures"
        )
        CroperinoFileUtil.verifyStoragePermissions(this@RegisterActivity)
        CroperinoFileUtil.setupDirectory(this@RegisterActivity)

    }

    private fun setError(
        personError: CharSequence?,
        emailError: CharSequence?,
        passwordError: CharSequence?,
        personErrorEnabled: Boolean,
        emailErrorEnabled: Boolean,
        passwordErrorEnabled: Boolean,
        confirmPasswordError: CharSequence?,
        confirmPasswordErrorEnabled: Boolean
    ) {
        displayNamenputLayout.error = personError
        emailAddressInputLayout.error = emailError
        passwordInputLayout.error = passwordError
        confirmPassword.error = confirmPasswordError
        confirmPasswordInputLayout.isErrorEnabled = confirmPasswordErrorEnabled
        displayNamenputLayout.isErrorEnabled = personErrorEnabled
        emailAddressInputLayout.isErrorEnabled = emailErrorEnabled
        passwordInputLayout.isErrorEnabled = passwordErrorEnabled
    }

    //call firebase to register user. If no URI specified, upload image first time. Then call method again to register user with his name, email , password and photoUrl uploaded.
    fun register(em: String, pass: String, personName: String, url: Uri? = null) {
        if (url == null) {
            val storageRef = storage.reference
            var x = UUID.randomUUID()
            val mountainsRef = storageRef.child("" + (x) + ".jpg")
            val mountainImagesRef = storageRef.child("images/" + x + ".jpg")
            mountainsRef.name == mountainImagesRef.name // true
            mountainsRef.path == mountainImagesRef.path // false
            val bitmap = (profile_image.drawable as BitmapDrawable).bitmap
            val baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            val data = baos.toByteArray()

            var uploadTask = mountainsRef.putBytes(data)
            uploadTask = storageRef.child("images/" + x + ".jpg").putBytes(data)

            uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                if (!task.isSuccessful) {
                    task.exception?.let {
                        throw it
                    }
                }
                return@Continuation mountainImagesRef.downloadUrl
            }).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val downloadUri = task.result
                    register(em, pass, personName, downloadUri)
                } else {
                    Toasty.success(
                        this@RegisterActivity,
                        R.string.failed_upload,
                        Toast.LENGTH_SHORT,
                        true
                    ).show()
                }
            }
        } else {
            //register user with email, and password after image was uplaoded to firebase storage.
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
                        //after successful registeration with email and password.Update user profile with his photo URL and displayname.
                        var user = auth.currentUser

                        val request = UserProfileChangeRequest.Builder()
                            .setPhotoUri(url)
                            .setDisplayName(personName)
                            .build()

                        user?.updateProfile(request)

                        val data = Intent()
                        data.putExtra("email", em)
                        data.putExtra("pass", pass)
                        setResult(RC_REGISTER, data)
                        finish()
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


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            CroperinoConfig.REQUEST_TAKE_PHOTO ->
                if (resultCode == Activity.RESULT_OK) {
                    /* Parameters of runCropImage = File, Activity Context, Image is Scalable or Not, Aspect Ratio X, Aspect Ratio Y, Button Bar Color, Background Color */
                    CroperinoFileUtil.newGalleryFile(data, this@RegisterActivity)
                    Croperino.runCropImage(
                        photoFile,
                        this@RegisterActivity,
                        true,
                        0,
                        0,
                        R.color.gray,
                        R.color.gray_variant
                    )
                }
            CroperinoConfig.REQUEST_PICK_FILE ->
                if (resultCode == Activity.RESULT_OK) {
                    CroperinoFileUtil.newGalleryFile(data, this@RegisterActivity)
                    photoFile = null
                    Croperino.runCropImage(
                        CroperinoFileUtil.getTempFile(),
                        this@RegisterActivity,
                        true,
                        0,
                        0,
                        R.color.gray,
                        R.color.gray_variant
                    )
                }
            CroperinoConfig.REQUEST_CROP_PHOTO ->
                if (resultCode == Activity.RESULT_OK) {
                    //cropped image returned is set to the imageview on  register layout
                    var i: Uri?
                    if (photoFile != null)
                        i = Uri.fromFile(photoFile!!)
                    else
                        i = Uri.fromFile(CroperinoFileUtil.getTempFile())
                    Glide.with(this@RegisterActivity).load(i)
                        .placeholder(R.drawable.profileplaceholder)
                        .apply(RequestOptions.circleCropTransform())
                        .into(
                            profile_image
                        )
                }
        }
    }

    override fun onBackPressed() {
        setResult(0, Intent())
        finish()
    }

    companion object {
        private const val RC_REGISTER = 9002
    }
}