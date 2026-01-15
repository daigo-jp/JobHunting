package it.group9.JoubHunting

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MemoAdapter(
    private var memos: List<Memo>, // TestMemo -> Memo に変更
    private val onItemClick: (Memo) -> Unit
) : RecyclerView.Adapter<MemoAdapter.MemoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_memo_row, parent, false)
        return MemoViewHolder(view)
    }

    override fun onBindViewHolder(holder: MemoViewHolder, position: Int) {
        val memo = memos[position]
        holder.bind(memo)
        holder.itemView.setOnClickListener { onItemClick(memo) }
    }

    override fun getItemCount(): Int = memos.size

    // データを更新するためのメソッド
    fun updateData(newMemos: List<Memo>) {
        memos = newMemos
        notifyDataSetChanged()
    }

    class MemoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val titleText: TextView = view.findViewById(R.id.rowTitle)
        private val dateText: TextView = view.findViewById(R.id.rowDate)

        fun bind(memo: Memo) {
            titleText.text = memo.title
            dateText.text = memo.date
        }
    }
}