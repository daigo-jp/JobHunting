package it.group9.JoubHunting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

// データクラス（変更なし）
data class Memo(
    val title: String,
    val date: String,
    val content: String
)

class MemoList : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_memo_list)

        // システムバーの余白調整（元のコードを維持）
        // ※注意: XMLのルート要素(ConstraintLayout等)に android:id="@+id/main" が必要です
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // --- ここから追加したロジック ---

        // 1. 表示するデータの作成（ダミーデータ）
        val memoList = listOf(
            Memo("メモタイトル", "2025/11/11", "11/11のメモ内容です。\nここをクリックすると詳細が表示されます。"),
            Memo("メモタイトル", "2025/11/12", "11/12のメモ内容です。"),
            Memo("メモタイトル", "2025/11/15", "11/15のメモ内容です。"),
            Memo("メモタイトル", "2025/12/22", "12/22のメモ内容です。"),
            Memo("メモタイトル", "2025/11/25", "11/25のメモ内容です。")
        )

        // 2. 画面上のパーツ（View）を取得
        // ※XML側のIDと一致させてください
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewMemos)
        val selectedTitle = findViewById<TextView>(R.id.tvSelectedTitle)
        val memoContent = findViewById<EditText>(R.id.etMemoContent)

        // 3. アダプターの作成とクリック時の動作定義
        val adapter = MemoAdapter(memoList) { clickedMemo ->
            // リストの項目がタップされたときの処理
            // タイトルに「タイトル (日付)」の形式で表示
            selectedTitle.text = "${clickedMemo.title} (${clickedMemo.date})"
            // 入力欄に内容を表示
            memoContent.setText(clickedMemo.content)
        }

        // 4. RecyclerViewの設定
        recyclerView.layoutManager = LinearLayoutManager(this) // リストを縦に並べる
        recyclerView.adapter = adapter
    }
}

// --- 以下、アダプタークラス（同じファイル内に追記） ---

class MemoAdapter(
    private val memos: List<Memo>,
    private val onItemClick: (Memo) -> Unit
) : RecyclerView.Adapter<MemoAdapter.MemoViewHolder>() {

    // 行のレイアウトを読み込む
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemoViewHolder {
        // ※ R.layout.item_memo_row が存在することを確認してください
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_memo_row, parent, false)
        return MemoViewHolder(view)
    }

    // データをセットする
    override fun onBindViewHolder(holder: MemoViewHolder, position: Int) {
        val memo = memos[position]
        holder.bind(memo)

        // タップリスナーのセット
        holder.itemView.setOnClickListener {
            onItemClick(memo)
        }
    }

    override fun getItemCount(): Int = memos.size

    // Viewを保持するクラス
    class MemoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        // ※ item_memo_row.xml 内のIDと一致させてください
        private val titleText: TextView = view.findViewById(R.id.rowTitle)
        private val dateText: TextView = view.findViewById(R.id.rowDate)

        fun bind(memo: Memo) {
            titleText.text = memo.title
            dateText.text = memo.date
        }
    }
}