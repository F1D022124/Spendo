package psti.unram.spendo

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.animation.AnimationUtils
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import psti.unram.spendo.data.AppDatabase
import psti.unram.spendo.data.Riwayat as RiwayatEntity
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class Form : AppCompatActivity() {
    private val USER_PREFS = "UserPrefs"
    private val TAG = "Form"
    private val scope = CoroutineScope(Dispatchers.Main)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_form)

        // Inisialisasi view
        val etItemName = findViewById<EditText>(R.id.etItemName)
        val etPrice = findViewById<EditText>(R.id.etPrice)
        val etExpenses = findViewById<EditText>(R.id.etExpenses)
        val dropdownFunction = findViewById<AutoCompleteTextView>(R.id.dropdownFunction)
        val dropdownFrequency = findViewById<AutoCompleteTextView>(R.id.dropdownFrequency)
        val btnSubmit = findViewById<Button>(R.id.btnSubmit)
        val ivBack = findViewById<ImageView>(R.id.ivBack)
        val ivhome = findViewById<ImageView>(R.id.ivhome)
        val ivinput = findViewById<ImageView>(R.id.ivinput)
        val ivToRiwayat = findViewById<ImageView>(R.id.ivToRiwayat)
        val ivuser = findViewById<ImageView>(R.id.ivuser)

        // Logging untuk memeriksa null view
        if (etItemName == null) Log.e(TAG, "etItemName is null, check activity_form.xml for R.id.etItemName")
        if (etPrice == null) Log.e(TAG, "etPrice is null, check activity_form.xml for R.id.etPrice")
        if (etExpenses == null) Log.e(TAG, "etExpenses is null, check activity_form.xml for R.id.etExpenses")
        if (dropdownFunction == null) Log.e(TAG, "dropdownFunction is null, check activity_form.xml for R.id.dropdownFunction")
        if (dropdownFrequency == null) Log.e(TAG, "dropdownFrequency is null, check activity_form.xml for R.id.dropdownFrequency")
        if (btnSubmit == null) Log.e(TAG, "btnSubmit is null, check activity_form.xml for R.id.btnSubmit")
        if (ivBack == null) Log.e(TAG, "ivBack is null, check activity_form.xml for R.id.ivBack")
        if (ivhome == null) Log.e(TAG, "ivhome is null, check activity_form.xml for R.id.ivhome")
        if (ivinput == null) Log.e(TAG, "ivinput is null, check activity_form.xml for R.id.ivinput")
        if (ivToRiwayat == null) Log.e(TAG, "ivToRiwayat is null, check activity_form.xml for R.id.ivToRiwayat")
        if (ivuser == null) Log.e(TAG, "ivuser is null, check activity_form.xml for R.id.ivuser")

        // Data dropdown
        val fungsiList = listOf("Hiburan", "Produktivitas", "Kebutuhan Sehari-hari")
        val frekuensiList = listOf("Jarang", "Kadang-kadang", "Sering")
        dropdownFunction.setAdapter(ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, fungsiList))
        dropdownFrequency.setAdapter(ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, frekuensiList))
        dropdownFunction.setOnClickListener { dropdownFunction.showDropDown() }
        dropdownFrequency.setOnClickListener { dropdownFrequency.showDropDown() }

        // Add thousands separator to etPrice and etExpenses
        etPrice.addTextChangedListener(ThousandsSeparatorTextWatcher())
        etExpenses.addTextChangedListener(ThousandsSeparatorTextWatcher())

        // Ambil userName dari UserPrefs
        val userPrefs = getSharedPreferences(USER_PREFS, MODE_PRIVATE)
        val userName = userPrefs.getString("user_name", null)?.takeIf { it.isNotBlank() } ?: run {
            Toast.makeText(this, "Pengguna tidak ditemukan, silakan login ulang", Toast.LENGTH_LONG).show()
            startActivity(Intent(this, Login::class.java))
            finish()
            return
        }
        Log.d(TAG, "UserName: $userName")

        // Inisialisasi database dan Perhitungan
        val db = AppDatabase.getDatabase(this)
        val dao = db.appDao()
        val perhitungan = Perhitungan(dao)

        // Tombol Simpan
        btnSubmit.setOnClickListener {
            btnSubmit.startAnimation(AnimationUtils.loadAnimation(this, R.anim.scale_bold))

            val namaBarang = etItemName.text.toString().trim()
            val harga = etPrice.text.toString().trim().replace(",", "").replace(" ", "")
            val pengeluaranTambahan = etExpenses.text.toString().trim().replace(",", "").replace(" ", "")
            val fungsi = dropdownFunction.text.toString().trim()
            val frekuensi = dropdownFrequency.text.toString().trim()

            // Validasi input
            if (namaBarang.isEmpty() || harga.isEmpty() || pengeluaranTambahan.isEmpty() ||
                fungsi.isEmpty() || frekuensi.isEmpty()) {
                Toast.makeText(this, "Lengkapi semua data di Form!", Toast.LENGTH_SHORT).show()
                Log.w(TAG, "Validation failed: One or more fields are empty")
                return@setOnClickListener
            }

            try {
                val hargaDouble = harga.toDouble()
                val pengeluaranTambahanDouble = pengeluaranTambahan.toDouble()

                if (hargaDouble < 0 || pengeluaranTambahanDouble < 0) {
                    Toast.makeText(this, "Nilai tidak boleh negatif!", Toast.LENGTH_SHORT).show()
                    Log.w(TAG, "Validation failed: Negative value detected")
                    return@setOnClickListener
                }

                // Ambil userId dan ProfilKeuangan untuk validasi sisa anggaran
                scope.launch {
                    val user = withContext(Dispatchers.IO) { dao.getUserByUsername(userName) }
                    if (user == null) {
                        Log.e(TAG, "User not found for username: $userName")
                        withContext(Dispatchers.Main) {
                            Toast.makeText(this@Form, "Pengguna tidak ditemukan, silakan login ulang", Toast.LENGTH_LONG).show()
                            startActivity(Intent(this@Form, Login::class.java))
                            finish()
                        }
                        return@launch
                    }

                    // Ambil data ProfilKeuangan untuk validasi sisa anggaran
                    val profil = withContext(Dispatchers.IO) { dao.getProfilKeuanganByUserId(user.id) }
                    if (profil == null) {
                        Log.e(TAG, "ProfilKeuangan for userId ${user.id} not found")
                        withContext(Dispatchers.Main) {
                            Toast.makeText(this@Form, "Profil Keuangan tidak ditemukan, silakan isi terlebih dahulu", Toast.LENGTH_LONG).show()
                            startActivity(Intent(this@Form, ProfilKeuangan::class.java))
                        }
                        return@launch
                    }

                    val gajiDouble = profil.gaji.toDoubleOrNull() ?: 0.0
                    val pengeluaranTetapDouble = profil.pengeluaranTetap.toDoubleOrNull() ?: 0.0
                    val tanggunganInt = profil.tanggungan.toIntOrNull() ?: 0
                    val sisaAnggaran = gajiDouble - pengeluaranTetapDouble

                    // Validasi harga barang <= saldo awal
                    if (hargaDouble > sisaAnggaran) {
                        Log.w(TAG, "Validation failed: Harga barang ($hargaDouble) melebihi saldo awal ($sisaAnggaran)")
                        Toast.makeText(this@Form, "Harga barang (Rp${String.format("%,.0f", hargaDouble)}) melebihi saldo awal (Rp${String.format("%,.0f", sisaAnggaran)}). Tidak dapat disubmit.", Toast.LENGTH_LONG).show()
                        return@launch
                    }

                    // Validasi total pengeluaran < sisa anggaran untuk mencegah sisa saldo negatif
                    val totalExpenses = hargaDouble + pengeluaranTambahanDouble
                    if (totalExpenses >= sisaAnggaran) {
                        Log.w(TAG, "Validation failed: Total expenses ($totalExpenses) exceeds or equals sisa anggaran (saldo awal) ($sisaAnggaran)")
                        Toast.makeText(this@Form, "Total pengeluaran (Rp${String.format("%,.0f", totalExpenses)}) melebihi saldo awal (Rp${String.format("%,.0f", sisaAnggaran)}). Tidak dapat disubmit.", Toast.LENGTH_LONG).show()
                        return@launch
                    }

                    // Hitung SAW
                    val (hasilRekomendasi, skorSAW) = perhitungan.calculateEligibility(
                        userId = user.id,
                        harga = hargaDouble,
                        fungsi = fungsi,
                        frekuensi = frekuensi,
                        pengeluaranTambahan = pengeluaranTambahanDouble
                    )

                    // Simpan ke SharedPreferences dengan kunci spesifik untuk userId
                    val PREF_NAME = "riwayat_pref_${user.id}"
                    val RIWAYAT_KEY = "riwayat_list"
                    val sharedPref = getSharedPreferences(PREF_NAME, MODE_PRIVATE)
                    val gson = Gson()
                    val historyList = withContext(Dispatchers.IO) {
                        val existingJson = sharedPref.getString(RIWAYAT_KEY, "[]")
                        val type = object : TypeToken<MutableList<HistoryItem>>() {}.type
                        gson.fromJson(existingJson, type) ?: mutableListOf<HistoryItem>()
                    }
                    val tanggal = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Calendar.getInstance().time)
                    val historyItem = HistoryItem(
                        namaBarang = namaBarang,
                        harga = hargaDouble,
                        fungsi = fungsi,
                        frekuensi = frekuensi,
                        pengeluaranTambahan = pengeluaranTambahanDouble,
                        tanggal = tanggal,
                        hasilRekomendasi = hasilRekomendasi,
                        gaji = gajiDouble,
                        pengeluaranTetap = pengeluaranTetapDouble,
                        tanggungan = tanggunganInt,
                        skorSAW = skorSAW
                    )
                    historyList.add(historyItem)
                    withContext(Dispatchers.IO) {
                        sharedPref.edit().putString(RIWAYAT_KEY, gson.toJson(historyList)).apply()
                    }

                    // Simpan ke Riwayat di database
                    val riwayat = RiwayatEntity(
                        userId = user.id,
                        namaBarang = namaBarang,
                        harga = hargaDouble,
                        fungsi = fungsi,
                        frekuensi = frekuensi,
                        pengeluaranTambahan = pengeluaranTambahanDouble,
                        tanggal = tanggal,
                        hasilRekomendasi = hasilRekomendasi,
                        gaji = gajiDouble,
                        pengeluaranTetap = pengeluaranTetapDouble,
                        tanggungan = tanggunganInt,
                        skorSAW = skorSAW
                    )
                    withContext(Dispatchers.IO) {
                        dao.insertRiwayat(riwayat)
                    }

                    // Pindah ke halaman rekomendasi sesuai hasil
                    val intent = when (hasilRekomendasi) {
                        "Direkomendasikan" -> Intent(this@Form, Rekomendasi::class.java)
                        "Tunda" -> Intent(this@Form, Rekomendasi2::class.java)
                        "Tidak Direkomendasikan" -> Intent(this@Form, Rekomendasi3::class.java)
                        else -> Intent(this@Form, Rekomendasi::class.java) // Fallback
                    }
                    intent.putExtra("HISTORY_ITEM", gson.toJson(historyItem))
                    startActivity(intent)
                    finish()
                }
            } catch (e: NumberFormatException) {
                Log.e(TAG, "Number Format Error: ${e.message}", e)
                Toast.makeText(this, "Gunakan angka tanpa tanda baca (contoh: 1000000)", Toast.LENGTH_LONG).show()
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
            etItemName.text.clear()
            etPrice.text.clear()
            etExpenses.text.clear()
            dropdownFunction.text.clear()
            dropdownFrequency.text.clear()
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
            try {
                startActivity(Intent(this, Profil::class.java))
                finish()
            } catch (e: Exception) {
                Log.e(TAG, "Navigation Error to Profil: ${e.message}", e)
                Toast.makeText(this, "Gagal ke Profil", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // TextWatcher for thousands separator
    private inner class ThousandsSeparatorTextWatcher : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

        override fun afterTextChanged(s: Editable?) {
            s?.let { editable ->
                val editText = when {
                    editable === findViewById<EditText>(R.id.etPrice).text -> findViewById<EditText>(R.id.etPrice)
                    editable === findViewById<EditText>(R.id.etExpenses).text -> findViewById<EditText>(R.id.etExpenses)
                    else -> return
                }
                editText.removeTextChangedListener(this)
                val original = editable.toString().replace(",", "")
                if (original.isNotEmpty()) {
                    try {
                        val number = original.toLong()
                        val formatted = String.format("%,d", number)
                        editText.setText(formatted)
                        editText.setSelection(formatted.length)
                    } catch (e: NumberFormatException) {
                        // Ignore invalid input
                    }
                }
                editText.addTextChangedListener(this)
            }
        }
    }
}