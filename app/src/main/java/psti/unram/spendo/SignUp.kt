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

class SignUp : AppCompatActivity() {
    private val scope = CoroutineScope(Dispatchers.Main)
    private val USER_PREFS = "UserPrefs"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_signup)

        // Initialize views
        val ivBack = findViewById<ImageView>(R.id.ivBack)
        val etName = findViewById<EditText>(R.id.etname)
        val etEmail = findViewById<EditText>(R.id.etemail2)
        val etPassword = findViewById<TextInputEditText>(R.id.etpassword2)
        val etConfirmPassword = findViewById<TextInputEditText>(R.id.etconfirmpassword)
        val btnSignUp = findViewById<Button>(R.id.btnsignup)
        val tvLogin = findViewById<TextView>(R.id.tvlogin)

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

        // Sign-up button
        btnSignUp.setOnClickListener {
            val name = etName.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()
            val confirmPassword = etConfirmPassword.text.toString().trim()

            if (name.isEmpty()) {
                etName.error = "Nama wajib diisi"
                return@setOnClickListener
            }
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
            if (confirmPassword.isEmpty()) {
                etConfirmPassword.error = "Konfirmasi Password wajib diisi"
                return@setOnClickListener
            }
            if (confirmPassword.length < 8) {
                etConfirmPassword.error = "Konfirmasi Password setidaknya harus terdiri atas 8 karakter"
                return@setOnClickListener
            }
            if (password != confirmPassword) {
                etConfirmPassword.error = "Password tidak cocok"
                return@setOnClickListener
            }

            // Cek apakah email sudah terdaftar
            scope.launch {
                val existingUser = withContext(Dispatchers.IO) {
                    dao.getUserByUsername(email)
                }
                if (existingUser != null) {
                    etEmail.error = "Email sudah terdaftar"
                    return@launch
                }

                // Simpan pengguna ke database
                val user = User(username = email, password = password)
                withContext(Dispatchers.IO) {
                    dao.insertUser(user)
                }

                // Simpan email sebagai user_name ke SharedPreferences untuk Beranda
                val sharedPreferences = getSharedPreferences(USER_PREFS, MODE_PRIVATE)
                with(sharedPreferences.edit()) {
                    putString("user_name", email)
                    putString("user_email", email)
                    putString("user_password", password) // Simpan untuk kompatibilitas sementara
                    putString("display_name", name) // Simpan nama untuk tampilan
                    apply()
                }

                // Show success dialog
                val dialog = Dialog(this@SignUp)
                dialog.setContentView(R.layout.dialog_success)
                dialog.setCancelable(false)
                val tvSuccessMessage = dialog.findViewById<TextView>(R.id.tv_success_message)
                tvSuccessMessage.text = "Pendaftaran berhasil!"
                val btnOk = dialog.findViewById<Button>(R.id.btn_ok)
                btnOk.setOnClickListener {
                    dialog.dismiss()
                    // Navigate to Beranda
                    val intent = Intent(this@SignUp, Beranda::class.java)
                    startActivity(intent)
                    finish()
                }
                dialog.show()
            }
        }

        // Navigate to Login
        tvLogin.setOnClickListener {
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }
}