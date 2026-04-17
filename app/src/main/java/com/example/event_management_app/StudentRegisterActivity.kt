package com.example.event_management_app

import android.os.Bundle
import android.util.Patterns
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

        val courses = arrayOf("Select Course", "IT", "CS")
        val years = arrayOf("Select Year", "FY", "SY","TY")

        course.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, courses)
        year.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, years)

        btn.setOnClickListener {

            val n = name.text.toString().trim()
            val e = email.text.toString().trim()
            val p = phone.text.toString().trim()
            val pass = password.text.toString().trim()
            val conf = confirm.text.toString().trim()
            val c = course.selectedItem.toString()
            val y = year.selectedItem.toString()

            // ✅ NAME
            if (n.isEmpty()) {
                name.error = "Enter name"
                name.requestFocus()
                return@setOnClickListener
            }

            // ✅ EMAIL
            if (e.isEmpty()) {
                email.error = "Enter email"
                email.requestFocus()
                return@setOnClickListener
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(e).matches()) {
                email.error = "Invalid email"
                email.requestFocus()
                return@setOnClickListener
            }

            // ✅ PHONE
            if (p.isEmpty()) {
                phone.error = "Enter phone"
                phone.requestFocus()
                return@setOnClickListener
            }

            if (p.length != 10) {
                phone.error = "Enter valid 10-digit phone"
                phone.requestFocus()
                return@setOnClickListener
            }

            // ✅ COURSE & YEAR
            if (c == "Select Course") {
                Toast.makeText(this, "Select course", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (y == "Select Year") {
                Toast.makeText(this, "Select year", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // ✅ PASSWORD
            if (pass.isEmpty()) {
                password.error = "Enter password"
                password.requestFocus()
                return@setOnClickListener
            }

            if (pass.length < 6) {
                password.error = "Minimum 6 characters"
                password.requestFocus()
                return@setOnClickListener
            }

            // ✅ CONFIRM PASSWORD
            if (conf.isEmpty()) {
                confirm.error = "Confirm password"
                confirm.requestFocus()
                return@setOnClickListener
            }

            if (pass != conf) {
                confirm.error = "Password mismatch"
                confirm.requestFocus()
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