package it.group9.JoubHunting

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import java.util.Calendar

class CompanyEdit : AppCompatActivity() {

    /** 編集対象の企業ID */
    private var companyId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_company_edit)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val editNextDate = findViewById<EditText>(R.id.editNextDate)

        val buttonMemo = findViewById<Button>(R.id.buttonMemo)



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



        // --- View取得 ---
        val editCompanyName = findViewById<EditText>(R.id.editCompanyName)
        val editIndustry = findViewById<EditText>(R.id.editIndustry)
        val editLocation = findViewById<EditText>(R.id.editLocation)
        val editStatus = findViewById<EditText>(R.id.editStatus)
        val editUrl = findViewById<EditText>(R.id.editUrl)

        val buttonSave = findViewById<Button>(R.id.buttonSave)
        val buttonDelete = findViewById<Button>(R.id.buttonDelete)

        // --- DB取得 ---
        val db = AppDatabase.getDatabase(this)

        // --- Intentから企業IDを取得 ---
        companyId = intent.getIntExtra("EXTRA_COMPANY_ID", -1)

        if (companyId == -1) {
            finish()
            return
        }

        // =========================
        // 企業情報をDBから取得して表示
        // =========================
        lifecycleScope.launch {
            val company = db.companyInfoDao().getCompanyById(companyId)

            company?.let {
                editCompanyName.setText(it.companyName)
                editIndustry.setText(it.industry)
                editLocation.setText(it.location)
                editStatus.setText(it.selectionStatus ?: "")
                editNextDate.setText(it.nextScheduledDate ?: "")
                editUrl.setText(it.companyUrl ?: "")
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

                val userId = getSharedPreferences("AppPrefs", MODE_PRIVATE)
                    .getLong("userId", -1L)

                val updatedCompany = CompanyInfo(
                    companyId = companyId,
                    companyName = companyName,
                    industry = industry,
                    location = location,
                    selectionStatus = if (status.isBlank()) null else status,
                    aspirationLevel = 0, // 必要なら取得済みデータを使ってもOK
                    nextScheduledDate = if (nextDate.isBlank()) null else nextDate,
                    companyUrl = if (url.isBlank()) null else url,
                    userId = userId
                )

                db.companyInfoDao().update(updatedCompany)
                finish()
            }
        }

        // =========================
        // 削除
        // =========================
        buttonDelete.setOnClickListener {
            lifecycleScope.launch {
                val company = db.companyInfoDao().getCompanyById(companyId)
                company?.let {
                    db.companyInfoDao().delete(it)
                }
                finish()
            }
        }


    }

    // ← 戻るボタン対応（任意）
    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
