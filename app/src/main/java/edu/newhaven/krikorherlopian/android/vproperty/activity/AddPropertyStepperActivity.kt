package edu.newhaven.krikorherlopian.android.vproperty.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.location.Geocoder
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.anupcowkur.statelin.Machine
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
import edu.newhaven.krikorherlopian.android.vproperty.*
import edu.newhaven.krikorherlopian.android.vproperty.R
import edu.newhaven.krikorherlopian.android.vproperty.adapter.MyStepperAdapter
import edu.newhaven.krikorherlopian.android.vproperty.interfaces.ApiInterface
import edu.newhaven.krikorherlopian.android.vproperty.interfaces.OnNavigationBarListener
import edu.newhaven.krikorherlopian.android.vproperty.model.*
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.add_property.*
import kotlinx.android.synthetic.main.add_property.toolbar
import kotlinx.android.synthetic.main.custom_croperino_dialog.view.*
import kotlinx.android.synthetic.main.fragment_step_home_address.addressName
import kotlinx.android.synthetic.main.fragment_step_home_address.latitudeInput
import kotlinx.android.synthetic.main.fragment_step_home_address.longitudeInput
import kotlinx.android.synthetic.main.fragment_step_home_address.zipCodeInput
import kotlinx.android.synthetic.main.stepper.*
import okhttp3.MediaType
import okhttp3.RequestBody
import retrofit2.Callback
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

