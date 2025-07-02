package psti.unram.spendo

import android.content.Intent
import android.os.Bundle
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
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

class Keuangan : AppCompatActivity() {

    private val scope = CoroutineScope(Dispatchers.Main)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_keuangan)

        // Inisialisasi view
        val ivBack = findViewById<ImageView>(R.id.ivBack)
        val tvTitle = findViewById<TextView>(R.id.tvTitle)
        val rvTips = findViewById<RecyclerView>(R.id.rvTips)
        val ivhome = findViewById<ImageView>(R.id.ivhome)
        val ivinput = findViewById<ImageView>(R.id.ivinput)
        val ivclock = findViewById<ImageView>(R.id.ivToRiwayat)
        val ivuser = findViewById<ImageView>(R.id.ivuser)

        // Muat tips secara asinkronus
        scope.launch {
            val tips = loadTipsFromJson()
            rvTips.layoutManager = LinearLayoutManager(this@Keuangan)
            rvTips.adapter = TipsAdapter(tips)
            // Removed: rvTips.isNestedScrollingEnabled = false
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

        ivclock.setOnClickListener {
            ivclock.startAnimation(AnimationUtils.loadAnimation(this, R.anim.scale_bold))
            startActivity(Intent(this, Riwayat::class.java))
            finish()
        }

        ivuser.setOnClickListener {
            ivuser.startAnimation(AnimationUtils.loadAnimation(this, R.anim.scale_bold))
            startActivity(Intent(this, Profil::class.java))
            finish()
        }
    }

    private suspend fun loadTipsFromJson(): List<FinanceTip> {
        return withContext(Dispatchers.IO) {
            try {
                val json = assets.open("tips.json").bufferedReader().use { it.readText() }
                val type = object : TypeToken<List<FinanceTip>>() {}.type
                Gson().fromJson(json, type) ?: emptyList()
            } catch (e: Exception) {
                e.printStackTrace()
                emptyList()
            }
        }
    }
}