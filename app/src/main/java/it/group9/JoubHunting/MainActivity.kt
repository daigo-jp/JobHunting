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
<<<<<<< HEAD
                val intent = Intent(this, CompanyEdit::class.java)

                // 編集に必要な情報を渡す（最低限ID）
                intent.putExtra("EXTRA_COMPANY_ID", company.companyId)

                startActivity(intent)
=======
                // 編集ボタンの処理（必要に応じて実装）
                Toast.makeText(this, "${company.companyName} を編集", Toast.LENGTH_SHORT).show()
>>>>>>> 82e7e1aacba6ec21620643cc6ffe001edb95a5a8
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
            // 企業登録画面への遷移処理（必要であれば記述）
            // startActivity(Intent(this, RegisterCompanyActivity::class.java))
        }
    }



    override fun onResume() {
        super.onResume()
        loadCompanyData()
    }

    // データベースから企業一覧を取得
    private fun loadCompanyData() {
        lifecycleScope.launch {
<<<<<<< HEAD
            // ▼ 変更点3：自分のIDのデータだけを取得するメソッドに戻す
            // ※ CompanyInfoDao に getCompaniesByUserId がある前提です
           // val companies = db.companyInfoDao().getCompaniesByUserId(userId)
            val companies = withContext(Dispatchers.IO) {
                db.companyInfoDao().getCompaniesByUserId(userId)
            }
=======
            // DAOで「お気に入り順 > 志望度順」にソートされたデータを取得
            val companies = db.companyInfoDao().getCompaniesByUserId(userId)
>>>>>>> 82e7e1aacba6ec21620643cc6ffe001edb95a5a8

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
<<<<<<< HEAD


=======
>>>>>>> 82e7e1aacba6ec21620643cc6ffe001edb95a5a8
}
