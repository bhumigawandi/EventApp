package com.example.event_management_app

import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class StudentDashboard : AppCompatActivity() {

    lateinit var db: DBHelper
    lateinit var container: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_student_dashboard)

        db = DBHelper(this)

        val logoutBtn = findViewById<TextView>(R.id.logoutBtn)
        container = findViewById(R.id.eventContainer)

        val email = intent.getStringExtra("email")
        Toast.makeText(this, "Welcome $email", Toast.LENGTH_SHORT).show()

        loadEvents()

        logoutBtn.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun loadEvents() {

        container.removeAllViews()

        val cursor = db.getApprovedEvents()

        if (cursor.moveToFirst()) {
            do {
                val title = cursor.getString(cursor.getColumnIndexOrThrow("title"))
                val date = cursor.getString(cursor.getColumnIndexOrThrow("date"))
                val venue = cursor.getString(cursor.getColumnIndexOrThrow("venue"))

                val card = LinearLayout(this)
                card.orientation = LinearLayout.VERTICAL
                card.setPadding(20, 20, 20, 20)
                card.setBackgroundResource(R.drawable.card_bg)

                val params = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                params.setMargins(0, 0, 0, 20)
                card.layoutParams = params

                val tvTitle = TextView(this)
                tvTitle.text = title
                tvTitle.textSize = 16f

                val tvInfo = TextView(this)
                tvInfo.text = "$date • $venue"

                // Click → Detail page
                card.setOnClickListener {
                    val intent = Intent(this, EventDetailActivity::class.java)
                    intent.putExtra("title", title)
                    intent.putExtra("date", date)
                    intent.putExtra("venue", venue)
                    startActivity(intent)
                }

                card.addView(tvTitle)
                card.addView(tvInfo)

                container.addView(card)

            } while (cursor.moveToNext())
        } else {
            val empty = TextView(this)
            empty.text = "No Events Available"
            container.addView(empty)
        }

        cursor.close()
    }
}