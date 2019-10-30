package edu.newhaven.krikorherlopian.android.vproperty.activity

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import com.mikelau.croperino.Croperino
import com.mikelau.croperino.CroperinoConfig
import com.mikelau.croperino.CroperinoFileUtil
import com.schibstedspain.leku.*
import com.stepstone.stepper.StepperLayout
import com.stepstone.stepper.VerificationError
import edu.newhaven.krikorherlopian.android.vproperty.R
import edu.newhaven.krikorherlopian.android.vproperty.adapter.MyStepperAdapter
import edu.newhaven.krikorherlopian.android.vproperty.interfaces.OnNavigationBarListener
import edu.newhaven.krikorherlopian.android.vproperty.loggedInUser
import edu.newhaven.krikorherlopian.android.vproperty.model.BuildingDetails
import edu.newhaven.krikorherlopian.android.vproperty.model.Property
import edu.newhaven.krikorherlopian.android.vproperty.model.RoomDetails
import edu.newhaven.krikorherlopian.android.vproperty.model.UtilityDetails
import edu.newhaven.krikorherlopian.android.vproperty.setUpPermissions
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.add_property.*
import kotlinx.android.synthetic.main.add_property.toolbar
import kotlinx.android.synthetic.main.fragment_step_home_address.addressName
import kotlinx.android.synthetic.main.fragment_step_home_address.latitudeInput
import kotlinx.android.synthetic.main.fragment_step_home_address.longitudeInput
import kotlinx.android.synthetic.main.fragment_step_home_address.zipCodeInput
import kotlinx.android.synthetic.main.stepper.*
import mumayank.com.airdialog.AirDialog
import java.io.ByteArrayOutputStream
import java.util.*

