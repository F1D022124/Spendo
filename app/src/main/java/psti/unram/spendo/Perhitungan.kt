package psti.unram.spendo

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.ImageView
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class Perhitungan : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_perhitungan)

        val back = findViewById<ImageView>(R.id.ivBack)
        val result = findViewById<Button>(R.id.btnresult)
        val chart = findViewById<CustomPieChartView>(R.id.pieChartView)

        val menu1 = findViewById<ImageView>(R.id.ivhome)
        val menu2 = findViewById<ImageView>(R.id.ivinput)
        val menu3 = findViewById<ImageView>(R.id.ivclock)
        val menu4 = findViewById<ImageView>(R.id.ivlight)
        val menu5 = findViewById<ImageView>(R.id.ivuser)

        // Tombol kembali
        back.setOnClickListener {
            finish()
        }

        // Tombol "See the Result"
        result.setOnClickListener {
            // === 1. Data input dummy SAW ===
            val harga = 7f
            val kualitas = 8f
            val fitur = 6f

            val maxHarga = 10f
            val maxKualitas = 10f
            val maxFitur = 10f

            val bobotHarga = 0.5f
            val bobotKualitas = 0.3f
            val bobotFitur = 0.2f

            val normHarga = 1 - (harga / maxHarga)        // Cost
            val normKualitas = kualitas / maxKualitas     // Benefit
            val normFitur = fitur / maxFitur              // Benefit

            val hasilAkhir = (normHarga * bobotHarga) + (normKualitas * bobotKualitas) + (normFitur * bobotFitur)

            // === 2. Update ke PieChart ===
            val data = listOf(
                PieChartData("Harga", normHarga * bobotHarga * 100, Color.parseColor("#0066CC")),
                PieChartData("Kualitas", normKualitas * bobotKualitas * 100, Color.parseColor("#0077D6")),
                PieChartData("Fitur", normFitur * bobotFitur * 100, Color.parseColor("#0088E6"))
            )
            chart.setData(data)

            // === 3. Arahkan ke halaman sesuai hasil ===
            when {
                hasilAkhir < 0.5 -> {
                    val intent = Intent(this, Rekomendasi::class.java)
                    startActivity(intent)
                }
                hasilAkhir in 0.5..0.75 -> {
                    val intent = Intent(this, Rekomendasi2::class.java)
                    startActivity(intent)
                }
                else -> {
                    val intent = Intent(this, Rekomendasi3::class.java)
                    startActivity(intent)
                }
            }
        }

        // Menu bawah
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
            startActivity(Intent(this, Riwayat::class.java))
        }
        menu5.setOnClickListener {
            startActivity(Intent(this, Profil::class.java))
        }
    }
}
