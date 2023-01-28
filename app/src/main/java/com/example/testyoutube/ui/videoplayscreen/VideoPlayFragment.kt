package com.example.testyoutube.ui.videoplayscreen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.delivery.utils.*
import com.example.testyoutube.databinding.FragmentVideoPlayBinding
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import dagger.hilt.android.AndroidEntryPoint


//https://github.com/PierfrancescoSoffritti/android-youtube-player#xml-attributes

@AndroidEntryPoint
class VideoPlayFragment : Fragment() {

    private var _binding: FragmentVideoPlayBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<VideoPlayViewModel>()
    lateinit var youTubePlayerView: YouTubePlayerView


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
        initYoutubePlayerView()
    }

    private fun initYoutubePlayerView() {
        youTubePlayerView = binding.youtubePlayerView
        getLifecycle().addObserver(youTubePlayerView)
        youTubePlayerView.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
            override fun onReady(youTubePlayer: YouTubePlayer) {
                //setPlayNextVideoButtonClickListener(youTubePlayer)
                //YouTubePlayerUtils.loadOrCueVideo(
                //    youTubePlayer, lifecycle,
                //    VideoIdsProvider.getNextVideoId(), 0f
                //)
            }
        })

    }

    private fun startUi() {
        viewModel.navigationVideo(0)
        //lifecycle.addObserver(playerView as YouTubePlayerView)
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
                  viewModel.currentId = this.videoId
              }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}