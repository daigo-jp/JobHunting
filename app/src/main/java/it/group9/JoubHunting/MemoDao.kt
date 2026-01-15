package it.group9.JoubHunting

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface MemoDao {
    // 特定の企業のメモを全部持ってくる
    @Query("SELECT * FROM TBL_memo WHERE companyinfo_ID = :companyId ORDER BY memo_ID DESC")
    suspend fun getMemosByCompanyId(companyId: Int): List<Memo>

    @Insert
    suspend fun insert(memo: Memo)

    @Update
    suspend fun update(memo: Memo)

    @Delete
    suspend fun delete(memo: Memo)
}