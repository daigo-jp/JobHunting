package it.group9.JoubHunting

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [User::class, CompanyInfo::class, Memo::class], version = 1, exportSchema = false)
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
                    // ▼▼▼ ここを追加：DB作成時にコールバックを呼ぶ ▼▼▼
                    .addCallback(PopulateDbCallback(context))
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }

    // ▼▼▼ ここを追加：初期データを投入するクラス ▼▼▼
    private class PopulateDbCallback(private val context: Context) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            // データベースが作成された直後に実行される
            // コルーチンを使って非同期でデータを挿入
            CoroutineScope(Dispatchers.IO).launch {
                populateDatabase(context)
            }
        }

        suspend fun populateDatabase(context: Context) {
            val database = getDatabase(context)
            val userDao = database.userDao()
            val companyDao = database.companyInfoDao()

            // 1. まずユーザーを作成（企業データと紐づけるため）
            val user = User(
                name = "テストユーザー",
                email = "test@example.com",
                password = "password",
                skill = "Java",
                intention = 1,
                experience = "なし"
            )
            val userId = userDao.insert(user) // 作成したIDを取得

            // 2. 企業データを複数作成（検索テスト用に名前をバラバラに）
            val companies = listOf(
                CompanyInfo(
                    userId = userId,
                    companyName = "株式会社テックイノベーション",
                    industry = "IT",
                    location = "東京都渋谷区",
                    selectionStatus = "書類選考中",
                    aspirationLevel = 5,
                    nextScheduledDate = "2024/04/01",
                    companyUrl = "https://tech-innnov.example.com"
                ),
                CompanyInfo(
                    userId = userId,
                    companyName = "みらい商事",
                    industry = "商社",
                    location = "大阪府大阪市",
                    selectionStatus = "1次面接通過",
                    aspirationLevel = 3,
                    nextScheduledDate = "2024/04/10",
                    companyUrl = "https://mirai-shoji.example.com"
                ),
                CompanyInfo(
                    userId = userId,
                    companyName = "日本システム開発",
                    industry = "SIer",
                    location = "神奈川県横浜市",
                    selectionStatus = "説明会参加済",
                    aspirationLevel = 4,
                    nextScheduledDate = null,
                    companyUrl = null
                ),
                CompanyInfo(
                    userId = userId,
                    companyName = "Global Foods Ltd.",
                    industry = "食品",
                    location = "東京都港区",
                    selectionStatus = "最終面接",
                    aspirationLevel = 5,
                    nextScheduledDate = "2024/03/25",
                    companyUrl = "https://global-foods.example.com"
                ),
                CompanyInfo(
                    userId = userId,
                    companyName = "デザインスタジオ・アーツ",
                    industry = "Web制作",
                    location = "福岡県福岡市",
                    selectionStatus = "不採用",
                    aspirationLevel = 2,
                    nextScheduledDate = null,
                    companyUrl = null
                )
            )

            // まとめて登録
            for (company in companies) {
                companyDao.insert(company)
            }
        }
    }
}
