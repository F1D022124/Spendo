package psti.unram.spendo

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class Riwayat : AppCompatActivity() {

    private val PREF_NAME = "riwayat_pref"
    private val RIWAYAT_KEY = "riwayat_list"
    private val scope = CoroutineScope(Dispatchers.Main)
    private val TAG = "Riwayat"
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: HistoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_riwayat)

        // Inisialisasi view
        recyclerView = findViewById(R.id.rvHistory)
        val tvNoData = findViewById<TextView>(R.id.tvNoData)
        val ivBack = findViewById<ImageView>(R.id.ivBack)
        val ivhome = findViewById<ImageView>(R.id.ivhome)
        val ivinput = findViewById<ImageView>(R.id.ivinput)
        val ivToRiwayat = findViewById<ImageView>(R.id.ivToRiwayat)
        val ivuser = findViewById<ImageView>(R.id.ivuser)

        // Logging untuk memeriksa null view
        if (ivBack == null) Log.e(TAG, "ivBack is null, check activity_riwayat.xml for R.id.ivBack")
        if (ivhome == null) Log.e(TAG, "ivhome is null, check activity_riwayat.xml for R.id.ivhome")
        if (ivinput == null) Log.e(TAG, "ivinput is null, check activity_riwayat.xml for R.id.ivinput")
        if (ivToRiwayat == null) Log.e(TAG, "ivToRiwayat is null, check activity_riwayat.xml for R.id.ivToRiwayat")
        if (ivuser == null) Log.e(TAG, "ivuser is null, check activity_riwayat.xml for R.id.ivuser")

        // Setup RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = HistoryAdapter(emptyList())
        recyclerView.adapter = adapter

        // Muat data riwayat secara asinkronus
        scope.launch {
            val historyList = loadHistory()
            if (historyList.isEmpty()) {
                tvNoData.visibility = TextView.VISIBLE
                recyclerView.visibility = RecyclerView.GONE
            } else {
                tvNoData.visibility = TextView.GONE
                recyclerView.visibility = RecyclerView.VISIBLE
                adapter.updateData(historyList.reversed()) // Urutkan dari terbaru
            }
        }

        // Navigasi
        ivBack?.setOnClickListener {
            ivBack.startAnimation(AnimationUtils.loadAnimation(this, R.anim.scale_bold))
            try {
                startActivity(Intent(this, Beranda::class.java))
                finish()
            } catch (e: Exception) {
                Log.e(TAG, "Navigation Error to Beranda: ${e.message}", e)
                Toast.makeText(this, "Gagal kembali ke Beranda", Toast.LENGTH_SHORT).show()
            }
        }

        ivhome?.setOnClickListener {
            ivhome.startAnimation(AnimationUtils.loadAnimation(this, R.anim.scale_bold))
            try {
                startActivity(Intent(this, Beranda::class.java))
                finish()
            } catch (e: Exception) {
                Log.e(TAG, "Navigation Error to Beranda: ${e.message}", e)
                Toast.makeText(this, "Gagal ke Beranda", Toast.LENGTH_SHORT).show()
            }
        }

        ivinput?.setOnClickListener {
            ivinput.startAnimation(AnimationUtils.loadAnimation(this, R.anim.scale_bold))
            try {
                startActivity(Intent(this, Form::class.java))
                finish()
            } catch (e: Exception) {
                Log.e(TAG, "Navigation Error to Form: ${e.message}", e)
                Toast.makeText(this, "Gagal ke Form", Toast.LENGTH_SHORT).show()
            }
        }

        ivToRiwayat?.setOnClickListener {
            ivToRiwayat.startAnimation(AnimationUtils.loadAnimation(this, R.anim.scale_bold))
            // Sudah di halaman Riwayat, hanya jalankan animasi
            Toast.makeText(this, "Anda sudah di halaman Riwayat", Toast.LENGTH_SHORT).show()
        }

        ivuser?.setOnClickListener {
            ivuser.startAnimation(AnimationUtils.loadAnimation(this, R.anim.scale_bold))
            try {
                startActivity(Intent(this, Profil::class.java))
                finish()
            } catch (e: Exception) {
                Log.e(TAG, "Navigation Error to Profil: ${e.message}", e)
                Toast.makeText(this, "Gagal ke Profil", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private suspend fun loadHistory(): List<HistoryItem> {
        return withContext(Dispatchers.IO) {
            try {
                val sharedPref = getSharedPreferences(PREF_NAME, MODE_PRIVATE)
                val gson = Gson()
                val json = sharedPref.getString(RIWAYAT_KEY, "[]")
                val type = object : TypeToken<List<HistoryItem>>() {}.type
                gson.fromJson(json, type) ?: emptyList()
            } catch (e: Exception) {
                Log.e(TAG, "Error loading history: ${e.message}", e)
                emptyList()
            }
        }
    }
}