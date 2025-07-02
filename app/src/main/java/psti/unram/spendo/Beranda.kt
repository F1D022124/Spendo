package psti.unram.spendo

import android.content.Context
import android.content.Intent
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
    private val EXCHANGE_API_KEY = "583b8442eef08a77d21c9acb" // Ganti dengan API key valid dari exchangerate-api.com
    private val USER_PREFS = "UserPrefs"
    private val RATE_PREFS = "RatePrefs"

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

        // Logging untuk memeriksa null view
        if (tvberanda == null) Log.e(TAG, "tvberanda is null, check activity_beranda.xml for R.id.tvberanda")
        if (tvnombalance == null) Log.e(TAG, "tvnombalance is null, check activity_beranda.xml for R.id.tvnombalance")
        if (tvnomexp == null) Log.e(TAG, "tvnomexp is null, check activity_beranda.xml for R.id.tvnomexp")
        if (tvbalanceUSD == null) Log.e(TAG, "tvbalanceUSD is null, check activity_beranda.xml for R.id.tvbalanceUSD")
        if (tvexpUSD == null) Log.e(TAG, "tvexpUSD is null, check activity_beranda.xml for R.id.tvexpUSD")
        if (btnProfileKeuangan == null) Log.e(TAG, "btnProfileKeuangan is null, check activity_beranda.xml for R.id.btnProfileKeuangan")
        if (layoutInsights == null) Log.e(TAG, "layoutInsights is null, check activity_beranda.xml for R.id.layoutInsights")
        if (pieChart == null) Log.e(TAG, "pieChart is null, check activity_beranda.xml for R.id.pieChart")
        if (laytips == null) Log.e(TAG, "laytips is null, check activity_beranda.xml for R.id.lyttxtips")
        if (tvtips2 == null) Log.e(TAG, "tvtips2 is null, check activity_beranda.xml for R.id.tvtips2")
        if (tvStats2 == null) Log.e(TAG, "tvStats2 is null, check activity_beranda.xml for R.id.tvStats2")
        if (lytRekomendasi == null) Log.e(TAG, "lytRekomendasi is null, check activity_beranda.xml for R.id.lytRekomendasi")
        if (tvRekomendasi2 == null) Log.e(TAG, "tvRekomendasi2 is null, check activity_beranda.xml for R.id.tvRekomendasi2")
        if (ivhome == null) Log.e(TAG, "ivhome is null, check activity_beranda.xml for R.id.ivhome")
        if (ivinput == null) Log.e(TAG, "ivinput is null, check activity_beranda.xml for R.id.ivinput")
        if (ivToRiwayat == null) Log.e(TAG, "ivToRiwayat is null, check activity_beranda.xml for R.id.ivToRiwayat")
        if (ivuser == null) Log.e(TAG, "ivuser is null, check activity_beranda.xml for R.id.ivuser")
        if (progressBar == null) Log.e(TAG, "progressBar is null, check activity_beranda.xml for R.id.progressBar")

        // Set animasi fade-in untuk kartu
        val fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in)
        layoutInsights?.startAnimation(fadeIn)
        laytips?.startAnimation(fadeIn)
        lytRekomendasi?.startAnimation(fadeIn)

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
        tvberanda?.text = "Halo, $displayName!"

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
        tvtips2?.text = financeTipsList.randomOrNull()?.description ?: "Tidak ada tips tersedia"

        // Inisialisasi font Poppins untuk PieChart
        val poppinsFont = ResourcesCompat.getFont(this, R.font.poppins_bold)

        // Ambil data keuangan dan kurs mata uang secara asinkronus
        scope.launch {
            progressBar?.visibility = View.VISIBLE

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
                Log.d(TAG, "History List Size: ${historyList.size}")
                Log.d(TAG, "Raw history items: ${historyList.joinToString { "${it.namaBarang} (${it.tanggal}, ${it.hasilRekomendasi}, id=${it.id})" }}")
                val totalRecommended = historyList
                    .filter { it.hasilRekomendasi == "Direkomendasikan" }
                    .sumOf { it.harga }
                val totalPengeluaran = pengeluaranTetap + totalRecommended
                val percentage = if (gaji > 0) (totalPengeluaran / gaji * 100).toInt() else 0
                val latest = historyList
                    .filter { it.hasilRekomendasi == "Direkomendasikan" }
                    .maxByOrNull { it.id }
                Log.d(TAG, "Latest Recommendation: ${latest?.namaBarang ?: "None"}, Status: ${latest?.hasilRekomendasi ?: "None"}, Date: ${latest?.tanggal ?: "None"}, ID: ${latest?.id ?: 0}")
                Quadruple(gaji, totalPengeluaran, percentage, latest)
            }

            // Perbarui teks tombol profil keuangan
            btnProfileKeuangan?.text = if (loginCount == 0 || totalSaldo == 0.0) {
                "Isi Profil Keuangan"
            } else {
                "Perbarui Profil Keuangan"
            }

            // Update UI keuangan
            tvnombalance?.text = "IDR %,d".format(totalSaldo.toInt())
            tvnomexp?.text = "IDR %,d".format(totalPengeluaran.toInt())
            tvStats2?.text = "Pengeluaran: $percentage% dari gaji"
            tvRekomendasi2?.text = latestRecommendation?.let {
                "${it.namaBarang} (IDR %,d, ${it.tanggal})".format(it.harga.toInt())
            } ?: "Cek riwayat rekomendasi yang telah dilakukan"

            // Setup pie chart
            val entries = listOf(
                PieEntry(totalPengeluaran.toFloat(), "Pengeluaran"),
                PieEntry((totalSaldo - totalPengeluaran).toFloat(), "Sisa Saldo")
            )
            val dataSet = PieDataSet(entries, "Keuangan")
            dataSet.colors = listOf(
                ContextCompat.getColor(this@Beranda, R.color.kuning_pie),
                ContextCompat.getColor(this@Beranda, R.color.biru_pie)
            )
            dataSet.valueTextSize = 12f
            dataSet.valueTextColor = ContextCompat.getColor(this@Beranda, R.color.tulisan)
            dataSet.valueTypeface = poppinsFont
            val pieData = PieData(dataSet)
            pieChart?.data = pieData
            pieChart?.description?.isEnabled = false
            pieChart?.setEntryLabelColor(ContextCompat.getColor(this@Beranda, R.color.tulisan))
            pieChart?.setEntryLabelTextSize(12f)
            pieChart?.setEntryLabelTypeface(poppinsFont)
            pieChart?.setDrawHoleEnabled(true)
            pieChart?.setHoleColor(ContextCompat.getColor(this@Beranda, R.color.background))
            pieChart?.setTransparentCircleColor(ContextCompat.getColor(this@Beranda, R.color.background))
            pieChart?.setTransparentCircleAlpha(0)
            pieChart?.animateY(1000)

            // Ambil data kurs mata uang
            val ratePrefs = getSharedPreferences(RATE_PREFS, MODE_PRIVATE)
            val lastRate = ratePrefs.getFloat("last_usd_rate", 0.000062f).toDouble()
            val exchangeRate = try {
                if (EXCHANGE_API_KEY.isEmpty() || EXCHANGE_API_KEY == "YOUR_NEW_API_KEY") {
                    Log.e(TAG, "EXCHANGE_API_KEY is empty or not set")
                    Toast.makeText(this@Beranda, "Kunci API kurs mata uang tidak tersedia", Toast.LENGTH_SHORT).show()
                    lastRate
                } else {
                    val retrofit = Retrofit.Builder()
                        .baseUrl("https://v6.exchangerate-api.com/v6/")
                        .addConverterFactory(GsonConverterFactory.create())
                        .build()
                    val service = retrofit.create(ExchangeRateService::class.java)
                    val response = service.getLatestRates(EXCHANGE_API_KEY)
                    if (response.result != "success" || response.conversion_rates == null) {
                        Log.e(TAG, "Exchange API failed: result=${response.result}, conversion_rates=${response.conversion_rates}")
                        Toast.makeText(this@Beranda, "Gagal memuat kurs mata uang: ${response.result}", Toast.LENGTH_SHORT).show()
                        lastRate
                    } else {
                        Log.d(TAG, "Exchange API Response: ${response.conversion_rates}")
                        val rate = response.conversion_rates["IDR"]?.let { 1.0 / it } ?: lastRate
                        ratePrefs.edit().putFloat("last_usd_rate", rate.toFloat()).apply()
                        rate
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Exchange API Error: ${e.message}", e)
                if (e is retrofit2.HttpException) {
                    Toast.makeText(this@Beranda, "Gagal memuat kurs mata uang: HTTP ${e.code()}", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@Beranda, "Gagal memuat kurs mata uang: ${e.message}", Toast.LENGTH_SHORT).show()
                }
                lastRate
            }
            tvbalanceUSD?.text = "~USD %,d".format((totalSaldo * exchangeRate).toInt())
            tvexpUSD?.text = "~USD %,d".format((totalPengeluaran * exchangeRate).toInt())

            // Sembunyikan ProgressBar setelah semua data dimuat
            progressBar?.visibility = View.GONE

            // Set click listener untuk lytRekomendasi
            lytRekomendasi?.setOnClickListener {
                lytRekomendasi.startAnimation(AnimationUtils.loadAnimation(this@Beranda, R.anim.scale_bold))
                if (latestRecommendation != null) {
                    try {
                        val historyItem = HistoryItem(
                            namaBarang = latestRecommendation.namaBarang,
                            harga = latestRecommendation.harga,
                            fungsi = latestRecommendation.fungsi,
                            frekuensi = latestRecommendation.frekuensi,
                            pengeluaranTambahan = latestRecommendation.pengeluaranTambahan,
                            tanggal = latestRecommendation.tanggal,
                            hasilRekomendasi = latestRecommendation.hasilRekomendasi,
                            gaji = latestRecommendation.gaji,
                            pengeluaranTetap = latestRecommendation.pengeluaranTetap,
                            tanggungan = latestRecommendation.tanggungan,
                            skorSAW = latestRecommendation.skorSAW
                        )
                        val intent = Intent(this@Beranda, Rekomendasi::class.java)
                        intent.putExtra("HISTORY_ITEM", Gson().toJson(historyItem))
                        Log.d(TAG, "Navigating to detail for item: ${historyItem.namaBarang}, Hasil: ${historyItem.hasilRekomendasi}, Date: ${historyItem.tanggal}, ID: ${latestRecommendation.id}")
                        startActivity(intent)
                    } catch (e: Exception) {
                        Log.e(TAG, "Navigation Error to Rekomendasi: ${e.message}", e)
                        Toast.makeText(this@Beranda, "Gagal ke Rekomendasi", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@Beranda, "Belum ada rekomendasi", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Set click listeners untuk navigasi dengan animasi
        btnProfileKeuangan?.setOnClickListener {
            btnProfileKeuangan.startAnimation(AnimationUtils.loadAnimation(this@Beranda, R.anim.scale_bold))
            try {
                startActivity(Intent(this@Beranda, ProfilKeuangan::class.java))
            } catch (e: Exception) {
                Log.e(TAG, "Navigation Error to ProfilKeuangan: ${e.message}", e)
                Toast.makeText(this@Beranda, "Gagal ke Profil Keuangan", Toast.LENGTH_SHORT).show()
            }
        }
        layoutInsights?.setOnClickListener {
            layoutInsights.startAnimation(AnimationUtils.loadAnimation(this@Beranda, R.anim.scale_bold))
            try {
                startActivity(Intent(this@Beranda, ProfilKeuangan::class.java))
            } catch (e: Exception) {
                Log.e(TAG, "Navigation Error to ProfilKeuangan: ${e.message}", e)
                Toast.makeText(this@Beranda, "Gagal ke Profil Keuangan", Toast.LENGTH_SHORT).show()
            }
        }
        laytips?.setOnClickListener {
            laytips.startAnimation(AnimationUtils.loadAnimation(this@Beranda, R.anim.scale_bold))
            try {
                startActivity(Intent(this@Beranda, Keuangan::class.java))
            } catch (e: Exception) {
                Log.e(TAG, "Navigation Error to Keuangan: ${e.message}", e)
                Toast.makeText(this@Beranda, "Gagal ke Keuangan", Toast.LENGTH_SHORT).show()
            }
        }
        ivhome?.setOnClickListener {
            ivhome.startAnimation(AnimationUtils.loadAnimation(this@Beranda, R.anim.scale_bold))
            // Sudah di Beranda
        }
        ivinput?.setOnClickListener {
            ivinput.startAnimation(AnimationUtils.loadAnimation(this@Beranda, R.anim.scale_bold))
            try {
                startActivity(Intent(this@Beranda, Form::class.java))
            } catch (e: Exception) {
                Log.e(TAG, "Navigation Error to Form: ${e.message}", e)
                Toast.makeText(this@Beranda, "Gagal ke Form", Toast.LENGTH_SHORT).show()
            }
        }
        ivToRiwayat?.setOnClickListener {
            ivToRiwayat.startAnimation(AnimationUtils.loadAnimation(this@Beranda, R.anim.scale_bold))
            try {
                startActivity(Intent(this@Beranda, Riwayat::class.java))
            } catch (e: Exception) {
                Log.e(TAG, "Navigation Error to Riwayat: ${e.message}", e)
                Toast.makeText(this@Beranda, "Gagal ke Riwayat", Toast.LENGTH_SHORT).show()
            }
        }
        ivuser?.setOnClickListener {
            ivuser.startAnimation(AnimationUtils.loadAnimation(this@Beranda, R.anim.scale_bold))
            try {
                startActivity(Intent(this@Beranda, Profil::class.java))
            } catch (e: Exception) {
                Log.e(TAG, "Navigation Error to Profil: ${e.message}", e)
                Toast.makeText(this@Beranda, "Gagal ke Profil", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }
}