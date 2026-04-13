package com.example.event_management_app

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class StudentListActivity : AppCompatActivity() {

    lateinit var db: DBHelper
    lateinit var listView: ListView
    lateinit var emptyText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_student_list)

        listView = findViewById(R.id.studentListView)
        emptyText = findViewById(R.id.emptyText)
        db = DBHelper(this)

        val eventId = intent.getIntExtra("eventId", -1)

        // DEBUG
        Toast.makeText(this, "Event ID: $eventId", Toast.LENGTH_SHORT).show()

        val cursor = db.getRegisteredStudents(eventId)

        val list = ArrayList<String>()

        if (cursor != null && cursor.moveToFirst()) {
            do {
                val email = cursor.getString(0)
                list.add("👤 $email")
            } while (cursor.moveToNext())
        }

        cursor.close()

        if (list.isEmpty()) {
            emptyText.text = "No students registered"
            emptyText.visibility = TextView.VISIBLE
        } else {
            emptyText.visibility = TextView.GONE
        }

        listView.adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, list)
    }
}