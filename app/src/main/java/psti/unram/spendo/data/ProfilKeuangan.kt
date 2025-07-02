package psti.unram.spendo.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "profil_keuangan",
    foreignKeys = [ForeignKey(
        entity = User::class,
        parentColumns = ["id"],
        childColumns = ["userId"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class ProfilKeuangan(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: Int,
    val gaji: String,
    val pengeluaranTetap: String,
    val tanggungan: String
)