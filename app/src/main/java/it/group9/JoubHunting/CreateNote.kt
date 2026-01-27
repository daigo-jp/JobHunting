package it.group9.JoubHunting

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView // ★追加
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers // ★追加
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext // ★追加
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CreateNote : AppCompatActivity() {

    private lateinit var db: AppDatabase
    private var companyId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_create_note)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "メモの作成"

        db = AppDatabase.getDatabase(this)

        companyId = intent.getIntExtra("EXTRA_COMPANY_ID", -1)
        if (companyId == -1) {
            Toast.makeText(this, "エラー: 企業情報が取得できません", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Viewの取得
        // ▼▼▼ 追加: ヘッダーのTextViewを取得 ▼▼▼
        val textTitleHeader = findViewById<TextView>(R.id.textTitleHeader)

        val editTitle = findViewById<EditText>(R.id.editNoteTitle)
        val editContent = findViewById<EditText>(R.id.editNoteContent)
        val btnCreate = findViewById<Button>(R.id.btnCreate)

        // ▼▼▼ 追加: DBから企業名を取得して表示を変更する処理 ▼▼▼
        lifecycleScope.launch {
            // IOスレッドでDB検索を行う
            val company = withContext(Dispatchers.IO) {
                db.companyInfoDao().getCompanyById(companyId)
            }

            // 企業が見つかったらタイトルを書き換える
            if (company != null) {
                // 例: "株式会社〇〇" または "株式会社〇〇 のメモ" など自由に設定可能
                textTitleHeader.text = company.companyName
            }
        }
        // ▲▲▲ 追加ここまで ▲▲▲

        btnCreate.setOnClickListener {
            val title = editTitle.text.toString()
            val content = editContent.text.toString()

            if (title.isBlank() || content.isBlank()) {
                Toast.makeText(this, "タイトルと内容を入力してください", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            saveMemoToDb(title, content)
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun saveMemoToDb(title: String, content: String) {
        val currentDate = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault()).format(Date())

        lifecycleScope.launch {
            val newMemo = Memo(
                companyInfoId = companyId,
                title = title,
                date = currentDate,
                content = content
            )
            db.memoDao().insert(newMemo)
            Toast.makeText(applicationContext, "メモを作成しました", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}