package it.group9.JoubHunting

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo
import androidx.room.ForeignKey

@Entity(
    tableName = "TBL_memo",
    foreignKeys = [
        ForeignKey(
            entity = CompanyInfo::class,
            parentColumns = ["company_ID"],
            childColumns = ["companyinfo_ID"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Memo(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "memo_ID") val memoId: Int = 0,

    @ColumnInfo(name = "companyinfo_ID", index = true) val companyInfoId: Int, // どの企業のメモか

    @ColumnInfo(name = "memo_title") val title: String,     // 追加：タイトル
    @ColumnInfo(name = "memo_date") val date: String,       // 追加：日付
    @ColumnInfo(name = "companymemo") val content: String   // 内容
)