package it.group9.JoubHunting

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch

// ▼ クラス名を MainActivity に変更
class MainActivity : AppCompatActivity() {

    private lateinit var db: AppDatabase
    private lateinit var adapter: CompanyAdapter
    private var userId: Long = 1L // ※本来はログイン画面から受け取ったIDを使います

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // ▼ レイアウトファイル名を activity_main に変更
        // (XMLファイルの名前も activity_main.xml になっているか確認してください)
        setContentView(R.layout.activity_main)

        // データベースのインスタンス取得
        db = AppDatabase.getDatabase(this)

        // RecyclerViewの設定
        val recyclerView = findViewById<RecyclerView>(R.id.rvCompanyList)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Adapterの初期化
        adapter = CompanyAdapter(
            companyList = emptyList(), // 最初は空
            onEditClick = { company ->
                // 編集ボタンが押された時の処理（画面遷移など）
                Toast.makeText(this, "${company.companyName} を編集", Toast.LENGTH_SHORT).show()
            },
            onMemoClick = { company ->
                // メモボタンが押された時の処理
                Toast.makeText(this, "メモ画面へ", Toast.LENGTH_SHORT).show()
            }
        )
        recyclerView.adapter = adapter

        // 検索機能の設定
        setupSearch()

        // 登録ボタンの設定
        findViewById<Button>(R.id.btnGoRegister).setOnClickListener {
            // 登録画面への遷移処理をここに書く
            // val intent = Intent(this, RegisterActivity::class.java)
            // startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        // 画面が表示されるたびにデータを再取得（登録画面から戻ってきた時など）
        loadCompanyData()
    }

    // データベースから企業一覧を取得
    private fun loadCompanyData() {
        lifecycleScope.launch {
            // 【変更】ID指定をやめて、全件取得してみる
            // val companies = db.companyInfoDao().getCompaniesByUserId(userId)
            val companies = db.companyInfoDao().getAllCompanies()

            // 【追加】ログを出力して、データが何件取れたか確認する
            // Logcatタブで "CHECK_DATA" と検索すると見れます
            android.util.Log.d("CHECK_DATA", "取得した件数: ${companies.size}")
            companies.forEach {
                android.util.Log.d("CHECK_DATA", "企業名: ${it.companyName}")
            }

            // Adapterにデータを渡して更新
            adapter.updateData(companies)
        }
    }

    // 検索バーの文字入力を監視する設定
    private fun setupSearch() {
        val searchEditText = findViewById<EditText>(R.id.etCompanySearch)

        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // 文字が入力されるたびにフィルターを実行
                adapter.filter(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }
}
