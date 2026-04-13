package com.example.event_management_app

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBHelper(context: Context) :
    SQLiteOpenHelper(context, "EventDB", null, 1) {

    override fun onCreate(db: SQLiteDatabase) {

        // STUDENTS
        db.execSQL(
            "CREATE TABLE students(" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "name TEXT," +
                    "email TEXT UNIQUE," +
                    "phone TEXT," +
                    "course TEXT," +
                    "year TEXT," +
                    "password TEXT)"
        )

        // ORGANIZERS
        db.execSQL(
            "CREATE TABLE organizers(" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "name TEXT," +
                    "email TEXT UNIQUE," +
                    "password TEXT)"
        )

        // EVENTS (✅ image added)
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
                    "image TEXT," +
                    "status TEXT)"
        )

        // ADMIN
        db.execSQL(
            "CREATE TABLE admin(" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "email TEXT UNIQUE," +
                    "password TEXT)"
        )

        // REGISTRATION (✅ only once)
        db.execSQL(
            "CREATE TABLE registrations(" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "eventTitle TEXT," +
                    "studentEmail TEXT)"
        )

        // DEFAULT ADMIN
        val admin = ContentValues()
        admin.put("email", "admin@gmail.com")
        admin.put("password", "admin123")
        db.insert("admin", null, admin)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS students")
        db.execSQL("DROP TABLE IF EXISTS organizers")
        db.execSQL("DROP TABLE IF EXISTS events")
        db.execSQL("DROP TABLE IF EXISTS registrations")
        db.execSQL("DROP TABLE IF EXISTS admin")
        onCreate(db)
    }

    // ================= LOGIN =================

    fun checkAdmin(email: String, password: String): Boolean {
        val cursor = readableDatabase.rawQuery(
            "SELECT * FROM admin WHERE email=? AND password=?",
            arrayOf(email, password)
        )
        val exists = cursor.moveToFirst()
        cursor.close()
        return exists
    }

    fun checkUser(email: String, password: String, role: String): Boolean {
        val table = if (role == "Student") "students" else "organizers"

        val cursor = readableDatabase.rawQuery(
            "SELECT * FROM $table WHERE email=? AND password=?",
            arrayOf(email, password)
        )

        val exists = cursor.moveToFirst()
        cursor.close()
        return exists
    }

    // ================= INSERT STUDENT =================

    fun insertStudent(
        name: String,
        email: String,
        phone: String,
        course: String,
        year: String,
        password: String
    ): Boolean {

        val cv = ContentValues()
        cv.put("name", name)
        cv.put("email", email)
        cv.put("phone", phone)
        cv.put("course", course)
        cv.put("year", year)
        cv.put("password", password)

        return writableDatabase.insert("students", null, cv) != -1L
    }

    // ================= INSERT ORGANIZER =================

    fun insertOrganizer(name: String, email: String, password: String): Boolean {

        val cv = ContentValues()
        cv.put("name", name)
        cv.put("email", email)
        cv.put("password", password)

        return writableDatabase.insert("organizers", null, cv) != -1L
    }

    // ================= INSERT EVENT (WITH IMAGE) =================

    fun insertEvent(
        title: String,
        category: String,
        description: String,
        date: String,
        time: String,
        venue: String,
        max: String,
        image: String
    ): Boolean {

        val cv = ContentValues()
        cv.put("title", title)
        cv.put("category", category)
        cv.put("description", description)
        cv.put("date", date)
        cv.put("time", time)
        cv.put("venue", venue)
        cv.put("maxParticipants", max)
        cv.put("image", image)
        cv.put("status", "Pending")

        return writableDatabase.insert("events", null, cv) != -1L
    }

    // ================= EVENTS =================

    fun getAllEvents(): Cursor {
        return readableDatabase.rawQuery("SELECT * FROM events", null)
    }

    fun getPendingEvents(): Cursor {
        return readableDatabase.rawQuery("SELECT * FROM events WHERE status='Pending'", null)
    }

    fun getApprovedEvents(): Cursor {
        return readableDatabase.rawQuery("SELECT * FROM events WHERE status='Approved'", null)
    }

    fun updateEventStatus(id: Int, status: String) {
        val cv = ContentValues()
        cv.put("status", status)
        writableDatabase.update("events", cv, "id=?", arrayOf(id.toString()))
    }

    // ================= REGISTER EVENT =================

    fun registerEvent(eventId: Int, email: String): Boolean {

        val db = writableDatabase

        // 🔹 GET TITLE FROM ID (ADD THIS)
        val cursorEvent = db.rawQuery(
            "SELECT title FROM events WHERE id=?",
            arrayOf(eventId.toString())
        )

        if (!cursorEvent.moveToFirst()) {
            cursorEvent.close()
            return false
        }

        val title = cursorEvent.getString(0)
        cursorEvent.close()

        // 🔹 CHECK EXISTING
        val cursor = db.rawQuery(
            "SELECT * FROM registrations WHERE eventTitle=? AND studentEmail=?",
            arrayOf(title, email)
        )

        if (cursor.count > 0) {
            cursor.close()
            return false
        }

        cursor.close()

        // 🔹 INSERT (USING OLD TABLE STRUCTURE)
        val values = ContentValues()
        values.put("eventTitle", title)
        values.put("studentEmail", email)

        db.insert("registrations", null, values)
        return true
    }
    // ================= COUNT =================



    // ================= MY EVENTS =================



    // 🔹 GET REGISTERED EVENTS FOR USER
    fun getMyEvents(email: String): Cursor {
        return readableDatabase.rawQuery("""
        SELECT events.* FROM events 
        INNER JOIN registrations 
        ON events.title = registrations.eventTitle
        WHERE registrations.userEmail=?
    """, arrayOf(email))
    }

    // 🔹 COUNT STUDENTS PER EVENT
    fun getEventCount(eventId: Int): Int {

        val cursorEvent = readableDatabase.rawQuery(
            "SELECT title FROM events WHERE id=?",
            arrayOf(eventId.toString())
        )

        if (!cursorEvent.moveToFirst()) {
            cursorEvent.close()
            return 0
        }

        val title = cursorEvent.getString(0)
        cursorEvent.close()

        val cursor = readableDatabase.rawQuery(
            "SELECT * FROM registrations WHERE eventTitle=?",
            arrayOf(title)
        )

        val count = cursor.count
        cursor.close()
        return count
    }

    // 🔹 GET STUDENTS FOR EVENT (BY ID)
    fun getRegisteredStudents(eventId: Int): Cursor {

        val cursorEvent = readableDatabase.rawQuery(
            "SELECT title FROM events WHERE id=?",
            arrayOf(eventId.toString())
        )

        if (!cursorEvent.moveToFirst()) {
            return readableDatabase.rawQuery("SELECT ''", null)
        }

        val title = cursorEvent.getString(0)
        cursorEvent.close()

        return readableDatabase.rawQuery(
            "SELECT studentEmail FROM registrations WHERE eventTitle=?",
            arrayOf(title)
        )
    }
    }
