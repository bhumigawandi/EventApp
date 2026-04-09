package com.example.event_management_app

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class CreateEventActivity : AppCompatActivity() {

    lateinit var db: DBHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_event)

        db = DBHelper(this)

        val title = findViewById<EditText>(R.id.etTitle)
        val category = findViewById<Spinner>(R.id.spinnerCategory)
        val desc = findViewById<EditText>(R.id.etDescription)
        val date = findViewById<EditText>(R.id.etDate)
        val time = findViewById<EditText>(R.id.etTime)
        val venue = findViewById<EditText>(R.id.etVenue)
        val max = findViewById<EditText>(R.id.etMax)
        val btn = findViewById<Button>(R.id.btnSubmit)

        // 🔹 Spinner Data
        val categories = arrayOf("Select Category", "Technical", "Cultural", "Sports")

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            categories
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        category.adapter = adapter

        // 🔹 Submit
        btn.setOnClickListener {

            val t = title.text.toString().trim()
            val c = category.selectedItem.toString()
            val d = desc.text.toString().trim()
            val dt = date.text.toString().trim()
            val tm = time.text.toString().trim()
            val v = venue.text.toString().trim()
            val m = max.text.toString().trim()

            if (t.isEmpty() || d.isEmpty() || dt.isEmpty() || tm.isEmpty() || v.isEmpty() || m.isEmpty()) {
                Toast.makeText(this, "Fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (c == "Select Category") {
                Toast.makeText(this, "Select category", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val result = db.insertEvent(t, c, d, dt, tm, v, m)

            if (result) {
                Toast.makeText(this, "Event Sent for Approval", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show()
            }
        }
    }
}