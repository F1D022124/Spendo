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

class Loading2 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_loading2)

        val layload2 = findViewById<LinearLayout>(R.id.Llload2)
        val load2 = findViewById<TextView>(R.id.tvload2)
        val footload2 = findViewById<LinearLayout>(R.id.footerLayout)
        val imload2 = findViewById<ImageView>(R.id.ivload2)
        val btnload2 = findViewById<Button>(R.id.btnNext)

        btnload2.setOnClickListener {
            val intent = Intent(this, Beranda::class.java)
            startActivity(intent)
        }

    }
}