package com.example.event_management_app

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class EventDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event_detail)

        val title = intent.getStringExtra("title")
        val date = intent.getStringExtra("date")
        val venue = intent.getStringExtra("venue")
        val desc = intent.getStringExtra("description")

        val tvTitle = findViewById<TextView>(R.id.tvTitle)
        val tvDate = findViewById<TextView>(R.id.tvDate)
        val tvVenue = findViewById<TextView>(R.id.tvVenue)
        val tvDesc = findViewById<TextView>(R.id.tvDescription)

        tvTitle.text = title
        tvDate.text = date
        tvVenue.text = venue
        tvDesc.text = desc
    }
}