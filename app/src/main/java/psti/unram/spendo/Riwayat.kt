package psti.unram.spendo

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class Riwayat : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_riwayat)

        // Inisialisasi view
        val back = findViewById<ImageView>(R.id.ivBack)
        val tithistory = findViewById<TextView>(R.id.tvTitle)
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewHistory)
        val menu1 = findViewById<ImageView>(R.id.ivhome)
        val menu2 = findViewById<ImageView>(R.id.ivinput)
        val menu3 = findViewById<ImageView>(R.id.ivclock)
        val menu4 = findViewById<ImageView>(R.id.ivlight)
        val menu5 = findViewById<ImageView>(R.id.ivuser)

        // Data dummy untuk testing, nanti bisa diganti dari database atau SharedPreferences
        val dataRiwayat = listOf(
            HistoryItem("10/06/2025", "Transport", "15000"),
            HistoryItem("09/06/2025", "Jajan", "5000"),
            HistoryItem("08/06/2025", "Belanja", "80000")
        )

        // Setup RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = HistoryAdapter(dataRiwayat)

        // Navigasi menu
        menu1.setOnClickListener {
            startActivity(Intent(this, Beranda::class.java))
        }
        menu2.setOnClickListener {
            startActivity(Intent(this, Form::class.java))
        }
        menu3.setOnClickListener {
            startActivity(Intent(this, Keuangan::class.java))
        }
        menu4.setOnClickListener {
            // Jangan buka Riwayat lagi
        }
        menu5.setOnClickListener {
            startActivity(Intent(this, Profil::class.java))
        }

        // Tombol back
        back.setOnClickListener {
            onBackPressed()
        }
    }
}