//this is both to add property , and also edit property.
class AddPropertyStepperActivity : AppCompatActivity(), StepperLayout.StepperListener,
    OnNavigationBarListener {
    val BASE_URL = "https://fcm.googleapis.com/"
    private var retrofit: Retrofit? = null
    val machine = Machine(addModifyProperty)
    lateinit var storage: FirebaseStorage
    var property: Property = Property()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.stepper)
        setupToolBar()
        machine.state = addModifyProperty
        try {
            //in case property to be edited, we would get all our property properties needed here.
            property = intent.getSerializableExtra("argPojo") as Property
        } catch (e: Exception) {
        }
        storage = FirebaseStorage.getInstance()
        setUpPermissions(this)
        prepareCroperino()
        stepperLayout.setAdapter(MyStepperAdapter(this, this, property))
        stepperLayout.setListener(this)
        progress_bar.visibility = View.GONE

    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(LocaleHelper.onAttach(base))
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

    //call the map library for user to select the property location.
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

    //on every step of add/edit property wizard update the page title.
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


    fun getClient(): Retrofit {
        if (retrofit == null) {
            retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
        return retrofit!!
    }

    //on add property only, we send notification to all users that a new property is added.
    fun sendNotificationToPatner() {

        val sendNotificationModel = SendNotificationModel(
            "" + property.houseName.toUpperCase(),
            "" + resources.getString(R.string.new_property_added)
        )
        val requestNotificaton = RequestNotificaton()
        requestNotificaton.sendNotificationModel = sendNotificationModel
        var title = resources.getString(R.string.new_property_added)
        var subTitle =
            resources.getString(R.string.property_name) + " = " + property.houseName + "\n" + resources.getString(
                R.string.address
            ) + " = " + property.address.addressName + "\n"
        var price = String.format("%,.2f", property.homeFacts.price?.toFloat())
        if (property.homeFacts.isRent) {
            title = title + " " + resources.getString(R.string.forrent)
            subTitle =
                subTitle + "" + resources.getString(R.string.price_per_month) + " = " + price + " $$"
        } else {
            title = title + " " + resources.getString(R.string.forsale)
            subTitle = subTitle + "" + resources.getString(R.string.price) + " = " + price + " $$"
        }

        var postJsonData = "{\n" +
                " \"to\" : \"/topics/vproperty\",\n" +
                " \"collapse_key\" : \"type_a\",\n" +
                " \"notification\" : {\n" +
                "     \"body\" : \"" + subTitle + "\",\n" +
                "     \"title\": \"" + title + "\"\n" +
                " },\n" +
                " \"data\" : {\n" +
                "     \"body\" : \"Body of Your Notification in Data\",\n" +
                "     \"title\": \"Title of Your Notification in Title\",\n" +
                "     \"key_1\" : \"Value for key_1\",\n" +
                "     \"key_2\" : \"Value for key_2\"\n" +
                " }\n" +
                "}"

        var apiService = getClient().create(ApiInterface::class.java)
        var body =
            RequestBody.create(MediaType.parse("application/json"), postJsonData)
        val responseBodyCall = apiService.sendChatNotification(body)
        responseBodyCall.enqueue(object : Callback<com.squareup.okhttp.ResponseBody> {
            override fun onResponse(
                call: retrofit2.Call<com.squareup.okhttp.ResponseBody>,
                response: retrofit2.Response<com.squareup.okhttp.ResponseBody>
            ) {
            }

            override fun onFailure(
                call: retrofit2.Call<com.squareup.okhttp.ResponseBody>,
                t: Throwable
            ) {
            }
        })
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


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK && data != null) {
            when (requestCode) {
                CroperinoConfig.REQUEST_TAKE_PHOTO ->
                    if (resultCode == Activity.RESULT_OK) {
                        /* Parameters of runCropImage = File, Activity Context, Image is Scalable or Not, Aspect Ratio X, Aspect Ratio Y, Button Bar Color, Background Color */
                        /* Croperino.runCropImage(
                             CroperinoFileUtil.getTempFile(),
                             this@AddPropertyStepperActivity,
                             true,
                             1,
                             1,
                             R.color.gray,
                             R.color.gray_variant
                         )*/
                        CroperinoFileUtil.newGalleryFile(data, this@AddPropertyStepperActivity)
                        Croperino.runCropImage(
                            photoFile,
                            this@AddPropertyStepperActivity,
                            true,
                            0,
                            0,
                            R.color.gray,
                            R.color.gray_variant
                        )
                    }
                CroperinoConfig.REQUEST_PICK_FILE ->
                    if (resultCode == Activity.RESULT_OK) {
                        CroperinoFileUtil.newGalleryFile(data, this@AddPropertyStepperActivity)
                        photoFile = null
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
                        var i: Uri?
                        if (photoFile != null)
                            i = Uri.fromFile(photoFile!!)
                        else
                            i = Uri.fromFile(CroperinoFileUtil.getTempFile())
                        Glide.with(this@AddPropertyStepperActivity).load(i)
                            .placeholder(R.drawable.placeholderdetail)
                            .into(
                                picture
                            )
                    }
                RC_LCOATION -> {
                    try {
                        val latitude = data.getDoubleExtra(LATITUDE, 0.0)
                        val longitude = data.getDoubleExtra(LONGITUDE, 0.0)
                        val address = data.getStringExtra(LOCATION_ADDRESS)
                        val postalcode = data.getStringExtra(ZIPCODE)
                        latitudeInput.setText("" + latitude)
                        longitudeInput.setText("" + longitude)
                        addressName.setText(address.toString())
                        zipCodeInput.setText(postalcode.toString())

                        val geocoder = Geocoder(this, Locale.getDefault())
                        val addresses =
                            geocoder.getFromLocation(latitude, longitude, 1)
                        val cityName = addresses[0].getAddressLine(0)
                        val stateName = addresses[0].getAddressLine(1)
                        val countryName = addresses[0].getAddressLine(2)
                        var add: String = ""
                        if (address == null || address.trim().equals("")) {
                            if (cityName != null) {
                                add = cityName
                                if (stateName != null) {
                                    add = add + " ," + stateName
                                }
                                addressName.setText(add)
                            } else if (stateName != null) {
                                add = stateName
                                addressName.setText(add)
                            }
                        }


                        var apiService = getClient2().create(ApiInterface::class.java)
                        var initialAddress = cityName.substringAfter(',')
                        var statePlace = initialAddress.substringBefore(',')
                        var zipeCode = initialAddress.substringAfter(',').substringBefore(',')
                        val responseBodyCall = apiService.zestimate(
                            "X1-ZWz1hi41bwnksr_5p30l",
                            "" + address.substringBefore(','),
                            "" + statePlace + "," + zipeCode,
                            "true"
                        )
                        responseBodyCall.enqueue(object : Callback<okhttp3.ResponseBody> {
                            override fun onResponse(
                                call: retrofit2.Call<okhttp3.ResponseBody>,
                                response: retrofit2.Response<okhttp3.ResponseBody>
                            ) {
                                if (response.isSuccessful) {
                                    var responseData = response.body()!!.string()
                                    var amount =
                                        responseData.substringAfter("<rentzestimate><amount currency=\"USD\">")
                                            .substringBefore("</amount>")
                                    if (amount != null && !amount.trim().equals("")) {
                                        Toasty.success(
                                            this@AddPropertyStepperActivity,
                                            R.string.estimate + " " + amount,
                                            Toast.LENGTH_SHORT,
                                            true
                                        ).show()
                                    }
                                }
                            }

                            override fun onFailure(
                                call: retrofit2.Call<okhttp3.ResponseBody>,
                                t: Throwable
                            ) {
                            }
                        })

                    } catch (e: Exception) {
                    }

                }

            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            Log.d("RESULT****", "CANCELLED")
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private var retrofit2: Retrofit? = null
    fun getClient2(): Retrofit {
        if (retrofit2 == null) {
            retrofit2 = Retrofit.Builder()
                .baseUrl("https://www.zillow.com/webservice/")
                .build()
        }
        return retrofit2!!
    }

    //open custom dialog for user to choose between selecting photo from gallery or take a photo from camera.
    override fun addPictureClicked() {
        val alerBuilder = AlertDialog.Builder(this@AddPropertyStepperActivity)
        val dialogView = layoutInflater.inflate(R.layout.custom_croperino_dialog, null)

        alerBuilder.setView(dialogView)
        val alert = alerBuilder.setCancelable(true).setTitle("").create()
        alert.window?.attributes?.windowAnimations = R.style.DialogAnimation
        alert.show()
        dialogView.close.setOnClickListener {
            alert.dismiss()
        }
        dialogView.camera.setOnClickListener {
            dispatchTakePictureIntent()
            //Croperino.prepareCamera(this@AddPropertyStepperActivity)
            alert.dismiss()
        }
        dialogView.gallery.setOnClickListener {
            Croperino.prepareGallery(this@AddPropertyStepperActivity)
            alert.dismiss()
        }
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
        property.homeFacts.price = price.toFloat()
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

    //last step  to add/edit property.
    override fun finishStep(
        relatedWebsite: String,
        virtualTour: String,
        contactinput: String,
        bitmap: Bitmap
    ) {
        if (machine.state == addModifyProperty) {
            machine.state = internetCall
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
                    machine.state = addModifyProperty
                    progress_bar.visibility = View.GONE
                }
                return@Continuation mountainImagesRef.downloadUrl
            }).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val downloadUri = task.result
                    uploadProperty(downloadUri)
                } else {
                    machine.state = addModifyProperty
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

    //upload image of property, before saving information into database with respective url.
    private fun uploadProperty(downloadUrl: Uri?) {
        val db = FirebaseFirestore.getInstance()
        property.photoUrl = downloadUrl.toString()
        if (property.id == null || property.id.trim().equals("")) {
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
                    machine.state = addModifyProperty
                    sendNotificationToPatner()
                    property.id = documentReference.id
                    try {
                        activityFunctionalities?.closeActivity()
                    } catch (e: Exception) {
                    }
                    val i = Intent(
                        this@AddPropertyStepperActivity,
                        PropertyDetailsActivity::class.java
                    )
                    i.putExtra("argPojo", property)
                    startActivity(i)
                    finish()
                }
                .addOnFailureListener { e ->
                    progress_bar.visibility = View.GONE
                    machine.state = addModifyProperty
                    Toasty.success(
                        this@AddPropertyStepperActivity,
                        R.string.error_adding_property,
                        Toast.LENGTH_SHORT,
                        true
                    ).show()
                }
        } else {
            db.collection("properties")
                .document(property.id)
                .set(property)
                .addOnSuccessListener { documentReference ->
                    Toasty.success(
                        this@AddPropertyStepperActivity,
                        R.string.success_property,
                        Toast.LENGTH_SHORT,
                        true
                    ).show()
                    progress_bar.visibility = View.GONE
                    machine.state = addModifyProperty
                    property.id = property.id
                    try {
                        activityFunctionalities?.closeActivity()
                    } catch (e: Exception) {
                    }
                    val i = Intent(
                        this@AddPropertyStepperActivity,
                        PropertyDetailsActivity::class.java
                    )
                    i.putExtra("argPojo", property)
                    startActivity(i)
                    finish()
                }
                .addOnFailureListener { e ->
                    progress_bar.visibility = View.GONE
                    machine.state = addModifyProperty
                    Toasty.success(
                        this@AddPropertyStepperActivity,
                        R.string.error_adding_property,
                        Toast.LENGTH_SHORT,
                        true
                    ).show()
                }
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

    override fun addHomeType(typeCode: String, forward: Int) {
        property.homeFacts.homeType = typeCode
        if (forward != -1)
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