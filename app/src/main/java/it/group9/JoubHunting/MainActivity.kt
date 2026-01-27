package it.group9.JoubHunting

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
//todo 志望度機能、メモ画面遷移、
class MainActivity : AppCompatActivity() {

    private lateinit var db: AppDatabase
    private lateinit var adapter: CompanyAdapter

    // 初期値を無効な値(-1)にしておく
    private var userId: Long = -1L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ログイン状態とユーザーIDの確認
        val sharedPref = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
        val isLoggedIn = sharedPref.getBoolean("isLoggedIn", false)
        userId = sharedPref.getLong("userId", -1L)

        // 未ログインまたはID取得失敗時はログイン画面へ
        if (!isLoggedIn || userId == -1L) {
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
            finish()
            return
        }

        setContentView(R.layout.activity_main)

        // ユーザーアイコン設定
        val userIcon = findViewById<ImageView>(R.id.ivUserProfileIcon)
        userIcon.setOnClickListener {
            val intent = Intent(this, UserProfile::class.java)
            intent.putExtra("EXTRA_USER_ID", userId)
            startActivity(intent)
        }

        // データベースのインスタンス取得
        db = AppDatabase.getDatabase(this)

        // RecyclerViewの設定
        val recyclerView = findViewById<RecyclerView>(R.id.rvCompanyList)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Adapterの初期化
        adapter = CompanyAdapter(
            companyList = emptyList(),
            onEditClick = { company ->
                // ★コンフリクト解消箇所1: 編集画面への遷移コード(HEAD)を採用
                val intent = Intent(this, CompanyEdit::class.java)
                // 編集に必要な情報を渡す（最低限ID）
                intent.putExtra("EXTRA_COMPANY_ID", company.companyId)
                startActivity(intent)
            },
            onMemoClick = { company ->
                // メモ画面への遷移
                val intent = Intent(this, MemoListActivity::class.java)
                intent.putExtra("EXTRA_COMPANY_ID", company.companyId)
                intent.putExtra("EXTRA_COMPANY_NAME", company.companyName)
                startActivity(intent)
            },
            // ▼▼▼ 追加: お気に入りボタンが押された時の処理 ▼▼▼
            onFavoriteClick = { company ->
                toggleFavorite(company)
            }
        )
        recyclerView.adapter = adapter

        setupSearch()

        findViewById<Button>(R.id.btnGoRegister).setOnClickListener {
            // 企業登録画面へ遷移
            val intent = Intent(this, CompanyRegister::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        loadCompanyData()
    }

    // データベースから企業一覧を取得
    private fun loadCompanyData() {
        lifecycleScope.launch {
            // ★コンフリクト解消箇所2: 安全なスレッド処理(HEAD)を採用
            // ※DAOの中身が「お気に入り順」になっていれば、この呼び出しで正しくソートされます
            val companies = withContext(Dispatchers.IO) {
                db.companyInfoDao().getCompaniesByUserId(userId)
            }

            android.util.Log.d("CHECK_DATA", "ログイン中ID: $userId, 取得件数: ${companies.size}")
            adapter.updateData(companies)
        }
    }

    // ▼▼▼ 追加: お気に入りの切り替え処理 ▼▼▼
    private fun toggleFavorite(company: CompanyInfo) {
        lifecycleScope.launch {
            // 現在の状態を反転 (true⇔false) させた新しいデータを作成
            val updatedCompany = company.copy(isFavorite = !company.isFavorite)

            // データベースを更新
            db.companyInfoDao().update(updatedCompany)

            // リストを再読み込み（並び順を反映させるため）
            loadCompanyData()
        }
    }

    private fun setupSearch() {
        val searchEditText = findViewById<EditText>(R.id.etCompanySearch)
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                adapter.filter(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }
}