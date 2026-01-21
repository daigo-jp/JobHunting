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

    // ▼ 変更点1：初期値を無効な値(-1)にしておく（ログイン判定で上書きするため）
    private var userId: Long = -1L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ▼ 変更点2：ログイン状態と同時に「ユーザーID」も取り出す
        val sharedPref = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
        val isLoggedIn = sharedPref.getBoolean("isLoggedIn", false)
        userId = sharedPref.getLong("userId", -1L) // 保存されたIDを取得

        // IDが -1 (取得失敗) の場合もログイン画面に戻すように修正
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
            val intent = Intent(this, UserProfile::class.java) // UserProfileActivityの場合は名前に注意
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
                val intent = Intent(this, CompanyEdit::class.java)

                // 編集に必要な情報を渡す（最低限ID）
                intent.putExtra("EXTRA_COMPANY_ID", company.companyId)

                startActivity(intent)
            },
            onMemoClick = { company ->
                val intent = Intent(this, MemoListActivity::class.java)
                intent.putExtra("EXTRA_COMPANY_ID", company.companyId)
                intent.putExtra("EXTRA_COMPANY_NAME", company.companyName)
                startActivity(intent)
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
            // ▼ 変更点3：自分のIDのデータだけを取得するメソッドに戻す
            // ※ CompanyInfoDao に getCompaniesByUserId がある前提です
            val companies = db.companyInfoDao().getCompaniesByUserId(userId)

            // ログで確認（userIdが正しく取れているかチェックできます）
            android.util.Log.d("CHECK_DATA", "ログイン中ID: $userId, 取得件数: ${companies.size}")

            adapter.updateData(companies)
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
