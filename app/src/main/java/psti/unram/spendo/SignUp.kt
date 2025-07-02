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

class Signup : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_signup)
        val headerlayout = findViewById<LinearLayout>(R.id.headerLayout)
        val headertv = findViewById<TextView>(R.id.tvsgnup1)
        val formlay = findViewById<LinearLayout>(R.id.formLayout)
        val titletv = findViewById<TextView>(R.id.tvsgnup2)
        val email = findViewById<TextView>(R.id.tvemail)
        val inemail = findViewById<EditText>(R.id.etemail)
        val username =findViewById<TextView>(R.id.tvusername)
        val inusername = findViewById<EditText>(R.id.etusername)
        val password = findViewById<TextView>(R.id.tvpassword)
        val inpassword = findViewById<EditText>(R.id.etpassword)
        val btnsignup = findViewById<Button>(R.id.btnsignup)
        val akun = findViewById<TextView>(R.id.tvakun)
        val log = findViewById<TextView>(R.id. tvlogin)

        btnsignup.setOnClickListener {
            val intent = Intent(this, Beranda::class.java)
            startActivity(intent)
        }
        log.setOnClickListener {
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
        }
    }
    }