class AddPropertyStepperActivity : AppCompatActivity(), StepperLayout.StepperListener,
    OnNavigationBarListener {
    lateinit var storage: FirebaseStorage
    var state: Int = 0
    var property: Property = Property()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.stepper)
        setupToolBar()
        storage = FirebaseStorage.getInstance()
        setUpPermissions(this)
        prepareCroperino()
        stepperLayout.setAdapter(MyStepperAdapter(this, this))
        stepperLayout.setListener(this)
        progress_bar.visibility = View.GONE
    }

    private fun setupToolBar() {
        setSupportActionBar(toolbar)
        val actionBar = supportActionBar
        actionBar!!.title = resources.getString(R.string.hometype)
        actionBar.elevation = 4.0F
        actionBar.setDisplayShowHomeEnabled(true)
        actionBar.setDisplayUseLogoEnabled(true)
        actionBar.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener(View.OnClickListener {
            super.onBackPressed()
        })
    }

    fun showLocationPicker() {
        val locationPickerIntent = LocationPickerActivity.Builder()
            .withGeolocApiKey(resources.getString(R.string.map_key))
            .withDefaultLocaleSearchZone()
            .withSatelliteViewHidden()
            .withGooglePlacesEnabled()
            .withGoogleTimeZoneEnabled()
            .withUnnamedRoadHidden()
            .build(applicationContext)
        startActivityForResult(locationPickerIntent, AddPropertyStepperActivity.RC_LCOATION)
    }

    override fun onCompleted(completeButton: View?) {
    }

    override fun onStepSelected(newStepPosition: Int) {
        add_location.visibility = View.GONE
        if (newStepPosition == 0)
            supportActionBar?.setTitle(R.string.hometype)
        else if (newStepPosition == 1) {
            add_location.visibility = View.VISIBLE
            add_location.setOnClickListener {
                showLocationPicker()
            }
            supportActionBar?.setTitle(R.string.address)
        } else if (newStepPosition == 2)
            supportActionBar?.setTitle(R.string.homefacts)
        else if (newStepPosition == 3)
            supportActionBar?.setTitle(R.string.roomdetails)
        else if (newStepPosition == 4)
            supportActionBar?.setTitle(R.string.buildingdetails)
        else if (newStepPosition == 5)
            supportActionBar?.setTitle(R.string.utilitydetails)
        else if (newStepPosition == 6)
            supportActionBar?.setTitle(R.string.more_information)
    }

    private fun prepareCroperino() {
        //prepare camera, gallery and ask for storage permissions.
        CroperinoConfig(
            "IMG_" + System.currentTimeMillis() + ".jpg",
            "/VProperty/Pictures",
            "/sdcard/VProperty/Pictures"
        )
        CroperinoFileUtil.setupDirectory(this@AddPropertyStepperActivity)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK && data != null) {
            when (requestCode) {
                CroperinoConfig.REQUEST_TAKE_PHOTO ->
                    if (resultCode == Activity.RESULT_OK) {
                        /* Parameters of runCropImage = File, Activity Context, Image is Scalable or Not, Aspect Ratio X, Aspect Ratio Y, Button Bar Color, Background Color */
                        Croperino.runCropImage(
                            CroperinoFileUtil.getTempFile(),
                            this@AddPropertyStepperActivity,
                            true,
                            1,
                            1,
                            R.color.gray,
                            R.color.gray_variant
                        )
                    }
                CroperinoConfig.REQUEST_PICK_FILE ->
                    if (resultCode == Activity.RESULT_OK) {
                        CroperinoFileUtil.newGalleryFile(data, this@AddPropertyStepperActivity)
                        Croperino.runCropImage(
                            CroperinoFileUtil.getTempFile(),
                            this@AddPropertyStepperActivity,
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
                        var i = Uri.fromFile(CroperinoFileUtil.getTempFile())
                        Glide.with(this@AddPropertyStepperActivity).load(i)
                            .placeholder(R.drawable.placeholderdetail)
                            .into(
                                picture
                            )
                    }
                RC_LCOATION -> {
                    val latitude = data.getDoubleExtra(LATITUDE, 0.0)
                    val longitude = data.getDoubleExtra(LONGITUDE, 0.0)
                    val address = data.getStringExtra(LOCATION_ADDRESS)
                    val postalcode = data.getStringExtra(ZIPCODE)
                    latitudeInput.setText("" + latitude)
                    longitudeInput.setText("" + longitude)
                    addressName.setText(address.toString())
                    zipCodeInput.setText(postalcode.toString())
                }
            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            Log.d("RESULT****", "CANCELLED")
        }
        super.onActivityResult(requestCode, resultCode, data)
    }


    override fun addPictureClicked() {
        /*Croperino.prepareChooser(
            this@AddPropertyStepperActivity,
            "" + resources.getString(R.string.capture_photo),
            ContextCompat.getColor(this@AddPropertyStepperActivity, R.color.colorPrimaryDark)
        )*/
        AirDialog.show(
            activity = this,                      // mandatory
            title = "" + resources.getString(R.string.app_name),              // mandatory
            message = "" + resources.getString(R.string.take_image),          // mandatory
            iconDrawableId = R.drawable.ic_camera_alt_black_24dp,
            isCancelable = false,
            airButton1 = AirDialog.Button("" + resources.getString(R.string.camera)) {
                // do something
                Croperino.prepareCamera(this@AddPropertyStepperActivity)
            },
            airButton2 = AirDialog.Button("" + resources.getString(android.R.string.cancel)) {
                // do something
            },
            airButton3 = AirDialog.Button("" + resources.getString(R.string.menu_gallery)) {
                // do something
                Croperino.prepareGallery(this@AddPropertyStepperActivity)
            }
        )
    }

    override fun addHomeFacts(
        price: String,
        isRent: Boolean,
        isSale: Boolean,
        bedrooms: String,
        bathrooms: String,
        totalRooms: String,
        parkingSpaces: String,
        yearBuilt: String,
        hoadues: String,
        structuralModalYear: String,
        floorNumber: String,
        finishedSqFt: String,
        lotSizeFqFt: String,
        basementSqFt: String,
        garageSqFt: String
    ) {
        property.homeFacts.price = price
        property.homeFacts.isRent = isRent
        property.homeFacts.isSale = isSale
        property.homeFacts.bedrooms = bedrooms
        property.homeFacts.bathrooms = bathrooms
        property.homeFacts.totalRooms = totalRooms
        property.homeFacts.parkingSpaces = parkingSpaces
        property.homeFacts.yearBuilt = yearBuilt
        property.homeFacts.hoadues = hoadues
        property.homeFacts.structuralModalYear = structuralModalYear
        property.homeFacts.floorNumber = floorNumber
        property.homeFacts.finishedSqFt = finishedSqFt
        property.homeFacts.lotSizeFqFt = lotSizeFqFt
        property.homeFacts.basementSqFt = basementSqFt
        property.homeFacts.garageSqFt = garageSqFt
    }

    override fun finishStep(
        relatedWebsite: String,
        virtualTour: String,
        contactinput: String,
        bitmap: Bitmap
    ) {
        if (state == 0) {
            state = 1
            property.virtualTour = virtualTour
            property.relatedWebsite = relatedWebsite
            property.contactPhone = contactinput
            val storageRef = storage.reference
            var x = UUID.randomUUID()
            val mountainsRef = storageRef.child("" + (x) + ".jpg")
            val mountainImagesRef = storageRef.child("images/" + x + ".jpg")
            mountainsRef.name == mountainImagesRef.name // true
            mountainsRef.path == mountainImagesRef.path // false

            val baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            val data = baos.toByteArray()

            var uploadTask = mountainsRef.putBytes(data)
            uploadTask = storageRef.child("images/" + x + ".jpg").putBytes(data)

            progress_bar.visibility = View.VISIBLE
            uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                if (!task.isSuccessful) {
                    task.exception?.let {
                        throw it
                    }
                    state = 0
                    progress_bar.visibility = View.GONE
                }
                return@Continuation mountainImagesRef.downloadUrl
            }).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val downloadUri = task.result
                    uploadProperty(downloadUri)
                } else {
                    state = 0
                    progress_bar.visibility = View.GONE
                    Toasty.success(
                        this@AddPropertyStepperActivity,
                        R.string.failed_upload,
                        Toast.LENGTH_SHORT,
                        true
                    ).show()
                }
            }
        }

    }


    private fun uploadProperty(downloadUrl: Uri?) {
        val db = FirebaseFirestore.getInstance()
        property.photoUrl = downloadUrl.toString()
        db.collection("properties")
            .add(property)
            .addOnSuccessListener { documentReference ->
                Toasty.success(
                    this@AddPropertyStepperActivity,
                    R.string.success_property,
                    Toast.LENGTH_SHORT,
                    true
                ).show()
                progress_bar.visibility = View.GONE
                state = 0
                finish()
            }
            .addOnFailureListener { e ->
                progress_bar.visibility = View.GONE
                state = 0
                Toasty.success(
                    this@AddPropertyStepperActivity,
                    R.string.error_adding_property,
                    Toast.LENGTH_SHORT,
                    true
                ).show()
            }

    }

    override fun addRoomDetails(room: RoomDetails) {
        property.roomDetails = room
    }

    override fun addBuildingDetails(buildingDetails: BuildingDetails) {
        property.buildingDetails = buildingDetails
    }

    override fun addUtilityDetails(utilityDetails: UtilityDetails) {
        property.utilityDetails = utilityDetails
    }


    override fun addAddress(
        displayName: String,
        address: String,
        zipCode: String,
        longitude: String,
        latitude: String,
        descriptionAddress: String
    ) {
        property.houseName = displayName
        property.address.addressName = address
        property.address.zipCode = zipCode
        property.address.latitude = latitude
        property.address.longitude = longitude
        property.address.descriptionAddress = descriptionAddress
        property.email = loggedInUser?.email.toString()
    }

    override fun addHomeType(typeCode: String) {
        property.homeFacts.homeType = typeCode
        stepperLayout.currentStepPosition = 1
    }

    override fun onError(verificationError: VerificationError?) {
    }

    override fun onReturn() {
        finish()
    }

    companion object {
        private const val RC_LCOATION = 9003
    }
}