package it.group9.JoubHunting

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import androidx.room.Delete

@Dao
interface CompanyInfoDao {
    @Insert
    suspend fun insert(companyInfo: CompanyInfo): Long

    // ▼ 修正箇所: ORDER BY aspiration_level DESC を追加
    // DESC は降順（大きい数字→小さい数字）という意味です
    @Query("SELECT * FROM TBL_company_info ORDER BY aspiration_level DESC")
    suspend fun getAllCompanies(): List<CompanyInfo>
    @Query("SELECT * FROM TBL_company_info WHERE user_ID = :userId ORDER BY is_favorite DESC, aspiration_level DESC")
    suspend fun getCompaniesByUserId(userId: Long): List<CompanyInfo>
    @Query("SELECT * FROM TBL_company_info WHERE company_ID = :companyId")
    suspend fun getCompanyById(companyId: Int): CompanyInfo?
    @Update
    suspend fun update(companyInfo: CompanyInfo)
    @Query("""
    UPDATE TBL_company_info
    SET priority = :priority
    WHERE company_ID = :companyId
""")
    suspend fun updatePriority(companyId: Int, priority: Int)


    @Delete
    suspend fun delete(companyInfo: CompanyInfo)
}
