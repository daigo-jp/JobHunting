package it.group9.JoubHunting

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

// バージョンは変更せずそのまま（もしエラーが出るなら version = 3 に上げて fallbackToDestructiveMigration を追加）
@Database(entities = [User::class, CompanyInfo::class, Memo::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun companyInfoDao(): CompanyInfoDao
    abstract fun memoDao(): MemoDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                )
                    .fallbackToDestructiveMigration() // バージョン変更時のクラッシュ防止
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}