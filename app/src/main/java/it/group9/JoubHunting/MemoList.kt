package it.group9.JoubHunting

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MemoListActivity : AppCompatActivity() {

    private lateinit var db: AppDatabase
    private lateinit var adapter: MemoAdapter

    private var targetCompanyId: Int = -1
    private var currentEditingMemo: Memo? = null

    // Viewの参照
    private lateinit var etContent: EditText
    // ★修正: TextView ではなく EditText に変更
    private lateinit var etTitle: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_memo_list)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        targetCompanyId = intent.getIntExtra("EXTRA_COMPANY_ID", -1)
        val companyName = intent.getStringExtra("EXTRA_COMPANY_NAME") ?: "企業"

        if (targetCompanyId == -1) {
            Toast.makeText(this, "エラー: 企業情報が取得できませんでした", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        findViewById<TextView>(R.id.tvTitle).text = "${companyName} メモ"

        db = AppDatabase.getDatabase(this)

        // Viewの取得
        etContent = findViewById(R.id.etMemoContent)
        // ★修正: XMLで変更した EditText のIDを取得
        etTitle = findViewById(R.id.etMemoTitle)
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewMemos)

        adapter = MemoAdapter(emptyList()) { clickedMemo ->
            // リストタップ時の処理
            currentEditingMemo = clickedMemo

            // ★修正: 編集モードなので、タイトル欄に既存のタイトルをセット（日付は付けない）
            etTitle.setText(clickedMemo.title)
            etContent.setText(clickedMemo.content)
        }

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        findViewById<AppCompatButton>(R.id.btnSave).setOnClickListener {
            saveMemo()
        }

        findViewById<AppCompatButton>(R.id.btnDelete).setOnClickListener {
            deleteMemo()
        }

        findViewById<TextView>(R.id.btnCreateNew).setOnClickListener {
            val intent = Intent(this, CreateNote::class.java)
            intent.putExtra("EXTRA_COMPANY_ID", targetCompanyId)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        loadMemos()
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    private fun loadMemos() {
        lifecycleScope.launch {
            val memoList = db.memoDao().getMemosByCompanyId(targetCompanyId)
            adapter.updateData(memoList)
        }
    }

    private fun saveMemo() {
        val content = etContent.text.toString()
        // ★修正: ユーザーが入力したタイトルを取得
        val inputTitle = etTitle.text.toString()

        if (content.isBlank()) {
            Toast.makeText(this, "内容を入力してください", Toast.LENGTH_SHORT).show()
            return
        }

        // ★修正: タイトル決定ロジック
        // ユーザーがタイトルを入力していればそれを使い、空欄の場合のみ本文から自動生成する
        val title = if (inputTitle.isNotBlank()) {
            inputTitle
        } else {
            if (content.length > 10) content.take(10) + "..." else content
        }

        val currentDate = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault()).format(Date())

        lifecycleScope.launch {
            if (currentEditingMemo == null) {
                // 新規作成（この画面で行う場合）
                val newMemo = Memo(
                    companyInfoId = targetCompanyId,
                    title = title,
                    date = currentDate,
                    content = content
                )
                db.memoDao().insert(newMemo)
            } else {
                // 更新
                val updateMemo = currentEditingMemo!!.copy(
                    title = title, // ★修正: ここで固定の自動生成ロジックを使わず、上で決定した title を使う
                    date = currentDate,
                    content = content
                )
                db.memoDao().update(updateMemo)
            }

            clearInput()
            loadMemos()
            Toast.makeText(applicationContext, "保存しました", Toast.LENGTH_SHORT).show()
        }
    }

    private fun deleteMemo() {
        val target = currentEditingMemo
        if (target == null) return

        lifecycleScope.launch {
            db.memoDao().delete(target)
            clearInput()
            loadMemos()
            Toast.makeText(applicationContext, "削除しました", Toast.LENGTH_SHORT).show()
        }
    }

    private fun clearInput() {
        currentEditingMemo = null
        // ★修正: テキストをクリア
        etTitle.setText("")
        etContent.setText("")
    }
}