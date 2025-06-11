package psti.unram.spendo

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class Login : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
        val headerlayout2 = findViewById<LinearLayout>(R.id.headerLayout2)
        val headertv = findViewById<TextView>(R.id.tvlog)
        val formlay2 = findViewById<LinearLayout>(R.id.formLayout2)
        val titletv2 = findViewById<TextView>(R.id.tvlog2)
        val email2 = findViewById<TextView>(R.id.tvemail2)
        val inemail2 = findViewById<EditText>(R.id.etemail2)
        val password2 = findViewById<TextView>(R.id.tvpassword2)
        val inpassword2 = findViewById<EditText>(R.id.etpassword2)
        val btnlogin = findViewById<Button>(R.id.btnlogin)
        val akun2 = findViewById<TextView>(R.id.tvakun2)
        val sign = findViewById<TextView>(R.id. tvsignup)

        btnlogin.setOnClickListener {
            val intent = Intent(this, Beranda::class.java)
            startActivity(intent)
        }
        sign.setOnClickListener {
            val intent = Intent(this, Signup::class.java)
            startActivity(intent)
        }
    }
}