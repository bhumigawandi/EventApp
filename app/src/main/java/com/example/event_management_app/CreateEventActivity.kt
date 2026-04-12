package com.example.event_management_app

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class CreateEventActivity : AppCompatActivity() {

    lateinit var db: DBHelper
    lateinit var imgPreview: ImageView

    private val pickImage = 100
    private val cameraCode = 200

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_event)

        db = DBHelper(this)

        // Inputs
        val title = findViewById<EditText>(R.id.etTitle)
        val spinner = findViewById<Spinner>(R.id.spinnerCategory)
        val description = findViewById<EditText>(R.id.etDescription)
        val date = findViewById<EditText>(R.id.etDate)
        val time = findViewById<EditText>(R.id.etTime)
        val venue = findViewById<EditText>(R.id.etVenue)
        val max = findViewById<EditText>(R.id.etMax)

        val btnUpload = findViewById<Button>(R.id.btnUploadImage)
        val btnCamera = findViewById<Button>(R.id.btnCamera)
        val btnSubmit = findViewById<Button>(R.id.btnSubmit)

        imgPreview = findViewById(R.id.imgPreview)

        // Spinner data
        val categories = arrayOf("Technical", "Cultural", "Sports", "Workshop")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, categories)
        spinner.adapter = adapter

        // Gallery
        btnUpload.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, pickImage)
        }

        // Camera
        btnCamera.setOnClickListener {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(intent, cameraCode)
        }

        // Submit Event
        btnSubmit.setOnClickListener {

            val t = title.text.toString().trim()
            val c = spinner.selectedItem.toString()
            val d = description.text.toString().trim()
            val dt = date.text.toString().trim()
            val tm = time.text.toString().trim()
            val v = venue.text.toString().trim()
            val m = max.text.toString().trim()

            if (t.isEmpty() || d.isEmpty() || dt.isEmpty() || tm.isEmpty() || v.isEmpty() || m.isEmpty()) {
                Toast.makeText(this, "Fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val inserted = db.insertEvent(t, c, d, dt, tm, v, m)

            if (inserted) {
                Toast.makeText(this, "Event submitted for approval", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Error saving event", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK) {
            if (requestCode == pickImage) {
                imgPreview.setImageURI(data?.data)
            }

            if (requestCode == cameraCode) {
                val bitmap = data?.extras?.get("data") as Bitmap
                imgPreview.setImageBitmap(bitmap)
            }
        }
    }
}