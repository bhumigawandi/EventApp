package com.example.event_management_app

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBHelper(context: Context) :
    SQLiteOpenHelper(context, "EventDB", null, 4) {

    override fun onCreate(db: SQLiteDatabase) {

        // 🔹 STUDENTS
        db.execSQL(
            "CREATE TABLE students(" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "name TEXT," +
                    "email TEXT," +
                    "phone TEXT," +
                    "course TEXT," +
                    "year TEXT," +
                    "password TEXT)"
        )

        // 🔹 ORGANIZERS
        db.execSQL(
            "CREATE TABLE organizers(" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "name TEXT," +
                    "email TEXT," +
                    "password TEXT)"
        )

        // 🔹 EVENTS
        db.execSQL(
            "CREATE TABLE events(" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "title TEXT," +
                    "category TEXT," +
                    "description TEXT," +
                    "date TEXT," +
                    "time TEXT," +
                    "venue TEXT," +
                    "maxParticipants TEXT," +
                    "status TEXT)"
        )

        // 🔹 ADMIN (ONLY ONE)
        db.execSQL(
            "CREATE TABLE admin(" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "email TEXT UNIQUE," +
                    "password TEXT)"
        )

        // 🔥 INSERT DEFAULT ADMIN
        val cv = ContentValues()
        cv.put("email", "admin@gmail.com")
        cv.put("password", "admin123")
        db.insert("admin", null, cv)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS students")
        db.execSQL("DROP TABLE IF EXISTS organizers")
        db.execSQL("DROP TABLE IF EXISTS events")
        db.execSQL("DROP TABLE IF EXISTS admin")
        onCreate(db)
    }

    // =========================
    // INSERT STUDENT
    // =========================
    fun insertStudent(
        name: String,
        email: String,
        phone: String,
        course: String,
        year: String,
        password: String
    ): Boolean {
        val db = writableDatabase
        val cv = ContentValues()
        cv.put("name", name)
        cv.put("email", email)
        cv.put("phone", phone)
        cv.put("course", course)
        cv.put("year", year)
        cv.put("password", password)

        return db.insert("students", null, cv) != -1L
    }

    // =========================
    // INSERT ORGANIZER
    // =========================
    fun insertOrganizer(name: String, email: String, password: String): Boolean {
        val db = writableDatabase
        val cv = ContentValues()
        cv.put("name", name)
        cv.put("email", email)
        cv.put("password", password)

        return db.insert("organizers", null, cv) != -1L
    }

    // =========================
    // LOGIN (Student / Organizer)
    // =========================
    fun checkUser(email: String, password: String, role: String): Boolean {
        val db = readableDatabase
        val table = if (role == "Student") "students" else "organizers"

        val cursor = db.rawQuery(
            "SELECT * FROM $table WHERE email=? AND password=?",
            arrayOf(email, password)
        )

        val exists = cursor.moveToFirst()
        cursor.close()
        return exists
    }

    // =========================
    // ADMIN LOGIN
    // =========================
    fun checkAdmin(email: String, password: String): Boolean {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM admin WHERE email=? AND password=?",
            arrayOf(email, password)
        )

        val exists = cursor.moveToFirst()
        cursor.close()
        return exists
    }

    // =========================
    // INSERT EVENT
    // =========================
    fun insertEvent(
        title: String,
        category: String,
        description: String,
        date: String,
        time: String,
        venue: String,
        max: String
    ): Boolean {
        val db = writableDatabase
        val cv = ContentValues()

        cv.put("title", title)
        cv.put("category", category)
        cv.put("description", description)
        cv.put("date", date)
        cv.put("time", time)
        cv.put("venue", venue)
        cv.put("maxParticipants", max)
        cv.put("status", "Pending")

        return db.insert("events", null, cv) != -1L
    }

    // =========================
    // ADMIN: GET PENDING EVENTS
    // =========================
    fun getPendingEvents(): Cursor {
        val db = readableDatabase
        return db.rawQuery("SELECT * FROM events WHERE status='Pending'", null)
    }

    // =========================
    // UPDATE STATUS
    // =========================
    fun updateEventStatus(id: Int, status: String) {
        val db = writableDatabase
        val cv = ContentValues()
        cv.put("status", status)

        db.update("events", cv, "id=?", arrayOf(id.toString()))
    }
    // =========================
// STUDENT: GET APPROVED EVENTS
// =========================
    fun getApprovedEvents(): Cursor {
        val db = readableDatabase
        return db.rawQuery("SELECT * FROM events WHERE status='Approved'", null)
    }
}