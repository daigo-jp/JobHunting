package it.group9.JoubHunting

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo
import androidx.room.ForeignKey

@Entity(
    tableName = "TBL_company_info",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["user_ID"],
            childColumns = ["user_ID"],
            onDelete = ForeignKey.CASCADE // ユーザーが消えたら企業情報も消す
        )
    ]
)
data class CompanyInfo(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "company_ID") val companyId: Int = 0,

    @ColumnInfo(name = "company_name") val companyName: String,
    @ColumnInfo(name = "industry") val industry: String,
    @ColumnInfo(name = "location") val location: String,
    @ColumnInfo(name = "selection_status") val selectionStatus: String?,
    @ColumnInfo(name = "aspiration_level") val aspirationLevel: Int,
    @ColumnInfo(name = "next_scheduled_date") val nextScheduledDate: String?, // DATETIME -> String
    @ColumnInfo(name = "companyURL") val companyUrl: String?,
    @ColumnInfo(name = "is_favorite") val isFavorite: Boolean = false,

    @ColumnInfo(name = "user_ID", index = true) val userId: Long // 外部キー
)
