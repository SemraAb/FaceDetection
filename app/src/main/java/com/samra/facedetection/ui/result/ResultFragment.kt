package com.samra.facedetection.ui.result

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.samra.facedetection.common.base.BaseFragment
import com.samra.facedetection.data.local.Result
import com.samra.facedetection.databinding.FragmentResultBinding

class ResultFragment : BaseFragment<FragmentResultBinding>(
    FragmentResultBinding::inflate
) {
    private val resultAdapter: ResultAdapter by lazy { ResultAdapter() }
    var result = listOf<Result>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.recyclerView.adapter = resultAdapter

        binding.plusBtn.setOnClickListener {
            permissionHandler()
        }
        observeNavigationCallBack()
    }
    private fun permissionHandler() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    requireActivity(),
                    Manifest.permission.CAMERA
                )
            ) {
                Snackbar.make(requireView(), "Permission is required for the camera", Snackbar.LENGTH_INDEFINITE)
                    .setAction("Give permission") {
                        requestCameraPermission()
                    }
                    .show()
            } else {
                requestCameraPermission()
            }
        } else {
            navigateToCameraFragment()
        }
    }

    private fun requestCameraPermission() {
        requestPermissionLauncher.launch(Manifest.permission.CAMERA)
    }

    private fun navigateToCameraFragment() {
        val action = ResultFragmentDirections.actionResultFragmentToCameraFragment()
        findNavController().navigate(action)
    }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                navigateToCameraFragment()
            } else {
                Toast.makeText(
                    requireContext(),
                    "Camera permission denied",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

    private fun observeNavigationCallBack() {
        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<ArrayList<Result>>("key")
            ?.observe(viewLifecycleOwner) {
                result = it
                Log.e("RESULT", "observeNavigationCallBack: $result", )
                resultAdapter.submitList(result)
            }
    }
}
