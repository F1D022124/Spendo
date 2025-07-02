package psti.unram.spendo

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton

class MainActivity : AppCompatActivity() {
    private val USER_PREFS = "UserPrefs"
    private val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Periksa status login dari SharedPreferences
        val userPrefs = getSharedPreferences(USER_PREFS, MODE_PRIVATE)
        val userName = userPrefs.getString("user_name", null)?.takeIf { it.isNotBlank() }

        if (userName != null) {
            // Pengguna sudah login, arahkan langsung ke Beranda
            Log.d(TAG, "User already logged in: $userName, redirecting to Beranda")
            val intent = Intent(this, Beranda::class.java)
            startActivity(intent)
            finish()
            return
        }

        // Pengguna belum login atau sudah logout, tampilkan MainActivity
        Log.d(TAG, "No logged-in user found, showing MainActivity")
        setContentView(R.layout.activity_main)

        // Initialize views
        val btnStart = findViewById<MaterialButton>(R.id.btnStart)
        if (btnStart == null) {
            Log.e(TAG, "btnStart is null, check activity_main.xml for R.id.btnStart")
            return
        }

        // Start button click listener
        btnStart.setOnClickListener {
            Log.d(TAG, "btnStart clicked, navigating to Loading1")
            val intent = Intent(this, Loading1::class.java)
            startActivity(intent)
            finish()
        }
    }
}