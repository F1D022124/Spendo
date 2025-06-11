package psti.unram.spendo

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class Beranda : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_beranda)

        val headberanda = findViewById<LinearLayout>(R.id.layoutberanda)
        val title = findViewById<TextView>(R.id.tvberanda)
        val total = findViewById<LinearLayout>(R.id.lyttotal)
        //val balance = findViewById<LinearLayout>(R.id.lytbalance)
        val nambalance = findViewById<TextView>(R.id.tvbalance)
        val nombalance = findViewById<TextView>(R.id.tvnombalance)
        //val expense = findViewById<LinearLayout>(R.id.lytexp)
        val namexp = findViewById<TextView>(R.id.tvexpense)
        val nomexp = findViewById<TextView>(R.id.tvnomexp)
        val progsec = findViewById<LinearLayout>(R.id.progressSection)
        val chart = findViewById<LinearLayout>(R.id.lytchart)
        val persen = findViewById<TextView>(R.id.percentageText)
        val progbar = findViewById<ProgressBar>(R.id.progressBar)
        val totung = findViewById<TextView>(R.id.totalText)
        val recomend = findViewById<TextView>(R.id.tvrecomed)
        val layberanda = findViewById<LinearLayout>(R.id.layoutberanda)
        val ivtips = findViewById<ImageView>(R.id.ivtips)
        val laytips = findViewById<LinearLayout>(R.id.lyttxtips)
        val tips = findViewById<TextView>(R.id.tvtips)
        val tips2 = findViewById<TextView>(R.id.tvtips2)
        val layberanda2 = findViewById<LinearLayout>(R.id.layoutberanda2)
        val ivinsight = findViewById<ImageView>(R.id.ivinsight)
        val layinsght = findViewById<LinearLayout>(R.id.lytinsght)
        val insight = findViewById<TextView>(R.id.tvinsight)
        val insight2 = findViewById<TextView>(R.id.tvinsight2)
        val layberanda3 = findViewById<LinearLayout>(R.id.layoutberanda3)
        val layberanda4 = findViewById<LinearLayout>(R.id.layoutberanda4)
        val ivbudget = findViewById<ImageView>(R.id.ivbudget)
        val budget = findViewById<TextView>(R.id.tvbudget)
        val budget2 = findViewById<TextView>(R.id.tvbudget2)
        val footer = findViewById<LinearLayout>(R.id.footerLayout)
        val menu1 = findViewById<ImageView>(R.id.ivhome)
        val menu2 = findViewById<ImageView>(R.id.ivinput)
        val menu3 = findViewById<ImageView>(R.id.ivlight)
        val menu4 = findViewById<ImageView>(R.id.ivclock)
        val menu5 = findViewById<ImageView>(R.id.ivuser)

        // Ambil data dari SharedPreferences
        val sharedPref = getSharedPreferences("keuangan_pref", Context.MODE_PRIVATE)
        val balance = sharedPref.getFloat("balance", 0f).toDouble()
        val expense = sharedPref.getFloat("expense", 0f).toDouble()

        // Update tampilan saldo dan pengeluaran
        nombalance.text = "IDR %.2f".format(balance)
        nomexp.text = "IDR %.2f".format(expense)

        // Hitung persentase pengeluaran terhadap saldo
        val percentage = if (balance > 0) ((expense / balance) * 100).toInt() else 0
        persen.text = "$percentage%"
        progbar.progress = percentage.coerceIn(0, 100)

        menu1.setOnClickListener {
            val intent = Intent(this, Beranda::class.java)
            startActivity(intent)
        }
        menu2.setOnClickListener {
            val intent = Intent(this, Form::class.java)
            startActivity(intent)
        }
        menu3.setOnClickListener {
            val intent = Intent(this, Keuangan::class.java)
            startActivity(intent)
        }
        menu4.setOnClickListener {
            val intent = Intent(this, Riwayat::class.java)
            startActivity(intent)
        }
        menu5.setOnClickListener {
            val intent = Intent(this, Profil::class.java)
            startActivity(intent)
        }
        laytips.setOnClickListener {
            val intent = Intent(this, Keuangan::class.java)
            startActivity(intent)
        }
        layinsght.setOnClickListener {
            val intent = Intent(this, Rekomendasi::class.java)
            startActivity(intent)
        }
        layberanda4.setOnClickListener {
            val intent = Intent(this, Form::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        updateKeuangan()
    }

    private fun updateKeuangan() {
        val nombalance = findViewById<TextView>(R.id.tvnombalance)
        val nomexp = findViewById<TextView>(R.id.tvnomexp)
        val persen = findViewById<TextView>(R.id.percentageText)
        val progbar = findViewById<ProgressBar>(R.id.progressBar)

        val sharedPref = getSharedPreferences("keuangan_pref", Context.MODE_PRIVATE)
        val balance = sharedPref.getFloat("balance", 0f).toDouble()
        val expense = sharedPref.getFloat("expense", 0f).toDouble()

        nombalance.text = "IDR %.2f".format(balance)
        nomexp.text = "IDR %.2f".format(expense)

        val percentage = if (balance > 0) ((expense / balance) * 100).toInt() else 0
        persen.text = "$percentage%"
        progbar.progress = percentage.coerceIn(0, 100)
    }
}
