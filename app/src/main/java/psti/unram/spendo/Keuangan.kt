package psti.unram.spendo

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.findViewTreeViewModelStoreOwner

class Keuangan : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_keuangan)

        val headlay = findViewById<LinearLayout>(R.id.headerLayout)
        val back = findViewById<ImageView>(R.id.ivBack)
        val titkeuangan = findViewById<TextView>(R.id.tvTitle)
        val tips = findViewById<LinearLayout>(R.id.tipsSection)
        val laykeuangan = findViewById<LinearLayout>(R.id.keuangan)
        val ivkeuangan = findViewById<ImageView>(R.id.ividea)
        val title = findViewById<TextView>(R.id.tvkeuangan)
        val penjelasan = findViewById<TextView>(R.id.tvRecommendation)
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

    }
}