package com.example.testyoutube.ui.filesscreen

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.testyoutube.databinding.FragmentFilesScreenBinding
import com.example.testyoutube.utils.*
import dagger.hilt.android.AndroidEntryPoint

//https://riptutorial.com/android/example/23916/fetch-audio-mp3-files-from-specific-folder-of-device-or-fetch-all-files
//android kotlin mp3 files in directory
//https://developer.android.com/training/data-storage/shared/media

@AndroidEntryPoint
class FilesScreenFragment : Fragment() {

    private var _binding: FragmentFilesScreenBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<FilesListViewModel>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentFilesScreenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        startUI()
        observeUiState()
        setOnMenuClickListener()
    }

    private fun observeUiState() {
        viewModel.state.observe(viewLifecycleOwner, ::handleUiState)
    }

    private fun handleUiState(state: AudioUiState) {
        when (state) {
            is AudioListError -> {
                Toast.makeText(
                    requireContext(), state.message, Toast.LENGTH_SHORT
                ).show()
            }
            is AudioListLoaded -> {
                Toast.makeText(context,"Loaded",Toast.LENGTH_LONG).show()
                state.data.apply {
                    val list = this
                    Toast.makeText(context,"$list",Toast.LENGTH_LONG).show()
                    binding.count.text = "${list.size}"
                    //refreshUi(this)
                }
            }
            is AudioListLoading -> {
                binding.visibleProgress = state.isLoading
            }
        }
    }


    private fun startUI() {
        binding.bottomBar.active = 2
        viewModel.getAllAudioFromDevice(requireContext())
    }

    private fun setOnMenuClickListener() {
        binding.bottomBar.music.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}