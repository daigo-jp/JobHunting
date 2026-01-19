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
            // リストタップ時の処理：編集モードにする
            currentEditingMemo = clickedMemo
            tvSelectedTitle.text = "${clickedMemo.title} (${clickedMemo.date})"
            etContent.setText(clickedMemo.content)
        }

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        // 5. ボタン設定
        // 保存ボタン
        findViewById<AppCompatButton>(R.id.btnSave).setOnClickListener {
            saveMemo()
        }

        // 消去（削除）ボタン
        findViewById<AppCompatButton>(R.id.btnDelete).setOnClickListener {
            deleteMemo()
        }

        // 新規作成ボタン
        findViewById<TextView>(R.id.btnCreateNew).setOnClickListener {
            clearInput()
            Toast.makeText(this, "新規作成モード", Toast.LENGTH_SHORT).show()
        }

        // 6. データを読み込む
        loadMemos()
    }
    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    private fun loadMemos() {
        lifecycleScope.launch {
            // DBからこの企業のメモだけを取得
            val memoList = db.memoDao().getMemosByCompanyId(targetCompanyId)
            adapter.updateData(memoList)
        }
    }

    private fun saveMemo() {
        val content = etContent.text.toString()
        if (content.isBlank()) {
            Toast.makeText(this, "内容を入力してください", Toast.LENGTH_SHORT).show()
            return
        }

        // 今日の日付を取得
        val currentDate = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault()).format(Date())
        // タイトルは内容の最初の10文字とする（または適当な名前）
        val title = if (content.length > 10) content.take(10) + "..." else content

        lifecycleScope.launch {
            if (currentEditingMemo == null) {
                // 新規作成
                val newMemo = Memo(
                    companyInfoId = targetCompanyId,
                    title = title,
                    date = currentDate,
                    content = content
                )
                db.memoDao().insert(newMemo)
            } else {
                // 更新（既存のメモを書き換え）
                val updateMemo = currentEditingMemo!!.copy(
                    title = title,
                    date = currentDate, // 更新日を変えるならここ
                    content = content
                )
                db.memoDao().update(updateMemo)
            }

            // 入力をクリアしてリスト再読み込み
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
