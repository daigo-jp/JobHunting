package it.group9.JoubHunting

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class Login : AppCompatActivity() {
    private lateinit var db: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        db = AppDatabase.getDatabase(this)

        val etEmail = findViewById<EditText>(R.id.edit_email)
        val etPassword = findViewById<EditText>(R.id.edit_password)
        val btnLogin = findViewById<Button>(R.id.button_login)
        val tvGoRegister = findViewById<TextView>(R.id.text_go_register)

        // ログインボタン処理
        btnLogin.setOnClickListener {
            val email = etEmail.text.toString()
            val password = etPassword.text.toString()

            if (email.isBlank() || password.isBlank()) {
                Toast.makeText(this, "入力してください", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                // DBからメールアドレスでユーザー検索（UserDaoにメソッドが必要）
                // ※UserDaoに getUserByEmail がある前提です
                val user = db.userDao().getUserByEmail(email)

                if (user != null && user.password == password) {
                    // ログイン成功：保存してメインへ
                    saveLoginState(user.userId)
                    Toast.makeText(applicationContext, "ログインしました", Toast.LENGTH_SHORT).show()

                    val intent = Intent(this@Login, MainActivity::class.java)
                    // 戻れないようにフラグ設定
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                } else {
                    Toast.makeText(applicationContext, "メールアドレスかパスワードが間違っています", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // 登録画面へ遷移
        tvGoRegister.setOnClickListener {
            val intent = Intent(this, CreateAccount::class.java)
            startActivity(intent)
        }
    }

    // ログイン状態を保存する関数
    private fun saveLoginState(userId: Long) {
        val sharedPref = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putBoolean("isLoggedIn", true)
            putLong("userId", userId)
            apply()
        }
    }
}