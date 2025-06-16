package psti.unram.spendo

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Riwayat : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_riwayat)

        val back = findViewById<ImageView>(R.id.ivBack)
        val tithistory = findViewById<TextView>(R.id.tvTitle)
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewHistory)
        val menu1 = findViewById<ImageView>(R.id.ivhome)
        val menu2 = findViewById<ImageView>(R.id.ivinput)
        val menu3 = findViewById<ImageView>(R.id.ivclock)
        val menu4 = findViewById<ImageView>(R.id.ivlight)
        val menu5 = findViewById<ImageView>(R.id.ivuser)

        val sharedPref = getSharedPreferences("riwayat_pref", MODE_PRIVATE)
        val gson = Gson()
        val existingJson = sharedPref.getString("riwayat_list", "[]")
        val type = object : TypeToken<MutableList<HistoryItem>>() {}.type
        val dataRiwayat: List<HistoryItem> = gson.fromJson(existingJson, type) ?: emptyList()

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = HistoryAdapter(dataRiwayat)

        menu1.setOnClickListener { startActivity(Intent(this, Beranda::class.java)) }
        menu2.setOnClickListener { startActivity(Intent(this, Form::class.java)) }
        menu3.setOnClickListener { startActivity(Intent(this, Keuangan::class.java)) }
//        menu4.setOnClickListener { /* Jangan buka Riwayat lagi */ }
        menu5.setOnClickListener { startActivity(Intent(this, Profil::class.java)) }

        back.setOnClickListener { onBackPressed() }
    }
}