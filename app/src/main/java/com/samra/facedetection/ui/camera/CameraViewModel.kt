package com.samra.facedetection.ui.camera

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.samra.facedetection.data.local.Result
import com.samra.facedetection.repository.ResultRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CameraViewModel : ViewModel() {
    private var currentTestIndex = 0
    private var currentTest = MutableLiveData<Result>()
    private var completedTest = MutableLiveData<List<Result>>()

    var tests = mutableListOf(
        Result(id = 1, testText = "Head Rotation Left", testResult = false, testTimestamp = 5000),
        Result(id = 2, testText = "Head Rotation Right", testResult = false, testTimestamp = 5000),
        Result(id = 3, testText = "Smile Detection", testResult = false, testTimestamp = 5000),
        Result(id = 4, testText = "Neutral Detection", testResult = false, testTimestamp = 5000)
    )
    fun startTests() {
        tests.shuffle()
        currentTestIndex = 0
        currentTest.postValue(tests[currentTestIndex])
    }

    fun update(isTestPassed : Boolean){
        tests[currentTestIndex].testResult = isTestPassed
        if(currentTestIndex+1>=tests.size){
            finish()
        }else{
            currentTestIndex++
            currentTest.postValue(tests[currentTestIndex])
        }
    }

    fun finish(){
        completedTest.postValue(tests)
    }
    fun getCurrentTest():LiveData<Result>{
        return currentTest
    }

    fun getCompletedTest() : LiveData<List<Result>>{
        return completedTest
    }

//    suspend fun performTest() {
//        withContext(Dispatchers.IO) {
//            tests.forEach { currentTest ->
//                currentTestIndex = currentTest.id
//                when (currentTestIndex) {
//                    1 -> updateLeftTest(false)
//                    2 -> updateRight(false)
//                    3 -> updateSmileTest(false)
//                    4 -> updateNeutralTest(false)
//                }
//                repo.insertResult(currentTest)
//            }
//        }
//    }
//
//    fun updateLeftTest(testResult: Boolean){
//        tests[0].testResult = testResult
//        val testText = tests[0].testText
//    }
//    fun updateRight(testResult: Boolean){
//        tests[1].testResult = testResult
//        val testText = tests[1].testText
//    }
//    fun updateSmileTest(testResult: Boolean){
//        tests[2].testResult = testResult
//        val testText = tests[2].testText
//    }
//    fun updateNeutralTest(testResult: Boolean){
//        tests[3].testResult = testResult
//        val testText = tests[3].testText
//    }
//    fun updateTestResult(id: Int , testResult: Boolean){
//        val test = tests.find { it.id == id }
//        test?.let {
//            it.testResult = testResult
//            viewModelScope.launch {
//                repo.insertResult(it)
//            }
//        }
//    }

}