package it.group9.JoubHunting

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar

class CompanyEdit : AppCompatActivity() {

    private var companyId: Int = -1
    private lateinit var stars: List<ImageView>

    // 修正: 'priority' ではなく 'currentAspirationLevel' として管理 (初期値0)
    private var currentAspirationLevel: Int = 0

    // DBインスタンス (onCreate内で重複していたのをメンバ変数に統一)
    private lateinit var db: AppDatabase

    // 保存時に既存のデータを保持するために一時保存する変数
    private var originalCompany: CompanyInfo? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_company_edit)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // DB初期化
        db = AppDatabase.getDatabase(this)

        // --- Intentから企業IDを取得 ---
        companyId = intent.getIntExtra("EXTRA_COMPANY_ID", -1)
        if (companyId == -1) {
            finish()
            return
        }

        // --- View取得 ---
        val editCompanyName = findViewById<EditText>(R.id.editCompanyName)
        val editIndustry = findViewById<EditText>(R.id.editIndustry)
        val editLocation = findViewById<EditText>(R.id.editLocation)
        val editStatus = findViewById<EditText>(R.id.editStatus)
        val editUrl = findViewById<EditText>(R.id.editUrl)
        val editNextDate = findViewById<EditText>(R.id.editNextDate)

        val buttonSave = findViewById<Button>(R.id.buttonSave)
        val buttonDelete = findViewById<Button>(R.id.buttonDelete)
        // val buttonMemo = findViewById<Button>(R.id.buttonMemo) // 使われていないようなのでコメントアウト

        // --- 星アイコンの設定 ---
        stars = listOf(
            findViewById(R.id.star1),
            findViewById(R.id.star2),
            findViewById(R.id.star3),
            findViewById(R.id.star4),
            findViewById(R.id.star5)
        )

        stars.forEachIndexed { index, star ->
            star.setOnClickListener {
                // 星をクリックした時の処理
                // indexは0始まりなので、レベルは +1 する (例: 1つ目の星=レベル1)
                currentAspirationLevel = index + 1

                // UIを更新
                updateStarUI(index)

                // 修正: ここで updatePriority を呼ぶのは削除しました。
                // 理由: 「保存」ボタンを押した時に他の項目と一緒にまとめて保存するためです。
            }
        }

        // --- 日付選択ダイアログ ---
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

        // =========================
        // 企業情報をDBから取得して表示
        // =========================
        lifecycleScope.launch {
            // IOスレッドで取得
            val company = withContext(Dispatchers.IO) {
                db.companyInfoDao().getCompanyById(companyId)
            }

            company?.let {
                originalCompany = it // 更新用に保持

                editCompanyName.setText(it.companyName)
                editIndustry.setText(it.industry)
                editLocation.setText(it.location)
                editStatus.setText(it.selectionStatus ?: "")
                editNextDate.setText(it.nextScheduledDate ?: "")
                editUrl.setText(it.companyUrl ?: "")

                // 修正: DBから取得した aspirationLevel を反映
                currentAspirationLevel = it.aspirationLevel
                // 星の表示を更新 (レベルが3なら、インデックス2までを光らせる)
                updateStarUI(currentAspirationLevel - 1)
            }
        }

        // =========================
        // 保存（更新）
        // =========================
        buttonSave.setOnClickListener {
            val companyName = editCompanyName.text.toString()
            val industry = editIndustry.text.toString()
            val location = editLocation.text.toString()
            val status = editStatus.text.toString()
            val nextDate = editNextDate.text.toString()
            val url = editUrl.text.toString()

            lifecycleScope.launch {
                // 既存のデータがあればそれをベースにする（IDやお気に入りフラグ等を消さないため）
                val baseCompany = originalCompany ?: return@launch

                val updatedCompany = baseCompany.copy(
                    companyName = companyName,
                    industry = industry,
                    location = location,
                    selectionStatus = if (status.isBlank()) null else status,
                    // 修正: 星で選択された currentAspirationLevel をセット
                    aspirationLevel = currentAspirationLevel,
                    nextScheduledDate = if (nextDate.isBlank()) null else nextDate,
                    companyUrl = if (url.isBlank()) null else url
                    // userId や companyId は copy 元の baseCompany のものが維持されます
                )

                withContext(Dispatchers.IO) {
                    db.companyInfoDao().update(updatedCompany)
                }
                finish()
            }
        }

        // =========================
        // 削除
        // =========================
        buttonDelete.setOnClickListener {
            lifecycleScope.launch {
                val company = originalCompany ?: return@launch
                withContext(Dispatchers.IO) {
                    db.companyInfoDao().delete(company)
                }
                finish()
            }
        }
    }

    /**
     * 星の見た目を更新する関数
     * @param selectedIndex 選択された星のインデックス (0~4)。-1の場合はすべてオフ。
     */
    private fun updateStarUI(selectedIndex: Int) {
        stars.forEachIndexed { index, imageView ->
            if (index <= selectedIndex) {
                imageView.setImageResource(R.drawable.ic_star_filled) // 光っている星画像
            } else {
                imageView.setImageResource(R.drawable.ic_star_outline) // 枠だけの星画像
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}