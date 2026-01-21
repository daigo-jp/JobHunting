package it.group9.JoubHunting

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.launch

class CreateAccount : AppCompatActivity() {
    private lateinit var db: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_account)

        // ▼▼▼ 追加: アクションバーに戻るボタンを表示 ▼▼▼
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = "アカウント作成" // 必要ならタイトルも設定
        }
        // ▲▲▲ 追加ここまで ▲▲▲

        db = AppDatabase.getDatabase(this)

        val tilLastName = findViewById<TextInputLayout>(R.id.tilLastName)
        val tilFirstName = findViewById<TextInputLayout>(R.id.tilFirstName)
        val tilEmail = findViewById<TextInputLayout>(R.id.tilEmail)
        val tilPassword = findViewById<TextInputLayout>(R.id.tilPassword)
        val tilConfirm = findViewById<TextInputLayout>(R.id.tilConfirmPassword)
        val btnCreate = findViewById<Button>(R.id.btnCreateAccount)

        btnCreate.setOnClickListener {
            val lastName = tilLastName.editText?.text.toString().trim()
            val firstName = tilFirstName.editText?.text.toString().trim()
            val email = tilEmail.editText?.text.toString().trim()
            val password = tilPassword.editText?.text.toString()
            val confirmPass = tilConfirm.editText?.text.toString()

            if (lastName.isEmpty() || firstName.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "すべての項目を入力してください", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (password != confirmPass) {
                Toast.makeText(this, "パスワードが一致しません", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val fullName = "$lastName $firstName"

            lifecycleScope.launch {
                // 1. ユーザーを登録し、新しいIDを取得
                val newUser = User(
                    email = email,
                    password = password,
                    name = fullName,
                    skill = "",
                    intention = 0,
                    experience = ""
                )
                // insertメソッドがID(Long)を返すようになっている必要があります
                val newUserId = db.userDao().insert(newUser)

                // 2. そのユーザー用の「初期企業データ」を作成
                createInitialDataForUser(newUserId)

                // 3. 確認画面へ遷移
                val intent = Intent(this@CreateAccount, CheckAccount::class.java).apply {
                    putExtra("EXTRA_LAST_NAME", lastName)
                    putExtra("EXTRA_FIRST_NAME", firstName)
                    putExtra("EXTRA_EMAIL", email)
                }
                startActivity(intent)
            }
        }
    }

    // ▼▼▼ 追加: 戻るボタンが押された時の処理 ▼▼▼
    override fun onSupportNavigateUp(): Boolean {
        finish() // 現在の画面を閉じて前の画面に戻る
        return true
    }
    // ▲▲▲ 追加ここまで ▲▲▲

    private suspend fun createInitialDataForUser(userId: Long) {
        val companies = listOf(
            CompanyInfo(
                userId = userId, // ここで新しいユーザーのIDをセット！
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
            db.companyInfoDao().insert(company)
        }
    }
}
