package com.example.event_management_app

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class StudentRegisterActivity : AppCompatActivity() {

    lateinit var db: DBHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_student_register)

        db = DBHelper(this)

        val btn = findViewById<Button>(R.id.btnRegister)

        val name = findViewById<EditText>(R.id.name)
        val email = findViewById<EditText>(R.id.email)
        val phone = findViewById<EditText>(R.id.phone)
        val password = findViewById<EditText>(R.id.password)
        val confirm = findViewById<EditText>(R.id.confirmPassword)
        val course = findViewById<Spinner>(R.id.spinnerCourse)
        val year = findViewById<Spinner>(R.id.spinnerYear)

        // Spinner data
        val courses = arrayOf("Select Course", "IT", "CS")
        val years = arrayOf("Select Year", "FY", "SY")

        course.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, courses)
        year.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, years)

        btn.setOnClickListener {

            val n = name.text.toString()
            val e = email.text.toString()
            val p = phone.text.toString()
            val pass = password.text.toString()
            val conf = confirm.text.toString()
            val c = course.selectedItem.toString()
            val y = year.selectedItem.toString()

            if (n.isEmpty() || e.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (c == "Select Course" || y == "Select Year") {
                Toast.makeText(this, "Select course & year", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (pass != conf) {
                Toast.makeText(this, "Password mismatch", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val result = db.insertStudent(n, e, p, c, y, pass)

            if (result) {
                Toast.makeText(this, "Success ✅", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Failed ❌ (Email may already exist)", Toast.LENGTH_SHORT).show()
            }
        }
    }
}