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

class Loading1 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_loading1)

        val layload1 = findViewById<LinearLayout>(R.id.Llload1)
        val load1 = findViewById<TextView>(R.id.tvload1)
        val footload = findViewById<LinearLayout>(R.id.footerLayout)
        val imload = findViewById<ImageView>(R.id.ivload1)
        val btnload = findViewById<Button>(R.id.btnNext)

        btnload.setOnClickListener {
            val intent = Intent(this, Loading2::class.java)
            startActivity(intent)
        }
    }
}