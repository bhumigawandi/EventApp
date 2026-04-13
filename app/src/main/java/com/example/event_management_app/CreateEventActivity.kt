package com.example.event_management_app

import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import java.util.Calendar

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

    // ✅ CAMERA (STABLE)
    private val cameraLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->

        if (result.resultCode == RESULT_OK) {

            val bitmap = result.data?.extras?.get("data") as? Bitmap

            if (bitmap != null) {
                imgView.setImageBitmap(bitmap)
                imageUri = getImageUri(bitmap)
            } else {
                Toast.makeText(this, "No image captured", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // ✅ GALLERY
    private val galleryLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        imageUri = uri
        imgView.setImageURI(uri)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_event)

        db = DBHelper(this)

        // 🔹 Bind Views
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

        // 🔹 DATE PICKER
        etDate.setOnClickListener {

            val calendar = Calendar.getInstance()

            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePicker = DatePickerDialog(
                this,
                { _, y, m, d ->
                    val selectedDate = "$d-${m + 1}-$y"
                    etDate.setText(selectedDate)
                },
                year, month, day
            )

            datePicker.show()
        }

        // 🔹 Upload Image
        btnUploadImage.setOnClickListener {
            galleryLauncher.launch("image/*")
        }

        // 🔹 Camera
        btnCamera.setOnClickListener {
            try {
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                cameraLauncher.launch(intent)
            } catch (e: Exception) {
                Toast.makeText(this, "Camera not supported", Toast.LENGTH_SHORT).show()
            }
        }

        // 🔹 Submit Event
        btnSubmit.setOnClickListener {

            val title = etTitle.text.toString()
            val desc = etDesc.text.toString()

            if (title.isEmpty() || desc.isEmpty()) {
                Toast.makeText(this, "Fill required fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val result = db.insertEvent(
                title,
                spinnerCategory.selectedItem.toString(),
                desc,
                etDate.text.toString(),
                etTime.text.toString(),
                etVenue.text.toString(),
                etMax.text.toString(),
                imageUri?.toString() ?: ""
            )

            if (result) {
                Toast.makeText(this, "Event Created ✅", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Failed ❌", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // 🔹 Bitmap → URI (SAFE)
    private fun getImageUri(bitmap: Bitmap): Uri {
        return try {
            val path = MediaStore.Images.Media.insertImage(
                contentResolver,
                bitmap,
                "EventImage",
                null
            )
            if (path != null) Uri.parse(path) else Uri.EMPTY
        } catch (e: Exception) {
            Uri.EMPTY
        }
    }
}