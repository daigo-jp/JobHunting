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

    @ColumnInfo(name = "companyinfo_ID", index = true) val companyInfoId: Int, // 外部キー
    @ColumnInfo(name = "companymemo") val companyMemo: String
)
