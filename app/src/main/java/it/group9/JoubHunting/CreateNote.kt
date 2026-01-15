package it.group9.JoubHunting

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class CreateNote : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_create_note)

        // --- 追加：標準のアクションバーに「戻る」を表示する ---
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "メモの作成" // バーのタイトルを設定

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    // --- 追加：アクションバーの「←」が押された時の動作 ---
    override fun onSupportNavigateUp(): Boolean {
        // 現在の画面を閉じて前の画面（MemoListActivity）に戻る
        finish()
        return true
    }
}