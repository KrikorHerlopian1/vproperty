package edu.newhaven.krikorherlopian.android.vproperty.fragment

import android.graphics.Bitmap
import android.graphics.Typeface
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.anupcowkur.statelin.Machine
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import edu.newhaven.krikorherlopian.android.vproperty.*
import edu.newhaven.krikorherlopian.android.vproperty.model.User
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_profile.view.*
import java.io.ByteArrayOutputStream
import java.util.*

class ProfileFragment : Fragment() {
    lateinit var storage: FirebaseStorage
    val machine = Machine(stateView)
    var user: User? = User()
    var root: View? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.activity_profile, container, false)
        fragmentActivityCommunication!!.hideShowMenuItems(false)
        machine.state = stateView
        var tf = Typeface.createFromAsset(context?.assets, "" + font)
        root?.emailAddressInputLayout?.typeface = tf
        root?.displayNamenputLayout?.typeface = tf
        root?.email?.typeface = tf
        root?.displayName?.typeface = tf
        root?.email?.setText(loggedInUser?.email)
        root?.displayName?.setText(loggedInUser?.displayName)
        storage = FirebaseStorage.getInstance()
        var photoUrl = loggedInUser?.photoUrl
        Glide.with(context!!).load(photoUrl)
            .placeholder(R.drawable.profileplaceholder).apply(RequestOptions.circleCropTransform())
            .into(
                root?.profile_image!!
            )

        val db = FirebaseFirestore.getInstance()

        try {
            val docRef = db.collection("users").document(loggedInUser?.email?.toString()!!).get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        user = document.toObject(User::class.java)
                        root?.phone_number_input?.setText(user?.phoneNumber)
                    } else {
                    }
                }
                .addOnFailureListener { exception ->
                }

        } catch (e: Exception) {
        }


        root?.submitbutton?.setOnClickListener {
            if (root?.displayName?.text.isNullOrBlank()) {
                setError(null, null, true, false)
                root?.displayNamenputLayout?.error =
                    resources.getString(R.string.enter_display_name)
            } else if (root?.email?.text.isNullOrBlank()) {
                setError(null, null, false, true)
                root?.emailAddressInputLayout?.error = resources.getString(R.string.enter_email)
            } else if (!isEmailValid(root?.email?.text.toString())) {
                setError(null, null, false, true)
                root?.emailAddressInputLayout?.error =
                    resources.getString(R.string.enter_valid_email)
            } else {
                updateProfile()
            }
        }
        root?.profile_image?.setOnClickListener {
            if (machine.state == stateCreate) {
                fragmentActivityCommunication?.addProfileButtonClicked()
            }
        }
        root?.fab?.setOnClickListener {
            fabClick()
        }
        return root
    }

    private fun fabClick() {
        if (machine.state == stateView) {
            machine.state = stateCreate
            root?.displayName?.isEnabled = true
            root?.emailAddressInputLayout?.visibility = View.GONE
            root?.phone_number_input?.isEnabled = true
            root?.submitbutton?.visibility = View.VISIBLE
            root?.fab?.setImageDrawable(
                ContextCompat.getDrawable(
                    context!!,
                    R.drawable.ic_close_black_24dp
                )
            )
            root?.displayName?.requestFocus()
        } else if (machine.state == stateCreate) {
            machine.state = stateView
            root?.email?.setText(loggedInUser?.email)
            root?.emailAddressInputLayout?.visibility = View.VISIBLE
            root?.displayName?.setText(loggedInUser?.displayName)
            root?.phone_number_input?.setText(user?.phoneNumber)
            Glide.with(context!!).load(loggedInUser?.photoUrl)
                .placeholder(R.drawable.profileplaceholder)
                .apply(RequestOptions.circleCropTransform())
                .into(
                    root?.profile_image!!
                )
            setError(null, null, false, false)
            root?.submitbutton?.visibility = View.GONE
            root?.displayName?.isEnabled = false
            root?.phone_number_input?.isEnabled = false
            root?.fab?.setImageDrawable(
                ContextCompat.getDrawable(
                    context!!,
                    R.drawable.ic_edit_white_24dp
                )
            )
        }
    }

    private fun updateProfile() {
        val storageRef = storage.reference
        var x = UUID.randomUUID()
        val mountainsRef = storageRef.child("" + (x) + ".jpg")
        val mountainImagesRef = storageRef.child("images/" + x + ".jpg")
        mountainsRef.name == mountainImagesRef.name // true
        mountainsRef.path == mountainImagesRef.path // false
        val bitmap = (root?.profile_image?.drawable as BitmapDrawable).bitmap
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()

        var uploadTask = mountainsRef.putBytes(data)
        uploadTask = storageRef.child("images/" + x + ".jpg").putBytes(data)
        root?.submitbutton?.isEnabled = false
        root?.progressbar?.visibility = View.VISIBLE
        uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
            if (!task.isSuccessful) {
                root?.progressbar?.visibility = View.GONE
                root?.submitbutton?.isEnabled = true
                task.exception?.let {
                    Toasty.success(
                        root?.context!!,
                        it.localizedMessage,
                        Toast.LENGTH_SHORT,
                        true
                    ).show()
                    throw it
                }
            }
            return@Continuation mountainImagesRef.downloadUrl
        }).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                root?.submitbutton?.isEnabled = true
                root?.progressbar?.visibility = View.GONE
                val downloadUri = task.result
                val request = UserProfileChangeRequest.Builder()
                    .setPhotoUri(downloadUri)
                    .setDisplayName(root?.displayName?.text.toString())
                    .build()
                loggedInUser?.updateProfile(request)
                fragmentActivityCommunication?.updateProfile()
                Toasty.success(
                    root?.context!!,
                    R.string.success_update_profile,
                    Toast.LENGTH_SHORT,
                    true
                ).show()
                val db = FirebaseFirestore.getInstance()
                user = User(root?.phone_number_input?.text.toString())
                db.collection("users").document(loggedInUser?.email.toString()).set(user!!)
                    .addOnSuccessListener { documentReference ->
                    }
                    .addOnFailureListener { e -> }
            } else {
                root?.progressbar?.visibility = View.GONE
                root?.submitbutton?.isEnabled = true
                Toasty.success(
                    root?.context!!,
                    R.string.process_failed,
                    Toast.LENGTH_SHORT,
                    true
                ).show()
            }
        }
    }

    private fun setError(
        personError: CharSequence?,
        emailError: CharSequence?,
        personErrorEnabled: Boolean,
        emailErrorEnabled: Boolean
    ) {
        root?.displayNamenputLayout?.error = personError
        root?.emailAddressInputLayout?.error = emailError
        root?.displayNamenputLayout?.isErrorEnabled = personErrorEnabled
        root?.emailAddressInputLayout?.isErrorEnabled = emailErrorEnabled
    }
}