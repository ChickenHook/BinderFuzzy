package org.chickenhook.binderfuzzy.storage

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class FuzzyTaskInfo(
    @PrimaryKey(autoGenerate = true)
    var task_id: Int,
    @ColumnInfo(name = "task_name") val taskName: String?,
    @ColumnInfo(name = "task_class") val className: String?,
    @ColumnInfo(name = "task_method") val methodName: String?,
    @ColumnInfo(name = "log_file") val logFile: String?
) {

}