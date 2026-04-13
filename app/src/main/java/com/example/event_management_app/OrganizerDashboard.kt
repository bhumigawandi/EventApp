package com.example.event_management_app

import android.database.Cursor
import android.os.Bundle
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class OrganizerDashboard : AppCompatActivity() {

    lateinit var container: LinearLayout
    lateinit var db: DBHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_organizer_dashboard)

        val name = intent.getStringExtra("organizer_name") ?: "Organizer"

        val tv = findViewById<TextView>(R.id.tvWelcome)
        val createEventBtn = findViewById<Button>(R.id.createEventBtn)
        val cameraBtn = findViewById<Button>(R.id.btnCamera)

        container = findViewById(R.id.mainContainer)

        tv.text = "Hello, $name 👋"

        db = DBHelper(this)

        createEventBtn.setOnClickListener {
            startActivity(android.content.Intent(this, CreateEventActivity::class.java))
        }

        cameraBtn.setOnClickListener {
            startActivity(android.content.Intent(this, CameraActivity::class.java))
        }

        loadEvents()
    }

    private fun loadEvents() {

        container.removeAllViews()

        val cursor: Cursor = db.getAllEvents()

        if (cursor.moveToFirst()) {
            do {
                val eventId = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
                val title = cursor.getString(cursor.getColumnIndexOrThrow("title"))
                val date = cursor.getString(cursor.getColumnIndexOrThrow("date"))
                val venue = cursor.getString(cursor.getColumnIndexOrThrow("venue"))

                // 🔹 Event Card
                val card = LinearLayout(this)
                card.orientation = LinearLayout.VERTICAL
                card.setPadding(20, 20, 20, 20)
                card.setBackgroundColor(0xFFEFEFEF.toInt())

                val params = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                params.setMargins(0, 0, 0, 20)
                card.layoutParams = params

                val tvTitle = TextView(this)
                tvTitle.text = title
                tvTitle.textSize = 18f

                val tvInfo = TextView(this)
                tvInfo.text = "$date | $venue"

                val btnView = Button(this)
                btnView.text = "View Students"

                val studentList = TextView(this)
                studentList.text = ""
                studentList.setPadding(0, 10, 0, 0)

                btnView.setOnClickListener {

                    val studentCursor = db.getStudentsForEvent(eventId)

                    val data = StringBuilder()

                    if (studentCursor.moveToFirst()) {
                        do {
                            val email = studentCursor.getString(0)
                            data.append("• $email\n")
                        } while (studentCursor.moveToNext())
                    } else {
                        data.append("No students registered")
                    }

                    studentCursor.close()

                    studentList.text = data.toString()
                }

                card.addView(tvTitle)
                card.addView(tvInfo)
                card.addView(btnView)
                card.addView(studentList)

                container.addView(card)

            } while (cursor.moveToNext())
        } else {
            val empty = TextView(this)
            empty.text = "No events"
            container.addView(empty)
        }

        cursor.close()
    }
}