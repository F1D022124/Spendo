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

class Rekomendasi3 : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_rekomendasi3)

        // Inisialisasi view
        val ivBack = findViewById<ImageView>(R.id.ivBack)
        val ivhome = findViewById<ImageView>(R.id.ivhome)
        val ivinput = findViewById<ImageView>(R.id.ivinput)
        val ivToRiwayat = findViewById<ImageView>(R.id.ivToRiwayat)
        val ivuser = findViewById<ImageView>(R.id.ivuser)
        val btnTryAgain = findViewById<Button>(R.id.btnTryAgain)
        val tvCalculationDetails = findViewById<TextView>(R.id.tvCalculationDetails)

        // Ambil data dari Intent
        val gson = Gson()
        val historyItemJson = intent.getStringExtra("HISTORY_ITEM")
        val historyItem = historyItemJson?.let {
            gson.fromJson(it, HistoryItem::class.java)
        }

        // Tampilkan rincian perhitungan
        historyItem?.let {
            tvCalculationDetails.text = """
                Nama Barang: ${it.namaBarang}
                Harga: Rp${String.format("%,.0f", it.harga)}
                Fungsi: ${it.fungsi}
                Frekuensi: ${it.frekuensi}
                Pengeluaran Tambahan: Rp${String.format("%,.0f", it.pengeluaranTambahan)}
                Gaji: Rp${String.format("%,.0f", it.gaji)}
                Pengeluaran Tetap: Rp${String.format("%,.0f", it.pengeluaranTetap)}
                Tanggungan: ${it.tanggungan}
                Skor SAW: ${String.format("%.3f", it.skorSAW)}
                Bobot: Harga (0.4), Fungsi (0.3), Frekuensi (0.2), Keuangan (0.1)
            """.trimIndent()
        }

        // Navigasi
        ivBack.setOnClickListener {
            ivBack.startAnimation(AnimationUtils.loadAnimation(this, R.anim.scale_bold))
            startActivity(Intent(this, Beranda::class.java))
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

        btnTryAgain.setOnClickListener {
            btnTryAgain.startAnimation(AnimationUtils.loadAnimation(this, R.anim.scale_bold))
            startActivity(Intent(this, Form::class.java))
            finish()
        }
    }
}