package com.example.testyoutube.ui.permissions

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.example.testyoutube.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class PermissionsFragment : BottomSheetDialogFragment() {
    private val permissionRequestCode = 10

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
               startMusic()
            } else {
                notPermission()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            launchCameraPermissionRequest()
        } else {
             startMusic()
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == permissionRequestCode) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                startMusic()
            else {
                Toast.makeText(context, R.string.text_explanation_permission, Toast.LENGTH_LONG).show()
            }
            return
        }
    }

    private fun launchCameraPermissionRequest() {
        requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
    }

    private fun startMusic() {
        findNavController().navigate(
            PermissionsFragmentDirections.actionPermissionsFragmentToFilesScreenFragment()
        )
        dismiss()
    }

    private fun notPermission(){
        dismiss()
    }
}