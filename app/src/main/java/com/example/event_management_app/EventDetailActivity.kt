package com.example.event_management_app

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class EventDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event_detail)

        val title = findViewById<TextView>(R.id.tvTitle)
        val date = findViewById<TextView>(R.id.tvDate)
        val location = findViewById<TextView>(R.id.tvLocation)
        val description = findViewById<TextView>(R.id.tvDescription)
        val registerBtn = findViewById<Button>(R.id.btnRegister)

        // Dummy data (you can replace later with intent data)
        title.text = "Tech Fest 2026"
        date.text = "Date: 20 April 2026"
        location.text = "Location: Auditorium"
        description.text = "This is a technical event with coding competitions and workshops."

        registerBtn.setOnClickListener {
            Toast.makeText(this, "Registered Successfully", Toast.LENGTH_SHORT).show()
        }
    }
}