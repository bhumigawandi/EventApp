package com.example.event_management_app

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class StudentListActivity : AppCompatActivity() {

    lateinit var db: DBHelper
    lateinit var listView: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_student_list)

        db = DBHelper(this)
        listView = findViewById(R.id.listViewStudents)

        val eventId = intent.getIntExtra("eventId", -1)

        if (eventId == -1) {
            Toast.makeText(this, "Invalid Event", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        loadStudents(eventId)
    }

    private fun loadStudents(eventId: Int) {

        val cursor = db.getStudentsForEvent(eventId)

        val list = ArrayList<String>()

        if (cursor.moveToFirst()) {
            do {
                val name = cursor.getString(0)
                val email = cursor.getString(1)

                list.add("$name\n$email")

            } while (cursor.moveToNext())
        } else {
            list.add("No students registered")
        }

        cursor.close()

        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, list)
        listView.adapter = adapter
    }
}