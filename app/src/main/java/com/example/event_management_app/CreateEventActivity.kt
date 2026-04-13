package com.example.event_management_app

import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity

class CreateEventActivity : AppCompatActivity() {

    lateinit var db: DBHelper

    lateinit var etTitle: EditText
    lateinit var spinnerCategory: Spinner
    lateinit var etDesc: EditText
    lateinit var etDate: EditText
    lateinit var etTime: EditText
    lateinit var etVenue: EditText
    lateinit var etMax: EditText
    lateinit var etRules: EditText
    lateinit var etGuidelines: EditText

    lateinit var imgView: ImageView
    lateinit var btnUploadImage: Button
    lateinit var btnCamera: Button
    lateinit var btnSubmit: Button

    var imageUri: Uri? = null

    // ✅ CAMERA (SAFE)
    val cameraLauncher = registerForActivityResult(
        ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->

        if (bitmap != null) {
            imgView.setImageBitmap(bitmap)
            imageUri = getImageUri(bitmap)
        } else {
            Toast.makeText(this, "Camera failed", Toast.LENGTH_SHORT).show()
        }
    }

    // ✅ GALLERY
    val galleryLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        imageUri = uri
        imgView.setImageURI(uri)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_event)

        db = DBHelper(this)

        etTitle = findViewById(R.id.etTitle)
        spinnerCategory = findViewById(R.id.spinnerCategory)
        etDesc = findViewById(R.id.etDescription)
        etDate = findViewById(R.id.etDate)
        etTime = findViewById(R.id.etTime)
        etVenue = findViewById(R.id.etVenue)
        etMax = findViewById(R.id.etMax)
        etRules = findViewById(R.id.etRules)
        etGuidelines = findViewById(R.id.etGuidelines)

        imgView = findViewById(R.id.imgPreview)
        btnUploadImage = findViewById(R.id.btnUploadImage)
        btnCamera = findViewById(R.id.btnCamera)
        btnSubmit = findViewById(R.id.btnSubmit)

        // Spinner
        val categories = arrayOf("Tech", "Sports", "Cultural", "Workshop")
        spinnerCategory.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            categories
        )

        // Gallery
        btnUploadImage.setOnClickListener {
            galleryLauncher.launch("image/*")
        }

        // Camera
        btnCamera.setOnClickListener {
            cameraLauncher.launch(null)
        }

        // Submit
        btnSubmit.setOnClickListener {

            val title = etTitle.text.toString()
            val cat = spinnerCategory.selectedItem.toString()
            val desc = etDesc.text.toString()
            val date = etDate.text.toString()
            val time = etTime.text.toString()
            val venue = etVenue.text.toString()
            val max = etMax.text.toString()
            val rules = etRules.text.toString()
            val guidelines = etGuidelines.text.toString()

            if (title.isEmpty() || desc.isEmpty()) {
                Toast.makeText(this, "Fill all required fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val image = imageUri?.toString() ?: ""
            val finalDesc = "$desc\n\nRules: $rules\nGuidelines: $guidelines"

            val result = db.insertEvent(
                title, cat, finalDesc, date, time, venue, max, image
            )

            if (result) {
                Toast.makeText(this, "Event Created ✅", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Failed ❌", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // ✅ Convert bitmap to URI
    private fun getImageUri(bitmap: Bitmap): Uri {
        val path = MediaStore.Images.Media.insertImage(
            contentResolver,
            bitmap,
            "EventImage",
            null
        )

        return if (path != null) Uri.parse(path) else Uri.EMPTY
    }
}