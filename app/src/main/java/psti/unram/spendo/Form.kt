package psti.unram.spendo

import android.app.DatePickerDialog
import android.content.Intent
import android.content.SharedPreferences
import android.icu.util.Calendar
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Form : AppCompatActivity() {

    private val PREF_NAME = "riwayat_pref"
    private val RIWAYAT_KEY = "riwayat_list"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_form)

        val inbarang = findViewById<EditText>(R.id.etBarang)
        val kategoriDropdown = findViewById<AutoCompleteTextView>(R.id.dropdownKategori)
        val sumberDanaDropdown = findViewById<AutoCompleteTextView>(R.id.dropdownDana)
        val tanggalField = findViewById<EditText>(R.id.etTanggal)
        val submit = findViewById<Button>(R.id.btnSubmit)

        val home = findViewById<ImageView>(R.id.ivhome)
        val input = findViewById<ImageView>(R.id.ivinput)
        val light = findViewById<ImageView>(R.id.ivlight)
        val jam = findViewById<ImageView>(R.id.ivclock)
        val user = findViewById<ImageView>(R.id.ivuser)

        val kategoriList = listOf("Tidak Penting", "Penting", "Sangat Penting")
        val danaList = listOf("Tabungan", "Kredit")

        kategoriDropdown.setAdapter(ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, kategoriList))
        kategoriDropdown.threshold = 0
        kategoriDropdown.setOnClickListener { kategoriDropdown.showDropDown() }
        sumberDanaDropdown.setAdapter(ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, danaList))
        sumberDanaDropdown.threshold = 0
        sumberDanaDropdown.setOnClickListener { sumberDanaDropdown.showDropDown() }

        // Tanggal picker
        tanggalField.setOnClickListener {
            val calendar = Calendar.getInstance()
            val datePicker = DatePickerDialog(this, { _, year, month, day ->
                val selectedDate = "$day/${month + 1}/$year"
                tanggalField.setText(selectedDate)
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))
            datePicker.show()
        }

//        submit.setOnClickListener {
//            val barang = inbarang.text.toString()
//            val kategori = kategoriDropdown.text.toString()
//            val sumberDana = sumberDanaDropdown.text.toString()
//            val tanggal = tanggalField.text.toString()
//
//            if (barang.isEmpty() || kategori.isEmpty() || sumberDana.isEmpty() || tanggal.isEmpty()) {
//                Toast.makeText(this, "Lengkapi semua data!", Toast.LENGTH_SHORT).show()
//                return@setOnClickListener
//            }
//
//            val sharedPref = getSharedPreferences(PREF_NAME, MODE_PRIVATE)
//            val editor = sharedPref.edit()
//            val gson = Gson()
//
//            val existingJson = sharedPref.getString(RIWAYAT_KEY, "[]")
//            val type = object : TypeToken<MutableList<HistoryItem>>() {}.type
//            val list: MutableList<HistoryItem> = gson.fromJson(existingJson, type) ?: mutableListOf()
//
//            list.add(HistoryItem(barang, kategori, tanggal, sumberDana))
//
//            val newJson = gson.toJson(list)
//            editor.putString(RIWAYAT_KEY, newJson)
//            editor.apply()
//
////            val intent = Intent(this, Riwayat::class.java)
////            startActivity(intent)
////            finish()
//            val intent = Intent(this, Perhitungan::class.java)
//            startActivity(intent)
//            finish()
//        }
        submit.setOnClickListener {
            val barang = inbarang.text.toString()
            val kategori = kategoriDropdown.text.toString()
            val sumberDana = sumberDanaDropdown.text.toString()
            val tanggal = tanggalField.text.toString()

            if (barang.isEmpty() || kategori.isEmpty() || sumberDana.isEmpty() || tanggal.isEmpty()) {
                Toast.makeText(this, "Lengkapi semua data!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val sharedPref = getSharedPreferences(PREF_NAME, MODE_PRIVATE)
            val editor = sharedPref.edit()
            val gson = Gson()

            val existingJson = sharedPref.getString(RIWAYAT_KEY, "[]")
            val type = object : TypeToken<MutableList<HistoryItem>>() {}.type
            val list: MutableList<HistoryItem> = gson.fromJson(existingJson, type) ?: mutableListOf()

            list.add(HistoryItem(barang, kategori, tanggal, sumberDana))

            val newJson = gson.toJson(list)
            editor.putString(RIWAYAT_KEY, newJson)
            editor.apply()

            // Kirim data ke Perhitungan
            val intent = Intent(this, Perhitungan::class.java)
            intent.putExtra("barang", barang)
            intent.putExtra("kategori", kategori)
            intent.putExtra("sumberDana", sumberDana)
            intent.putExtra("tanggal", tanggal)
            startActivity(intent)
            finish()
        }

        home.setOnClickListener {
            startActivity(Intent(this, Beranda::class.java))
        }
        input.setOnClickListener {
            startActivity(Intent(this, Form::class.java))
        }
        light.setOnClickListener {
            startActivity(Intent(this, Keuangan::class.java))
        }
        jam.setOnClickListener {
            startActivity(Intent(this, Riwayat::class.java))
        }
        user.setOnClickListener {
            startActivity(Intent(this, Profil::class.java))
        }
    }
}