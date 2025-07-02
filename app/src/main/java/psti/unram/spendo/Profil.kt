package psti.unram.spendo

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

class Profil : AppCompatActivity() {

    private val USER_PREFS = "UserPrefs"
    private val KEY_NAMA = "user_name"
    private val KEY_EMAIL = "user_email"
    private val TAG = "Profil"
    private var isEditing = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_profil)

        // Inisialisasi view
        val ivProfile = findViewById<ImageView>(R.id.ivProfile)
        val etNama = findViewById<EditText>(R.id.etNama)
        val etEmail = findViewById<EditText>(R.id.etEmail)
        val btnEditProfil = findViewById<Button>(R.id.btnEditProfil)
        val btnProfilKeuangan = findViewById<Button>(R.id.btnProfilKeuangan)
        val btnLogout = findViewById<Button>(R.id.btnLogout)
        val ivBack = findViewById<ImageView>(R.id.ivBack)
        val ivhome = findViewById<ImageView>(R.id.ivhome)
        val ivinput = findViewById<ImageView>(R.id.ivinput)
        val ivToRiwayat = findViewById<ImageView>(R.id.ivToRiwayat)
        val ivuser = findViewById<ImageView>(R.id.ivuser)

        // Logging untuk memeriksa null view
        if (ivProfile == null) Log.e(TAG, "ivProfile is null, check activity_profil.xml for R.id.ivProfile")
        if (ivBack == null) Log.e(TAG, "ivBack is null, check activity_profil.xml for R.id.ivBack")
        if (ivhome == null) Log.e(TAG, "ivhome is null, check activity_profil.xml for R.id.ivhome")
        if (ivinput == null) Log.e(TAG, "ivinput is null, check activity_profil.xml for R.id.ivinput")
        if (ivToRiwayat == null) Log.e(TAG, "ivToRiwayat is null, check activity_profil.xml for R.id.ivToRiwayat")
        if (ivuser == null) Log.e(TAG, "ivuser is null, check activity_profil.xml for R.id.ivuser")

        // Muat data pengguna dari UserPrefs
        val userPrefs = getSharedPreferences(USER_PREFS, MODE_PRIVATE)
        etNama.setText(userPrefs.getString(KEY_NAMA, ""))
        etEmail.setText(userPrefs.getString(KEY_EMAIL, ""))

        // Tombol Edit Profil
        btnEditProfil.setOnClickListener {
            btnEditProfil.startAnimation(AnimationUtils.loadAnimation(this, R.anim.scale_bold))
            if (isEditing) {
                // Simpan nama
                val nama = etNama.text.toString().trim()
                if (nama.isEmpty()) {
                    Toast.makeText(this, "Nama tidak boleh kosong!", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                userPrefs.edit().putString(KEY_NAMA, nama).apply()
                etNama.isEnabled = false
                btnEditProfil.text = "Edit Profil"
                isEditing = false
                Toast.makeText(this, "Nama disimpan!", Toast.LENGTH_SHORT).show()
            } else {
                // Aktifkan edit nama
                etNama.isEnabled = true
                etNama.requestFocus()
                btnEditProfil.text = "Simpan"
                isEditing = true
            }
        }

        // Tombol Profil Keuangan
        btnProfilKeuangan.setOnClickListener {
            btnProfilKeuangan.startAnimation(AnimationUtils.loadAnimation(this, R.anim.scale_bold))
            try {
                startActivity(Intent(this, ProfilKeuangan::class.java))
            } catch (e: Exception) {
                Log.e(TAG, "Navigation Error to ProfilKeuangan: ${e.message}", e)
                Toast.makeText(this, "Gagal ke Profil Keuangan", Toast.LENGTH_SHORT).show()
            }
        }

        // Tombol Logout
        btnLogout.setOnClickListener {
            btnLogout.startAnimation(AnimationUtils.loadAnimation(this, R.anim.scale_bold))
            try {
                // Kosongkan UserPrefs, pertahankan profil_pref untuk data keuangan
                with(userPrefs.edit()) {
                    clear()
                    apply()
                }
                Toast.makeText(this, "Berhasil logout!", Toast.LENGTH_SHORT).show()
                // Pindah ke MainActivity (login screen)
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            } catch (e: Exception) {
                Log.e(TAG, "Logout Error: ${e.message}", e)
                Toast.makeText(this, "Gagal logout", Toast.LENGTH_SHORT).show()
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
            // Sudah di halaman Profil
            Toast.makeText(this, "Anda sudah di halaman Profil", Toast.LENGTH_SHORT).show()
        }
    }
}