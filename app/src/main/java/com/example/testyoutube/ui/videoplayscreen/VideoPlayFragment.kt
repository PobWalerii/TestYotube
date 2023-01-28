package com.example.testyoutube.ui.videoplayscreen

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.delivery.utils.*
import com.example.testyoutube.R
import com.example.testyoutube.databinding.FragmentVideoPlayBinding
import com.example.testyoutube.databinding.FragmentYoutubeScreenBinding
import com.example.testyoutube.ui.youtubescreen.VideoListViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class VideoPlayFragment : Fragment() {
    private var _binding: FragmentVideoPlayBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<VideoPlayViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentVideoPlayBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        startUi()
        observeUiState()
        setupButtonClickListener()
    }

    private fun startUi() {
        viewModel.navigationVideo(0)
    }

    private fun setupButtonClickListener() {
        binding.collapse.setOnClickListener {
            findNavController().navigateUp()
        }
        binding.imagePrev.setOnClickListener {
            viewModel.navigationVideo(-1)
        }
        binding.imageNext.setOnClickListener {
            viewModel.navigationVideo(1)
        }
        binding.imagePlay.setOnClickListener {
            binding.play = true
        }
        binding.imageStop.setOnClickListener {
            binding.play = false
        }

    }

    private fun observeUiState() {
        viewModel.state.observe(viewLifecycleOwner, ::handleItemUiState)
    }

    private fun handleItemUiState(state: ItemUiState) {
        when (state) {
            is ExchangeItem -> {
              state.data.apply {
                  binding.item = this
              }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}