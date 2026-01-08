package it.group9.JoubHunting

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import androidx.room.Delete

@Dao
interface CompanyInfoDao {
    @Insert
    suspend fun insert(companyInfo: CompanyInfo): Long

    // 特定のユーザーに紐づく企業一覧を取得
    @Query("SELECT * FROM TBL_company_info WHERE user_ID = :userId")
    suspend fun getCompaniesByUserId(userId: Long): List<CompanyInfo>

    @Update
    suspend fun update(companyInfo: CompanyInfo)

    @Delete
    suspend fun delete(companyInfo: CompanyInfo)
}
