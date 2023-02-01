package com.example.testyoutube.ui.filesscreen

import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.testyoutube.audiodata.entity.ItemAudio
import com.example.testyoutube.audiodata.repository.AudioRepository
import com.example.testyoutube.databinding.FragmentFilesScreenBinding
import com.example.testyoutube.utils.*
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class FilesScreenFragment : Fragment() {

    @Inject lateinit var audioRepository: AudioRepository

    private var _binding: FragmentFilesScreenBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<FilesListViewModel>()
    private lateinit var adapter: AudioListAdapter
    private lateinit var recyclerView: RecyclerView
    private var player: MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupAdapter()
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
        setupRecycler()
        observeUiState()
        startUI()
        setOnMenuClickListener()
        setupItemClickListener()
        setItemNavigationListener()
        observeListState()
    }

    private fun observeListState() {
        viewModel.stateItem.observe(viewLifecycleOwner, ::handleItemUiState)
    }
    private fun handleItemUiState(stateItem: ItemAudioUiState) {
        when (stateItem) {
            is ExchangeAudioItem -> {
                stateItem.data.apply {
                    miniPlayerSetItem(this)
                    refreshRecycler(this)
                }
            }
        }
    }

    private fun refreshRecycler(current: ItemAudio) {
        val position = adapter.getItemPosition(current)
        adapter.setCurrentId(current)
        recyclerView.layoutManager?.scrollToPosition(position)
    }

    private fun miniPlayerSetItem(current: ItemAudio) {
        with (binding.miniPlayer) {
            image = current.art_uri.toString()
            channel = current.artist
            title = current.title
        }
    }

    private fun setupAdapter() {
        adapter = AudioListAdapter()
        adapter.setHasStableIds(true)
    }

    private fun setupRecycler() {
        recyclerView = binding.recycler
        recyclerView.adapter = adapter
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
                    adapter.setList(this)
                    if(this.isNotEmpty()) {
                        exchangeCurrentItem(this[0])
                        viewModel.setCurrentList(this)
                    }
                }
            }
            is AudioLoading -> {
                binding.visibleProgress = state.isLoading
            }
        }
    }

    private fun startUI() {
        binding.bottomBar.active = 2
        binding.miniPlayer.isAudio = true
        viewModel.getAllAudioFromDevice()
    }

    private fun setupItemClickListener() {
        adapter.setOnItemClickListener(object : AudioListAdapter.OnItemClickListener {
            override fun onItemClick(current: ItemAudio) {
                exchangeCurrentItem(current)
            }
        })
    }

    private fun exchangeCurrentItem(current: ItemAudio) {
        viewModel.setCurrentAudio(current)
        viewModel.navigationAudio(0)
    }

    private fun setOnMenuClickListener() {
        binding.bottomBar.music.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        player?.release()
        player = null
        _binding = null
    }

    private fun setItemNavigationListener() {
        binding.miniPlayer.imageNext.setOnClickListener {
            pauseAudio()
            viewModel.navigationAudio(1)
        }
        binding.miniPlayer.imagePrev.setOnClickListener {
            pauseAudio()
            viewModel.navigationAudio(-1)
        }
        binding.miniPlayer.imagePlay.setOnClickListener {
            playAudio(viewModel.getCurrentAudio())
        }
        binding.miniPlayer.imageStop.setOnClickListener {
            pauseAudio()
        }
    }

    private fun pauseAudio() {
        player?.pause()
        binding.miniPlayer.isPlay = false
    }

    private fun playAudio(audio: ItemAudio?) {
        if (audio != null) {
            try {
                binding.miniPlayer.isPlay = true
                if(audio==viewModel.currentPlayAudio && player!=null) {
                    player?.start()
                } else {
                    player?.stop()
                    viewModel.currentPlayAudio = audio
                    player?.release()
                    player = MediaPlayer()
                    val content = Uri.parse(audio.musicUri)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        val file = requireContext().contentResolver.openAssetFileDescriptor(
                            content,
                            "r",
                            null
                        )
                        if (file != null) player?.setDataSource(file)
                        file?.close()
                    } else {
                        player?.setDataSource(audio.filePath)
                    }
                    player?.setLooping(false)
                    player?.prepare()
                    player?.start()
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
            }
        }
    }



}