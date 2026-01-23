package it.group9.JoubHunting

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView // ★追加
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CompanyAdapter(
    private var companyList: List<CompanyInfo>,
    private val onEditClick: (CompanyInfo) -> Unit,
    private val onMemoClick: (CompanyInfo) -> Unit,
    private val onFavoriteClick: (CompanyInfo) -> Unit
) : RecyclerView.Adapter<CompanyAdapter.CompanyViewHolder>() {

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

        // ▼▼▼ ここが抜けていたので追加してください ▼▼▼
        val ivFavorite: ImageView = itemView.findViewById(R.id.ivFavorite)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CompanyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_company, parent, false)
        return CompanyViewHolder(view)
    }

    override fun onBindViewHolder(holder: CompanyViewHolder, position: Int) {
        val company = displayList[position]

        holder.tvName.text = company.companyName
        holder.tvLocation.text = "・所在地: ${company.location}"
        holder.tvStatus.text = "・選考状況: ${company.selectionStatus ?: "未設定"}"
        holder.tvNextDate.text = "・次回予定日: ${company.nextScheduledDate ?: "なし"}"
        holder.tvUrl.text = "・URL: ${company.companyUrl ?: "なし"}"

        // ▼ 定義を追加したのでエラーが消えるはずです
        val starIcon = if (company.isFavorite) {
            android.R.drawable.btn_star_big_on
        } else {
            android.R.drawable.btn_star_big_off
        }
        holder.ivFavorite.setImageResource(starIcon)
        holder.ivFavorite.setOnClickListener {
            onFavoriteClick(company)
        }

        var isExpanded = false
        holder.layoutDetail.visibility = View.GONE

        holder.layoutHeader.setOnClickListener {
            isExpanded = !isExpanded
            holder.layoutDetail.visibility = if (isExpanded) View.VISIBLE else View.GONE
        }

        holder.btnEdit.setOnClickListener { onEditClick(company) }
        holder.btnMemo.setOnClickListener { onMemoClick(company) }
    }

    override fun getItemCount(): Int = displayList.size

    fun filter(query: String) {
        val text = query.trim()
        displayList = if (text.isEmpty()) {
            companyList
        } else {
            companyList.filter { it.companyName.contains(text, ignoreCase = true) }
        }
        notifyDataSetChanged()
    }

    fun updateData(newList: List<CompanyInfo>) {
        companyList = newList
        displayList = newList
        notifyDataSetChanged()
    }
}
