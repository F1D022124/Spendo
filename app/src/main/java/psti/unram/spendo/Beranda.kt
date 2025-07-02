package psti.unram.spendo

import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import psti.unram.spendo.data.AppDatabase
import psti.unram.spendo.network.ExchangeRateService
import psti.unram.spendo.network.Quadruple
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.*

class Beranda : AppCompatActivity() {
    private val scope = CoroutineScope(Dispatchers.Main)
    private val LOGIN_COUNT = "login_count"
    private val TAG = "Beranda"
    private val EXCHANGE_API_KEY = "583b8442eef08a77d21c9acb" // Ganti dengan API key dari exchangerate-api.com
    private val USER_PREFS = "UserPrefs"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_beranda)

        // Initialize views
        val tvberanda = findViewById<TextView>(R.id.tvberanda)
        val tvnombalance = findViewById<TextView>(R.id.tvnombalance)
        val tvnomexp = findViewById<TextView>(R.id.tvnomexp)
        val tvbalanceUSD = findViewById<TextView>(R.id.tvbalanceUSD)
        val tvexpUSD = findViewById<TextView>(R.id.tvexpUSD)
        val btnProfileKeuangan = findViewById<Button>(R.id.btnProfileKeuangan)
        val layoutInsights = findViewById<LinearLayout>(R.id.layoutInsights)
        val pieChart = findViewById<PieChart>(R.id.pieChart)
        val laytips = findViewById<LinearLayout>(R.id.lyttxtips)
        val tvtips2 = findViewById<TextView>(R.id.tvtips2)
        val tvStats2 = findViewById<TextView>(R.id.tvStats2)
        val lytRekomendasi = findViewById<LinearLayout>(R.id.lytRekomendasi)
        val tvRekomendasi2 = findViewById<TextView>(R.id.tvRekomendasi2)
        val ivhome = findViewById<ImageView>(R.id.ivhome)
        val ivinput = findViewById<ImageView>(R.id.ivinput)
        val ivToRiwayat = findViewById<ImageView>(R.id.ivToRiwayat)
        val ivuser = findViewById<ImageView>(R.id.ivuser)
        val progressBar = findViewById<ProgressBar>(R.id.progressBar)

        // Set animasi fade-in untuk kartu
        val fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in)
        layoutInsights.startAnimation(fadeIn)
        laytips.startAnimation(fadeIn)
        lytRekomendasi.startAnimation(fadeIn)

        // Ambil nama pengguna dari SharedPreferences
        val userPrefs = getSharedPreferences(USER_PREFS, Context.MODE_PRIVATE)
        val userName = userPrefs.getString("user_name", null)?.takeIf { it.isNotBlank() } ?: run {
            Toast.makeText(this, "Pengguna tidak ditemukan, silakan login ulang", Toast.LENGTH_LONG).show()
            startActivity(Intent(this, Login::class.java))
            finish()
            return
        }
        val displayName = userPrefs.getString("display_name", userName.split("@")[0])?.takeIf { it.isNotBlank() }?.replace("_", " ")?.split(" ")?.joinToString(" ") { word ->
            word.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
        } ?: userName.split("@")[0].replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
        Log.d(TAG, "UserName: $userName, DisplayName: $displayName")
        tvberanda.text = "Halo, $displayName!"

        // Inisialisasi database
        val db = AppDatabase.getDatabase(this)
        val dao = db.appDao()

        // Hitung jumlah login
        val loginPrefs = getSharedPreferences("LoginPrefs_$userName", Context.MODE_PRIVATE)
        val loginCount = loginPrefs.getInt(LOGIN_COUNT, 0)
        with(loginPrefs.edit()) {
            putInt(LOGIN_COUNT, loginCount + 1)
            apply()
        }

        // Inisialisasi tips keuangan dari assets/tips.json
        val gson = Gson()
        val tipsType = object : TypeToken<List<FinanceTip>>() {}.type
        val financeTipsList: List<FinanceTip> = try {
            assets.open("tips.json").bufferedReader().use { it.readText() }.let { json ->
                gson.fromJson(json, tipsType) ?: listOf()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Tips JSON Parsing Error: ${e.message}", e)
            listOf()
        }
        tvtips2.text = financeTipsList.randomOrNull()?.description ?: "Tidak ada tips tersedia"

        // Inisialisasi font Poppins untuk PieChart
        val poppinsFont = ResourcesCompat.getFont(this, R.font.poppins_bold)

        // Ambil data keuangan dan kurs mata uang secara asinkronus
        scope.launch {
            progressBar.visibility = View.VISIBLE

            // Ambil total saldo, pengeluaran, dan rekomendasi
            val (totalSaldo, totalPengeluaran, percentage, latestRecommendation) = withContext(Dispatchers.IO) {
                val user = dao.getUserByUsername(userName)
                if (user == null) {
                    Log.e(TAG, "User not found for username: $userName")
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@Beranda, "Pengguna tidak ditemukan, silakan login ulang", Toast.LENGTH_LONG).show()
                        startActivity(Intent(this@Beranda, Login::class.java))
                        finish()
                    }
                    return@withContext Quadruple(0.0, 0.0, 0, null)
                }
                val profil = dao.getProfilKeuanganByUserId(user.id)
                val gaji = profil?.gaji?.toDoubleOrNull() ?: 0.0
                val pengeluaranTetap = profil?.pengeluaranTetap?.toDoubleOrNull() ?: 0.0
                Log.d(TAG, "Loaded Gaji: $gaji, Pengeluaran Tetap: $pengeluaranTetap")

                val historyList = dao.getRiwayatByUserId(user.id)
                val totalRecommended = historyList
                    .filter { it.hasilRekomendasi == "Direkomendasikan" }
                    .sumOf { it.harga }
                val totalPengeluaran = pengeluaranTetap + totalRecommended
                val percentage = if (gaji > 0) (totalPengeluaran / gaji * 100).toInt() else 0
                val latest = historyList
                    .filter { it.hasilRekomendasi == "Direkomendasikan" }
                    .maxByOrNull {
                        try {
                            SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).parse(it.tanggal) ?: Date(0)
                        } catch (e: Exception) {
                            Log.e(TAG, "Date Parsing Error: ${e.message}", e)
                            Date(0)
                        }
                    }
                Quadruple(gaji, totalPengeluaran, percentage, latest)
            }

            // Perbarui teks tombol profil keuangan
            btnProfileKeuangan.text = if (loginCount == 0 || totalSaldo == 0.0) {
                "Isi Profil Keuangan"
            } else {
                "Perbarui Profil Keuangan"
            }

            // Update UI keuangan
            tvnombalance.text = "IDR %,d".format(totalSaldo.toInt())
            tvnomexp.text = "IDR %,d".format(totalPengeluaran.toInt())
            tvStats2.text = "Pengeluaran: $percentage% dari gaji"
            tvRekomendasi2.text = latestRecommendation?.let {
                "${it.namaBarang} (IDR %,d, ${it.tanggal})".format(it.harga.toInt())
            } ?: "Cek riwayat rekomendasi yang telah dilakukan"

            // Setup pie chart
            val entries = listOf(
                PieEntry(totalPengeluaran.toFloat(), "Pengeluaran"),
                PieEntry((totalSaldo - totalPengeluaran).toFloat(), "Sisa Saldo")
            )
            val dataSet = PieDataSet(entries, "Keuangan")
            dataSet.colors = listOf(
                ContextCompat.getColor(this@Beranda, R.color.kuning_pie), // Kuning untuk Pengeluaran
                ContextCompat.getColor(this@Beranda, R.color.biru_pie)  // Biru untuk Sisa Saldo
            )
            dataSet.valueTextSize = 12f
            dataSet.valueTextColor = ContextCompat.getColor(this@Beranda, R.color.tulisan) // Teks putih untuk nilai
            dataSet.valueTypeface = poppinsFont // Font Poppins untuk nilai
            val pieData = PieData(dataSet)
            pieChart.data = pieData
            pieChart.description.isEnabled = false
            pieChart.setEntryLabelColor(ContextCompat.getColor(this@Beranda, R.color.tulisan)) // Teks putih untuk label
            pieChart.setEntryLabelTextSize(12f)
            pieChart.setEntryLabelTypeface(poppinsFont) // Font Poppins untuk label
            pieChart.setDrawHoleEnabled(true) // Aktifkan hole untuk mengisi tengah
            pieChart.setHoleColor(ContextCompat.getColor(this@Beranda, R.color.background)) // Warna tengah sama dengan background card
            pieChart.setTransparentCircleColor(ContextCompat.getColor(this@Beranda, R.color.background)) // Warna transparan sama dengan background
            pieChart.setTransparentCircleAlpha(0) // Nonaktifkan transparansi
            pieChart.animateY(1000)

            // Ambil data kurs mata uang
            val exchangeRate = try {
                if (EXCHANGE_API_KEY.isEmpty()) {
                    Log.e(TAG, "EXCHANGE_API_KEY is empty")
                    0.000062
                } else {
                    val retrofit = Retrofit.Builder()
                        .baseUrl("https://api.exchangerate-api.com/v4/")
                        .addConverterFactory(GsonConverterFactory.create())
                        .build()
                    val service = retrofit.create(ExchangeRateService::class.java)
                    val response = service.getLatestRates(EXCHANGE_API_KEY)
                    Log.d(TAG, "Exchange API Response: ${response.rates}")
                    response.rates["USD"] ?: 0.000062
                }
            } catch (e: Exception) {
                Log.e(TAG, "Exchange API Error: ${e.message}", e)
                0.000062 // Fallback jika API gagal
            }
            tvbalanceUSD.text = "~USD %,d".format((totalSaldo * exchangeRate).toInt())
            tvexpUSD.text = "~USD %,d".format((totalPengeluaran * exchangeRate).toInt())

            // Sembunyikan ProgressBar setelah semua data dimuat
            progressBar.visibility = View.GONE
        }

        // Set click listeners untuk navigasi dengan animasi
        btnProfileKeuangan.setOnClickListener {
            btnProfileKeuangan.startAnimation(AnimationUtils.loadAnimation(this@Beranda, R.anim.scale_bold))
            try {
                startActivity(Intent(this@Beranda, ProfilKeuangan::class.java))
            } catch (e: Exception) {
                Log.e(TAG, "Navigation Error to ProfilKeuangan: ${e.message}", e)
            }
        }
        layoutInsights.setOnClickListener {
            layoutInsights.startAnimation(AnimationUtils.loadAnimation(this@Beranda, R.anim.scale_bold))
            try {
                startActivity(Intent(this@Beranda, ProfilKeuangan::class.java))
            } catch (e: Exception) {
                Log.e(TAG, "Navigation Error to ProfilKeuangan: ${e.message}", e)
            }
        }
        laytips.setOnClickListener {
            laytips.startAnimation(AnimationUtils.loadAnimation(this@Beranda, R.anim.scale_bold))
            try {
                startActivity(Intent(this@Beranda, Keuangan::class.java))
            } catch (e: Exception) {
                Log.e(TAG, "Navigation Error to Keuangan: ${e.message}", e)
            }
        }
        lytRekomendasi.setOnClickListener {
            lytRekomendasi.startAnimation(AnimationUtils.loadAnimation(this@Beranda, R.anim.scale_bold))
            try {
                startActivity(Intent(this@Beranda, Rekomendasi::class.java))
            } catch (e: Exception) {
                Log.e(TAG, "Navigation Error to Rekomendasi: ${e.message}", e)
            }
        }
        ivhome.setOnClickListener {
            ivhome.startAnimation(AnimationUtils.loadAnimation(this@Beranda, R.anim.scale_bold))
            // Sudah di Beranda
        }
        ivinput.setOnClickListener {
            ivinput.startAnimation(AnimationUtils.loadAnimation(this@Beranda, R.anim.scale_bold))
            try {
                startActivity(Intent(this@Beranda, Form::class.java))
            } catch (e: Exception) {
                Log.e(TAG, "Navigation Error to Form: ${e.message}", e)
            }
        }
        ivToRiwayat.setOnClickListener {
            ivToRiwayat.startAnimation(AnimationUtils.loadAnimation(this@Beranda, R.anim.scale_bold))
            try {
                startActivity(Intent(this@Beranda, Riwayat::class.java))
            } catch (e: Exception) {
                Log.e(TAG, "Navigation Error to Riwayat: ${e.message}", e)
            }
        }
        ivuser.setOnClickListener {
            ivuser.startAnimation(AnimationUtils.loadAnimation(this@Beranda, R.anim.scale_bold))
            try {
                startActivity(Intent(this@Beranda, Profil::class.java))
            } catch (e: Exception) {
                Log.e(TAG, "Navigation Error to Profil: ${e.message}", e)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }
}