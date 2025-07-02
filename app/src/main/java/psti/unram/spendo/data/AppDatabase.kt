package psti.unram.spendo.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [User::class, ProfilKeuangan::class, Riwayat::class], version = 2, exportSchema = true)
abstract class AppDatabase : RoomDatabase() {
    abstract fun appDao(): AppDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE riwayat ADD COLUMN fungsi TEXT NOT NULL DEFAULT ''")
                database.execSQL("ALTER TABLE riwayat ADD COLUMN frekuensi TEXT NOT NULL DEFAULT ''")
                database.execSQL("ALTER TABLE riwayat ADD COLUMN pengeluaranTambahan REAL NOT NULL DEFAULT 0.0")
                database.execSQL("ALTER TABLE riwayat ADD COLUMN gaji REAL")
                database.execSQL("ALTER TABLE riwayat ADD COLUMN pengeluaranTetap REAL")
                database.execSQL("ALTER TABLE riwayat ADD COLUMN tanggungan INTEGER")
                database.execSQL("ALTER TABLE riwayat ADD COLUMN skorSAW REAL")
            }
        }

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "spendo_database"
                ).addMigrations(MIGRATION_1_2).build()
                INSTANCE = instance
                instance
            }
        }
    }
}