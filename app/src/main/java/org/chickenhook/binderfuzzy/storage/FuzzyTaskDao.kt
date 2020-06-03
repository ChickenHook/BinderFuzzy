package org.chickenhook.binderfuzzy.storage

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
public interface FuzzyTaskDao {
    @Query("SELECT * FROM fuzzytaskinfo")
    fun getAll(): List<FuzzyTaskInfo>

    @Query("SELECT * FROM fuzzytaskinfo WHERE task_id IN (:taskids)")
    fun loadAllByIds(taskids: IntArray): List<FuzzyTaskInfo>

    @Query(
        "SELECT * FROM fuzzytaskinfo WHERE task_class LIKE :class_name AND " +
                "task_method LIKE :method_name LIMIT 1"
    )
    fun findByName(class_name: String, method_name: String): FuzzyTaskInfo

    @Insert
    fun insertAll(vararg tasks: FuzzyTaskInfo)

    @Delete
    fun delete(task: FuzzyTaskInfo)
}