package it.group9.JoubHunting

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CreateNote : AppCompatActivity() {

    private lateinit var db: AppDatabase
    private var companyId: Int = -1 // 前の画面から受け取る企業ID

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_create_note)

        // アクションバーの設定
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "メモの作成"

        // DBの初期化
        db = AppDatabase.getDatabase(this)

        // ▼ 前の画面から企業IDを受け取る
        companyId = intent.getIntExtra("EXTRA_COMPANY_ID", -1)
        if (companyId == -1) {
            Toast.makeText(this, "エラー: 企業情報が取得できません", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Viewの取得
        val editTitle = findViewById<EditText>(R.id.editNoteTitle)
        val editContent = findViewById<EditText>(R.id.editNoteContent)
        val btnCreate = findViewById<Button>(R.id.btnCreate)

        // ▼ 作成ボタンが押されたときの処理
        btnCreate.setOnClickListener {
            val title = editTitle.text.toString()
            val content = editContent.text.toString()

            // 入力チェック（空欄なら保存しない）
            if (title.isBlank() || content.isBlank()) {
                Toast.makeText(this, "タイトルと内容を入力してください", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            saveMemoToDb(title, content)
        }

        // システムバーの調整
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    // データベースへの保存処理
    private fun saveMemoToDb(title: String, content: String) {
        // 今日の日付を取得 (例: 2026/01/21)
        val currentDate = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault()).format(Date())

        lifecycleScope.launch {
            // 新しいメモオブジェクトを作成
            val newMemo = Memo(
                companyInfoId = companyId, // 受け取った企業IDを紐付ける
                title = title,
                date = currentDate,
                content = content
            )

            // DBに挿入
            db.memoDao().insert(newMemo)

            // 保存完了メッセージ
            Toast.makeText(applicationContext, "メモを作成しました", Toast.LENGTH_SHORT).show()

            // 画面を閉じて一覧に戻る
            finish()
        }
    }

    // 戻るボタンの動作
    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
