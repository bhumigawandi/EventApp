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
        val extra = findViewById<TextView>(R.id.tvExtra)
        val registerBtn = findViewById<Button>(R.id.btnRegister)

        // 🔥 Get data from intent
        val eventTitle = intent.getStringExtra("title")
        val eventDate = intent.getStringExtra("date")
        val eventVenue = intent.getStringExtra("venue")
        val eventDesc = intent.getStringExtra("description")
        val seats = intent.getStringExtra("seats")

        title.text = eventTitle ?: "No Title"
        date.text = eventDate ?: "No Date"
        location.text = "📍 ${eventVenue ?: "No Location"}"
        description.text = eventDesc ?: "No Description"
        extra.text = "${seats ?: "0"} seats available"

        registerBtn.setOnClickListener {
            Toast.makeText(this, "Registered Successfully", Toast.LENGTH_SHORT).show()
        }
    }
}