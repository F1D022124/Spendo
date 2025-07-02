package psti.unram.spendo

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import psti.unram.spendo.data.AppDatabase
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.ceil

class Rekomendasi2 : AppCompatActivity() {
    private val USER_PREFS = "UserPrefs"
    private val TAG = "Rekomendasi2"
    private val scope = CoroutineScope(Dispatchers.Main)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_rekomendasi2)

        // Inisialisasi view
        val ivBack = findViewById<ImageView>(R.id.ivBack)
        val tvRecommendationStatus = findViewById<TextView>(R.id.tvRecommendationStatus)
        val ivRecommendationIcon = findViewById<ImageView>(R.id.ivRecommendationIcon)
        val tvRecommendationMessage = findViewById<TextView>(R.id.tvRecommendationMessage)
        val tvSavingTime = findViewById<TextView>(R.id.tvSavingTime)
        val tvCalculationDetails = findViewById<TextView>(R.id.tvCalculationDetails)
        val btnTryAgain = findViewById<Button>(R.id.btnTryAgain)
        val ivhome = findViewById<ImageView>(R.id.ivhome)
        val ivinput = findViewById<ImageView>(R.id.ivinput)
        val ivToRiwayat = findViewById<ImageView>(R.id.ivToRiwayat)
        val ivuser = findViewById<ImageView>(R.id.ivuser)

        // Logging untuk memeriksa null view
        if (ivBack == null) Log.e(TAG, "ivBack is null, check activity_rekomendasi2.xml for R.id.ivBack")
        if (tvRecommendationStatus == null) Log.e(TAG, "tvRecommendationStatus is null, check activity_rekomendasi2.xml for R.id.tvRecommendationStatus")
        if (ivRecommendationIcon == null) Log.e(TAG, "ivRecommendationIcon is null, check activity_rekomendasi2.xml for R.id.ivRecommendationIcon")
        if (tvRecommendationMessage == null) Log.e(TAG, "tvRecommendationMessage is null, check activity_rekomendasi2.xml for R.id.tvRecommendationMessage")
        if (tvSavingTime == null) Log.e(TAG, "tvSavingTime is null, check activity_rekomendasi2.xml for R.id.tvSavingTime")
        if (tvCalculationDetails == null) Log.e(TAG, "tvCalculationDetails is null, check activity_rekomendasi2.xml for R.id.tvCalculationDetails")
        if (btnTryAgain == null) Log.e(TAG, "btnTryAgain is null, check activity_rekomendasi2.xml for R.id.btnTryAgain")
        if (ivhome == null) Log.e(TAG, "ivhome is null, check activity_rekomendasi2.xml for R.id.ivhome")
        if (ivinput == null) Log.e(TAG, "ivinput is null, check activity_rekomendasi2.xml for R.id.ivinput")
        if (ivToRiwayat == null) Log.e(TAG, "ivToRiwayat is null, check activity_rekomendasi2.xml for R.id.ivToRiwayat")
        if (ivuser == null) Log.e(TAG, "ivuser is null, check activity_rekomendasi2.xml for R.id.ivuser")

        // Inisialisasi database
        val db = AppDatabase.getDatabase(this)
        val dao = db.appDao()

        // Muat data riwayat terbaru secara asinkronus
        scope.launch {
            // Ambil userName dari UserPrefs
            val userPrefs = getSharedPreferences(USER_PREFS, MODE_PRIVATE)
            val userName = userPrefs.getString("user_name", null)?.takeIf { it.isNotBlank() }
            if (userName == null) {
                Log.e(TAG, "UserName not found in SharedPreferences")
                Toast.makeText(this@Rekomendasi2, "Pengguna tidak ditemukan, silakan login ulang", Toast.LENGTH_LONG).show()
                startActivity(Intent(this@Rekomendasi2, Login::class.java))
                finish()
                return@launch
            }
            Log.d(TAG, "UserName: $userName")

            val historyItem = withContext(Dispatchers.IO) {
                // Coba ambil dari intent extra
                val gson = Gson()
                val json = intent.getStringExtra("HISTORY_ITEM")
                if (json != null) {
                    try {
                        Log.d(TAG, "Loading history item from intent: $json")
                        gson.fromJson(json, HistoryItem::class.java)
                    } catch (e: Exception) {
                        Log.e(TAG, "Error parsing intent extra: ${e.message}", e)
                        null
                    }
                } else {
                    // Jika tidak ada intent extra, ambil dari database
                    val user = dao.getUserByUsername(userName)
                    if (user == null) {
                        Log.e(TAG, "User not found for username: $userName")
                        withContext(Dispatchers.Main) {
                            Toast.makeText(this@Rekomendasi2, "Pengguna tidak ditemukan, silakan login ulang", Toast.LENGTH_LONG).show()
                            startActivity(Intent(this@Rekomendasi2, Login::class.java))
                            finish()
                        }
                        return@withContext null
                    }
                    val historyList = dao.getRiwayatByUserId(user.id)
                    Log.d(TAG, "History List Size: ${historyList.size}")
                    historyList.maxByOrNull {
                        try {
                            SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(it.tanggal) ?: Date(0)
                        } catch (e: Exception) {
                            Log.e(TAG, "Date Parsing Error: ${e.message}", e)
                            Date(0)
                        }
                    }?.let { riwayat ->
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
                }
            }

            // Perbarui UI berdasarkan historyItem
            if (historyItem != null && historyItem.hasilRekomendasi == "Tunda") {
                Log.d(TAG, "Displaying history item: ${historyItem.namaBarang}, Hasil: ${historyItem.hasilRekomendasi}")
                tvRecommendationStatus?.text = "Tunda"
                tvRecommendationStatus?.setTextColor(getColor(R.color.yellow))
                ivRecommendationIcon?.setImageResource(R.drawable.baseline_hourglass_empty_24)
                ivRecommendationIcon?.setColorFilter(getColor(R.color.yellow))
                tvRecommendationMessage?.text = "Pertimbangkan kembali pembelian ${historyItem.namaBarang}. Anggaran Anda terbatas."

                // Hitung estimasi waktu menabung (using old 20% savings rate)
                val gaji = historyItem.gaji ?: 0.0
                val pengeluaranTetap = historyItem.pengeluaranTetap ?: 0.0
                val pengeluaranTambahan = historyItem.pengeluaranTambahan
                val sisaAnggaran = gaji - pengeluaranTetap - pengeluaranTambahan
                val tabunganPerBulan = sisaAnggaran * 0.2 // Asumsi 20% sisa anggaran ditabung
                val estimasiBulan = if (tabunganPerBulan > 0) {
                    ceil(historyItem.harga / tabunganPerBulan).toInt()
                } else {
                    -1 // Tidak bisa menabung
                }
                tvSavingTime?.text = if (estimasiBulan > 0) {
                    "Estimasi Waktu Menabung: $estimasiBulan bulan"
                } else {
                    "Estimasi Waktu Menabung: Tidak memungkinkan dengan anggaran saat ini"
                }

                // Logging untuk debugging estimasi menabung
                Log.d(TAG, "Savings Calculation: gaji=$gaji, pengeluaranTetap=$pengeluaranTetap, pengeluaranTambahan=$pengeluaranTambahan, sisaAnggaran=$sisaAnggaran, tabunganPerBulan=$tabunganPerBulan, estimasiBulan=$estimasiBulan")

                // Atur rincian perhitungan
                val details = """
                    Nama Barang: ${historyItem.namaBarang}
                    Harga: Rp${String.format("%,.0f", historyItem.harga)}
                    Fungsi: ${historyItem.fungsi}
                    Frekuensi: ${historyItem.frekuensi}
                    Pengeluaran Tambahan: Rp${String.format("%,.0f", historyItem.pengeluaranTambahan)}
                    Tanggal: ${historyItem.tanggal}
                    Gaji: Rp${String.format("%,.0f", historyItem.gaji ?: 0.0)}
                    Pengeluaran Tetap: Rp${String.format("%,.0f", historyItem.pengeluaranTetap ?: 0.0)}
                    Tanggungan: ${historyItem.tanggungan ?: 0}
                    Skor SAW: ${String.format("%.2f", historyItem.skorSAW ?: 0.0)}
                    Bobot: Harga (0.4), Fungsi (0.3), Frekuensi (0.2), Keuangan (0.1)
                """.trimIndent()
                tvCalculationDetails?.text = details
            } else {
                Log.w(TAG, "No history item found for user or not 'Tunda'")
                tvRecommendationStatus?.text = "Tidak Ada Data"
                tvRecommendationStatus?.setTextColor(getColor(R.color.grey))
                ivRecommendationIcon?.setImageResource(R.drawable.baseline_error_24)
                ivRecommendationIcon?.setColorFilter(getColor(R.color.grey))
                tvRecommendationMessage?.text = "Tidak ada riwayat pembelian dengan status Tunda."
                tvSavingTime?.text = "Estimasi Waktu Menabung: -"
                tvCalculationDetails?.text = "Silakan masukkan data pembelian di menu Input."
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

        btnTryAgain?.setOnClickListener {
            btnTryAgain.startAnimation(AnimationUtils.loadAnimation(this, R.anim.scale_bold))
            try {
                startActivity(Intent(this, Form::class.java))
                finish()
            } catch (e: Exception) {
                Log.e(TAG, "Navigation Error to Form: ${e.message}", e)
                Toast.makeText(this, "Gagal ke Form", Toast.LENGTH_SHORT).show()
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
            try {
                startActivity(Intent(this, Riwayat::class.java))
                finish()
            } catch (e: Exception) {
                Log.e(TAG, "Navigation Error to Riwayat: ${e.message}", e)
                Toast.makeText(this, "Gagal ke Riwayat", Toast.LENGTH_SHORT).show()
            }
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

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }
}