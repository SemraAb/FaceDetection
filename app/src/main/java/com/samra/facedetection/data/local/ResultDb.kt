package com.samra.facedetection.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Result::class], version = 1,exportSchema = true)
abstract class ResultDB:RoomDatabase() {
    abstract fun getResultDao():ResultDao
}