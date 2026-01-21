package it.group9.JoubHunting

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class CheckAccount : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_check_account) // レイアウトファイル名に合わせてください

        // Intentからデータを受け取る
        val lastName = intent.getStringExtra("EXTRA_LAST_NAME") ?: ""
        val firstName = intent.getStringExtra("EXTRA_FIRST_NAME") ?: ""
        val email = intent.getStringExtra("EXTRA_EMAIL") ?: ""

        // Viewにセット
        findViewById<TextView>(R.id.tvLastName).text = lastName
        findViewById<TextView>(R.id.tvFirstName).text = firstName
        findViewById<TextView>(R.id.tvEmail).text = email

        // 確認ボタン（ログイン画面へ戻る）
        findViewById<Button>(R.id.btnVerify).setOnClickListener {
            val intent = Intent(this, Login::class.java)
            // 履歴をクリアしてログイン画面を先頭にする
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
            finish()
        }

        // 左上の戻るボタン
        findViewById<ImageButton>(R.id.btnBack).setOnClickListener {
            finish()
        }
    }
}