package psti.unram.spendo

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // Initialize views
        val btnStart = findViewById<MaterialButton>(R.id.btnStart)

        // Start button click listener
        btnStart.setOnClickListener {
            val intent = Intent(this, Loading1::class.java)
            startActivity(intent)
            finish()
        }
    }
}