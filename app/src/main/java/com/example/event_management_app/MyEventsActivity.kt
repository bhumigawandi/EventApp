package com.example.event_management_app

import android.os.Bundle
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class MyEventsActivity : AppCompatActivity() {

    lateinit var db: DBHelper
    lateinit var container: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_events)

        db = DBHelper(this)
        container = findViewById(R.id.container)

        val email = intent.getStringExtra("email") ?: ""

        loadMyEvents(email)
    }

    private fun loadMyEvents(email: String) {

        container.removeAllViews()

        val cursor = db.getMyEvents(email)

        if (cursor.moveToFirst()) {
            do {
                val title = cursor.getString(cursor.getColumnIndexOrThrow("title"))
                val date = cursor.getString(cursor.getColumnIndexOrThrow("date"))
                val venue = cursor.getString(cursor.getColumnIndexOrThrow("venue"))

                val card = LinearLayout(this)
                card.orientation = LinearLayout.VERTICAL
                card.setPadding(20, 20, 20, 20)
                card.setBackgroundColor(0xFFFFFFFF.toInt())

                val tvTitle = TextView(this)
                tvTitle.text = title
                tvTitle.textSize = 18f

                val tvInfo = TextView(this)
                tvInfo.text = "$date • $venue"

                card.addView(tvTitle)
                card.addView(tvInfo)

                container.addView(card)

            } while (cursor.moveToNext())
        } else {
            val tv = TextView(this)
            tv.text = "No Registered Events"
            container.addView(tv)
        }

        cursor.close()
    }
}