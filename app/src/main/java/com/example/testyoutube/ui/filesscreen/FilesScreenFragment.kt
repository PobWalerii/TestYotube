package com.example.testyoutube.ui.filesscreen

import android.annotation.SuppressLint
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.testyoutube.audiodata.entity.ItemAudio
import com.example.testyoutube.databinding.FragmentFilesScreenBinding
import com.example.testyoutube.utils.*
import com.example.testyoutube.utils.HideKeyboard.hideKeyboardFromView
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class FilesScreenFragment : Fragment() {

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
        setListenersTextSearchChanged()
        setOnSearchClickListener()
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

    @SuppressLint("NotifyDataSetChanged")
    private fun refreshRecycler(current: ItemAudio) {
        val position = adapter.getItemPosition(current)
        adapter.setCurrentId(current)
        recyclerView.layoutManager?.scrollToPosition(position)
        adapter.notifyDataSetChanged()
    }

    private fun miniPlayerSetItem(current: ItemAudio?) {
        with(binding.miniPlayer) {
            if(current==null) {
                imageBitmap = null
                channel = ""
                title = ""
            } else {
                //image = current.art_uri.toString()
                imageBitmap = current.image
                channel = current.artist
                title = current.title
            }
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
                val list = state.data
                adapter.setList(list)
                if(list.isNotEmpty()) {
                    exchangeCurrentItem(list[0])
                    viewModel.setFullList(list)
                }
            }
            is AudioLoading -> {
                binding.visibleProgress = state.isSearch
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
                pauseAudio()
                viewModel.setCurrentAudio(current)
                miniPlayerSetItem(current)
                hideKeyboard()
            }
        })
    }

    private fun exchangeCurrentItem(current: ItemAudio?) {
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
            hideKeyboard()
        }
        binding.miniPlayer.imagePrev.setOnClickListener {
            pauseAudio()
            viewModel.navigationAudio(-1)
            hideKeyboard()
        }
        binding.miniPlayer.imagePlay.setOnClickListener {
            playAudio(viewModel.getCurrentAudio())
            hideKeyboard()
        }
        binding.miniPlayer.imageStop.setOnClickListener {
            pauseAudio()
            hideKeyboard()
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
                    player?.isLooping = false
                    player?.prepare()
                    player?.start()
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setListenersTextSearchChanged() {
        binding.appBarLayout.textSearch.addTextChangedListener {
            definitionOfChange()
        }
    }

    private fun definitionOfChange() {
        val textSearch = binding.appBarLayout.textSearch.text.toString().lowercase(Locale.ROOT)
        val newList = viewModel.getFullList().filter { item ->
            (item.title!=null && item.title.lowercase().contains(textSearch)) ||
            (item.artist!=null && item.artist.lowercase().contains(textSearch))
        }
        adapter.setList(newList)
        viewModel.setCurrentList(newList)
        val current: ItemAudio? =
            if (newList.isNotEmpty()) {
                newList[0]
            } else {
                null
            }
        viewModel.setCurrentAudio(current)
        miniPlayerSetItem(current)
        if(current != viewModel.currentPlayAudio) {
            pauseAudio()
        }
    }

    private fun setOnSearchClickListener() {
        binding.appBarLayout.search.setOnClickListener {
            hideKeyboard()
        }
        binding.appBarLayout.textSearch.setOnClickListener {
            binding.appBarLayout.textSearch.isCursorVisible = true
        }
    }

    private fun hideKeyboard() {
        hideKeyboardFromView(requireContext(),binding.appBarLayout.textSearch)
        binding.appBarLayout.textSearch.isCursorVisible =false
    }

}