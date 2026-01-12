package it.group9.JoubHunting

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CompanyAdapter(
    private var companyList: List<CompanyInfo>, // 全データ
    private val onEditClick: (CompanyInfo) -> Unit, // 編集ボタンが押された時の処理
    private val onMemoClick: (CompanyInfo) -> Unit  // メモボタンが押された時の処理
) : RecyclerView.Adapter<CompanyAdapter.CompanyViewHolder>() {

    // 検索用に表示するリスト（最初は全データと同じ）
    private var displayList: List<CompanyInfo> = companyList

    class CompanyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName: TextView = itemView.findViewById(R.id.tvCompanyName)
        val layoutDetail: LinearLayout = itemView.findViewById(R.id.layoutCompanyDetail)
        val layoutHeader: LinearLayout = itemView.findViewById(R.id.layoutCompanyHeader)

        // 詳細項目
        val tvLocation: TextView = itemView.findViewById(R.id.tvCompanyLocation)
        val tvStatus: TextView = itemView.findViewById(R.id.tvCompanyStatus)
        val tvNextDate: TextView = itemView.findViewById(R.id.tvCompanyNextDate)
        val tvUrl: TextView = itemView.findViewById(R.id.tvCompanyUrl)

        val btnEdit: Button = itemView.findViewById(R.id.btnEditCompany)
        val btnMemo: Button = itemView.findViewById(R.id.btnCompanyMemo)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CompanyViewHolder {
        // item_company.xml (あなたが作ったリスト用xmlの名前) を読み込む
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_company, parent, false)
        return CompanyViewHolder(view)
    }

    override fun onBindViewHolder(holder: CompanyViewHolder, position: Int) {
        val company = displayList[position]

        // データのセット
        holder.tvName.text = company.companyName
        holder.tvLocation.text = "・所在地: ${company.location}"
        holder.tvStatus.text = "・選考状況: ${company.selectionStatus ?: "未設定"}"
        holder.tvNextDate.text = "・次回予定日: ${company.nextScheduledDate ?: "なし"}"
        holder.tvUrl.text = "・URL: ${company.companyUrl ?: "なし"}"

        // 【機能】詳細の開閉ロジック (最初は閉じておく場合)
        // XMLで visibility="visible" になっていますが、コードで制御するならここ
        var isExpanded = false
        holder.layoutDetail.visibility = View.GONE // 初期状態は閉じる

        holder.layoutHeader.setOnClickListener {
            isExpanded = !isExpanded
            holder.layoutDetail.visibility = if (isExpanded) View.VISIBLE else View.GONE
        }

        // ボタンのクリックイベント
        holder.btnEdit.setOnClickListener { onEditClick(company) }
        holder.btnMemo.setOnClickListener { onMemoClick(company) }
    }

    override fun getItemCount(): Int = displayList.size

    // === 検索フィルター機能 ===
    fun filter(query: String) {
        val text = query.trim()
        displayList = if (text.isEmpty()) {
            companyList // 空なら全件表示
        } else {
            // 企業名に検索文字が含まれているものだけ抽出
            companyList.filter { it.companyName.contains(text, ignoreCase = true) }
        }
        notifyDataSetChanged() // リスト更新通知
    }

    // データベースから新しいデータを読み込んだ時に呼ぶ
    fun updateData(newList: List<CompanyInfo>) {
        companyList = newList
        displayList = newList
        notifyDataSetChanged()
    }
}
