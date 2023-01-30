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
        observeUiState()
        startUI()
        setOnMenuClickListener()
    }

    private fun observeUiState() {
        viewModel.state.observe(viewLifecycleOwner, ::handleAudioUiState)
    }

    private fun handleAudioUiState(state: AudioUiState) {
        when (state) {
            is AudioError -> {
                Toast.makeText(
                    requireContext(), state.message, Toast.LENGTH_SHORT
                ).show()
            }
            is AudioLoaded -> {
                state.data.apply {
                    val list = this
                    Toast.makeText(context,"$list",Toast.LENGTH_LONG).show()
                }
            }
            is AudioLoading -> {
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