package psti.unram.spendo

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.button.MaterialButton

class Loading1 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_loading1)

        // Initialize views
        val btnNext = findViewById<MaterialButton>(R.id.btnNext)

        // Next button click listener
        btnNext.setOnClickListener {
            val intent = Intent(this, Loading2::class.java)
            startActivity(intent)
            finish()
        }
    }
}