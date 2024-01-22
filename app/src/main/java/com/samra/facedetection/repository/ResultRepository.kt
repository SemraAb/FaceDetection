package com.samra.facedetection.repository

import com.samra.facedetection.data.local.Result
import com.samra.facedetection.data.local.ResultDao

class ResultRepository( private val db : ResultDao) {
    suspend fun insertResult(result: Result){
        db.insertResult(result)
    }
    fun getResultData()=db.getResultData()
}