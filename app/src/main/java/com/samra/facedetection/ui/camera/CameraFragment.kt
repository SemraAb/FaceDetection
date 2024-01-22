package com.samra.facedetection.ui.camera

import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import androidx.annotation.OptIn
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.mlkit.common.MlKitException
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetector
import com.google.mlkit.vision.face.FaceDetectorOptions
import com.samra.facedetection.common.base.BaseFragment
import com.samra.facedetection.data.local.Result
import com.samra.facedetection.databinding.FragmentCameraBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


class CameraFragment : BaseFragment<FragmentCameraBinding>(
    FragmentCameraBinding::inflate
) {
    private lateinit var faceDetectionExecutor: ExecutorService
    private lateinit var imageAnalysis: ImageAnalysis
    private lateinit var faceDetector: FaceDetector
    private lateinit var cameraExecutor: ExecutorService
    private lateinit var countdownTimer: CountDownTimer

    var testResult: Boolean = false
    var currentId: Int = 0
    var results = listOf<Result>()
    private val cameraViewModel: CameraViewModel by viewModels()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        startCamera(binding.cameraPreview)
        setupFaceDetection()
        observeData()
        startCountdownTimer()
    }

    private fun setupFaceDetection() {
        faceDetectionExecutor = Executors.newSingleThreadExecutor()

        val faceDetectorOptions = FaceDetectorOptions.Builder()
            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
            .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
            .build()

        faceDetector = FaceDetection.getClient(faceDetectorOptions)

        imageAnalysis = ImageAnalysis.Builder()
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_YUV_420_888)
            .build()

        imageAnalysis.setAnalyzer(faceDetectionExecutor) { imageProxy ->
            processImageProxy(imageProxy)
        }
    }

    @OptIn(ExperimentalGetImage::class)
    private fun processImageProxy(imageProxy: ImageProxy) {
        try {
            val mediaImage = imageProxy.image
            if (mediaImage != null) {
                val inputImage =
                    InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
                faceDetector.process(inputImage)
                    .addOnSuccessListener { faces ->
                        for (face in faces) {
                            val rotY =
                                face.headEulerAngleY // Head is rotated to the right rotY degrees
                            val smileProbability = face.smilingProbability

                            if (currentId == 1) {
                                if (rotY > 20) {
                                    Log.e("TEST left", "processImageProxy: ${face}")
                                    testResult = true
                                    stopCountdownTimer()
                                    cameraViewModel.update(testResult)
                                } else {
                                    Log.e("TEST left", "processImageProxy: 'test is not passed',")
                                }
                            } else if (currentId == 2) {
                                if (rotY < -20) {
                                    testResult = true
                                    stopCountdownTimer()
                                    Log.e("TEST right", "processImageProxy: ${face}")
                                    cameraViewModel.update(testResult)

                                } else {
                                    Log.e("TEST right", "processImageProxy: 'test is not passed',")
                                }
                            } else if (currentId == 3) {
                                if (smileProbability != null && smileProbability > 0.7) {
                                    testResult = true
                                    stopCountdownTimer()
                                    cameraViewModel.update(testResult)
                                    Log.e("TEST smile", "Detected face ${smileProbability}")

                                } else {
                                    Log.e("TEST smile", "processImageProxy: 'test is not passed',")
                                }
                            } else if (currentId == 4) {
                                testResult = true
                                cameraViewModel.update(testResult)
                                Log.e("TEST Neutral", "processImageProxy: 'test is passed',")
                            }
                        }

                    }
                    .addOnFailureListener { e ->
                        imageProxy.close()
                    }
                    .addOnCompleteListener {
                        imageProxy.close()
                    }
            }
        } catch (e: MlKitException) {
            Log.e("MlKitException", "Exception processing image proxy: $e")
        }
    }

    private fun startCamera(viewFinder: PreviewView) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(viewFinder.surfaceProvider)
                }

            val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA

            cameraProvider.bindToLifecycle(
                viewLifecycleOwner,
                cameraSelector,
                preview,
                imageAnalysis
            )
        }, ContextCompat.getMainExecutor(requireContext()))

        cameraExecutor = Executors.newSingleThreadExecutor()
    }


    fun observeData() {
        cameraViewModel.startTests()

        cameraViewModel.getCurrentTest().observe(viewLifecycleOwner, Observer {
            currentId = it.id
            it.testResult = testResult
            binding.testText.text = it.testText
        })

        cameraViewModel.getCompletedTest().observe(viewLifecycleOwner, Observer { it ->
            results = it
            Log.e("RESULT", "observeData: $it")
            if(it.size == 4){
                stopCountdownTimer()
                findNavController().previousBackStackEntry?.savedStateHandle?.set("key", results as ArrayList<Result>)
                findNavController().popBackStack()
//                if (results.lastOrNull()?.testResult == false) {
//                    findNavController().popBackStack()
//                }
            }
        })

    }

    private fun startCountdownTimer() {
        countdownTimer = object : CountDownTimer(10000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                // Update UI or perform any action on tick if needed
            }

            override fun onFinish() {
                // When the timer finishes, move to the next test and reset the timer
                testResult = false
                cameraViewModel.update(testResult)
                startCountdownTimer()
            }
        }.start()
    }

    private fun stopCountdownTimer() {
        countdownTimer.cancel()
    }

    override fun onDestroyView() {
        // Stop the countdown timer when the fragment is destroyed to avoid leaks
        stopCountdownTimer()
        super.onDestroyView()
    }

}