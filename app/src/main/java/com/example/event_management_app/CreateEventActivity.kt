package com.example.event_management_app

import android.app.Activity
import android.content.Intent
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

    // 📸 CAMERA
    val cameraLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val bitmap = result.data?.extras?.get("data") as Bitmap
            imgView.setImageBitmap(bitmap)
            imageUri = getImageUri(bitmap)
        }
    }

    // 📁 GALLERY
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

        // ✅ FIXED IDS
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

        // 🔹 Spinner Data
        val categories = arrayOf("Tech", "Sports", "Cultural", "Workshop")
        spinnerCategory.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            categories
        )

        // 📁 Upload Image
        btnUploadImage.setOnClickListener {
            galleryLauncher.launch("image/*")
        }

        // 📸 Camera
        btnCamera.setOnClickListener {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            cameraLauncher.launch(intent)
        }

        // ✅ SUBMIT
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

            val finalDesc = desc + "\n\nRules: $rules\nGuidelines: $guidelines"

            val result = db.insertEvent(
                title,
                cat,
                finalDesc,
                date,
                time,
                venue,
                max,
                image
            )

            if (result) {
                Toast.makeText(this, "Event Created", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // 🔹 Bitmap → URI
    private fun getImageUri(bitmap: Bitmap): Uri {
        val path = MediaStore.Images.Media.insertImage(
            contentResolver,
            bitmap,
            "EventImage",
            null
        )
        return Uri.parse(path)
    }
}