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

class Rekomendasi : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_rekomendasi)
        val headlay = findViewById<LinearLayout>(R.id.headerLayout5)
        val navigation = findViewById<ImageView>(R.id.ivback)
        val recomresult = findViewById<TextView>(R.id.tvtithitung)
        val container = findViewById<LinearLayout>(R.id.formLayout6)
        val ivshop = findViewById<ImageView>(R.id.ivbeli)
        val shop = findViewById<TextView>(R.id.tvbeli)
        val tryagain = findViewById<Button>(R.id.btncblg)
        val backmenus =findViewById<Button>(R.id.btnbkmenu)
        val footer = findViewById<LinearLayout>(R.id.footerLayout)
        val menu1 = findViewById<ImageView>(R.id.ivhome)
        val menu2 = findViewById<ImageView>(R.id.ivinput)
        val menu3 = findViewById<ImageView>(R.id.ivclock)
        val menu4 = findViewById<ImageView>(R.id.ivlight)
        val menu5 = findViewById<ImageView>(R.id.ivuser)


        menu1.setOnClickListener {
            val intent = Intent(this, Beranda::class.java)
            startActivity(intent)
        }
        menu2.setOnClickListener {
            val intent = Intent(this, Form::class.java)
            startActivity(intent)
        }
        menu3.setOnClickListener {
            val intent = Intent(this, Keuangan::class.java)
            startActivity(intent)
        }
        menu4.setOnClickListener {
            val intent = Intent(this, Riwayat::class.java)
            startActivity(intent)
        }
        menu5.setOnClickListener {
            val intent = Intent(this, Profil::class.java)
            startActivity(intent)
        }
        tryagain.setOnClickListener {
            val intent = Intent(this, Form::class.java)
            startActivity(intent)
        }
        backmenus.setOnClickListener {
            val intent = Intent(this, Beranda::class.java)
            startActivity(intent)
        }
    }
}