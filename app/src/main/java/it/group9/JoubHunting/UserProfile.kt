package it.group9.JoubHunting

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem // 戻るボタン処理用
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.chip.Chip // 追加
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UserProfile : AppCompatActivity() {

    private lateinit var db: AppDatabase

    private lateinit var etLastName: EditText
    private lateinit var etFirstName: EditText
    private lateinit var etEmail: EditText
    private lateinit var etSkill: EditText
    private lateinit var etExperience: EditText

    // Chipsの参照を保持
    private lateinit var chipGrowth: Chip
    private lateinit var chipStability: Chip
    private lateinit var chipChallenge: Chip
    private lateinit var chipExpert: Chip

    // ビット値定義
    private val FLAG_GROWTH = 1
    private val FLAG_STABILITY = 2
    private val FLAG_CHALLENGE = 4
    private val FLAG_EXPERT = 8

    private var currentUserId: Long = 0L
    private var currentPassword: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)
        findViewById<Button>(R.id.btnLogout).setOnClickListener {
            logout()
        }


        // ★追加: アクションバーに戻るボタンを表示
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = "プロフィール編集" // 必要ならタイトル設定
        }

        currentUserId = intent.getLongExtra("EXTRA_USER_ID", -1L)
        if (currentUserId == -1L) {
            Toast.makeText(this, "ユーザーID取得エラー", Toast.LENGTH_SHORT).show()
        }

        db = AppDatabase.getDatabase(this)
        initViews()

        findViewById<Button>(R.id.btnRegister).setOnClickListener {
            saveUserData()
        }

        loadUserData()
    }

    // ★追加: 戻るボタンが押されたときの処理
    override fun onSupportNavigateUp(): Boolean {
        finish() // 画面を閉じて前の画面に戻る
        return true
    }

    private fun initViews() {
        etLastName = findViewById(R.id.etLastName)
        etFirstName = findViewById(R.id.etFirstName)
        etEmail = findViewById(R.id.etEmail)
        etSkill = findViewById(R.id.etSkill)
        etExperience = findViewById(R.id.etExperience)

        // Chipの取得
        chipGrowth = findViewById(R.id.chipGrowth)
        chipStability = findViewById(R.id.chipStability)
        chipChallenge = findViewById(R.id.chipChallenge)
        chipExpert = findViewById(R.id.chipExpert)
    }

    private fun loadUserData() {
        lifecycleScope.launch {
            val user = withContext(Dispatchers.IO) {
                db.userDao().getUser(currentUserId)
            }

            if (user != null) {
                currentPassword = user.password

                val names = user.name.split(" ")
                if (names.isNotEmpty()) etLastName.setText(names[0])
                if (names.size > 1) etFirstName.setText(names[1])

                etEmail.setText(user.email)
                etSkill.setText(user.skill ?: "")
                etExperience.setText(user.experience ?: "")

                // ★修正: 志向性(Int)をChipのチェック状態に反映
                val intention = user.intention ?: 0

                // ビット論理積(AND)が0でなければチェックを入れる
                chipGrowth.isChecked = (intention and FLAG_GROWTH) != 0
                chipStability.isChecked = (intention and FLAG_STABILITY) != 0
                chipChallenge.isChecked = (intention and FLAG_CHALLENGE) != 0
                chipExpert.isChecked = (intention and FLAG_EXPERT) != 0

            } else {
                Toast.makeText(this@UserProfile, "ユーザー情報なし", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun logout() {
        Toast.makeText(this, "ログアウトしました", Toast.LENGTH_SHORT).show()

        // ログイン画面へ遷移
        val intent = Intent(this, Login::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    private fun saveUserData() {
        val lastName = etLastName.text.toString().trim()
        val firstName = etFirstName.text.toString().trim()
        val email = etEmail.text.toString().trim()
        val skillInput = etSkill.text.toString()
        val experienceInput = etExperience.text.toString()

        if (lastName.isEmpty() || firstName.isEmpty()) {
            Toast.makeText(this, "氏名を入力してください", Toast.LENGTH_SHORT).show()
            return
        }

        // ★修正: Chipのチェック状態からInt値を計算
        var intentionValue = 0
        if (chipGrowth.isChecked) intentionValue += FLAG_GROWTH
        if (chipStability.isChecked) intentionValue += FLAG_STABILITY
        if (chipChallenge.isChecked) intentionValue += FLAG_CHALLENGE
        if (chipExpert.isChecked) intentionValue += FLAG_EXPERT

        val fullName = "$lastName $firstName"

        lifecycleScope.launch {
            val updatedUser = User(
                userId = currentUserId,
                password = currentPassword,
                name = fullName,
                email = email,
                skill = skillInput,
                intention = intentionValue, // 計算した値を保存
                experience = experienceInput
            )

            withContext(Dispatchers.IO) {
                db.userDao().update(updatedUser)
            }

            Toast.makeText(this@UserProfile, "保存しました", Toast.LENGTH_SHORT).show()
            finish() // 保存完了時に戻る場合
        }
    }
}
