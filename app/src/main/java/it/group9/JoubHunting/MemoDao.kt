package it.group9.JoubHunting

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import androidx.room.Delete

@Dao
interface MemoDao {
    @Insert
    suspend fun insert(memo: Memo)

    // 特定の企業に紐づくメモを取得
    @Query("SELECT * FROM TBL_memo WHERE companyinfo_ID = :companyId")
    suspend fun getMemosByCompanyId(companyId: Int): List<Memo>

    @Update
    suspend fun update(memo: Memo)

    @Delete
    suspend fun delete(memo: Memo)
}
