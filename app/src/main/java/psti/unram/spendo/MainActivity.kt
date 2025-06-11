package psti.unram.spendo

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        val tvawal = findViewById<TextView>(R.id.textMain)
        val btnstart = findViewById<Button>(R.id.btnstart)
        val btnlog = findViewById<Button>(R.id.btnlogin)

        btnstart.setOnClickListener {
            val intent = Intent(this, Loading1::class.java)
            startActivity(intent)
        }
        btnlog.setOnClickListener {
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
        }
        }
    }
