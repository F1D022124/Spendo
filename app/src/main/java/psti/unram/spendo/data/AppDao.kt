package psti.unram.spendo.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface AppDao {
    // User
    @Insert
    suspend fun insertUser(user: User)

    @Query("SELECT * FROM users WHERE username = :username LIMIT 1")
    suspend fun getUserByUsername(username: String): User?

    // Profil Keuangan
    @Insert
    suspend fun insertProfilKeuangan(profil: ProfilKeuangan)

    @Update
    suspend fun updateProfilKeuangan(profil: ProfilKeuangan)

    @Query("SELECT * FROM profil_keuangan WHERE userId = :userId LIMIT 1")
    suspend fun getProfilKeuanganByUserId(userId: Int): ProfilKeuangan?

    // Riwayat
    @Insert
    suspend fun insertRiwayat(riwayat: Riwayat): Long

    @Query("SELECT * FROM riwayat WHERE userId = :userId")
    suspend fun getRiwayatByUserId(userId: Int): List<Riwayat>

    @Query("SELECT * FROM riwayat WHERE id = :id LIMIT 1")
    suspend fun getRiwayatById(id: Int): Riwayat?
}