package psti.unram.spendo

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import psti.unram.spendo.data.AppDao

class Perhitungan(private val dao: AppDao) {

    suspend fun calculateEligibility(
        userId: Int,
        harga: Double,
        fungsi: String,
        frekuensi: String,
        pengeluaranTambahan: Double
    ): Pair<String, Double> {
        val TAG = "Perhitungan"
        // Ambil data ProfilKeuangan berdasarkan userId
        val profil = withContext(Dispatchers.IO) { dao.getProfilKeuanganByUserId(userId) }
            ?: return Pair("Tidak Direkomendasikan", 0.0).also {
                Log.e(TAG, "ProfilKeuangan for userId $userId not found")
            }

        // Ambil data dari ProfilKeuangan
        val gaji = profil.gaji.toDoubleOrNull() ?: return Pair("Tidak Direkomendasikan", 0.0).also {
            Log.e(TAG, "Invalid gaji format: ${profil.gaji}")
        }
        val pengeluaranTetap = profil.pengeluaranTetap.toDoubleOrNull() ?: return Pair("Tidak Direkomendasikan", 0.0).also {
            Log.e(TAG, "Invalid pengeluaranTetap format: ${profil.pengeluaranTetap}")
        }
        val tanggungan = profil.tanggungan.toIntOrNull() ?: return Pair("Tidak Direkomendasikan", 0.0).also {
            Log.e(TAG, "Invalid tanggungan format: ${profil.tanggungan}")
        }

        // Bobot kriteria
        val weightHarga = 0.4
        val weightFungsi = 0.3
        val weightFrekuensi = 0.2
        val weightKeuangan = 0.1

        // Skor maksimum dan minimum untuk normalisasi
        val maxHarga = 100_000_000.0 // Batas harga maksimum
        val minHarga = 0.0
        val maxFungsi = 3.0 // Kebutuhan Sehari-hari
        val maxFrekuensi = 3.0 // Sering
        val maxKeuangan = gaji // Gaji sebagai batas maksimum
        val minKeuangan = 0.0

        // Hitung skor untuk setiap kriteria
        // Harga (cost, semakin rendah semakin baik)
        val normalizedHarga = if (maxHarga != minHarga) {
            (maxHarga - harga) / (maxHarga - minHarga)
        } else {
            0.0
        }

        // Fungsi (benefit, semakin penting semakin baik)
        val skorFungsi = when (fungsi) {
            "Kebutuhan Sehari-hari" -> 3.0
            "Produktivitas" -> 2.0
            "Hiburan" -> 1.0
            else -> 1.0 // Default untuk input tidak valid
        }
        val normalizedFungsi = skorFungsi / maxFungsi

        // Frekuensi (benefit, semakin sering semakin baik)
        val skorFrekuensi = when (frekuensi) {
            "Sering" -> 3.0
            "Kadang-kadang" -> 2.0
            "Jarang" -> 1.0
            else -> 1.0 // Default untuk input tidak valid
        }
        val normalizedFrekuensi = skorFrekuensi / maxFrekuensi

        // Keuangan (benefit, semakin besar sisa anggaran semakin baik)
        val sisaAnggaran = gaji - pengeluaranTetap - pengeluaranTambahan - (tanggungan * 500_000.0)
        val normalizedKeuangan = if (maxKeuangan != minKeuangan) {
            (sisaAnggaran.coerceAtLeast(0.0)) / maxKeuangan
        } else {
            0.0
        }

        // Hitung skor SAW
        val skorSAW = (normalizedHarga * weightHarga) +
                (normalizedFungsi * weightFungsi) +
                (normalizedFrekuensi * weightFrekuensi) +
                (normalizedKeuangan * weightKeuangan)

        // Tentukan hasil rekomendasi
        val hasilRekomendasi = when {
            skorSAW > 0.8 -> "Direkomendasikan"
            skorSAW >= 0.5 -> "Tunda"
            else -> "Tidak Direkomendasikan"
        }

        // Logging untuk debugging
        Log.d(TAG, "Harga: $harga, Fungsi: $fungsi, Frekuensi: $frekuensi")
        Log.d(TAG, "Gaji: $gaji, Pengeluaran Tetap: $pengeluaranTetap, Pengeluaran Tambahan: $pengeluaranTambahan, Tanggungan: $tanggungan")
        Log.d(TAG, "Skor: Harga=$normalizedHarga, Fungsi=$normalizedFungsi, Frekuensi=$normalizedFrekuensi, Keuangan=$normalizedKeuangan")
        Log.d(TAG, "Skor SAW: $skorSAW, Hasil: $hasilRekomendasi")

        return Pair(hasilRekomendasi, skorSAW)
    }
}