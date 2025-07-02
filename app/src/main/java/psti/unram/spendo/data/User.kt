package psti.unram.spendo.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val username: String,
    val password: String // Simpan password sebagai plain text untuk demo; gunakan hashing di produksi
)