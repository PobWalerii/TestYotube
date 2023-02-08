package com.example.testyoutube.ui.videoplayscreen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.testyoutube.databinding.FragmentVideoPlayBinding
import com.example.testyoutube.utils.ExchangeItem
import com.example.testyoutube.utils.ItemUiState
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class VideoPlayFragment : Fragment() {

    private var _binding: FragmentVideoPlayBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<VideoPlayViewModel>()
    var youTubePlayer: YouTubePlayer? = null

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
        viewModel.currentId = ""
        viewModel.lastPlayId = ""
        viewModel.navigationVideo(0)
    }

    private fun initYoutubePlayerView() {
        val youTubePlayerView = binding.youtubePlayerView
        lifecycle.addObserver(youTubePlayerView)
        youTubePlayerView.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
            //override fun onError(youTubePlayer: YouTubePlayer, error: PlayerConstants.PlayerError) {
            //    super.onError(youTubePlayer, error)
                //Toast.makeText(context, error.toString(), Toast.LENGTH_LONG).show()
            //}
            override fun onReady(youTubePlayer: YouTubePlayer) {
                this@VideoPlayFragment.youTubePlayer = youTubePlayer
                youTubePlayer.cueVideo(viewModel.currentId.trim(),0F)
            }
        })
    }

    private fun setupButtonClickListener() {
        binding.collapse.setOnClickListener {
            youTubePlayer?.pause()
            findNavController().navigateUp()
        }
        binding.imagePrev.setOnClickListener {
            viewModel.navigationVideo(-1)
            binding.isPlay = false
            youTubePlayer?.pause()
        }
        binding.imageNext.setOnClickListener {
            viewModel.navigationVideo(1)
            binding.isPlay = false
            youTubePlayer?.pause()
        }
        binding.imagePlay.setOnClickListener {
            if(viewModel.lastPlayId == viewModel.currentId) {
                youTubePlayer?.play()
            } else {
                youTubePlayer?.loadVideo(viewModel.currentId.trim(), 0F)
                viewModel.lastPlayId = viewModel.currentId
            }
            binding.isPlay = true
        }
        binding.imageStop.setOnClickListener {
            binding.isPlay = false
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
                  if( youTubePlayer == null ) {
                      viewModel.lastPlayId = this.videoId
                      initYoutubePlayerView()
                  }
              }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        youTubePlayer?.pause()
        youTubePlayer = null
        _binding = null
    }

}