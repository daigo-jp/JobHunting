package it.group9.JoubHunting

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
import android.content.Intent

class MemoListActivity : AppCompatActivity() {

    private lateinit var db: AppDatabase
    private lateinit var adapter: MemoAdapter

    // 現在選択中の企業IDと、編集中のメモ（新規の場合はnull）
    private var targetCompanyId: Int = -1
    private var currentEditingMemo: Memo? = null

    // Viewの参照
    private lateinit var etContent: EditText
    private lateinit var tvSelectedTitle: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_memo_list)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // 1. 前の画面から企業IDと名前を受け取る
        targetCompanyId = intent.getIntExtra("EXTRA_COMPANY_ID", -1)
        val companyName = intent.getStringExtra("EXTRA_COMPANY_NAME") ?: "企業"

        if (targetCompanyId == -1) {
            Toast.makeText(this, "エラー: 企業情報が取得できませんでした", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // タイトル設定
        findViewById<TextView>(R.id.tvTitle).text = "${companyName} メモ"

        // 2. DB初期化
        db = AppDatabase.getDatabase(this)

        // 3. Viewの取得
        etContent = findViewById(R.id.etMemoContent)
        tvSelectedTitle = findViewById(R.id.tvSelectedTitle)
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewMemos)

        // 4. アダプターの設定
        adapter = MemoAdapter(emptyList()) { clickedMemo ->
            // リストタップ時の処理：簡易編集モード（既存機能）
            currentEditingMemo = clickedMemo
            tvSelectedTitle.text = "${clickedMemo.title} (${clickedMemo.date})"
            etContent.setText(clickedMemo.content)
        }

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        // 5. ボタン設定
        // 簡易保存ボタン（この画面で編集した場合用）
        findViewById<AppCompatButton>(R.id.btnSave).setOnClickListener {
            saveMemo()
        }

        // 消去（削除）ボタン
        findViewById<AppCompatButton>(R.id.btnDelete).setOnClickListener {
            deleteMemo()
        }

        // ★修正ポイント：新規作成ボタン（画面遷移）
        findViewById<TextView>(R.id.btnCreateNew).setOnClickListener {
            val intent = Intent(this, CreateNote::class.java)
            // 企業IDというバトンを渡す
            intent.putExtra("EXTRA_COMPANY_ID", targetCompanyId)
            startActivity(intent)
        }

        // ★注意: onCreate内での loadMemos() は削除しました
    }

    // ★修正ポイント：画面に戻ってきたときにリストを更新する仕組み
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
            // DBからこの企業のメモだけを取得してリストを更新
            val memoList = db.memoDao().getMemosByCompanyId(targetCompanyId)
            adapter.updateData(memoList)
        }
    }

    // --- 以下、既存の簡易編集機能（この画面内で編集する場合に使用） ---

    private fun saveMemo() {
        val content = etContent.text.toString()
        if (content.isBlank()) {
            Toast.makeText(this, "内容を入力してください", Toast.LENGTH_SHORT).show()
            return
        }

        val currentDate = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault()).format(Date())
        val title = if (content.length > 10) content.take(10) + "..." else content

        lifecycleScope.launch {
            if (currentEditingMemo == null) {
                // この画面で直接新規作成する場合（予備機能）
                val newMemo = Memo(
                    companyInfoId = targetCompanyId,
                    title = title,
                    date = currentDate,
                    content = content
                )
                db.memoDao().insert(newMemo)
            } else {
                // 既存メモの更新
                val updateMemo = currentEditingMemo!!.copy(
                    title = title,
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
        tvSelectedTitle.text = "新規メモ"
        etContent.setText("")
    }
}
