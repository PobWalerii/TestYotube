package com.example.testyoutube.ui.filesscreen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.testyoutube.audiodata.repository.AudioRepository
import com.example.testyoutube.databinding.FragmentFilesScreenBinding
import com.example.testyoutube.utils.AudioError
import com.example.testyoutube.utils.AudioLoaded
import com.example.testyoutube.utils.AudioLoading
import com.example.testyoutube.utils.AudioUiState
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class FilesScreenFragment : Fragment() {
    @Inject lateinit var audioRepository: AudioRepository

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
        audioRepository.context = this.context
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
        viewModel.getAllAudioFromDevice()
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