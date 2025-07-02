package psti.unram.spendo

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import psti.unram.spendo.data.AppDatabase
import psti.unram.spendo.data.User

class Login : AppCompatActivity() {
    private val scope = CoroutineScope(Dispatchers.Main)
    private val USER_PREFS = "UserPrefs"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        // Initialize views
        val ivBack = findViewById<ImageView>(R.id.ivBack)
        val etEmail = findViewById<EditText>(R.id.etemail2)
        val etPassword = findViewById<TextInputEditText>(R.id.etpassword2)
        val btnLogin = findViewById<Button>(R.id.btnlogin)
        val tvSignUp = findViewById<TextView>(R.id.tvsignup)

        // Inisialisasi database
        val db = AppDatabase.getDatabase(this)
        val dao = db.appDao()

        // Animasi skala untuk tombol Back
        val scaleAnimation = AnimationUtils.loadAnimation(this, R.anim.scale_bold)

        // Back button click listener
        ivBack.setOnClickListener {
            ivBack.startAnimation(scaleAnimation)
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        // Login button
        btnLogin.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (email.isEmpty()) {
                etEmail.error = "Email wajib diisi"
                return@setOnClickListener
            }
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                etEmail.error = "Masukkan Email yang valid (contoh: user@domain.com)"
                return@setOnClickListener
            }
            if (password.isEmpty()) {
                etPassword.error = "Password wajib diisi"
                return@setOnClickListener
            }
            if (password.length < 8) {
                etPassword.error = "Password setidaknya harus terdiri atas 8 karakter"
                return@setOnClickListener
            }

            // Verifikasi login menggunakan database Room
            scope.launch {
                val user = withContext(Dispatchers.IO) {
                    dao.getUserByUsername(email)
                }
                if (user != null && user.password == password) {
                    // Simpan email dan nama ke SharedPreferences untuk Beranda
                    val sharedPreferences = getSharedPreferences(USER_PREFS, MODE_PRIVATE)
                    with(sharedPreferences.edit()) {
                        putString("user_name", email)
                        putString("user_email", email)
                        putString("user_password", password) // Simpan untuk kompatibilitas sementara
                        apply()
                    }

                    // Show success dialog
                    val dialog = Dialog(this@Login)
                    dialog.setContentView(R.layout.dialog_success)
                    dialog.setCancelable(false)
                    val tvSuccessMessage = dialog.findViewById<TextView>(R.id.tv_success_message)
                    tvSuccessMessage.text = "Login berhasil!"
                    val btnOk = dialog.findViewById<Button>(R.id.btn_ok)
                    btnOk.setOnClickListener {
                        dialog.dismiss()
                        // Navigate to Beranda
                        val intent = Intent(this@Login, Beranda::class.java)
                        startActivity(intent)
                        finish()
                    }
                    dialog.show()
                } else {
                    etEmail.error = "Email atau password salah"
                    etPassword.error = "Email atau password salah"
                    Toast.makeText(this@Login, "Email atau password salah", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Navigate to SignUp
        tvSignUp.setOnClickListener {
            val intent = Intent(this, SignUp::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }
}