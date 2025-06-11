package psti.unram.spendo

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class Profil : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_profil)

        val header = findViewById<LinearLayout>(R.id.headerprofil)
        val back = findViewById<ImageView>(R.id.btnBack)
        val profil = findViewById<TextView>(R.id.tvTitle)
        val scroll = findViewById<ScrollView>(R.id.scrollprofil)
        val layprofil = findViewById<LinearLayout>(R.id.lyprofil)
        val ivprofil = findViewById<ImageView>(R.id.ivProfile)
        val nama = findViewById<TextView>(R.id.tvNama)
        val email = findViewById<TextView>(R.id.tvEmail)
        val card = findViewById<CardView>(R.id.cardview)
        val laymenu = findViewById<LinearLayout>(R.id.lymenu)
        val ivakun = findViewById<ImageView>(R.id.ivakun)
        val akun = findViewById<TextView>(R.id.tvakun)
        val pembatas = findViewById<View>(R.id.pembatas)
        val ivnotif = findViewById<ImageView>(R.id.ivnotif)
        val notifikasi = findViewById<TextView>(R.id.tvnotif)
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
    }
}