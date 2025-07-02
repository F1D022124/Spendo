package psti.unram.spendo

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import psti.unram.spendo.data.AppDatabase
import psti.unram.spendo.data.ProfilKeuangan

class ProfilKeuangan : AppCompatActivity() {
    private val USER_PREFS = "UserPrefs"
    private val TAG = "ProfilKeuangan"
    private val scope = CoroutineScope(Dispatchers.Main)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_profil_keuangan)

        // Inisialisasi view
        val etGaji = findViewById<EditText>(R.id.etGaji)
        val etPengeluaranTetap = findViewById<EditText>(R.id.etPengeluaranTetap)
        val etTanggungan = findViewById<EditText>(R.id.etTanggungan)
        val btnSimpan = findViewById<Button>(R.id.btnSimpan)
        val ivBack = findViewById<ImageView>(R.id.ivBack)
        val ivhome = findViewById<ImageView>(R.id.ivhome)
        val ivinput = findViewById<ImageView>(R.id.ivinput)
        val ivlight = findViewById<ImageView>(R.id.ivlight)
        val ivclock = findViewById<ImageView>(R.id.ivclock)
        val ivuser = findViewById<ImageView>(R.id.ivuser)

        // Logging untuk memeriksa null view
        if (etGaji == null) Log.e(TAG, "etGaji is null, check activity_profil_keuangan.xml for R.id.etGaji")
        if (etPengeluaranTetap == null) Log.e(TAG, "etPengeluaranTetap is null, check activity_profil_keuangan.xml for R.id.etPengeluaranTetap")
        if (etTanggungan == null) Log.e(TAG, "etTanggungan is null, check activity_profil_keuangan.xml for R.id.etTanggungan")
        if (btnSimpan == null) Log.e(TAG, "btnSimpan is null, check activity_profil_keuangan.xml for R.id.btnSimpan")
        if (ivBack == null) Log.e(TAG, "ivBack is null, check activity_profil_keuangan.xml for R.id.ivBack")
        if (ivhome == null) Log.e(TAG, "ivhome is null, check activity_profil_keuangan.xml for R.id.ivhome")
        if (ivinput == null) Log.e(TAG, "ivinput is null, check activity_profil_keuangan.xml for R.id.ivinput")
        if (ivlight == null) Log.e(TAG, "ivlight is null, check activity_profil_keuangan.xml for R.id.ivlight")
        if (ivclock == null) Log.e(TAG, "ivclock is null, check activity_profil_keuangan.xml for R.id.ivclock")
        if (ivuser == null) Log.e(TAG, "ivuser is null, check activity_profil_keuangan.xml for R.id.ivuser")

        // Ambil userName dari UserPrefs
        val userPrefs = getSharedPreferences(USER_PREFS, MODE_PRIVATE)
        val userName = userPrefs.getString("user_name", null)?.takeIf { it.isNotBlank() } ?: run {
            Toast.makeText(this, "Pengguna tidak ditemukan, silakan login ulang", Toast.LENGTH_LONG).show()
            startActivity(Intent(this, Login::class.java))
            finish()
            return
        }
        Log.d(TAG, "UserName: $userName")

        // Inisialisasi database
        val db = AppDatabase.getDatabase(this)
        val dao = db.appDao()

        // Muat data profil keuangan
        scope.launch {
            val user = withContext(Dispatchers.IO) { dao.getUserByUsername(userName) }
            if (user == null) {
                Log.e(TAG, "User not found for username: $userName")
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@ProfilKeuangan, "Pengguna tidak ditemukan, silakan login ulang", Toast.LENGTH_LONG).show()
                    startActivity(Intent(this@ProfilKeuangan, Login::class.java))
                    finish()
                }
                return@launch
            }
            val profil = withContext(Dispatchers.IO) { dao.getProfilKeuanganByUserId(user.id) }
            profil?.let {
                etGaji.setText(it.gaji)
                etPengeluaranTetap.setText(it.pengeluaranTetap)
                etTanggungan.setText(it.tanggungan)
                Log.d(TAG, "Loaded Gaji: ${it.gaji}, Pengeluaran Tetap: ${it.pengeluaranTetap}, Tanggungan: ${it.tanggungan}")
            }
        }

        // Tombol Simpan
        btnSimpan.setOnClickListener {
            btnSimpan.startAnimation(AnimationUtils.loadAnimation(this, R.anim.scale_bold))

            val gaji = etGaji.text.toString().trim().replace(",", "").replace(" ", "")
            val pengeluaranTetap = etPengeluaranTetap.text.toString().trim().replace(",", "").replace(" ", "")
            val tanggungan = etTanggungan.text.toString().trim()

            // Validasi input
            if (gaji.isEmpty() || pengeluaranTetap.isEmpty() || tanggungan.isEmpty()) {
                Toast.makeText(this, "Lengkapi semua data keuangan!", Toast.LENGTH_SHORT).show()
                Log.w(TAG, "Validation failed: One or more fields are empty")
                return@setOnClickListener
            }

            try {
                val gajiDouble = gaji.toDouble()
                val pengeluaranTetapDouble = pengeluaranTetap.toDouble()
                val tanggunganInt = tanggungan.toInt()

                if (gajiDouble < 0 || pengeluaranTetapDouble < 0 || tanggunganInt < 0) {
                    Toast.makeText(this, "Nilai tidak boleh negatif!", Toast.LENGTH_SHORT).show()
                    Log.w(TAG, "Validation failed: Negative value detected")
                    return@setOnClickListener
                }

                if (pengeluaranTetapDouble > gajiDouble) {
                    Toast.makeText(this, "Pengeluaran tetap tidak boleh melebihi gaji!", Toast.LENGTH_SHORT).show()
                    Log.w(TAG, "Validation failed: Pengeluaran ($pengeluaranTetapDouble) exceeds Gaji ($gajiDouble)")
                    return@setOnClickListener
                }

                // Simpan ke database
                scope.launch {
                    val user = withContext(Dispatchers.IO) { dao.getUserByUsername(userName) }
                    if (user == null) {
                        Log.e(TAG, "User not found for username: $userName")
                        withContext(Dispatchers.Main) {
                            Toast.makeText(this@ProfilKeuangan, "Pengguna tidak ditemukan, silakan login ulang", Toast.LENGTH_LONG).show()
                            startActivity(Intent(this@ProfilKeuangan, Login::class.java))
                            finish()
                        }
                        return@launch
                    }

                    val existingProfil = withContext(Dispatchers.IO) { dao.getProfilKeuanganByUserId(user.id) }
                    val profilKeuangan = ProfilKeuangan(
                        id = existingProfil?.id ?: 0,
                        userId = user.id,
                        gaji = gaji,
                        pengeluaranTetap = pengeluaranTetap,
                        tanggungan = tanggungan
                    )

                    withContext(Dispatchers.IO) {
                        if (existingProfil == null) {
                            dao.insertProfilKeuangan(profilKeuangan)
                        } else {
                            dao.updateProfilKeuangan(profilKeuangan)
                        }
                    }

                    Log.d(TAG, "Saved Gaji: $gaji, Pengeluaran Tetap: $pengeluaranTetap, Tanggungan: $tanggungan")
                    Toast.makeText(this@ProfilKeuangan, "Data keuangan disimpan!", Toast.LENGTH_SHORT).show()

                    // Pindah ke Beranda
                    try {
                        val intent = Intent(this@ProfilKeuangan, Beranda::class.java)
                        startActivity(intent)
                        finish()
                    } catch (e: Exception) {
                        Log.e(TAG, "Navigation Error to Beranda: ${e.message}", e)
                        Toast.makeText(this@ProfilKeuangan, "Gagal ke Beranda", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: NumberFormatException) {
                Log.e(TAG, "Number Format Error: ${e.message}", e)
                Toast.makeText(this, "Gunakan angka tanpa tanda baca (contoh: 10000000)", Toast.LENGTH_LONG).show()
            } catch (e: Exception) {
                Log.e(TAG, "Save Error: ${e.message}", e)
                Toast.makeText(this, "Gagal menyimpan data: ${e.message}", Toast.LENGTH_SHORT).show()
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

        ivlight?.setOnClickListener {
            ivlight.startAnimation(AnimationUtils.loadAnimation(this, R.anim.scale_bold))
            Toast.makeText(this, "Anda sudah di halaman Profil Keuangan", Toast.LENGTH_SHORT).show()
        }

        ivclock?.setOnClickListener {
            ivclock.startAnimation(AnimationUtils.loadAnimation(this, R.anim.scale_bold))
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