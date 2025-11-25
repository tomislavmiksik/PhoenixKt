package dev.tomislavmiksik.phoenix.core.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "progress_points")
data class ProgressPoint(
    @PrimaryKey(autoGenerate = false)
    val progressPointId : Int,
    val weight : Float,
    val date : Date

)
