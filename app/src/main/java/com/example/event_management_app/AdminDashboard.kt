package com.example.event_management_app

import android.graphics.Color
import android.os.Bundle
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class AdminDashboard : AppCompatActivity() {

    private lateinit var db: DBHelper
    private lateinit var container: LinearLayout

    private lateinit var totalBox: TextView
    private lateinit var pendingBox: TextView
    private lateinit var approvedBox: TextView
    private lateinit var pendingCount: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_dashboard)

        db = DBHelper(this)

        // 🔹 CONNECT UI (same as your XML)
        container = findViewById(R.id.pendingContainer)
        totalBox = findViewById(R.id.totalCount)
        pendingBox = findViewById(R.id.pendingBox)
        approvedBox = findViewById(R.id.approvedCount)
        pendingCount = findViewById(R.id.pendingCount)

        // 🔹 CLICK FILTER
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
                val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
                val title = cursor.getString(cursor.getColumnIndexOrThrow("title"))
                val category = cursor.getString(cursor.getColumnIndexOrThrow("category"))
                val date = cursor.getString(cursor.getColumnIndexOrThrow("date"))

                // 🔹 CARD
                val card = LinearLayout(this)
                card.orientation = LinearLayout.VERTICAL
                card.setPadding(25, 25, 25, 25)
                card.setBackgroundColor(Color.WHITE)

                val params = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                params.setMargins(0, 0, 0, 20)
                card.layoutParams = params

                // 🔹 TITLE
                val tvTitle = TextView(this)
                tvTitle.text = title
                tvTitle.textSize = 16f
                tvTitle.setTextColor(Color.BLACK)

                // 🔹 INFO
                val tvInfo = TextView(this)
                tvInfo.text = "$category • $date"
                tvInfo.setTextColor(Color.GRAY)

                card.addView(tvTitle)
                card.addView(tvInfo)

                // 🔥 ONLY FOR PENDING → SHOW BUTTONS
                if (type == "PENDING") {

                    val btnLayout = LinearLayout(this)
                    btnLayout.orientation = LinearLayout.HORIZONTAL

                    val approve = Button(this)
                    approve.text = "Approve"

                    val reject = Button(this)
                    reject.text = "Reject"

                    val btnParams = LinearLayout.LayoutParams(
                        0,
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        1f
                    )
                    btnParams.setMargins(10, 10, 10, 0)

                    approve.layoutParams = btnParams
                    reject.layoutParams = btnParams

                    // ✅ APPROVE
                    approve.setOnClickListener {
                        db.updateEventStatus(id, "Approved")
                        Toast.makeText(this, "Approved", Toast.LENGTH_SHORT).show()
                        loadEvents("PENDING")
                        updateCounts()
                    }

                    // ❌ REJECT
                    reject.setOnClickListener {
                        db.updateEventStatus(id, "Rejected")
                        Toast.makeText(this, "Rejected", Toast.LENGTH_SHORT).show()
                        loadEvents("PENDING")
                        updateCounts()
                    }

                    btnLayout.addView(approve)
                    btnLayout.addView(reject)
                    card.addView(btnLayout)
                }

                container.addView(card)

            } while (cursor.moveToNext())
        } else {
            val empty = TextView(this)
            empty.text = "No Events Found"
            empty.setTextColor(Color.GRAY)
            container.addView(empty)
        }

        cursor.close()
    }

    private fun updateCounts() {

        val total = db.getAllEvents().count
        val pending = db.getPendingEvents().count
        val approved = db.getApprovedEvents().count

        totalBox.text = "$total"
        pendingBox.text = "$pending"
        approvedBox.text = "$approved"

        pendingCount.text = "$pending events awaiting approval"
    }
}