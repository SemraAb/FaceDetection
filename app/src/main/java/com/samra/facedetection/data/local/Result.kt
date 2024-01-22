package com.samra.facedetection.data.local

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity(tableName = "result")
@Parcelize
data class Result(
    @PrimaryKey(autoGenerate = true)
    val id:Int = 0,
    val testText : String ,
    var testResult: Boolean = false,
    val testTimestamp : Int
): Parcelable