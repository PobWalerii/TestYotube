package com.example.testyoutube.ui.videoplayscreen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.testyoutube.databinding.FragmentVideoPlayBinding
import com.example.testyoutube.utils.ExchangeItem
import com.example.testyoutube.utils.ItemUiState
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class VideoPlayFragment : Fragment() {

    private var _binding: FragmentVideoPlayBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<VideoPlayViewModel>()
    private lateinit var youTubePlayerView: YouTubePlayerView

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
        setupPlayerView()
        setupButtonClickListener()
    }

    private fun startUi() {
        viewModel.navigationVideo(0)
    }

    private fun setupPlayerView() {
        youTubePlayerView = binding.youtubePlayerView
    }
    private fun initYoutubePlayerView() {
        lifecycle.addObserver(youTubePlayerView)
        youTubePlayerView.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
            override fun onError(youTubePlayer: YouTubePlayer, error: PlayerConstants.PlayerError) {
                super.onError(youTubePlayer, error)
                Toast.makeText(context, error.toString(), Toast.LENGTH_LONG).show()
            }
            override fun onReady(youTubePlayer: YouTubePlayer) {
                viewModel.youTubePlayer = youTubePlayer
                youTubePlayer.cueVideo(viewModel.currentId,0F)
            }
        })
    }

    private fun setupButtonClickListener() {
        binding.collapse.setOnClickListener {
            findNavController().navigateUp()
        }
        binding.imagePrev.setOnClickListener {
            viewModel.navigationVideo(-1)
            binding.play = false
            viewModel.youTubePlayer?.pause()
        }
        binding.imageNext.setOnClickListener {
            viewModel.navigationVideo(1)
            binding.play = false
            viewModel.youTubePlayer?.pause()
        }
        binding.imagePlay.setOnClickListener {
            if(viewModel.lastPlayId == viewModel.currentId) {
                viewModel.youTubePlayer?.play()
            } else {
                viewModel.youTubePlayer?.loadVideo(viewModel.currentId, 0F)
                viewModel.lastPlayId = viewModel.currentId
            }
            binding.play = true
        }
        binding.imageStop.setOnClickListener {
            val youTubePlayer = viewModel.youTubePlayer
            binding.play = false
            youTubePlayer?.pause()
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
                  viewModel.currentId = this.videoId
                  if( viewModel.youTubePlayer == null ) {
                      viewModel.lastPlayId = this.videoId
                      initYoutubePlayerView()
                  }
              }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.youTubePlayer = null
        _binding = null
    }

}