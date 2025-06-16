package psti.unram.spendo

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.ImageView
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class Perhitungan : AppCompatActivity() {
    data class Criteria(val name: String, val value: Float, val isBenefit: Boolean, val maxValue: Float)

    private fun calculateSAW(criteria: List<Criteria>, weights: Map<String, Float>): Float {
        var totalScore = 0f
        criteria.forEach { crit ->
            val normalized = if (crit.isBenefit) {
                crit.value / crit.maxValue
            } else {
                1 - (crit.value / crit.maxValue)
            }
            totalScore += normalized * weights[crit.name]!!
        }
        return totalScore
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_perhitungan)

        val back = findViewById<ImageView>(R.id.ivBack)
        val result = findViewById<Button>(R.id.btnresult)
        val chart = findViewById<CustomPieChartView>(R.id.pieChartView)
        val resultText = findViewById<TextView>(R.id.resultText)

        val menu1 = findViewById<ImageView>(R.id.ivhome)
        val menu2 = findViewById<ImageView>(R.id.ivinput)
        val menu3 = findViewById<ImageView>(R.id.ivclock)
        val menu4 = findViewById<ImageView>(R.id.ivlight)
        val menu5 = findViewById<ImageView>(R.id.ivuser)

        val barang = intent.getStringExtra("barang") ?: ""
        val kategori = intent.getStringExtra("kategori") ?: ""
        val sumberDana = intent.getStringExtra("sumberDana") ?: ""
        val tanggal = intent.getStringExtra("tanggal") ?: ""

        val qualityValue = when (kategori) {
            "Sangat Penting" -> 10f
            "Penting" -> 7f
            "Tidak Penting" -> 4f
            else -> 0f
        }

        val fundValue = when (sumberDana) {
            "Tabungan" -> 10f
            "Kredit" -> 5f
            else -> 0f
        }

        val criteria = listOf(
            Criteria("Kualitas", qualityValue, true, 10f), // Benefit
            Criteria("Dana", fundValue, true, 10f)        // Benefit
        )
        val weights = mapOf(
            "Kualitas" to 0.6f, // Bobot kualitas 60%
            "Dana" to 0.4f     // Bobot dana 40%
        )

        val hasilAkhir = calculateSAW(criteria, weights)

        result.setOnClickListener {
            val totalWeight = weights.values.sum()
            val data = criteria.map { crit ->
                val normalized = if (crit.isBenefit) crit.value / crit.maxValue else 1 - (crit.value / crit.maxValue)
                val percentage = (normalized * weights[crit.name]!! / totalWeight) * 100
                PieChartData(crit.name, percentage, getColorForCriteria(crit.name))
            }
            chart.setData(data)

            when {
                hasilAkhir < 0.5 -> {
                    resultText.text = "Looks like a smart purchase! Just hold on a bit - your wallet needs a breather"
                }
                hasilAkhir in 0.5..0.75 -> {
                    resultText.text = "This purchase is feasible with some adjustments"
                }
                else -> {
                    resultText.text = "Great choice! This is a smart purchase"
                }
            }

            result.text = "Go to Recommendation"
            result.setOnClickListener {
                val intent = Intent(this, Rekomendasi::class.java)
                intent.putExtra("barang", barang)
                intent.putExtra("kategori", kategori)
                intent.putExtra("sumberDana", sumberDana)
                intent.putExtra("tanggal", tanggal)
                intent.putExtra("hasilAkhir", hasilAkhir)
                startActivity(intent)
            }
        }

        back.setOnClickListener { finish() }

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

    private fun getColorForCriteria(name: String): Int = when (name) {
        "Kualitas" -> Color.parseColor("#0066CC")
        "Dana" -> Color.parseColor("#0077D6")
        else -> Color.GRAY
    }
}