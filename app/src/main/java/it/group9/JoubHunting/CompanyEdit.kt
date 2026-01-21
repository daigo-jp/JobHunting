package it.group9.JoubHunting

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

class CompanyEdit : AppCompatActivity() {

    /** 編集対象の企業ID */
    private var companyId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_company_edit)

        // --- View取得 ---
        val editCompanyName = findViewById<EditText>(R.id.editCompanyName)
        val editIndustry = findViewById<EditText>(R.id.editIndustry)
        val editLocation = findViewById<EditText>(R.id.editLocation)
        val editStatus = findViewById<EditText>(R.id.editStatus)
        val editNextDate = findViewById<EditText>(R.id.editNextDate)
        val editUrl = findViewById<EditText>(R.id.editUrl)

        val buttonSave = findViewById<Button>(R.id.buttonSave)
        val buttonDelete = findViewById<Button>(R.id.buttonDelete)

        // --- Intentから企業IDを受け取る ---
        companyId = intent.getIntExtra("COMPANY_ID", -1)

        /*
         ======================================
         ★ DB参照処理（ここだけ変更すればOK）
         ======================================
         ・DBクラス名
         ・取得メソッド名
         ・戻り値の型
         が変わったらこのブロックのみ修正
         */

        if (companyId != -1) {
            // val db = CompanyDatabase(this)
            // val company = db.getCompanyById(companyId)

            // ---- 取得したデータを画面に反映 ----
            // editCompanyName.setText(company.name)
            // editIndustry.setText(company.industry)
            // editLocation.setText(company.location)
            // editStatus.setText(company.status)
            // editNextDate.setText(company.nextDate)
            // editUrl.setText(company.url)
        }

        // --- 保存（上書き） ---
        buttonSave.setOnClickListener {

            val companyName = editCompanyName.text.toString()
            val industry = editIndustry.text.toString()
            val location = editLocation.text.toString()
            val status = editStatus.text.toString()
            val nextDate = editNextDate.text.toString()
            val url = editUrl.text.toString()

            /*
             ======================================
             ★ DB更新処理（ここだけ変更すればOK）
             ======================================
             ・update メソッド名
             ・引数
             が変わったらこのブロックのみ修正
             */

            if (companyId != -1) {
                // val db = CompanyDatabase(this)
                // db.updateCompany(
                //     companyId,
                //     companyName,
                //     industry,
                //     location,
                //     status,
                //     nextDate,
                //     url
                // )
            }

            // --- 一覧画面へ戻る ---
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        // --- 削除（任意・未実装でもOK） ---
        buttonDelete.setOnClickListener {
            finish()
        }
    }
}
