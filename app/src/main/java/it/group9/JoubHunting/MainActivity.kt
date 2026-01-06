package it.group9.JoubHunting

import android.content.Intent // これが必要です
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // --- ここを追加 ---
        // ボタンを見つけてクリック時の処理を書く
        val btnToMemo = findViewById<Button>(R.id.btnGoToMemo)

        btnToMemo.setOnClickListener {
            // インテントを作成して画面遷移（ここから -> MemoListへ）
            val intent = Intent(this, MemoList::class.java)
            startActivity(intent)
        }
    }
}