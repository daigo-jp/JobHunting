package it.group9.JoubHunting

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar

class CompanyRegister : AppCompatActivity() {

    // DB
    private lateinit var db: AppDatabase

    // UI
    private lateinit var stars: List<ImageView>
    private lateinit var ivFavorite: ImageView

    // データ管理用
    private var currentAspirationLevel: Int = 0 // 志望度 (0~5)
    private var isFavorite: Boolean = false     // お気に入り状態

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_company_register)

        // アクションバーの設定（戻るボタン）
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = "企業登録"
        }

        // DB初期化
        db = AppDatabase.getDatabase(this)

        // Viewの取得
        val editCompanyName = findViewById<EditText>(R.id.editCompanyName)
        val editIndustry = findViewById<EditText>(R.id.editIndustry)
        val editLocation = findViewById<EditText>(R.id.editLocation)
        val editStatus = findViewById<EditText>(R.id.editStatus)
        val editUrl = findViewById<EditText>(R.id.editUrl)
        val editNextDate = findViewById<EditText>(R.id.editNextDate)

        val buttonRegister = findViewById<Button>(R.id.buttonRegister)

        // お気に入りエリア
        val favoriteArea = findViewById<FrameLayout>(R.id.favoriteArea)
        ivFavorite = findViewById<ImageView>(R.id.starFavorite)

        // 志望度の星
        stars = listOf(
            findViewById(R.id.star1),
            findViewById(R.id.star2),
            findViewById(R.id.star3),
            findViewById(R.id.star4),
            findViewById(R.id.star5)
        )

        // --- 1. 志望度（星）クリック時の処理 ---
        stars.forEachIndexed { index, star ->
            star.setOnClickListener {
                currentAspirationLevel = index + 1
                updateStarUI(index)
            }
        }

        // --- 2. お気に入りクリック時の処理 ---
        favoriteArea.setOnClickListener {
            // 反転させる
            isFavorite = !isFavorite
            updateFavoriteUI()
        }

        // --- 3. 日付選択ダイアログ ---
        editNextDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            DatePickerDialog(
                this,
                { _, year, month, dayOfMonth ->
                    val date = "%04d-%02d-%02d".format(year, month + 1, dayOfMonth)
                    editNextDate.setText(date)
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        // --- 4. 登録ボタンクリック時 ---
        buttonRegister.setOnClickListener {
            val companyName = editCompanyName.text.toString().trim()
            val industry = editIndustry.text.toString().trim()
            val location = editLocation.text.toString().trim()
            val status = editStatus.text.toString().trim()
            val nextDate = editNextDate.text.toString().trim()
            val url = editUrl.text.toString().trim()

            // バリデーション：企業名は必須
            if (companyName.isEmpty()) {
                Toast.makeText(this, "企業名を入力してください", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                // SharedPreferencesからログイン中のユーザーIDを取得
                val sharedPref = getSharedPreferences("AppPrefs", MODE_PRIVATE)
                val userId = sharedPref.getLong("userId", -1L)

                if (userId == -1L) {
                    Toast.makeText(this@CompanyRegister, "ユーザーIDの取得に失敗しました", Toast.LENGTH_SHORT).show()
                    return@launch
                }

                // 新規登録データの作成
                // ID(companyId)は自動生成なので指定しない（0などでOK）
                val newCompany = CompanyInfo(
                    userId = userId,
                    companyName = companyName,
                    industry = industry,
                    location = location,
                    selectionStatus = if (status.isBlank()) null else status,
                    aspirationLevel = currentAspirationLevel,
                    isFavorite = isFavorite, // お気に入り状態も保存
                    nextScheduledDate = if (nextDate.isBlank()) null else nextDate,
                    companyUrl = if (url.isBlank()) null else url
                )

                // DBに挿入 (IOスレッド)
                withContext(Dispatchers.IO) {
                    db.companyInfoDao().insert(newCompany)
                }

                Toast.makeText(this@CompanyRegister, "登録しました", Toast.LENGTH_SHORT).show()
                finish() // 画面を閉じて一覧に戻る
            }
        }
    }

    /** 志望度スターの見た目更新 */
    private fun updateStarUI(selectedIndex: Int) {
        stars.forEachIndexed { index, imageView ->
            if (index <= selectedIndex) {
                imageView.setImageResource(android.R.drawable.btn_star_big_on) // 光っている星
            } else {
                imageView.setImageResource(android.R.drawable.btn_star_big_off) // 消えている星
            }
        }
    }

    /** お気に入りスターの見た目更新 */
    private fun updateFavoriteUI() {
        if (isFavorite) {
            ivFavorite.setImageResource(android.R.drawable.btn_star_big_on) // 光っている星
        } else {
            ivFavorite.setImageResource(android.R.drawable.btn_star_big_off) // 消えている星
        }
    }

    // 戻るボタン
    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}