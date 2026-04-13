package com.example.event_management_app

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar

class AdminDashboard : AppCompatActivity() {

    private lateinit var db: DBHelper
    private lateinit var container: LinearLayout

    private lateinit var totalBox: LinearLayout
    private lateinit var pendingBox: LinearLayout
    private lateinit var approvedBox: LinearLayout

    private lateinit var totalCount: TextView
    private lateinit var pendingCount: TextView
    private lateinit var approvedCount: TextView
    private lateinit var pendingText: TextView

    private var lastClickTime = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_dashboard)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        db = DBHelper(this)

        container = findViewById(R.id.pendingContainer)

        totalBox = findViewById(R.id.totalBox)
        pendingBox = findViewById(R.id.pendingBox)
        approvedBox = findViewById(R.id.approvedBox)

        totalCount = findViewById(R.id.totalCount)
        pendingCount = findViewById(R.id.pendingCount)
        approvedCount = findViewById(R.id.approvedCount)
        pendingText = findViewById(R.id.pendingText)

        totalBox.setOnClickListener { loadEvents("ALL") }
        pendingBox.setOnClickListener { loadEvents("PENDING") }
        approvedBox.setOnClickListener { loadEvents("APPROVED") }

        loadEvents("PENDING")
        updateCounts()
    }

    private fun loadEvents(type: String) {

        container.removeAllViews()

        val cursor = when (type) {
            "ALL" -> db.getAllEvents()
            "APPROVED" -> db.getApprovedEvents()
            else -> db.getPendingEvents()
        }

        if (cursor.moveToFirst()) {
            do {
                val title = cursor.getString(cursor.getColumnIndexOrThrow("title"))
                val category = cursor.getString(cursor.getColumnIndexOrThrow("category"))
                val date = cursor.getString(cursor.getColumnIndexOrThrow("date"))

                val card = LinearLayout(this)
                card.orientation = LinearLayout.VERTICAL
                card.setPadding(25, 25, 25, 25)
                card.setBackgroundColor(Color.WHITE)

                val tvTitle = TextView(this)
                tvTitle.text = title
                tvTitle.textSize = 16f

                val tvInfo = TextView(this)
                tvInfo.text = "$category • $date"

                card.addView(tvTitle)
                card.addView(tvInfo)

                container.addView(card)

            } while (cursor.moveToNext())
        }

        cursor.close()
    }

    private fun updateCounts() {
        val total = db.getAllEvents().count
        val pending = db.getPendingEvents().count
        val approved = db.getApprovedEvents().count

        totalCount.text = total.toString()
        pendingCount.text = pending.toString()
        approvedCount.text = approved.toString()
        pendingText.text = "$pending events awaiting approval"
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.admin_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {

            R.id.notification -> {
                Toast.makeText(this, "Notifications", Toast.LENGTH_SHORT).show()
                true
            }

            R.id.settings -> {
                Toast.makeText(this, "Settings", Toast.LENGTH_SHORT).show()
                true
            }

            R.id.logout -> {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }
}