package it.group9.JoubHunting

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar

class CompanyEdit : AppCompatActivity() {

    private var companyId: Int = -1
    private lateinit var db: AppDatabase

    // --- UI部品 ---
    private lateinit var stars: List<ImageView>
    private lateinit var ivFavorite: ImageView

    // --- データ保持用 ---
    private var currentAspirationLevel: Int = 0 // 志望度
    private var isFavorite: Boolean = false     // お気に入り状態 ★追加

    // 更新時に元のデータを消さないよう保持しておく変数
    private var originalCompany: CompanyInfo? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_company_edit)

        // アクションバーの戻るボタン有効化
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "企業編集"

        db = AppDatabase.getDatabase(this)

        // IntentからID取得
        companyId = intent.getIntExtra("EXTRA_COMPANY_ID", -1)
        if (companyId == -1) {
            Toast.makeText(this, "企業ID取得エラー", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // --- Viewの取得 ---
        val editCompanyName = findViewById<EditText>(R.id.editCompanyName)
        val editIndustry = findViewById<EditText>(R.id.editIndustry)
        val editLocation = findViewById<EditText>(R.id.editLocation)
        val editStatus = findViewById<EditText>(R.id.editStatus)
        val editNextDate = findViewById<EditText>(R.id.editNextDate)
        val editUrl = findViewById<EditText>(R.id.editUrl)

        val buttonSave = findViewById<Button>(R.id.buttonSave)
        val buttonDelete = findViewById<Button>(R.id.buttonDelete)
        val buttonMemo = findViewById<Button>(R.id.buttonMemo)

        // お気に入りUI
        val favoriteArea = findViewById<FrameLayout>(R.id.favoriteArea)
        ivFavorite = findViewById<ImageView>(R.id.starFavorite)

        // 志望度スターUI
        stars = listOf(
            findViewById(R.id.star1),
            findViewById(R.id.star2),
            findViewById(R.id.star3),
            findViewById(R.id.star4),
            findViewById(R.id.star5)
        )

        // --- 1. 志望度スター クリック処理 ---
        stars.forEachIndexed { index, star ->
            star.setOnClickListener {
                currentAspirationLevel = index + 1
                updateStarUI(currentAspirationLevel - 1)
            }
        }

        // --- 2. お気に入り クリック処理 ★追加 ---
        favoriteArea.setOnClickListener {
            isFavorite = !isFavorite // 状態を反転
            updateFavoriteUI()       // 見た目を更新
        }

        // --- 3. 日付選択 ---
        editNextDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            DatePickerDialog(
                this,
                { _, year, month, dayOfMonth ->
                    val date = "%04d-%02d-%02d".format(year, month + 1, dayOfMonth)
                    editNextDate.setText(date)
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        // --- 4. メモボタン（XMLにあるので実装） ---
        buttonMemo.setOnClickListener {
            // メモ画面へ遷移（IDと名前を渡す）
            val intent = Intent(this, MemoListActivity::class.java)
            intent.putExtra("EXTRA_COMPANY_ID", companyId)
            intent.putExtra("EXTRA_COMPANY_NAME", editCompanyName.text.toString())
            startActivity(intent)
        }

        // --- 5. データの読み込み ---
        lifecycleScope.launch {
            val company = withContext(Dispatchers.IO) {
                db.companyInfoDao().getCompanyById(companyId)
            }

            company?.let {
                originalCompany = it // オリジナルを保持

                // テキスト項目のセット
                editCompanyName.setText(it.companyName)
                editIndustry.setText(it.industry)
                editLocation.setText(it.location)
                editStatus.setText(it.selectionStatus ?: "")
                editNextDate.setText(it.nextScheduledDate ?: "")
                editUrl.setText(it.companyUrl ?: "")

                // 志望度の復元
                currentAspirationLevel = it.aspirationLevel
                updateStarUI(currentAspirationLevel - 1)

                // お気に入りの復元 ★追加
                isFavorite = it.isFavorite
                updateFavoriteUI()
            }
        }

        // --- 6. 保存（更新）ボタン ---
        buttonSave.setOnClickListener {
            // ベースとなるデータがない場合は処理中断
            val baseCompany = originalCompany ?: return@setOnClickListener

            val companyName = editCompanyName.text.toString().trim()
            if (companyName.isEmpty()) {
                Toast.makeText(this, "企業名を入力してください", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 更新データの作成
            val updatedCompany = baseCompany.copy(
                companyName = companyName,
                industry = editIndustry.text.toString().trim(),
                location = editLocation.text.toString().trim(),
                selectionStatus = editStatus.text.toString().trim().ifBlank { null },
                nextScheduledDate = editNextDate.text.toString().trim().ifBlank { null },
                companyUrl = editUrl.text.toString().trim().ifBlank { null },

                // 変更されたステータスをセット
                aspirationLevel = currentAspirationLevel,
                isFavorite = isFavorite // ★追加
            )

            lifecycleScope.launch {
                withContext(Dispatchers.IO) {
                    db.companyInfoDao().update(updatedCompany)
                }
                Toast.makeText(this@CompanyEdit, "更新しました", Toast.LENGTH_SHORT).show()
                finish()
            }
        }

        // --- 7. 削除ボタン ---
        buttonDelete.setOnClickListener {
            lifecycleScope.launch {
                originalCompany?.let {
                    withContext(Dispatchers.IO) {
                        db.companyInfoDao().delete(it)
                    }
                }
                Toast.makeText(this@CompanyEdit, "削除しました", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    /** 志望度スターの見た目更新 */
    private fun updateStarUI(selectedIndex: Int) {
        stars.forEachIndexed { index, imageView ->
            if (index <= selectedIndex) {
                imageView.setImageResource(android.R.drawable.btn_star_big_on)
            } else {
                imageView.setImageResource(android.R.drawable.btn_star_big_off)
            }
        }
    }

    /** お気に入りスターの見た目更新 ★追加 */
    private fun updateFavoriteUI() {
        if (isFavorite) {
            ivFavorite.setImageResource(android.R.drawable.btn_star_big_on) // ON(黄色)
        } else {
            ivFavorite.setImageResource(android.R.drawable.btn_star_big_off) // OFF(灰色)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}