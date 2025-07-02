package psti.unram.spendo

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import psti.unram.spendo.data.AppDatabase

class Riwayat : AppCompatActivity(), HistoryAdapter.OnItemClickListener {
    private val USER_PREFS = "UserPrefs"
    private val TAG = "Riwayat"
    private val scope = CoroutineScope(Dispatchers.Main)
    private lateinit var historyAdapter: HistoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_riwayat)

        // Inisialisasi view
        val rvHistory = findViewById<RecyclerView>(R.id.rvHistory)
        val tvNoData = findViewById<TextView>(R.id.tvNoData)
        val ivBack = findViewById<ImageView>(R.id.ivBack)
        val ivhome = findViewById<ImageView>(R.id.ivhome)
        val ivinput = findViewById<ImageView>(R.id.ivinput)
        val ivToRiwayat = findViewById<ImageView>(R.id.ivToRiwayat)
        val ivuser = findViewById<ImageView>(R.id.ivuser)

        // Logging untuk memeriksa null view
        if (rvHistory == null) Log.e(TAG, "rvHistory is null, check activity_riwayat.xml for R.id.rvHistory")
        if (tvNoData == null) Log.e(TAG, "tvNoData is null, check activity_riwayat.xml for R.id.tvNoData")
        if (ivBack == null) Log.e(TAG, "ivBack is null, check activity_riwayat.xml for R.id.ivBack")
        if (ivhome == null) Log.e(TAG, "ivhome is null, check activity_riwayat.xml for R.id.ivhome")
        if (ivinput == null) Log.e(TAG, "ivinput is null, check activity_riwayat.xml for R.id.ivinput")
        if (ivToRiwayat == null) Log.e(TAG, "ivToRiwayat is null, check activity_riwayat.xml for R.id.ivToRiwayat")
        if (ivuser == null) Log.e(TAG, "ivuser is null, check activity_riwayat.xml for R.id.ivuser")

        // Inisialisasi RecyclerView
        historyAdapter = HistoryAdapter(emptyList(), this)
        rvHistory?.apply {
            layoutManager = LinearLayoutManager(this@Riwayat)
            adapter = historyAdapter
        }

        // Ambil userName dari UserPrefs
        val userPrefs = getSharedPreferences(USER_PREFS, MODE_PRIVATE)
        val userName = userPrefs.getString("user_name", null)?.takeIf { it.isNotBlank() }
        if (userName == null) {
            Log.e(TAG, "UserName not found in SharedPreferences")
            Toast.makeText(this, "Pengguna tidak ditemukan, silakan login ulang", Toast.LENGTH_LONG).show()
            startActivity(Intent(this, Login::class.java))
            finish()
            return
        }
        Log.d(TAG, "UserName: $userName")

        // Inisialisasi database
        val db = AppDatabase.getDatabase(this)
        val dao = db.appDao()

        // Muat riwayat berdasarkan userId
        scope.launch {
            val user = withContext(Dispatchers.IO) { dao.getUserByUsername(userName) }
            if (user == null) {
                Log.e(TAG, "User not found for username: $userName")
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@Riwayat, "Pengguna tidak ditemukan, silakan login ulang", Toast.LENGTH_LONG).show()
                    startActivity(Intent(this@Riwayat, Login::class.java))
                    finish()
                }
                return@launch
            }

            val riwayatList = withContext(Dispatchers.IO) { dao.getRiwayatByUserId(user.id) }
            Log.d(TAG, "Raw history items: ${riwayatList.joinToString { "${it.namaBarang} (id=${it.id}, ${it.tanggal}, ${it.hasilRekomendasi})" }}")
            val historyItems = riwayatList
                .sortedByDescending { it.id } // Sort by id in descending order
                .map { riwayat ->
                    HistoryItem(
                        namaBarang = riwayat.namaBarang,
                        harga = riwayat.harga,
                        fungsi = riwayat.fungsi,
                        frekuensi = riwayat.frekuensi,
                        pengeluaranTambahan = riwayat.pengeluaranTambahan,
                        tanggal = riwayat.tanggal,
                        hasilRekomendasi = riwayat.hasilRekomendasi,
                        gaji = riwayat.gaji,
                        pengeluaranTetap = riwayat.pengeluaranTetap,
                        tanggungan = riwayat.tanggungan,
                        skorSAW = riwayat.skorSAW
                    )
                }
            Log.d(TAG, "Loaded ${historyItems.size} history items for userId: ${user.id}")
            Log.d(TAG, "Sorted history items: ${historyItems.joinToString { "${it.namaBarang} (${it.tanggal}, ${it.hasilRekomendasi})" }}")
            withContext(Dispatchers.Main) {
                historyAdapter.updateData(historyItems)
                if (historyItems.isEmpty()) {
                    tvNoData?.visibility = View.VISIBLE
                    rvHistory?.visibility = View.GONE
                    Toast.makeText(this@Riwayat, "Belum ada riwayat pembelian", Toast.LENGTH_SHORT).show()
                } else {
                    tvNoData?.visibility = View.GONE
                    rvHistory?.visibility = View.VISIBLE
                }
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
            // Sudah di Riwayat
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

    override fun onItemClick(historyItem: HistoryItem) {
        Log.d(TAG, "Navigating to detail for item: ${historyItem.namaBarang}, Hasil: ${historyItem.hasilRekomendasi}")
        val intent = when (historyItem.hasilRekomendasi) {
            "Direkomendasikan" -> Intent(this, Rekomendasi::class.java)
            "Tunda" -> Intent(this, Rekomendasi2::class.java)
            "Tidak Direkomendasikan" -> Intent(this, Rekomendasi3::class.java)
            else -> Intent(this, Rekomendasi::class.java) // Fallback
        }
        try {
            intent.putExtra("HISTORY_ITEM", Gson().toJson(historyItem))
            startActivity(intent)
        } catch (e: Exception) {
            Log.e(TAG, "Navigation Error to Rekomendasi: ${e.message}", e)
            Toast.makeText(this, "Gagal menampilkan detail rekomendasi", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }
}