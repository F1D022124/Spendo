package psti.unram.spendo.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "riwayat",
    foreignKeys = [ForeignKey(
        entity = User::class,
        parentColumns = ["id"],
        childColumns = ["userId"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class Riwayat(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: Int,
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
)
