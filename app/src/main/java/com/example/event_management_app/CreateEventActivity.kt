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

    lateinit var imgView: ImageView
    lateinit var btnUploadImage: Button
    lateinit var btnCamera: Button
    lateinit var btnSubmit: Button

    var imageUri: Uri? = null

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

        etTitle = findViewById(R.id.etTitle)
        spinnerCategory = findViewById(R.id.spinnerCategory)
        etDesc = findViewById(R.id.etDescription)
        etDate = findViewById(R.id.etDate)
        etTime = findViewById(R.id.etTime)
        etVenue = findViewById(R.id.etVenue)
        etMax = findViewById(R.id.etMax)

        imgView = findViewById(R.id.imgPreview)
        btnUploadImage = findViewById(R.id.btnUploadImage)
        btnCamera = findViewById(R.id.btnCamera)
        btnSubmit = findViewById(R.id.btnSubmit)

        val categories = arrayOf("Tech", "Sports", "Cultural", "Workshop")
        spinnerCategory.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            categories
        )

        // 📅 DATE PICKER
        etDate.setOnClickListener {

            val cal = Calendar.getInstance()

            DatePickerDialog(
                this,
                { _, y, m, d ->
                    etDate.setText("$d-${m + 1}-$y")
                },
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        // 🖼 GALLERY
        btnUploadImage.setOnClickListener {
            galleryLauncher.launch("image/*")
        }

        // 📸 CAMERA
        btnCamera.setOnClickListener {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            cameraLauncher.launch(intent)
        }

        // 🚀 SUBMIT EVENT
        btnSubmit.setOnClickListener {

            val title = etTitle.text.toString()
            val desc = etDesc.text.toString()
            val date = etDate.text.toString()
            val time = etTime.text.toString()

            if (title.isEmpty() || desc.isEmpty() || date.isEmpty() || time.isEmpty()) {
                Toast.makeText(this, "Fill all required fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // ❌ CHECK CONFLICT
            if (db.isEventConflict(date, time)) {
                Toast.makeText(this, "Event already exists at this time ❌", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            // ✅ INSERT EVENT
            val result = db.insertEvent(
                title,
                spinnerCategory.selectedItem.toString(),
                desc,
                date,
                time,
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