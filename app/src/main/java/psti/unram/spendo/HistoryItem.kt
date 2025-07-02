package psti.unram.spendo

import com.google.gson.Gson

data class HistoryItem(
    val namaBarang: String,
    val harga: Double,
    val fungsi: String,
    val frekuensi: String,
    val pengeluaranTambahan: Double,
    val tanggal: String,
    val hasilRekomendasi: String,
    val gaji: Double?,
    val pengeluaranTetap: Double?,
    val tanggungan: Int?,
    val skorSAW: Double?
) {
    fun toJson(): String {
        return Gson().toJson(this)
    }
}