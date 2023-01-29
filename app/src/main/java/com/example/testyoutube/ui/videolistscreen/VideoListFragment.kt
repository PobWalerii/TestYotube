package com.example.testyoutube.ui.videolistscreen

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.testyoutube.R
import com.example.testyoutube.data.database.entity.ItemVideo
import com.example.testyoutube.databinding.FragmentYoutubeScreenBinding
import com.example.testyoutube.utils.*
import com.example.testyoutube.utils.Constants.COUNT_HORIZONTAL_ITEMS
import com.example.testyoutube.utils.HideKeyboard.hideKeyboardFromView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class VideoListFragment : Fragment() {
    private var _binding: FragmentYoutubeScreenBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<VideoListViewModel>()
    private lateinit var horisontalAdapter: HorisontalListAdapter
    private lateinit var verticalAdapter: VerticalListAdapter
    private lateinit var horisontalRecyclerView: RecyclerView
    private lateinit var verticalRecyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupAdapters()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentYoutubeScreenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        startUI()
        setupRecyclers()
        observeUiState()
        setOnMenuClickListener()
        setOnSearchClickListener()
        setupItemClickListener()
        setItemNavigationListener()
        startVideoListener()
    }

    private fun setupItemClickListener() {
        horisontalAdapter.setOnItemClickListener(object : HorisontalListAdapter.OnItemClickListener {
            override fun onItemClick(current: ItemVideo) {
                miniPlayerSetItem(current)
            }
        })
        verticalAdapter.setOnItemClickListener(object : VerticalListAdapter.OnItemClickListener {
            override fun onItemClick(current: ItemVideo) {
                miniPlayerSetItem(current)
            }
        })
    }

    private fun miniPlayerSetItem(current: ItemVideo) {
        viewModel.setCurrentVideo(current)
        with (binding.miniPlayer) {
            image = current.imageUrl
            channel = current.channelTitle
            title = current.title
        }

    }

    private fun setupAdapters() {
        horisontalAdapter = HorisontalListAdapter()
        horisontalAdapter.setHasStableIds(true)
        verticalAdapter = VerticalListAdapter()
        verticalAdapter.setHasStableIds(true)
    }

    private fun setupRecyclers() {
        horisontalRecyclerView = binding.recyclerChannels
        horisontalRecyclerView.adapter = horisontalAdapter
        verticalRecyclerView = binding.recyclerContent
        verticalRecyclerView.adapter = verticalAdapter
        verticalRecyclerView.layoutManager = GridLayoutManager(requireContext(),3)
    }
    private fun startUI() {
        binding.bottomBar.active = 1
        if ( !viewModel.isStarted ) {
            val sPref =
                requireActivity().getSharedPreferences("MyPref", AppCompatActivity.MODE_PRIVATE)
            val default = getString(R.string.start_response)
            val startResponse = sPref.getString("textSearch", default)
            val keyWord = startResponse ?: default
            viewModel.keyWord = keyWord
            getVideoFromDatabase()
            //getVideoList(keyWord)
            viewModel.isStarted = true
        }
    }

    private fun setOnSearchClickListener() {
        binding.appBarLayout.search.setOnClickListener {
            val textView = binding.appBarLayout.textSearch
            val keyWord = textView.text.toString()
            if (keyWord.isNotEmpty()) {
                hideKeyboardFromView(textView.context, textView)
                viewModel.keyWord = keyWord
                getVideoList(keyWord)
            }
        }
    }

    private fun getVideoList(keyWord: String) {
        viewModel.getVideoList(keyWord)
    }
    private fun getVideoFromDatabase() {
        viewModel.getVideoFromDatabase()
    }

    private fun setOnMenuClickListener() {
        binding.bottomBar.files.setOnClickListener {
            findNavController().navigate(
                VideoListFragmentDirections.actionYoutubeScreenFragmentToFilesScreenFragment()
            )
        }
    }
    private fun startVideoListener() {
        binding.miniPlayer.miniPlayerContainer.setOnClickListener {
            findNavController().navigate(
                VideoListFragmentDirections.actionYoutubeScreenFragmentToVideoPlayFragment()
            )
        }
    }

    private fun observeUiState() {
        viewModel.state.observe(viewLifecycleOwner, ::handleUiState)
    }

    private fun handleUiState(state: ListUiState) {
        when (state) {
            is ListError -> {
                Toast.makeText(
                    requireContext(), state.message, Toast.LENGTH_SHORT
                ).show()
            }
            is ListLoaded -> {
                state.data.apply {
                    refreshUi(this)
                }
            }
            is ListLoading -> {
                binding.visibleProgress = state.isLoading
            }
            is ListFromBase -> {
                state.data.apply {
                    if(this.isNotEmpty()) {
                        refreshUi(this)
                    }
                    viewModel.isBaseLoaded = true
                }
            }
        }
    }

    private fun refreshUi(list: List<ItemVideo>) {
        viewModel.setCurrentList(list)
        horisontalAdapter.setList(list.take(COUNT_HORIZONTAL_ITEMS))
        verticalAdapter.setList(list)
        val keyWord = viewModel.keyWord
        binding.responseSize = list.size
        binding.searchText = keyWord
        saveSearhText(keyWord)
        //****************************************************************************
        if(viewModel.getCurrentVideo() == null) {
            viewModel.setCurrentVideo(list[0])
        }
        refreshPlayer(0)
        miniPlayerSetItem(viewModel.getCurrentVideo() ?: list[0])
    }

    private fun setItemNavigationListener() {
        binding.miniPlayer.imageNext.setOnClickListener {
            refreshPlayer(1)
        }
        binding.miniPlayer.imagePrev.setOnClickListener {
            refreshPlayer(-1)
        }
    }

    private fun refreshPlayer(bias: Int) {
        val position = verticalAdapter.getPosition(viewModel.getCurrentVideo(), bias)
        if(position != -1) {
            val item: ItemVideo = verticalAdapter.getItemFromPosition(position)
            verticalRecyclerView.layoutManager?.scrollToPosition(position)
            if (position < COUNT_HORIZONTAL_ITEMS) {
                horisontalRecyclerView.layoutManager?.scrollToPosition(position)
            }
            miniPlayerSetItem(item)
        }
    }

    private fun saveSearhText(keyWord: String) {
        val sPref = requireActivity().getSharedPreferences("MyPref", AppCompatActivity.MODE_PRIVATE)
        val ed: SharedPreferences.Editor = sPref.edit()
        ed.putString("textSearch", keyWord)
        ed.apply()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}