package com.example.event_management_app

import android.os.Bundle
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class AllEventsActivity : AppCompatActivity() {

    lateinit var db: DBHelper
    lateinit var container: LinearLayout
    lateinit var email: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_events)

        db = DBHelper(this)
        container = findViewById(R.id.container)

        email = intent.getStringExtra("email")!!

        loadEvents()
    }

    private fun loadEvents() {

        val cursor = db.getAllEvents()

        if (cursor.moveToFirst()) {
            do {

                val eventId = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
                val title = cursor.getString(cursor.getColumnIndexOrThrow("title"))
                val date = cursor.getString(cursor.getColumnIndexOrThrow("date"))
                val venue = cursor.getString(cursor.getColumnIndexOrThrow("venue"))

                val card = LinearLayout(this)
                card.orientation = LinearLayout.VERTICAL
                card.setPadding(20, 20, 20, 20)

                val tv = TextView(this)
                tv.text = "$title\n$date | $venue"

                val count = db.getEventCount(eventId)
                val tvCount = TextView(this)
                tvCount.text = "Registered: $count"

                val btn = Button(this)
                btn.text = "Register"

                btn.setOnClickListener {
                    val result = db.registerEvent(eventId, email)

                    if (result) {
                        btn.text = "Registered"
                        btn.isEnabled = false
                    } else {
                        Toast.makeText(this, "Already Registered", Toast.LENGTH_SHORT).show()
                    }
                }

                card.addView(tv)
                card.addView(tvCount)
                card.addView(btn)

                container.addView(card)

            } while (cursor.moveToNext())
        }

        cursor.close()
    }
}