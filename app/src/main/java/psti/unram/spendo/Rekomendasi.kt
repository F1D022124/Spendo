package psti.unram.spendo

import android.content.Intent
import android.os.Bundle
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class Rekomendasi : AppCompatActivity() {

    private val PREF_NAME = "riwayat_pref"
    private val RIWAYAT_KEY = "riwayat_list"
    private val scope = CoroutineScope(Dispatchers.Main)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_rekomendasi)

        // Inisialisasi view
        val ivBack = findViewById<ImageView>(R.id.ivBack)
        val tvRecommendationStatus = findViewById<TextView>(R.id.tvRecommendationStatus)
        val ivRecommendationIcon = findViewById<ImageView>(R.id.ivRecommendationIcon)
        val tvRecommendationMessage = findViewById<TextView>(R.id.tvRecommendationMessage)
        val tvCalculationDetails = findViewById<TextView>(R.id.tvCalculationDetails)
        val btnTryAgain = findViewById<Button>(R.id.btnTryAgain)
        val ivhome = findViewById<ImageView>(R.id.ivhome)
        val ivinput = findViewById<ImageView>(R.id.ivinput)
        val ivToRiwayat = findViewById<ImageView>(R.id.ivToRiwayat)
        val ivuser = findViewById<ImageView>(R.id.ivuser)

        // Muat data riwayat terbaru secara asinkronus
        scope.launch {
            val latestItem = loadLatestHistoryItem()
            if (latestItem != null) {
                // Atur status rekomendasi
                when (latestItem.hasilRekomendasi) {
                    "Direkomendasikan" -> {
                        tvRecommendationStatus.text = "Direkomendasikan"
                        tvRecommendationStatus.setTextColor(getColor(R.color.green))
                        ivRecommendationIcon.setImageResource(R.drawable.baseline_shopping_cart_24)
                        ivRecommendationIcon.setColorFilter(getColor(R.color.green))
                        tvRecommendationMessage.text = "Anggaran Anda cukup untuk membeli ${latestItem.namaBarang} dengan aman!"
                    }
                    "Tunda" -> {
                        tvRecommendationStatus.text = "Tunda"
                        tvRecommendationStatus.setTextColor(getColor(R.color.yellow))
                        ivRecommendationIcon.setImageResource(R.drawable.baseline_hourglass_empty_24)
                        ivRecommendationIcon.setColorFilter(getColor(R.color.yellow))
                        tvRecommendationMessage.text = "Pertimbangkan kembali pembelian ${latestItem.namaBarang}. Anggaran Anda terbatas."
                    }
                    "Tidak Direkomendasikan" -> {
                        tvRecommendationStatus.text = "Tidak Direkomendasikan"
                        tvRecommendationStatus.setTextColor(getColor(R.color.red))
                        ivRecommendationIcon.setImageResource(R.drawable.baseline_block_24)
                        ivRecommendationIcon.setColorFilter(getColor(R.color.red))
                        tvRecommendationMessage.text = "Hindari membeli ${latestItem.namaBarang}. Anggaran Anda tidak memadai."
                    }
                }

                // Atur rincian perhitungan
                val details = """
                    Nama Barang: ${latestItem.namaBarang}
                    Harga: Rp${String.format("%,.0f", latestItem.harga)}
                    Fungsi: ${latestItem.fungsi}
                    Frekuensi: ${latestItem.frekuensi}
                    Pengeluaran Tambahan: Rp${String.format("%,.0f", latestItem.pengeluaranTambahan)}
                    Tanggal: ${latestItem.tanggal}
                    Gaji: Rp${String.format("%,.0f", latestItem.gaji ?: 0.0)}
                    Pengeluaran Tetap: Rp${String.format("%,.0f", latestItem.pengeluaranTetap ?: 0.0)}
                    Tanggungan: ${latestItem.tanggungan ?: 0}
                    Skor SAW: ${String.format("%.2f", latestItem.skorSAW ?: 0.0)}
                    Bobot: Harga (0.4), Fungsi (0.3), Frekuensi (0.2), Keuangan (0.1)
                """.trimIndent()
                tvCalculationDetails.text = details
            } else {
                tvRecommendationStatus.text = "Tidak Ada Data"
                tvRecommendationStatus.setTextColor(getColor(R.color.grey))
                ivRecommendationIcon.setImageResource(R.drawable.baseline_error_24)
                ivRecommendationIcon.setColorFilter(getColor(R.color.grey))
                tvRecommendationMessage.text = "Tidak ada riwayat pembelian untuk ditampilkan."
                tvCalculationDetails.text = "Silakan masukkan data pembelian di menu Input."
            }
        }

        // Navigasi
        ivBack.setOnClickListener {
            ivBack.startAnimation(AnimationUtils.loadAnimation(this, R.anim.scale_bold))
            startActivity(Intent(this, Beranda::class.java))
            finish()
        }

        btnTryAgain.setOnClickListener {
            btnTryAgain.startAnimation(AnimationUtils.loadAnimation(this, R.anim.scale_bold))
            startActivity(Intent(this, Form::class.java))
            finish()
        }

        ivhome.setOnClickListener {
            ivhome.startAnimation(AnimationUtils.loadAnimation(this, R.anim.scale_bold))
            startActivity(Intent(this, Beranda::class.java))
            finish()
        }

        ivinput.setOnClickListener {
            ivinput.startAnimation(AnimationUtils.loadAnimation(this, R.anim.scale_bold))
            startActivity(Intent(this, Form::class.java))
            finish()
        }

        ivToRiwayat.setOnClickListener {
            ivToRiwayat.startAnimation(AnimationUtils.loadAnimation(this, R.anim.scale_bold))
            startActivity(Intent(this, Riwayat::class.java))
            finish()
        }

        ivuser.setOnClickListener {
            ivuser.startAnimation(AnimationUtils.loadAnimation(this, R.anim.scale_bold))
            startActivity(Intent(this, Profil::class.java))
            finish()
        }
    }

    private suspend fun loadLatestHistoryItem(): HistoryItem? {
        return withContext(Dispatchers.IO) {
            try {
                val sharedPref = getSharedPreferences(PREF_NAME, MODE_PRIVATE)
                val gson = Gson()
                val json = sharedPref.getString(RIWAYAT_KEY, "[]")
                val type = object : TypeToken<MutableList<HistoryItem>>() {}.type
                val historyList: MutableList<HistoryItem> = gson.fromJson(json, type) ?: mutableListOf()
                historyList.lastOrNull()
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }
}