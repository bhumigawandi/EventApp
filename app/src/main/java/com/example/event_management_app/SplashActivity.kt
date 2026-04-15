package com.example.event_management_app.com.example.event_management_app



import android.content.Intent
import android.os.*
import androidx.appcompat.app.AppCompatActivity
import com.example.event_management_app.HomepageActivity
import com.example.event_management_app.LoginActivity
import com.example.event_management_app.MainActivity
import com.example.event_management_app.R


class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        setContentView(R.layout.activity_splash) // ✅ IMPORTANT

        Handler(Looper.getMainLooper()).postDelayed({
            val session = SessionManager(this)

            if (session.isLoggedIn()) {
                startActivity(Intent(this, MainActivity::class.java))
            } else {
                startActivity(Intent(this, HomepageActivity::class.java))
            }

            finish()
        }, 2000)
    }
}